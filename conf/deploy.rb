namespace :deploy do

	desc 'Try to acquire a deployment-lock. If a lock is already taken, throw an error'
	task :lock do
		on roles(:app) do
		  lockfile = "#{shared_path}/DEPLOYMENT_LOCKED"
			isLocked = capture 'if [ -e  ] ; then echo true ; else echo false ; fi'
		  if fileExists lockfile
				time = capture "cat #{shared_path}/DEPLOYMENT_LOCKED"
				puts "\n\nDEPLOYMENT IS CURRENTLY LOCKED!\n\nCheck if someone else is deploying!\nForce lock release with $ cap stage deploy:unlock\n\n".yellow
				puts time.white
				exit 0
			else
				puts "\n\nACQUIRED DEPLOY LOCK!\n".green
				execute "echo \"currently running a deployment, started on `date`\" > #{lockfile}"
			end
		end
	end

	desc 'Remove deployment lock. this might be a bad idea'
	task :unlock do
		on roles(:app) do
			execute "rm -f #{shared_path}/DEPLOYMENT_LOCKED"
		end
	end

	task :completed do
		on roles(:app) do
			puts "\nDEPLOYMENT COMPLETED, you may start the fresh build with".yellow
			puts "    $ cap #{fetch(:stage)} play:restart".blue
			puts "    You can Rollback to the currently running release with: $ cap #{fetch(:stage)} deploy:rollback".blue
		end
	end
end

namespace :play do
	desc 'Create executable to launch play in a nohup session (creation of start/stop binaries)'
  task :prepare do
		on roles(:db) do
			execute "mkdir -p #{shared_path}/backups"
		end

    on roles(:app) do

			execute "mkdir -p #{shared_path}/bin"
			execute "mkdir -p #{shared_path}/lib"
			execute "mkdir -p #{shared_path}/logs"
			# create a new local.conf (if not exists yet!)
      execute "touch #{shared_path}/local.conf"

			# create start-scripts in #{shared_path}/bin
      execute "echo -e '#!/bin/bash\\nnohup bash -c \"echo \\\"STARTING APP (#{current_path}/#{fetch(:play_dir)})\\\" && cd #{current_path}/#{fetch(:play_dir)}/target/universal/stage && bin/#{fetch(:application.downcase)} $* &> /dev/null 2>&1\" &> /dev/null &' > #{shared_path}/bin/nohup_process.sh"
      execute "echo -e '#!/bin/bash\\npid=`cat #{fetch(:app_pid)} 2> /dev/null`\\nif [ \"$pid\" == \"\" ]; then echo #{fetch(:application)} is not running; exit 0; fi\\necho Stopping #{fetch(:application)}...\\nif ! kill -SIGTERM $pid  > /dev/null 2>&1; then echo unable to stop Service! No process with PID `cat #{fetch(:app_pid)}` running, pid-file: #{fetch(:app_pid)}! Remove PID-File... ; rm -f #{fetch(:app_pid)} ; fi' > #{shared_path}/bin/stop.sh"
      execute "echo -e '#!/bin/bash\\npid=`cat #{fetch(:app_pid)} 2> /dev/null`\\nif [ \"$pid\" == \"\" ]; then echo #{fetch(:application)} is not running; exit 0; fi\\necho PROCESS ID of Play instance: `cat #{fetch(:app_pid)}`, pid-file: #{fetch(:app_pid)}' > #{shared_path}/bin/displayPID.sh"

      execute "chmod u+x #{shared_path}/bin/nohup_process.sh"
      execute "chmod u+x #{shared_path}/bin/stop.sh"
      execute "chmod u+x #{shared_path}/bin/displayPID.sh"
    end
	end


	desc 'prepare everything to build the app'
	task :pre_build do
		on roles(:app) do

			# hardcopy local.conf because it gets packed within the executable jar. Changes to local.conf require repackaging
			if fetch(:local_conf) != nil
				execute "cp #{fetch(:local_conf)} #{release_path}/#{fetch(:play_dir)}/conf/"
			end

			# hardcopy library directory from shared folder (to have all libs available) during compilation
			execute "cp -af #{fetch(:shared_path)}/lib #{release_path}/#{fetch(:play_dir)}"
		end
	end

  desc 'compile and stage app'
  task :build do
    on roles(:app) do
			with_verbosity Logger::DEBUG do
      	execute "cd #{release_path}/#{fetch(:play_dir)} ; /opt/activator/activator clean compile stage"
			end
		end
  end

	desc 'make build ready to be executed, also perform a db-backup (only on production) once ready'
	task :post_build do
		on roles(:app) do
			# symlink public directory from repo to the staged folder (so we can access assets directly within application, needed for imagemagick magic)
			execute "ln -sf #{fetch(:deploy_to)}/repo/#{fetch(:play_dir)}/public #{release_path}/#{fetch(:play_dir)}/target/universal/stage/"

			# symlink logs directory from shared folder to the staged application
			execute "rm -rf #{release_path}/#{fetch(:play_dir)}/target/universal/stage/logs"
			execute "ln -sf #{fetch(:shared_path)}/logs #{release_path}/#{fetch(:play_dir)}/target/universal/stage/"

			if fetch(:backup_db_on_deploy, false)
				puts '\nPerforming a DB-Backup\n'.yellow
				invoke 'mysql:db_backup'
			else
				puts '\nSkipping DB-Backup\n'.red
			end

			# prepare Deploy tag according to release-name
			releaseName = capture "basename #{release_path}"
			set :deploy_tag, "#{fetch(:stage)}_deploy_#{releaseName} -m \"deployed to #{fetch(:stage)}, release timestamp UTC #{releaseName}\""
		end
	end

	desc 'Show environmental Variables'
	task :envvars do
		on roles(:app) do
			execute 'echo | export'
		end
	end

	desc 'Test it Baby!'
	task :test do
		on roles(:app) do
			with_verbosity Logger::DEBUG do
				execute "cd #{current_path}/#{fetch(:play_dir)} ; /opt/activator/activator test"
			end
		end
	end

  desc 'Stop Play-Server-Instance'
  task :stop do
    on roles(:app) do
			puts 'Kill Play with SIGTERM signal'.yellow
			# Kills the Process safely with SIGTERM and removes the pid-file
      execute "cd #{shared_path}/bin/ ; ./stop.sh"
			sleep(2)

			# Wait 60 seconds for play to terminate. If it is still running after that, we kill it wit SIGKILL
			timeout=60
			timeout.times do |n|
				alienProcess = findAlienProcessOnPort "#{fetch(:app_port)}"
				if !alienProcess.empty?
					if n == timeout - 1
						puts "There is still a Process running that wasn't kill by SIGTERM in the last #{timeout}s.".red
						puts 'Also possible that there was no pid-file or containing PID was wrong.'.red
						puts 'We will kill it immediately with SIGKILL now'.red

						execute "kill -SIGKILL #{alienProcess} || true"
                                                # under certain circumstances even SIGKILL can be delayed (I/O operations), sleep a short while to be sure it is really killed
                                                sleep(2)
					else
						puts "play-process (PID: #{alienProcess}) is still running, SIGKILL in #{timeout - n - 1}s".red
					end
					sleep(1)
				else
					appendOnAllLogs "###### PLAY STOPPED `date +%Y-%m-%d_%H-%M-%S` (wit Signal SIGTERM!)\n"
					puts "\n\nPlay-process has stopped\n".yellow
					break
				end
			end
		end
	end


  desc 'Start Play-Server-Instance'
  task :start do
    on roles(:app) do
      # unfortunately this does not work since local.conf gets packed into staged jar-File.
      # target/universal/stage/conf files are just a reference copy and don't have direct influence
      # on currently deployed application :(
      # changes in config are only applied after rebuilding ($ cap YOURSTAGE play:pre_build & cap YOURSTAGE play:build) & cap YOURSTAGE play:post_build)!
      #print "reapplying configuration from shared/local.conf"
      #execute "cp #{fetch(:local_conf)} #{current_path}/#{fetch(:play_dir)}/target/universal/stage/conf/"


			# (re)generating start-script
			execute "echo -e '#!/bin/bash\\n export JAVA_OPTS=\"#{fetch(:java_options)}\"\\ncd #{shared_path}/bin/\\n./nohup_process.sh . -Dhttp.port=#{fetch(:app_port)} -Dpidfile.path=#{fetch(:app_pid)}' > #{shared_path}/bin/start.sh ; chmod u+x #{shared_path}/bin/start.sh"

			# check if another istance is running
			alienProcess = findAlienProcessOnPort "#{fetch(:app_port)}"
			if !alienProcess.empty?
				puts "There is another instance running (PID: #{alienProcess})".red
				puts "Omitting restart... you may issue play:restart instead".yellow
			else
				# start the server
				execute "cd #{shared_path}/bin/ ; ./start.sh"

				# Append PLAY STARTED message to all log files
				appendOnAllLogs "\\n\\n###### PLAY STARTED `date +%Y-%m-%d_%H-%M-%S`\\n\\n"

				puts "\nstarting server... this may take a while ;-)".yellow
				puts "    server output $ cap #{fetch(:stage)} play:log".green
				puts "               or $ cap #{fetch(:stage)} play:log[logEnvironment]".green
				puts "       server pid $ cap #{fetch(:stage)} play:pid\n".green

				#httpResponse = capture "wget --max-redirect=0 -SO- \"http://localhost:#{fetch(:app_port)}/\" 2>&1 | grep \"HTTP/\" | awk '{print $2}'"
				#httpResponse = capture "curl --connect-timeout 20 --retry 6 -o /dev/null --silent --write-out \"http\" http://localhost:#{fetch(:app_port)}"

				httpResponse = "0"
				timeElapsed = 0
				while 0 == httpResponse.to_i do
					httpResponse = capture "curl --write-out %{http_code} --silent output /dev/null localhost:#{fetch(:app_port)} || true"
					print ".".blue
					sleep 1
					timeElapsed += 1
				end

				puts "\nSERVER started and respondes to HTTP-calls with status #{httpResponse.to_i} after #{timeElapsed}s".yellow

				#puts "\nSERVER doesn't serve a website over HTTP".red
			end
		end

  end

  desc 'Show PID of current Server-Instance'
  task :pid do
    on roles(:app) do
      pidDetails = capture "#{shared_path}/bin/displayPID.sh"
			puts pidDetails.yellow
    end
  end

  desc 'Restart Play-Server-Instace'
  task :restart  => [:stop, :start]


  desc 'Play-Server-Instace Console Output'
  task :log, :arg1 do |t, args|
    on roles(:app) do
			logfile = args[:arg1]
			if(logfile == nil)
				logfile = "application"
			end

			trap("INT") { puts 'Interupted'; exit 0; }
			with_verbosity Logger::DEBUG do
				execute "tail -f #{shared_path}/logs/#{logfile}.log"
			end
    end
  end

  desc 'fetch serverside and clientside play properties used in certain tasks'
  task :fetch_play_conf do
    on roles(:app) do
      ### Merge and read REMOTE Play-Configuration
      if fetch(:local_conf) != nil
        print "local.conf is present on stage, merge it with application.conf and store it in /tmp/application.conf\n"
        execute "cp #{fetch(:application_conf)} /tmp/application.conf"
        execute "cat #{fetch(:local_conf)} >> /tmp/application.conf"
      else
        execute "cp #{fetch(:application_conf)} /tmp/application.conf"
      end
      puts "download servers Play-Configuration to read it locally\n".yellow
      download! '/tmp/application.conf', '/tmp/remote_application.conf'

      # Set Properties from remote configuration
      remote_handler = PropertyFileHandler.new('/tmp/remote_application.conf')
      set :remote_db_host, remote_handler.read_property('db.default.host')
      set :remote_db_name, remote_handler.read_property('db.default.name')
      set :remote_db_user, remote_handler.read_property('db.default.user')
      set :remote_db_pass, remote_handler.read_property('db.default.pass')


      ### Merge and read LOCAL Play-Configuration
      run_locally do
        if File.file?('conf/local.conf')
          print "local.conf is present on local machine, merge it with application.conf and store it in /tmp/local_application.conf\n"
          execute 'cp conf/application.conf /tmp/local_application.conf'
          execute 'cat conf/local.conf >> /tmp/local_application.conf'
          else
          execute 'cp conf/application.conf /tmp/local_application.conf'
        end
      end

      # Set Properties from local application.conf
      # read db-properties
      print "reading required fields in local Play-Configuration\n"
      local_handler = PropertyFileHandler.new('/tmp/local_application.conf')
      set :local_db_host, local_handler.read_property('db.default.host')
      set :local_db_name, local_handler.read_property('db.default.name')
      set :local_db_user, local_handler.read_property('db.default.user')
      set :local_db_pass, local_handler.read_property('db.default.pass')
    end
  end

  # Dummy Task to check variables
  desc 'show certain configuration properties'
  task :check_play_conf do
    on roles(:app) do
			# todo: read what you want to display, like:
      # puts 'local host: ' + fetch(:local_db_host)
    end
  end
  before :check_play_conf, :fetch_play_conf
end

namespace :lib do
	desc 'Overwrites local Library-Files (lib folder) with libraries from Server'
	task :sync do
		on roles(:app) do |server|
			run_locally do
				execute "mkdir -p './lib' &&  rsync -avz --progress '#{server.user}@#{server.hostname}:#{fetch(:shared_path)}/lib/*' './lib/'"
			end
			puts "\n\ndid you sync the correct libs?\nmake sure you don't sync libs from production-Stage, since those libs point to the productive SAP-System!!!".yellow
		end
	end
end


# Hooks for deployment
before :deploy, 'deploy:lock'
after 'deploy:updated', 'play:prepare'
after 'deploy:updated', 'play:pre_build'
after 'deploy:updated', 'play:build'
after 'deploy:updated', 'play:post_build'
#after 'deploy:updated', 'play:test'
after :deploy, 'deploy:unlock'
after :deploy, 'deploy:completed'


# Hooks for Rollback
before 'deploy:rollback', 'deploy:lock'
after 'deploy:rollback', 'deploy:unlock'




# Default branch is :master
# ask :branch, proc { `git rev-parse --abbrev-ref HEAD`.chomp }

# Default deploy_to directory is /var/www/my_app
# set :deploy_to, '/var/www/my_app'

# Default value for :scm is :git
# set :scm, :git

# Default value for :format is :pretty
# set :format, :pretty

# Default value for :log_level is :debug
set :log_level, :info

# Default value for :pty is false
# set :pty, true

# Default value for :linked_files is []
# set :linked_files, %w{config/database.yml}

# Default value for linked_dirs is []
# set :linked_dirs, %w{bin log tmp/pids tmp/cache tmp/sockets vendor/bundle public/system}

# Default value for default_env is {}
# set :default_env, { path: "/opt/ruby/bin:$PATH" }

# Default value for keep_releases is 5
# set :keep_releases, 5

# Append Message to all log files
def appendOnAllLogs (msg)
	execute "echo -e \"#{msg}\" | tee -a `ls #{shared_path}/logs/*.log`"
end

# Checks if file exists. No wildcard support
def fileExists (file)
	'true' == (capture "if [ -e #{file} ] ; then echo true ; else echo false ; fi")
end

# Get the process id of a play-process' started with Option Dhttp.port=#{port} (useful to detect alien-processes without PID-File)
def findAlienProcessOnPort (port)
	capture "ps -ef | grep \"Dhttp.port=#{port}\" | grep -v grep | awk '{print $2}' | tail -1"
end


# execute things within with certain verbosity-level
def with_verbosity(output_verbosity)
	old_verbosity = SSHKit.config.output_verbosity
	begin
		SSHKit.config.output_verbosity = output_verbosity
		yield
	ensure
		SSHKit.config.output_verbosity = old_verbosity
	end
end
