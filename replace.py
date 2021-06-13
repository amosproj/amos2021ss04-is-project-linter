from mako.template import Template
import json

# config.json
with open('config.json', 'r') as config_file:
    data=config_file.read()
config = json.loads(data)

# check.mako
with open('check.mako', 'r') as mako_file:
    template = mako_file.read()
    for check in config['checks']:
        with open(f'./server/src/main/java/amosproj/server/linter/checks/{check}.java', 'w+') as new_file:
            new_file.write(Template(template).render(data=check))
            new_file.close()
