import { HttpClient, HttpEvent } from "@angular/common/http";
import { Observable } from "rxjs";
import { patientDetailsWithDocumentDataAndAppointmentData, patientProfile, patientResponse } from "../models/patient.models";
import { documentResponse } from "../models/patient_document.models";
import { environment } from "../../environments/environment";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class PatientDocumentService {
  private readonly apiUrl: string = environment.apiUrl
      private readonly service: string = '/document'
      private readonly serviceUrl: string = this.apiUrl + this.service

  constructor(
    private http: HttpClient
  ) { }

  viewDocument(documentId: number) {
    return this.http.get(`${this.serviceUrl}/authenticated/view?documentId=${documentId}`, {
      responseType: 'blob'
    });
  }

  patientUploadDocument(file: File): Observable<HttpEvent<any>> {
    const formData = new FormData();
    formData.append('document', file);

    return this.http.post(`${this.serviceUrl}/add`, formData, {
      reportProgress: true,
      observe: 'events',
      responseType: 'text'
    });
  }

  patientUploadMultipleDocument(files: File[]): Observable<HttpEvent<any>> {
    const formData = new FormData();
    files.forEach(file => {
      formData.append('documents', file);
    })
    return this.http.post(`${this.serviceUrl}/add-multiple`, formData, {
      reportProgress: true,
      observe: 'events',
      responseType: 'text'
    });
  }

  loadDocument(): Observable<documentResponse[]> {
    return this.http.get<documentResponse[]>(
      `${this.serviceUrl}/patient`
    )
  }
}