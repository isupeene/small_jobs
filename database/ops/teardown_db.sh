# Undoes everything that create_db.sh does, leaving an empty database.
# The user will be required to provide the admin password for the
# small jobs database.

cd $(dirname $0)/..
python -m src.teardown

