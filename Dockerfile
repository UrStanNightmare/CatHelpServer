FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE='build/libs/cathelpserver-*.jar'
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom -Xmx2g","-jar","/app.jar"]