from src.connect import get_postgres_connection
from config import db

def setup_user(cursor):
	cursor.execute("""
		CREATE USER
			{}
		WITH
			ENCRYPTED PASSWORD '{}'
			CREATEDB
	""".format(db.user, db.password))

	cursor.execute("""
		CREATE DATABASE
			{}
		WITH 
			OWNER {}
	""".format(db.name, db.user))

if __name__ == "__main__":
	connection = get_postgres_connection()
	cursor = connection.cursor()

	connection.set_isolation_level(0)
	setup_user(cursor)

	cursor.close()
	connection.close()
	
