# The name of the database we're connecting to.
# This must match the corresponding value in server/config/db.py.
name = "small_jobs"

# The name of the user that the server will log in as.
# This must match the corresponding value in server/config/db.py
user = "small_jobs_user"

# The name of the user who will be the database owner.
# This must not be the same as the user.
# We're using separate accounts to mitigate injection / escalation attacks.
admin = "small_jobs_admin"

# The hostname of the computer where the database is located.
# Currently only 'localhost' will work, because we assume
# peer authentication for the postgres user.
hostname = "localhost"

# The port that postgreSQL is listening on.
# This must match the corresponding value in server/config/db.py.
port = "5432"

