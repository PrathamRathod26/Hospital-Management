import { Component, OnInit, signal } from '@angular/core';
import { PatientService } from '../../service/patient.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MaterialModule } from '../../material/material-module';
import { ActivatedRoute } from '@angular/router';
import { DomSanitizer } from '@angular/platform-browser';
import { BloodGroupLabel } from '../../shared/enum';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { patientDetailsWithDocumentDataAndAppointmentData } from '../../models/patient.models';
import { prescriptionRequest } from '../../models/prescription.models';
import { MatDialog } from '@angular/material/dialog';
import { DocViewer } from '../doc-viewer/doc-viewer';
import { PrescriptionService } from '../../service/prescription.service';

@Component({
  selector: 'app-patient-details',
  imports: [MaterialModule],
  templateUrl: './patient-details.html',
  styleUrl: './patient-details.scss',
})
export class PatientDetails implements OnInit {

  BloodGroupLabel = BloodGroupLabel;

  documentFlag = signal<boolean>(false);
  prescriptionFlag = signal<boolean>(true);

  patientDetails = signal<patientDetailsWithDocumentDataAndAppointmentData | null>(null);
  selectedDocument = signal<any>(null);

  prescriptionForm: FormGroup;

  patientId: string | null = null;
  appointmentId: string | null = null;

  ngOnInit(): void {
    this.patientId = this.route.snapshot.paramMap.get('patientId');
    this.appointmentId = this.route.snapshot.paramMap.get('appointmentId');
    this.loadPatientDetails(this.patientId, this.appointmentId);
  }

  constructor(
    private patientService: PatientService,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private sanitizer: DomSanitizer,
    private fb: FormBuilder,
    private prescriptionService: PrescriptionService,
    private dialog: MatDialog
  ) {
    this.prescriptionForm = this.fb.group({
      diagnosis: ['', [Validators.required]],
      medicationNotes: ['', [Validators.required]],
      advice: ['', [Validators.required]]
    })
  }

  loadPatientDetails(patientId: string | null, appointmentId: string | null) {
    if (patientId == null || appointmentId == null) {
      console.error("Patient & appointment Id needed");
      return;
    }
    this.patientService.loadPatientDetails(patientId, appointmentId).subscribe({
      next: (data) => {
        this.patientDetails.set(data);
        console.log("Patient details loaded successfully", data)
      },
      error: (err) => {
        this.snackBar.open(`Failed to Load patient Details: ${err.error.message}`);
        console.error("Failed to load patient details: ", err);
      }
    })
  }

  showDocuments() {
    this.prescriptionFlag.set(false);
    this.documentFlag.set(true);
  }

  showPrescription() {
    this.documentFlag.set(false);
    this.prescriptionFlag.set(true);
  }

  addPrescription(id: number) {
    if (this.prescriptionForm.invalid || !id) {
      this.snackBar.open("Invalid Values", "close");
      return;
    }

    const payload: prescriptionRequest = {
      ...this.prescriptionForm.getRawValue()
    }

    this.prescriptionService.addPrescription(id, payload).subscribe({
      next: () => {
        this.snackBar.open(`Prescription added success`, 'close', { duration: 3000 });
        this.loadPatientDetails(this.patientId, this.appointmentId)
      }, error: (err) => {
        this.snackBar.open(`Prescription add error: ${err.error.message}`, 'close', { duration: 3000 })
      }
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
