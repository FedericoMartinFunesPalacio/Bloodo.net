import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { OrganizerEmpService } from '../../services/organizer-emp.service';
import { OrganizerPerService } from '../../services/organizer-per.service';
import { RoleService } from '../../services/role.service';
import { ToastService } from '../../services/toast.service';
import { ResponseOrganizerEmp } from '../../models/organizer-emp';
import { ResponseOrganizerPer } from '../../models/organizer-per';
import { animate, stagger } from 'animejs';
import { MatIcon } from '@angular/material/icon';
import { LoadingComponent } from '../loading/loading';

interface OrganizerItem {
  id: number | undefined;
  type: 'EMP' | 'PER';
  name: string;
  document: string;
  direction: string;
  email: string;
  phoneNumber: string;
  isActive: boolean;
}

@Component({
  selector: 'app-organizer-list',
  standalone: true,
  imports: [CommonModule, RouterModule, MatIcon, LoadingComponent],
  templateUrl: './organizer-list.html',
  styleUrls: ['./organizer-list.css']
})
export class OrganizerListComponent implements OnInit {
  organizers: OrganizerItem[] = [];
  loading = true;
  isAdmin = false;

  constructor(
    private organizerEmpService: OrganizerEmpService,
    private organizerPerService: OrganizerPerService,
    private roleService: RoleService,
    private router: Router,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.checkRoles();
    this.loadOrganizers();
  }

  private checkRoles(): void {
    this.roleService.isAdmin().subscribe(isAdm => this.isAdmin = isAdm);
  }

  private loadOrganizers(): void {
    this.loading = true;

    forkJoin({
      emps: this.organizerEmpService.getAllOrganizerEmps(),
      pers: this.organizerPerService.getAllOrganizerPers()
    }).subscribe({
      next: ({ emps, pers }) => {
        const empItems: OrganizerItem[] = emps.map(org => this.mapEmp(org));
        const perItems: OrganizerItem[] = pers.map(org => this.mapPer(org));
        this.organizers = [...empItems, ...perItems];
        this.loading = false;
        this.animateRows();
      },
      error: (err) => {
        this.toast.error('Error al cargar los organizadores');
        this.loading = false;
        console.error('Error loading organizers:', err);
      }
    });
  }

  private animateRows(): void {
    setTimeout(() => {
      const header = document.querySelector('.organizer-list-header');
      const rows = document.querySelectorAll('.organizer-table tbody tr');
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

  private mapEmp(org: ResponseOrganizerEmp): OrganizerItem {
    return {
      id: org.id,
      type: 'EMP',
      name: org.fullName,
      document: org.document,
      direction: org.direction,
      email: org.email,
      phoneNumber: org.phoneNumber,
      isActive: org.isActive
    };
  }

  private mapPer(org: ResponseOrganizerPer): OrganizerItem {
    return {
      id: org.id,
      type: 'PER',
      name: `${org.firstName} ${org.lastName}`,
      document: org.document,
      direction: org.direction,
      email: org.email,
      phoneNumber: org.phoneNumber,
      isActive: org.isActive
    };
  }

  editOrganizer(item: OrganizerItem): void {
    if (item.id) {
      this.router.navigate(['/organizers', item.type.toLowerCase(), item.id, 'edit']);
    }
  }

  async deleteOrganizer(item: OrganizerItem): Promise<void> {
    if (!item.id) return;

    const confirmed = await this.toast.confirm('¿Estás seguro de que deseas eliminar este organizador?');
    if (!confirmed) return;

    const onSuccess = () => {
      this.organizers = this.organizers.filter(o => o.id !== item.id);
      this.toast.success('Organizador eliminado correctamente');
    };

    const onError = (err: unknown) => {
      this.toast.error('Error al eliminar el organizador');
      console.error('Error deleting organizer:', err);
    };

    if (item.type === 'EMP') {
      this.organizerEmpService.deleteOrganizerEmp(item.id).subscribe({ next: onSuccess, error: onError });
    } else {
      this.organizerPerService.deleteOrganizerPer(item.id).subscribe({ next: onSuccess, error: onError });
    }
  }
}
