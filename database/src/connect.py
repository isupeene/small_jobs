from sqlalchemy import create_engine
from psycopg2 import connect
from config import db


_engine = None

def get_admin_engine(password):
	global _engine

	if not _engine:
			_engine = create_engine("{}+{}://{}:{}@{}:{}/{}".format(
				"postgresql",
				"psycopg2",
				db.admin,
				password,
				db.hostname,
				db.port,
				db.name
			))

	return _engine

def get_postgres_connection():
	return connect(
		database="postgres",
		user="postgres",
		port=db.port
	)

