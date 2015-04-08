# Requirement 3.2.2

from django.conf.urls import patterns, url
from job_seeking.views import *

urlpatterns = patterns('',
	url(r'^profile/$', ProfileView.as_view(), name='profile'),
	url(r'^create_account/$', CreateAccountView.as_view(), name='create_account'),
	url(r'^login/$', LoginView.as_view(), name='login'),
	url(r'^logout/$', LogoutView.as_view(), name='logout'),
	url(r'^jobs/$', JobsView.as_view(), name='jobs'),
	url(r'^job_poster/(?P<poster_id>\d+)/$', JobPosterView.as_view(), name='job_poster'),
	url(r'^job_poster_rating/(?P<poster_id>\d+)/$', JobPosterRatingView.as_view(), name='job_poster_rating'),
	url(r'^bid/$', BidView.as_view(), name='bid'),
	url(r'^current_jobs/$', CurrentJobsView.as_view(), name='current_jobs'),
	url(r'^completed_jobs/$', CompletedJobsView.as_view(), name='completed_jobs'),
	url(r'^prospective_jobs/$', ProspectiveJobsView.as_view(), name='prospective_jobs'),
	url(r'^rate_job_poster/(?P<poster_id>\d+)/(?P<rating>\d+)/$', RateJobPosterView.as_view(), name='rate_job_poster'),
	url(r'^mark_complete/(?P<posting_id>\d+)/$', MarkCompleteView.as_view(), name='mark_complete')
)

