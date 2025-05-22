# Tukio Notification Service

This microservice provides notification capabilities for the Tukio Campus Event Scheduling System.

## Features

- Multi-channel notifications (Email, Push, In-App)
- Template-based notification content
- User notification preferences
- Batch notification sending
- Event reminders
- Real-time notifications via WebSockets

## Getting Started

### Prerequisites

- Java 17 or higher
- PostgreSQL
- Redis
- Kafka

### Running the Service

```bash
./gradlew bootRun
```

### Building the Service

```bash
./gradlew build
```

## API Documentation

Swagger UI: http://localhost:8086/swagger-ui.html

## Configuration

Update `src/main/resources/application.properties` with your database and service configurations.
