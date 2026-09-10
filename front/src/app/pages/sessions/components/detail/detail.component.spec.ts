import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { expect } from '@jest/globals';
import { of } from 'rxjs';

import { DetailComponent } from './detail.component';
import { SessionApiService } from '../../../../core/service/session-api.service';
import { SessionService } from '../../../../core/service/session.service';
import { TeacherService } from '../../../../core/service/teacher.service';
import { Session } from '../../../../core/models/session.interface';
import { Teacher } from '../../../../core/models/teacher.interface';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let sessionApiService: {
    detail: jest.Mock;
    delete: jest.Mock;
    participate: jest.Mock;
    unParticipate: jest.Mock;
  };
  let router: { navigate: jest.Mock };
  let matSnackBar: { open: jest.Mock };

  const teacher: Teacher = { id: 1, firstName: 'Margot', lastName: 'Delahaye', createdAt: new Date('2026-01-01'), updatedAt: new Date('2026-01-01') };

  function createComponent(admin: boolean, session: Session): void {
    sessionApiService = {
      detail: jest.fn().mockReturnValue(of(session)),
      delete: jest.fn().mockReturnValue(of(undefined)),
      participate: jest.fn().mockReturnValue(of(undefined)),
      unParticipate: jest.fn().mockReturnValue(of(undefined))
    };
    router = { navigate: jest.fn() };
    matSnackBar = { open: jest.fn() };

    TestBed.configureTestingModule({
      imports: [DetailComponent],
      providers: [
        { provide: SessionApiService, useValue: sessionApiService },
        { provide: SessionService, useValue: { sessionInformation: { admin, id: 2 } } },
        { provide: TeacherService, useValue: { detail: jest.fn().mockReturnValue(of(teacher)) } },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '1' } } } },
        { provide: Router, useValue: router }
      ]
    });
    // MatSnackBar is provided again via `overrideProvider`: MaterialModule (imported by
    // DetailComponent) pulls in MatSnackBarModule, which otherwise wins over the plain
    // `providers` entry above and creates a real overlay that jsdom can't render.
    TestBed.overrideProvider(MatSnackBar, { useValue: matSnackBar });

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('should create', () => {
    createComponent(true, { id: 1, name: 'Yoga du matin', description: 'Une session', date: new Date('2026-09-20'), teacher_id: 1, users: [] });
    expect(component).toBeTruthy();
  });

  it('should display the session information', () => {
    const session: Session = { id: 1, name: 'Yoga du matin', description: 'Une session bien relaxante', date: new Date('2026-09-20'), teacher_id: 1, users: [] };
    createComponent(true, session);

    const content = fixture.nativeElement.textContent;
    // session.name goes through the `titlecase` pipe in the template
    expect(content).toContain('Yoga Du Matin');
    expect(content).toContain('Une session bien relaxante');
    expect(content).toContain('Margot');
  });

  it('should show the Delete button when the user is admin', () => {
    createComponent(true, { id: 1, name: 'Yoga du matin', description: 'Une session', date: new Date('2026-09-20'), teacher_id: 1, users: [] });

    const buttons: HTMLButtonElement[] = Array.from(fixture.nativeElement.querySelectorAll('button'));
    expect(buttons.some((b) => b.textContent?.includes('Delete'))).toBe(true);
  });

  it('should show the Participate button when the user is not admin and not yet participating', () => {
    createComponent(false, { id: 1, name: 'Yoga du matin', description: 'Une session', date: new Date('2026-09-20'), teacher_id: 1, users: [] });

    const buttons: HTMLButtonElement[] = Array.from(fixture.nativeElement.querySelectorAll('button'));
    expect(buttons.some((b) => b.textContent?.includes('Participate'))).toBe(true);
    expect(buttons.some((b) => b.textContent?.includes('Delete'))).toBe(false);
  });

  it('should delete the session and navigate to the sessions list', () => {
    createComponent(true, { id: 1, name: 'Yoga du matin', description: 'Une session', date: new Date('2026-09-20'), teacher_id: 1, users: [] });

    component.delete();

    expect(sessionApiService.delete).toHaveBeenCalledWith('1');
    expect(matSnackBar.open).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['sessions']);
  });
});
