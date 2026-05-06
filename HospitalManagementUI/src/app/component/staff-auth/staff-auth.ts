import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MaterialModule } from '../../material/material-module';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-staff-auth',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule
  ],
  templateUrl: './staff-auth.html',
  styleUrl: './staff-auth.scss',
})
export class StaffAuth {
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
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onLogin() {
    this.authService.staffLogin(this.loginForm.value).subscribe({
      next: () => {
        this.snackBar.open('Logged in successfully', 'Close', { duration: 3000 });
        this.router.navigate(['']);
      },
      error: (err) => {
        this.snackBar.open('Login failed. Check your credentials.', 'Close', { duration: 3000 });
        console.error('Staff Login failed', err);
        this.loginForm.reset();
      }
    });
  }

  onRegister() {
    if (this.registerForm.valid) {
      this.authService.staffRegister(this.registerForm.value).subscribe({
        next: () => {
          this.snackBar.open('Staff Registration successful!', 'Close', { duration: 3000 });
          this.selectedTabIndex = 0;
          this.registerForm.reset();
        },
        error: (err) => {
          this.snackBar.open('Registration failed. Please try again.', 'Close', { duration: 3000 });
          console.error('Staff Registration failed', err);
        }
      });
    }
  }

  togglePasswordVisibility() {
    this.hidePassword = !this.hidePassword;
  }
}
