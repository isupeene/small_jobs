# This doesn't 'create' the database per se, but it does
# create the schema in the existing database that was set up
# when setup_db_users.sh was run.
#
# This script creates all the tables and indices required for
# the application.  The user will be required to provide the admin
# password for the small jobs database.

cd $(dirname $0)/..
python -m src.create

