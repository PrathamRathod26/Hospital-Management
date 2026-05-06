import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { appointmentDraftRequestV2, appointmentRequestV1, appointmentResponseWithPatientAndSlotAndDoctor } from '../../models/appointment.models';
import { AppointmentService } from '../../service/appointment.service';
import { MaterialModule } from '../../material/material-module';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { doctorResponse } from '../../models/doctor.models';
import { filterSlotRequest, slotResponseWithAppointmentIdFlag } from '../../models/slot.models';
import { documentResponse } from '../../models/patient_document.models';
import { SlotStatus } from '../../shared/enum';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DoctorService } from '../../service/doctor.service';
import { SlotService } from '../../service/slot.service';

@Component({
  selector: 'app-draft-appointment',
  imports: [MaterialModule],
  templateUrl: './draft-appointment.html',
  styleUrl: './draft-appointment.scss',
})
export class DraftAppointment implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private appointmentService: AppointmentService,
    private snackbar: MatSnackBar,
    private fb: FormBuilder,
    private slotService: SlotService,
    private doctorService: DoctorService

  ) { }

  appointment = signal<appointmentResponseWithPatientAndSlotAndDoctor | null>(null);
  appointmentForm!: FormGroup;
  doctors = signal<doctorResponse[]>([]);
  doctorSlots = signal<slotResponseWithAppointmentIdFlag[]>([]);
  selectedDoctor = signal<doctorResponse | null>(null);
  selectedDate = signal<number>(Date.now());
  selectedSlotId = signal<number | null>(null);
  newDcuments = signal<documentResponse[] | null>(null);
  documentIds = signal<number[]>([]);
  attachedDocuments = signal<documentResponse[] | null>(null)
  selectedFiles: File[] = [];
  searchControl = new FormControl('');
  readonly SlotStatus = SlotStatus;

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      this.loadAppointmentWithAttachedDocument(params.get('appointmentId')!);
    });
    this.initForm();
    this.loadDoctors();
  }
  initForm() {
    this.appointmentForm = this.fb.group({
      doctorId: [null, [Validators.required]],
      slotId: [null, [Validators.required]],
      reason: ['', [Validators.required]],
      notes: [''],
    });
  }

  loadAppointmentWithAttachedDocument(appointmentId: string) {
    this.appointmentService.getAppointment(appointmentId)?.subscribe({
      next: (data) => {
        this.appointment.set(data);
        this.appointmentForm.patchValue({
          reason: this.appointment()?.reason,
          notes: this.appointment()?.notes
        })
      }, error: (err) => {
        console.error("Error loding  draft appointment", err.error.message);
        this.snackbar.open("Error loading draft", "ok", { duration: 30000 });
      }
    });
    this.appointmentService.getAttachedDocument(appointmentId).subscribe({
      next: (data) => {
        this.attachedDocuments.set(data);
      }, error: (err) => {
        console.error(err);
        this.snackbar.open("Error loading Documents", "ok", { duration: 30000 });
      }
    })
  }

  loadDoctors(): void {
    const filter = {
      name: this.searchControl.value || '',
      specialization: ''
    };

    this.doctorService.filterDoctors(filter).subscribe({
      next: (data) => this.doctors.set(data),
      error: (err) => console.error('Error fetching doctors:', err)
    });
  }

  selectDoctor(doctor: doctorResponse) {
    this.selectedDoctor.set(doctor);
    this.appointmentForm.patchValue({ doctorId: doctor.id });
    this.loadDoctorSchedule();
  }

  onSlotSelectionChange(slotId: number, selected: boolean) {
    const id = selected ? slotId : null;
    this.selectedSlotId.set(id);
    this.appointmentForm.patchValue({ slotId: id });
  }

  loadDoctorSchedule() {
    const doctorId = this.selectedDoctor()?.id;
    if (!doctorId) return;

    const date = new Date(this.selectedDate());
    const formattedDate = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;

    const filter: filterSlotRequest = {
      doctorId: doctorId,
      date: formattedDate
    };

    this.slotService.getSchedule(filter).subscribe({
      next: (data) => this.doctorSlots.set(data),
      error: (err) => console.error('Error fetching Slots', err)
    });
  }

  onFilesSelected(event: any) {
    const files: FileList = event.target.files;
    if (!files.length) return;
    this.selectedFiles = [...this.selectedFiles, ...Array.from(files)];
    event.target.value = '';
  }

  onSubmit() {
    if (this.appointmentForm.invalid) {
      this.snackbar.open('Please fill all required fields.', 'Close', { duration: 3000 });
      return;
    }

    this.appointmentService.makeAppointmentV3(this.appointmentForm.value, this.selectedFiles).subscribe({
      next: () => {
        this.snackbar.open('Appointment booked successfully!', 'ok', { duration: 3000 });
        this.resetFormState();
        this.selectedFiles = [];
        this.attachedDocuments.set([]);
      },
      error: (err) => this.snackbar.open('Failed to book appointment.', 'Retry', { duration: 3000 })
    });
  }

  removeSelectedFile(fileName: string) {
    this.selectedFiles = this.selectedFiles.filter(
      file => file.name !== fileName
    );
  }

  removeAttachedDocument(documentId: number) {
    this.appointmentService.removeAttachedDocument(this.appointment()!.id, documentId).subscribe({
      next: () => {
        this.snackbar.open("Document removed from draft")
        this.loadAppointmentWithAttachedDocument(this.appointment()!.id.toString())
      }, error: (err) => {
        console.error(err);
        this.snackbar.open("Error removing document");
      }
    })
  }

  private resetFormState() {
    this.appointmentForm.reset({ documentIds: [] });
    this.selectedDoctor.set(null);
    this.selectedSlotId.set(null);
    this.doctorSlots.set([]);
    this.documentIds.set([]);
  }

  updateDraft() {
    const { reason, notes } = this.appointmentForm.value;

    const payload: appointmentDraftRequestV2 = { reason, notes };

    this.appointmentService.updateDraftAppointment(payload, this.selectedFiles, this.appointment()!.id).subscribe({
      next: () => {
        this.snackbar.open("Draft Updated");
        this.resetFormState();
        this.loadAppointmentWithAttachedDocument(this.appointment()!.id.toString())
        this.selectedFiles = []
      }, error: (err) => {
        console.error(err);
        this.snackbar.open("Error updating draft");
      }
    })
  }
}
