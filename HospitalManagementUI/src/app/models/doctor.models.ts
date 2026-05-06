import { SlotStatus, Status } from "../shared/enum";
import { appointmentResponseWithPatientAndSlotAndDoctor } from "./appointment.models";
import { patientResponse } from "./patient.models";
import { documentResponse } from "./patient_document.models";
import { slotResponse } from "./slot.models";

export interface doctorRegisterRequest{
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string;
  specialization: string;
  licenseNumber: string;
}

export interface doctorResponse{
  id: number;
  firstName: string;
  lastName: string;
  phone: string;
  specialization: string;
  licenseNumber: string;
}

export interface doctorDetailsAndSlotListWithAppointmentListWithPatient {
  doctor: doctorResponse;
  slots: doctorSlotWithAppointments[]
}

export interface doctorSlotWithAppointments{
  id: number;
  date: Date;
  startTime: string;
  endTime: string;
  capacity: number;
  bookedCount: number;
  status: SlotStatus;
  appointments: doctorAppointment[]
}

export interface doctorAppointment{
  id: number;
  status: Status;
  reason: string;
  notes: string;
  patient: patientResponse;
}

export interface doctorAppointmentWithDetails {
  id: number;
  status: Status;
  reason: string;
  notes: string;
  date: Date;
  startTime: string;
  patient: patientResponse;
  patientDocuments: documentResponse[]
}

export interface doctorProfile{
  doctor: doctorResponse;
  slots: slotResponse[];
  appointments: doctorAppointmentWithDetails[];
}

export interface filterDoctorRequest {
  name: string;
  specialization: string;
}