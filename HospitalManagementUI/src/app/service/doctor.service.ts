import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { slotRequest, slotResponse } from '../models/slot.models';
import { doctorProfile, doctorResponse, filterDoctorRequest } from '../models/doctor.models';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class DoctorService {
  private doctorUrl: string = 'http://localhost:8080/api/doctor'
  private slotUrl: string = 'http://localhost:8080/api/slot'

  private readonly apiUrl: string = environment.apiUrl
  private readonly service: string = '/doctor'
  private readonly serviceUrl: string = this.apiUrl + this.service

  constructor(private http: HttpClient) { }

  loadDoctorProfile(): Observable<doctorProfile> {
    return this.http.get<doctorProfile>(
      `${this.serviceUrl}/profile`
    );
  }

  filterDoctors(filters: filterDoctorRequest): Observable<doctorResponse[]> {
    return this.http.get<doctorResponse[]>(
      `${this.serviceUrl}/filtered/v3?name=${filters.name || ''}&specialization=${filters.specialization || ''}`
    );
  }

}
