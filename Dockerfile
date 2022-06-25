FROM adoptopenjdk/openjdk15:ubi

ARG JAR_FILE=target/Mega-Market-Open-API-0.0.1-SNAPSHOT.jar
WORKDIR /opt/app
COPY ${JAR_FILE} app.jar

EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom" , "-jar", "app.jar"]