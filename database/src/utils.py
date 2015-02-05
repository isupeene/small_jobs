from contextlib import contextmanager

@contextmanager
def ignore_errors():
	try:
		yield
	except:
		pass

