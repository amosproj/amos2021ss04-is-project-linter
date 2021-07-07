from enum import auto
from sqlalchemy import create_engine
from sqlalchemy import MetaData, Table
import requests
import json


db_name = input('db_name: ')
db_user = input('db_user: ')
db_pass = input('db_pass: ')

db = create_engine(f'postgresql://{db_user}:{db_pass}@localhost/{db_name}')
meta = MetaData(db)

projects = Table('project', meta, autoload=True)
linting_results = Table('linting_result', meta, autoload=True)
check_results = Table('check_result', meta, autoload=True)


def get_projects():
    with open('../config.json', 'r') as f:
        config = json.load(f)
        gitlab_host = config['settings']['gitLabHost']

    res = requests.get(f'{gitlab_host}/api/v4/projects?per_page=100')
    if res.status_code == 200:
        return res.json()
    else:
        return []


