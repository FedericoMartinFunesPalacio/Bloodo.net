import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CampaignService } from '../../services/campaign.service';
import { RoleService } from '../../services/role.service';
import { ToastService } from '../../services/toast.service';
import { MetricsService } from '../../services/metrics.service';
import { EnumLabelPipe } from '../../pipes/enum-label.pipe';
import { ResponseCampaign } from '../../models/campaign';
import { SubscribedDonor } from '../../models/donor';
import { AuthService } from '../../services/auth.service';
import { CampaignBloodEstimated, CampaignLivesSaved, BloodTypeRanking } from '../../models/metrics';
import { MatIcon } from '@angular/material/icon';
import {DonorService} from '../../services/donor.service';
declare var google: any;

@Component({
  selector: 'app-campaign-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, EnumLabelPipe, MatIcon],
  templateUrl: './campaign-detail.html',
  styleUrls: ['./campaign-detail.css']
})
export class CampaignDetailComponent implements OnInit {
  campaign: ResponseCampaign | null = null;
  subscribedDonors: SubscribedDonor[] = [];
  loading = true;
  isOrganizer = false;
  isAdmin = false;
  isDonor = false;
  isSubscribed = false;

  activeTab: 'info' | 'metrics' = 'info';
  campaignBloodEstimated: CampaignBloodEstimated | null = null;
  campaignLivesSaved: CampaignLivesSaved | null = null;
  bloodTypeRanking: BloodTypeRanking[] = [];
  metricsLoading = false;

  constructor(
    private campaignService: CampaignService,
    private roleService: RoleService,
    private authService: AuthService,
    private donorService: DonorService,
    private route: ActivatedRoute,
    private router: Router,
    private toast: ToastService,
    private metricsService: MetricsService
  ) {}

  ngOnInit(): void {
    this.checkRoles();
    this.loadCampaignDetail();
  }

  loadMap(): void {
    if (!this.campaign) return;
    const location = { lat: this.campaign.latitude, lng: this.campaign.longitude };
    const map = new google.maps.Map(document.getElementById("map") as HTMLElement, {
      zoom: 15,
      center: location,
    });

    new google.maps.Marker({
      position: location,
      map: map,
      title: "Ubicación de la campaña"
    });
  }

  private checkRoles(): void {
    this.roleService.isOrganizer().subscribe(isOrg => this.isOrganizer = isOrg);
    this.roleService.isAdmin().subscribe(isAdm => this.isAdmin = isAdm);
    this.roleService.isDonor().subscribe(isDon => this.isDonor = isDon);
  }

  private loadCampaignDetail(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.toast.error('ID de campaña inválido');
      this.loading = false;
      return;
    }

    this.campaignService.getCampaignById(+id).subscribe({
      next: (campaign) => {
        this.campaign = campaign;
        this.loadSubscribedDonors(+id);
        this.loading = false;

        setTimeout(() => this.loadMap(), 100);
      },
      error: (err) => {
        this.toast.error('Error al cargar los detalles de la campaña');
        this.loading = false;
        console.error('Error loading campaign detail:', err);
      }
    });
  }

  private loadSubscribedDonors(campaignId: number): void {
    this.campaignService.getSubscribedDonors(campaignId).subscribe({
      next: (donors) => {
        this.subscribedDonors = donors;
        this.checkIfSubscribed();
      },
      error: (err) => {
        console.error('Error loading subscribed donors:', err);
      }
    });
  }

  private checkIfSubscribed(): void {
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        if (user?.roleId) {
          this.isSubscribed = this.subscribedDonors.some(d => d.id === Number(user.roleId));
        }
      }
    });
  }

  switchTab(tab: 'info' | 'metrics'): void {
    this.activeTab = tab;
    if (tab === 'metrics' && this.campaign?.id) {
      this.loadMetrics();
    }
    if (tab === 'info') {
      setTimeout(() => this.loadMap(), 100);
    }
  }

  private loadMetrics(): void {
    if (!this.campaign?.id) return;
    this.metricsLoading = true;

    this.metricsService.getBloodEstimatedPerCampaign().subscribe({
      next: (data) => {
        this.campaignBloodEstimated = data.find(d => d.campaignId === this.campaign!.id) || null;
      },
      error: () => {}
    });

    this.metricsService.getLivesSavedPerCampaign().subscribe({
      next: (data) => {
        this.campaignLivesSaved = data.find(d => d.campaignId === this.campaign!.id) || null;
      },
      error: () => {}
    });

    this.metricsService.getBloodTypeRanking(this.campaign.id).subscribe({
      next: (data) => {
        this.bloodTypeRanking = data;
        this.metricsLoading = false;
      },
      error: () => {
        this.metricsLoading = false;
      }
    });
  }

  suscribeEvent(campaingId: number, roleId: number): void {
    this.campaignService.subscribeDonor(campaingId, roleId).subscribe({
      next: () => {
        this.isSubscribed = true;
        this.toast.success('Te has suscrito a la campaña');
        this.loadSubscribedDonors(this.campaign!.id!);
        this.loadMetrics();
      },
      error: (err) => {
        this.toast.error('Error al suscribirse a la campaña');
        console.error('Error subscribing to campaign:', err);
      }
    });
  }

  subscribeToCampaign(): void {
    if (!this.campaign?.id) {
      this.toast.error('No se puede suscribir a esta campaña');
      return;
    }

    if (this.campaign?.bloodFactorRequired && this.campaign?.bloodGroupRequired) {
      this.authService.getCurrentUser().subscribe({
        next: (user) => {
          if (!user) {
            this.toast.error('No se pudo obtener la información del usuario');
            return;
          }

          this.donorService.getDonorById(user.roleId).subscribe({
            next: (donor) => {
              if (donor.bloodFactor !== this.campaign!.bloodFactorRequired || donor.bloodGroup !== this.campaign!.bloodGroupRequired) {
                this.toast.error('No cumples con los requisitos de sangre para suscribirte a esta campaña');
                return;
              }
              this.suscribeEvent(this.campaign!.id!, user.roleId);
            },
            error: (err) => {
              this.toast.error('Error al obtener información del donante');
              console.error('Error fetching donor info:', err);
            }
          })
        }
      })
    } else {
      this.authService.getCurrentUser().subscribe({
        next: (user) => {
          if (!user) {
            this.toast.error('No se pudo obtener la información del usuario');
            return;
          }
          this.suscribeEvent(this.campaign!.id!, user.roleId);
        },
        error: (err) => {
          this.toast.error('Error al obtener información del usuario');
          console.error('Error fetching current user:', err);
        }
      });
    }
  }

  unsubscribeFromCampaign(): void {
    if (!this.campaign?.id) {
      this.toast.error('No se puede desuscribirse de esta campaña');
      return;
    }

    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        if (!user) {
          this.toast.error('No se pudo obtener la información del usuario');
          return;
        }

        this.campaignService.unsubscribeDonor(this.campaign!.id!, user.roleId).subscribe({
          next: () => {
            this.isSubscribed = false;
            this.toast.success('Te has desuscrito de la campaña');
            this.loadSubscribedDonors(this.campaign!.id!);
            this.loadMetrics();
          },
          error: (err) => {
            this.toast.error('Error al desuscribirse de la campaña');
            console.error('Error unsubscribing from campaign:', err);
          }
        });
      },
      error: (err) => {
        this.toast.error('Error al obtener información del usuario');
        console.error('Error fetching current user:', err);
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/campaigns']);
  }
}
