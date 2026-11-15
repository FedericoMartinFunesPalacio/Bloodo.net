import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { enviroment } from '../../env/enviroments';
import { RequestUser, ResponseUser, LoginResponse } from '../models/user';
import { mapUserToApi} from '../utils/dto-mapper';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private baseUrl = `${enviroment.appServiceUrl}/auth`;
  private tokenKey = 'authToken';
  private currentUserSubject = new BehaviorSubject<ResponseUser | null>(null);

  constructor(private http: HttpClient) {
    this.initializeCurrentUser();
  }

  private initializeCurrentUser(): void {
    const token = localStorage.getItem(this.tokenKey);
    if (!token) {
      localStorage.removeItem('currentUser');
      return;
    }
    const userJson = localStorage.getItem('currentUser');
    if (userJson) {
      try {
        const user = JSON.parse(userJson);
        this.currentUserSubject.next(user);
      } catch (e) {
        localStorage.removeItem('currentUser');
      }
    }
  }

  register(user: RequestUser): Observable<ResponseUser> {
    const body = mapUserToApi(user as unknown as Record<string, any>);
    return this.http.post<ResponseUser>(`${this.baseUrl}/register`, body);
  }

  login(credentials: RequestUser): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, {
      username: credentials.username,
      password: credentials.password
    });
  }

  resetPasswordFirstStep(email: string): Observable<string> {
    return this.http.post(`${this.baseUrl}/reset-request`, email, {
      responseType: 'text'
    });
  }

  resetPasswordSecondStep(email: string, password: string): Observable<boolean> {
    return this.http.post<boolean>(`${this.baseUrl}/reset-password`, { email, password });
  }

  saveToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }


  saveCurrentUser(user: ResponseUser): void {
    localStorage.setItem('currentUser', JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  getCurrentUser(): Observable<ResponseUser | null> {
    return this.currentUserSubject.asObservable();
  }
}

