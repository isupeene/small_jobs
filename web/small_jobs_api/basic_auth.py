from django.core.exceptions import SuspiciousOperation, ValidationError
from django.db import IntegrityError

from rest_framework.authentication import BaseAuthentication
from rest_framework.exceptions import AuthenticationFailed

from small_jobs_api.models import Contractor

from base64 import b64decode


class BasicAuthentication(BaseAuthentication):
	def authenticate(self, request):
		if 'authenticated_contractor' in request.session:
			return (request.session['authenticated_contractor'], None)
		elif 'HTTP_AUTHORIZATION' in request.META:
			b64_credentials = request.META['HTTP_AUTHORIZATION'].split()[1]
			email = b64decode(b64_credentials).split(":")[0]
			try:
				request.session['authenticated_contractor'] = \
					Contractor.objects.get(email=email)
				return (request.session['authenticated_contractor'], None)
			except:
				raise AuthenticationFailed
		else:
			return None

def create_account(request, contractor):
	try:
		contractor.save()
		request.session['authenticated_contractor'] = contractor
	except (IntegrityError, ValidationError):
		raise SuspiciousOperation

def logout(request):
	if 'authenticated_contractor' in request.session:
		del request.session['authenticated_contractor']

