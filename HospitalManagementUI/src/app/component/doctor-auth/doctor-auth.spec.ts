import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DoctorAuth } from './doctor-auth';

describe('DoctorAuth', () => {
  let component: DoctorAuth;
  let fixture: ComponentFixture<DoctorAuth>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DoctorAuth],
    }).compileComponents();

    fixture = TestBed.createComponent(DoctorAuth);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
