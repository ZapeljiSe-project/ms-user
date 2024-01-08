FROM eclipse-temurin:17-jre

RUN mkdir /app

WORKDIR /app

ADD ./api/target/ms-user-api-1.0.0-SNAPSHOT.jar /app

EXPOSE 8081

CMD ["java", "-jar", "ms-user-api-1.0.0-SNAPSHOT.jar"]
#ENTRYPOINT ["java", "-jar", "ms-user-api-1.0.0-SNAPSHOT.jar"]
#CMD java -jar ms-user-api-1.0.0-SNAPSHOT.jar