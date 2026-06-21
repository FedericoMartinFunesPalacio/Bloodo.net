import { Component, OnInit, ElementRef, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { RoleService } from '../../services/role.service';
import { DonorService } from '../../services/donor.service';
import { OrganizerPerService } from '../../services/organizer-per.service';
import { OrganizerEmpService } from '../../services/organizer-emp.service';
import { ResponseUser, UserRole } from '../../models/user';
import { DonateModalComponent } from '../donate-modal/donate-modal';
import { animate, stagger } from 'animejs';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, DonateModalComponent, MatIcon],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class NavbarComponent implements OnInit {
  currentUser: ResponseUser | null = null;
  isOrganizer = false;
  isAdmin = false;
  isDonor = false;
  menuOpen = false;
  profileOpen = false;
  editProfileUrl: string | null = null;

  profileFirstName: string = '';
  profileLastName: string = '';

  readonly UserRole = UserRole;

  constructor(
    private authService: AuthService,
    private roleService: RoleService,
    private donorService: DonorService,
    private organizerPerService: OrganizerPerService,
    private organizerEmpService: OrganizerEmpService,
    private router: Router,
    private elementRef: ElementRef
  ) {}

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe(user => {
      this.currentUser = user;
      this.buildEditProfileUrl(user);
      this.loadProfileData(user);
    });
    this.roleService.isOrganizer().subscribe(isOrg => this.isOrganizer = isOrg);
    this.roleService.isAdmin().subscribe(isAdm => this.isAdmin = isAdm);
    this.roleService.isDonor().subscribe(isDon => this.isDonor = isDon);
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event): void {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.profileOpen = false;
    }
  }

  private loadProfileData(user: ResponseUser | null): void {
    this.profileFirstName = '';
    this.profileLastName = '';

    if (!user || !user.roleId) return;

    const roleId = Number(user.roleId);
    if (roleId === 0) return;

    if (user.role === UserRole.DONOR) {
      this.donorService.getDonorById(roleId).subscribe({
        next: (donor) => {
          this.profileFirstName = donor.firstName;
          this.profileLastName = donor.lastName;
        },
        error: () => {}
      });
    } else if (user.role === UserRole.ORGANIZER) {
      this.organizerPerService.getOrganizerPerById(roleId).subscribe({
        next: (org) => {
          this.profileFirstName = org.firstName;
          this.profileLastName = org.lastName;
        },
        error: () => {
          this.organizerEmpService.getOrganizerEmpById(roleId).subscribe({
            next: (org) => {
              this.profileFirstName = org.fullName;
              this.profileLastName = '';
            },
            error: () => {}
          });
        }
      });
    }
  }

  private buildEditProfileUrl(user: ResponseUser | null): void {
    if (!user) {
      this.editProfileUrl = null;
      return;
    }

    const roleId = user.roleId ? Number(user.roleId) : 0;
    if (roleId === 0) {
      this.editProfileUrl = null;
      return;
    }

    if (user.role === UserRole.DONOR) {
      this.editProfileUrl = `/donors/${roleId}/edit`;
      return;
    }

    if (user.role === UserRole.ORGANIZER) {
      this.organizerPerService.getOrganizerPerById(roleId).subscribe({
        next: () => { this.editProfileUrl = `/organizers/per/${roleId}/edit`; },
        error: () => { this.editProfileUrl = `/organizers/emp/${roleId}/edit`; }
      });
      return;
    }

    this.editProfileUrl = null;
  }

  toggleProfile(): void {
    this.profileOpen = !this.profileOpen;
    if (this.profileOpen) {
      setTimeout(() => {
        const dropdown = document.querySelector('.profile-dropdown');
        if (dropdown) {
          animate(dropdown, { opacity: [0, 1], translateY: [-8, 0], duration: 250, ease: 'outQuad' });
          const items = dropdown.querySelectorAll('.profile-header, .profile-detail, .profile-action');
          if (items.length) {
            animate(items, { opacity: [0, 1], translateY: [-5, 0], duration: 200, delay: stagger(30, { start: 80 }), ease: 'outQuad' });
          }
        }
      }, 10);
    }
  }

  closeProfile(): void {
    this.profileOpen = false;
  }

  toggleMenu(): void {
    this.menuOpen = !this.menuOpen;
  }

  closeMenu(): void {
    this.menuOpen = false;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
    this.menuOpen = false;
    this.profileOpen = false;
  }
}
