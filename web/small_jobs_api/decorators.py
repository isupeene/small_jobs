from small_jobs_api.views import begin_openid_login, finish_openid_login

def require_login(handler):
	"""
	Protectes the function with openid authentication.
	A function with this decorator may assume that
	the user is logged in, and that the authenticated
	JobPoster is in request.session['authenticated_user']
	"""
	def _protected_handler(request):
		if "authenticated_user" in request.session:
			return handler(request)
		elif "openid_login" in request.session:
			return finish_openid_login(request)
		else:
			return begin_openid_login(request)

	return _protected_handler

