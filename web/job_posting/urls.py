from django.conf.urls import patterns, url
from job_posting import views


urlpatterns = patterns('',
	url(r'^protected/$', views.protected, name='protected'),
	url(r'^homepage/$', views.homepage, name='homepage'),
	url(r'^mainpage/$', views.mainpage, name='mainpage'),
	url(r'^edit_profile/$', views.edit_profile, name='edit_profile'),
	url(r'^create_job/$', views.post_job, name='create_job'),
	url(r'^post_job/$', views.post_job, name='post_job'),
	url(r'^job_details/$', views.job_details, name='job_details'),
	url(r'^view_profile/$', views.view_profile, name='view_profile'),
	url(r'^jobs/$', views.jobs, name='jobs_main'),
	url(r'^jobs/js_message/$', views.js_message, name='js_message'),
	url(r'^job_details/js_message/$', views.js_message, name='js_message'),
	url(r'^homepage/login', views.login, name='login'),
	url(r'^edit_job/$', views.post_job, name='edit_job'),
	url(r'^job_details/rate_contractor_form', views.rate_contractor_form, name='rate_contractor_form'),
)


