from psycopg2 import connect
from config import db

def get_admin_connection(password):
	return connect(
		database=db.name,
		user=db.admin,
		password=password,
		port=db.port
	)

def get_postgres_connection():
	return connect(
		database="postgres",
		user="postgres",
		port=db.port
	)

