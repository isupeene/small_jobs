# This file is only for the purposes of initial database creation.
# For development and production, use the file settings.py in the
# Django project.

# Note that hostname is not included in this file.
# The database creation scripts must be run on the
# database server itself, since we assume peer
# authentication for the postgres user.  Of course,
# this doesn't mean that the web server needs to be
# running on the same host as the database - the
# web server doesn't need to do anything as postgres.

# The name of the database we're connecting to.
name = "small_jobs"

# The name of the database user.
user = "small_jobs_user"

# The database user's password.  Set this to something secure in production!
password = "password"

# The port that postgreSQL is listening on.
port = "5432"

