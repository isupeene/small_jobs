# Requirement 3.2.1.1.1

from django.shortcuts import render
from django.http import HttpResponseRedirect
from django.core.exceptions import PermissionDenied
from django.core.urlresolvers import reverse

from openid.extensions.sreg import SRegRequest, SRegResponse

from small_jobs_api.qbo_openid import (
	get_consumer, get_job_poster, get_base_url
)

from urlparse import urljoin


def begin_openid_login(request):
	request.session["openid_login"] = True

	consumer = get_consumer(request)

	auth_request = consumer.begin('https://openid.intuit.com/openid/xrds')
	auth_request.addExtension(SRegRequest(
		required=['fullname', 'email']
	))

	trust_root = get_base_url(request)
	return_to = urljoin(get_base_url(request), request.path)

	url = auth_request.redirectURL(trust_root, return_to)
	return HttpResponseRedirect(url)

def finish_openid_login(request):
	del request.session["openid_login"]

	args = {k : v for k, v in request.GET.iteritems()}
	args.update({k : v for k, v in request.POST.iteritems()})

	consumer = get_consumer(request)
	return_to = urljoin(get_base_url(request), request.path)
	response = consumer.complete(args, return_to)

	if response.status != "success":
		raise PermissionDenied
	else:
		sreg = SRegResponse.fromSuccessResponse(response)
		request.session["authenticated_user"] = get_job_poster(response, sreg)
		return HttpResponseRedirect(return_to)


