export enum BloodGroup {
  A_POSITIVE = 'A_POSITIVE',
  A_NEGATIVE = 'A_NEGATIVE',
  B_POSITIVE = 'B_POSITIVE',
  B_NEGATIVE = 'B_NEGATIVE',
  O_POSITIVE = 'O_POSITIVE',
  O_NEGATIVE = 'O_NEGATIVE',
  AB_POSITIVE = 'AB_POSITIVE',
  AB_NEGATIVE = 'AB_NEGATIVE'
}

export const BloodGroupLabel: Record<BloodGroup, string> = {
  [BloodGroup.A_POSITIVE]: 'A+',
  [BloodGroup.A_NEGATIVE]: 'A-',
  [BloodGroup.B_POSITIVE]: 'B+',
  [BloodGroup.B_NEGATIVE]: 'B-',
  [BloodGroup.O_POSITIVE]: 'O+',
  [BloodGroup.O_NEGATIVE]: 'O-',
  [BloodGroup.AB_POSITIVE]: 'AB+',
  [BloodGroup.AB_NEGATIVE]: 'AB-'
};

export enum Gender {
  MALE, FEMALE, OTHERS
}

export enum Status {
  SCHEDULED, COMPLETED, CANCELLED
}

export enum SlotStatus {
  AVAILABLE = 'AVAILABLE',
  FULL = 'FULL',
  CANCELLED = 'CANCELLED',
  DISABLED = 'DISABLED'
}