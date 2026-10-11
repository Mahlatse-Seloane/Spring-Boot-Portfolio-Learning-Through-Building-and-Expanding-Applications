# Task Tracker API

A RESTful task management API built with Java and Spring Boot. Originally based on the [Devtiro Task App build](https://github.com/devtiro/devtiro-task-app), this project extends the tutorial's implementation with additional API functionality, refined error handling, data integrity safeguards, and automated tests.

## Project Background

This project began as a learning exercise based on [Devtiro's Task App tutorial](https://www.youtube.com/watch?v=M2U4_t_PSRM), using the accompanying [GitHub repository](https://github.com/devtiro/devtiro-task-app) and [build guide](https://github.com/devtiro/devtiro-task-app/blob/main/docs/task-app-v2-combined.md) as the starting point.

Rather than stopping at the tutorial implementation, I continued developing the project to explore additional functionality and examine the design decisions involved in building a REST API. This included refining HTTP error handling, strengthening data integrity, and expanding automated test coverage.

The resulting project reflects an ongoing learning process: moving beyond implementing features to understanding how API design, application structure, persistence, validation, and testing work together.

## Original vs. Expanded Implementation

The original implementation provided the foundation for a task management REST API. The expanded version builds on that foundation by adding functionality, refining error handling, introducing stronger data integrity safeguards, and making transaction boundaries more explicit.

The following comparison highlights the most significant changes.

| Area | Original Implementation | Expanded Implementation | Why It Matters |
|---|---|---|---|
| **HTTP error semantics** | Returned `400 Bad Request` when a requested task could not be found. | Returns `404 Not Found` for missing tasks and `409 Conflict` for duplicate-title conflicts. | Aligns responses more closely with HTTP semantics, making API behaviour clearer to clients. |
| **Exception handling** | Handled validation failures and missing tasks, returning a relatively simple error response. | Introduced more comprehensive exception handling for validation failures, malformed JSON, invalid path parameters, missing tasks, data integrity conflicts, and unexpected runtime errors. | Makes failure responses more consistent and predictable while avoiding the exposure of internal exception details in unexpected-error responses. |
| **Validation feedback** | Returned the first validation error encountered. | Returns structured validation feedback containing field-specific errors and supports multiple validation messages for a field. | Helps API consumers identify and correct invalid request data more efficiently. |
| **Duplicate-title protection** | Did not include an explicit duplicate-title check or a database uniqueness constraint. | Added application-level duplicate-title checks and a database unique constraint on task titles. | Prevents duplicate titles under normal operation and provides a database-level safeguard against conflicting writes. |
| **Deletion behaviour** | Delegated deletion to the repository's `deleteById` method without an explicit service-level existence check. | Checks whether the task exists before deleting it and raises a task-not-found exception when necessary. | Makes the API's behaviour for deletion requests involving nonexistent tasks explicit and consistent with its other resource operations. |
| **Transaction management** | Did not explicitly define transaction boundaries in the service layer. | Uses read-only transactions by default for service operations, with explicit write transactions for create, update, and delete operations. | Makes transaction intent clearer and establishes a more deliberate boundary for database operations. |
| **Application responsibilities** | The controller converted request DTOs into separate request records, invoked the service, and mapped returned entities into response DTOs. | The controller delegates more of the request-to-response workflow to the service, which returns response DTOs. | Reduces controller-level mapping work and centralizes more of the application workflow in the service layer. |
| **Response models** | Used a single task response DTO for create, read, and update operations. | Introduced separate response DTOs for create, read, and update operations. | Allows each operation's response contract to evolve independently, although the separate DTOs may be repetitive while their fields remain similar. |
| **Automated testing** | The tutorial implementation served as the starting point for the project's testing work. | Added service-layer unit tests, controller-focused tests, repository tests for derived query methods, and an application-context loading test. | Adds automated checks for business logic, HTTP behaviour, persistence queries, and application startup. |

### Key Takeaway

The expanded implementation explores how a REST API can communicate failures more accurately, protect data integrity at both the application and database levels, define transaction boundaries explicitly, and organize responsibilities across its layers.

These changes reflect a shift from implementing core functionality toward examining the design decisions that make an API more predictable, maintainable, and easier to test.

## Technical Highlights

- **REST API design:** Implemented task creation, collection retrieval, updating, and deletion using resource-oriented endpoints and appropriate HTTP response statuses.
- **Centralized exception handling:** Used a global exception handler to translate application exceptions, validation failures, and malformed requests into structured HTTP error responses.
- **Validation and data integrity:** Combined request validation, application-level duplicate-title checks, and a database uniqueness constraint to protect task data.
- **Layered application design:** Separated web, service, persistence, and mapping responsibilities, using DTOs to define API request and response contracts.
- **Transaction management:** Applied read-only transactions by default in the service layer and explicitly defined transaction boundaries for write operations.
- **Automated testing:** Used JUnit 5, Mockito, AssertJ, MockMvc, and Spring Boot testing support to test service logic, controller behaviour, repository queries, and application-context loading.

## API Overview

All endpoints are available under the `/api/v1/tasks` base path.

| Method | Endpoint | Description | Success Response |
|---|---|---|---|
| `POST` | `/api/v1/tasks` | Creates a task. | `201 Created` |
| `GET` | `/api/v1/tasks` | Retrieves all tasks, ordered by creation time (ascending). | `200 OK` |
| `PUT` | `/api/v1/tasks/{id}` | Updates an existing task. | `200 OK` |
| `DELETE` | `/api/v1/tasks/{id}` | Deletes an existing task. | `204 No Content` |

### Error Responses

The API uses HTTP status codes to distinguish common failure scenarios:

- `400 Bad Request` — Invalid request data, validation failures, malformed JSON, or an invalid UUID.
- `404 Not Found` — The requested task does not exist.
- `409 Conflict` — A task title conflicts with an existing task title.
- `500 Internal Server Error` — An unexpected server-side error occurs.

Error responses provide structured details to help API consumers understand and resolve request failures.

## Architecture and Project Structure

The application follows a layered architecture that separates HTTP handling, application logic, persistence, and data transformation.

### Request Flow

A typical request moves through the application as follows:

1. **Controller:** Receives the HTTP request, validates input, and delegates the operation to the service.
2. **Service:** Applies business rules, coordinates the operation, and defines transaction boundaries.
3. **Repository:** Uses Spring Data JPA to retrieve or persist task entities.
4. **Response mapping:** Maps the result into the appropriate response DTO for the client.

The global exception handler translates supported application and request errors into structured HTTP responses.

### Main Components

| Component | Responsibility |
|---|---|
| `controller` | Exposes REST endpoints and handles HTTP requests. |
| `service` | Coordinates task operations and enforces application-level rules. |
| `repository` | Provides database access through Spring Data JPA. |
| `domain.entity` | Defines the task persistence model. |
| `domain.dto` | Defines request, response, validation, and error data structures. |
| `mapper` | Converts between DTOs and entities. |
| `exception` | Contains application-specific exceptions. |
| `GlobalExceptionHandler` | Maps exceptions to appropriate HTTP responses. |

### Testing Structure

The test suite covers the application's main responsibilities:

- **Service tests:** Verify business rules and success and failure paths using Mockito.
- **Controller tests:** Use MockMvc to verify HTTP responses, JSON output, validation, and exception handling.
- **Repository tests:** Exercise derived query methods against a test database.
- **Application-context test:** Checks that the Spring application context loads successfully.

## Testing and Running the Application

### Prerequisites

- **Java:** JDK 25
- **Build tool:** Maven Wrapper, included in the repository

### Run the Tests

Run the automated test suite from the project root.

**Linux / macOS**

```bash
./mvnw clean test
```

**Windows (Command Prompt)**

```bat
mvnw.cmd clean test
```

The test suite covers service-layer business logic, controller behaviour and HTTP responses, repository query methods, and application-context loading.

### Start the Application

**Linux / macOS**

```bash
./mvnw spring-boot:run
```

**Windows (Command Prompt)**

```bat
mvnw.cmd spring-boot:run
```

Once started, the API is available at:

`http://localhost:8080`

Use the endpoints documented in the [API Overview](#api-overview) to interact with the application.

### H2 Database Console

The application uses an in-memory H2 database for development. If the H2 console is enabled in the application configuration, access it at:

`http://localhost:8080/h2-console`

Use the following local development connection settings:

| Setting | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:Devtiro_tasks` |
| Username | `sa` |
| Password | `password` |

Because the database is in memory, task data is normally lost when the application stops. These database settings and the H2 console are intended for local development, not production deployment.

## Current Limitations and Potential Next Steps

The project focuses on core REST API functionality, persistence, validation, error handling, and automated testing. Several areas remain outside its current scope and provide opportunities for further development.

- **Authentication and authorization:** The API does not currently restrict access based on user identity or task ownership. Authentication and authorization could be introduced to control who can access and modify tasks.
- **Persistent storage:** The application uses an in-memory H2 database, so task data is not retained across application restarts. Integrating a persistent database such as PostgreSQL would support longer-term data storage.
- **Pagination and filtering:** The task collection endpoint returns all tasks. Pagination, filtering, and more flexible sorting would make retrieval more manageable as the dataset grows.
- **Concurrent data updates:** The database uniqueness constraint protects task-title uniqueness, but application-level duplicate checks can still encounter race conditions under concurrent requests. Handling database constraint violations consistently and testing concurrent operations would strengthen this behaviour.
- **Production readiness:** Deployment configuration, externalized secrets, operational logging, monitoring, and other production concerns would require additional work before the API could be considered for a production environment.

These are potential next steps rather than features currently implemented. They provide a path for extending the project while building on its existing API design, persistence, validation, and testing foundations.