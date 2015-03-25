from openid.store.sqlstore import PostgreSQLStore
from openid.consumer.consumer import Consumer

from psycopg2 import ProgrammingError

from django.db import connection

from small_jobs_api.models import JobPoster

# Note that python-openid doesn't lend itself to idiomatic django code.
# Here, we're creating database tables programmatically instead
# of using model classes.
# This code gets called a lot, and creates a connection to the
# database every time, so it might end up being a performance bottleneck.

def get_sql_store():
	tablenames = {
		'associations_table': 'openid_associations',
		'nonces_table': 'openid_nonces'
	}

	# We need to make sure the database connection has been created.
	# Otherwise, we'll get an exception creating the store.
	connection.cursor()

	store = PostgreSQLStore(connection.connection, **tablenames)

	# python-openid does not check if the tables have already been
	# created.
	try:
		store.createTables()
	except ProgrammingError:
		pass

	return store

def get_consumer(request):
	return Consumer(request.session, get_sql_store())

def get_job_poster(auth_response, sreg_response):
	try:
		return JobPoster.objects.get(openid=auth_response.identity_url)
	except:
		poster = JobPoster(
			openid=auth_response.identity_url,
			name=sreg_response['fullname'],
			email=sreg_response['email']
		)
		poster.save()
		return poster	

def get_base_url(req):
	hostname = req.META["HTTP_HOST"]
	protocol = "https" if "HTTPS" in req.META["HTTP_HOST"] else "http"
	return "{}://{}".format(protocol, hostname)

