from django.shortcuts import render

from small_jobs_api.job_seeking_api import get_jobs

from small_jobs_api.models import JobPosting, JobPoster
from django.core.serializers import serialize
from django.http import HttpResponse

from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated, AllowAny

from base64 import b64decode
import json

from small_jobs_api.models import Contractor
from small_jobs_api.serializers import NewContractorSerializer
from small_jobs_api import basic_auth


def deserialize_post_request(serializer_class):
	def decorator(func):
		def new_func(self, request, **kwargs):
			serializer = serializer_class(data=request.data, **kwargs)
			if serializer.is_valid():
				func(self, request, serializer.get_instance())
				return Response(serializer.data, status=201)
			else:
				return Response(serializer.errors, status=400)
		return new_func
	return decorator

# Login and Identity

class CreateAccount(APIView):
	permission_classes = (AllowAny,)

	@deserialize_post_request(NewContractorSerializer)
	def post(self, request, contractor):
		basic_auth.create_account(request, contractor)


class Login(APIView):
	permission_classes = (AllowAny,)

	def get(self, request):
		if 'authenticated_user' in request.session:
			return Response("Welcome, {}!".format(
				request.session['authenticated_user'].name
			))
		else:
			return Response("Login Failed", status=401)


class Profile(APIView):
	permission_classes = (IsAuthenticated,)

	def get(self, request):
		contractor = request.user
		return HttpResponse('name: {}; email: {}'.format(
			contractor.name, contractor.email
		))

def jobs(request):
	return HttpResponse(serialize("json", get_jobs(None)))

def job_poster(request, id):
	return HttpResponse(serialize("json", JobPoster.objects.filter(pk=id)))

