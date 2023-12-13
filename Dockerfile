FROM openjdk:17-alpine
COPY build/libs/*.jar server.jar
EXPOSE 8080
ENV TZ=Asia/Seoul
VOLUME ["logs"]
#ENTRYPOINT ["java", "-Dspring.profiles.active=prd", "-jar", "server.jar"]
ENTRYPOINT ["java", "-jar", "server.jar", "--spring.profiles.active=prd"]