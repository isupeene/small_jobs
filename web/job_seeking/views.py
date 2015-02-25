from django.shortcuts import render

# TODO: Implement API methods for job seekers
# and use them instead of accessing models directly
from small_jobs_api.models import JobPosting, JobPoster
from django.core.serializers import serialize
from django.http import HttpResponse

def jobs(request):
	return HttpResponse(serialize("json", JobPosting.objects.all()))

def job_poster(request, id):
	return HttpResponse(serialize("json", JobPoster.objects.filter(pk=id)))

