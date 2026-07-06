import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { enviroment } from '../../env/enviroments';
import { RequestCampaign, ResponseCampaign } from '../models/campaign';
import { SubscribedDonor } from '../models/donor';
import { AuthService } from './auth.service';
import { mapCampaignToApi, mapCampaignFromApi, mapCampaignArrayFromApi, mapDonorArrayFromApi } from '../utils/dto-mapper';
import { TotalBloodEstimated, TotalLivesSaved, BloodTypePercentage, GeographicDistribution } from '../models/metrics';

@Injectable({
  providedIn: 'root',
})
export class CampaignService {
  private baseUrl = `${enviroment.appServiceUrl}/campaigns`;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  getAllCampaigns(): Observable<ResponseCampaign[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>[]>(`${this.baseUrl}/`, { headers })
      .pipe(map(data => mapCampaignArrayFromApi(data) as ResponseCampaign[]));
  }

  getCampaignById(id: number): Observable<ResponseCampaign> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>>(`${this.baseUrl}/${id}`, { headers })
      .pipe(map(data => mapCampaignFromApi(data) as ResponseCampaign));
  }

  createCampaign(campaign: RequestCampaign): Observable<ResponseCampaign> {
    const headers = this.authService.getAuthHeaders();
    const body = mapCampaignToApi(campaign as unknown as Record<string, any>);
    return this.http.post<Record<string, any>>(`${this.baseUrl}/`, body, { headers })
      .pipe(map(data => mapCampaignFromApi(data) as ResponseCampaign));
  }

  updateCampaign(id: number, campaign: RequestCampaign): Observable<ResponseCampaign> {
    const headers = this.authService.getAuthHeaders();
    const body = mapCampaignToApi(campaign as unknown as Record<string, any>);
    return this.http.put<Record<string, any>>(`${this.baseUrl}/${id}`, body, { headers })
      .pipe(map(data => mapCampaignFromApi(data) as ResponseCampaign));
  }

  deleteCampaign(id: number): Observable<ResponseCampaign> {
    const headers = this.authService.getAuthHeaders();
    return this.http.delete<Record<string, any>>(`${this.baseUrl}/${id}`, { headers })
      .pipe(map(data => mapCampaignFromApi(data) as ResponseCampaign));
  }

  subscribeDonor(campaignId: number, donorId: number): Observable<ResponseCampaign> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post<Record<string, any>>(
      `${this.baseUrl}/${campaignId}/subscribe/${donorId}`,
      {},
      { headers }
    ).pipe(map(data => mapCampaignFromApi(data) as ResponseCampaign));
  }

  unsubscribeDonor(campaignId: number, donorId: number): Observable<SubscribedDonor[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.delete<Record<string, any>[]>(
      `${this.baseUrl}/${campaignId}/unsubscribe/${donorId}`,
      { headers }
    ).pipe(map(data => data as SubscribedDonor[]));
  }

  finishCampaign(campaignId: number, endDate: string): Observable<ResponseCampaign> {
    const headers = this.authService.getAuthHeaders();
    return this.http.put<Record<string, any>>(
      `${this.baseUrl}/${campaignId}/finish?endDate=${endDate}`,
      {},
      { headers }
    ).pipe(map(data => mapCampaignFromApi(data) as ResponseCampaign));
  }

  getAllFinishedCampaigns(): Observable<ResponseCampaign[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>[]>(`${this.baseUrl}/finished`, { headers })
      .pipe(map(data => mapCampaignArrayFromApi(data) as ResponseCampaign[]));
  }

  getSubscribedDonors(campaignId: number): Observable<SubscribedDonor[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>[]>(`${this.baseUrl}/subscribed/${campaignId}`, { headers })
      .pipe(map(data => mapDonorArrayFromApi(data) as unknown as SubscribedDonor[]));
  }

  notifyUpcomingCampaign(id: number | undefined): Observable<void> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post<void>(`${this.baseUrl}/${id}/notify-upcoming`, {}, { headers });
  }

  getCampaignsByOrganizer(organizerId: number): Observable<ResponseCampaign[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>[]>(`${this.baseUrl}/organizer/${organizerId}`, { headers })
      .pipe(map(data => mapCampaignArrayFromApi(data) as ResponseCampaign[]));
  }

  getActiveSubscribedCampaigns(donorId: number): Observable<ResponseCampaign[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>[]>(`${this.baseUrl}/subscribed-by/${donorId}`, { headers })
      .pipe(map(data => mapCampaignArrayFromApi(data) as ResponseCampaign[]));
  }

  unsubscribeFromCampaign(campaignId: number, donorId: number): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.delete(`${this.baseUrl}/${campaignId}/unsubscribe/${donorId}`, { headers });
  }

  getOrganizerBloodTotal(organizerId: number): Observable<TotalBloodEstimated> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>>(`${this.baseUrl}/metrics/organizer/${organizerId}/blood-total`, { headers })
      .pipe(map(data => ({
        totalSubscribers: data['total_subscribers'],
        totalCampaigns: data['total_campaigns'],
        estimatedMl: data['estimated_ml'],
        estimatedLiters: data['estimated_liters']
      })));
  }

  getOrganizerBloodTypePercentage(organizerId: number): Observable<BloodTypePercentage[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>[]>(`${this.baseUrl}/metrics/organizer/${organizerId}/blood-type-percentage`, { headers })
      .pipe(map(data => data.map(item => ({
        bloodType: item['blood_type'],
        count: item['count'],
        percentage: item['percentage']
      }))));
  }

  getOrganizerCampaignCount(organizerId: number): Observable<number> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<number>(`${this.baseUrl}/metrics/organizer/${organizerId}/campaign-count`, { headers });
  }

  getOrganizerFinishedCount(organizerId: number): Observable<number> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<number>(`${this.baseUrl}/metrics/organizer/${organizerId}/finished-count`, { headers });
  }

  getOrganizerTotalDonors(organizerId: number): Observable<number> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<number>(`${this.baseUrl}/metrics/organizer/${organizerId}/total-donors`, { headers });
  }

  getOrganizerAverageDonors(organizerId: number): Observable<number> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<number>(`${this.baseUrl}/metrics/organizer/${organizerId}/average-donors`, { headers });
  }

  getOrganizerLivesSaved(organizerId: number): Observable<TotalLivesSaved> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>>(`${this.baseUrl}/metrics/organizer/${organizerId}/lives-saved`, { headers })
      .pipe(map(data => ({
        totalSubscribers: data['total_subscribers'],
        totalFinishedCampaigns: data['total_finished_campaigns'],
        estimatedLivesSaved: data['estimated_lives_saved']
      })));
  }

  getOrganizerGeographicDistribution(organizerId: number): Observable<GeographicDistribution[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>[]>(`${this.baseUrl}/metrics/organizer/${organizerId}/geographic-distribution`, { headers })
      .pipe(map(data => data.map(item => ({
        campaignId: item['campaign_id'],
        title: item['title'],
        direction: item['direction'],
        latitude: item['latitude'],
        longitude: item['longitude'],
        isActive: item['is_active'],
        isFinished: item['is_finished']
      }))));
  }
}
