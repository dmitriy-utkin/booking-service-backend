#!/bin/sh

cd ../
./gradlew wrapper
./gradlew clean
./gradlew build -x testClasses -x test

docker build -t booking-service -f docker/Dockerfile .

export DOCKERHOST=$(ifconfig | grep -E "([0-9]{1,3}\.){3}[0-9]{1,3}" | grep -v 127.0.0.1 | awk '{print $2 }' | cut -f2 -d:| head -n1)
docker-compose -f docker/docker-compose.yaml up
docker-compose -f docker/docker-compose.yaml down --volumes --remove-orphans
