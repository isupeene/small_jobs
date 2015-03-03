from django.shortcuts import render
from django.shortcuts import redirect
from django.http import HttpResponse
from django.core.exceptions import PermissionDenied
from django.utils.timezone import now
from django.core.serializers import serialize
from datetime import timedelta

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

#@require_login
def homepage(request):
	return render(request,'job_posting/homepage.html')

def mainpage(request):
	return render(request,'job_posting/mainpage.html')

def edit_profile(request):
	return render(request,'job_posting/edit_profile.html')

def create_job(request):
	return render(request,'job_posting/create_job.html')

def job_details(request):
	return render(request,'job_posting/job_details.html')

def jobs(request):
	# TODO get the actual persons id
	jobList = JobPosting.objects.all
	context = {'jobList': jobList}
	return render(request,'job_posting/jobs.html',context)


def view_profile(request):
	return render(request,'job_posting/view_profile.html')

def new_job(request):
	return render(request,'job_posting/index.html')


# form stuff
def post_new_job(request):
	description = request.POST['description']
	short_description = request.POST['short_description']
	# bidding_deadline = request.POST['bidding_deadline']
	# compensation_amount = request.POST['compensation_amount']
	# TODO change all of this hardcoded stuff! 
	dave = JobPoster.objects.get(name="Dave")
	email = "bob@cableguy.com"
	dave.email = email
	update_job_poster(dave)
	dave.openid = 5
	dave.description = "Every group needs a Dave"
	dave.phonenumber = "123456789"
	myPosting = JobPosting(
			description=description,
			short_description=short_description,
			bidding_deadline=now() + timedelta(days=10),
			bidding_confirmation_deadline=now() + timedelta(days=15),
			bid_includes_compensation_amount = False,
			bid_includes_completion_date = False,
		)
	create_job_posting(dave, myPosting)
	# return redirect('job_posting/jobs.html')
	return HttpResponse("Job has been posted!")
	# return render(request, 'job_posting/jobs.html')

def edit_my_profile_form(request):
	# name = request.POST['name']
	dave = JobPoster.objects.get(name="Dave")
	dave.description = request.POST['description']
	dave.email = request.POST['email']
	dave.phonenumber = request.POST['phone_number']
	update_job_poster(dave)
	return HttpResponse(serialize("json", JobPoster.objects.all()))
