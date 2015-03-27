from django.test import TestCase
from django.core.urlresolvers import reverse as get_url
from django.utils.timezone import now

from base64 import b64encode
from datetime import timedelta
from json import loads

from small_jobs_api.models import *
from small_jobs_api.serializers import *
from small_jobs_api import gcm_notifications

gcm_notifications.dry_run = True

# TODO: Silence annoying errors from Django in the test output.


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
		bid_includes_compensation_amount=False,
		bid_includes_completion_date=False,
		compensation_amount=50000,
		completion_date=now() + timedelta(days=50),
		**kwargs
	)

class CreateAccountTest(TestCase):
	def test_create_account(self):
		data = {
			"name" : "Emily",
			"registration_id" : "0",
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
		response_data = loads(response.content)
		self.assertEquals(emilys_data, response_data)

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
			"registration_id" : "0",
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
			"registration_id" : "0",
			"email" : "emily95@gmail.com"
		}
		self.client.post(get_url("job_seeking:create_account"), data)
		self.client.get(get_url("job_seeking:logout"))

	def test_login(self):
		emily = Contractor.objects.get(name="Emily")
		response = self.client.get(
			get_url("job_seeking:login"),
			HTTP_AUTHORIZATION="Basic {}".format(b64encode("emily95@gmail.com:"))
		)
		data = loads(response.content)

		self.assertEquals(200, response.status_code)
		self.assertEquals(
			Contractor.objects.get(),
			self.client.session["authenticated_contractor"]
		)
		self.assertEquals(emily.id, data['id'])
		self.assertEquals(emily.name, data['name'])
		self.assertEquals(emily.email, data['email'])


	def test_login_user_does_not_exist(self):
		response = self.client.get(
			get_url("job_seeking:login"),
			HTTP_AUTHORIZATION="Basic {}".format(b64encode("joseph86@gmail.com:"))
		)
		self.assertEquals(403, response.status_code)

class JobSeekingAPITest(TestCase):
	def setUp(self):
		JobPoster(name="Bob", openid="0", region="Calgary").save()
		JobPoster(name="Frank", openid="1", region="Edmonton").save()
		Contractor(name="Joseph", registration_id="1", email="joseph86@gmail.com").save()
		# Create and login as Emily
		self.client.post(
			get_url("job_seeking:create_account"),
			{"name" : "Emily", "registration_id" : "0", "email" : "emily95@gmail.com"}
		)

	def test_get_profile(self):
		emily = Contractor.objects.get(name="Emily")
		response = self.client.get(get_url("job_seeking:profile"))
		data = loads(response.content)

		self.assertEquals(emily.id, data['id'])
		self.assertEquals(emily.name, data['name'])
		self.assertEquals(emily.email, data['email'])

	def test_update_profile(self):
		emily = Contractor.objects.get(name="Emily")
		update_data = {
			"name" : "Emily Durkheim",
			"registration_id" : "1",
			"email" : "emilyd98@gmail.com"
		}

		response = self.client.post(get_url("job_seeking:profile"), update_data)
		self.assertEquals(201, response.status_code)

		new_emily = Contractor.objects.get(id=emily.id)
		self.assertEquals(update_data["name"], new_emily.name)
		self.assertEquals(update_data["registration_id"], new_emily.registration_id)
		self.assertEquals(update_data["email"], new_emily.email)

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

	def test_get_jobs_with_skill(self):
		bob = JobPoster.objects.get(name="Bob")

		python_posting = new_job_posting(poster=bob)
		python_posting.save()
		python_posting.jobskill_set.add(JobSkill(skill="python"))

		django_posting = new_job_posting(poster=bob)
		django_posting.save()
		django_posting.jobskill_set.add(JobSkill(skill="django"))

		java_posting = new_job_posting(poster=bob)
		java_posting.save()
		java_posting.jobskill_set.add(JobSkill(skill="java"))

		response = self.client.get(
			get_url("job_seeking:jobs"),
			{"skill" : ["python", "django"]}
		)
		data = loads(response.content)

		self.assertEquals(200, response.status_code)
		self.assertEquals(2, len(data))
		self.assertEquals(
			{python_posting.id, django_posting.id},
			{posting["id"] for posting in data}
		)

	def test_get_jobs_with_region(self):
		bob = JobPoster.objects.get(name="Bob")
		frank = JobPoster.objects.get(name="Frank")

		calgary_posting = new_job_posting(poster=bob)
		calgary_posting.save()

		edmonton_posting = new_job_posting(poster=frank)
		edmonton_posting.save()

		response = self.client.get(
			get_url("job_seeking:jobs"),
			{"region" : "Calgary"}
		)
		data = loads(response.content)

		self.assertEquals(200, response.status_code)
		self.assertEquals(1, len(data))
		self.assertEquals(
			calgary_posting.id,
			data[0]["id"]
		)

	def test_get_job_poster(self):
		bob = JobPoster.objects.get(name="Bob")
		response = self.client.get(
			get_url("job_seeking:job_poster", kwargs={'poster_id' : bob.id})
		)
		data = loads(response.content)

		self.assertEquals(bob.id, data['id'])
		self.assertEquals("Bob", data['name'])

	def test_get_job_poster_does_not_exist(self):
		bob = JobPoster.objects.get(name="Bob")
		response = self.client.get(
			get_url("job_seeking:job_poster", kwargs={'poster_id' : bob.id - 1})
		)
		self.assertEquals(404, response.status_code)

	def test_place_bid(self):
		bob = JobPoster.objects.get(name="Bob")
		posting = new_job_posting(poster=bob)
		posting.save()

		# TODO: There is inconsistency between this test and the
		# backend API test, since the backend API does not require
		# the contractor.  For consistency, it may be better for the
		# backend to require the contractor id to be part of the bid,
		# and raise PermissionDenied if it does not match the current user.
		emily = Contractor.objects.get(name="Emily")
		bid_data = {"job" : posting.id, "contractor" : emily.id}

		response = self.client.post(
			get_url("job_seeking:bid"),
			bid_data
		)

		self.assertEquals(201, response.status_code)
		self.assertEquals(posting.id, emily.bid_set.get().job.id)

	def test_place_bid_job_does_not_exist(self):
		emily = Contractor.objects.get(name="Emily")
		bid_data = {"job" : 0, "contractor" : emily.id}

		response = self.client.post(
			get_url("job_seeking:bid"),
			bid_data
		)
		print(response.content)

		# TODO: Here we have more inconsistency with the backend -
		# The serializer validation will return a 400 before it gets to the
		# backend, but the backend wants to throw a 404.
		self.assertEquals(400, response.status_code)

	def test_place_bid_invalid_compensation_amount(self):
		bob = JobPoster.objects.get(name="Bob")
		posting = new_job_posting(poster=bob)
		posting.save()

		emily = Contractor.objects.get(name="Emily")
		bid_data = {
			"job" : posting.id,
			"contractor" : emily.id,
			"compensation_amount" : "invalid"
		}

		response = self.client.post(
			get_url("job_seeking:bid"),
			bid_data
		)

		self.assertEquals(400, response.status_code)

	def test_place_bid_invalid_completion_date(self):
		bob = JobPoster.objects.get(name="Bob")
		posting = new_job_posting(poster=bob)
		posting.save()

		emily = Contractor.objects.get(name="Emily")
		bid_data = {
			"job" : posting.id,
			"contractor" : emily.id,
			"completion_date" : "invalid"
		}

		response = self.client.post(
			get_url("job_seeking:bid"),
			bid_data
		)

		self.assertEquals(400, response.status_code)

	def test_get_current_jobs(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		posting = new_job_posting(poster=bob)
		prospective = new_job_posting(poster=bob)
		current1 = new_job_posting(poster=bob, contractor=emily)
		current2 = new_job_posting(poster=bob, contractor=emily)
		completed1 = new_job_posting(
			poster=bob,
			contractor=emily,
			completed=True
		)
		completed2 = new_job_posting(
			poster=bob,
			contractor=emily,
			marked_completed_by_contractor=True
		)

		posting.save()
		prospective.save()
		current1.save()
		current2.save()
		completed1.save()
		completed2.save()

		Bid(job=prospective, contractor=emily).save()

		response = self.client.get(get_url("job_seeking:current_jobs"))

		self.assertEquals(200, response.status_code)
		self.assertEquals(
			{current1.id, current2.id},
			{data['id'] for data in loads(response.content)}
		)

	def test_get_current_jobs_no_current_jobs(self):
		emily = Contractor.objects.get(name="Emily")

		response = self.client.get(get_url("job_seeking:current_jobs"))
		self.assertEquals(200, response.status_code)
		self.assertEquals(0, len(loads(response.content)))

	def test_get_completed_jobs(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		posting = new_job_posting(poster=bob)
		prospective = new_job_posting(poster=bob)
		current = new_job_posting(poster=bob, contractor=emily)
		completed1 = new_job_posting(
			poster=bob,
			contractor=emily,
			completed=True
		)
		completed2 = new_job_posting(
			poster=bob,
			contractor=emily,
			marked_completed_by_contractor=True
		)

		posting.save()
		prospective.save()
		current.save()
		completed1.save()
		completed2.save()

		Bid(job=prospective, contractor=emily).save()

		response = self.client.get(get_url("job_seeking:completed_jobs"))

		self.assertEquals(200, response.status_code)
		self.assertEquals(
			{completed1.id, completed2.id},
			{data['id'] for data in loads(response.content)}
		)

	def test_get_completed_jobs_no_completed_jobs(self):
		emily = Contractor.objects.get(name="Emily")

		response = self.client.get(get_url("job_seeking:completed_jobs"))
		self.assertEquals(200, response.status_code)
		self.assertEquals(0, len(loads(response.content)))

	def test_get_prospective_jobs(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		posting = new_job_posting(poster=bob)
		prospective1 = new_job_posting(poster=bob)
		prospective2 = new_job_posting(poster=bob)
		current = new_job_posting(poster=bob, contractor=emily)
		completed1 = new_job_posting(
			poster=bob,
			contractor=emily,
			completed=True
		)
		completed2 = new_job_posting(
			poster=bob,
			contractor=emily,
			marked_completed_by_contractor=True
		)

		posting.save()
		prospective1.save()
		prospective2.save()
		current.save()
		completed1.save()
		completed2.save()

		Bid(job=prospective1, contractor=emily).save()
		Bid(job=prospective2, contractor=emily).save()

		response = self.client.get(get_url("job_seeking:prospective_jobs"))

		self.assertEquals(200, response.status_code)
		self.assertEquals(
			{prospective1.id, prospective2.id},
			{data['id'] for data in loads(response.content)}
		)

	def test_get_prospective_jobs_no_prospective_jobs(self):
		emily = Contractor.objects.get(name="Emily")

		response = self.client.get(get_url("job_seeking:completed_jobs"))
		self.assertEquals(200, response.status_code)
		self.assertEquals(0, len(loads(response.content)))

	def test_rate_job_poster(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		posting = new_job_posting(
			poster=bob,
			contractor=emily,
			completed=True
		)
		posting.save()

		response = self.client.post(
			get_url("job_seeking:rate_job_poster", kwargs={
				"poster_id" : bob.id,
				"rating" : 4
			})
		)

		self.assertEquals(201, response.status_code)
		self.assertEquals(bob.id, emily.jobposterrating_set.get().poster_id)

	def test_rate_job_poster_does_not_exist(self):
		response = self.client.post(
			get_url("job_seeking:rate_job_poster", kwargs={
				"poster_id" : 100,
				"rating" : 4
			})
		)

		# TODO: Consider if this should be 404
		self.assertEquals(403, response.status_code)

	def test_rate_job_poster_no_jobs(self):
		bob = JobPoster.objects.get(name="Bob")

		response = self.client.post(
			get_url("job_seeking:rate_job_poster", kwargs={
				"poster_id" : bob.id,
				"rating" : 4
			})
		)

		self.assertEquals(403, response.status_code)

	def test_mark_complete(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		posting = new_job_posting(
			poster=bob,
			contractor=emily
		)
		posting.save()

		response = self.client.post(
			get_url("job_seeking:mark_complete", kwargs={
				"posting_id" : posting.id
			})
		)

		self.assertEquals(201, response.status_code)
		self.assertTrue(emily.jobposting_set.get().marked_completed_by_contractor)

	def test_mark_complete_no_such_job(self):
		response = self.client.post(
			get_url("job_seeking:mark_complete", kwargs={"posting_id" : 1})
		)

		self.assertEquals(404, response.status_code)

	def test_mark_complete_permission_denied(self):
		bob = JobPoster.objects.get(name="Bob")
		joseph = Contractor.objects.get(name="Joseph")

		posting = new_job_posting(
			poster=bob,
			contractor=joseph
		)
		posting.save()

		response = self.client.post(
			get_url("job_seeking:mark_complete", kwargs={
				"posting_id" : posting.id
			})
		)

		self.assertEquals(403, response.status_code)

