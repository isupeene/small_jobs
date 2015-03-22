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
from job_posting.forms import JobPosterForm

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

@require_login
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

def edit_profile(request):
    # Get the context from the request.
    context = RequestContext(request)

    # A HTTP POST?
    if request.method == 'POST':
        form = JobPosterForm(request.POST)

        # Have we been provided with a valid form?
        if form.is_valid():
            # Save the new category to the database.
            
            # form.save(commit=True)
            # Now call the index() view.
            # The user will be shown the homepage.
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

# TODO Lots of repition 	
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

# Helper Functions
def _get_job_poster(request):
	openid = request.session["authenticated_user"].openid
	jobposter = JobPoster.objects.get(openid=openid)
	return jobposter


