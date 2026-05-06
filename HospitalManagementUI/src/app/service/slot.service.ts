import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "../../environments/environment";
import { filterSlotRequest, slotRequest, slotResponse, slotResponseWithAppointmentIdFlag } from "../models/slot.models";
import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root',
})
export class SlotService {

  private readonly apiUrl: string = environment.apiUrl
  private readonly service: string = '/slot'
  private readonly serviceUrl: string = this.apiUrl + this.service

  constructor(private http: HttpClient) { }

  addSlot(payload: slotRequest): Observable<slotResponse> {
    return this.http.post<slotResponse>(
      `${this.serviceUrl}/doctor/add`, payload
    )
  }

  getSlots(): Observable<slotResponse[]> {
    return this.http.get<slotResponse[]>(
      `${this.serviceUrl}/doctor/doctor`
    );
  }

  updateSlot(payload: slotRequest): Observable<slotResponse> {
    return this.http.put<slotResponse>(
      `${this.serviceUrl}/doctor/update`, payload
    );
  }

  getSchedule(filter: filterSlotRequest): Observable<slotResponseWithAppointmentIdFlag[]> {
    return this.http.get<slotResponseWithAppointmentIdFlag[]>(
      `${this.serviceUrl}/patient/filter/user?doctorId=${filter.doctorId || ''}&date=${filter.date}`
    );
  }

  getScheduleForNewUser(filter: filterSlotRequest): Observable<slotResponse[]> {
    return this.http.get<slotResponse[]>(
      `${this.serviceUrl}/public/filter/v3?doctorId=${filter.doctorId || ''}&date=${filter.date}`
    );
  }

}