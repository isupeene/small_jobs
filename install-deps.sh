apt-get install postgresql
apt-get install python-psycopg2
easy_install "Django==1.6.5"
apt-get install python-pip
pip install djangorestframework
pip install phonenumbers


cd $(dirname $0)/..

if [-d python-openid]; then
	cd python-openid
	git pull
else
	git clone git@github.com:openid/python-openid.git
	cd python-openid
fi

python setup.py install
cd ..

# We need the latest version of python-gcm, which has
# dry_run support for tests.  Unfortunately, the
# version fetched by pip doesn't have this.

if [-d python-gcm]; then
	cd python-gcm
	git pull
else
	git clone git@github.com:geeknam/python-gcm.git
	cd python-gcm
fi

python setup.py install

