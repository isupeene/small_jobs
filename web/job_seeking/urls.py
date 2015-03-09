from django.conf.urls import patterns, url
from job_seeking import views

urlpatterns = patterns('',
	url(r'^profile/$', views.ProfileView.as_view(), name='profile'),
	url(r'^create_account/$', views.CreateAccountView.as_view(), name='create_account'),
	url(r'^login/$', views.LoginView.as_view(), name='login'),
	url(r'^logout/$', views.LogoutView.as_view(), name='logout'),
	url(r'^jobs/$', views.JobsView.as_view(), name='jobs'),
	url(r'^job_poster/(?P<id>\d+)/$', views.JobPosterView.as_view(), name='job_poster')
)

