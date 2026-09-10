import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';
import { Session } from '../models/session.interface';

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  const session: Session = {
    id: 1,
    name: 'Hatha Yoga',
    description: 'Une séance douce',
    date: new Date('2026-09-20'),
    teacher_id: 1,
    users: []
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should send a GET request to fetch all sessions', () => {
    service.all().subscribe((response) => {
      expect(response).toEqual([session]);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('GET');
    req.flush([session]);
  });

  it('should send a GET request to fetch a session by id', () => {
    service.detail('1').subscribe((response) => {
      expect(response).toEqual(session);
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('GET');
    req.flush(session);
  });

  it('should send a DELETE request to delete a session', () => {
    service.delete('1').subscribe();

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should send a POST request to create a session', () => {
    service.create(session).subscribe((response) => {
      expect(response).toEqual(session);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(session);
    req.flush(session);
  });

  it('should send a PUT request to update a session', () => {
    service.update('1', session).subscribe((response) => {
      expect(response).toEqual(session);
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(session);
    req.flush(session);
  });

  it('should send a POST request to participate in a session', () => {
    service.participate('1', '2').subscribe();

    const req = httpMock.expectOne('api/session/1/participate/2');
    expect(req.request.method).toBe('POST');
    req.flush(null);
  });

  it('should send a DELETE request to no longer participate in a session', () => {
    service.unParticipate('1', '2').subscribe();

    const req = httpMock.expectOne('api/session/1/participate/2');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
