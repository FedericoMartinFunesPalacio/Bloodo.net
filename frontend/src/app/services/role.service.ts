import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { UserRole } from '../models/user';
import { Observable, map } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RoleService {
  constructor(private authService: AuthService) {}

  isAdmin(): Observable<boolean> {
    return this.authService.getCurrentUser().pipe(
      map(user => user?.role === UserRole.ADMIN)
    );
  }

  isDonor(): Observable<boolean> {
    return this.authService.getCurrentUser().pipe(
      map(user => user?.role === UserRole.DONOR)
    );
  }

  isOrganizer(): Observable<boolean> {
    return this.authService.getCurrentUser().pipe(
      map(user => user?.role === UserRole.ORGANIZER)
    );
  }

  hasRole(role: UserRole): Observable<boolean> {
    return this.authService.getCurrentUser().pipe(
      map(user => user?.role === role)
    );
  }

  getCurrentRole(): Observable<UserRole | null> {
    return this.authService.getCurrentUser().pipe(
      map(user => user?.role || null)
    );
  }
}

