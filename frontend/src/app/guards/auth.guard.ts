import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable, map } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { UserRole } from '../models/user';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/']);
      return new Observable(obs => obs.next(false));
    }
    return new Observable(obs => obs.next(true));
  }
}

@Injectable({
  providedIn: 'root',
})
export class RoleGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/']);
      return new Observable(obs => obs.next(false));
    }

    const requiredRoles: UserRole[] = route.data['roles'] || [];

    return this.authService.getCurrentUser().pipe(
      map(user => {
        if (!user || !requiredRoles.includes(user.role)) {
          this.router.navigate(['/campaigns']);
          return false;
        }
        return true;
      })
    );
  }
}

