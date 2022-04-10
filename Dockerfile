FROM openjdk:11

COPY target/MockMock.jar . 
CMD ["java", "-jar", "MockMock.jar"]