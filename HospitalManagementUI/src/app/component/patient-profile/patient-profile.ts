import { Component, OnInit, signal } from '@angular/core';
import { MaterialModule } from '../../material/material-module';
import { PatientService } from '../../service/patient.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Route, Router, RouterLink } from "@angular/router";
import { DomSanitizer } from '@angular/platform-browser';
import { AuthService } from '../../service/auth.service';
import { BloodGroupLabel } from '../../shared/enum';
import { patientProfileAppointment, patientResponse } from '../../models/patient.models';
import { documentResponse, documentResponseWithUrl } from '../../models/patient_document.models';
import { MatDialog } from '@angular/material/dialog';
import { DocumentDialog } from '../document-dialog/document-dialog';
import { draftResponse } from '../../models/appointment.models';
import { DocViewer } from '../doc-viewer/doc-viewer';

@Component({
  selector: 'app-patient-profile',
  imports: [MaterialModule, RouterLink],
  templateUrl: './patient-profile.html',
  styleUrl: './patient-profile.scss',
})
export class PatientProfile implements OnInit {

  BloodGroupLabel = BloodGroupLabel;

  email = signal<string | null>(null);
  patient = signal<patientResponse | null>(null);
  appointments = signal<patientProfileAppointment[] | null>(null);
  drafts = signal<draftResponse[] | null>(null)
  appointmentViewFlag = signal<boolean>(true);

  documentData = signal<documentResponseWithUrl[] | null>(null);
  DocumentViewFlag = signal<boolean>(false);
  selectedDocument = signal<any>(null);

  draftViewFlag = signal<boolean>(false);

  constructor(
    private patientService: PatientService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private router: Router,
    private sanitizer: DomSanitizer,
    private dialog: MatDialog,
  ) { }

  ngOnInit(): void {
    this.loadPatientProfile();
  }

  logout() {
    if (confirm("Do you want to logout?")) {
      this.authService.logout().subscribe(() => {
        this.snackBar.open('Logged out successfully', 'Close', { duration: 3000 });
        this.router.navigate(['']);
      });
    } else {
      return;
    }
  }

  showDocuments() {
    this.appointmentViewFlag.set(false);
    this.draftViewFlag.set(false);
    this.DocumentViewFlag.set(true);
  }

  showAppointments() {
    this.draftViewFlag.set(false);
    this.DocumentViewFlag.set(false);
    this.appointmentViewFlag.set(true);
  }

  showDrafts() {
    this.appointmentViewFlag.set(false);
    this.DocumentViewFlag.set(false);
    this.draftViewFlag.set(true);
  }

  loadPatientProfile() {
    this.patientService.loadPatientProfile().subscribe({
      next: (data) => {
        this.email.set(data.email);
        this.patient.set(data.patient);
        this.appointments.set(data.appointments);
        this.documentData.set(data.documents);
        this.drafts.set(data.drafts);

      }, error: (err) => {
        this.snackBar.open(`Error loding details: ${err.error.message}`);
        console.error("Error: ", err);
      }
    })
  }

  addNewDocument() {
    const dialogRef = this.dialog.open(DocumentDialog, { width: "1600px" });
    dialogRef.afterClosed().subscribe(result => {
      if (result == true) {
        this.loadPatientProfile();
      }
    })
  }

  onDelete(id: number){}

  onResume(draft: draftResponse){
    this.router.navigate(['/patient/draft'], {
      queryParams: {appointmentId: draft.id}
    })
  }

  openDocument(doc: any) {
    this.dialog.open(DocViewer, {
      data: { id: doc.id },
      maxWidth: '90vw',
      maxHeight: '90vh',
      panelClass: 'medical-doc-modal'
    });
  }

}
