import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CampaignService } from '../../services/campaign.service';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';
import { EnumLabelPipe } from '../../pipes/enum-label.pipe';
import { ResponseCampaign } from '../../models/campaign';
import { SubscribedDonor } from '../../models/donor';
import { TotalBloodEstimated, TotalLivesSaved, BloodTypePercentage, GeographicDistribution } from '../../models/metrics';
import { animate, stagger } from 'animejs';
import { MatIcon } from '@angular/material/icon';
import { LoadingComponent } from '../loading/loading';

@Component({
  selector: 'app-my-campaigns',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, EnumLabelPipe, MatIcon, LoadingComponent],
  templateUrl: './my-campaigns.html',
  styleUrls: ['./my-campaigns.css']
})
export class MyCampaignsComponent implements OnInit {
  campaigns: ResponseCampaign[] = [];
  loading = true;
  organizerId: number = 0;

  activeTab: 'info' | 'metrics' = 'info';

  showSubscribedList = false;
  subscribedDonors: SubscribedDonor[] = [];
  selectedCampaignId: number | null = null;

  finishCampaignId: number | null = null;
  finishDate: string = '';

  metricsLoading = true;
  bloodTotal: TotalBloodEstimated | null = null;
  livesTotal: TotalLivesSaved | null = null;
  bloodTypePercentages: BloodTypePercentage[] = [];
  campaignCount = 0;
  finishedCount = 0;
  totalDonors = 0;
  averageDonors = 0;
  geoDistribution: GeographicDistribution[] = [];

  hoveredBt: BloodTypePercentage | null = null;
  fillHeight = 0;
  pieGradient = '';

  private readonly btColors = [
    '#ef4444', '#f97316', '#eab308', '#22c55e',
    '#3b82f6', '#8b5cf6', '#ec4899', '#6b7280'
  ];

  constructor(
    private campaignService: CampaignService,
    private authService: AuthService,
    private router: Router,
    private toast: ToastService
  ) {

  }

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe(user => {
      if (user?.roleId) {
        this.organizerId = Number(user.roleId);
        this.loadMyCampaigns();
      }
    });

  }

  private notifyUpcoming(campaignsList: ResponseCampaign[]): void {
    for (const campaign of campaignsList) {
      this.campaignService.notifyUpcomingCampaign(campaign.id).subscribe({
        next: () => {
        },
        error: (err) => {
          console.error('Error checking upcoming campaign:', err);
        }
      })
    }
  }

  private loadMyCampaigns(): void {
    this.loading = true;
    this.campaignService.getCampaignsByOrganizer(this.organizerId).subscribe({
      next: (campaigns) => {
        this.campaigns = campaigns;
        this.loading = false;
        this.notifyUpcoming(campaigns);
        this.animateCards();
      },
      error: (err) => {
        this.toast.error('Error al cargar tus campañas');
        this.loading = false;
        console.error('Error loading my campaigns:', err);
      }
    });
  }

  switchTab(tab: 'info' | 'metrics'): void {
    this.activeTab = tab;
    if (tab === 'info') {
      this.loadMyCampaigns();
    }
    if (tab === 'metrics') {
      this.bloodTotal = null;
      this.livesTotal = null;
      this.fillHeight = 0;
      this.loadMetrics();
    }
  }

  onMetricsTabClick(): void {
    if (!this.hasFinishedCampaigns) {
      this.toast.warning('Finalizá al menos una campaña para ver las métricas');
      return;
    }
    this.switchTab('metrics');
  }

  get hasFinishedCampaigns(): boolean {
    return this.campaigns.some(c => !!c.endDate);
  }

  get activeCampaigns(): ResponseCampaign[] {
    return this.campaigns.filter(c => !c.endDate);
  }

  getBtColor(index: number): string {
    return this.btColors[index % this.btColors.length];
  }

  private computePieGradient(): void {
    if (!this.bloodTypePercentages.length) {
      this.pieGradient = '';
      return;
    }
    const stops: string[] = [];
    let acc = 0;
    for (let i = 0; i < this.bloodTypePercentages.length; i++) {
      const bt = this.bloodTypePercentages[i];
      const start = acc;
      acc += bt.percentage;
      stops.push(`${this.getBtColor(i)} ${start}% ${acc}%`);
    }
    this.pieGradient = `conic-gradient(${stops.join(', ')})`;
  }

  getVBarHeight(): number {
    if (this.totalDonors === 0) return 0;
    return Math.min((this.averageDonors / this.totalDonors) * 100, 100);
  }

  private loadMetrics(): void {
    this.metricsLoading = true;
    let loaded = 0;
    const total = 8;
    const onDone = () => {
      loaded++;
      if (loaded >= total) {
        this.metricsLoading = false;
        this.animateMetrics();
      }
    };

    this.campaignService.getOrganizerBloodTotal(this.organizerId).subscribe({
      next: (data) => {
        this.bloodTotal = data;
        this.animateBloodDrop(data.estimatedLiters);
      },
      error: () => {},
      complete: onDone
    });
    this.campaignService.getOrganizerLivesSaved(this.organizerId).subscribe({
      next: (data) => this.livesTotal = data,
      error: () => {},
      complete: onDone
    });
    this.campaignService.getOrganizerBloodTypePercentage(this.organizerId).subscribe({
      next: (data) => {
        this.bloodTypePercentages = data;
        this.computePieGradient();
      },
      error: () => {},
      complete: onDone
    });
    this.campaignService.getOrganizerCampaignCount(this.organizerId).subscribe({
      next: (data) => this.campaignCount = data,
      error: () => {},
      complete: onDone
    });
    this.campaignService.getOrganizerFinishedCount(this.organizerId).subscribe({
      next: (data) => this.finishedCount = data,
      error: () => {},
      complete: onDone
    });
    this.campaignService.getOrganizerTotalDonors(this.organizerId).subscribe({
      next: (data) => this.totalDonors = data,
      error: () => {},
      complete: onDone
    });
    this.campaignService.getOrganizerAverageDonors(this.organizerId).subscribe({
      next: (data) => this.averageDonors = data,
      error: () => {},
      complete: onDone
    });
    this.campaignService.getOrganizerGeographicDistribution(this.organizerId).subscribe({
      next: (data) => this.geoDistribution = data,
      error: () => {},
      complete: onDone
    });
  }

  private animateBloodDrop(liters: number): void {
    setTimeout(() => {
      const maxLiters = Math.max(liters, 1);
      const pct = Math.min(liters / maxLiters, 1);
      this.fillHeight = pct * 170;
    }, 100);
  }

  private animateMetrics(): void {
    setTimeout(() => {
      const cards = document.querySelectorAll('.donation-card');
      if (cards.length) {
        animate(cards, {
          opacity: [0, 1],
          translateY: [20, 0],
          duration: 600,
          delay: stagger(120, { start: 200 }),
          ease: 'outQuad'
        });
      }

      const heart = document.querySelector('.lives-heart');
      if (heart) {
        animate(heart, {
          scale: [0.5, 1],
          duration: 1200,
          ease: 'outElastic(1, 0.4)',
          delay: 500
        });
      }

      const barFills = document.querySelectorAll('.campaigns-bar-finished, .hbar-fill');
      if (barFills.length) {
        barFills.forEach((el) => {
          const target = (el as HTMLElement).style.width || (el as HTMLElement).style.width;
          (el as HTMLElement).style.width = '0%';
          setTimeout(() => {
            (el as HTMLElement).style.width = target;
          }, 400);
        });
      }
    }, 50);
  }

  private animateCards(): void {
    setTimeout(() => {
      const header = document.querySelector('.my-campaigns-header');
      const cards = document.querySelectorAll('.campaign-card');
      if (header) {
        animate(header as any, { opacity: [0, 1], translateY: [20, 0], duration: 500, ease: 'outQuad' });
      }
      if (cards.length) {
        animate(cards, {
          opacity: [0, 1],
          scale: [0.95, 1],
          translateY: [20, 0],
          duration: 450,
          delay: stagger(60, { start: 150 }),
          ease: 'outQuad'
        });
      }
    }, 50);
  }

  viewCampaignDetail(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/campaigns', id]);
    }
  }

  editCampaign(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/campaigns', id, 'edit']);
    }
  }

  async deleteCampaign(id: number | undefined): Promise<void> {
    if (!id) return;

    const confirmed = await this.toast.confirm('¿Estás seguro de que deseas eliminar esta campaña?');
    if (!confirmed) return;

    this.campaignService.deleteCampaign(id).subscribe({
      next: () => {
        this.campaigns = this.campaigns.filter(c => c.id !== id);
        this.toast.success('Campaña eliminada correctamente');
      },
      error: (err) => {
        this.toast.error('Error al eliminar la campaña');
        console.error('Error deleting campaign:', err);
      }
    });
  }

  openFinishModal(campaignId: number | undefined): void {
    if (!campaignId) return;
    this.finishCampaignId = campaignId;
    this.finishDate = '';
  }

  closeFinishModal(): void {
    this.finishCampaignId = null;
    this.finishDate = '';
  }

  confirmFinish(): void {
    if (!this.finishCampaignId || !this.finishDate) {
      this.toast.warning('Seleccioná una fecha de finalización');
      return;
    }

    this.campaignService.finishCampaign(this.finishCampaignId, this.finishDate).subscribe({
      next: () => {
        this.toast.success('Campaña finalizada correctamente');
        this.closeFinishModal();
        this.loadMyCampaigns();
      },
      error: (err) => {
        this.toast.error('Error al finalizar la campaña');
        console.error('Error finishing campaign:', err);
      }
    });
  }

  toggleSubscribedList(campaignId: number | undefined): void {
    if (!campaignId) return;

    if (this.selectedCampaignId === campaignId && this.showSubscribedList) {
      this.showSubscribedList = false;
      this.selectedCampaignId = null;
      this.subscribedDonors = [];
      return;
    }

    this.selectedCampaignId = campaignId;
    this.showSubscribedList = true;
    this.subscribedDonors = [];
    this.campaignService.getSubscribedDonors(campaignId).subscribe({
      next: (donors) => {
        this.subscribedDonors = donors;
      },
      error: (err) => {
        this.showSubscribedList = false;
        this.selectedCampaignId = null;
        this.toast.error('Error al cargar los donadores suscritos');
        console.error('Error loading subscribed donors:', err);
      }
    });
  }
}
