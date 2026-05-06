import { Component, OnInit, signal } from '@angular/core';
import { MaterialModule } from '../../material/material-module';
import { DoctorService } from '../../service/doctor.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router, RouterLink } from "@angular/router";
import { AuthService } from '../../service/auth.service';
import { slotResponse } from '../../models/slot.models';
import { doctorAppointmentWithDetails, doctorResponse } from '../../models/doctor.models';

@Component({
  selector: 'app-doctor-profile',
  imports: [MaterialModule],
  templateUrl: './doctor-profile.html',
  styleUrl: './doctor-profile.scss',
})
export class DoctorProfile implements OnInit {
  doctorProfile = signal<doctorResponse | null>(null);
  slots = signal<slotResponse[] | null>(null);
  appointments = signal<doctorAppointmentWithDetails[] | null>(null);

  appointmentViewFlag = signal<number>(1);
  slotViewFlag = signal<number>(0);

  ngOnInit(): void {
    this.loadDoctorProfile();
  }

  constructor(
    private doctorService: DoctorService,
    private snackBar: MatSnackBar,
    private authService: AuthService,
    private router: Router,
  ){ }

  showAppointments(){
    this.slotViewFlag.set(0);
    this.appointmentViewFlag.set(1);
  }

  showSlots(){
    this.appointmentViewFlag.set(0);
    this.slotViewFlag.set(1);
  }

  loadDoctorProfile(){
    this.doctorService.loadDoctorProfile().subscribe({
      next: (data)=>{
        this.doctorProfile.set(data.doctor);
        this.slots.set(data.slots);
        this.appointments.set(data.appointments);
      }, error: (err)=>{
        this.snackBar.open(`Error loading profile: ${err.error.message}`)
        console.error("Error: ", err);
      }
    })
  }

  logout(){
    if (confirm("Do you want to logout?")) {
      this.authService.logout().subscribe(() => {
        this.snackBar.open('Logged out successfully', 'Close', { duration: 3000 });
        this.router.navigate(['']);
      });
    } else {
      return;
    }
  }
}
