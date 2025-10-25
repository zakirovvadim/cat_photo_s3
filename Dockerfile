FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar /app/app.jar
RUN mkdir -p /app/data
ENV JAVA_OPTS="-Xms16m -Xmx128m -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError -Xss512k -XX:MaxMetaspaceSize=128m -XX:MaxDirectMemorySize=32m"
EXPOSE 8080
CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
