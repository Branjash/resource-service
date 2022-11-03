FROM openjdk:11
LABEL maintainer="branko_rovcanin@epam.com"
VOLUME /resource-service
COPY ./target/resource-service-0.0.1-SNAPSHOT.jar /usr/app/
EXPOSE 8081
WORKDIR /usr/app
ENTRYPOINT ["java","-jar", "resource-service-0.0.1-SNAPSHOT.jar"]