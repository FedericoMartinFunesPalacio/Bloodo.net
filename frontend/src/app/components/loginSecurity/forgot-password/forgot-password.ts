import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { ToastService } from '../../../services/toast.service';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIcon],
  templateUrl: './forgot-password.html',
  styleUrls: ['./forgot-password.css']
})
export class ForgotPasswordComponent {
  step: 1 | 2 | 3 = 1;
  email: string = '';
  code: string = '';
  receivedCode: string = '';
  newPassword: string = '';
  confirmPassword: string = '';
  loading = false;
  showPassword = false;
  showConfirmPassword = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private toast: ToastService
  ) {}

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPassword(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  onSubmitStep1(): void {
    if (!this.email) {
      this.toast.error('El email es requerido');
      return;
    }
    this.loading = true;
    this.authService.resetPasswordFirstStep(this.email).subscribe({
      next: (code) => {
        this.receivedCode = code;
        this.loading = false;
        this.step = 2;
        this.toast.success('Código enviado a tu email');
      },
      error: (err) => {
        this.loading = false;
        this.toast.error('Email no encontrado');
        console.error('Reset request error:', err);
      }
    });
  }

  onSubmitStep2(): void {
    if (!this.code) {
      this.toast.error('El código es requerido');
      return;
    }
    if (this.code !== this.receivedCode) {
      this.toast.error('El código ingresado es incorrecto');
      return;
    }
    this.step = 3;
  }

  onSubmitStep3(): void {
    if (!this.newPassword || !this.confirmPassword) {
      this.toast.error('Ambas contraseñas son requeridas');
      return;
    }
    if (this.newPassword !== this.confirmPassword) {
      this.toast.error('Las contraseñas no coinciden');
      return;
    }
    this.loading = true;
    this.authService.resetPasswordSecondStep(this.email, this.newPassword).subscribe({
      next: () => {
        this.loading = false;
        this.toast.success('Contraseña restablecida exitosamente');
        this.router.navigate(['/auth']);
      },
      error: (err) => {
        this.loading = false;
        this.toast.error('Error al restablecer la contraseña');
        console.error('Reset password error:', err);
      }
    });
  }

  goBack(): void {
    if (this.step > 1) {
      this.step--;
    } else {
      this.router.navigate(['/auth']);
    }
  }

  goToLogin(): void {
    this.router.navigate(['/auth']);
  }
}
