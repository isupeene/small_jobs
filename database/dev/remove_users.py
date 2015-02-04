from database.ops.connect import get_postgres_connection
from database.ops.utils import ignore_errors
from config import db

def remove_users(cursor):
	with ignore_errors():
		cursor.execute("DROP DATABASE {}".format(db.name))
	with ignore_errors():
		cursor.execute("DROP USER {}".format(db.admin))
	with ignore_errors():
		cursor.execute("DROP USER {}".format(db.user))

if __name__ == "__main__":
	connection = get_postgres_connection()
	cursor = connection.cursor()

	connection.set_isolation_level(0)
	remove_users(cursor)

	cursor.close()
	connection.close()

