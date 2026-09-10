import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { take } from 'rxjs';

import { SessionService } from './session.service';
import { SessionInformation } from '../models/sessionInformation.interface';

describe('SessionService', () => {
  let service: SessionService;

  const sessionInformation: SessionInformation = {
    token: 'fake-token',
    type: 'Bearer',
    id: 1,
    username: 'margot@teacher.com',
    firstName: 'Margot',
    lastName: 'Delahaye',
    admin: false
  };

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should emit false initially', (done) => {
    service.$isLogged().pipe(take(1)).subscribe((isLogged) => {
      expect(isLogged).toBe(false);
      done();
    });
  });

  it('should update state and emit true when logging in', (done) => {
    service.logIn(sessionInformation);

    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(sessionInformation);
    service.$isLogged().pipe(take(1)).subscribe((isLogged) => {
      expect(isLogged).toBe(true);
      done();
    });
  });

  it('should clear state and emit false when logging out', (done) => {
    service.logIn(sessionInformation);

    service.logOut();

    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
    service.$isLogged().pipe(take(1)).subscribe((isLogged) => {
      expect(isLogged).toBe(false);
      done();
    });
  });
});
