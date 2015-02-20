from django.shortcuts import render
from django.http import HttpResponse
from django.core.exceptions import PermissionDenied

from oauth2_provider.views.generic import ProtectedResourceView
from oauth2_provider.decorators import protected_resource

from small_jobs_api.decorators import require_login


# A sample view requiring OpenID authentication.
@require_login
def protected(request):
	return HttpResponse("Hello, World!")

