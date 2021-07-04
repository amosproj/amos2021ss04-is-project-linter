from datetime import datetime, timedelta, timezone

start_time = datetime(2021, 6, 6, 15, 0, 0)
dates = [start_time + timedelta(n) for n in range(7)]

projects = [
    {
        "id": 68955,
        "description": "Test repo where we want everything to be true when linted.",
        "fork_count": 0,
        "gitlab_project_id": 19386,
        "last_commit": "2021-06-16 21:11:21.597000",
        "name": "AllChecksTrue",
        "name_space": "uv59uxut",
        "url": "https://gitlab.cs.fau.de/uv59uxut/allcheckstrue"
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


for lr in linting_results: print(lr)