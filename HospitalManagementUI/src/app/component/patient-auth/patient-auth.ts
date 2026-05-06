import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../../material/material-module';
import { patientRegisterRequest } from '../../models/patient.models';

@Component({
  selector: 'app-patient-auth',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule
  ],
  templateUrl: './patient-auth.html',
  styleUrl: './patient-auth.scss',
})
export class PatientAuth implements OnInit{
  loginForm!: FormGroup;
  registerForm!: FormGroup;
  selectedTabIndex = 0;
  hidePassword = true;
  // selectedFile: File | null = null;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.initForm()
  }

  initForm(){
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      phone: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      age: [0, [Validators.required, Validators.min(0)]],
      gender: ['', [Validators.required]],
      bloodGroup: ['', [Validators.required]],
      emergencyContact: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
    });

    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onLogin(){
    this.authService.patientLogin(this.loginForm.value).subscribe({
      next: () => {
        this.snackBar.open('Logged in successfully', 'Close', { duration: 3000 });
        this.router.navigate(['']);
      },
      error: (err) => {
        this.snackBar.open('Login failed. Check your credentials.', 'Close', { duration: 3000 });
        console.error('Login failed', err);
        this.loginForm.reset();
      }
    });
  }

  // onFileSelected(event: any) {
  //   this.selectedFile = event.target.files[0];
  // }
  
  // onRegisterV2() {
  //   if (this.registerForm.valid && this.selectedFile) {
  //     const data: patientRegisterRequest = this.registerForm.value;

  //     this.authService.patientRegisterV2(data,this.selectedFile).subscribe({
  //       next: () => {
  //         this.snackBar.open('Registration successful! Please login.', 'Close', { duration: 3000 });
  //         this.selectedTabIndex = 0;
  //         this.registerForm.reset();
  //       },
  //       error: (err) => {
  //         this.snackBar.open('Registration failed. Please try again.', 'Close', { duration: 3000 });
  //         console.error('Registration failed', err);
  //       }
  //     });
  //   }
  // }

  onRegisterV1() {
    if (this.registerForm.valid) {
      const data: patientRegisterRequest = this.registerForm.value;

      this.authService.patientRegisterV1(data).subscribe({
        next: () => {
          this.snackBar.open('Registration successful! Please login.', 'Close', { duration: 3000 });
          this.selectedTabIndex = 0;
          this.registerForm.reset();
        },
        error: (err) => {
          this.snackBar.open('Registration failed. Please try again.', 'Close', { duration: 3000 });
          console.error('Registration failed', err);
        }
      });
    }
  }

  togglePasswordVisibility() {
    this.hidePassword = !this.hidePassword;
  }

}
