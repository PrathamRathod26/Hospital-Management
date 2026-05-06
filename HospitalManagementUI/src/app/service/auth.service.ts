import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, catchError, Observable, tap } from 'rxjs';
import { auth, loginRequest, response } from '../models/user.models';
import { patientRegisterRequest, patientResponse } from '../models/patient.models';
import { doctorRegisterRequest, doctorResponse } from '../models/doctor.models';
import { environment } from '../../environments/environment';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly apiUrl: string = environment.apiUrl
  private readonly serviceUrl: string = this.apiUrl + '/user'

  private accessTokenSubject = new BehaviorSubject<string | null>(null);

  constructor(private http: HttpClient) { }

  patientRegisterV1(patientData: patientRegisterRequest): Observable<patientResponse> {
    return this.http.post<patientResponse>(`${this.serviceUrl}/patient/register/v1`, patientData);
  }

  patientRegisterV2(patientData: patientRegisterRequest, document: File): Observable<patientResponse> {

    const formdata = new FormData();

    const jsonBlob = new Blob([JSON.stringify(patientData)], {
      type: 'application/json'
    })

    formdata.append('data', jsonBlob)
    formdata.append('document', document)

    return this.http.post<patientResponse>(`${this.serviceUrl}/patient/register/v2`, formdata);
  }

  patientLogin(credentials: loginRequest): Observable<auth> {
    return this.http.post<auth>(`${this.serviceUrl}/patient/login`, credentials, {
      withCredentials: true
    }).pipe(
      tap(response => {
        if (response && response.accessToken) {
          this.accessTokenSubject.next(response.accessToken);
        }
      })
    );
  }

  doctorRegister(doctorData: doctorRegisterRequest): Observable<doctorResponse> {
    return this.http.post<doctorResponse>(`${this.serviceUrl}/doctor/register`, doctorData);
  }

  doctorLogin(credentials: loginRequest): Observable<auth> {
    return this.http.post<auth>(`${this.serviceUrl}/doctor/login`, credentials, {
      withCredentials: true
    }).pipe(
      tap(response => {
        if (response && response.accessToken) {
          this.accessTokenSubject.next(response.accessToken);
        }
      })
    );
  }

  staffRegister(staffData: loginRequest): Observable<response> {
    return this.http.post<response>(
      `${this.serviceUrl}/staff/register`, staffData
    )
  }

  staffLogin(credentials: loginRequest): Observable<auth> {
    return this.http.post<auth>(`${this.serviceUrl}/staff/login`, credentials, {
      withCredentials: true
    }).pipe(
      tap(response => {
        if (response && response.accessToken) {
          this.accessTokenSubject.next(response.accessToken);
        }
      })
    );
  }

  refreshToken(): Observable<auth> {
    return this.http.post<auth>(`${this.serviceUrl}/refresh`, {}, {
      withCredentials: true
    }).pipe(
      tap(response => {
        if (response && response.accessToken) {
          this.accessTokenSubject.next(response.accessToken);
        }
      })
    );
  }

  logout(): Observable<any> {
    return this.http.post(`${this.serviceUrl}/logout`, {}, {
      withCredentials: true
    }).pipe(
      tap(() => {
        this.accessTokenSubject.next(null);
      }),
      catchError(err => {
        this.accessTokenSubject.next(null);
        return err;
      })
    );
  }

  getAccessToken(): string | null {
    return this.accessTokenSubject.value;
  }

  getuserId(): number | null {
    const token = this.accessTokenSubject.value;
    if (!token) return null;
    try {
      const payloadBase64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      const payload = JSON.parse(payloadBase64);;
      return payload.userId;
    } catch (e) {
      return null;
    }
  }

  getRole(): string | null {
    const token = this.accessTokenSubject.value;
    if (!token) return null;
    try {
      const payloadBase64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      const decodedJson = atob(payloadBase64);
      const payload = JSON.parse(decodedJson);

      return payload.role || null;
    } catch (e) {
      console.error("Failed to decode token", e);
      return null;
    }
  }

  isLoggedIn(): boolean {
    return !!this.getAccessToken();
  }
}
