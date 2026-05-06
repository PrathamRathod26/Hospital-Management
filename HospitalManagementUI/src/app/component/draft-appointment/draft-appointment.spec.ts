import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DraftAppointment } from './draft-appointment';

describe('DraftAppointment', () => {
  let component: DraftAppointment;
  let fixture: ComponentFixture<DraftAppointment>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DraftAppointment],
    }).compileComponents();

    fixture = TestBed.createComponent(DraftAppointment);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
