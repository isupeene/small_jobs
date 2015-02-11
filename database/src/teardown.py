#!/usr/bin/python

from shared.src.schema import Base
from src.connect import get_admin_engine
from src.password import get_admin_password


if __name__ == "__main__":
	engine = get_admin_engine(get_admin_password())
	Base.metadata.drop_all(engine)

