import { Component, OnInit, OnDestroy, AfterViewInit, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { CampaignService } from '../../services/campaign.service';
import { DonorService } from '../../services/donor.service';
import { OrganizerEmpService } from '../../services/organizer-emp.service';
import { OrganizerPerService } from '../../services/organizer-per.service';
import { RoleService } from '../../services/role.service';
import { ToastService } from '../../services/toast.service';
import { MetricsService } from '../../services/metrics.service';
import { EnumLabelPipe } from '../../pipes/enum-label.pipe';
import { ResponseCampaign } from '../../models/campaign';
import { TotalBloodEstimated, TotalLivesSaved, BloodTypePercentage } from '../../models/metrics';
import { animate, stagger } from 'animejs';
import { MatIcon } from '@angular/material/icon';

interface StatCard {
  label: string;
  value: number;
  icon: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, EnumLabelPipe, MatIcon],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent implements OnInit, OnDestroy, AfterViewInit {
  isAdmin = false;
  isDonor = false;
  isOrganizer = false;
  loading = true;
  username = '';

  stats: StatCard[] = [];
  recentCampaigns: ResponseCampaign[] = [];
  bloodTotal: TotalBloodEstimated | null = null;
  livesTotal: TotalLivesSaved | null = null;
  bloodTypePercentages: BloodTypePercentage[] = [];
  currentBloodTypeIndex = 0;
  private carouselInterval: any = null;
  private dataLoaded = false;

  constructor(
    private authService: AuthService,
    private campaignService: CampaignService,
    private donorService: DonorService,
    private organizerEmpService: OrganizerEmpService,
    private organizerPerService: OrganizerPerService,
    private roleService: RoleService,
    private toast: ToastService,
    private metricsService: MetricsService,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe(user => {
      this.username = user?.username || '';
    });

    this.roleService.isAdmin().subscribe(v => this.isAdmin = v);
    this.roleService.isDonor().subscribe(v => this.isDonor = v);
    this.roleService.isOrganizer().subscribe(v => this.isOrganizer = v);

    this.loadDashboardData();
    this.loadMetrics();
  }

  ngAfterViewInit(): void {
    const header = document.querySelector('.home-header');
    if (header) {
      animate(header.children as any, {
        opacity: [0, 1],
        translateY: [20, 0],
        duration: 500,
        delay: stagger(100),
        ease: 'outQuad'
      });
    }
  }

  ngOnDestroy(): void {
    if (this.carouselInterval) {
      clearInterval(this.carouselInterval);
    }
  }

  private loadMetrics(): void {
    this.metricsService.getTotalBloodEstimated().subscribe({
      next: (data) => this.bloodTotal = data,
      error: () => {}
    });
    this.metricsService.getTotalLivesSaved().subscribe({
      next: (data) => this.livesTotal = data,
      error: () => {}
    });
    this.metricsService.getBloodTypePercentage().subscribe({
      next: (data) => {
        this.bloodTypePercentages = data;
        if (data.length > 0) {
          this.startCarousel();
        }
      },
      error: () => {}
    });
  }

  private startCarousel(): void {
    this.carouselInterval = setInterval(() => {
      this.currentBloodTypeIndex = (this.currentBloodTypeIndex + 1) % this.bloodTypePercentages.length;
    }, 3000);
  }

  get currentBloodType(): BloodTypePercentage | null {
    if (this.bloodTypePercentages.length === 0) return null;
    return this.bloodTypePercentages[this.currentBloodTypeIndex];
  }

  private loadDashboardData(): void {
    this.loading = true;

    this.roleService.isAdmin().subscribe(isAdm => {
      if (isAdm) {
        this.loadAdminDashboard();
      } else {
        this.roleService.isOrganizer().subscribe(isOrg => {
          if (isOrg) {
            this.loadOrganizerDashboard();
          } else {
            this.loadDonorDashboard();
          }
        });
      }
    });
  }

  private loadAdminDashboard(): void {
    forkJoin({
      campaigns: this.campaignService.getAllCampaigns(),
      finishedCampaigns: this.campaignService.getAllFinishedCampaigns(),
      donors: this.donorService.getAllDonors(),
      empOrgs: this.organizerEmpService.getAllOrganizerEmps(),
      perOrgs: this.organizerPerService.getAllOrganizerPers()
    }).subscribe({
      next: ({ campaigns, finishedCampaigns, donors, empOrgs, perOrgs }) => {
        this.stats = [
          { label: 'Campañas Activas', value: campaigns.length, icon: '📋' },
          { label: 'Campañas Finalizadas', value: finishedCampaigns.length, icon: '✅' },
          { label: 'Donadores', value: donors.length, icon: '🩸' },
          { label: 'Organizadores', value: empOrgs.length + perOrgs.length, icon: '🏢' }
        ];
        this.recentCampaigns = campaigns.slice(0, 5);
        this.loading = false;
        this.animateDashboardContent();
      },
      error: () => {
        this.toast.error('Error al cargar el dashboard');
        this.loading = false;
      }
    });
  }

  private loadOrganizerDashboard(): void {
    this.authService.getCurrentUser().subscribe(user => {
      const orgId = user?.roleId ? Number(user.roleId) : 0;

      forkJoin({
        campaigns: this.campaignService.getAllCampaigns(),
        finishedCampaigns: this.campaignService.getAllFinishedCampaigns()
      }).subscribe({
        next: ({ campaigns, finishedCampaigns }) => {
          const myActive = campaigns.filter(c => c.organizerId === orgId);
          const myFinished = finishedCampaigns.filter(c => c.organizerId === orgId);

          this.stats = [
            { label: 'Mis Campañas Activas', value: myActive.length, icon: '📋' },
            { label: 'Mis Campañas Finalizadas', value: myFinished.length, icon: '✅' },
            { label: 'Total Campañas', value: myActive.length + myFinished.length, icon: '📊' }
          ];
          this.recentCampaigns = myActive.slice(0, 5);
          this.loading = false;
          this.animateDashboardContent();
        },
        error: () => {
          this.toast.error('Error al cargar el dashboard');
          this.loading = false;
        }
      });
    });
  }

  private loadDonorDashboard(): void {
    this.campaignService.getAllCampaigns().subscribe({
      next: (campaigns) => {
        this.stats = [
          { label: 'Campañas Disponibles', value: campaigns.length, icon: '📋' },
          { label: 'Campañas Próximas', value: campaigns.filter(c => c.startDate).length, icon: '📅' }
        ];
        this.recentCampaigns = campaigns.slice(0, 5);
        this.loading = false;
        this.animateDashboardContent();
      },
      error: () => {
        this.toast.error('Error al cargar el dashboard');
        this.loading = false;
      }
    });
  }

  private animateDashboardContent(): void {
    if (this.dataLoaded) return;
    this.dataLoaded = true;

    this.ngZone.runOutsideAngular(() => {
      setTimeout(() => {
        const heroCards = document.querySelectorAll('.hero-card');
        if (heroCards.length) {
          animate(heroCards, {
            opacity: [0, 1],
            translateY: [30, 0],
            scale: [0.95, 1],
            duration: 600,
            delay: stagger(150),
            ease: 'outQuad'
          });
        }

        const statCards = document.querySelectorAll('.stat-card');
        if (statCards.length) {
          animate(statCards, {
            opacity: [0, 1],
            scale: [0.85, 1],
            translateY: [20, 0],
            duration: 500,
            delay: stagger(80, { start: 200 }),
            ease: 'outQuad'
          });
        }

        const campaignItems = document.querySelectorAll('.campaign-item');
        if (campaignItems.length) {
          animate(campaignItems, {
            opacity: [0, 1],
            translateX: [-20, 0],
            duration: 400,
            delay: stagger(60, { start: 300 }),
            ease: 'outQuad'
          });
        }

        const actionCards = document.querySelectorAll('.action-card');
        if (actionCards.length) {
          animate(actionCards, {
            opacity: [0, 1],
            scale: [0.85, 1],
            duration: 400,
            delay: stagger(60, { start: 400 }),
            ease: 'outQuad'
          });
        }
      }, 50);
    });
  }
}
