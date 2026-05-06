import { Component, inject } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { MaterialModule } from '../../material/material-module';

@Component({
  selector: 'app-auth-dialog',
  imports: [MaterialModule],
  templateUrl: './auth-dialog.html',
  styleUrl: './auth-dialog.scss',
})
export class AuthDialog {
  constructor(
    private router: Router,
    private dialogRef: MatDialogRef<AuthDialog>
  ){ }

  goToHome() {
    this.dialogRef.close();
    this.router.navigate(['']);  
  }
}
