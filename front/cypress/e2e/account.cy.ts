describe('Account spec', () => {
  const user = {
    id: 1,
    email: 'margot@teacher.com',
    lastName: 'Delahaye',
    firstName: 'Margot',
    admin: false,
    createdAt: '2026-01-01T00:00:00.000Z',
    updatedAt: '2026-01-01T00:00:00.000Z'
  };

  it('should display the user account information', () => {
    cy.intercept('GET', '/api/session', []).as('sessions');
    cy.login(false);
    cy.wait('@sessions');

    cy.intercept('GET', '/api/user/1', user).as('user');
    cy.contains('Account').click();
    cy.wait('@user');

    cy.contains('Margot').should('be.visible');
    cy.contains('margot@teacher.com').should('be.visible');
  });

  it('should log out and redirect to the login/register screen', () => {
    cy.intercept('GET', '/api/session', []).as('sessions');
    cy.login(false);
    cy.wait('@sessions');

    cy.contains('Logout').click();

    cy.contains('Login').should('be.visible');
  });
});
