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

