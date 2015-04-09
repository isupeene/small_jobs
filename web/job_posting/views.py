# Requirements 3.2.1
from django.shortcuts import *
from django.http import HttpResponse
from django.core.exceptions import PermissionDenied
from django.utils.timezone import now
from django.core.serializers import serialize
from django.template import RequestContext
from datetime import timedelta

from small_jobs_api.decorators import require_login
from small_jobs_api.models import (
	JobPoster, JobPosting, Contractor
)
from small_jobs_api.job_posting_api import *
from job_posting.forms import *
from django.views.decorators.csrf import csrf_exempt #quickfix
import json



@require_login
def protected(request):
	return HttpResponse("Hello, World!")

# Requirement 3.2.1.1
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

# Requirement 3.2.1.3.4
@require_login
def job_details(request):
	jobPk = request.GET.get('pk','')
	complete_job = request.GET.get('completed','')
	active_job = request.GET.get('active','')
	myJob = JobPosting.objects.get(pk= jobPk) #TODO Change this to use API
	poster = _get_job_poster(request)
	bidList = get_bids(poster,myJob.pk)
	print active_job
	jobSkills = _get_job_skills(myJob)
	context = {'myJob': myJob , 'bidList': bidList, 'complete': complete_job, 'active' : active_job}
	if myJob.marked_completed_by_contractor and myJob.completed :
		rating = _get_old_rating(poster , myJob.contractor)
		context['rating'] = rating
	if  jobSkills:
		context['skills'] = jobSkills	
	return render(request,'job_posting/job_details.html',context)

# Requirement 3.2.1.3.1
@require_login
def jobs(request):
	poster = _get_job_poster(request)
	jobList = get_job_postings(poster)
	activeJobs = get_active_jobs(poster)
	completedJobs = get_completed_jobs(poster)
	context = {'jobList': jobList , 'activeJobs' : activeJobs , 'completedJobs' :completedJobs }
	return render(request,'job_posting/jobs.html',context)

# Requirement 3.2.1.3.4.1
@require_login
def view_profile(request):
	contractorPK = request.GET.get('contractor','')
	rating = get_contractor_rating(_get_contractor(contractorPK))
	if rating != None:
		context = {'userInfo': _get_contractor(contractorPK), 'rating': rating}
	else:
		context = context = {'userInfo': _get_contractor(contractorPK) }
	return render(request,'job_posting/view_profile.html',context)

# form stuff

# Requirement 3.2.1.1.2
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
			ctx = {'form' : form}
	else:
		# If the request was not a POST, display the form to enter details.
		form = JobPosterForm(instance= _get_job_poster(request))
		ctx = {'form' : form}
		myRating = get_rating(_get_job_poster(request))
		if myRating != None:
			ctx['myRating'] = myRating
    # Bad form (or form details), no form supplied...
    # Render the form with error messages (if any).
	return render_to_response('job_posting/edit_profile.html', ctx, context)

# Requirements 3.2.1.2 , 3.2.1.3.3
@require_login
def post_job(request):
    context = RequestContext(request)
    creating = True
    jobPk = request.GET.get('pk','')
    skillsFormSet = get_jobskill_formset( extra=1, can_delete=True)
    formset = skillsFormSet()
    if request.method == 'POST':
        form = JobPostingForm(request.POST)
        # Have we been provided with a valid form?
        if form.is_valid():
			description = form.cleaned_data['description']
			short_description = form.cleaned_data['short_description']
			bidding_deadline = form.cleaned_data['bidding_deadline']
			print bidding_deadline
			bidding_confirmation_deadline = form.cleaned_data['bidding_confirmation_deadline']
			print bidding_confirmation_deadline
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
				skillsFormSet = skillsFormSet(request.POST,instance = myJob)
				if skillsFormSet.is_valid():
					skillsFormSet.save()
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
				skillsFormSet = skillsFormSet(request.POST,instance = myPosting)
				if skillsFormSet.is_valid():	
					skillsFormSet.save()
			return HttpResponsePermanentRedirect("/job_posting/jobs/")
        else:
            # The supplied form contained errors - just print them to the terminal.
            print form.errors
            if jobPk != '': 
				creating = False
				myJob = JobPosting.objects.get(pk= jobPk)
				formset = skillsFormSet(instance=myJob)
    else:
    	# TODO better to specify in urls and have parameters
		if jobPk != '': 
			myJob = JobPosting.objects.get(pk= jobPk)
			form = JobPostingForm(instance = myJob)
			creating = False
			myJobSkills = _get_job_skills(myJob)
			formset = skillsFormSet(instance=myJob)
		else:
			form = JobPostingForm()

    # Bad form (or form details), no form supplied...
    # Render the form with error messages (if any).
    return render_to_response('job_posting/create_job.html', {'form': form,'creating': creating, 'pk': jobPk , 'formset' : formset}, context)

# Requirement 3.2.1.4
def rate_contractor_form(request):
	rating = request.POST['rating']
	contractorPK = request.GET.get('contractor','')
	rate_contractor(_get_job_poster(request), _get_contractor(contractorPK), rating)
	return HttpResponsePermanentRedirect("/job_posting/jobs/")

# Requirements 3.2.1.4.2 , 3.2.1.3.5 , 3.2.1.3.2
@csrf_exempt
def js_message(request):
	# Get the context from the request.
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
			print jobPoster
			print parsedJob['pk']
			accept_bid(jobPoster, parsedJob['pk'])
	payload = {'success':'Success!'}
	return HttpResponse(json.dumps(payload), content_type='application/json')

# Helper Functions
def _get_job_poster(request):
	openid = request.session["authenticated_user"].openid
	jobposter = JobPoster.objects.get(openid=openid)
	return jobposter

def _get_contractor(contractorPK):
	contractor = Contractor.objects.get(pk=contractorPK)
	return contractor

def _get_old_rating(job_poster , contractor):
	try:
		existing_rating = ContractorRating.objects.get(
			contractor=contractor,
			poster=job_poster
		)
		return existing_rating.rating
	except ContractorRating.DoesNotExist:
		return 3

def _get_job_skills(job):
	skills = JobSkill.objects.filter(job=job)
	return skills.all()


