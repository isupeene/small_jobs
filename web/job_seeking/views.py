from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.permissions import AllowAny

from small_jobs_api.job_seeking_api import *
from small_jobs_api.models import *
from small_jobs_api.serializers import *
from small_jobs_api.basic_auth import *


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

class CreateAccountView(APIView):
	permission_classes = (AllowAny,)

	@deserialize_request(NewContractorSerializer)
	def post(self, request, contractor):
		create_account(request, contractor)


# TODO: Clean this up.
class LoginView(APIView):
	def get(self, request):
		return Response("Welcome, {}!".format(
			request.user.name
		))

class LogoutView(APIView):
	permission_classes = (AllowAny,)

	def get(self, request):
		logout(request)
		return Response("Successfully logged out.")

class ProfileView(APIView):
	def get(self, request):
		contractor = request.user
		return HttpResponse('name: {}; email: {}'.format(
			contractor.name, contractor.email
		))


class JobsView(APIView):
	@serialize_response(default_serializer(JobPosting), many=True)
	def get(self, request):
		return get_jobs(request.user)


# TODO: Skills and region
class JobPosterView(APIView):
	@serialize_response(SecureJobPosterSerializer)
	def get(self, request, poster_id):
		return get_job_poster(request.user, int(poster_id))


class BidView(APIView):
	@deserialize_request(default_serializer(Bid))
	def post(self, request, bid):
		place_bid(request.user, bid)


class CurrentJobsView(APIView):
	@serialize_response(default_serializer(JobPosting), many=True)
	def get(self, request):
		return get_current_jobs(request.user)


class CompletedJobsView(APIView):
	@serialize_response(default_serializer(JobPosting), many=True)
	def get(self, request):
		return get_completed_jobs(request.user)


class ProspectiveJobsView(APIView):
	@serialize_response(default_serializer(JobPosting), many=True)
	def get(self, request):
		return get_prospective_jobs(request.user)


class RateJobPosterView(APIView):
	def post(self, request, poster_id, rating):
		rate_job_poster(request.user, int(poster_id), int(rating))
		return Response("OK", status=201)


class MarkCompleteView(APIView):
	def post(self, request, posting_id):
		mark_complete(request.user, int(posting_id))
		return Response("OK", status=201)

