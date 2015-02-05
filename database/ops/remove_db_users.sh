# Undoes the effects of setup_db_users.sh, removing the admin and user
# accounts, and the small jobs database itself.
#
# teardown_db.sh does not need to be run before this - the database
# will be dropped in its entirety, and no tables will remain at all.
#
# This script must be run as either root, or postgres.

cd $(dirname $0)/..
COMMAND="python -m src.remove_users"

if [ "$(whoami)" = 'root' ]
then
	su postgres -c "$COMMAND"
elif [ "$(whoami)" = 'postgres' ]
then
	sh -c "$COMMAND"
else
	echo 'This command must be run as either root, or postgres.'
	exit 1
fi

