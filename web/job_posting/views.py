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
from small_jobs_api.job_posting_api import *

# A sample view requiring OpenID authentication.
@require_login
def protected(request):
	return HttpResponse("Hello, World!")

def homepage(request):
	return render(request,'job_posting/homepage.html')

@require_login
def mainpage(request):
	context = {'userInfo': _get_job_poster(request)}
	return render(request,'job_posting/mainpage.html', context)

def edit_profile(request):
	context = {'userInfo': _get_job_poster(request)}
	return render(request,'job_posting/edit_profile.html',context)

def create_job(request):
	return render(request,'job_posting/create_job.html')

def edit_job(request):
	jobPk = request.GET.get('pk','')
	myJob = JobPosting.objects.get(pk= jobPk) #TODO Change this to use API
	context = {'myJob': myJob }
	return render(request,'job_posting/edit_job.html',context)

def job_details(request):
	jobPk = request.GET.get('pk','')
	myJob = JobPosting.objects.get(pk= jobPk) #TODO Change this to use API
	bidList = get_bids(_get_job_poster(request),myJob.pk)
	context = {'myJob': myJob , 'bidList': bidList}
	return render(request,'job_posting/job_details.html',context)

def jobs(request):
	jobList = get_job_postings(_get_job_poster(request))
	context = {'jobList': jobList}
	return render(request,'job_posting/jobs.html',context)

def view_profile(request):
	return render(request,'job_posting/view_profile.html')

def new_job(request):
	return render(request,'job_posting/index.html')

def login(request):
	return mainpage(request)

# form stuff
def post_new_job(request):
	description = request.POST['description']
	short_description = request.POST['short_description']
	# TODO have to check these probably w/ js
	# bidding_deadline = request.POST['bidding_deadline']
	# compensation_amount = request.POST['compensation_amount']
	myPosting = JobPosting(
			description=description,
			short_description=short_description,
			bidding_deadline=now() + timedelta(days=10),
			bidding_confirmation_deadline=now() + timedelta(days=15),
			bid_includes_compensation_amount = False,
			bid_includes_completion_date = False,
		)
	create_job_posting(_get_job_poster(request), myPosting)
	return jobs(request)

# TODO Lots of repittion 	
def edit_job_form(request):
	description = request.POST['description']
	short_description = request.POST['short_description']
	jobPk = request.POST['pk']
	# TODO have to check these probably w/ js
	# bidding_deadline = request.POST['bidding_deadline']
	# compensation_amount = request.POST['compensation_amount']
	myJob = JobPosting.objects.get(pk= jobPk) #TODO Change this to use API
	myJob.description = description
	#TODO ADD other fields 
	myJob.short_description = short_description
	update_job_posting(_get_job_poster(request), myJob)
	return jobs(request)

def edit_my_profile_form(request):
	# name = request.POST['name']
	openid = request.session["authenticated_user"].openid
	jobposter = JobPoster.objects.get(openid=openid)
	jobposter.description = request.POST['description']
	jobposter.email = request.POST['email']
	jobposter.phone_number = request.POST['phone_number']
	update_job_poster(jobposter)
	return mainpage(request)

# Helper Functions
def _get_job_poster(request):
	openid = request.session["authenticated_user"].openid
	jobposter = JobPoster.objects.get(openid=openid)
	return jobposter


