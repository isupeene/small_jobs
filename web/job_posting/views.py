from django.shortcuts import render
from django.http import HttpResponse
from django.core.exceptions import PermissionDenied


from small_jobs_api.decorators import require_login
from small_jobs_api.models import (
	JobPoster, JobPosting
)
from small_jobs_api.api import (
	update_job_poster, 
	create_job_posting
)


# A sample view requiring OpenID authentication.
@require_login
def protected(request):
	return HttpResponse("Hello, World!")

def new_job(request):
	context =  {'superman': "I'm super!"}
	return render(request,'job_posting/index.html', context)

@require_login
def post_new_job(request):
	description = request.POST['description']
	short_description = request.POST['short_description']
	# bidding_deadline = request.POST['bidding_deadline']
	# compensation_amount = request.POST['compensation_amount']
	# TODO need to ge the id prob 
	JobPoster(name="Bob", openid="0")
	myPosting = JobPosting(
			description=description,
			short_description=short_description,
			bidding_deadline=now() + timedelta(days=10),
			bidding_confirmation_deadline=now() + timedelta(days=15),
			bid_includes_compensation_amount = False,
			bid_includes_completion_date = False,
			**kwargs
		)
	create_job_posting(myUser, myPosting)
	return HttpResponse(short_description)