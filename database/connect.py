from psycopg2 import connect
from config import db

def get_user_connection(password):
	return connect(
		database=db.name,
		user=db.user,
		password=password,
		host=db.hostname,
		port=db.port
	)


