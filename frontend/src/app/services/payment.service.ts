import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { enviroment } from '../../env/enviroments';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private baseUrl = `${enviroment.appServiceUrl}/payments`;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  donate(amount: number): Observable<string> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post(`${this.baseUrl}/donate`, { amount }, {
      headers,
      responseType: 'text'
    });
  }
}
