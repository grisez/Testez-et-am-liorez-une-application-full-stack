import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';

import { ListComponent } from './list.component';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { SessionService } from '../../../../core/service/session.service';
import { Session } from '../../../../core/models/session.interface';

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;

  const sessions: Session[] = [
    { id: 1, name: 'Yoga du matin', description: 'Une session', date: new Date('2026-09-20'), teacher_id: 1, users: [] }
  ];

  function findButtonByText(text: string): HTMLButtonElement | undefined {
    const buttons: HTMLButtonElement[] = Array.from(fixture.nativeElement.querySelectorAll('button'));
    return buttons.find((b) => b.textContent?.includes(text));
  }

  function createComponent(admin: boolean): void {
    const mockSessionApiService = { all: jest.fn().mockReturnValue(of(sessions)) };
    const mockSessionService = { sessionInformation: { admin } };

    TestBed.configureTestingModule({
      imports: [ListComponent, MatCardModule, MatIconModule, RouterTestingModule],
      providers: [
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: SessionService, useValue: mockSessionService }
      ]
    });

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('should create', () => {
    createComponent(true);
    expect(component).toBeTruthy();
  });

  it('should display the list of sessions', () => {
    createComponent(true);
    const cards = fixture.nativeElement.querySelectorAll('mat-card.item');
    expect(cards.length).toBe(1);
    expect(cards[0].textContent).toContain('Yoga du matin');
  });

  it('should show the Create button when the user is admin', () => {
    createComponent(true);
    expect(findButtonByText('Create')).toBeTruthy();
  });

  it('should hide the Create button when the user is not admin', () => {
    createComponent(false);
    expect(findButtonByText('Create')).toBeFalsy();
  });

  it('should show the Edit button when the user is admin', () => {
    createComponent(true);
    expect(findButtonByText('Edit')).toBeTruthy();
  });

  it('should hide the Edit button when the user is not admin', () => {
    createComponent(false);
    expect(findButtonByText('Edit')).toBeFalsy();
  });

  it('should show the Detail button when the user is admin', () => {
    createComponent(true);
    expect(findButtonByText('Detail')).toBeTruthy();
  });

  it('should show the Detail button when the user is not admin', () => {
    createComponent(false);
    expect(findButtonByText('Detail')).toBeTruthy();
  });
});
