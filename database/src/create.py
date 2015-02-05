#!/usr/bin/python

from sys import argv
from getpass import getpass
from src.connect import get_admin_connection
from src.password import get_admin_password

def create_database(cursor):

	# TODO: Add check constraints.

	cursor.execute("""
		CREATE TABLE JobPoster (
			id SERIAL PRIMARY KEY,
			name TEXT NOT NULL,
			description TEXT,
			email TEXT,
			phone_number TEXT
		)
	""")

	cursor.execute("""
		CREATE TABLE Contractor (
			id SERIAL PRIMARY KEY,
			name TEXT NOT NULL,
			description TEXT,
			email TEXT,
			phone_number TEXT
		)
	""")

	cursor.execute("""
		CREATE TABLE JobPosting (
			id SERIAL PRIMARY KEY,
			poster_id INT REFERENCES JobPoster(id) NOT NULL,
			creation_date DATE NOT NULL,
			short_description TEXT NOT NULL,
			description TEXT NOT NULL,
			bidding_deadline DATE NOT NULL,
			bidding_confirmation_deadline DATE NOT NULL,
			compensation_amount MONEY,
			completion_date DATE,
			bid_includes_compensation_amount BOOLEAN NOT NULL,
			bid_includes_completion_date BOOLEAN NOT NULL,
			contractor_id INT REFERENCES Contractor(id),
			completed BOOLEAN NOT NULL,
			marked_completed_by_contractor BOOLEAN NOT NULL,
			date_completed DATE
		)
	""")

	cursor.execute("""
		CREATE INDEX
			JobPosting_contractor_id
		ON
			JobPosting (contractor_id)
	""")

	cursor.execute("""
		CREATE TABLE Bid (
			job_id INT REFERENCES JobPosting(id),
			contractor_id INT REFERENCES Contractor(id),
			message TEXT,
			compensation_amount MONEY,
			completion_date DATE,
			PRIMARY KEY (job_id, contractor_id)
		)
	""")

	cursor.execute("""
		CREATE INDEX
			Bid_contractor_id
		ON
			Bid (contractor_id)
	""")

	cursor.execute("""
		CREATE TABLE JobSkill (
			job_id INT REFERENCES JobPosting(id),
			skill TEXT,
			PRIMARY KEY (job_id, skill)
		)
	""")

	cursor.execute("""
		CREATE INDEX
			JobSkill_skill
		ON
			JobSkill (skill)
	""")

	cursor.execute("""
		CREATE TABLE ContractorSkill (
			contractor_id INT REFERENCES Contractor(id),
			skill TEXT,
			PRIMARY KEY (contractor_id, skill)
		)
	""")

	cursor.execute("""
		CREATE INDEX
			ContractorSkill_skill
		ON
			ContractorSkill (skill)
	""")

	cursor.execute("""
		CREATE TABLE JobPosterRating (
			poster_id INT REFERENCES JobPoster(id),
			contractor_id INT REFERENCES Contractor(id),
			rating INT,
			PRIMARY KEY (poster_id, contractor_id)
		)
	""")

	cursor.execute("""
		CREATE TABLE ContractorRating (
			contractor_id INT REFERENCES Contractor(id),
			poster_id INT REFERENCES JobPoster(id),
			rating INT,
			PRIMARY KEY (contractor_id, poster_id)
		)
	""")

	for privilege in ["SELECT", "INSERT", "UPDATE", "DELETE"]:
		for item in [
			"JobPoster", "Contractor", "JobPosting", "Bid", "JobSkill",
			"ContractorSkill", "JobPosterRating", "ContractorRating",
			"JobPoster_id_seq", "Contractor_id_seq", "JobPosting_id_seq"
		]:
			cursor.execute("GRANT {} ON {} TO small_jobs_user".format(
				privilege, item
			))


if __name__ == "__main__":
	connection = get_admin_connection(get_admin_password())
	cursor = connection.cursor()

	create_database(cursor)
	connection.commit()

	cursor.close()
	connection.close()

