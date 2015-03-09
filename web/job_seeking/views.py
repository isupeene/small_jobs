from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.permissions import AllowAny

from small_jobs_api.job_seeking_api import *
from small_jobs_api.models import *
from small_jobs_api.serializers import *
from small_jobs_api import basic_auth


def deserialize_request(serializer_class, **skwargs):
	def decorator(func):
		def new_func(self, request, *args, **kwargs):
			serializer = serializer_class(data=request.data, **skwargs)
			if serializer.is_valid():
				func(self, request, serializer.get_instance(), *args, **kwargs)
				return Response(serializer.data, status=201)
			else:
				return Response(serializer.errors, status=400)
		return new_func
	return decorator

def serialize_response(serializer_class, **skwargs):
	def decorator(func):
		def new_func(self, request, *args, **kwargs):
			result = func(self, request, *args, **kwargs)
			serializer = serializer_class(result, **skwargs)
			return Response(serializer.data)
		return new_func
	return decorator

# Login and Identity

class CreateAccount(APIView):
	permission_classes = (AllowAny,)

	@deserialize_request(NewContractorSerializer)
	def post(self, request, contractor):
		basic_auth.create_account(request, contractor)


# TODO: Clean this up.
class Login(APIView):
	permission_classes = (AllowAny,)

	def get(self, request):
		if 'authenticated_contractor' in request.session:
			return Response("Welcome, {}!".format(
				request.session['authenticated_contractor'].name
			))
		else:
			return Response("Login Failed", status=401)


class Profile(APIView):
	def get(self, request):
		contractor = request.user
		return HttpResponse('name: {}; email: {}'.format(
			contractor.name, contractor.email
		))


class Jobs(APIView):
	@serialize_response(default_serializer(JobPosting), many=True)
	def get(self, request):
		return get_jobs(request.user)


# TODO: Skills and region
class JobPoster(APIView):
	@serialize_response(SecureJobPosterSerializer)
	def get(self, request, id):
		return get_job_poster(request.user, id)

