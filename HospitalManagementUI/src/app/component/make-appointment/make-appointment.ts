import { Component, DestroyRef, OnInit, signal } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AppointmentService } from '../../service/appointment.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MaterialModule } from '../../material/material-module';
import { SlotStatus } from '../../shared/enum';
import { doctorResponse } from '../../models/doctor.models';
import { filterSlotRequest, slotResponseWithAppointmentIdFlag } from '../../models/slot.models';
import { PatientService } from '../../service/patient.service';
import { documentResponse } from '../../models/patient_document.models';
import { appointmentDraftRequestV2 } from '../../models/appointment.models';
import { debounceTime, distinctUntilChanged, startWith, switchMap } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DoctorService } from '../../service/doctor.service';
import { PatientDocumentService } from '../../service/patient-document.service';
import { SlotService } from '../../service/slot.service';

@Component({
  selector: 'app-make-appointment',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule],
  templateUrl: './make-appointment.html',
  styleUrl: './make-appointment.scss',
})
export class MakeAppointment implements OnInit {
  appointmentForm!: FormGroup;
  doctorFilterForm!: FormGroup;
  doctors = signal<doctorResponse[]>([]);
  doctorSlots = signal<slotResponseWithAppointmentIdFlag[]>([]);
  selectedDoctor = signal<doctorResponse | null>(null);
  selectedDate = signal<number>(Date.now());
  selectedSlotId = signal<number | null>(null);
  documents = signal<documentResponse[] | null>(null);
  documentIds = signal<number[]>([]);
  selectedFiles: File[] = [];

  searchControl = new FormControl('');
  readonly SlotStatus = SlotStatus;

  constructor(
    private appointmentService: AppointmentService,
    private slotService: SlotService,
    private snackbar: MatSnackBar,
    private fb: FormBuilder,
    private destroyRef: DestroyRef,
    private patientDocumentService: PatientDocumentService,
    private doctorService: DoctorService

  ) { }

  ngOnInit(): void {
    this.initForm();
    this.searchPipeline();
    this.loadDocumentData();
  }

  initForm() {
    this.appointmentForm = this.fb.group({
      doctorId: [null, [Validators.required]],
      slotId: [null, [Validators.required]],
      reason: ['', [Validators.required]],
      notes: [''],
    });

    this.doctorFilterForm = this.fb.group({
      docName: [''],
      docSpecialization: ['']
    })
  }

  searchPipeline() {
    this.doctorFilterForm.valueChanges.pipe(
      startWith(this.doctorFilterForm.value),
      debounceTime(300),
      distinctUntilChanged((prev, curr) =>
        prev.docName === curr.docName && prev.docSpecialization === curr.docSpecialization
      ),
      switchMap(values =>
        this.doctorService.filterDoctors({
          name: values.docName ?? '',
          specialization: values.docSpecialization ?? ''
        })
      ),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (data) => {
        this.doctors.set(data);
      },
      error: (err) => console.error('Filter failed', err)
    });
  }

  loadDocumentData() {
    this.patientDocumentService.loadDocument().subscribe({
      next: (data) => this.documents.set(data),
      error: (err) => {
        this.snackbar.open(`Error loading documents`, 'Close', { duration: 3000 });
        console.error(err);
      }
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
      },
      error: (err) => this.snackbar.open('Failed to book appointment.', 'Retry', { duration: 3000 })
    });
  }

  makeAppointmentDraft() {
    const { reason, notes } = this.appointmentForm.value;

    const payload: appointmentDraftRequestV2 = { reason, notes };
    console.log("appointment request sent!")

    this.appointmentService.makeAppointmentDraftV2(payload, this.selectedFiles).subscribe({
      next: () => {
        this.snackbar.open('Draft saved successfully!', 'ok', { duration: 3000 });
        this.resetFormState();
      },
      error: (err) => this.snackbar.open('Failed to save draft.', 'Retry', { duration: 3000 })
    });
  }

  private resetFormState() {
    this.appointmentForm.reset({ documentIds: [] });
    this.selectedDoctor.set(null);
    this.selectedSlotId.set(null);
    this.doctorSlots.set([]);
  }

  removeSelectedFile(fileName: string) {
    this.selectedFiles = this.selectedFiles.filter(
      file => file.name !== fileName
    );
  }


}