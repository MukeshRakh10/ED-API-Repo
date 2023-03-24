FROM openjdk:8

#COPY target/spring-boot-docker-app.jar  /usr/app/
COPY target/ed-api.jar  ed-api.jar

#WORKDIR /usr/app/

#ENTRYPOINT ["java", "-jar", "spring-boot-docker-app.jar"]
ENTRYPOINT ["java", "-jar", "ed-api.jar"]
