import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { ToastService } from '../../../services/toast.service';
import {RequestUser, ResponseUser, UserRole} from '../../../models/user';
import {MatIcon} from '@angular/material/icon';
import { isValidEmail, isValidPhone } from '../../../utils/validators';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIcon, RouterLink],
  templateUrl: './auth.html',
  styleUrls: ['./auth.css']
})
export class AuthComponent {
  isLoginMode = true;
  registerStep: 1 | 2 = 1;
  username: string = '';
  password: string = '';
  email: string = '';
  phone: string = '';
  loading = false;
  showPassword = false;



  readonly donorRole = UserRole.DONOR;
  readonly organizerRole = UserRole.ORGANIZER;

  constructor(
    private authService: AuthService,
    private router: Router,
    private toast: ToastService
  ) {}

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  toggleMode(): void {
    this.isLoginMode = !this.isLoginMode;
    this.registerStep = 1;
    this.clearForm();
  }

  onSubmit(): void {
    if (!this.username || !this.password) {
      this.toast.error('Usuario y contraseña son requeridos');
      return;
    }

    if (this.isLoginMode) {
      this.login();
    } else {
      this.goToRoleSelection();
    }
  }

  goToRoleSelection(): void {
    if (!this.email) {
      this.toast.error('El email es requerido');
      return;
    }
    if (!isValidEmail(this.email)) {
      this.toast.error('El email no tiene un formato válido');
      return;
    }
    if (!this.phone) {
      this.toast.error('El teléfono es requerido');
      return;
    }
    if (!isValidPhone(this.phone)) {
      this.toast.error('El teléfono debe tener entre 8 y 13 dígitos, sin espacios ni signos');
      return;
    }
    this.registerStep = 2;
  }

  goBackToStep1(): void {
    this.registerStep = 1;
  }

  selectRole(role: UserRole): void {
    this.loading = true;
    const user: RequestUser = {
      username: this.username,
      password: this.password,
      email: this.email,
      phone: this.phone,
      role: role,
      roleId: 0
    };

    if (role === UserRole.ORGANIZER) {
      this.router.navigate(['/organizers/new'], { state: { user: user } });
    } else if (role === UserRole.DONOR) {
      this.router.navigate(['/donors/new'], { state: { user: user } });
    }
  }

  private login(): void {
    this.loading = true;
    const credentials: RequestUser = {
      username: this.username,
      password: this.password,
      email: '',
      phone: '',
      role: UserRole.DONOR,
      roleId: 0
    };

    this.authService.login(credentials).subscribe({
      next: (response) => {
        this.authService.saveToken(response.token);

        const user: ResponseUser = {
          username: this.username,
          id: response.id,
          email: response.email,
          phone: response.phone,
          role: response.role,
          roleId: response.roleId
        };
        this.authService.saveCurrentUser(user);

        this.loading = false;
        this.router.navigate(['/home']);
      },
      error: (err) => {
        this.loading = false;
        this.toast.error('Usuario o contraseña inválidos');
        console.error('Login error:', err);
      }
    });
  }

  private clearForm(): void {
    this.username = '';
    this.password = '';
    this.email = '';
    this.phone = '';
    this.registerStep = 1;
  }
}
