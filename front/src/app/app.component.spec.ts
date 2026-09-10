import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { expect } from '@jest/globals';

import { AppComponent } from './app.component';
import { AuthService } from './core/service/auth.service';
import { SessionService } from './core/service/session.service';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let sessionService: { logOut: jest.Mock; $isLogged: jest.Mock };
  let router: { navigate: jest.Mock };

  beforeEach(async () => {
    sessionService = { logOut: jest.fn(), $isLogged: jest.fn() };
    router = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [
        { provide: AuthService, useValue: {} },
        { provide: SessionService, useValue: sessionService },
        { provide: Router, useValue: router }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should log out and navigate to home when logout is called', () => {
    component.logout();

    expect(sessionService.logOut).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['']);
  });
});
