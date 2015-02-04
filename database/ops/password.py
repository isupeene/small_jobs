from getpass import getpass
from config import db

def get_user_password():
	return getpass("Database Password for " + db.user)

def get_admin_password():
	return getpass("Database Password for " + db.admin)

