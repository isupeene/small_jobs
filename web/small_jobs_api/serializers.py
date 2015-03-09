from rest_framework.serializers import ModelSerializer

from small_jobs_api.models import Contractor


class Serializer(ModelSerializer):
	def get_instance(self):
		return self.Meta.model(**self.validated_data)

class ContractorSerializer(Serializer):
	class Meta:
		model = Contractor
		fields = ('id', 'email', 'name', 'phone_number')

class NewContractorSerializer(Serializer):
	class Meta:
		model = Contractor
		fields = ('email', 'name', 'phone_number')

