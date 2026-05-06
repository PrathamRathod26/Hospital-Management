import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { patientDetailsWithDocumentDataAndAppointmentData, patientProfile, patientResponse } from "../models/patient.models";
import { environment } from "../../environments/environment";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private readonly apiUrl: string = environment.apiUrl
    private readonly service: string = '/patient'
    private readonly serviceUrl: string = this.apiUrl + this.service

  constructor(
    private http: HttpClient
  ) { }

  loadPatient(): Observable<patientResponse> {
    return this.http.get<patientResponse>(
      `${this.serviceUrl}/`
    )
  }

  loadPatientDetails(patientId: string, appointmentId: string): Observable<patientDetailsWithDocumentDataAndAppointmentData> {
    return this.http.get<patientDetailsWithDocumentDataAndAppointmentData>(
      `${this.serviceUrl}/view/details/v2?patientId=${patientId}&appointmentId=${appointmentId}`
    )
  }

  loadPatientProfile(): Observable<patientProfile> {
    return this.http.get<patientProfile>(
      `${this.serviceUrl}/profile`
    )
  }
}