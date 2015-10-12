# Roles per Server instance (multiple ones can be useful in multi-layered environments)
role :app, "nosketch@uhon.ch"
#role :web, "nosketch@uhon.ch"
role :db,  "nosketch@uhon.ch"


# The server's user for deploys
set :user, "nosketch"
set :use_sudo, false

set :application, "nosketchServer"
set :repo_url, "git@bitbucket.org:uhon/nosketch.git"
set :port, 22


set :deploy_via, :remote_cache
set :keep_releases, 10

set :scm, :git
set :branch, "master"
set :git_strategy, SubmoduleStrategy
set :scm_verbose, true
# Tag a deploy
set :skip_deploy_tagging, true

set :ssh_options, { :forward_agent => true }

set :java_options, '-Xms1024m -Xmx4096m'

set :play_dir, "jvm" # set :play_dir to "." unless your app lives in a subfolder of the repo

set :app_port, "9000"

set :deploy_to, "/home/nosketch/#{fetch(:application)}"

# Shared Paths
set :shared_path, "#{fetch(:deploy_to)}/shared"
set :app_pid, "#{fetch(:shared_path)}/server.pid"
set :app_path, "#{fetch(:deploy_to)}/current"

# backup-directory on db-host (change it if db-server is not equal to app-server)
set :db_backup_path, "#{fetch(:shared_path)}/backups"

set :backup_db_on_deploy, false

# Configurations
# local.conf overwrites configuration from application.conf and can be used to store productive configs (like db passwords)
# which are environment-specific or confidential. usually not checked into SCM (added to .gitignore)
set :application_conf, "#{fetch(:app_path)}/#{fetch(:play_dir)}/conf/application.conf"
set :local_conf, "#{fetch(:shared_path)}/local.conf"  # if no local.conf, set it to nil