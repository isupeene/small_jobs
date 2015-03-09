from django.test import TestCase
from django.core.urlresolvers import reverse as get_url

from small_jobs_api.models import *


class AuthTest(TestCase):
	def test_create_account(self):
		data = {
			"name" : "Emily",
			"email" : "emily95@gmail.com"
		}
		response = self.client.post(get_url("job_seeking:create_account"), data)
		self.assertEquals(201, response.status_code)

		emily = self.client.session["authenticated_contractor"]
		self.assertEquals(data['name'], emily.name)
		self.assertEquals(data['email'], emily.email)
		self.assertEquals(emily, Contractor.objects.get())

	def test_create_account_no_name(self):
		data = {
			"email" : "emily95@gmail.com"
		}
		response = self.client.post(get_url("job_seeking:create_account"), data)

		self.assertEquals(400, response.status_code)

	def test_create_account_no_email(self):
		data = {
			"name" : "Emily"
		}
		response = self.client.post(get_url("job_seeking:create_account"), data)

		self.assertEquals(400, response.status_code)

	def test_create_account_duplicate_email(self):
		data = {
			"name" : "Emily",
			"email" : "emily95@gmail.com"
		}
		response = self.client.post(get_url("job_seeking:create_account"), data)
		self.assertEquals(201, response.status_code)

		response = self.client.post(get_url("job_seeking:create_account"), data)
		self.assertEquals(400, response.status_code)

		data['name'] = 'Not Emily'
		response = self.client.post(get_url("job_seeking:create_account"), data)
		self.assertEquals(400, response.status_code)

		data['name'] = "Not Emily"
		data['email'] = "notemily95@gmail.com"
		response = self.client.post(get_url("job_seeking:create_account"), data)
		self.assertEquals(201, response.status_code)

