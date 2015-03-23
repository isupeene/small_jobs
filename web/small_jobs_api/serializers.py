from rest_framework.serializers import ModelSerializer

from small_jobs_api.models import *


class Serializer(ModelSerializer):
	def get_instance(self):
		return self.Meta.model(**self.validated_data)

def default_serializer(model_class):
	class _serializer(Serializer):
		class Meta:
			model = model_class
			fields = [field.name for field in model_class._meta.fields]

	return _serializer

# Exclude ID to ensure it's actually new.
class NewContractorSerializer(Serializer):
	class Meta:
		model = Contractor
		fields = [field.name
				  for field
				  in Contractor._meta.fields
				  if field.name != "id"]

# Exclude OpenID for the Job Poster's security.
class SecureJobPosterSerializer(Serializer):
	class Meta:
		model = JobPoster
		fields = [field.name
				  for field
				  in JobPoster._meta.fields
				  if field.name != "openid"]

