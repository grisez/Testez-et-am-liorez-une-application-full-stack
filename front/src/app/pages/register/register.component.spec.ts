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

import { RegisterComponent } from './register.component';
import { AuthService } from '../../core/service/auth.service';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: { register: jest.Mock };
  let router: { navigate: jest.Mock };

  beforeEach(async () => {
    authService = { register: jest.fn() };
    router = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [
        RegisterComponent,
        BrowserAnimationsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        ReactiveFormsModule
      ],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should register and navigate to /login when the form is valid', () => {
    // given
    authService.register.mockReturnValue(of(undefined));
    component.form.setValue({
      email: 'new.teacher@mail.com',
      firstName: 'Prof',
      lastName: 'Nouveau',
      password: 'password123'
    });

    // when
    component.submit();

    // then
    expect(authService.register).toHaveBeenCalledWith({
      email: 'new.teacher@mail.com',
      firstName: 'Prof',
      lastName: 'Nouveau',
      password: 'password123'
    });
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
    expect(component.onError).toBe(false);
  });

  it('should display an error message when registration fails', () => {
    // given
    authService.register.mockReturnValue(throwError(() => new Error('Email is already taken!')));
    component.form.setValue({
      email: 'existing@mail.com',
      firstName: 'Prof',
      lastName: 'Nouveau',
      password: 'password123'
    });

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
    component.form.setValue({ email: '', firstName: '', lastName: '', password: '' });

    // when
    fixture.detectChanges();

    // then
    const submitButton: HTMLButtonElement = fixture.nativeElement.querySelector('button[type="submit"]');
    expect(submitButton.disabled).toBe(true);
  });
});
