from django.conf.urls import patterns, url
from job_seeking import views

urlpatterns = patterns('',
	url(r'^jobs/$', views.jobs, name='jobs'),
	url(r'^job_poster/(?P<id>\d+)/$', views.job_poster, name='job_poster')
)

