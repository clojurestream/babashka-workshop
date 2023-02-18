FROM docker.io/eclipse-temurin:19-jre

WORKDIR /opt

COPY todos.jar .

ENTRYPOINT ["java", "-jar", "todos.jar"]
