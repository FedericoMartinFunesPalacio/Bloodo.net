export interface RequestUser {
  username: string;
  password: string;
  email: string;
  phone: string;
  role: UserRole;
  roleId: number;
}

export interface ResponseUser {
  id: number;
  username: string;
  email: string;
  phone: string;
  role: UserRole;
  roleId: number;
}

export interface LoginResponse {
  token: string;
  id: number;
  email: string;
  phone: string;
  role: UserRole;
  roleId: number;
}

export enum UserRole {
  ADMIN = 'ADMIN',
  DONOR = 'DONOR',
  ORGANIZER = 'ORGANIZER'
}

