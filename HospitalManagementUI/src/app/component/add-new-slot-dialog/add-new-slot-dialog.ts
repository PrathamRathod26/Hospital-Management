import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { DoctorService } from '../../service/doctor.service';
import { MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SlotStatus } from '../../shared/enum';
import { MaterialModule } from '../../material/material-module';
import { slotResponse } from '../../models/slot.models';
import { SlotService } from '../../service/slot.service';

@Component({
  selector: 'app-add-new-slot-dialog',
  imports: [MaterialModule],
  templateUrl: './add-new-slot-dialog.html',
  styleUrl: './add-new-slot-dialog.scss',
})
export class AddNewSlotDialog {

  addSlotForm: FormGroup

  constructor(
    private router: Router,
    private fb: FormBuilder,
    private slotService: SlotService,
    private dialogRef: MatDialogRef<AddNewSlotDialog>,
    private snackBar: MatSnackBar,
  ) {
    this.addSlotForm = this.fb.group({
      id: null,
      date: ['', [Validators.required]],
      startTime: ['', [Validators.required]],
      endTime: ['', [Validators.required]],
      slotCapacity: [1,[Validators.required, Validators.min(1)]],
      status: null
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
      if (this.addSlotForm.valid) {
        const payload: slotResponse = this.addSlotForm.value;
  
        this.slotService.addSlot(payload).subscribe({
          next: (res) => {
            this.snackBar.open("Slot added", "ok")
            this.dialogRef.close(true);
          },
          error: (err) => {
            this.snackBar.open(`Error adding slot: ${err.error.message}`)
            console.error("Error adding slot: ", err);
          }
        });
      }
    }
  
  onCancel(){
      this.addSlotForm.reset();
      this.dialogRef.close(true);
    }
}
