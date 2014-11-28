# We change default config-path to match Plays conf-directory structure. Having both a conf and a config directory would look silly
set :deploy_config_path, 'conf/deploy.rb'
set :stage_config_path, 'conf/deploy'

# Load DSL and Setup Up Stages
require 'capistrano/setup'

# Includes default deployment tasks
require 'capistrano/deploy'

# Capistrano Bundler Common tasks without "bundle exec" prefix
require 'capistrano/bundler'

# rbenv to always rund the correct Ruby env
# require 'capistrano/rbenv'

# set Tags on git-revisions
require 'cap-deploy-tagger/capistrano'

# attach custom lib functions
require "./conf/deploy/lib/submodule_strategy.rb"
require "./conf/deploy/lib/property_file_handler.rb"


# Includes tasks from other gems included in your Gemfile
#
# For documentation on these, see for example:
#
#   https://github.com/capistrano/rvm
#   https://github.com/capistrano/rbenv
#   https://github.com/capistrano/chruby
#   https://github.com/capistrano/bundler
#   https://github.com/capistrano/rails
#
# require 'capistrano/rvm'
# require 'capistrano/rbenv'
# require 'capistrano/chruby'
# require 'capistrano/bundler'
# require 'capistrano/rails/assets'
# require 'capistrano/rails/migrations'

# Loads custom tasks from `lib/capistrano/tasks' if you have any defined.
Dir.glob('lib/capistrano/tasks/*.rake').each { |r| import r }
