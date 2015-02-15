# Sets up the user account for the small jobs database.
# The user will be required to choose and confirm the password
# for the account.  This script also creates the database, but
# with no tables.
#
# This script must be run as either root, or postgres.

cd $(dirname $0)/..
COMMAND="python -m src.create"

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

