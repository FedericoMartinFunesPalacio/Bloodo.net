import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { DonorService } from '../../services/donor.service';
import { RoleService } from '../../services/role.service';
import { ToastService } from '../../services/toast.service';
import { EnumLabelPipe } from '../../pipes/enum-label.pipe';
import { ResponseDonor } from '../../models/donor';
import { animate, stagger } from 'animejs';
import { MatIcon } from '@angular/material/icon';
import { LoadingComponent } from '../loading/loading';

@Component({
  selector: 'app-donor-list',
  standalone: true,
  imports: [CommonModule, RouterModule, EnumLabelPipe, MatIcon, LoadingComponent],
  templateUrl: './donor-list.html',
  styleUrls: ['./donor-list.css']
})
export class DonorListComponent implements OnInit {
  donors: ResponseDonor[] = [];
  loading = true;
  isAdmin = false;

  constructor(
    private donorService: DonorService,
    private roleService: RoleService,
    private router: Router,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.checkRoles();
    this.loadDonors();
  }

  private checkRoles(): void {
    this.roleService.isAdmin().subscribe(isAdm => this.isAdmin = isAdm);
  }

  private loadDonors(): void {
    this.loading = true;
    this.donorService.getAllDonors().subscribe({
      next: (donors) => {
        this.donors = donors;
        this.loading = false;
        this.animateRows();
      },
      error: (err) => {
        this.toast.error('Error al cargar los donadores');
        this.loading = false;
        console.error('Error loading donors:', err);
      }
    });
  }

  private animateRows(): void {
    setTimeout(() => {
      const header = document.querySelector('.donor-list-header');
      const rows = document.querySelectorAll('.donor-table tbody tr');
      if (header) {
        animate(header as any, { opacity: [0, 1], translateY: [20, 0], duration: 500, ease: 'outQuad' });
      }
      if (rows.length) {
        animate(rows, {
          opacity: [0, 1],
          translateX: [-20, 0],
          duration: 400,
          delay: stagger(40, { start: 150 }),
          ease: 'outQuad'
        });
      }
    }, 50);
  }

  editDonor(id: number | undefined): void {
    if (id) {
      this.router.navigate(['/donors', id, 'edit']);
    }
  }

  async deleteDonor(id: number | undefined): Promise<void> {
    if (!id) return;

    const confirmed = await this.toast.confirm('¿Estás seguro de que deseas eliminar este donador?');
    if (!confirmed) return;

    this.donorService.deleteDonor(id).subscribe({
      next: () => {
        this.donors = this.donors.filter(d => d.id !== id);
        this.toast.success('Donador eliminado correctamente');
      },
      error: (err) => {
        this.toast.error('Error al eliminar el donador');
        console.error('Error deleting donor:', err);
      }
    });
  }
}
