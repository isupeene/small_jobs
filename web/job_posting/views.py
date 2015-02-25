from django.shortcuts import render
from django.http import HttpResponse
from django.core.exceptions import PermissionDenied

from small_jobs_api.decorators import require_login


# A sample view requiring OpenID authentication.
@require_login
def protected(request):
	return HttpResponse("Hello, World!")

