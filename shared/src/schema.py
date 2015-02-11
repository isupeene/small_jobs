from sqlalchemy import PrimaryKeyConstraint, ForeignKey, Index, Column, func
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.dialects.postgresql import (
	INTEGER, TEXT, DATE, NUMERIC, BOOLEAN
)

class MONEY(NUMERIC):
	def __init__(self):
		super(MONEY, self).__init__(precision=20)

Base = declarative_base()

# TODO: Add check constraints

class JobPoster(Base):
	__tablename__ = "job_poster"

	id = Column(INTEGER, primary_key=True)
	name = Column(TEXT, nullable=False)
	description = Column(TEXT)
	email = Column(TEXT)
	phone_number = Column(TEXT)


class Contractor(Base):
	__tablename__ = "contractor"

	id = Column(INTEGER, primary_key=True)
	name = Column(TEXT, nullable=False)
	description = Column(TEXT)
	email = Column(TEXT)
	phone_number = Column(TEXT)


class JobPosting(Base):
	__tablename__ = "job_posting"

	id = Column(INTEGER, primary_key=True)
	poster_id = Column(INTEGER, ForeignKey("job_poster.id"), nullable=False)
	creation_date = Column(DATE, nullable=False, default=func.now())
	short_description = Column(TEXT, nullable=False)
	description = Column(TEXT, nullable=False)
	bidding_deadline = Column(DATE, nullable=False)
	bidding_confirmation_deadline = Column(DATE, nullable=False)
	compensation_amount = Column(MONEY)
	completion_date = Column(DATE)
	bid_includes_compensation_amount = Column(BOOLEAN, nullable=False)
	bid_includes_completion_date = Column(BOOLEAN, nullable=False)
	contractor_id = Column(INTEGER, ForeignKey("contractor.id"))
	completed = Column(BOOLEAN, nullable=False, default=False)
	marked_completed_by_contractor = Column(BOOLEAN, nullable=False, default=False)
	date_completed = Column(DATE)

	Index("job_posting_contractor_id", "contractor_id")


class Bid(Base):
	__tablename__ = "bid"

	job_id = Column(INTEGER, ForeignKey("job_posting.id"))
	contractor_id = Column(INTEGER, ForeignKey("contractor.id"))
	message = Column(TEXT)
	compensation_amount = Column(MONEY)
	completion_date = Column(DATE)

	__table_args__ = (PrimaryKeyConstraint("job_id", "contractor_id"),)
	Index("bid_contractor_id", "contractor_id")


class JobSkill(Base):
	__tablename__ = "job_skill"

	job_id = Column(INTEGER, ForeignKey("job_posting.id"))
	skill = Column(TEXT)

	__table_args__ = (PrimaryKeyConstraint("job_id", "skill"),)
	Index("job_skill_skill", "skill")


class ContractorSkill(Base):
	__tablename__ = "contractor_skill"

	contractor_id = Column(INTEGER, ForeignKey("contractor.id"))
	skill = Column(TEXT)

	__table_args__ = (PrimaryKeyConstraint("contractor_id", "skill"),)
	Index("contractor_skill_skill", "skill")


class JobPosterRating(Base):
	__tablename__ = "job_poster_rating"

	poster_id = Column(INTEGER, ForeignKey("job_poster.id"))
	contractor_id = Column(INTEGER, ForeignKey("contractor.id"))
	rating = Column(INTEGER, nullable=False)
	
	__table_args__ = (PrimaryKeyConstraint("poster_id", "contractor_id"),)


class ContractorRating(Base):
	__tablename__ = "contractor_rating"

	contractor_id = Column(INTEGER, ForeignKey("contractor.id"))
	poster_id = Column(INTEGER, ForeignKey("job_poster.id"))
	rating = Column(INTEGER, nullable=False)

	__table_args__ = (PrimaryKeyConstraint("contractor_id", "poster_id"),)



