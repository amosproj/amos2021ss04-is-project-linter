#!/bin/bash

read -p "database name: " dbname

read -p "database user: " dbuser

read -p "database password: " dbpass

psql -U $dbuser -d $dbname -q -c "TRUNCATE TABLE project RESTART IDENTITY CASCADE;"

psql -U $dbuser -d $dbname -q -c "\copy project(id, description, fork_count, gitlab_project_id, last_commit, name, name_space, url) from 'projects.csv' delimiter ','"
psql -U $dbuser -d $dbname -q -c "\copy linting_result(id, lint_time, project_id) from 'linting_results.csv' delimiter ','"
psql -U $dbuser -d $dbname -q -c "\copy check_result(id, check_name, lint_id, result) from 'check_results.csv' delimiter ','"
