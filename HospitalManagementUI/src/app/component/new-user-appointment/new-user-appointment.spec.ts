import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewUserAppointment } from './new-user-appointment';

describe('NewUserAppointment', () => {
  let component: NewUserAppointment;
  let fixture: ComponentFixture<NewUserAppointment>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewUserAppointment],
    }).compileComponents();

    fixture = TestBed.createComponent(NewUserAppointment);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
