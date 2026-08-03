import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PaymentService } from '../../../services/payment.service';
import { ToastService } from '../../../services/toast.service';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-donate-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIcon],
  templateUrl: './donate-modal.html',
  styleUrls: ['./donate-modal.css']
})
export class DonateModalComponent {
  visible = false;
  loading = false;
  selectedAmount: number | null = null;
  customAmount: string = '';

  readonly presets = [500, 1000, 2000, 5000];

  constructor(
    private paymentService: PaymentService,
    private toast: ToastService
  ) {}

  open(): void {
    this.visible = true;
    this.selectedAmount = null;
    this.customAmount = '';
  }

  close(): void {
    if (!this.loading) {
      this.visible = false;
    }
  }

  selectPreset(amount: number): void {
    this.selectedAmount = amount;
    this.customAmount = '';
  }

  onCustomAmountChange(): void {
    this.selectedAmount = null;
  }

  get currentAmount(): number {
    if (this.selectedAmount) return this.selectedAmount;
    const parsed = parseFloat(this.customAmount);
    return isNaN(parsed) ? 0 : parsed;
  }

  onOverlayClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('donate-overlay')) {
      this.close();
    }
  }

  donate(): void {
    const amount = this.currentAmount;
    if (amount <= 0) {
      this.toast.warning('Seleccioná o ingresá un monto válido');
      return;
    }

    this.loading = true;
    this.paymentService.donate(amount).subscribe({
      next: (url) => {
        window.open(url, '_blank');
        this.toast.success('Redirigiendo a MercadoPago...');
        this.loading = false;
        this.visible = false;
      },
      error: (err) => {
        this.toast.error('Error al procesar la donación');
        this.loading = false;
        console.error('Donation error:', err);
      }
    });
  }
}
