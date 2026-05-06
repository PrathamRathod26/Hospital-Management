import { appointmentResponseWithPatientAndSlotAndDoctor } from "./appointment.models";

export interface prescriptionRequest{
  diagnosis: string;
  medicationNotes:string;
  advice: string;
}

export interface prescriptionBase{
  id: number;
  diagnosis: string;
  medicationNotes: string;
  advice: string;
}

export interface prescriptionResponse{
  id: number;
  diagnosis: string;
  medicationNotes: string;
  advice: string;
  appointment: appointmentResponseWithPatientAndSlotAndDoctor;
}