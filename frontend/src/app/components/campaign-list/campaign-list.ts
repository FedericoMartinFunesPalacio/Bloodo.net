import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { CampaignService } from '../../services/campaign.service';
import { ToastService } from '../../services/toast.service';
import { EnumLabelPipe } from '../../pipes/enum-label.pipe';
import { ResponseCampaign } from '../../models/campaign';
import { animate, stagger } from 'animejs';
import {MatIcon} from '@angular/material/icon';
import { LoadingComponent } from '../loading/loading';

@Component({
  selector: 'app-campaign-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, EnumLabelPipe, MatIcon, LoadingComponent],
  templateUrl: './campaign-list.html',
  styleUrls: ['./campaign-list.css']
})
export class CampaignListComponent implements OnInit {
  campaigns: ResponseCampaign[] = [];
  loading = true;
  searchTerm = '';

  get filteredCampaigns(): ResponseCampaign[] {
    if (!this.searchTerm.trim()) return this.campaigns;
    const term = this.searchTerm.toLowerCase().trim();
    const matching = this.campaigns.filter(c => c.title.toLowerCase().includes(term));
    const nonMatching = this.campaigns.filter(c => !c.title.toLowerCase().includes(term));
    return [...matching, ...nonMatching];
  }

  isSearchResult(index: number): boolean {
    if (!this.searchTerm.trim()) return false;
    return index === 0;
  }

  constructor(
    private campaignService: CampaignService,
    private router: Router,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadCampaigns();
  }

  private loadCampaigns(): void {
    this.loading = true;
    this.campaignService.getAllCampaigns().subscribe({
      next: (campaigns) => {
        this.campaigns = campaigns;
        this.loading = false;
        this.animateCards();
      },
      error: (err) => {
        this.toast.error('Error al cargar las campañas');
        this.loading = false;
        console.error('Error loading campaigns:', err);
      }
    });
  }

  private animateCards(): void {
    setTimeout(() => {
      const header = document.querySelector('.campaign-list-header');
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
}
