#!/usr/bin/python

from src.schema import Base
from src.connect import get_admin_engine
from src.password import get_admin_password
from config import db


if __name__ == "__main__":
	engine = get_admin_engine(get_admin_password())
	Base.metadata.create_all(engine)

	with engine.begin() as connection:
			for privilege in ["SELECT", "INSERT", "UPDATE", "DELETE"]:
				for t in Base.metadata.tables:
					connection.execute("GRANT {} ON {} TO {}".format(
						privilege, t, db.user
					))

			serial_id_tables = [name for name, table
								in Base.metadata.tables.iteritems()
								if "id" in table.c]
			for t in serial_id_tables:
				connection.execute("GRANT ALL ON {}_id_seq TO {}".format(
					t, db.user
				))
	
