FROM openjdk:8
ADD target/reviews-app.jar reviews-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "reviews-app.jar"]