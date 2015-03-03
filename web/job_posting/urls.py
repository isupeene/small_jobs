from django.conf.urls import patterns, url
from job_posting import views


urlpatterns = patterns('',
	url(r'^protected/$', views.protected, name='protected'),
	url(r'^homepage/$', views.homepage, name='homepage'),
	url(r'^mainpage/$', views.mainpage, name='mainpage'),
	url(r'^edit_profile/$', views.edit_profile, name='edit_profile'),
	url(r'^create_job/$', views.create_job, name='create_job'),
	url(r'^job_details/$', views.job_details, name='job_details'),
	url(r'^view_profile/$', views.view_profile, name='view_profile'),
	url(r'^jobs/$', views.jobs, name='jobs'),
	url(r'^newjob/$', views.new_job, name='new job'),
	url(r'^create_job/post_a_job', views.post_new_job, name='post_a_job'),
	url(r'^edit_profile/edit_my_profile_form', views.edit_my_profile_form, name='edit_my_profile_form'),

)


