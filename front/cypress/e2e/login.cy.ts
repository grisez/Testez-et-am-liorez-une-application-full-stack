describe('Login spec', () => {
  it('should log in successfully with valid credentials', () => {
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'yoga@studio.com',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true
      },
    });

    cy.intercept('GET', '/api/session', []).as('session');

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234');
    cy.get('button[type=submit]').click();

    cy.url().should('include', '/sessions');
  });

  it('should display an error message when credentials are invalid', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: { message: 'Bad credentials' }
    });

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('wrongPassword');
    cy.get('button[type=submit]').click();

    cy.contains('An error occurred').should('be.visible');
    cy.url().should('include', '/login');
  });

  it('should disable the submit button when a required field is missing', () => {
    cy.visit('/login');

    cy.get('button[type=submit]').should('be.disabled');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('button[type=submit]').should('be.disabled');

    cy.get('input[formControlName=password]').type('test!1234');
    cy.get('button[type=submit]').should('be.enabled');
  });
});
