from small_jobs_api.views import begin_openid_login, finish_openid_login

def require_login(handler):
	def _protected_handler(request):
		if "authenticated_user" in request.session:
			del request.session["authenticated_user"]
			return handler(request)
		elif "openid_login" in request.session:
			return finish_openid_login(request)
		else:
			return begin_openid_login(request)

	return _protected_handler

