import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { enviroment } from '../../env/enviroments';
import { RequestOrganizerPer, ResponseOrganizerPer } from '../models/organizer-per';
import { AuthService } from './auth.service';
import { mapOrganizerPerToApi, mapOrganizerPerFromApi, mapOrganizerPerArrayFromApi } from '../utils/dto-mapper';

@Injectable({
  providedIn: 'root',
})
export class OrganizerPerService {
  private baseUrl = `${enviroment.appServiceUrl}/organizer-pers`;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  getAllOrganizerPers(): Observable<ResponseOrganizerPer[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>[]>(`${this.baseUrl}/`, { headers })
      .pipe(map(data => mapOrganizerPerArrayFromApi(data) as ResponseOrganizerPer[]));
  }

  getOrganizerPerById(id: number): Observable<ResponseOrganizerPer> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>>(`${this.baseUrl}/${id}`, { headers })
      .pipe(map(data => mapOrganizerPerFromApi(data) as ResponseOrganizerPer));
  }

  createOrganizerPer(organizerPer: RequestOrganizerPer): Observable<ResponseOrganizerPer> {
    const body = mapOrganizerPerToApi(organizerPer as unknown as Record<string, any>);
    return this.http.post<Record<string, any>>(`${this.baseUrl}/auth`, body)
      .pipe(map(data => mapOrganizerPerFromApi(data) as ResponseOrganizerPer));
  }

  updateOrganizerPer(id: number, organizerPer: RequestOrganizerPer): Observable<ResponseOrganizerPer> {
    const headers = this.authService.getAuthHeaders();
    const body = mapOrganizerPerToApi(organizerPer as unknown as Record<string, any>);
    return this.http.put<Record<string, any>>(`${this.baseUrl}/${id}`, body, { headers })
      .pipe(map(data => mapOrganizerPerFromApi(data) as ResponseOrganizerPer));
  }

  deleteOrganizerPer(id: number): Observable<ResponseOrganizerPer> {
    const headers = this.authService.getAuthHeaders();
    return this.http.delete<Record<string, any>>(`${this.baseUrl}/${id}`, { headers })
      .pipe(map(data => mapOrganizerPerFromApi(data) as ResponseOrganizerPer));
  }
}
