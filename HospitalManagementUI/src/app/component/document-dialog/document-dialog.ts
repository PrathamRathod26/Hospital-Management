import { Component, signal } from '@angular/core';
import { MaterialModule } from '../../material/material-module';
import { documentUploadStatus } from '../../models/patient_document.models';
import { PatientService } from '../../service/patient.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpEventType } from '@angular/common/http';
import { MatDialogRef } from '@angular/material/dialog';
import { PatientDocumentService } from '../../service/patient-document.service';

@Component({
  selector: 'app-document-dialog',
  imports: [MaterialModule],
  templateUrl: './document-dialog.html',
  styleUrl: './document-dialog.scss',
})
export class DocumentDialog {
  uploadQueue = signal<documentUploadStatus[]>([]);
  isUploading = signal<boolean>(false);
  selectedFiles: File[] = [];

  constructor(
    private patientService: PatientService,
    private snackBar: MatSnackBar,
    private patientDocumentService: PatientDocumentService,
    private dialogRef: MatDialogRef<DocumentDialog>
  ) { }

  onFilesSelected(event: any) {
    const files: FileList = event.target.files;
    if (!files.length) return;
    this.selectedFiles = [...this.selectedFiles, ...Array.from(files)];
    event.target.value = '';
  }

  onUpload() {
    this.isUploading.set(true);

    this.patientDocumentService.patientUploadMultipleDocument(this.selectedFiles).subscribe({
      next: (event) => {
        if (event.type === HttpEventType.Response) {
          this.isUploading.set(false);
          this.dialogRef.close(true);
        }
      },
      error: () => {
        this.isUploading.set(false);
      }
    });
  }
}
