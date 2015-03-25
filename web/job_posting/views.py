from django.shortcuts import *
from django.http import HttpResponse
from django.core.exceptions import PermissionDenied
from django.utils.timezone import now
from django.core.serializers import serialize
from django.template import RequestContext
from datetime import timedelta

from small_jobs_api.decorators import require_login
from small_jobs_api.models import (
	JobPoster, JobPosting
)
from small_jobs_api.job_posting_api import *
from job_posting.forms import *
from django.views.decorators.csrf import csrf_exempt #quickfix
import json


# A sample view requiring OpenID authentication.
@require_login
def protected(request):
	return HttpResponse("Hello, World!")

@require_login
def homepage(request):
	return render(request,'job_posting/homepage.html')

@require_login
def mainpage(request):
	context = {'userInfo': _get_job_poster(request)}
	return render(request,'job_posting/mainpage.html', context)

@require_login
def create_job(request):
	return render(request,'job_posting/create_job.html')

@require_login
def edit_job(request):
	jobPk = request.GET.get('pk','')
	myJob = JobPosting.objects.get(pk= jobPk) #TODO Change this to use API
	context = {'myJob': myJob }
	return render(request,'job_posting/edit_job.html',context)

@require_login
def job_details(request):
	jobPk = request.GET.get('pk','')
	myJob = JobPosting.objects.get(pk= jobPk) #TODO Change this to use API
	bidList = get_bids(_get_job_poster(request),myJob.pk)
	context = {'myJob': myJob , 'bidList': bidList}
	return render(request,'job_posting/job_details.html',context)

@require_login
def jobs(request):
	jobList = get_job_postings(_get_job_poster(request))
	context = {'jobList': jobList}
	return render(request,'job_posting/jobs.html',context)

@require_login
def view_profile(request):
	return render(request,'job_posting/view_profile.html')

@require_login
def login(request):
	return mainpage(request)

# form stuff

@require_login
def edit_profile(request):
    # Get the context from the request.
    context = RequestContext(request)

    # A HTTP POST?
    if request.method == 'POST':
        form = JobPosterForm(request.POST)

        # Have we been provided with a valid form?
        if form.is_valid():
			jobposter = _get_job_poster(request)
			jobposter.description = form.cleaned_data['description']
			jobposter.email = form.cleaned_data['email']
			jobposter.phone_number = form.cleaned_data['phone_number']
			jobposter.region = form.cleaned_data['region']
			update_job_poster(jobposter)
			return jobs(request)
        else:
            # The supplied form contained errors - just print them to the terminal.
            print form.errors
    else:
        # If the request was not a POST, display the form to enter details.
		form = JobPosterForm(instance= _get_job_poster(request))

    # Bad form (or form details), no form supplied...
    # Render the form with error messages (if any).
    return render_to_response('job_posting/edit_profile.html', {'form': form}, context)

@require_login
def post_job(request):
    context = RequestContext(request)
    creating = True
    jobPk = request.GET.get('pk','')
    if request.method == 'POST':
        form = JobPostingForm(request.POST)
        # Have we been provided with a valid form?
        if form.is_valid():
			description = form.cleaned_data['description']
			short_description = form.cleaned_data['short_description']
			bidding_deadline = form.cleaned_data['bidding_deadline']
			bidding_confirmation_deadline = form.cleaned_data['bidding_confirmation_deadline']
			compensation_amount = form.cleaned_data['compensation_amount']
			completion_date = form.cleaned_data['completion_date']
			bid_includes_compensation_amount = form.cleaned_data['bid_includes_compensation_amount']
			bid_includes_completion_date = form.cleaned_data['bid_includes_completion_date']
			if jobPk != '': 
				myJob = JobPosting.objects.get(pk= jobPk)
				myJob.description = description
				myJob.short_description=short_description
				myJob.bidding_deadline=bidding_deadline
				myJob.bidding_confirmation_deadline=bidding_confirmation_deadline
				myJob.bid_includes_compensation_amount = bid_includes_compensation_amount
				myJob.compensation_amount = compensation_amount
				myJob.bid_includes_completion_date = bid_includes_completion_date
				myJob.completion_date = completion_date
				
				update_job_posting(_get_job_poster(request), myJob)
			else:
				myPosting = JobPosting(
					description=description,
					short_description=short_description,
					bidding_deadline=bidding_deadline,
					bidding_confirmation_deadline=bidding_confirmation_deadline,
					bid_includes_compensation_amount = bid_includes_compensation_amount,
					compensation_amount = compensation_amount,
					bid_includes_completion_date = bid_includes_completion_date,
					completion_date = completion_date,
				)
				create_job_posting(_get_job_poster(request), myPosting)
			return HttpResponsePermanentRedirect("/job_posting/jobs/")
        else:
            # The supplied form contained errors - just print them to the terminal.
            print form.errors
    else:
    	# TODO better to specify in urls and have parameters
		if jobPk != '': 
			myJob = JobPosting.objects.get(pk= jobPk)
			form = JobPostingForm(instance = myJob)
			creating = False
		else:
			form = JobPostingForm()

    # Bad form (or form details), no form supplied...
    # Render the form with error messages (if any).
    return render_to_response('job_posting/create_job.html', {'form': form,'creating': creating, 'pk': jobPk}, context)

@csrf_exempt
def js_message(request):
	# Get the context from the request.
	print "Hey bud"
	JSONdata = request.POST.getlist('values[]')
	action = request.POST.get('action')
	payload = {'success':False}
	jobPoster = _get_job_poster(request)
	for job in JSONdata:
		parsedJob = json.loads(job)
		if (action == 'delete'):
			delete_job_posting(jobPoster,parsedJob['pk'])
		elif (action == 'mark'):    			
			mark_complete(jobPoster,JobPosting.objects.get(pk=parsedJob['pk'] ))
		elif (action == 'accept'):
			accept_bid(jobPoster, parsedJob['pk'])
	payload = {'success':True}
	return HttpResponse(json.dumps(payload), content_type='application/json')

# Helper Functions
def _get_job_poster(request):
	openid = request.session["authenticated_user"].openid
	jobposter = JobPoster.objects.get(openid=openid)
	return jobposter


