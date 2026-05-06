import { Routes } from '@angular/router';
import { doctorGuard, guestGuard, patientGuard, staffGuard } from './core/auth/auth.guard';

import { HomePage } from './component/home-page/home-page';

import { PatientAuth } from './component/patient-auth/patient-auth';
import { DoctorAuth } from './component/doctor-auth/doctor-auth';
import { StaffAuth } from './component/staff-auth/staff-auth';

import { PatientProfile } from './component/patient-profile/patient-profile';
import { PatientAppointments } from './component/patient-appointments/patient-appointments';
import { MakeAppointment } from './component/make-appointment/make-appointment';
import { DraftAppointment } from './component/draft-appointment/draft-appointment';

import { Slot } from './component/slot/slot';
import { DoctorProfile } from './component/doctor-profile/doctor-profile';
import { PatientDetails } from './component/patient-details/patient-details';
import { NewUserAppointment } from './component/new-user-appointment/new-user-appointment';
import { DoctorAppointments } from './component/doctor-appointments/doctor-appointments';


export const routes: Routes = [
  {path: '', component: HomePage},

  {path: 'doctor/auth', component: DoctorAuth, canActivate: [guestGuard]},
  {path: 'patient/auth', component: PatientAuth, canActivate: [guestGuard]},
  {path: 'staff/auth', component: StaffAuth, canActivate: [guestGuard]},
  
  {path: 'patient/make-appointment/new', component: NewUserAppointment, canActivate: [staffGuard]},

  {path: 'patient/profile', component: PatientProfile, canActivate: [patientGuard]},
  {path: 'patient/appointments', component: PatientAppointments, canActivate: [patientGuard]},
  {path: 'patient/make-appointment', component: MakeAppointment, canActivate: [patientGuard]},
  {path: 'patient/draft', component: DraftAppointment, canActivate: [patientGuard]},

  {path: 'doctor/slot', component: Slot, canActivate: [doctorGuard]},
  {path: 'doctor/profile', component: DoctorProfile, canActivate:[doctorGuard]},
  {path: 'patient-details/:patientId/:appointmentId', component: PatientDetails, canActivate: [doctorGuard]},
  {path: 'doctor/appointments', component: DoctorAppointments, canActivate: [doctorGuard]},
];
