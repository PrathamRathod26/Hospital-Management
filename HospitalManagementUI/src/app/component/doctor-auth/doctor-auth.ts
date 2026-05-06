import { Component } from '@angular/core';
import { MaterialModule } from '../../material/material-module';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-doctor-auth',
  imports: [MaterialModule],
  templateUrl: './doctor-auth.html',
  styleUrl: './doctor-auth.scss',
})
export class DoctorAuth {
  loginForm: FormGroup;
  registerForm: FormGroup;
  selectedTabIndex = 0;
  hidePassword = true;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      phone: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      specialization: ['', [Validators.required]],
      licenseNumber: ['', [Validators.required, Validators.minLength(10)]]
    });
  }

  onLogin() {
    this.authService.doctorLogin(this.loginForm.value).subscribe({
      next: () => {
        this.snackBar.open('Logged in successfully', 'Close', { duration: 3000 });
        this.router.navigate(['']);
      },
      error: (err) => {
        this.snackBar.open('Login failed. Check your credentials.', 'Close', { duration: 3000 });
        console.error('Login failed', err);
      }
    });
  }

  onRegister() {
    if (this.registerForm.valid) {
      this.authService.doctorRegister(this.registerForm.value).subscribe({
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
