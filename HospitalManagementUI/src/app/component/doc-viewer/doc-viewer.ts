import { Component, Inject, OnInit, signal } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { MaterialModule } from '../../material/material-module';
import { PatientService } from '../../service/patient.service';
import { PatientDocumentService } from '../../service/patient-document.service';

@Component({
  selector: 'app-doc-viewer',
  imports: [MaterialModule],
  templateUrl: './doc-viewer.html',
  styleUrl: './doc-viewer.scss',
})
export class DocViewer implements OnInit {

  document = signal<any>(null);

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { id: number },
    private dialogRef: MatDialogRef<DocViewer>,
    private sanitizer: DomSanitizer,
    private patientDocumentService: PatientDocumentService,

  ) { }

  ngOnInit() {
    this.viewSelectedDocument(this.data.id)
  }

  viewSelectedDocument(documentId: number) {
    this.patientDocumentService.viewDocument(documentId).subscribe({
      next: (blob: Blob) => {
        const objectUrl = URL.createObjectURL(blob);
        const safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(objectUrl);
        this.document.set(safeUrl);
      },
      error: (err) => {
        console.error("error displaying document: ", err)
      }
    })
  }

  close() {
    this.dialogRef.close();
  }
}
