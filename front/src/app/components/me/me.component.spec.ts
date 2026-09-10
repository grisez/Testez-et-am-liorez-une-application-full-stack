import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { expect } from '@jest/globals';
import { of } from 'rxjs';

import { MeComponent } from './me.component';
import { SessionService } from '../../core/service/session.service';
import { UserService } from '../../core/service/user.service';
import { User } from '../../core/models/user.interface';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let userService: { getById: jest.Mock; delete: jest.Mock };
  let sessionService: { sessionInformation: { id: number }; logOut: jest.Mock };
  let router: { navigate: jest.Mock };
  let matSnackBar: { open: jest.Mock };

  function createComponent(user: User): void {
    userService = {
      getById: jest.fn().mockReturnValue(of(user)),
      delete: jest.fn().mockReturnValue(of(undefined))
    };
    sessionService = { sessionInformation: { id: 1 }, logOut: jest.fn() };
    router = { navigate: jest.fn() };
    matSnackBar = { open: jest.fn() };

    TestBed.configureTestingModule({
      imports: [MeComponent],
      providers: [
        { provide: UserService, useValue: userService },
        { provide: SessionService, useValue: sessionService },
        { provide: Router, useValue: router }
      ]
    });
    TestBed.overrideProvider(MatSnackBar, { useValue: matSnackBar });

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  const nonAdminUser: User = {
    id: 1,
    email: 'margot@teacher.com',
    lastName: 'Delahaye',
    firstName: 'Margot',
    admin: false,
    password: 'encoded',
    createdAt: new Date('2026-01-01'),
    updatedAt: new Date('2026-01-01')
  };

  it('should create', () => {
    createComponent(nonAdminUser);
    expect(component).toBeTruthy();
  });

  it('should display the user information', () => {
    createComponent(nonAdminUser);

    const content = fixture.nativeElement.textContent;
    expect(content).toContain('Margot');
    expect(content).toContain('margot@teacher.com');
  });

  it('should show "You are admin" and hide the delete button for an admin user', () => {
    createComponent({ ...nonAdminUser, admin: true });

    const content = fixture.nativeElement.textContent;
    expect(content).toContain('You are admin');
    expect(fixture.nativeElement.querySelector('button[color="warn"]')).toBeFalsy();
  });

  it('should delete the account, log out and navigate to home for a non-admin user', () => {
    createComponent(nonAdminUser);

    component.delete();

    expect(userService.delete).toHaveBeenCalledWith('1');
    expect(matSnackBar.open).toHaveBeenCalled();
    expect(sessionService.logOut).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });
});
