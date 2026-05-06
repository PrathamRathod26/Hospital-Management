import { Component } from '@angular/core';
import { MaterialModule } from '../../material/material-module';
import { AuthService } from '../../service/auth.service';
import { Router, RouterLink } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-navigation-bar',
  imports: [MaterialModule, RouterLink],
  templateUrl: './navigation-bar.html',
  styleUrl: './navigation-bar.scss',
})
export class NavigationBar {
  constructor(
    public authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) { }

  get userDetails() {
    const token = this.authService.getAccessToken();
    if (!token) return null;
    try {
      const payloadBase64 = token.split('.')[1];
      const decodedJson = atob(payloadBase64);
      return JSON.parse(decodedJson);
    } catch (e) {
      return null;
    }
  }

  logout() {
    this.authService.logout().subscribe(() => {
      this.snackBar.open('Logged out successfully', 'Close', { duration: 3000 });
      this.router.navigate(['']);
    });
  }
}
