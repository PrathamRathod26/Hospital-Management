import { Component, OnInit, signal } from '@angular/core';
import { MaterialModule } from '../../material/material-module';
import { AppointmentService } from '../../service/appointment.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { BloodGroupLabel } from '../../shared/enum';
import { doctorResponse, doctorSlotWithAppointments } from '../../models/doctor.models';

@Component({
  selector: 'app-doctor-appointments',
  imports: [MaterialModule],
  templateUrl: './doctor-appointments.html',
  styleUrl: './doctor-appointments.scss',
})
export class DoctorAppointments implements OnInit {

  BloodGroupLabel=BloodGroupLabel;

  doctor = signal<doctorResponse | null>(null)
  slots = signal<doctorSlotWithAppointments[] | null>(null);

  constructor(
    private appointmentService: AppointmentService,
    private snackBar: MatSnackBar,
    private router: Router,
  ) { }

  ngOnInit(): void {
    this.loadAppointments();
  }

  loadAppointments() {
    this.appointmentService.getDoctorAllAppointments().subscribe({
      next: (data) => {
        this.doctor.set(data.doctor);
        this.slots.set(data.slots);
      },
      error: (err) => {
        console.error('Error fetching appointments: ', err);
        this.snackBar.open(`Error fetching appointments: ${err.error.message}`, "Close");
      }
    })
  }

  viewPatientDetails(patientid: number, appointmentId: number){
    this.router.navigate(["/patient-details", patientid, appointmentId]);
  }

  addAppointmentReport(id: number){
    this.router.navigate(["/add-prescription",id])
  }

}
