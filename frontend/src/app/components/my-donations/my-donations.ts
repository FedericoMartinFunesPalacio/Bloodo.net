import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { DonorService } from '../../services/donor.service';
import { CampaignService } from '../../services/campaign.service';
import { ToastService } from '../../services/toast.service';
import { DonorStats, DonorHealth } from '../../models/metrics';
import { ResponseCampaign } from '../../models/campaign';
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
  subscribedCampaigns: ResponseCampaign[] = [];
  loading = true;
  error = false;
  donorId = 0;
  private animated = false;

  confirmUnsub = false;
  confirmCampaignId: number | null = null;
  confirmCampaignTitle = '';

  constructor(
    private authService: AuthService,
    private donorService: DonorService,
    private campaignService: CampaignService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe(user => {
      if (user && user.roleId) {
        this.donorId = Number(user.roleId);
        this.loadData(this.donorId);
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
    this.loadSubscribedCampaigns();
  }

  private loadSubscribedCampaigns(): void {
    this.campaignService.getActiveSubscribedCampaigns(this.donorId).subscribe({
      next: (campaigns) => {
        this.subscribedCampaigns = campaigns;
        this.animateSubscribedList();
      },
      error: () => {}
    });
  }

  unsubscribe(campaignId: number | undefined, campaignTitle: string): void {
    if (!campaignId) return;
    this.confirmCampaignId = campaignId;
    this.confirmCampaignTitle = campaignTitle;
    this.confirmUnsub = true;
  }

  confirmUnsubscribe(): void {
    if (!this.confirmCampaignId) return;
    this.campaignService.unsubscribeFromCampaign(this.confirmCampaignId, this.donorId).subscribe({
      next: () => {
        this.subscribedCampaigns = this.subscribedCampaigns.filter(c => c.id !== this.confirmCampaignId);
        this.toast.success('Te desuscribiste correctamente');
        this.cancelUnsub();
      },
      error: () => {
        this.toast.error('Error al desuscribirse');
        this.cancelUnsub();
      }
    });
  }

  cancelUnsub(): void {
    this.confirmUnsub = false;
    this.confirmCampaignId = null;
    this.confirmCampaignTitle = '';
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

  private animateSubscribedList(): void {
    setTimeout(() => {
      const items = document.querySelectorAll('.sub-campaign-item');
      if (items.length) {
        animate(items, {
          opacity: [0, 1],
          translateY: [12, 0],
          duration: 350,
          delay: stagger(50, { start: 100 }),
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
