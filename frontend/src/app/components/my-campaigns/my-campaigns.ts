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
import { animate, stagger } from 'animejs';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-my-campaigns',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, EnumLabelPipe, MatIcon],
  templateUrl: './my-campaigns.html',
  styleUrls: ['./my-campaigns.css']
})
export class MyCampaignsComponent implements OnInit {
  campaigns: ResponseCampaign[] = [];
  loading = true;
  organizerId: number = 0;

  showSubscribedList = false;
  subscribedDonors: SubscribedDonor[] = [];
  selectedCampaignId: number | null = null;

  finishCampaignId: number | null = null;
  finishDate: string = '';

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
