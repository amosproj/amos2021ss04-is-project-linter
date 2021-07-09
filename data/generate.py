import csv
from datetime import datetime

from util import dates, get_checks, get_projects, next_result


project_id = 0
projects = []
linting_result_id = 0
linting_results = []
check_result_id = 0
check_results = []

# generate projects
for p in get_projects():
    project_id += 1
    projects.append({
        "id": project_id,
        "description": str(p['description']).replace('\r\n', ' ').replace(',', ' '),
        "fork_count": p['forks_count'],
        "gitlab_project_id": p['id'],
        "last_commit": p['last_activity_at'],
        "name": p['name'],
        "name_space": p['namespace']['path'],
        "url": p['web_url']
    })

# generate linting results
for project in projects:
    for date in dates:
        linting_result_id += 1
        linting_results.append({
            "id": linting_result_id,
            "lint_time": date.isoformat(),
            "project_id": project['id']
        })

# generate check results
for lr in linting_results:
    for check in get_checks():
        check_result_id += 1
        check_results.append({
            "id": check_result_id,
            "check_name": check,
            "lint_id": lr['id'],
            "result": next_result(datetime.fromisoformat(lr['lint_time'])),
        })

# export stuff
with open('projects.csv', 'w', encoding='UTF8') as f:
    w = csv.writer(f)
    for p in projects:
        w.writerow(p.values())

with open('linting_results.csv', 'w', encoding='UTF8') as f:
    w = csv.writer(f)
    for lr in linting_results:
        w.writerow(lr.values())

with open('check_results.csv', 'w', encoding='UTF8') as f:
    w = csv.writer(f)
    for cr in check_results:
        w.writerow(cr.values())


print('done')
