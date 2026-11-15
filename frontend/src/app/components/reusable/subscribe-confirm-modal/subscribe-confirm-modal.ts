import { Component, EventEmitter, Input, Output, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { DonorHealth } from '../../../models/metrics';
import { Gender } from '../../../models/donor';

type WarningLevel = 'ok' | 'warn' | 'danger';

interface RequirementCheck {
  label: string;
  tooltip: string;
  level: WarningLevel;
  showTooltip: boolean;
}

@Component({
  selector: 'app-subscribe-confirm-modal',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './subscribe-confirm-modal.html',
  styleUrls: ['./subscribe-confirm-modal.css']
})
export class SubscribeConfirmModalComponent implements OnChanges {
  @Input() isOpen = false;
  @Input() donorHealth: DonorHealth | null = null;
  @Input() donorGender: Gender | null = null;
  @Input() donorWeight: number | null = null;
  @Output() confirmed = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  checks: RequirementCheck[] = [];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['donorHealth'] || changes['donorGender'] || changes['donorWeight']) {
      this.buildChecks();
    }
  }

  private buildChecks(): void {
    this.checks = [];

    if (!this.donorHealth || !this.donorGender || this.donorWeight == null) {
      this.checks = this.getGenericChecks();
      return;
    }

    const h = this.donorHealth;
    const gender = this.donorGender;
    const weight = this.donorWeight;

    // Peso
    let weightLevel: WarningLevel = 'ok';
    if (weight < 50) weightLevel = 'danger';
    else if (weight <= 55) weightLevel = 'warn';
    this.checks.push({
      label: 'Peso mínimo 50 kg',
      tooltip: `Peso actual: ${weight} kg`,
      level: weightLevel,
      showTooltip: true
    });

    // Edad
    let ageLevel: WarningLevel = 'ok';
    if (h.age < 18) ageLevel = 'danger';
    else if (h.age < 21 || h.age > 60) ageLevel = 'warn';
    this.checks.push({
      label: 'Edad entre 18 y 65 años',
      tooltip: `Edad actual: ${h.age} años`,
      level: ageLevel,
      showTooltip: true
    });

    // Última donación
    const minGap = gender === Gender.FEMALE ? 90 : 60;
    const gapLabel = gender === Gender.FEMALE ? '3 meses' : '2 meses';
    let donationLevel: WarningLevel = 'ok';
    let donationTooltip = 'No se registran donaciones anteriores';
    if (h.lastDonationDate) {
      const parts = h.lastDonationDate.split('-');
      const lastDate = new Date(+parts[2], +parts[1] - 1, +parts[0]);
      const now = new Date();
      let years = now.getFullYear() - lastDate.getFullYear();
      let months = now.getMonth() - lastDate.getMonth();
      let days = now.getDate() - lastDate.getDate();
      if (days < 0) {
        months--;
        const prevMonth = new Date(now.getFullYear(), now.getMonth(), 0);
        days += prevMonth.getDate();
      }
      if (months < 0) {
        years--;
        months += 12;
      }
      const parts_arr: string[] = [];
      if (years > 0) parts_arr.push(`${years} año${years > 1 ? 's' : ''}`);
      if (months > 0) parts_arr.push(`${months} mes${months > 1 ? 'es' : ''}`);
      if (days > 0) parts_arr.push(`${days} día${days > 1 ? 's' : ''}`);
      const formatted = parts_arr.length > 0 ? parts_arr.join(' y ') : 'menos de 1 día';
      if (years > 0 || months > 3 || (months === 3 && days >= 0)) donationLevel = 'ok';
      else if (months >= 2) donationLevel = 'warn';
      else donationLevel = 'danger';
      donationTooltip = `Última donación: hace ${formatted}`;
    }
    this.checks.push({
      label: `Esperar ${gapLabel} entre donaciones`,
      tooltip: donationTooltip,
      level: donationLevel,
      showTooltip: true
    });

    // Genéricos (sin tooltip personalizado)
    this.checks.push({ label: 'Sin tatuajes/piercing de 6 meses', tooltip: '', level: 'ok', showTooltip: false });
    this.checks.push({ label: 'Sin antibióticos en 15 días', tooltip: '', level: 'ok', showTooltip: false });
    if (gender === Gender.FEMALE) {
      this.checks.push({ label: '6 meses sin embarazo', tooltip: '', level: 'ok', showTooltip: false });
    }
    this.checks.push({ label: 'Presentar documento válido', tooltip: '', level: 'ok', showTooltip: false });
    this.checks.push({ label: 'Comer sano y tomar agua previamente', tooltip: '', level: 'ok', showTooltip: false });
  }

  private getGenericChecks(): RequirementCheck[] {
    return [
      { label: 'Peso mínimo 50 kg', tooltip: '', level: 'ok', showTooltip: false },
      { label: 'Edad entre 18 y 65 años', tooltip: '', level: 'ok', showTooltip: false },
      { label: 'Esperar 2-3 meses entre donaciones', tooltip: '', level: 'ok', showTooltip: false },
      { label: 'Sin tatuajes/piercing de 6 meses', tooltip: '', level: 'ok', showTooltip: false },
      { label: 'Sin antibióticos en 15 días', tooltip: '', level: 'ok', showTooltip: false },
      { label: '6 meses sin embarazo', tooltip: '', level: 'ok', showTooltip: false },
      { label: 'Presentar documento válido', tooltip: '', level: 'ok', showTooltip: false },
      { label: 'Comer sano y tomar agua previamente', tooltip: '', level: 'ok', showTooltip: false }
    ];
  }

  confirm(): void { this.confirmed.emit(); }
  cancel(): void { this.cancelled.emit(); }

  onOverlayClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('subscribe-confirm-overlay')) {
      this.cancel();
    }
  }
}
