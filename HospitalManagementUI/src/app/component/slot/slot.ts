import { Component, inject, OnInit, signal } from '@angular/core';
import { DoctorService } from '../../service/doctor.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../service/auth.service';
import { MaterialModule } from "../../material/material-module";
import { UpdateSlotDialog } from '../update-slot-dialog/update-slot-dialog';
import { MatDialog } from '@angular/material/dialog';
import { AddNewSlotDialog } from '../add-new-slot-dialog/add-new-slot-dialog';
import { slotResponse } from '../../models/slot.models';
import { SlotService } from '../../service/slot.service';

@Component({
  selector: 'app-slot',
  imports: [MaterialModule],
  templateUrl: './slot.html',
  styleUrl: './slot.scss',
})
export class Slot implements OnInit {
  dialog = inject(MatDialog);

  slots = signal<slotResponse[]>([]);

  constructor(
    private snackBar: MatSnackBar,
    private slotService: SlotService,
    private authService: AuthService,
  ) { }

  ngOnInit(): void {
    this.getSlots();
  }
  getSlots() {
    const userId = this.authService?.getuserId();

    this.slotService.getSlots().subscribe({
      next: (data) => {
        this.slots.set(data);
      },
      error: (err) => {
        console.log("Error fetching Slots", err);
        this.snackBar.open("Error fetching slots", "close");
      }
    });
  }

  editSlot(slot: any) {
    const dialogRef = this.dialog.open(UpdateSlotDialog, {
      width: '1600px',
      data: { slot: slot }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result == true) {
        this.getSlots();
      }
    })
  }

  addNewSlot() {
    const dialogRef = this.dialog.open(AddNewSlotDialog, {
      width: '1600px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result == true) {
        this.getSlots();
      }
    })
  }
}
