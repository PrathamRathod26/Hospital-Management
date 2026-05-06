import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MakeAppointment } from './make-appointment';

describe('MakeAppointment', () => {
  let component: MakeAppointment;
  let fixture: ComponentFixture<MakeAppointment>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MakeAppointment],
    }).compileComponents();

    fixture = TestBed.createComponent(MakeAppointment);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
