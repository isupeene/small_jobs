from django.db.models import (
	Model,
	TextField, DateTimeField, DecimalField, BooleanField, IntegerField,
	ForeignKey
)
from django.utils.timezone import now


class MoneyField(DecimalField):
	def __init__(self, *args, **kwargs):
		super(MoneyField, self).__init__(
			*args, max_digits=20, decimal_places=2, **kwargs
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
NullableDateTimeField = nullable(DateTimeField)
NullableForeignKey = nullable(ForeignKey)


class JobPoster(Model):
	name = TextField()
	description = NullableTextField()
	email = NullableTextField()
	phone_number = NullableTextField()

class Contractor(Model):
	name = TextField()
	description = NullableTextField()
	email = NullableTextField()
	phone_number = NullableTextField()

class JobPosting(Model):
	poster = ForeignKey(JobPoster)
	contractor = NullableForeignKey(Contractor)
	creation_date = DateTimeField(default=now)
	short_description = TextField()
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

class Bid(Model):
	job = ForeignKey(JobPosting)
	contractor = ForeignKey(Contractor)
	message = NullableTextField()
	compensation_amount = NullableMoneyField()
	completion_date = NullableDateTimeField()

	class Meta:
		unique_together = (('job', 'contractor'),)

class JobSkill(Model):
	job = ForeignKey(JobPosting)
	skill = TextField(db_index=True)

	class Meta:
		unique_together = (('job', 'skill'),)

class ContractorSkill(Model):
	contractor = ForeignKey(Contractor)
	skill = TextField(db_index=True)

	class Meta:
		unique_together = (('contractor', 'skill'),)

class JobPosterRating(Model):
	poster = ForeignKey(JobPoster)
	contractor = ForeignKey(Contractor)
	rating = IntegerField()
	
	class Meta:
		unique_together = (('poster', 'contractor'),)

class ContractorRating(Model):
	contractor = ForeignKey(Contractor)
	poster = ForeignKey(JobPoster)
	rating = IntegerField()

	class Meta:
		unique_together = (('contractor', 'poster'),)

