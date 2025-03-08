# Enterprise Asset Management System (EAMS)

A comprehensive enterprise-grade asset management system built with Quarkus and Kotlin.

## Project Overview

Enterprise Asset Management System (EAMS) is a sophisticated application designed to help organizations track, maintain, and manage their physical assets throughout their lifecycle. This system provides a robust set of features for asset tracking, maintenance scheduling, depreciation calculation, and reporting.

## Architecture

The project follows a domain-driven design (DDD) approach with a clean architecture:

- **Domain Layer**: Contains the core business logic and entities
- **Application Layer**: Contains services that orchestrate the domain objects
- **Infrastructure Layer**: Contains implementations of repositories, REST resources, and external integrations

## Technology Stack

- **Framework**: Quarkus 3.19.2
- **Language**: Kotlin 2.0.21
- **Java Version**: 21
- **Build Tool**: Gradle with Kotlin DSL
- **Database**: Reactive PostgreSQL (to be implemented)
- **Messaging**: Kafka with SmallRye Reactive Messaging (to be implemented)
- **Authentication**: OpenID Connect with Keycloak (to be implemented)
- **Documentation**: OpenAPI/Swagger
- **Testing**: JUnit 5, REST-assured, Mockito
- **Metrics**: Micrometer with Prometheus

## Getting Started

### Prerequisites

- JDK 21 or higher
- Gradle 8.x or higher
- Docker (for running PostgreSQL and Kafka)

### Running the Application in Development Mode

```bash
./gradlew quarkusDev
```

The application will be available at http://localhost:8080

## Features

- Asset lifecycle management
- Maintenance scheduling and tracking
- Depreciation calculation
- Asset transfers between locations
- Reporting and analytics
- User authentication and authorization (to be implemented)
- Notification system (to be implemented)

## API Documentation

Once the application is running, you can access the OpenAPI documentation at:
http://localhost:8080/q/swagger-ui/

## Project Structure

```
src
├── main
│   ├── kotlin
│   │   └── solutions
│   │       └── dreamforge
│   │           ├── application
│   │           │   ├── AppLifecycle.kt
│   │           │   └── service
│   │           │       └── AssetService.kt
│   │           ├── domain
│   │           │   ├── model
│   │           │   │   ├── Asset.kt
│   │           │   │   └── MaintenanceRecord.kt
│   │           │   └── repository
│   │           │       └── AssetRepository.kt
│   │           ├── infrastructure
│   │           │   ├── dto
│   │           │   │   ├── AssetDto.kt
│   │           │   │   └── MaintenanceRecordDto.kt
│   │           │   ├── repository
│   │           │   │   └── InMemoryAssetRepository.kt
│   │           │   └── rest
│   │           │       └── AssetResource.kt
│   │           └── ExampleResource.kt
│   └── resources
│       └── application.properties
└── test
    └── kotlin
        └── solutions
            └── dreamforge
                ├── domain
                │   └── model
                │       └── AssetTest.kt
                ├── application
                │   └── service
                │       └── AssetServiceTest.kt
                └── infrastructure
                    └── rest
                        └── AssetResourceTest.kt
```

## Contributing

Contributions are welcome. Please feel free to submit a Pull Request.

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.
