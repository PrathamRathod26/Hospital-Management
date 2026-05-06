import { Component, OnInit, signal } from '@angular/core';
import { MaterialModule } from '../../material/material-module';
import { AppointmentService } from '../../service/appointment.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RouterLink } from "@angular/router";
import { BloodGroupLabel } from '../../shared/enum';
import { patientDetailsWithAppointmentListWithDoctor } from '../../models/patient.models';
import { DocumentDialog } from '../document-dialog/document-dialog';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-patient-appointments',
  imports: [MaterialModule, RouterLink],
  templateUrl: './patient-appointments.html',
  styleUrl: './patient-appointments.scss',
})
export class PatientAppointments implements OnInit {

  BloodGroupLabel = BloodGroupLabel;

  patientDetailsWithAppointments = signal<patientDetailsWithAppointmentListWithDoctor | null>(null);

  constructor(
    private appointmentService: AppointmentService,
    private snackBar: MatSnackBar,
  ){ }

  ngOnInit(): void {
    this.loadAppointments();
  }

  loadAppointments(){
    this.appointmentService.getPatientAllAppointments().subscribe({
      next: (data) => {
        this.patientDetailsWithAppointments.set(data);
      },
      error: (err) => {
        console.error('Error fetching appointments: ', err);
        this.snackBar.open(`Error fetching appointments: ${err.error.message}`,"Close");
      }
    })
  }
}
