import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, map } from 'rxjs';
import { enviroment } from '../../env/enviroments';
import { Notification } from '../models/notification';
import {AuthService} from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private baseUrl = `${enviroment.appServiceUrl}/notifications`;
  private unreadCount$ = new BehaviorSubject<number>(0);

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  getNotifications(): Observable<Notification[]> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<any[]>(this.baseUrl, { headers }).pipe(
      map(list => list.map(n => ({
        id: n.id,
        userId: n.user_id,
        title: n.title,
        message: n.message,
        isRead: n.is_read,
        createdAt: n.created_at
      })))
    );
  }

  getUnreadCount(): Observable<number> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get<number>(`${this.baseUrl}/unread-count`, { headers });
  }

  markAsRead(id: number): Observable<Notification> {
    const headers = this.authService.getAuthHeaders();
    return this.http.put<Notification>(`${this.baseUrl}/${id}/read`, {}, { headers });
  }

  markAllAsRead(): Observable<void> {
    const headers = this.authService.getAuthHeaders();
    return this.http.put<void>(`${this.baseUrl}/read-all`, {}, { headers });
  }

  get unreadCount(): Observable<number> {
    return this.unreadCount$.asObservable();
  }

  refreshUnreadCount(): void {
    this.getUnreadCount().subscribe({
      next: (count) => this.unreadCount$.next(count),
      error: () => this.unreadCount$.next(0)
    });
  }
}
