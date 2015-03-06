from django.core.exceptions import PermissionDenied, SuspiciousOperation
from django.shortcuts import get_object_or_404
from django.db import DataError, IntegrityError

from small_jobs_api.models import (
	JobPosting, Bid, ContractorRating, JobPosterRating
)


# NOTE: All this code is simplified by high level abstractions.
# Some of these abstractions will inevitably hurt performance.
# Therefore, there are likely to be several opportunities for
# optimization in this file, should the situation call for it.


# Identity

def update_job_poster(job_poster):
	try:
		job_poster.save()
	except DataError:
		raise SuspiciousOperation

def get_rating(job_poster):
	ratings = JobPosterRating.objects.filter(poster=job_poster)
	if len(ratings) == 0:
		return None
	else:
		return float(sum([r.rating for r in ratings])) / len(ratings)

# Posting Jobs

def create_job_posting(job_poster, job_posting):
	try:
		job_posting.poster = job_poster
		job_posting.save()
	except (IntegrityError, DataError):
		raise SuspiciousOperation

# Reviewing Postings and Bids

# TODO: Notify Contractors who have bid on a modified or deleted job posting.
# TODO: Notify all bidders when a bid has been accepted.

def get_job_postings(job_poster):
	return JobPosting.objects.filter(poster=job_poster, contractor=None)

def get_active_jobs(job_poster):
	return JobPosting.objects \
		.filter(poster=job_poster, completed=False) \
		.exclude(contractor=None)

def get_completed_jobs(job_poster):
	return JobPosting.objects.filter(poster=job_poster, completed=True)

def delete_job_posting(job_poster, job_id):
	job_posting = get_object_or_404(JobPosting, pk=job_id)
	_check_job_owner(job_poster, job_posting)

	job_posting.delete()

def update_job_posting(job_poster, job_posting):
	_check_job_owner(job_poster, job_posting)

	job_posting.save()

def get_bids(job_poster, job_id):
	job_posting = get_object_or_404(JobPosting, pk=job_id)
	_check_job_owner(job_poster, job_posting)

	return job_posting.bid_set.all()

def accept_bid(job_poster, bid_id):
	bid = get_object_or_404(Bid, pk=bid_id)
	_check_job_owner(job_poster, bid.job)

	bid.job.contractor = bid.contractor
	bid.job.save()

# After the Job

def rate_contractor(job_poster, contractor, rating):
	jobs = get_completed_jobs(job_poster)
	if contractor not in [job.contractor for job in jobs]:
		raise PermissionDenied

	try:
		existing_rating = ContractorRating.get(
			contractor=contractor,
			poster=job_poster
		)
		existing_rating.rating = rating
	except:
		ContractorRating(
			contractor=contractor,
			poster=job_poster,
			rating=rating
		).save()

def mark_complete(job_poster, job_posting):
	_check_job_owner(job_poster, job_posting)

	job_posting.complete = True
	job_posting.save()

# Helper Functions

# TODO: Make sure it's really impossible for someone to delete
# or modify someone else's job.

def _check_job_owner(job_poster, job_posting):
	if job_posting.poster != job_poster:
		raise PermissionDenied

