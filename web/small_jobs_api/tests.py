from django.test import TestCase
from django.utils.timezone import now
from django.core.exceptions import SuspiciousOperation, PermissionDenied
from django.http import Http404

from datetime import timedelta

from small_jobs_api.models import (
	JobPoster, JobPosting, Contractor, JobPosterRating, Bid
)
from small_jobs_api.api import (
	update_job_poster, get_rating,
	create_job_posting, get_job_postings,
	get_active_jobs, get_completed_jobs,
	delete_job_posting, update_job_posting,
	get_bids, accept_bid, rate_contractor
)


class JobPostingAPITest(TestCase):
	def setUp(self):
		JobPoster(name="Bob", openid="0").save()
		JobPoster(name="Frank", openid="1").save()
		Contractor(name="Emily").save()
		Contractor(name="Joseph").save()

	# Add more parameters if necessary.  Make sure not to call
	# now() in a parameter default - it will only be executed when the
	# function is interpreted, not when it's run!
	def new_job_posting(self, description="foo", short_description="bar",
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

	def test_update_job_poster(self):
		bob = JobPoster.objects.get(name="Bob")
		email = "bob@cableguy.com"
		bob.email = email
		update_job_poster(bob)

		bob = JobPoster.objects.get(name="Bob")
		self.assertEquals(email, bob.email)

	def test_update_job_poster_field_too_long(self):
		with self.assertRaises(SuspiciousOperation):
			bob = JobPoster.objects.get(name="Bob")
			email = "toolong" * 50
			bob.email = email
			update_job_poster(bob)

	# TODO: Test invalid email and phone number fields

	def test_get_rating(self):
		emily = Contractor.objects.get(name="Emily")
		joseph = Contractor.objects.get(name="Joseph")
		bob = JobPoster.objects.get(name="Bob")

		JobPosterRating(contractor=emily, poster=bob, rating=3).save()
		JobPosterRating(contractor=joseph, poster=bob, rating=4).save()

		self.assertAlmostEquals(3.5, get_rating(bob))

	def test_get_rating_no_ratings(self):
		bob = JobPoster.objects.get(name="Bob")
		self.assertEquals(None, get_rating(bob))

	def test_create_job_posting(self):
		bob = JobPoster.objects.get(name="Bob")
		posting = self.new_job_posting()
		create_job_posting(bob, posting)

		self.assertTrue(posting in bob.jobposting_set.all())

	def test_create_job_posting_missing_required_field(self):
		with self.assertRaises(SuspiciousOperation):
			bob = JobPoster.objects.get(name="Bob")
			posting = self.new_job_posting(description=None)
			create_job_posting(bob, posting)

	def test_create_job_posting_field_too_long(self):
		with self.assertRaises(SuspiciousOperation):
			bob = JobPoster.objects.get(name="Bob")
			posting = self.new_job_posting(short_description="toolong" * 50)
			create_job_posting(bob, posting)

	# TODO: Test other contstraints, such as requiring compensation amount
	# if bids don't include it

	def test_get_job_postings(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		posting1 = self.new_job_posting()
		posting2 = self.new_job_posting()

		active = self.new_job_posting(contractor=emily)
		completed = self.new_job_posting(
			contractor=emily,
			completed=True,
			completion_date=now()
		)

		create_job_posting(bob, posting1)
		create_job_posting(bob, posting2)
		create_job_posting(bob, active)
		create_job_posting(bob, completed)

		self.assertEquals({posting1, posting2}, set(get_job_postings(bob)))

	def test_get_job_postings_no_postings(self):
		bob = JobPoster.objects.get(name="Bob")
		self.assertEquals(0, len(get_job_postings(bob)))

	def test_get_active_jobs(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")
		joseph = Contractor.objects.get(name="Joseph")

		active1 = self.new_job_posting(contractor=emily)
		active2 = self.new_job_posting(contractor=joseph)

		posting = self.new_job_posting()

		completed = self.new_job_posting(
			contractor=emily,
			completed=True,
			completion_date=now()
		)

		create_job_posting(bob, active1)
		create_job_posting(bob, active2)
		create_job_posting(bob, posting)
		create_job_posting(bob, completed)

		self.assertEquals({active1, active2}, set(get_active_jobs(bob)))

	def test_get_active_jobs_no_jobs(self):
		bob = JobPoster.objects.get(name="Bob")
		self.assertEquals(0, len(get_active_jobs(bob)))

	def test_get_completed_jobs(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")
		joseph = Contractor.objects.get(name="Joseph")

		completed1 = self.new_job_posting(
			contractor=emily,
			completed=True,
			completion_date=now()
		)
		completed2 = self.new_job_posting(
			contractor=joseph,
			completed=True,
			completion_date=now()
		)

		posting = self.new_job_posting()

		active = self.new_job_posting(contractor=emily)

		create_job_posting(bob, completed1)
		create_job_posting(bob, completed2)
		create_job_posting(bob, posting)
		create_job_posting(bob, active)

		self.assertEquals({completed1, completed2}, set(get_completed_jobs(bob)))

	def test_get_completed_jobs_no_jobs(self):
		bob = JobPoster.objects.get(name="Bob")
		self.assertEquals(0, len(get_completed_jobs(bob)))

	def test_delete_job_posting(self):
		bob = JobPoster.objects.get(name="Bob")
		posting = self.new_job_posting()
		create_job_posting(bob, posting)

		delete_job_posting(bob, posting.pk)

		self.assertEquals(0, len(bob.jobposting_set.all()))

	def test_delete_job_posting_wrong_owner(self):
		bob = JobPoster.objects.get(name="Bob")
		frank = JobPoster.objects.get(name="Frank")
		posting = self.new_job_posting()
		create_job_posting(bob, posting)

		with self.assertRaises(PermissionDenied):
			delete_job_posting(frank, posting.pk)

	def test_delete_job_posting_does_not_exist(self):
		bob = JobPoster.objects.get(name="Bob")

		with self.assertRaises(Http404):
			delete_job_posting(bob, 0)

	def test_update_job_posting(self):
		bob = JobPoster.objects.get(name="Bob")
		posting = self.new_job_posting(description="old description")
		create_job_posting(bob, posting)

		posting.description = "new_description"
		update_job_posting(bob, posting)

		self.assertEquals(
			"new_description",
			bob.jobposting_set.get().description
		)

	def test_update_job_posting_wrong_owner(self):
		bob = JobPoster.objects.get(name="Bob")
		frank = JobPoster.objects.get(name="Frank")
		posting = self.new_job_posting(description="old_description")
		create_job_posting(bob, posting)

		posting.description = "new_description"
		with self.assertRaises(PermissionDenied):
			update_job_posting(frank, posting)

		self.assertEquals(
			"old_description",
			bob.jobposting_set.get().description
		)

	# TODO: Test that contractors receive a notification on update and deletion

	def test_get_bids(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")
		joseph = Contractor.objects.get(name="Joseph")

		posting1 = self.new_job_posting()
		posting2 = self.new_job_posting()
		create_job_posting(bob, posting1)
		create_job_posting(bob, posting2)

		bid1 = Bid(contractor=emily, job=posting1)
		bid2 = Bid(contractor=joseph, job=posting1)
		bid3 = Bid(contractor=emily, job=posting2)

		bid1.save()
		bid2.save()
		bid3.save()

		self.assertEquals({bid1, bid2}, set(get_bids(bob, posting1.pk)))

	def test_get_bids_wrong_owner(self):
		bob = JobPoster.objects.get(name="Bob")
		frank = JobPoster.objects.get(name="Frank")
		emily = Contractor.objects.get(name="Emily")
		joseph = Contractor.objects.get(name="Joseph")

		posting = self.new_job_posting()
		create_job_posting(bob, posting)

		bid1 = Bid(contractor=emily, job=posting)
		bid2 = Bid(contractor=joseph, job=posting)
		bid1.save()
		bid2.save()

		with self.assertRaises(PermissionDenied):
			get_bids(frank, posting.pk)

	def test_get_bids_no_bids(self):
		bob = JobPoster.objects.get(name="Bob")

		posting = self.new_job_posting()
		create_job_posting(bob, posting)

		self.assertEquals(0, len(get_bids(bob, posting.pk)))

	def test_get_bids_does_not_exist(self):
		bob = JobPoster.objects.get(name="Bob")

		with self.assertRaises(Http404):
			get_bids(bob, 0)

	def test_accept_bid(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")
		joseph = Contractor.objects.get(name="Joseph")

		posting = self.new_job_posting()
		create_job_posting(bob, posting)

		bid1 = Bid(contractor=emily, job=posting)
		bid2 = Bid(contractor=joseph, job=posting)
		bid1.save()
		bid2.save()

		accept_bid(bob, bid1.pk)

		self.assertEquals(bob.jobposting_set.get().contractor, emily)

	def test_accept_bid_wrong_owner(self):
		bob = JobPoster.objects.get(name="Bob")
		frank = JobPoster.objects.get(name="Frank")
		emily = Contractor.objects.get(name="Emily")

		posting = self.new_job_posting()
		create_job_posting(bob, posting)

		bid = Bid(contractor=emily, job=posting)
		bid.save()

		with self.assertRaises(PermissionDenied):
			accept_bid(frank, bid.pk)

		self.assertEquals(bob.jobposting_set.get().contractor, None)

	def test_accept_bid_does_not_exist(self):
		bob = JobPoster.objects.get(name="Bob")

		with self.assertRaises(Http404):
			accept_bid(bob, 0)

	# TODO: Test that all contractors receive a notification when accepting a bid

	def test_rate_contractor(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		posting = self.new_job_posting(
			contractor=emily,
			completed=True,
			completion_date=now()
		)
		create_job_posting(bob, posting)

		rate_contractor(bob, emily, 5)
		self.assertEquals(5, emily.contractorrating_set.get(poster=bob).rating)

	def test_rate_contractor_job_not_completed(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		posting = self.new_job_posting(contractor=emily)
		create_job_posting(bob, posting)

		with self.assertRaises(PermissionDenied):
			rate_contractor(bob, emily, 5)

		self.assertEquals(0, len(emily.contractorrating_set.all()))

	def test_rate_contractor_no_jobs(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		with self.assertRaises(PermissionDenied):
			rate_contractor(bob, emily, 5)

		self.assertEquals(0, len(emily.contractorrating_set.all()))

	# TODO: Test that the value must be between 1 and 5

