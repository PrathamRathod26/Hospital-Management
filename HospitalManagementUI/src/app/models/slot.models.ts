import { SlotStatus } from "../shared/enum";
import { doctorResponse } from "./doctor.models";

export interface slotRequest {
  date: Date;
  startTime: string;
  endTime: string;
  bookedCount: number;
  capacity: number;
  status: SlotStatus;
}

export interface slotResponse{
  id: number;
  date: Date;
  startTime: string;
  endTime: string;
  bookedCount: number;
  capacity: number;
  status: SlotStatus;
}

export interface filterSlotRequest {
  doctorId: number;
  date: string;
}

export interface slotResponse {
  id: number;
  doctor: doctorResponse;
  date: Date;
  startTime: string;
  endTime: string;
  bookedCount: number;
  capacity: number;
  status: SlotStatus;
}

export interface slotResponseWithAppointmentIdFlag {
  id: number;
  date: Date;
  bookedCount: number;
  capacity: number;
  startTime: string;
  endTime: string;
  doctorId: number;
  status: SlotStatus;
  appointmentId: number | null;
}
