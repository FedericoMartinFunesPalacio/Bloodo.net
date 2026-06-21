export interface RequestDonor {
  firstName: string;
  lastName: string;
  birthdate: string; // formato dd-MM-yyyy
  document: string;
  bloodFactor: BloodFactor;
  bloodGroup: BloodGroup;
  gender: Gender;
  height: number; // en metros
  weight: number; // en kilogramos
  email: string;
  phoneNumber: string;
}

export interface ResponseDonor {
  id?: number;
  firstName: string;
  lastName: string;
  birthdate: string; // formato dd-MM-yyyy
  document: string;
  bloodFactor: BloodFactor;
  bloodGroup: BloodGroup;
  gender: Gender;
  height: number;
  weight: number;
  email: string;
  phoneNumber: string;
  isActive: boolean;
}

export interface SubscribedDonor {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  document: string;
  bloodGroup: BloodGroup;
  bloodFactor: BloodFactor;
  isActive: boolean;
}

export enum BloodFactor {
  POSITIVE = 'POSITIVE',
  NEGATIVE = 'NEGATIVE'
}

export enum BloodGroup {
  O = 'O',
  A = 'A',
  B = 'B',
  AB = 'AB'
}

export enum Gender {
  MALE = 'MALE',
  FEMALE = 'FEMALE',
  OTHER = 'OTHER'
}

