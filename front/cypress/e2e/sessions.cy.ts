describe('Sessions spec', () => {
  const session = {
    id: 1,
    name: 'Yoga du matin',
    description: 'Une session relaxante',
    date: '2026-09-20T00:00:00.000Z',
    teacher_id: 1,
    users: [],
    createdAt: '2026-01-01T00:00:00.000Z',
    updatedAt: '2026-01-01T00:00:00.000Z'
  };

  const teacher = {
    id: 1,
    firstName: 'Margot',
    lastName: 'Delahaye',
    createdAt: '2026-01-01T00:00:00.000Z',
    updatedAt: '2026-01-01T00:00:00.000Z'
  };

  it('should display the sessions list and show the Create button for an admin', () => {
    cy.intercept('GET', '/api/session', [session]).as('sessions');
    cy.login(true);
    cy.wait('@sessions');

    cy.contains('Yoga du matin').should('be.visible');
    cy.contains('Create').should('be.visible');
  });

  it('should hide the Create button for a non-admin user', () => {
    cy.intercept('GET', '/api/session', [session]).as('sessions');
    cy.login(false);
    cy.wait('@sessions');

    cy.contains('Yoga du matin').should('be.visible');
    cy.contains('Create').should('not.exist');
  });

  it('should display session details and the Delete button for an admin', () => {
    cy.intercept('GET', '/api/session', [session]).as('sessions');
    cy.login(true);
    cy.wait('@sessions');

    cy.intercept('GET', '/api/session/1', session).as('sessionDetail');
    cy.intercept('GET', '/api/teacher/1', teacher).as('teacherDetail');

    cy.contains('Detail').click();
    cy.wait(['@sessionDetail', '@teacherDetail']);

    cy.contains('Une session relaxante').should('be.visible');
    cy.contains('Margot').should('be.visible');
    cy.contains('Delete').should('be.visible');
  });

  it('should create a new session', () => {
    cy.intercept('GET', '/api/session', [session]).as('sessions');
    cy.login(true);
    cy.wait('@sessions');

    cy.intercept('GET', '/api/teacher', [teacher]).as('teachers');
    cy.intercept('POST', '/api/session', { ...session, id: 2, name: 'Yoga du soir' }).as('createSession');

    cy.contains('Create').click();
    cy.wait('@teachers');

    cy.get('input[formControlName=name]').type('Yoga du soir');
    cy.get('input[formControlName=date]').type('2026-09-21');
    cy.get('mat-select[formControlName=teacher_id]').click();
    cy.get('mat-option').contains('Margot').click();
    cy.get('textarea[formControlName=description]').type('Une autre session');

    cy.get('button[type=submit]').click();
    cy.wait('@createSession');

    cy.url().should('include', '/sessions');
  });

  it('should show the submit button disabled when required fields are missing on session creation', () => {
    cy.intercept('GET', '/api/session', [session]).as('sessions');
    cy.login(true);
    cy.wait('@sessions');

    cy.intercept('GET', '/api/teacher', [teacher]).as('teachers');

    cy.contains('Create').click();
    cy.wait('@teachers');

    cy.get('button[type=submit]').should('be.disabled');
  });

  it('should update an existing session', () => {
    cy.intercept('GET', '/api/session', [session]).as('sessions');
    cy.login(true);
    cy.wait('@sessions');

    cy.intercept('GET', '/api/teacher', [teacher]).as('teachers');
    cy.intercept('GET', '/api/session/1', session).as('sessionDetail');
    cy.intercept('PUT', '/api/session/1', { ...session, name: 'Yoga du matin modifie' }).as('updateSession');

    cy.contains('Edit').click();
    cy.wait(['@teachers', '@sessionDetail']);

    cy.get('input[formControlName=name]').clear().type('Yoga du matin modifie');
    cy.get('button[type=submit]').click();
    cy.wait('@updateSession');

    cy.url().should('include', '/sessions');
  });

  it('should delete a session', () => {
    cy.intercept('GET', '/api/session', [session]).as('sessions');
    cy.login(true);
    cy.wait('@sessions');

    cy.intercept('GET', '/api/session/1', session).as('sessionDetail');
    cy.intercept('GET', '/api/teacher/1', teacher).as('teacherDetail');
    cy.intercept('DELETE', '/api/session/1', {}).as('deleteSession');

    cy.contains('Detail').click();
    cy.wait(['@sessionDetail', '@teacherDetail']);

    cy.contains('Delete').click();
    cy.wait('@deleteSession');

    cy.url().should('include', '/sessions');
  });
});
