export interface RequestOrganizerEmp {
  fullName: string;
  document: string;
  direction: string;
  email: string;
  phoneNumber: string;
}

export interface ResponseOrganizerEmp {
  id?: number;
  fullName: string;
  document: string;
  direction: string;
  latitude?: number;
  longitude?: number;
  email: string;
  phoneNumber: string;
  isActive: boolean;
}

