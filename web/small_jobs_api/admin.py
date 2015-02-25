from django.contrib import admin
from nested_inlines.admin import NestedModelAdmin, NestedStackedInline
from small_jobs_api.models import (
	JobPoster, Contractor, JobPosting, Bid,
	JobSkill, ContractorSkill, JobPosterRating, ContractorRating
)
from copy import deepcopy


# Job Skills
# Define the inline forms for Job Skills.

class SkillInline(NestedStackedInline):
	verbose_name = "Skill"
	extra = 0

class JobSkillInline(SkillInline):
	verbose_name_plural = "Required Skills"
	model = JobSkill

class ContractorSkillInline(SkillInline):
	verbose_name_plural = "Skills"
	model = ContractorSkill


# Bids
# Define the inline forms for Bids.
# The forms are specialized depending on whether the are shown
# in the context of a Job Posting or a Contractor.

bid_inline_fieldsets = [
	("Details", {
		'fields' : [
			'message',
			'compensation_amount',
			'completion_date'
		],
		'classes' : [
			'collapse'
		]
	})
]

job_posting_bid_inline_fieldsets = deepcopy(bid_inline_fieldsets)
job_posting_bid_inline_fieldsets[0][1]['fields'].insert(0, 'contractor')

contractor_bid_inline_fieldsets = deepcopy(bid_inline_fieldsets)
contractor_bid_inline_fieldsets[0][1]['fields'].insert(0, 'job')

class BidInline(NestedStackedInline):
	model = Bid
	extra = 0

class JobPostingBidInline(BidInline):
	fieldsets = job_posting_bid_inline_fieldsets

class ContractorBidInline(BidInline):
	fieldsets = contractor_bid_inline_fieldsets


# Job Postings
# Define the inline forms for Job Postings.
# The forms are specialized depending on whether they are shown
# in the context of a Job Poster or a Contractor who has accepted the job.

job_posting_inline_fieldsets = [
	("Summary", {
		'fields' : [
			'short_description',
			'creation_date',
			'description'
		],
		'classes' : [
			'collapse'
		]
	}),
	("Bidding", {
		'fields' : [
			'bidding_deadline',
			'bidding_confirmation_deadline',
			'compensation_amount',
			'completion_date',
			'bid_includes_compensation_amount',
			'bid_includes_completion_date'
		],
		'classes' : [
			'collapse'
		]
	}),
	("Contractor", {
		'fields' : [
			'completed',
			'marked_completed_by_contractor',
			'date_completed'
		],
		'classes' : [
			'collapse'
		]
	})
]

job_poster_job_posting_inline_fieldsets = deepcopy(job_posting_inline_fieldsets)
job_poster_job_posting_inline_fieldsets[2][1]['fields'].insert(0, 'contractor')

contractor_job_posting_inline_fieldsets = deepcopy(job_posting_inline_fieldsets)
contractor_job_posting_inline_fieldsets[0][1]['fields'].insert(1, 'poster')

class JobPostingInline(NestedStackedInline):
	model = JobPosting
	extra = 0
	readonly_fields = ('creation_date',)

class JobPosterJobPostingInline(JobPostingInline):
	fieldsets = job_poster_job_posting_inline_fieldsets
	inlines = [JobSkillInline, JobPostingBidInline]

class ContractorJobPostingInline(JobPostingInline):
	fieldsets = contractor_job_posting_inline_fieldsets
	inlines = [JobSkillInline]
	verbose_name = "Accepted Job"
	verbose_name_plural = "Jobs Accepted"


# Job Poster Ratings
# Define the inline forms for ratings given to Job Posters by Contractors.
# The forms are specialized depending on whether they are shown in the
# context of the Job Poster being rated, or the Contractor
# who is giving the rating.

job_poster_rating_inline_fieldsets = [
	("Details", {
		'fields' : ['rating'],
		'classes' : ['collapse']
	})
]

job_poster_job_poster_rating_inline_fieldsets = \
	deepcopy(job_poster_rating_inline_fieldsets)
job_poster_job_poster_rating_inline_fieldsets[0][1]['fields'] \
	.insert(0, 'contractor')

contractor_job_poster_rating_inline_fieldsets = \
	deepcopy(job_poster_rating_inline_fieldsets)
contractor_job_poster_rating_inline_fieldsets[0][1]['fields'] \
	.insert(0, 'poster')

class JobPosterRatingInline(NestedStackedInline):
	model = JobPosterRating
	extra = 0
	verbose_name = "Rating"

class JobPosterJobPosterRatingInline(JobPosterRatingInline):
	verbose_name_plural = "Ratings from Contractors"
	fieldsets = job_poster_job_poster_rating_inline_fieldsets

class ContractorJobPosterRatingInline(JobPosterRatingInline):
	verbose_name_plural = "Ratings given to Job Posters"
	fieldsets = contractor_job_poster_rating_inline_fieldsets


# Contractor Ratings
# Define the inline forms for ratigns given to Contractors by Job Posters.
# The forms are specialized depending on whether they are shown in the
# context of the Contractor being rated, or the Job Poster
# who is giving the rating.

contractor_rating_inline_fieldsets = [
	("Details", {
		'fields' : ['rating'],
		'classes' : ['collapse']
	})
]

contractor_contractor_rating_inline_fieldsets = \
	deepcopy(job_poster_rating_inline_fieldsets)
contractor_contractor_rating_inline_fieldsets[0][1]['fields'] \
	.insert(0, 'poster')

job_poster_contractor_rating_inline_fieldsets = \
	deepcopy(job_poster_rating_inline_fieldsets)
job_poster_contractor_rating_inline_fieldsets[0][1]['fields'] \
	.insert(0, 'contractor')

class ContractorRatingInline(NestedStackedInline):
	model = ContractorRating
	extra = 0
	verbose_name = "Rating"

class ContractorContractorRatingInline(ContractorRatingInline):
	verbose_name_plural = "Ratings from Job Posters"
	fieldsets = contractor_contractor_rating_inline_fieldsets

class JobPosterContractorRatingInline(ContractorRatingInline):
	verbose_name_plural = "Ratings given to Contractors"
	fieldsets = job_poster_contractor_rating_inline_fieldsets

# Users

user_fieldsets = [
	(None, {'fields' : ['name', 'description']}),
	("Contact info", {'fields' : ['email', 'phone_number']})
]

job_poster_fieldsets = deepcopy(user_fieldsets)
job_poster_fieldsets[0][1]['fields'].insert(1, 'openid')

contractor_fieldsets = deepcopy(user_fieldsets)

class JobPosterAdmin(NestedModelAdmin):
	fieldsets = job_poster_fieldsets
	inlines = [
		JobPosterJobPostingInline,
		JobPosterJobPosterRatingInline,
		JobPosterContractorRatingInline
	]

class ContractorAdmin(NestedModelAdmin):
	fieldsets = contractor_fieldsets
	inlines = [
		ContractorSkillInline,
		ContractorJobPostingInline,
		ContractorBidInline,
		ContractorContractorRatingInline,
		ContractorJobPosterRatingInline
	]


admin.site.register(JobPoster, JobPosterAdmin)
admin.site.register(Contractor, ContractorAdmin)

