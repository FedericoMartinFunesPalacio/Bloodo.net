import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { DonorService } from '../../services/donor.service';
import { DonorStats, DonorHealth } from '../../models/metrics';
import { ResponseUser } from '../../models/user';
import { EnumLabelPipe } from '../../pipes/enum-label.pipe';
import { animate, stagger } from 'animejs';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-my-donations',
  standalone: true,
  imports: [CommonModule, EnumLabelPipe, MatIcon],
  templateUrl: './my-donations.html',
  styleUrls: ['./my-donations.css']
})
export class MyDonationsComponent implements OnInit {
  stats: DonorStats | null = null;
  health: DonorHealth | null = null;
  loading = true;
  error = false;
  private animated = false;

  constructor(
    private authService: AuthService,
    private donorService: DonorService
  ) {}

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe(user => {
      if (user && user.roleId) {
        this.loadData(Number(user.roleId));
      }
    });
  }

  private loadData(donorId: number): void {
    this.loading = true;
    this.donorService.getDonorStats(donorId).subscribe({
      next: (stats) => {
        this.stats = stats;
        this.loading = false;
        this.animateContent();
      },
      error: () => {
        this.error = true;
        this.loading = false;
      }
    });
    this.donorService.getDonorHealth(donorId).subscribe({
      next: (health) => {
        this.health = health;
        this.animateHealthCards();
      },
      error: () => {}
    });
  }

  private animateContent(): void {
    setTimeout(() => {
      const header = document.querySelector('.page-header');
      const statCards = document.querySelectorAll('.stat-card');
      if (header) {
        animate(header as any, { opacity: [0, 1], translateY: [20, 0], duration: 500, ease: 'outQuad' });
      }
      if (statCards.length) {
        animate(statCards, {
          opacity: [0, 1],
          scale: [0.9, 1],
          translateY: [20, 0],
          duration: 450,
          delay: stagger(100, { start: 150 }),
          ease: 'outQuad'
        });
      }
    }, 50);
  }

  private animateHealthCards(): void {
    if (this.animated) return;
    this.animated = true;
    setTimeout(() => {
      const healthCards = document.querySelectorAll('.health-card');
      if (healthCards.length) {
        animate(healthCards, {
          opacity: [0, 1],
          scale: [0.9, 1],
          duration: 400,
          delay: stagger(80, { start: 100 }),
          ease: 'outQuad'
        });
      }
    }, 50);
  }

  getBmiCategory(bmi: number): string {
    if (bmi < 18.5) return 'Bajo peso';
    if (bmi < 25) return 'Normal';
    if (bmi < 30) return 'Sobrepeso';
    return 'Obesidad';
  }

  getBmiColor(bmi: number): string {
    if (bmi < 18.5) return '#f39c12';
    if (bmi < 25) return '#27ae60';
    if (bmi < 30) return '#f39c12';
    return '#e74c3c';
  }
}
