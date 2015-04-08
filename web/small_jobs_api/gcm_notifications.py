# Requirement 3.2.2.3.2

from django.conf import settings

from gcm import GCM

from small_jobs_api.models import *
from small_jobs_api.serializers import default_serializer

_gcm = GCM(settings.GCM_AUTHORIZATION_KEY)
dry_run = False

# TODO: Sensible error logging instead of this
# print nonsense.

def _notification_data(notification_type, job_posting):
	return {
		"type" : notification_type,
		"job" : default_serializer(JobPosting)(job_posting).data
	}

def _contractor_reg_ids(job_posting):
	return [bid.contractor.registration_id
			for bid in job_posting.bid_set.all()]

def job_modified(job_posting):
	reg_ids = _contractor_reg_ids(job_posting)
	if reg_ids:
		response = _gcm.json_request(
			registration_ids=_contractor_reg_ids(job_posting),
			data=_notification_data("job_modified", job_posting),
			dry_run=dry_run
		)

		if "errors" in response:
			print(response["errors"])

def job_deleted(job_posting):
	reg_ids = _contractor_reg_ids(job_posting)
	if reg_ids:
		response = _gcm.json_request(
			registration_ids=reg_ids,
			data=_notification_data("job_deleted", job_posting),
			dry_run=dry_run
		)

		if "errors" in response:
			print(response["errors"])

def bid_accepted(accepted_bid):
	job_posting = accepted_bid.job

	accepted_data = {"type" : "bid_accepted"}
	rejected_data = {"type" : "bid_rejected"}

	serializer = default_serializer(JobPosting)(job_posting)
	accepted_data["job"] = serializer.data
	rejected_data["job"] = serializer.data

	accepted_contractor = accepted_bid.contractor
	rejected_bids = job_posting.bid_set.exclude(pk=accepted_bid.pk)
	rejected_contractors = [bid.contractor for bid in rejected_bids]

	accepted_reg_id = accepted_contractor.registration_id
	rejected_reg_ids = [contractor.registration_id for contractor in rejected_contractors]

	accepted_response = _gcm.json_request(
		registration_ids=[accepted_reg_id],
		data=accepted_data,
		dry_run=dry_run
	)

	if "errors" in accepted_response:
		print(accepted_response["errors"])

	if rejected_reg_ids:
		rejected_response = _gcm.json_request(
			registration_ids=rejected_reg_ids,
			data=rejected_data,
			dry_run=dry_run
		)

		if "errors" in rejected_response:
			print(rejected_response["errors"])

