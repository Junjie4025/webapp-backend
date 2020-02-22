FROM java:8-jdk-alpine

COPY ./target/ccwebapp-0.0.1-SNAPSHOT.jar /usr/app/

WORKDIR /usr/app

ENV db_url localhost

ENV bucket_url localhost

ENTRYPOINT java -jar ccwebapp-0.0.1-SNAPSHOT.jar