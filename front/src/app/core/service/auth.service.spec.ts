import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { AuthService } from './auth.service';
import { LoginRequest } from '../models/loginRequest.interface';
import { RegisterRequest } from '../models/registerRequest.interface';
import { SessionInformation } from '../models/sessionInformation.interface';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should send a POST request to /api/auth/login', () => {
    const loginRequest: LoginRequest = { email: 'test@yoga.com', password: 'password123' };
    const sessionInformation: SessionInformation = {
      token: 'fake-token',
      type: 'Bearer',
      id: 1,
      username: 'test@yoga.com',
      firstName: 'Test',
      lastName: 'User',
      admin: false
    };

    service.login(loginRequest).subscribe((response) => {
      expect(response).toEqual(sessionInformation);
    });

    const req = httpMock.expectOne('/api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(loginRequest);
    req.flush(sessionInformation);
  });

  it('should propagate a 401 error when login credentials are invalid', () => {
    const loginRequest: LoginRequest = { email: 'test@yoga.com', password: 'wrong-password' };

    service.login(loginRequest).subscribe({
      next: () => fail('expected an error, not a successful response'),
      error: (error) => {
        expect(error.status).toBe(401);
      }
    });

    const req = httpMock.expectOne('/api/auth/login');
    req.flush({ message: 'Invalid credentials' }, { status: 401, statusText: 'Unauthorized' });
  });

  it('should send a POST request to /api/auth/register', () => {
    const registerRequest: RegisterRequest = {
      email: 'new.user@yoga.com',
      firstName: 'New',
      lastName: 'User',
      password: 'password123'
    };

    service.register(registerRequest).subscribe();

    const req = httpMock.expectOne('/api/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(registerRequest);
    req.flush(null);
  });

  it('should propagate a 400 error when the email is already taken', () => {
    const registerRequest: RegisterRequest = {
      email: 'existing@yoga.com',
      firstName: 'Existing',
      lastName: 'User',
      password: 'password123'
    };

    service.register(registerRequest).subscribe({
      next: () => fail('expected an error, not a successful response'),
      error: (error) => {
        expect(error.status).toBe(400);
      }
    });

    const req = httpMock.expectOne('/api/auth/register');
    req.flush({ message: 'Email is already taken!' }, { status: 400, statusText: 'Bad Request' });
  });
});
