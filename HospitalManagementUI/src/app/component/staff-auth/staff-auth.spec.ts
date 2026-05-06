import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StaffAuth } from './staff-auth';

describe('StaffAuth', () => {
  let component: StaffAuth;
  let fixture: ComponentFixture<StaffAuth>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StaffAuth],
    }).compileComponents();

    fixture = TestBed.createComponent(StaffAuth);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
