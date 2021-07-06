from sqlalchemy import create_engine
from sqlalchemy import MetaData, Table, Column
from sqlalchemy import String, BigInteger, ForeignKey, Integer, DateTime, Boolean


db_name = input('db_name: ')
db_user = input('db_user: ')
db_pass = input('db_pass: ')

db = create_engine(f'postgresql://{db_user}:{db_pass}@localhost/{db_name}')
meta = MetaData(db)

projects = Table('project', meta,
                 Column('id', BigInteger, primary_key=True, unique=True),
                 Column('description', String),
                 Column('fork_count', Integer),
                 Column('gitlab_project_id', Integer),
                 Column('last_commit', DateTime),
                 Column('name', String),
                 Column('name_space', String),
                 Column('url', String)
                 )

linting_results = Table('linting_result', meta,
                        Column('id', BigInteger, primary_key=True, unique=True),
                        Column('lint_time', DateTime),
                        Column('project_id', BigInteger,
                               ForeignKey('project.id')),
                        )

check_results = Table('check_result', meta,
                      Column('id', BigInteger, primary_key=True, unique=True),
                      Column('check_name', String),
                      Column('lint_id', BigInteger,
                             ForeignKey('linting_result.id')),
                      Column('result', Boolean)
                      )


with db.connect() as conn:
    pass
