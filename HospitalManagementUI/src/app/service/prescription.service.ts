import { Observable } from "rxjs"
import { environment } from "../../environments/environment"
import { prescriptionRequest, prescriptionResponse } from "../models/prescription.models"
import { HttpClient } from "@angular/common/http"
import { Injectable } from "@angular/core"

@Injectable({
  providedIn: 'root'
})
export class PrescriptionService {
  private readonly apiUrl: string = environment.apiUrl
  private readonly service: string = '/prescription'
  private readonly serviceUrl: string = this.apiUrl + this.service

  constructor(private http: HttpClient) { }

  addPrescription(id: number, payload: prescriptionRequest): Observable<prescriptionResponse> {
      return this.http.post<prescriptionResponse>(
        `${this.serviceUrl}/doctor/add?appointmentId=${id}`, payload
      )
    }
}