import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { AuthDialog } from '../../component/auth-dialog/auth-dialog';

export const patientGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const dialog = inject(MatDialog);

  if (authService.getRole() == "PATIENT") {
    return true;
  }
  dialog.open(AuthDialog, { width: '400px' });
  return false;
};

export const doctorGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const dialog = inject(MatDialog);

  if (authService.getRole() == "DOCTOR") {
    return true;
  }

  dialog.open(AuthDialog, { width: '400px' });
  return false;
};

export const guestGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const dialog = inject(MatDialog);

  if (!authService.isLoggedIn()) {
    return true;
  }

  dialog.open(AuthDialog, { width: '400px' });
  return false;
};

export const staffGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const dialog = inject(MatDialog);

  if (authService.getRole() == "STAFF") {
    return true;
  }

  dialog.open(AuthDialog, { width: '400px' });
  return false;
};