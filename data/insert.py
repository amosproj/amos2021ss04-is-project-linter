from sqlalchemy import create_engine
from sqlalchemy import MetaData, Table
import getpass
from util import dates, get_checks, get_projects, next_result


###################
# database connection
###################

db_name = input('db_name: ')
db_user = input('db_user: ')
db_pass = getpass.getpass('db_pass : ')


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


print('done')
