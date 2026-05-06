import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavigationBar } from "./component/navigation-bar/navigation-bar";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavigationBar],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('HospitalManagementUI');
}
