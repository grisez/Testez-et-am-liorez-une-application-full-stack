import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { expect } from '@jest/globals';
import { of } from 'rxjs';

import { FormComponent } from './form.component';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { SessionService } from '../../../../core/service/session.service';
import { TeacherService } from '../../../../core/service/teacher.service';
import { Session } from '../../../../core/models/session.interface';
import { Teacher } from '../../../../core/models/teacher.interface';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let sessionApiService: { detail: jest.Mock; create: jest.Mock; update: jest.Mock };
  let router: { navigate: jest.Mock; url: string };
  let matSnackBar: { open: jest.Mock };

  const teachers: Teacher[] = [
    { id: 1, firstName: 'Margot', lastName: 'Delahaye', createdAt: new Date('2026-01-01'), updatedAt: new Date('2026-01-01') }
  ];

  const session: Session = {
    id: 1,
    name: 'Yoga du matin',
    description: 'Une session',
    date: new Date('2026-09-20'),
    teacher_id: 1,
    users: []
  };

  function createComponent(options: { admin: boolean; url: string }): void {
    sessionApiService = {
      detail: jest.fn().mockReturnValue(of(session)),
      create: jest.fn().mockReturnValue(of(session)),
      update: jest.fn().mockReturnValue(of(session))
    };
    router = { navigate: jest.fn(), url: options.url };
    matSnackBar = { open: jest.fn() };

    TestBed.configureTestingModule({
      imports: [FormComponent],
      providers: [
        { provide: SessionApiService, useValue: sessionApiService },
        { provide: SessionService, useValue: { sessionInformation: { admin: options.admin } } },
        { provide: TeacherService, useValue: { all: jest.fn().mockReturnValue(of(teachers)) } },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '1' } } } },
        { provide: Router, useValue: router }
      ]
    });
    TestBed.overrideProvider(MatSnackBar, { useValue: matSnackBar });

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('should create', () => {
    createComponent({ admin: true, url: '/sessions/create' });
    expect(component).toBeTruthy();
  });

  it('should redirect to /sessions when the user is not admin', () => {
    createComponent({ admin: false, url: '/sessions/create' });
    expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should create a session when the form is valid', () => {
    createComponent({ admin: true, url: '/sessions/create' });
    component.sessionForm?.setValue({ name: 'Yoga du soir', date: '2026-09-21', teacher_id: 1, description: 'Une autre session' });

    component.submit();

    expect(sessionApiService.create).toHaveBeenCalled();
    expect(matSnackBar.open).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
    expect(router.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should disable the submit button when a required field is missing (create mode)', () => {
    createComponent({ admin: true, url: '/sessions/create' });
    component.sessionForm?.setValue({ name: '', date: '', teacher_id: '', description: '' });
    fixture.detectChanges();

    const submitButton: HTMLButtonElement = fixture.nativeElement.querySelector('button[type="submit"]');
    expect(submitButton.disabled).toBe(true);
  });

  it('should load and update an existing session in update mode', () => {
    createComponent({ admin: true, url: '/sessions/update/1' });

    expect(sessionApiService.detail).toHaveBeenCalledWith('1');
    expect(component.sessionForm?.get('name')?.value).toBe('Yoga du matin');

    component.submit();

    expect(sessionApiService.update).toHaveBeenCalledWith('1', expect.anything());
    expect(matSnackBar.open).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
  });
});
