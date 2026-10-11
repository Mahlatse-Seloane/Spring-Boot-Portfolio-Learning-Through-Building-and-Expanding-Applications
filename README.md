# Spring Boot Portfolio: Learning Through Building and Expanding Applications

Welcome to my Spring Boot portfolio! 🚀

This repository brings together a collection of backend projects built using Java and Spring Boot, starting with existing tutorial implementations and extending them through independent development, experimentation, and testing.

Rather than treating tutorials as finished products, my goal is to use them as starting points. For each application, I want to understand the original design, explore the reasoning behind its implementation, identify areas for further development, and make deliberate changes that improve my understanding of backend engineering.

Each project focuses on a different application domain, providing opportunities to explore REST API design, layered architecture, persistence, validation, exception handling, security, and automated testing as the projects evolve.

The original tutorial authors and repositories are credited throughout. The focus of this portfolio is the additional work I undertake beyond the initial implementations, together with the lessons learned along the way.

---

## 🗺️ Project Roadmap & Progress

### 🔹 Project 1: [Task Tracker API](https://github.com/Mahlatse-Seloane/Spring-Boot-Portfolio-Learning-Through-Building-and-Expanding-Applications/tree/main/TaskTrackerAPI)

**Status:** Expanded implementation completed

**Original tutorial:** [Devtiro Task App](https://github.com/devtiro/devtiro-task-app)

**Objective:** Build a foundation in REST API development by understanding how HTTP requests, application logic, data validation, and persistence work together.

**Work completed:**
- Refined HTTP error handling to distinguish invalid requests, missing resources, and conflicting data.
- Added structured validation feedback and centralized exception handling.
- Introduced application-level duplicate-title checks and a database uniqueness constraint.
- Made transaction boundaries more explicit in the service layer.
- Added automated service, controller, repository, and application-context tests.

**Key concepts:** RESTful API design, DTOs, Spring Web, Spring Data JPA, validation, exception handling, transaction management, and automated testing.

---

### 🔹 Project 2: [Blog Application](https://github.com/devtiro/devtiro-blog-app)

**Status:** Planned

**Original tutorial:** [Devtiro Blog App](https://github.com/devtiro/devtiro-blog-app)

**Objective:** Expand a blog application while exploring the design decisions involved in managing content and securing API access.

**Areas to explore:**
- Understanding the blog domain and its core operations.
- Examining how Spring Security and JWT-based authentication fit into the application.
- Reviewing request validation, error handling, and authorization boundaries.
- Expanding automated tests around application behaviour and security-related scenarios.
- Identifying areas where the original implementation could be extended or refined.

**Key concepts to explore:** REST API design, content management, Spring Security, authentication, authorization, persistence, and testing.

---

### 🔹 Project 3: [Restaurant Review Application](https://github.com/devtiro/devtiro-restaurant-review-app)

**Status:** Planned

**Original tutorial:** [Devtiro Restaurant Review App](https://github.com/devtiro/devtiro-restaurant-review-app)

**Objective:** Explore a more involved application domain involving restaurant data, reviews, and search, while examining how additional infrastructure affects backend design.

**Areas to explore:**
- Understanding the relationships between restaurants, reviews, and related data.
- Examining the application's authentication and authorization approach.
- Investigating the photo-upload and restaurant-search functionality.
- Exploring how Elasticsearch supports geospatial search.
- Reviewing data validation, error handling, and automated test coverage.

**Key concepts to explore:** Resource relationships, persistence, external service integration, search, geospatial queries, security, and testing.

---

### 🔹 Project 4: [Event Ticket Application](https://github.com/devtiro/devtiro-event-ticket-app)

**Status:** Planned

**Original tutorial:** [Devtiro Event Ticket App](https://github.com/devtiro/devtiro-event-ticket-app)

**Objective:** Study a more involved application domain centred on event management and ticket sales, with opportunities to explore business rules and interactions between different parts of a system.

**Areas to explore:**
- Understanding the relationships between events, organizers, attendees, and tickets.
- Examining ticket purchasing and the associated business rules.
- Investigating how authentication and authorization are handled.
- Reviewing data integrity and transaction boundaries around ticket-related operations.
- Expanding automated tests to cover important success and failure scenarios.

**Key concepts to explore:** Domain modelling, resource relationships, business rules, transaction management, security, data integrity, and testing.

---

## 🛠️ Technology Stack & Engineering Concepts

The exact technologies will vary by project. The main areas I intend to explore include:

- **Language:** Java
- **Framework:** Spring Boot
- **Web layer:** Spring Web and REST API design
- **Persistence:** Spring Data JPA, Hibernate, and relational databases
- **Data transfer:** Request and response DTOs, validation, and mapping
- **Error handling:** Application-specific exceptions and centralized HTTP error responses
- **Security:** Spring Security, authentication, and authorization where applicable
- **Testing:** JUnit 5, Mockito, AssertJ, MockMvc, and persistence-focused tests
- **Additional infrastructure:** Technologies such as Elasticsearch and Keycloak where the application requires them

## 🎯 Learning Objectives

Across these projects, my goal is to develop a stronger understanding of:

1. **API design:** Building predictable REST endpoints with appropriate HTTP methods and status codes.
2. **Application architecture:** Separating controllers, services, repositories, and data-mapping responsibilities.
3. **Data integrity:** Applying validation, database constraints, and deliberate transaction boundaries.
4. **Security:** Understanding authentication and authorization, and how they affect API design.
5. **Testing:** Verifying business logic, HTTP behaviour, persistence queries, and failure scenarios.
6. **Engineering judgment:** Understanding not only how a solution works, but also its trade-offs, limitations, and possible alternatives.

## 📌 Approach to Each Project

For each application, I aim to follow a consistent process:

1. **Understand the baseline:** Study the tutorial, source code, domain model, and architecture.
2. **Establish the behaviour:** Identify the existing functionality and verify how it works.
3. **Identify opportunities:** Look for missing functionality, unclear responsibilities, weak error handling, or insufficient testing.
4. **Expand deliberately:** Make changes for specific reasons rather than adding complexity without a clear purpose.
5. **Test the changes:** Add automated tests for the relevant behaviour and important failure cases.
6. **Document the result:** Explain what changed, why it changed, what trade-offs remain, and what could be explored next.

The intention is not to label every change as an improvement automatically, but to understand the reasoning behind each decision and evaluate its consequences.

---

## Closing Note

This portfolio represents an ongoing learning process rather than a collection of projects presented as finished, production-ready systems.

By studying existing implementations, expanding them with clear objectives, and documenting the results, I aim to develop practical backend engineering skills and a stronger understanding of how Spring Boot applications are designed, tested, and maintained.
