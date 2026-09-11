# Yoga

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 19.2.16.

## Start the project

Clone the repository, then go inside the `front` folder:

> cd front

Install dependencies:

> npm install

Launch Front-end:

> npm run start

The app is available on http://localhost:4200

## Tests

### Unitary and integration test (Jest)

Launching test:

> npm run test

for following change:

> npm run test:watch

Generate coverage report:

> npm run test -- --coverage

Report is available here:

> front/coverage/jest/lcov-report/index.html

### E2E (Cypress)

Launching e2e test (interactive mode, opens a browser):

> npm run e2e

Launching e2e test headless (used for CI, and required for coverage collection):

> npm run e2e:ci

Generate coverage report (you should launch `npm run e2e:ci` before):

> npm run e2e:coverage

Report is available here:

> front/coverage/lcov-report/index.html
