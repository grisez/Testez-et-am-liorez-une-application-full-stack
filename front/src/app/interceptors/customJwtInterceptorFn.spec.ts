import { TestBed } from '@angular/core/testing';
import { HttpEvent, HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { expect, jest } from '@jest/globals';
import { of } from 'rxjs';

import { customJwtInterceptorFn } from './customJwtInterceptorFn';
import { SessionService } from '../core/service/session.service';
import { SessionInformation } from '../core/models/sessionInformation.interface';

describe('customJwtInterceptorFn', () => {
  let sessionService: { isLogged: boolean; sessionInformation: SessionInformation | undefined };

  beforeEach(() => {
    sessionService = { isLogged: false, sessionInformation: undefined };

    TestBed.configureTestingModule({
      providers: [
        { provide: SessionService, useValue: sessionService }
      ]
    });
  });

  function runInterceptor(request: HttpRequest<unknown>) {
    const next = jest.fn<HttpHandlerFn>().mockReturnValue(of({} as HttpEvent<unknown>));
    TestBed.runInInjectionContext(() =>
      customJwtInterceptorFn(request, next)
    );
    return next;
  }

  it('should forward the request unchanged when the user is not logged in', () => {
    const request = new HttpRequest('GET', '/api/session');

    const next = runInterceptor(request);

    expect(next).toHaveBeenCalledTimes(1);
    const forwardedRequest = next.mock.calls[0][0] as HttpRequest<unknown>;
    expect(forwardedRequest.headers.has('Authorization')).toBe(false);
  });

  it('should add a Bearer Authorization header with the session token when the user is logged in', () => {
    sessionService.isLogged = true;
    sessionService.sessionInformation = {
      token: 'abc123',
      type: 'Bearer',
      id: 1,
      username: 'yoga@studio.com',
      firstName: 'Margot',
      lastName: 'Delahaye',
      admin: false
    };
    const request = new HttpRequest('GET', '/api/session');

    const next = runInterceptor(request);

    expect(next).toHaveBeenCalledTimes(1);
    const forwardedRequest = next.mock.calls[0][0] as HttpRequest<unknown>;
    expect(forwardedRequest.headers.get('Authorization')).toBe('Bearer abc123');
  });
});
