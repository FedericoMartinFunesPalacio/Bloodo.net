import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { enviroment } from '../../env/enviroments';
import { RequestOrganizerEmp, ResponseOrganizerEmp } from '../models/organizer-emp';
import { AuthService } from './auth.service';
import { mapOrganizerEmpToApi, mapOrganizerEmpFromApi, mapOrganizerEmpArrayFromApi } from '../utils/dto-mapper';

@Injectable({
  providedIn: 'root',
})
export class OrganizerEmpService {
  private baseUrl = `${enviroment.appServiceUrl}/organizer-emps`;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  getAllOrganizerEmps(): Observable<ResponseOrganizerEmp[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>[]>(`${this.baseUrl}/`, { headers })
      .pipe(map(data => mapOrganizerEmpArrayFromApi(data) as ResponseOrganizerEmp[]));
  }

  getOrganizerEmpById(id: number): Observable<ResponseOrganizerEmp> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<Record<string, any>>(`${this.baseUrl}/${id}`, { headers })
      .pipe(map(data => mapOrganizerEmpFromApi(data) as ResponseOrganizerEmp));
  }

  createOrganizerEmp(organizerEmp: RequestOrganizerEmp): Observable<ResponseOrganizerEmp> {
    const body = mapOrganizerEmpToApi(organizerEmp as unknown as Record<string, any>);
    return this.http.post<Record<string, any>>(`${this.baseUrl}/auth`, body)
      .pipe(map(data => mapOrganizerEmpFromApi(data) as ResponseOrganizerEmp));
  }

  updateOrganizerEmp(id: number, organizerEmp: RequestOrganizerEmp): Observable<ResponseOrganizerEmp> {
    const headers = this.authService.getAuthHeaders();
    const body = mapOrganizerEmpToApi(organizerEmp as unknown as Record<string, any>);
    return this.http.put<Record<string, any>>(`${this.baseUrl}/${id}`, body, { headers })
      .pipe(map(data => mapOrganizerEmpFromApi(data) as ResponseOrganizerEmp));
  }

  deleteOrganizerEmp(id: number): Observable<ResponseOrganizerEmp> {
    const headers = this.authService.getAuthHeaders();
    return this.http.delete<Record<string, any>>(`${this.baseUrl}/${id}`, { headers })
      .pipe(map(data => mapOrganizerEmpFromApi(data) as ResponseOrganizerEmp));
  }
}
