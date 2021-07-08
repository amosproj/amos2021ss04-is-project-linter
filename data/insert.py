from sqlalchemy import create_engine
from sqlalchemy import MetaData, Table
from datetime import datetime, timedelta
import random
import requests
import json


###################
# database connection
###################

db_name = input('db_name: ')
db_user = input('db_user: ')
db_pass = input('db_pass: ')

db = create_engine(f'postgresql://{db_user}:{db_pass}@localhost/{db_name}')
meta = MetaData(db)

projects = Table('project', meta, autoload=True)
linting_results = Table('linting_result', meta, autoload=True)
check_results = Table('check_result', meta, autoload=True)

###################
# clear database
###################

check_results.delete().execute()
linting_results.delete().execute()
projects.delete().execute()

###################
# data generation
###################

start_time = datetime(2021, 6, 1, 15, 0, 0)
dates = [start_time + timedelta(n) for n in range(7)]


def next_result(dt: datetime):
    x = random.randint(start_time.day, dt.day + 3)
    return x > 3


def get_projects():
    with open('../config.json', 'r') as f:
        config = json.load(f)
        gitlab_host = config['settings']['gitLabHost']

    # 20 projects TODO variable
    res = requests.get(f'{gitlab_host}/api/v4/projects?per_page=20')
    if res.status_code == 200:
        return res.json()
    else:
        return []


def get_checks():
    with open('../config.json', 'r') as f:
        config = json.load(f)
        return config['checks'].keys()

###################
# insert into database
###################


for p in get_projects():
    projects.insert().values().execute({
        "description": str(p['description']).replace('\r\n', ' ').replace(',', ' '),
        "fork_count": p['forks_count'],
        "gitlab_project_id": p['id'],
        "last_commit": p['last_activity_at'],
        "name": p['name'],
        "name_space": p['namespace']['path'],
        "url": p['web_url']
    })

for project in projects.select().execute():
    for date in dates:
        linting_results.insert().values().execute({
            "lint_time": date.isoformat(),
            "project_id": project['id']
        })


# generate check results
for lr in linting_results.select().execute():
    for check in get_checks():
        check_results.insert().values().execute({
            "check_name": check,
            "lint_id": lr['id'],
            "result": next_result(lr['lint_time']),
        })
