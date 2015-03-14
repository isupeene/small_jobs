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

