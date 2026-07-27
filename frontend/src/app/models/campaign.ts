import { BloodFactor, BloodGroup } from './donor';

export interface RequestCampaign {
  title: string;
  description: string;
  startDate: string;
  endDate?: string;
  startTime: string;
  direction: string;
  bloodFactorRequired?: BloodFactor;
  bloodGroupRequired?: BloodGroup;
  organizerId: number;
}

export interface ResponseCampaign {
  id?: number;
  title: string;
  description: string;
  startDate: string;
  endDate?: string;
  startTime: string;
  direction: string;
  latitude?: number;
  longitude?: number;
  bloodFactorRequired?: BloodFactor;
  bloodGroupRequired?: BloodGroup;
  organizerId: number;
  isActive?: boolean;
}

