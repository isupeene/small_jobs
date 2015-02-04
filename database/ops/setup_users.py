from database.ops.connect import get_postgres_connection
from config import db
from getpass import getpass

def get_confirmed_password(prompt):
	password = getpass(prompt)
	repeat = getpass("Type the same password again:")
	if password == repeat:
		return password
	else:
		print("The two passwords do not match.")
		return get_confirmed_password(prompt)

def setup_users(cursor):
	admin_password = get_confirmed_password(
		"Choose a password for {}:".format(db.admin)
	)
	user_password = get_confirmed_password(
		"Choose a password for {}:".format(db.user)
	)

	cursor.execute("""
		CREATE USER
			{}
		WITH
			ENCRYPTED PASSWORD '{}'
			CREATEDB
	""".format(db.admin, admin_password))

	cursor.execute("""
		CREATE USER
			{}
		WITH
			ENCRYPTED PASSWORD '{}'
	""".format(db.user, user_password))

	cursor.execute("""
		CREATE DATABASE
			{}
		WITH 
			OWNER {}
	""".format(db.name, db.admin))

if __name__ == "__main__":
	connection = get_postgres_connection()
	cursor = connection.cursor()

	connection.set_isolation_level(0)
	setup_users(cursor)

	cursor.close()
	connection.close()
	
