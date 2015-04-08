# Requirement 3.2.2

from django.core.exceptions import (
	PermissionDenied, SuspiciousOperation, ValidationError
)
from django.shortcuts import get_object_or_404
from django.utils.timezone import now
from django.db import DataError, IntegrityError
from django.db.models import Q
from django.http import Http404

from small_jobs_api.models import (
	JobPosting, JobSkill, JobPoster, JobPosterRating, ContractorRating
)
from small_jobs_api import job_posting_api as post


# NOTE: All this code is simplified by high level abstractions.
# Some of these abstractions will inevitably hurt performance.
# Therefore, there are likely to be several opportunities for
# optimization in this file, should the situation call for it.


# Identity

def update_contractor(contractor, data):
	try:
		# YOLO: No idea if this can be used to hack the server.
		for k, v in data.iteritems():
			setattr(contractor, k, v)
		contractor.save()
	except (DataError, IntegrityError, ValidationError):
		raise SuspiciousOperation

def get_rating(contractor):
	ratings = ContractorRating.objects.filter(contractor=contractor)
	if len(ratings) == 0:
		return None
	else:
		return float(sum([r.rating for r in ratings])) / len(ratings)

# Finding Jobs

def get_jobs(contractor, skills=None, region=None):
	if skills:
		return {skill.job
				for skill
				in JobSkill.objects.filter(skill__in=skills)
				if ((not region) or (skill.job.poster.region == region))
				and skill.job.contractor is None
				and skill.job.bidding_deadline > now()}
	elif region:
		return JobPosting.objects.filter(
			poster__region=region,
			contractor=None,
			bidding_deadline__gt=now()
		)
	else:
		return JobPosting.objects.filter(
			contractor=None,
			bidding_deadline__gt=now()
		)

def get_job_poster(contractor, poster_id):
	return get_object_or_404(JobPoster, pk=poster_id)

def get_job_poster_rating(contractor, poster_id):
	return post.get_rating(get_object_or_404(JobPoster, pk=poster_id))

# Bidding

def place_bid(contractor, bid):
	# TODO: Disable deferred constraint checking so I don't have to do this.
	try:
		bid.job
	except JobPosting.DoesNotExist:
		raise Http404

	try:
		bid.contractor = contractor
		bid.save()
	except (DataError, ValidationError):
		raise SuspiciousOperation
	except IntegrityError:
		raise Http404

def get_current_jobs(contractor):
	return contractor.jobposting_set.filter(
		completed=False, marked_completed_by_contractor=False
	)

def get_completed_jobs(contractor):
	return contractor.jobposting_set.filter(
		Q(completed=True) | Q(marked_completed_by_contractor=True)
	)

def get_prospective_jobs(contractor):
	return [bid.job for
			bid in
			contractor.bid_set.all() if
			bid.job.contractor is None]

# After the Job

def rate_job_poster(contractor, poster_id, rating):
	jobs = get_completed_jobs(contractor)
	if poster_id not in [job.poster_id for job in jobs]:
		raise PermissionDenied

	try:
		existing_rating = JobPosterRating.objects.get(
			poster_id=poster_id,
			contractor=contractor
		)
		existing_rating.rating = rating
	except ValidationError:
		raise SuspiciousOperation
	except JobPosterRating.DoesNotExist:
		JobPosterRating(
			poster_id=poster_id,
			contractor=contractor,
			rating=rating
		).save()

def mark_complete(contractor, posting_id):
	job_posting = get_object_or_404(JobPosting, pk=posting_id)
	if job_posting.contractor != contractor:
		raise PermissionDenied

	job_posting.marked_completed_by_contractor = True
	job_posting.save()

