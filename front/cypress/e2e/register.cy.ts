describe('Register spec', () => {
  it('should register successfully and redirect to login', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 201,
      body: { message: 'User registered successfully!' }
    });

    cy.visit('/register');
    cy.get('input[formControlName=firstName]').type('Margot');
    cy.get('input[formControlName=lastName]').type('Delahaye');
    cy.get('input[formControlName=email]').type('margot@teacher.com');
    cy.get('input[formControlName=password]').type('password123');
    cy.get('button[type=submit]').click();

    cy.url().should('include', '/login');
  });

  it('should display an error message when the email is already taken', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: { message: 'Email is already taken!' }
    });

    cy.visit('/register');
    cy.get('input[formControlName=firstName]').type('Margot');
    cy.get('input[formControlName=lastName]').type('Delahaye');
    cy.get('input[formControlName=email]').type('existing@teacher.com');
    cy.get('input[formControlName=password]').type('password123');
    cy.get('button[type=submit]').click();

    cy.contains('An error occurred').should('be.visible');
    cy.url().should('include', '/register');
  });

  it('should disable the submit button when a required field is missing', () => {
    cy.visit('/register');

    cy.get('button[type=submit]').should('be.disabled');

    cy.get('input[formControlName=firstName]').type('Margot');
    cy.get('input[formControlName=lastName]').type('Delahaye');
    cy.get('input[formControlName=email]').type('margot@teacher.com');
    cy.get('button[type=submit]').should('be.disabled');

    cy.get('input[formControlName=password]').type('password123');
    cy.get('button[type=submit]').should('be.enabled');
  });
});
