from django.conf.urls import patterns, url
from job_seeking import views

urlpatterns = patterns('',
	url(r'^profile/$', views.Profile.as_view(), name='profile'),
	url(r'^create_account/$', views.CreateAccount.as_view(), name='create_account'),
	url(r'^login/$', views.Login.as_view(), name='login'),
	url(r'^jobs/$', views.jobs, name='jobs'),
	url(r'^job_poster/(?P<id>\d+)/$', views.job_poster, name='job_poster')
)

