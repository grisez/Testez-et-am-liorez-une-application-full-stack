import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { expect, jest } from '@jest/globals';

import { UnauthGuard } from './unauth.guard';
import { SessionService } from '../core/service/session.service';

describe('UnauthGuard', () => {
  let guard: UnauthGuard;
  let sessionService: { isLogged: boolean };
  let router: { navigate: jest.Mock };

  beforeEach(() => {
    sessionService = { isLogged: false };
    router = { navigate: jest.fn() };

    TestBed.configureTestingModule({
      providers: [
        UnauthGuard,
        { provide: SessionService, useValue: sessionService },
        { provide: Router, useValue: router }
      ]
    });

    guard = TestBed.inject(UnauthGuard);
  });

  it('should allow activation when the user is not logged in', () => {
    sessionService.isLogged = false;

    expect(guard.canActivate()).toBe(true);
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should deny activation and redirect away when the user is already logged in', () => {
    sessionService.isLogged = true;

    expect(guard.canActivate()).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['sessions']);
  });
});
