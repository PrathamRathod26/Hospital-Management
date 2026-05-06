import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { doctorDetailsAndSlotListWithAppointmentListWithPatient } from '../models/doctor.models';
import { appointmentDraftRequestV1, appointmentDraftRequestV2, appointmentNewUserRequest, appointmentRequestV1, appointmentRequestV2, appointmentResponseWithPatientAndSlotAndDoctor } from '../models/appointment.models';
import { patientDetailsWithAppointmentListWithDoctor } from '../models/patient.models';
import { documentResponse } from '../models/patient_document.models';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AppointmentService {
  private readonly apiUrl: string = environment.apiUrl
  private readonly serviceUrl: string = this.apiUrl + '/appointment'

  constructor(private http: HttpClient) { }

  makeAppointmentV1(payload: appointmentRequestV1): Observable<appointmentResponseWithPatientAndSlotAndDoctor> {
    return this.http.post<appointmentResponseWithPatientAndSlotAndDoctor>(
      `${this.serviceUrl}/add/v1`, payload, {}
    )
  }

  makeAppointmentV2(payload: appointmentRequestV2): Observable<appointmentResponseWithPatientAndSlotAndDoctor> {
    return this.http.post<appointmentResponseWithPatientAndSlotAndDoctor>(
      `${this.serviceUrl}/patient/add/v2`, payload, {}
    )
  }

  makeAppointmentV3(data: appointmentRequestV1, files: File[]) {
    const formData = new FormData();
    const jsonBlob = new Blob(
      [JSON.stringify(data)],
      { type: 'application/json' }
    );

    formData.append('data', jsonBlob);
    if (files?.length) {
      files.forEach(file => {
        formData.append('documents', file);
      });
    }
    return this.http.post<appointmentResponseWithPatientAndSlotAndDoctor>(
      `${this.serviceUrl}/patient/add/v3`, formData, {}
    )
  }

  makeAppointmentDraftV1(payload: appointmentDraftRequestV1): Observable<appointmentResponseWithPatientAndSlotAndDoctor> {
    return this.http.post<appointmentResponseWithPatientAndSlotAndDoctor>(
      `${this.serviceUrl}/patient/add/draft/v1`, payload, {}
    )
  }

  makeAppointmentDraftV2(payload: appointmentDraftRequestV2, files: File[]) {
    const formData = new FormData();
    const jsonBlob = new Blob(
      [JSON.stringify(payload)],
      { type: 'application/json' }
    );

    formData.append('data', jsonBlob);
    if (files?.length) {
      files.forEach(file => {
        formData.append('documents', file);
      });
    }
    return this.http.post<appointmentResponseWithPatientAndSlotAndDoctor>(
      `${this.serviceUrl}/patient/add/draft/v2`, formData, {}
    )
  }

  makeAppointmentForNewUserV1(payload: appointmentNewUserRequest): Observable<appointmentResponseWithPatientAndSlotAndDoctor> {
    return this.http.post<appointmentResponseWithPatientAndSlotAndDoctor>(
      `${this.serviceUrl}/staff/add/v1`, payload, {}
    )
  }

  makeAppointmentForNewUserV2(payload: appointmentNewUserRequest, files: File[]): Observable<appointmentResponseWithPatientAndSlotAndDoctor> {
    const formData = new FormData();
    const jsonBlob = new Blob(
      [JSON.stringify(payload)],
      { type: 'application/json' }
    );

    formData.append('data', jsonBlob);
    if (files?.length) {
      files.forEach(file => {
        formData.append('documents', file);
      });
    }
    return this.http.post<appointmentResponseWithPatientAndSlotAndDoctor>(
      `${this.serviceUrl}/staff/add/v2`, formData, {}
    )
  }

  getPatientAllAppointments(): Observable<patientDetailsWithAppointmentListWithDoctor> {
    return this.http.get<patientDetailsWithAppointmentListWithDoctor>(
      `${this.serviceUrl}/patient/v3`
    );
  }

  getDoctorAllAppointments(): Observable<doctorDetailsAndSlotListWithAppointmentListWithPatient> {
    return this.http.get<doctorDetailsAndSlotListWithAppointmentListWithPatient>(
      `${this.serviceUrl}/doctor/v3`
    );
  }

  getAppointment(id: string): Observable<appointmentResponseWithPatientAndSlotAndDoctor> {
    return this.http.get<appointmentResponseWithPatientAndSlotAndDoctor>(
      `${this.serviceUrl}/patient/appointment?appointmentId=${id}`
    )
  }

  getAttachedDocument(appointmentId: string): Observable<documentResponse[]> {
    return this.http.get<documentResponse[]>(
      `${this.serviceUrl}/authenticated/documents?appointmentId=${appointmentId}`
    )
  }

  removeAttachedDocument(appointmentId: number, patientDocumentId: number) {
    return this.http.delete(
      `${this.serviceUrl}/patient/remove/document?appointmentId=${appointmentId}&patientDocumentId=${patientDocumentId}`
    )
  }

  updateDraftAppointment(data: appointmentDraftRequestV2, files: File[], appointmentId: number) {
    const formData = new FormData();
    const jsonBlob = new Blob(
      [JSON.stringify(data)],
      { type: 'application/json' }
    );

    formData.append('data', jsonBlob);
    if (files?.length) {
      files.forEach(file => {
        formData.append('documents', file);
      });
    }
    return this.http.put<appointmentResponseWithPatientAndSlotAndDoctor>(
      `${this.serviceUrl}/patient/update-draft?appointmentId=${appointmentId}`, formData, {}
    )
  }
}
