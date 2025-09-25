FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/cat_photo_s3-0.0.1-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]