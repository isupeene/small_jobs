from django.conf.urls import patterns, url
from job_posting import views


urlpatterns = patterns('',
	url(r'^protected/$', views.protected, name='protected')
)

