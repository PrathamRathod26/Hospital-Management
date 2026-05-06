import { BloodGroup, Gender, Status } from "../shared/enum";
import { draftResponse } from "./appointment.models";
import { doctorResponse } from "./doctor.models";
import { documentResponse, documentResponseWithUrl } from "./patient_document.models";
import { prescriptionBase } from "./prescription.models";

export interface patientRegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string;
  age: number;
  gender: Gender;
  bloodGroup: BloodGroup;
  emergencyContact: string;
};

export interface patientResponse {
  id: number;
  firstName: string;
  lastName: string;
  phone: string;
  age: number;
  gender: Gender;
  bloodGroup: BloodGroup;
  emergencyContact: string;
}

export interface patientDetailsWithAppointmentListWithDoctor {
  patient: patientResponse;
  appointments: patientAppointmentWithDoctor[]
}

interface patientAppointmentWithDoctor {
  id: number;
  reason: string;
  notes: string;
  status: Status;
  date: Date;
  startTime: string;
  doctor: doctorResponse;
  prescription: prescriptionBase;
}

export interface patientDetailsWithDocumentDataAndAppointmentData {
  patient: patientResponse;
  documents: documentResponseWithUrl[];
  appointment: patientDetailAppointment;
}

interface patientDetailAppointment {
  id: number;
  reason: string;
  notes: string;
  status: Status;
  date: Date;
  startTime: string;
  prescription: prescriptionBase;
}

export interface patientProfile {
  email: string;
  patient: patientResponse;
  documents: documentResponseWithUrl[];
  appointments: patientProfileAppointment[];
  drafts: draftResponse[];
}

export interface patientProfileAppointment {
  id: number;
  reason: string;
  notes: string;
  status: Status;
  date: Date;
  startTime: string;
  doctor: doctorResponse;
  prescription: prescriptionBase;
}