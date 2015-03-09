from django.test import TestCase
from django.core.urlresolvers import reverse as get_url
from django.utils.timezone import now

from base64 import b64encode
from datetime import timedelta
from json import loads

from small_jobs_api.models import *
from small_jobs_api.serializers import *


# TODO: Maybe eliminate duplication between here and small_jobs_api tests.
# Add more parameters if necessary.  Make sure not to call
# now() in a parameter default - it will only be executed when the
# function is interpreted, not when it's run!
def new_job_posting(description="foo", short_description="bar",
					**kwargs):
	return JobPosting(
		description=description,
		short_description=short_description,
		bidding_deadline=now() + timedelta(days=10),
		bidding_confirmation_deadline=now() + timedelta(days=15),
		bid_includes_compensation_amount = False,
		bid_includes_completion_date = False,
		**kwargs
	)

class CreateAccountTest(TestCase):
	def test_create_account(self):
		data = {
			"name" : "Emily",
			"email" : "emily95@gmail.com",
			"description" : "Recent Graduate",
			"phone_number" : "555-555-5555"
		}
		response = self.client.post(get_url("job_seeking:create_account"), data)
		self.assertEquals(201, response.status_code)

		emily = self.client.session["authenticated_contractor"]
		emilys_data = {
			field.name : getattr(emily, field.name)
			for field
			in Contractor._meta.fields
		}
		del emilys_data["id"]
		self.assertEquals(data, emilys_data)
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

# TODO: Update login test cases when password protection is implemented.
class LoginTest(TestCase):
	def setUp(self):
		data = {
			"name" : "Emily",
			"email" : "emily95@gmail.com"
		}
		self.client.post(get_url("job_seeking:create_account"), data)
		self.client.get(get_url("job_seeking:logout"))

	def test_login(self):
		response = self.client.get(
			get_url("job_seeking:login"),
			HTTP_AUTHORIZATION="Basic {}".format(b64encode("emily95@gmail.com:"))
		)

		self.assertEquals(200, response.status_code)
		self.assertEquals(
			Contractor.objects.get(),
			self.client.session["authenticated_contractor"]
		)

	def test_login_user_does_not_exist(self):
		response = self.client.get(
			get_url("job_seeking:login"),
			HTTP_AUTHORIZATION="Basic {}".format(b64encode("joseph86@gmail.com:"))
		)
		self.assertEquals(403, response.status_code)

class JobSeekingAPITest(TestCase):
	def setUp(self):
		JobPoster(name="Bob", openid="0").save()
		# Create and login as Emily
		self.client.post(
			get_url("job_seeking:create_account"),
			{"name" : "Emily", "email" : "emily95@gmail.com"}
		)

	def test_get_jobs(self):
		bob = JobPoster.objects.get(name="Bob")
		posting = new_job_posting(poster=bob)
		posting.save()

		response = self.client.get(get_url("job_seeking:jobs"))
		data = loads(response.content)

		self.assertEquals(200, response.status_code)
		self.assertEquals(1, len(data))
		self.assertEquals(
			posting.id,
			data[0]['id']
		)

	def test_get_jobs_no_jobs(self):
		response = self.client.get(get_url("job_seeking:jobs"))
		data = loads(response.content)

		self.assertEquals(0, len(data))

	# TODO: Test skills and regions

	def test_get_job_poster(self):
		bob = JobPoster.objects.get(name="Bob")
		response = self.client.get(
			get_url("job_seeking:job_poster", kwargs={'id' : bob.id})
		)
		data = loads(response.content)

		self.assertEquals(bob.id, data['id'])
		self.assertEquals("Bob", data['name'])

	def test_get_job_poster(self):
		bob = JobPoster.objects.get(name="Bob")
		response = self.client.get(
			get_url("job_seeking:job_poster", kwargs={'id' : bob.id - 1})
		)
		self.assertEquals(404, response.status_code)

