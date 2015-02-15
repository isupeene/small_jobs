# Undoes the effects of create_db.sh, removing the user
# account, and the small jobs database itself.
#
# This script must be run as either root, or postgres.

cd $(dirname $0)/..
COMMAND="python -m src.destroy"

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

