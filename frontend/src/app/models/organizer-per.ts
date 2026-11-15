import { Gender } from './donor';

export interface RequestOrganizerPer {
  firstName: string;
  lastName: string;
  birthdate: string; // formato dd-MM-yyyy
  document: string;
  direction: string;
  gender: Gender;
  email: string;
  phoneNumber: string;
}

export interface ResponseOrganizerPer {
  id?: number;
  firstName: string;
  lastName: string;
  birthdate: string; // formato dd-MM-yyyy
  document: string;
  direction: string;
  latitude?: number;
  longitude?: number;
  gender: Gender;
  email: string;
  phoneNumber: string;
  isActive: boolean;
}

