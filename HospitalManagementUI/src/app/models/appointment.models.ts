import { BloodGroup, Gender, Status } from "../shared/enum";
import { doctorResponse } from "./doctor.models";
import { patientResponse } from "./patient.models";
import { documentResponse } from "./patient_document.models";
import { slotResponse } from "./slot.models";

export interface appointmentRequestV1 {
  doctorId: number;
  slotId: number;
  reason: string;
  notes: string;
}

export interface appointmentRequestV2 {
  doctorId: number;
  slotId: number;
  reason: string;
  notes: string;
  documentIds: number[];
}

export interface appointmentDraftRequestV2{
  reason: string;
  notes: string;
}

export interface appointmentDraftRequestV1 {
  reason: string;
  notes: string;
  documentIds: number[];
}

export interface appointmentNewUserRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string;
  age: number;
  gender: Gender;
  bloodGroup: BloodGroup;
  emergencyContact: string;
  doctorId: number;
  slotId: number;
  reason: string;
  notes: string;
}

export interface appointmentResponseWithPatientAndSlotAndDoctor {
  id: number;
  patient: patientResponse;
  doctor: doctorResponse;
  slot: slotResponse;
  status: Status;
  reason: string;
  notes: string;
}

export interface draftResponse{
  id: number;
  status: Status;
  reason: string;
  notes: string;
  draft: boolean;
  documents: documentResponse[];
}