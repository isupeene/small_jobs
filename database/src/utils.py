from contextlib import contextmanager

@contextmanager
def ignore_errors():
	"""
	Ignore all errors in the given block.
	Useful, for example, when you're tearing down a database, and
	you don't care if it might be corrupt / missing tables / etc.
	"""
	try:
		yield
	except:
		pass

