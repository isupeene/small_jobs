from django.db.models import (
	Model,
	TextField, DateTimeField, DecimalField,
	BooleanField, IntegerField, CharField,
	ForeignKey
)
from django.utils.timezone import now


class MoneyField(DecimalField):
	def __init__(self, *args, **kwargs):
		super(MoneyField, self).__init__(
			*args, max_digits=20, decimal_places=2, **kwargs
		)

class ShortCharField(CharField):
	def __init__(self, *args, **kwargs):
		super(ShortCharField, self).__init__(
			*args, max_length=100, **kwargs
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


class JobPoster(Model):
	name = ShortCharField()
	description = NullableTextField()
	email = NullableShortCharField()
	phone_number = NullableShortCharField()

	def __unicode__(self):
		return self.name

class Contractor(Model):
	name = ShortCharField()
	description = NullableTextField()
	email = NullableShortCharField()
	phone_number = NullableShortCharField()

	def __unicode__(self):
		return self.name

class JobPosting(Model):
	poster = ForeignKey(JobPoster)
	contractor = NullableForeignKey(Contractor)
	creation_date = DateTimeField(default=now)
	short_description = ShortCharField()
	description = TextField()
	bidding_deadline = DateTimeField()
	bidding_confirmation_deadline = DateTimeField()
	compensation_amount = NullableMoneyField()
	completion_date = NullableDateTimeField()
	bid_includes_compensation_amount = BooleanField()
	bid_includes_completion_date = BooleanField()
	completed = BooleanField(default=False)
	marked_completed_by_contractor = BooleanField(default=False)
	date_completed = NullableDateTimeField()

	def __unicode__(self):
		return self.short_description

class Bid(Model):
	job = ForeignKey(JobPosting)
	contractor = ForeignKey(Contractor)
	message = NullableTextField()
	compensation_amount = NullableMoneyField()
	completion_date = NullableDateTimeField()

	class Meta:
		unique_together = (('job', 'contractor'),)

	def __unicode__(self):
		return "{} | {}".format(self.job.short_description, self.contractor.name)

class JobSkill(Model):
	job = ForeignKey(JobPosting)
	skill = ShortCharField(db_index=True)

	class Meta:
		unique_together = (('job', 'skill'),)

	def __unicode__(self):
		return self.skill

class ContractorSkill(Model):
	contractor = ForeignKey(Contractor)
	skill = ShortCharField(db_index=True)

	class Meta:
		unique_together = (('contractor', 'skill'),)

	def __unicode__(self):
		return self.skill

class JobPosterRating(Model):
	poster = ForeignKey(JobPoster)
	contractor = ForeignKey(Contractor)
	rating = IntegerField()
	
	class Meta:
		unique_together = (('poster', 'contractor'),)

	def __unicode__(self):
		return "{} -> {} | {}".format(
			self.contractor.name, self.poster.name, self.rating
		)

class ContractorRating(Model):
	contractor = ForeignKey(Contractor)
	poster = ForeignKey(JobPoster)
	rating = IntegerField()

	class Meta:
		unique_together = (('contractor', 'poster'),)

	def __unicode__(self):
		return "{} -> {} | {}".format(
			self.poster.name, self.contractor.name, self.rating
		)

