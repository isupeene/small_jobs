from django import forms
from small_jobs_api.models import (
    JobPosting, JobPoster , JobSkill
)
from django.utils.timezone import now
from django.forms.extras.widgets import SelectDateWidget
from django.forms import fields, models, formsets
from django.forms.widgets import *

# Requirment 3.2.1.1.2
class JobPosterForm(forms.ModelForm):
    name = forms.CharField(max_length=128, help_text="Name")
    description = forms.CharField(max_length=128, help_text="Description")
    email = forms.EmailField(widget=EmailInput, max_length=128, help_text="eMail")
    phone_number = forms.CharField(max_length=128, help_text="Phone Number")
    region = forms.CharField(max_length=128, help_text="Region")

    # An inline class to provide additional information on the form.
    class Meta:
        # Provide an association between the ModelForm and a model
        model = JobPoster
        fields = ('name', 'description', 'email', 'phone_number' , 'region')

# Requirement 3.2.1.2
class JobPostingForm(forms.ModelForm):
    short_description = forms.CharField(max_length=128)
    bidding_deadline = forms.DateField(widget=SelectDateWidget,initial=now())
    bidding_confirmation_deadline = forms.DateField(widget=SelectDateWidget,initial=now())
    bid_includes_completion_date = forms.BooleanField(required=False)
    completion_date = forms.DateField(widget=SelectDateWidget,required=False,initial=now())
    bid_includes_compensation_amount = forms.BooleanField(required=False)
    compensation_amount = forms.DecimalField(required=False)
    description = forms.CharField(widget=forms.Textarea)
    
    class Meta:
        # Provide an association between the ModelForm and a model
        model = JobPosting

        # What fields do we want to include in our form?
        # This way we don't need every field in the model present.
        # Some fields may allow NULL values, so we may not want to include them...
        # Here, we are hiding the foreign key.
        fields = ('short_description', 'bidding_deadline','bidding_confirmation_deadline' ,
        'bid_includes_completion_date' , 'completion_date', 'bid_includes_compensation_amount',
         'compensation_amount' , 'description')

# Requirement 3.2.2.1.4
class JobSkillForm(forms.ModelForm):
    skill = forms.CharField(max_length=128, help_text="Skill",required=False)

    class Meta:
        model = JobSkill

        fields = ('skill',)

def get_jobskill_formset(formset=models.BaseInlineFormSet, **kwargs):
    return models.inlineformset_factory(JobPosting, JobSkill, JobSkillForm , formset, **kwargs)