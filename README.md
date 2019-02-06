# worker
backend worker component written in java

# this app is dockerized.

build:
docker build -t IMG_NAME:TAG --build-arg JAR_FILE=NAME_OF_FILE .

run:
docker run -d -p 8080:8080 -e DB_URL='jdbc:mysql://192.168.99.100:3306/transport?useSSL=false&allowPublicKeyRetrieval=true' -e ROOT_PASS='secret' IMG_NAME:TAG
use your host, port, dbschema and root pass.

run DB image:
docker run  -p 3306:3306 --name sample-mysql -e MYSQL_ROOT_PASSWORD=secret -e MYSQL_DATABASE=transport -d mysql:8.0

image for worker is pullable from dockerhub repo: docker pull pchlebus/demo:worker

