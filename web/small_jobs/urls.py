from django.conf.urls import patterns, include, url

from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'small_jobs.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

	url(r'^job_posting/', include('job_posting.urls', namespace="job_posting")),
	url(r'^job_seeking/', include('job_seeking.urls', namespace="job_seeking")),
    url(r'^admin/', include(admin.site.urls)),
)
