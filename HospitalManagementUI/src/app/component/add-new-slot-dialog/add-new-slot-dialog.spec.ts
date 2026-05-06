import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddNewSlotDialog } from './add-new-slot-dialog';

describe('AddNewSlotDialog', () => {
  let component: AddNewSlotDialog;
  let fixture: ComponentFixture<AddNewSlotDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddNewSlotDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(AddNewSlotDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
