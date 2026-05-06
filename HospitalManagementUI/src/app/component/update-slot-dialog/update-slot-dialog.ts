import { Component, Inject, OnInit } from '@angular/core';
import { MaterialModule } from '../../material/material-module';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { SlotStatus } from '../../shared/enum';
import { DoctorService } from '../../service/doctor.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { slotRequest } from '../../models/slot.models';
import { SlotService } from '../../service/slot.service';

@Component({
  selector: 'app-update-slot-dialog',
  imports: [MaterialModule],
  templateUrl: './update-slot-dialog.html',
  styleUrl: './update-slot-dialog.scss',
})
export class UpdateSlotDialog implements OnInit {
  ngOnInit(): void { }
  updateSlotForm: FormGroup;

  constructor(
    private router: Router,
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<UpdateSlotDialog>,
    private snackBar: MatSnackBar,
    private slotService: SlotService,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.updateSlotForm = this.fb.group({
      id: data.slot.id,
      startTime: [data.slot.startTime, [Validators.required]],
      endTime: [data.slot.endTime, [Validators.required]],
      slotCapacity: [data.slot.slotCapacity,[Validators.required, Validators.min(data.slot.slotCapacity)]],
      status: [data.slot.status || SlotStatus.AVAILABLE, [Validators.required]]
    }, {
      validators: this.timeRangeValidator
    });
  }

  timeRangeValidator(group: AbstractControl): ValidationErrors | null {
    const start = group.get('startTime')?.value;
    const end = group.get('endTime')?.value;

    if (start && end && start >= end) {
      return { timeRangeInvalid: true };
    }
    return null;
  }

  onSubmit() {
    if (this.updateSlotForm.valid) {
      const payload: slotRequest = this.updateSlotForm.value;

      this.slotService.updateSlot(payload).subscribe({
        next: () => {
          this.snackBar.open("Slot updated", "ok")
          this.dialogRef.close(true);
        },
        error: (err) => {
          this.snackBar.open(`Error updating slot: ${err.error.message}`)
          console.error("Error updating slot: ", err);
        }
      });
    }
  }
}
