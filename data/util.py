import json
from datetime import datetime, timedelta
import random
import requests

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
