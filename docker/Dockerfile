FROM openjdk:17-oracle

WORKDIR /app

COPY ../build/libs/booking-service-backend-0.0.1-SNAPSHOT.jar app.jar
COPY ../src/main/resources/application.yaml application.yaml
COPY ../src/main/resources/data/data.json data.json

ENV SPRING_PROFILES_ACTIVE=prod
ENV APP_CACHE_ENABLE=true
ENV APP_UPLOADING_MOCK_DATA="data.json"
ENV APP_DATE_FORMAT="dd/MM/yyyy"
ENV KAFKA_BOOTSTRAP_SERVER="localhost:9092"
ENV MONGO_URI="mongodb://root:root@localhost:27017/appdatabase?authSource=admin"
ENV POSTRGES_URI="jdbc:postgresql://localhost:5432/booking_system"
ENV POSTGRES_USERNAME="postgres"
ENV POSTGRES_PASSWORD="postgres"
ENV POSTGRES_SCHEMA="booking_schema"



CMD ["java", "-Dspring.config.location=file:/app/application.yaml", "-jar", "app.jar"]