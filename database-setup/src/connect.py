from psycopg2 import connect
from config import db


def get_postgres_connection():
	return connect(
		database="postgres",
		user="postgres",
		port=db.port
	)

