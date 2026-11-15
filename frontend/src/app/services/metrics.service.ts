import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { enviroment } from '../../env/enviroments';
import { AuthService } from './auth.service';
import {
  TotalBloodEstimated,
  TotalLivesSaved,
  BloodTypePercentage,
  CampaignBloodEstimated,
  CampaignLivesSaved,
  BloodTypeRanking
} from '../models/metrics';

@Injectable({
  providedIn: 'root'
})
export class MetricsService {
  private campaignsBaseUrl = `${enviroment.appServiceUrl}/campaigns/metrics`;
  private donorsBaseUrl = `${enviroment.appServiceUrl}/donors/metrics`;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  getTotalBloodEstimated(): Observable<TotalBloodEstimated> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>>(`${this.campaignsBaseUrl}/blood-total`, { headers })
      .pipe(map(data => ({
        totalSubscribers: data['total_subscribers'],
        totalCampaigns: data['total_campaigns'],
        estimatedMl: data['estimated_ml'],
        estimatedLiters: data['estimated_liters']
      })));
  }

  getTotalLivesSaved(): Observable<TotalLivesSaved> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>>(`${this.campaignsBaseUrl}/lives-saved-total`, { headers })
      .pipe(map(data => ({
        totalSubscribers: data['total_subscribers'],
        totalFinishedCampaigns: data['total_finished_campaigns'],
        estimatedLivesSaved: data['estimated_lives_saved']
      })));
  }

  getBloodTypePercentage(): Observable<BloodTypePercentage[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>[]>(`${this.donorsBaseUrl}/blood-type-percentage`, { headers })
      .pipe(map(data => data.map(item => ({
        bloodType: item['blood_type'],
        count: item['count'],
        percentage: item['percentage']
      }))));
  }

  getBloodEstimatedPerCampaign(): Observable<CampaignBloodEstimated[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>[]>(`${this.campaignsBaseUrl}/blood-estimated`, { headers })
      .pipe(map(data => data.map(item => ({
        campaignId: item['campaign_id'],
        campaignTitle: item['campaign_title'],
        status: item['status'],
        subscribedDonors: item['subscribed_donors'],
        estimatedMl: item['estimated_ml'],
        estimatedLiters: item['estimated_liters']
      }))));
  }

  getLivesSavedPerCampaign(): Observable<CampaignLivesSaved[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>[]>(`${this.campaignsBaseUrl}/lives-saved`, { headers })
      .pipe(map(data => data.map(item => ({
        campaignId: item['campaign_id'],
        campaignTitle: item['campaign_title'],
        status: item['status'],
        subscribedDonors: item['subscribed_donors'],
        estimatedLivesSaved: item['estimated_lives_saved']
      }))));
  }

  getBloodTypeRanking(campaignId: number): Observable<BloodTypeRanking[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>[]>(`${this.campaignsBaseUrl}/blood-type-ranking/${campaignId}`, { headers })
      .pipe(map(data => data.map(item => ({
        bloodType: item['blood_type'],
        count: item['count']
      }))));
  }
}
