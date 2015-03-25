"""
Django settings for small_jobs project.

For more information on this file, see
https://docs.djangoproject.com/en/1.6/topics/settings/

For the full list of settings and their values, see
https://docs.djangoproject.com/en/1.6/ref/settings/
"""

# Build paths inside the project like this: os.path.join(BASE_DIR, ...)
import os
BASE_DIR = os.path.dirname(os.path.dirname(__file__))


# Quick-start development settings - unsuitable for production
# See https://docs.djangoproject.com/en/1.6/howto/deployment/checklist/

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = 's2hwq64t*2ohpm3q7#d_%f#16v*n_ld!5462y2uk_m*bz)+^47'

# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = True

TEMPLATE_DEBUG = True

ALLOWED_HOSTS = []


# Application definition

INSTALLED_APPS = (
	'nested_inlines',
	'django.contrib.admin',
	'django.contrib.auth',
	'django.contrib.contenttypes',
	'django.contrib.sessions',
	'django.contrib.messages',
	'django.contrib.staticfiles',
	'rest_framework',
	'small_jobs_api',
	'job_seeking',
	'job_posting'
)

MIDDLEWARE_CLASSES = (
	'django.contrib.sessions.middleware.SessionMiddleware',
	'django.middleware.common.CommonMiddleware',
	'django.contrib.auth.middleware.AuthenticationMiddleware',
	'django.middleware.csrf.CsrfViewMiddleware',
	'django.contrib.messages.middleware.MessageMiddleware',
	'django.middleware.clickjacking.XFrameOptionsMiddleware',
)

ROOT_URLCONF = 'small_jobs.urls'

WSGI_APPLICATION = 'small_jobs.wsgi.application'

# The default serializer can't serialize YadisServiceManager objects,
# which python-openid stores in the session.
# Note that for signed-cookie based session backends, this means that
# a leak of the SECRET_KEY allows an attacker to execute arbitrary
# code on the server.  However, we are using the database backend,
# so this isn't an issue for us.
SESSION_SERIALIZER = 'django.contrib.sessions.serializers.PickleSerializer'

# Database
# https://docs.djangoproject.com/en/1.6/ref/settings/#databases

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.postgresql_psycopg2',
        'NAME': 'small_jobs',
		'USER': 'small_jobs_user',
		'PASSWORD': 'password',
		'HOST': 'localhost',
		'PORT': 5432
    }
}

REST_FRAMEWORK = {
	'DEFAULT_PERMISSION_CLASSES': ('rest_framework.permissions.IsAuthenticated',),
	'DEFAULT_AUTHENTICATION_CLASSES': ('small_jobs_api.basic_auth.BasicAuthentication',)
}

GCM_AUTHORIZATION_KEY = "AIzaSyBf5SRRKSEyD213TTqOAgsU-KEZmfFOVLo"

# Internationalization
# https://docs.djangoproject.com/en/1.6/topics/i18n/

LANGUAGE_CODE = 'en-us'

TIME_ZONE = 'Canada/Mountain'

USE_I18N = True

USE_L10N = True

USE_TZ = True


# Static files (CSS, JavaScript, Images)
# https://docs.djangoproject.com/en/1.6/howto/static-files/

STATIC_URL = '/static/'
