import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { expect, jest } from '@jest/globals';

import { AuthGuard } from './auth.guard';
import { SessionService } from '../core/service/session.service';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let sessionService: { isLogged: boolean };
  let router: { navigate: jest.Mock };

  beforeEach(() => {
    sessionService = { isLogged: false };
    router = { navigate: jest.fn() };

    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        { provide: SessionService, useValue: sessionService },
        { provide: Router, useValue: router }
      ]
    });

    guard = TestBed.inject(AuthGuard);
  });

  it('should allow activation when the user is logged in', () => {
    sessionService.isLogged = true;

    expect(guard.canActivate()).toBe(true);
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should deny activation and redirect to login when the user is not logged in', () => {
    sessionService.isLogged = false;

    expect(guard.canActivate()).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['login']);
  });
});
