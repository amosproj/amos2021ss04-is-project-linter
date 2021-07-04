from datetime import datetime, timedelta, timezone
import random
import csv

start_time = datetime(2021, 6, 1, 15, 0, 0)
dates = [start_time + timedelta(n) for n in range(7)]


def next_result(dt : datetime):
    x = random.randint(start_time.day, dt.day + 3)
    return x > 3


checks = [
    'CheckContributingExistence',
    'EitherOwnersOrMaintainersExist',
    'GuestRoleDisabled',
    'HasSquashingDisabled',
    'HasAvatar',
    'CheckReadmeHasLinks',
    'CheckReadmeHasPicture',
    'HasForkingEnabled',
    'GitlabWikiDisabled',
    'HasServiceDeskDisabled',
    'HasBadges',
    'HasDescription',
    'IsPublic',
    'CheckReadmeExistence',
    'DeveloperRoleDisabled',
    'CheckNoContributingChain',
    'HasIssuesEnabled',
    'NotDefaultReadme',
    'HasMergeRequestEnabled',
]

projects = [
    {
        "id": 1,
        "description": "Test repo where we want everything to be true when linted.",
        "fork_count": 0,
        "gitlab_project_id": 19386,
        "last_commit": "2021-06-16 21:11:21.597000",
        "name": "AllChecksTrue",
        "name_space": "uv59uxut",
        "url": "https://gitlab.cs.fau.de/uv59uxut/allcheckstrue"
    },
    {
        "id": 2,
        "description": "Mixture Density Networks for PyTorch",
        "fork_count": 0,
        "gitlab_project_id": 19277,
        "last_commit": "2021-06-04 15:04:03.972000",
        "name": "pytorch-mdn",
        "name_space": "oc81adow",
        "url": "https://gitlab.cs.fau.de/oc81adow/pytorch-mdn"
    }
]
linting_result_id = 0
linting_results = []
check_result_id = 0
check_results = []

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
    for check in checks:
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
