from django import forms
from small_jobs_api.models import (
    JobPosting, JobPoster
)
from django.forms.extras.widgets import SelectDateWidget
from django.forms.widgets import *

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

class JobPostingForm(forms.ModelForm):
    short_description = forms.CharField(max_length=128, help_text="Short Description")
    bidding_deadline = forms.DateField(widget=SelectDateWidget,help_text="Bidding Deadline")
    bidding_confirmation_deadline = forms.DateField(widget=SelectDateWidget,help_text="Bidding Cofirmation Deadline")
    compensation_amount = forms.IntegerField(help_text="Compensation Amount")
    description = forms.CharField(widget=forms.Textarea, help_text="Description (Input Box Is Resizable)")
    
    class Meta:
        # Provide an association between the ModelForm and a model
        model = JobPosting

        # What fields do we want to include in our form?
        # This way we don't need every field in the model present.
        # Some fields may allow NULL values, so we may not want to include them...
        # Here, we are hiding the foreign key.
        fields = ('short_description', 'bidding_deadline','bidding_confirmation_deadline' , 'compensation_amount' , 'description')