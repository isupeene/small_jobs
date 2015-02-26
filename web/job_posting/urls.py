from django.conf.urls import patterns, url
from job_posting import views


urlpatterns = patterns('',
	url(r'^protected/$', views.protected, name='protected'),
	url(r'^newjob/$', views.new_job, name='new job'),
	url(r'^newjob/post_a_job', views.post_new_job, name='vote'),

)


