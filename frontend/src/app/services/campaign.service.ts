import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { enviroment } from '../../env/enviroments';
import { RequestCampaign, ResponseCampaign } from '../models/campaign';
import { SubscribedDonor } from '../models/donor';
import { AuthService } from './auth.service';
import { mapCampaignToApi, mapCampaignFromApi, mapCampaignArrayFromApi, mapDonorArrayFromApi } from '../utils/dto-mapper';

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
}
