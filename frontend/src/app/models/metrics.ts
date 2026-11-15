export interface TotalBloodEstimated {
  totalSubscribers: number;
  totalCampaigns: number;
  estimatedMl: number;
  estimatedLiters: number;
}

export interface TotalLivesSaved {
  totalSubscribers: number;
  totalFinishedCampaigns: number;
  estimatedLivesSaved: number;
}

export interface BloodTypePercentage {
  bloodType: string;
  count: number;
  percentage: number;
}

export interface CampaignBloodEstimated {
  campaignId: number;
  campaignTitle: string;
  status: string;
  subscribedDonors: number;
  estimatedMl: number;
  estimatedLiters: number;
}

export interface CampaignLivesSaved {
  campaignId: number;
  campaignTitle: string;
  status: string;
  subscribedDonors: number;
  estimatedLivesSaved: number;
}

export interface BloodTypeRanking {
  bloodType: string;
  count: number;
}

export interface DonorStats {
  campaignsAttended: number;
  estimatedMl: number;
  estimatedLiters: number;
}

export interface DonorHealth {
  bloodType: string;
  lastDonationDate: string | null;
  nextEligibleDate: string | null;
  bmi: number;
  age: number;
}

export interface GeographicDistribution {
  campaignId: number;
  title: string;
  direction: string;
  latitude: number;
  longitude: number;
  isActive: boolean;
  isFinished: boolean;
}
