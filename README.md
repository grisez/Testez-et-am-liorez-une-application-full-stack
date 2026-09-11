# Yoga App

Full-stack application (Spring Boot + Angular) for managing yoga sessions: users can register, book sessions, and admins can create/edit/delete sessions and assign teachers.

This repository was refactored and covered with an automated test suite (unit, integration, end-to-end) as part of an OpenClassrooms training project.

## Architecture

| Layer    | Stack                                                                | Port |
|----------|-----------------------------------------------------------------------|------|
| Frontend | Angular 19 (standalone components), Angular Material, RxJS            | 4200 |
| Backend  | Spring Boot 3.5.5, Spring Security (JWT), Spring Data JPA, MapStruct   | 8080 |
| Database | MySQL (started via Docker Compose)                                     | 3306 |

```
front (Angular) --HTTP/JWT--> back (Spring Boot) --JPA--> MySQL
```

- [`back/`](back) — REST API, authentication, business logic. See [back/README.md](back/README.md) for backend-specific setup and tests.
- [`front/`](front) — Angular single-page app. See [front/README.md](front/README.md) for frontend-specific setup and tests.

## Prerequisites

- JDK 21
- Node.js 18+ and npm
- Docker and Docker Compose (used by the backend to run MySQL)
- Maven 3.9.3+

## Configuration

- **Backend**: configuration is provided via `back/.env` (database connection and JWT
  secret), already checked in with working default values for local development. See
  [back/README.md](back/README.md#environment-configuration) for details.
- **Frontend**: the API base URL is set in `front/src/environments/environment.ts`
  (`baseUrl: 'localhost:8080/api/'`), pointing to the backend started below.

## Getting started

1. Clone the repository:
   ```
   git clone https://github.com/grisez/Testez-et-am-liorez-une-application-full-stack.git
   cd Testez-et-am-liorez-une-application-full-stack
   ```
2. Start the backend (see [back/README.md](back/README.md) for details):
   ```
   cd back
   mvn spring-boot:run
   ```
   This starts the MySQL container (via Docker Compose) and the API on http://localhost:8080.
3. In another terminal, start the frontend (see [front/README.md](front/README.md) for details):
   ```
   cd front
   npm install
   npm run start
   ```
   The app is available on http://localhost:4200.
4. Log in with the default admin account created in the database (see [back/README.md](back/README.md#starting-the-backend) for how to seed it):
   - email: `yoga@studio.com`
   - password: `test!1234`

## Running the tests

Each module has its own test suite and coverage report; full details (commands, thresholds, report paths) are in the module READMEs.

### Backend (JUnit 5 + Mockito, Jacoco)

```
cd back
mvn test      # run unit + integration tests
mvn verify    # run tests, generate the Jacoco report, and check coverage thresholds
```
Coverage report: `back/target/site/jacoco/index.html`

### Frontend unit/integration tests (Jest)

```
cd front
npm run test -- --coverage
```
Coverage report: `front/coverage/jest/lcov-report/index.html`

### End-to-end tests (Cypress)

```
cd front
npm run e2e:ci        # headless run, required to collect coverage
npm run e2e:coverage  # generate the e2e coverage report
```
Coverage report: `front/coverage/lcov-report/index.html`

## Test coverage

Coverage thresholds (statements/branches/functions/lines) are enforced automatically:
- backend: via `jacoco-maven-plugin` (`mvn verify`)
- frontend unit tests: via Jest's `coverageThreshold` (`front/jest.config.js`)
- frontend e2e: via `nyc` (`front/.nycrc.json`)

## Resources

- Postman collection: [`back/postman/yoga.postman_collection.json`](back/postman/yoga.postman_collection.json)
