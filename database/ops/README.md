# ops
Contains scripts relevant for Ops personnel setting up the database.
Individual scripts contain more detailed documentation on their usage, but the important details are outlined here.

To initialize the database, first run `setup_db_users.sh` as either `root` or `postgres`.  Then, run `create_db.sh` as any user.
To teardown the database, first run `teardown_db.sh` as any user, then run `remove_db_users.sh` as either `root` or `postgres`.

