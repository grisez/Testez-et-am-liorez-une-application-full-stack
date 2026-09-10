import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { expect } from '@jest/globals';
import { of, throwError } from 'rxjs';

import { LoginComponent } from './login.component';
import { AuthService } from '../../core/service/auth.service';
import { SessionService } from '../../core/service/session.service';
import { SessionInformation } from '../../core/models/sessionInformation.interface';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: { login: jest.Mock };
  let sessionService: { logIn: jest.Mock };
  let router: { navigate: jest.Mock };

  const sessionInformation: SessionInformation = {
    token: 'fake-token',
    type: 'Bearer',
    id: 1,
    username: 'margot@teacher.com',
    firstName: 'Margot',
    lastName: 'Delahaye',
    admin: false
  };

  beforeEach(async () => {
    authService = { login: jest.fn() };
    sessionService = { logIn: jest.fn() };
    router = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [
        LoginComponent,
        BrowserAnimationsModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule
      ],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: SessionService, useValue: sessionService },
        { provide: Router, useValue: router }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should log in and navigate to /sessions when credentials are valid', () => {
    // given
    authService.login.mockReturnValue(of(sessionInformation));
    component.form.setValue({ email: 'margot@teacher.com', password: 'password123' });

    // when
    component.submit();

    // then
    expect(sessionService.logIn).toHaveBeenCalledWith(sessionInformation);
    expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
    expect(component.onError).toBe(false);
  });

  it('should display an error message when login fails', () => {
    // given
    authService.login.mockReturnValue(throwError(() => new Error('Invalid credentials')));
    component.form.setValue({ email: 'margot@teacher.com', password: 'wrong-password' });

    // when
    component.submit();
    fixture.detectChanges();

    // then
    expect(component.onError).toBe(true);
    const errorMessage = fixture.nativeElement.querySelector('.error');
    expect(errorMessage).toBeTruthy();
    expect(errorMessage.textContent).toContain('An error occurred');
  });

  it('should disable the submit button when a required field is missing', () => {
    // given
    component.form.setValue({ email: '', password: '' });

    // when
    fixture.detectChanges();

    // then
    const submitButton: HTMLButtonElement = fixture.nativeElement.querySelector('button[type="submit"]');
    expect(submitButton.disabled).toBe(true);
  });
});
