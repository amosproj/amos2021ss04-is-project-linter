# Getting Started

## Properties file

Have a file called `application.properties` located in `server/main/resources/` that looks as follows:

```properties
spring.datasource.platform=postgres
spring.datasource.url=jdbc:postgresql://localhost:5432/db_name
spring.datasource.username=user
spring.datasource.password=password
```

## Building

If youre on windows, use gradlew.bat instead

- See tasks: `./gradlew tasks`
- Build: `./gradlew build`

## Docker

run:

```shell
docker build --build-arg JAR_FILE=build/libs/\*.jar -t linter-server .
docker run linter-server
```

