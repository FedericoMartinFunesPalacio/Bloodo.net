import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CampaignService } from '../../services/campaign.service';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';
import { EnumLabelPipe } from '../../pipes/enum-label.pipe';
import { RequestCampaign, ResponseCampaign } from '../../models/campaign';
import { BloodFactor, BloodGroup } from '../../models/donor';
import { isFutureDate } from '../../utils/validators';

@Component({
  selector: 'app-campaign-form',
  standalone: true,
  imports: [CommonModule, FormsModule, EnumLabelPipe],
  templateUrl: './campaign-form.html',
  styleUrls: ['./campaign-form.css']
})
export class CampaignFormComponent implements OnInit {
  isEditMode = false;
  campaignId: number | null = null;
  loading = false;

  form: RequestCampaign = {
    title: '',
    description: '',
    startDate: '',
    endDate: '',
    startTime: '',
    direction: '',
    bloodFactorRequired: undefined,
    bloodGroupRequired: undefined,
    organizerId: 0
  };

  readonly bloodFactors = Object.values(BloodFactor);
  readonly bloodGroups = Object.values(BloodGroup);

  constructor(
    private campaignService: CampaignService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe(user => {
      if (user) {
        this.form.organizerId = user.roleId;
      }
    });

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.campaignId = +id;
      this.loadCampaignForEdit(+id);
    }
  }

  private loadCampaignForEdit(id: number): void {
    this.loading = true;
    this.campaignService.getCampaignById(id).subscribe({
      next: (campaign) => {
        this.form = {
          title: campaign.title,
          description: campaign.description,
          startDate: campaign.startDate,
          endDate: campaign.endDate || '',
          startTime: campaign.startTime,
          direction: campaign.direction,
          bloodFactorRequired: campaign.bloodFactorRequired,
          bloodGroupRequired: campaign.bloodGroupRequired,
          organizerId: campaign.organizerId
        };
        this.loading = false;
      },
      error: (err) => {
        this.toast.error('Error al cargar la campaña');
        this.loading = false;
        console.error('Error loading campaign:', err);
      }
    });
  }

  onSubmit(): void {
    if (!this.validateForm()) {
      return;
    }

    this.loading = true;

    if (this.isEditMode && this.campaignId) {
      this.campaignService.updateCampaign(this.campaignId, this.form).subscribe({
        next: () => {
          this.toast.success('Campaña actualizada exitosamente');
          this.loading = false;
          setTimeout(() => {
            this.router.navigate(['/campaigns', this.campaignId]);
          }, 1500);
        },
        error: (err) => {
          this.toast.error('Error al actualizar la campaña');
          this.loading = false;
          console.error('Error updating campaign:', err);
        }
      });
    } else {
      this.campaignService.createCampaign(this.form).subscribe({
        next: () => {
          this.toast.success('Campaña creada exitosamente');
          this.loading = false;
          setTimeout(() => {
            this.router.navigate(['/campaigns']);
          }, 1500);
        },
        error: (err) => {
          this.toast.error('Error al crear la campaña');
          this.loading = false;
          console.error('Error creating campaign:', err);
        }
      });
    }
  }

  private validateForm(): boolean {
    if (!this.form.title.trim()) {
      this.toast.warning('El título es requerido');
      return false;
    }
    if (!this.form.description.trim()) {
      this.toast.warning('La descripción es requerida');
      return false;
    }
    if (!this.form.startDate) {
      this.toast.warning('La fecha de inicio es requerida');
      return false;
    }
    if (!isFutureDate(this.form.startDate)) {
      this.toast.warning('La fecha de inicio debe ser hoy o en el futuro');
      return false;
    }
    if (!this.form.startTime) {
      this.toast.warning('El horario de inicio es requerido');
      return false;
    }
    if (!this.form.direction.trim()) {
      this.toast.warning('La ubicación es requerida');
      return false;
    }
    return true;
  }

  onCancel(): void {
    if (this.isEditMode) {
      this.router.navigate(['/campaigns', this.campaignId]);
    } else {
      this.router.navigate(['/campaigns']);
    }
  }
}
