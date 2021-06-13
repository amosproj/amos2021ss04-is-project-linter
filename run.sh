#!/bin/bash
docker-compose down -v
cd ./server
./gradlew build -x test
docker build -t amoslinter/server:staging .
cd ..
docker-compose --env-file .env up
