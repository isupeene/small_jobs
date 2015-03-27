from django.test import TestCase
from django.utils.timezone import now
from django.core.exceptions import (
	SuspiciousOperation, PermissionDenied, ValidationError
)
from django.http import Http404

from datetime import timedelta

from small_jobs_api.models import *
from small_jobs_api import job_posting_api as post
from small_jobs_api import job_seeking_api as seek
from small_jobs_api import gcm_notifications

gcm_notifications.dry_run = True


# Add more parameters if necessary.
# It's OK to call now() in a parameter default, even though it will
# only be executed when the function is interpreted, not when it's run,
# because unit tests are a very short running process.
def new_job_posting(description="foo", short_description="bar",
					bidding_deadline=now() + timedelta(days=10),
					bidding_confirmation_deadline=now() + timedelta(days=15),
					bid_includes_compensation_amount=False,
					bid_includes_completion_date=False,
					compensation_amount=50000,
					completion_date=now() + timedelta(days=50),
					**kwargs):
	return JobPosting(
		description=description,
		short_description=short_description,
		bidding_deadline=bidding_deadline,
		bidding_confirmation_deadline=bidding_confirmation_deadline,
		bid_includes_compensation_amount=bid_includes_compensation_amount,
		bid_includes_completion_date=bid_includes_completion_date,
		compensation_amount=compensation_amount,
		completion_date=completion_date,
		**kwargs
	)

class JobPostingAPITest(TestCase):
	def setUp(self):
		JobPoster(name="Bob", openid="0").save()
		JobPoster(name="Frank", openid="1").save()
		Contractor(name="Emily", registration_id="0", email="emily95@gmail.com").save()
		Contractor(name="Joseph", registration_id="1", email="joseph86@gmail.com").save()

	def test_update_job_poster(self):
		bob = JobPoster.objects.get(name="Bob")
		email = "bob@cableguy.com"
		bob.email = email
		post.update_job_poster(bob)

		bob = JobPoster.objects.get(name="Bob")
		self.assertEquals(email, bob.email)

	def test_update_job_poster_field_too_long(self):
		with self.assertRaises(SuspiciousOperation):
			bob = JobPoster.objects.get(name="Bob")
			email = "toolong" * 50 + "@thecableguy.com"
			bob.email = email
			post.update_job_poster(bob)

	def test_get_rating(self):
		emily = Contractor.objects.get(name="Emily")
		joseph = Contractor.objects.get(name="Joseph")
		bob = JobPoster.objects.get(name="Bob")

		JobPosterRating(contractor=emily, poster=bob, rating=3).save()
		JobPosterRating(contractor=joseph, poster=bob, rating=4).save()

		self.assertAlmostEquals(3.5, post.get_rating(bob))

	def test_get_rating_no_ratings(self):
		bob = JobPoster.objects.get(name="Bob")
		self.assertEquals(None, post.get_rating(bob))

	def test_create_job_posting(self):
		bob = JobPoster.objects.get(name="Bob")
		posting = new_job_posting()
		post.create_job_posting(bob, posting)

		self.assertTrue(posting in bob.jobposting_set.all())

	def test_create_job_posting_missing_required_field(self):
		with self.assertRaises(SuspiciousOperation):
			bob = JobPoster.objects.get(name="Bob")
			posting = new_job_posting(description=None)
			post.create_job_posting(bob, posting)

	def test_create_job_posting_field_too_long(self):
		with self.assertRaises(SuspiciousOperation):
			bob = JobPoster.objects.get(name="Bob")
			posting = new_job_posting(short_description="toolong" * 50)
			post.create_job_posting(bob, posting)

	def test_get_job_postings(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		posting1 = new_job_posting()
		posting2 = new_job_posting()

		active = new_job_posting(contractor=emily)
		completed = new_job_posting(
			contractor=emily,
			completed=True,
			completion_date=now()
		)

		post.create_job_posting(bob, posting1)
		post.create_job_posting(bob, posting2)
		post.create_job_posting(bob, active)
		post.create_job_posting(bob, completed)

		self.assertEquals({posting1, posting2}, set(post.get_job_postings(bob)))

	def test_get_job_postings_no_postings(self):
		bob = JobPoster.objects.get(name="Bob")
		self.assertEquals(0, len(post.get_job_postings(bob)))

	def test_get_active_jobs(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")
		joseph = Contractor.objects.get(name="Joseph")

		active1 = new_job_posting(contractor=emily)
		active2 = new_job_posting(contractor=joseph)

		posting = new_job_posting()

		completed = new_job_posting(
			contractor=emily,
			completed=True,
			completion_date=now()
		)

		post.create_job_posting(bob, active1)
		post.create_job_posting(bob, active2)
		post.create_job_posting(bob, posting)
		post.create_job_posting(bob, completed)

		self.assertEquals({active1, active2}, set(post.get_active_jobs(bob)))

	def test_get_active_jobs_no_jobs(self):
		bob = JobPoster.objects.get(name="Bob")
		self.assertEquals(0, len(post.get_active_jobs(bob)))

	def test_get_completed_jobs(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")
		joseph = Contractor.objects.get(name="Joseph")

		completed1 = new_job_posting(
			contractor=emily,
			completed=True,
			completion_date=now()
		)
		completed2 = new_job_posting(
			contractor=joseph,
			completed=True,
			completion_date=now()
		)

		posting = new_job_posting()

		active = new_job_posting(contractor=emily)

		post.create_job_posting(bob, completed1)
		post.create_job_posting(bob, completed2)
		post.create_job_posting(bob, posting)
		post.create_job_posting(bob, active)

		self.assertEquals(
			{completed1, completed2},
			set(post.get_completed_jobs(bob))
		)

	def test_get_completed_jobs_no_jobs(self):
		bob = JobPoster.objects.get(name="Bob")
		self.assertEquals(0, len(post.get_completed_jobs(bob)))

	def test_delete_job_posting(self):
		bob = JobPoster.objects.get(name="Bob")
		posting = new_job_posting()
		post.create_job_posting(bob, posting)

		post.delete_job_posting(bob, posting.pk)

		self.assertEquals(0, len(bob.jobposting_set.all()))

	def test_delete_job_posting_wrong_owner(self):
		bob = JobPoster.objects.get(name="Bob")
		frank = JobPoster.objects.get(name="Frank")
		posting = new_job_posting()
		post.create_job_posting(bob, posting)

		with self.assertRaises(PermissionDenied):
			post.delete_job_posting(frank, posting.pk)

	def test_delete_job_posting_does_not_exist(self):
		bob = JobPoster.objects.get(name="Bob")

		with self.assertRaises(Http404):
			post.delete_job_posting(bob, 0)

	def test_update_job_posting(self):
		bob = JobPoster.objects.get(name="Bob")
		posting = new_job_posting(description="old description")
		post.create_job_posting(bob, posting)

		posting.description = "new_description"
		post.update_job_posting(bob, posting)

		self.assertEquals(
			"new_description",
			bob.jobposting_set.get().description
		)

	def test_update_job_posting_wrong_owner(self):
		bob = JobPoster.objects.get(name="Bob")
		frank = JobPoster.objects.get(name="Frank")
		posting = new_job_posting(description="old_description")
		post.create_job_posting(bob, posting)

		posting.description = "new_description"
		with self.assertRaises(PermissionDenied):
			post.update_job_posting(frank, posting)

		self.assertEquals(
			"old_description",
			bob.jobposting_set.get().description
		)

	def test_get_bids(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")
		joseph = Contractor.objects.get(name="Joseph")

		posting1 = new_job_posting()
		posting2 = new_job_posting()
		post.create_job_posting(bob, posting1)
		post.create_job_posting(bob, posting2)

		bid1 = Bid(contractor=emily, job=posting1)
		bid2 = Bid(contractor=joseph, job=posting1)
		bid3 = Bid(contractor=emily, job=posting2)

		bid1.save()
		bid2.save()
		bid3.save()

		self.assertEquals({bid1, bid2}, set(post.get_bids(bob, posting1.pk)))

	def test_get_bids_wrong_owner(self):
		bob = JobPoster.objects.get(name="Bob")
		frank = JobPoster.objects.get(name="Frank")
		emily = Contractor.objects.get(name="Emily")
		joseph = Contractor.objects.get(name="Joseph")

		posting = new_job_posting()
		post.create_job_posting(bob, posting)

		bid1 = Bid(contractor=emily, job=posting)
		bid2 = Bid(contractor=joseph, job=posting)
		bid1.save()
		bid2.save()

		with self.assertRaises(PermissionDenied):
			post.get_bids(frank, posting.pk)

	def test_get_bids_no_bids(self):
		bob = JobPoster.objects.get(name="Bob")

		posting = new_job_posting()
		post.create_job_posting(bob, posting)

		self.assertEquals(0, len(post.get_bids(bob, posting.pk)))

	def test_get_bids_does_not_exist(self):
		bob = JobPoster.objects.get(name="Bob")

		with self.assertRaises(Http404):
			post.get_bids(bob, 0)

	def test_accept_bid(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")
		joseph = Contractor.objects.get(name="Joseph")

		posting = new_job_posting()
		post.create_job_posting(bob, posting)

		bid1 = Bid(contractor=emily, job=posting)
		bid2 = Bid(contractor=joseph, job=posting)
		bid1.save()
		bid2.save()

		post.accept_bid(bob, bid1.pk)

		self.assertEquals(bob.jobposting_set.get().contractor, emily)

	def test_accept_bid_wrong_owner(self):
		bob = JobPoster.objects.get(name="Bob")
		frank = JobPoster.objects.get(name="Frank")
		emily = Contractor.objects.get(name="Emily")

		posting = new_job_posting()
		post.create_job_posting(bob, posting)

		bid = Bid(contractor=emily, job=posting)
		bid.save()

		with self.assertRaises(PermissionDenied):
			post.accept_bid(frank, bid.pk)

		self.assertEquals(bob.jobposting_set.get().contractor, None)

	def test_accept_bid_does_not_exist(self):
		bob = JobPoster.objects.get(name="Bob")

		with self.assertRaises(Http404):
			post.accept_bid(bob, 0)

	def test_rate_contractor(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		posting = new_job_posting(
			contractor=emily,
			completed=True,
			completion_date=now()
		)
		post.create_job_posting(bob, posting)

		post.rate_contractor(bob, emily, 5)
		self.assertEquals(5, emily.contractorrating_set.get(poster=bob).rating)

	def test_rate_contractor_job_not_completed(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		posting = new_job_posting(contractor=emily)
		post.create_job_posting(bob, posting)

		with self.assertRaises(PermissionDenied):
			post.rate_contractor(bob, emily, 5)

		self.assertEquals(0, len(emily.contractorrating_set.all()))

	def test_rate_contractor_no_jobs(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		with self.assertRaises(PermissionDenied):
			post.rate_contractor(bob, emily, 5)

		self.assertEquals(0, len(emily.contractorrating_set.all()))


class JobSeekingAPITest(TestCase):
	def setUp(self):
		JobPoster(name="Bob", openid="0", region="Calgary").save()
		JobPoster(name="Frank", openid="1", region="Edmonton").save()
		Contractor(name="Emily", registration_id="0", email="emily95@gmail.com").save()
		Contractor(name="Joseph", registration_id="1", email="joseph86@gmail.com").save()

	def test_update_contractor(self):
		emily = Contractor.objects.get(name="Emily")
		email = "emily@contractors.org"
		emily.email = email
		seek.update_contractor(emily)

		emily = Contractor.objects.get(name="Emily")
		self.assertEquals(email, emily.email)

	def test_update_contractor_field_too_long(self):
		with self.assertRaises(SuspiciousOperation):
			emily = Contractor.objects.get(name="Emily")
			email = "toolong" * 50 + "@contractors.org"
			emily.email = email
			seek.update_contractor(emily)

	def test_get_rating(self):
		emily = Contractor.objects.get(name="Emily")
		bob = JobPoster.objects.get(name="Bob")
		frank = JobPoster.objects.get(name="Frank")

		ContractorRating(contractor=emily, poster=bob, rating=5).save()
		ContractorRating(contractor=emily, poster=frank, rating=4).save()
	
		self.assertAlmostEquals(4.5, seek.get_rating(emily))

	def test_get_rating_no_ratings(self):
		emily = Contractor.objects.get(name="Emily")
		self.assertEquals(None, seek.get_rating(emily))

	def test_get_jobs(self):
		bob = JobPoster.objects.get(name="Bob")
		joseph = Contractor.objects.get(name="Joseph")

		posting1 = new_job_posting(poster=bob)
		posting1.save()

		posting2 = new_job_posting(poster=bob)
		posting2.save()

		posting3 = new_job_posting(
			poster=bob,
			creation_date=now() - timedelta(days=4),
			bidding_deadline=now() - timedelta(days=1)
		)
		posting3.save()

		posting4 = new_job_posting(
			poster=bob,
			contractor=joseph
		)
		posting4.save()

		emily = Contractor.objects.get(name="Emily")
		self.assertEquals({posting1, posting2}, set(seek.get_jobs(emily)))

	def test_get_jobs_with_skill(self):
		bob = JobPoster.objects.get(name="Bob")
		joseph = Contractor.objects.get(name="Joseph")

		posting1 = new_job_posting(poster=bob)
		posting1.save()
		posting1.jobskill_set.add(JobSkill(skill="python"))
		posting1.jobskill_set.add(JobSkill(skill="django"))

		posting2 = new_job_posting(poster=bob)
		posting2.save()
		posting2.jobskill_set.add(JobSkill(skill="python"))

		posting3 = new_job_posting(poster=bob)
		posting3.save()
		posting3.jobskill_set.add(JobSkill(skill="django"))

		posting4 = new_job_posting(poster=bob)
		posting4.save()
		posting4.jobskill_set.add(JobSkill(skill="ruby"))

		posting5 = new_job_posting(
			poster=bob,
			creation_date=now() - timedelta(days=4),
			bidding_deadline=now() - timedelta(days=1)
		)
		posting5.save()
		posting5.jobskill_set.add(JobSkill(skill="python"))

		posting6 = new_job_posting(
			poster=bob,
			contractor=joseph
		)
		posting6.save()
		posting6.jobskill_set.add(JobSkill(skill="django"))

		emily = Contractor.objects.get(name="Emily")
		self.assertEquals(
			{posting1, posting2, posting3},
			set(seek.get_jobs(emily, ["python", "django"]))
		)


	def test_get_jobs_with_region(self):
		bob = JobPoster.objects.get(name="Bob")
		frank = JobPoster.objects.get(name="Frank")
		joseph = Contractor.objects.get(name="Joseph")

		posting1 = new_job_posting(poster=bob)
		posting1.save()

		posting2 = new_job_posting(poster=frank)
		posting2.save()

		posting3 = new_job_posting(
			poster=bob,
			creation_date=now() - timedelta(days=4),
			bidding_deadline=now() - timedelta(days=1)
		)
		posting3.save()

		posting4 = new_job_posting(
			poster=bob,
			contractor=joseph
		)
		posting4.save()

		emily = Contractor.objects.get(name="Emily")
		self.assertEquals(
			{posting1},
			set(seek.get_jobs(emily, region=bob.region))
		)

	def test_get_jobs_with_skill_and_region(self):
		bob = JobPoster.objects.get(name="Bob")
		frank = JobPoster.objects.get(name="Frank")
		joseph = Contractor.objects.get(name="Joseph")

		posting1 = new_job_posting(poster=bob)
		posting1.save()
		posting1.jobskill_set.add(JobSkill(skill="python"))
		posting1.jobskill_set.add(JobSkill(skill="django"))

		posting2 = new_job_posting(poster=bob)
		posting2.save()
		posting2.jobskill_set.add(JobSkill(skill="python"))

		posting3 = new_job_posting(poster=frank)
		posting3.save()
		posting3.jobskill_set.add(JobSkill(skill="django"))

		posting4 = new_job_posting(poster=bob)
		posting4.save()
		posting4.jobskill_set.add(JobSkill(skill="ruby"))

		posting5 = new_job_posting(
			poster=bob,
			creation_date=now() - timedelta(days=4),
			bidding_deadline=now() - timedelta(days=1)
		)
		posting5.save()
		posting5.jobskill_set.add(JobSkill(skill="python"))

		posting6 = new_job_posting(
			poster=bob,
			contractor=joseph
		)
		posting6.save()
		posting6.jobskill_set.add(JobSkill(skill="django"))

		emily = Contractor.objects.get(name="Emily")
		self.assertEquals(
			{posting1, posting2},
			set(seek.get_jobs(emily, ["python", "django"], "Calgary"))
		)

	def test_get_job_poster(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		self.assertEquals(bob, seek.get_job_poster(emily, bob.id))

	def test_get_job_poster_does_not_exist(self):
		emily = Contractor.objects.get(name="Emily")
		with self.assertRaises(Http404):
			seek.get_job_poster(emily, 0)

	def test_get_job_poster_rating(self):
		emily = Contractor.objects.get(name="Emily")
		joseph = Contractor.objects.get(name="Joseph")
		bob = JobPoster.objects.get(name="Bob")

		JobPosterRating(contractor=emily, poster=bob, rating=3).save()
		JobPosterRating(contractor=joseph, poster=bob, rating=4).save()

		self.assertAlmostEquals(3.5, seek.get_job_poster_rating(emily, bob.id))

	def test_get_job_poster_rating_no_ratings(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")
		self.assertEquals(None, seek.get_job_poster_rating(emily, bob.id))

	def test_get_job_poster_rating_does_not_exist(self):
		emily = Contractor.objects.get(name="Emily")
		with self.assertRaises(Http404):
			seek.get_job_poster_rating(emily, -1)

	def test_place_bid(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		posting = new_job_posting(poster=bob)
		posting.save()

		bid = Bid(job=posting)
		seek.place_bid(emily, bid)

		self.assertTrue(bid in posting.bid_set.all())
		self.assertEquals(emily, bid.contractor)

	def test_place_bid_no_such_job(self):
		emily = Contractor.objects.get(name="Emily")
		with self.assertRaises(Http404):
			bid = Bid(job_id=0)
			seek.place_bid(emily, bid)

	# TODO: Test bid with invalid fields, or bid that specifies
	# completion date and compensation amount when it's not supposed to.

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

		self.assertEquals(
			{current1, current2},
			set(seek.get_current_jobs(emily))
		)

	def test_get_current_jobs_no_current_jobs(self):
		emily = Contractor.objects.get(name="Emily")
		self.assertEquals(0, len(seek.get_current_jobs(emily)))

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

		self.assertEquals(
			{completed1, completed2},
			set(seek.get_completed_jobs(emily))
		)

	def test_get_completed_jobs_no_completed_jobs(self):
		emily = Contractor.objects.get(name="Emily")
		self.assertEquals(0, len(seek.get_completed_jobs(emily)))

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

		self.assertEquals(
			{prospective1, prospective2},
			set(seek.get_prospective_jobs(emily))
		)

	def test_get_prospective_jobs_no_prospective_jobs(self):
		emily = Contractor.objects.get(name="Emily")
		self.assertEquals(0, len(seek.get_prospective_jobs(emily)))

	def test_rate_job_poster(self):
		bob = JobPoster.objects.get(name="Bob")
		frank = JobPoster.objects.get(name="Frank")
		emily = Contractor.objects.get(name="Emily")

		completed1 = new_job_posting(
			poster=bob,
			contractor=emily,
			completed=True
		)
		completed2 = new_job_posting(
			poster=frank,
			contractor=emily,
			marked_completed_by_contractor=True
		)

		completed1.save()
		completed2.save()

		seek.rate_job_poster(emily, bob.id, 5)
		seek.rate_job_poster(emily, frank.id, 2)

		self.assertEquals(5, bob.jobposterrating_set.get().rating)
		self.assertEquals(2, frank.jobposterrating_set.get().rating)

	def test_rate_job_poster_job_not_complete(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		current = new_job_posting(
			poster=bob,
			contractor=emily
		)
		current.save()

		with self.assertRaises(PermissionDenied):
			seek.rate_job_poster(emily, bob.id, 5)

	def test_rate_job_poster_no_jobs(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		with self.assertRaises(PermissionDenied):
			seek.rate_job_poster(emily, bob.id, 5)

	def test_mark_complete(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		current = new_job_posting(
			poster=bob,
			contractor=emily
		)
		complete = new_job_posting(
			poster=bob,
			contractor=emily,
			completed=True
		)
		current.save()
		complete.save()

		seek.mark_complete(emily, current.id)
		seek.mark_complete(emily, complete.id)

		current = JobPosting.objects.get(pk=current.id)
		complete = JobPosting.objects.get(pk=complete.id)

		self.assertTrue(current.marked_completed_by_contractor)
		self.assertTrue(complete.marked_completed_by_contractor)
		self.assertFalse(current.completed)
		self.assertTrue(complete.completed)

	def test_mark_complete_wrong_contractor(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")
		joseph = Contractor.objects.get(name="Joseph")

		posting = new_job_posting(
			poster=bob,
			contractor=joseph
		)
		posting.save()

		with self.assertRaises(PermissionDenied):
			seek.mark_complete(emily, posting.id)

	def test_mark_complete_no_such_job(self):
		emily = Contractor.objects.get(name="Emily")
		with self.assertRaises(Http404):
			seek.mark_complete(emily, 0)


class ModelValidationTest(TestCase):
	def setUp(self):
		JobPoster(openid=0, name="Bob").save()
		Contractor(name="Emily", registration_id="0", email="emily95@gmail.com").save()

	def test_job_poster_invalid_phone_number(self):
		with self.assertRaises(ValidationError):
			JobPoster(openid=1, name="Frank", phone_number="2").save()

	def test_job_poster_invalid_email(self):
		with self.assertRaises(ValidationError):
			JobPoster(openid=1, name="Frang", email="invalid").save()

	def test_contractor_invalid_phone_number(self):
		with self.assertRaises(ValidationError):
			Contractor(
				name="Joseph",
				email="joseph86@gmail.com",
				phone_number="2"
			).save()

	def test_contractor_invalid_email(self):
		with self.assertRaises(ValidationError):
			Contractor(name="Joseph", email="invalid").save()

	def test_job_posting_bidding_confirmation_deadline_too_early(self):
		bob = JobPoster.objects.get(name="Bob")
		with self.assertRaises(ValidationError):
			new_job_posting(
				poster=bob,
				bidding_deadline=now() + timedelta(days=10),
				bidding_confirmation_deadline=now() + timedelta(days=5)
			).save()

	def test_job_posting_bidding_deadline_too_early(self):
		bob = JobPoster.objects.get(name="Bob")
		with self.assertRaises(ValidationError):
			new_job_posting(
				poster=bob,
				bidding_deadline=now() - timedelta(days=10)
			).save()

	def test_job_posting_negative_compensation_amount(self):
		bob = JobPoster.objects.get(name="Bob")
		with self.assertRaises(ValidationError):
			new_job_posting(
				poster=bob,
				compensation_amount=-1
			).save()

	def test_job_posting_no_compensation_amount(self):
		bob = JobPoster.objects.get(name="Bob")
		with self.assertRaises(ValidationError):
			new_job_posting(
				poster=bob,
				bid_includes_compensation_amount=True,
				compensation_amount=None
			).save()

	def test_job_posting_no_completion_date(self):
		bob = JobPoster.objects.get(name="Bob")
		with self.assertRaises(ValidationError):
			new_job_posting(
				poster=bob,
				bid_includes_completion_date=True,
				completion_date=None
			).save()

	def test_bid_no_compensation_amount(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")
		posting = new_job_posting(
			poster=bob,
			bid_includes_compensation_amount=True
		)
		posting.save()
		with self.assertRaises(ValidationError):
			Bid(contractor=emily, job=posting, compensation_amount=None).save()

	def test_bid_no_completion_date(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")
		posting = new_job_posting(
			poster=bob,
			bid_includes_completion_date=True
		)
		posting.save()
		with self.assertRaises(ValidationError):
			Bid(contractor=emily, job=posting, completion_date=None).save()

	def test_bid_with_compensation_amount(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")
		posting = new_job_posting(
			poster=bob,
			bid_includes_compensation_amount=False
		)
		posting.save()
		with self.assertRaises(ValidationError):
			Bid(contractor=emily, job=posting, compensation_amount=10000).save()

	def test_bid_with_completion_date(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")
		posting = new_job_posting(
			poster=bob,
			bid_includes_completion_date=False
		)
		posting.save()
		with self.assertRaises(ValidationError):
			Bid(
				contractor=emily,
				job=posting,
				completion_date=now() + timedelta(days=50)
			).save()

	def test_bid_negative_compensation_amount(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")
		posting = new_job_posting(
			poster=bob,
			bid_includes_compensation_amount=True
		)
		posting.save()
		with self.assertRaises(ValidationError):
			Bid(contractor=emily, job=posting, compensation_amount=-1).save()

	def test_job_poster_rating_too_low(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")
		new_job_posting(
			poster=bob,
			contractor=emily,
			completed=True
		).save()
		with self.assertRaises(ValidationError):
			JobPosterRating(poster=bob, contractor=emily, rating=-1).save()

	def test_job_poster_rating_too_high(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")
		new_job_posting(
			poster=bob,
			contractor=emily,
			completed=True
		).save()
		with self.assertRaises(ValidationError):
			JobPosterRating(poster=bob, contractor=emily, rating=6).save()
		
	def test_contractor_rating_too_low(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")
		new_job_posting(
			poster=bob,
			contractor=emily,
			completed=True
		).save()
		with self.assertRaises(ValidationError):
			ContractorRating(poster=bob, contractor=emily, rating=-1).save()

	def test_contractor_rating_too_high(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")
		new_job_posting(
			poster=bob,
			contractor=emily,
			completed=True
		).save()
		with self.assertRaises(ValidationError):
			ContractorRating(poster=bob, contractor=emily, rating=6).save()

	def test_change_job_posting_when_bids_exist_no_compensation_amount(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		posting = new_job_posting(
			poster=bob,
			bid_includes_compensation_amount=False
		)
		posting.save()

		Bid(job=posting, contractor=emily).save()

		posting.bid_includes_compensation_amount = True
		with self.assertRaises(ValidationError):
			posting.save()
		self.assertFalse(
			JobPosting.objects.get().bid_includes_compensation_amount
		)

	def test_change_job_posting_when_bids_exist_compensation_amount(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		posting = new_job_posting(
			poster=bob,
			bid_includes_compensation_amount=True
		)
		posting.save()

		Bid(job=posting, contractor=emily, compensation_amount=50000).save()

		posting.bid_includes_compensation_amount = False
		with self.assertRaises(ValidationError):
			posting.save()
		self.assertTrue(
			JobPosting.objects.get().bid_includes_compensation_amount
		)

	def test_change_job_posting_when_no_bids_exist_no_compensation_amount(self):
		bob = JobPoster.objects.get(name="Bob")

		posting = new_job_posting(
			poster=bob,
			bid_includes_compensation_amount=False
		)
		posting.save()

		posting.bid_includes_compensation_amount = True
		posting.save()

	def test_change_job_posting_when_no_bids_exist_compensation_amount(self):
		bob = JobPoster.objects.get(name="Bob")

		posting = new_job_posting(
			poster=bob,
			bid_includes_compensation_amount=True
		)
		posting.save()

		posting.bid_includes_compensation_amount = False
		posting.save()

	def test_change_job_posting_when_bids_exist_no_completion_date(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		posting = new_job_posting(
			poster=bob,
			bid_includes_completion_date=False
		)
		posting.save()

		Bid(job=posting, contractor=emily).save()

		posting.bid_includes_completion_date = True
		with self.assertRaises(ValidationError):
			posting.save()
		self.assertFalse(JobPosting.objects.get().bid_includes_completion_date)

	def test_change_job_posting_when_bids_exist_completion_date(self):
		bob = JobPoster.objects.get(name="Bob")
		emily = Contractor.objects.get(name="Emily")

		posting = new_job_posting(
			poster=bob,
			bid_includes_completion_date=True
		)
		posting.save()

		Bid(
			job=posting,
			contractor=emily,
			completion_date=now() + timedelta(days=50)
		).save()

		posting.bid_includes_completion_date = False
		with self.assertRaises(ValidationError):
			posting.save()
		self.assertTrue(JobPosting.objects.get().bid_includes_completion_date)

	def test_change_job_posting_when_no_bids_exist_no_completion_date(self):
		bob = JobPoster.objects.get(name="Bob")

		posting = new_job_posting(
			poster=bob,
			bid_includes_completion_date=False
		)
		posting.save()

		posting.bid_includes_completion_date = True
		posting.save()

	def test_change_job_posting_when_no_bids_exist_completion_date(self):
		bob = JobPoster.objects.get(name="Bob")

		posting = new_job_posting(
			poster=bob,
			bid_includes_completion_date=True
		)
		posting.save()

		posting.bid_includes_completion_date = False
		posting.save()

		
