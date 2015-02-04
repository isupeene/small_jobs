cd $(dirname $0)/..

if [ "$(whoami)" = 'root' ]
then
	su postgres -c "python -m database.ops.setup_users"
elif [ "$(whoami)" = 'postgres' ]
then
	python -m database.ops.setup_users
else
	echo 'This command must be run as either root, or postgres.'
	exit 1
fi

