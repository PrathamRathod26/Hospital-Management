import { Component, OnInit, signal, DestroyRef, inject } from '@angular/core';
import { FormControl, FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, startWith, switchMap } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AppointmentService } from '../../service/appointment.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BloodGroup, Gender, SlotStatus } from '../../shared/enum';
import { MaterialModule } from "../../material/material-module";
import { doctorResponse } from '../../models/doctor.models';
import { filterSlotRequest, slotResponse } from '../../models/slot.models';
import { appointmentNewUserRequest } from '../../models/appointment.models';
import { DoctorService } from '../../service/doctor.service';
import { SlotService } from '../../service/slot.service';

@Component({
  selector: 'app-new-user-appointment',
  standalone: true,
  imports: [MaterialModule, ReactiveFormsModule],
  templateUrl: './new-user-appointment.html',
  styleUrl: './new-user-appointment.scss',
})
export class NewUserAppointment implements OnInit {
  appointmentForm!: FormGroup;
  doctorFilterForm!: FormGroup;


  constructor(
    private fb: FormBuilder,
    private appointmentService: AppointmentService,
    private snackbar: MatSnackBar,
    private destroyRef: DestroyRef,
    private slotService: SlotService,
    private doctorService: DoctorService
  ){  }

  doctors = signal<doctorResponse[]>([]);
  doctorSlots = signal<slotResponse[]>([]);
  selectedDoctor = signal<doctorResponse | null>(null);
  selectedDate = signal<number>(Date.now());
  selectedSlotId = signal<number | null>(null);
  searchControl = new FormControl('');
  readonly SlotStatus = SlotStatus;
  selectedFiles: File[] = [];

  ngOnInit(): void {
    this.initForm();
    this.searchPipeline();
  }

  initForm(){
    this.appointmentForm = this.fb.group({
      doctorId: [null, Validators.required],
      slotId: [null, Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      phone: ['', Validators.required],
      age: [0, [Validators.required, Validators.min(0)]],
      gender: [null as Gender | null, Validators.required],
      bloodGroup: [null as BloodGroup | null, Validators.required],
      emergencyContact: ['', Validators.required],
      reason: ['', Validators.required],
      notes: ['']
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

    const filter: filterSlotRequest = { doctorId, date: formattedDate };

    this.slotService.getScheduleForNewUser(filter).subscribe({
      next: (data) => this.doctorSlots.set(data),
      error: (err) => {
        this.snackbar.open(`Error fetching slots: ${err.error.message}`, 'Close');
      }
    });
  }

  makeAppointmentV1() {
    const slotId = this.selectedSlotId();
    const doctorId = this.selectedDoctor()?.id;

    if (this.appointmentForm.invalid || !slotId || !doctorId) {
      this.snackbar.open('Please complete the form and select a time slot.', 'Close', {duration: 3000});
      return;
    }

    const payload: appointmentNewUserRequest = {
      ...this.appointmentForm.getRawValue(),
      doctorId: doctorId,
      slotId: slotId,
      reason: this.appointmentForm.value.appointmentReason!,
      notes: this.appointmentForm.value.appointmentNotes!,
    };

    this.appointmentService.makeAppointmentForNewUserV1(payload).subscribe({
      next: () => {
        this.snackbar.open('Appointment booked successfully!', 'Success', { duration: 3000 });
        this.resetFormState();
      },
      error: (err) => {
        this.snackbar.open('Failed to book appointment.', 'Retry', { duration: 3000 });
      }
    });
  }

  makeAppointmentV2() {
    if (this.appointmentForm.invalid) {
      this.snackbar.open('Please fill all required fields.', 'Close', { duration: 3000 });
      return;
    }

    this.appointmentService.makeAppointmentForNewUserV2(this.appointmentForm.value, this.selectedFiles).subscribe({
      next: () => {
        this.snackbar.open('Appointment booked successfully!', 'ok', { duration: 3000 });
        this.resetFormState();
        this.selectedFiles = [];
      },
      error: (err) => {
        this.snackbar.open(`Failed to book appointment: ${err.error.message}`, 'Retry', { duration: 3000 })
        console.error(err)
      }
    });
  }

  onFilesSelected(event: any) {
    const files: FileList = event.target.files;
    if (!files.length) return;
    this.selectedFiles = [...this.selectedFiles, ...Array.from(files)];
    event.target.value = '';
  }

  removeSelectedFile(fileName: string) {
    this.selectedFiles = this.selectedFiles.filter(
      file => file.name !== fileName
    );
  }

  private resetFormState() {
    this.appointmentForm.reset({ documentIds: [] });
    this.selectedDoctor.set(null);
    this.selectedSlotId.set(null);
    this.doctorSlots.set([]);
  }

  
}