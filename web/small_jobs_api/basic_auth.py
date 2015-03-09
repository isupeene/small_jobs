from django.core.exceptions import SuspiciousOperation
from django.db import IntegrityError

from rest_framework.authentication import BaseAuthentication
from rest_framework.exceptions import AuthenticationFailed

from small_jobs_api.models import Contractor

from base64 import b64decode


class BasicAuthentication(BaseAuthentication):
	def authenticate(self, request):
		if 'authenticated_user' in request.session:
			return (request.session['authenticated_user'], None)
		elif 'HTTP_AUTHORIZATION' in request.META:
			b64_credentials = request.META['HTTP_AUTHORIZATION'].split()[1]
			email = b64decode(b64_credentials).split(":")[0]
			try:
				request.session['authenticated_user'] = \
					Contractor.objects.get(email=email)
				return (request.session['authenticated_user'], None)
			except:
				raise AuthenticationFailed
		else:
			return None

def create_account(request, contractor):
	try:
		contractor.save()
		request.session['authenticated_user'] = contractor
	except IntegrityError as ex:
		print(ex)
		raise SuspiciousOperation

def logout(request):
	if 'authenticated_user' in request.session:
		del request.session['authenticated_user']

