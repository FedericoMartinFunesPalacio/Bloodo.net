import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { enviroment } from '../../env/enviroments';
import { RequestDonor, ResponseDonor } from '../models/donor';
import { DonorStats, DonorHealth } from '../models/metrics';
import { AuthService } from './auth.service';
import { mapDonorToApi, mapDonorFromApi, mapDonorArrayFromApi } from '../utils/dto-mapper';

function mapFromApi(data: Record<string, any>): Record<string, any> {
  return mapDonorFromApi(data);
}

@Injectable({
  providedIn: 'root',
})
export class DonorService {
  private baseUrl = `${enviroment.appServiceUrl}/donors`;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  getAllDonors(): Observable<ResponseDonor[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>[]>(`${this.baseUrl}/`, { headers })
      .pipe(map(data => mapDonorArrayFromApi(data) as ResponseDonor[]));
  }

  getDonorById(id: number): Observable<ResponseDonor> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>>(`${this.baseUrl}/${id}`, { headers })
      .pipe(map(data => mapDonorFromApi(data) as ResponseDonor));
  }

  createDonor(donor: RequestDonor): Observable<ResponseDonor> {
    const body = mapDonorToApi(donor as unknown as Record<string, any>);
    return this.http.post<Record<string, any>>(`${this.baseUrl}/auth`, body)
      .pipe(map(data => mapDonorFromApi(data) as ResponseDonor));
  }

  updateDonor(id: number, donor: RequestDonor): Observable<ResponseDonor> {
    const headers = this.authService.getAuthHeaders();
    const body = mapDonorToApi(donor as unknown as Record<string, any>);
    return this.http.put<Record<string, any>>(`${this.baseUrl}/${id}`, body, { headers })
      .pipe(map(data => mapDonorFromApi(data) as ResponseDonor));
  }

  deleteDonor(id: number): Observable<ResponseDonor> {
    const headers = this.authService.getAuthHeaders();
    return this.http.delete<Record<string, any>>(`${this.baseUrl}/${id}`, { headers })
      .pipe(map(data => mapDonorFromApi(data) as ResponseDonor));
  }

  getDonorStats(): Observable<DonorStats> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>>(`${this.baseUrl}/me/metrics/stats`, { headers })
      .pipe(map(data => mapFromApi(data) as unknown as DonorStats));
  }

  getDonorHealth(): Observable<DonorHealth> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>>(`${this.baseUrl}/me/metrics/health`, { headers })
      .pipe(map(data => {
        const mapped = mapFromApi(data);
        if (mapped['lastDonationDate'] === 'null') mapped['lastDonationDate'] = null;
        if (mapped['nextEligibleDate'] === 'null') mapped['nextEligibleDate'] = null;
        return mapped as unknown as DonorHealth;
      }));
  }
}
