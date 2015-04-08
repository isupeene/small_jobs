# Requirement 3.2.2

from django.db.models import (
	Model,
	TextField as _TextField, DateTimeField, DecimalField,
	BooleanField, IntegerField, CharField,
	ForeignKey
)
from django.core.validators import (
	validate_email,
	MinValueValidator,
	MaxValueValidator
)
from django.core.exceptions import ValidationError
from django.utils.timezone import now

import phonenumbers
from phonenumbers import NumberParseException, is_possible_number


class MoneyField(DecimalField):
	def __init__(self, *args, **kwargs):
		super(MoneyField, self).__init__(
			*args, max_digits=20, decimal_places=2, **kwargs
		)

# We set the default value to None for ShortCharField and TextField
# so that the database will give us an error if the field is left out.
# Otherwise, the default value is an empty string, and the error can
# only be caught in Django forms.

class ShortCharField(CharField):
	def __init__(self, *args, **kwargs):
		super(ShortCharField, self).__init__(
			*args, max_length=100, default=None, **kwargs
		)

class TextField(_TextField):
	def __init__(self, *args, **kwargs):
		super(TextField, self).__init__(
			*args, default=None, **kwargs
		)


def nullable(field_class):
	class _Nullable(field_class):
		def __init__(self, *args, **kwargs):
			super(_Nullable, self).__init__(
				*args, blank=True, null=True, **kwargs
			)

	return _Nullable

NullableMoneyField = nullable(MoneyField)
NullableTextField = nullable(TextField)
NullableShortCharField = nullable(ShortCharField)
NullableDateTimeField = nullable(DateTimeField)
NullableForeignKey = nullable(ForeignKey)


# YOLO: We are assuming canadian phone numbers for this validation.
# In the future, a way to use the user's actual location to determine
# the region would be ideal.
def validate_phone_number(text):
	try:
		number = phonenumbers.parse(text, "CA")
		if not is_possible_number(number):
			raise ValidationError
	except NumberParseException:
		raise ValidationError(NumberParseException)

class JobPoster(Model):
	openid = ShortCharField(unique=True)
	name = ShortCharField()
	description = NullableTextField()
	email = NullableShortCharField(validators=[validate_email])
	phone_number = NullableShortCharField(validators=[validate_phone_number])
	region = NullableShortCharField()

	def save(self):
		self.clean_fields()
		self.clean()
		super(JobPoster, self).save()

	def __unicode__(self):
		return self.name

class Contractor(Model):
	name = ShortCharField()
	description = NullableTextField()
	email = ShortCharField(unique=True, validators=[validate_email])
	phone_number = NullableShortCharField(validators=[validate_phone_number])
	registration_id = CharField(max_length=500, default=None)

	def save(self):
		self.clean_fields()
		self.clean()
		super(Contractor, self).save()

	def is_authenticated(self):
		return True

	def __unicode__(self):
		return self.name

class JobPosting(Model):
	poster = ForeignKey(JobPoster)
	contractor = NullableForeignKey(Contractor)
	creation_date = DateTimeField(default=now)
	short_description = ShortCharField()
	description = TextField(blank=False)
	bidding_deadline = DateTimeField()
	bidding_confirmation_deadline = DateTimeField()
	compensation_amount = NullableMoneyField(validators=[MinValueValidator(0)])
	completion_date = NullableDateTimeField()
	bid_includes_compensation_amount = BooleanField()
	bid_includes_completion_date = BooleanField()
	completed = BooleanField(default=False)
	marked_completed_by_contractor = BooleanField(default=False)
	date_completed = NullableDateTimeField()

	def save(self):
		self.clean_fields()
		self.clean()
		super(JobPosting, self).save()

	def __init__(self, *args, **kwargs):
		super(JobPosting, self).__init__(*args, **kwargs)
		self.bid_included_compensation_amount = \
			self.bid_includes_compensation_amount
		self.bid_included_completion_date = \
			self.bid_includes_completion_date

	def clean(self):
		if self.bidding_confirmation_deadline < self.bidding_deadline:
			raise ValidationError(
				"Bidding confirmation deadline must be after bidding deadline."
			)

		if self.bidding_deadline < self.creation_date:
			raise ValidationError(
				"Bidding deadline must be after creation date."
			)

		if not self.bid_includes_compensation_amount \
		   and self.compensation_amount is None:
			raise ValidationError(
				"Jobs for which the bid cannot include a compensation " +
				"amount must include one in the job posting."
			)

		if not self.bid_includes_completion_date \
		   and self.completion_date is None:
			raise ValidationError(
				"Jobs for which the bid cannot include a completion date " +
				"must include one in the job posting."
			)

		if any(self.bid_set.all()) and \
		 (self.bid_included_compensation_amount !=
		   self.bid_includes_compensation_amount or
		  self.bid_included_completion_date !=
		   self.bid_includes_completion_date):
			raise ValidationError(
				"Cannot modify bid_includes_compensation_amount or " +
				"bid_includes_completion_date when bids have already been added."
			)


	def __unicode__(self):
		return self.short_description

class Bid(Model):
	job = ForeignKey(JobPosting)
	contractor = ForeignKey(Contractor)
	message = NullableTextField()
	compensation_amount = NullableMoneyField(validators=[MinValueValidator(0)])
	completion_date = NullableDateTimeField()

	def save(self):
		self.clean_fields()
		self.clean()
		super(Bid, self).save()

	# TODO: Consider the following -
	# If a posting specifies a compensation amount, the
	# bid_includes_compensation_amount field will *allow* the bid to include
	# a compensation amount, otherwise, it will *require* it.  For now,
	# it is always required if that field is true.
	def clean(self):
		if self.job.bid_includes_compensation_amount \
		   and self.compensation_amount is None:
			raise ValidationError(
				"Bids on this job must include a compensation amount."
			)

		if self.job.bid_includes_completion_date \
		   and self.completion_date is None:
			raise ValidationError(
				"Bids on this job must include a completion date."
			)

		if not self.job.bid_includes_compensation_amount \
		   and self.compensation_amount is not None:
			raise ValidationError(
				"Bids on this job cannot include a compensation amount."
			)

		if not self.job.bid_includes_completion_date \
		   and self.completion_date is not None:
			raise ValidationError(
				"Bids on this job cannot include a completion date."
			)

	class Meta:
		unique_together = (('job', 'contractor'),)

	def __unicode__(self):
		return "{} | {}".format(self.job.short_description, self.contractor.name)

class JobSkill(Model):
	job = ForeignKey(JobPosting)
	skill = ShortCharField(db_index=True)

	def save(self):
		self.clean_fields()
		self.clean()
		super(JobSkill, self).save()

	class Meta:
		unique_together = (('job', 'skill'),)

	def __unicode__(self):
		return self.skill

class ContractorSkill(Model):
	contractor = ForeignKey(Contractor)
	skill = ShortCharField(db_index=True)

	def save(self):
		self.clean_fields()
		self.clean()
		super(ContractorSkill, self).save()

	class Meta:
		unique_together = (('contractor', 'skill'),)

	def __unicode__(self):
		return self.skill

class JobPosterRating(Model):
	poster = ForeignKey(JobPoster)
	contractor = ForeignKey(Contractor)
	rating = IntegerField(
		validators=[MinValueValidator(0), MaxValueValidator(5)]
	)
	
	def save(self):
		self.clean_fields()
		self.clean()
		super(JobPosterRating, self).save()

	class Meta:
		unique_together = (('poster', 'contractor'),)

	def __unicode__(self):
		return "{} -> {} | {}".format(
			self.contractor.name, self.poster.name, self.rating
		)

class ContractorRating(Model):
	contractor = ForeignKey(Contractor)
	poster = ForeignKey(JobPoster)
	rating = IntegerField(
		validators=[MinValueValidator(0), MaxValueValidator(5)]
	)

	def save(self):
		self.clean_fields()
		self.clean()
		super(ContractorRating, self).save()

	class Meta:
		unique_together = (('contractor', 'poster'),)

	def __unicode__(self):
		return "{} -> {} | {}".format(
			self.poster.name, self.contractor.name, self.rating
		)

