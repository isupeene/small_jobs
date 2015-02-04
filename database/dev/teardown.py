#!/usr/bin/python

from getpass import getpass
from psycopg2 import connect
from config import db
from database.ops.connect import get_admin_connection
from database.ops.password import get_admin_password
from database.ops.utils import ignore_errors

def teardown_database(cursor):
	for table in [
		"JobPoster", "Contractor", "JobPosting", "Bid", "JobSkill",
		"ContractorSkill", "JobPosterRating", "ContractorRating"
	]:
		with ignore_errors():
			cursor.execute("DROP TABLE {} CASCADE".format(table))

if __name__ == "__main__":
	connection = get_admin_connection(get_admin_password())
	cursor = connection.cursor()

	teardown_database(cursor)
	connection.commit()

	cursor.close()
	connection.close()
