import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateSlotDialog } from './update-slot-dialog';

describe('UpdateSlotDialog', () => {
  let component: UpdateSlotDialog;
  let fixture: ComponentFixture<UpdateSlotDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateSlotDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(UpdateSlotDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
