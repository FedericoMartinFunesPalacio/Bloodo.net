import { Component, OnInit, OnDestroy, ElementRef, HostListener, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { RoleService } from '../../../services/role.service';
import { DonorService } from '../../../services/donor.service';
import { OrganizerPerService } from '../../../services/organizer-per.service';
import { OrganizerEmpService } from '../../../services/organizer-emp.service';
import { ResponseUser, UserRole } from '../../../models/user';
import { DonateModalComponent } from '../donate-modal/donate-modal';
import { NotificationBellComponent } from '../notification-bell/notification-bell';
import { MatIcon } from '@angular/material/icon';
import { GuidedTourService } from '@pantarey.io/ngx-guided-tour-lite';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, DonateModalComponent, NotificationBellComponent, MatIcon],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class NavbarComponent implements OnInit, OnDestroy {
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
  private autoAdvanceTimer: any = null;
  private progressRaf: any = null;
  private progressStart: number = 0;
  private tourObserver: MutationObserver | null = null;
  private tourActive = false;

  constructor(
    private authService: AuthService,
    private roleService: RoleService,
    private donorService: DonorService,
    private organizerPerService: OrganizerPerService,
    private organizerEmpService: OrganizerEmpService,
    private router: Router,
    private elementRef: ElementRef,
    private guidedTour: GuidedTourService
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

  ngOnDestroy(): void {
    this.stopAutoAdvance();
    this.stopTourWatcher();
    if (this.progressRaf) cancelAnimationFrame(this.progressRaf);
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
  }

  closeProfile(): void {
    this.profileOpen = false;
  }

  startTour(): void {
    const url = this.router.url;
    const isHome = url.startsWith('/home');
    const isCampaignList = url === '/campaigns' || url.startsWith('/campaigns?');
    const isCampaignDetail = /^\/campaigns\/\d+$/.test(url);
    const isMyCampaigns = url.startsWith('/my-campaigns');
    const isMyDonations = url.startsWith('/my-donations');

    const steps: any[] = [];
    const mobileSteps: any[] = [];

    if (isHome) {
      steps.push(
        {
          selector: '.brand-logo',
          side: 'bottom',
          text: { es: { title: 'Bloodo.net', description: 'El logo te lleva al inicio de la plataforma.' } }
        },
        {
          selector: '#tour-campanas',
          side: 'bottom',
          text: { es: { title: 'Campañas', description: 'Acá podés ver todas las campañas de donación disponibles.' } }
        }
      );

      if (this.isAdmin || this.isOrganizer) {
        steps.push({
          selector: '#tour-mis-campanas',
          side: 'bottom',
          text: { es: { title: 'Mis Campañas', description: 'Gestioná las campañas que creaste. Podés ver suscritos, editar y finalizar.' } }
        });
      }

      if (this.isDonor) {
        steps.push({
          selector: '#tour-mis-donaciones',
          side: 'bottom',
          text: { es: { title: 'Mis Donaciones', description: 'Visualizá tus estadísticas de donación y datos de salud.' } }
        });
      }

      steps.push(
        {
          selector: '#tour-donar',
          side: 'bottom',
          text: { es: { title: 'Donar', description: 'Contribuí al funcionamiento de la plataforma con una donación.' } }
        },
        {
          selector: '.btn-profile',
          side: 'bottom',
          text: { es: { title: 'Tu Perfil', description: 'Accedé a tu información personal, editá tu perfil o cerrá sesión.' } }
        },
        {
          selector: '.home-header',
          side: 'bottom',
          text: { es: { title: 'Tu Dashboard', description: 'Acá encontrás un resumen personalizado según tu rol en la plataforma.' } }
        },
        {
          selector: '#tour-hero-blood',
          side: 'bottom',
          text: { es: { title: 'Recolección de Sangre', description: 'Indicador global de litros de sangre recolectados a través de todas las campañas.' } }
        },
        {
          selector: '#tour-hero-lives',
          side: 'bottom',
          text: { es: { title: 'Vidas Salvadas', description: 'Estimación de vidas que pudimos ayudar a salvar gracias a las donaciones.' } }
        },
        {
          selector: '#tour-stats',
          side: 'top',
          text: { es: { title: 'Estadísticas', description: 'Resumen clave de tu actividad: campañas, donadores y más.' } }
        },
        {
          selector: '#tour-recent-campaigns',
          side: 'left',
          text: { es: { title: 'Campañas Recientes', description: 'Accedé rápidamente a las últimas campañas. Hacé click en una para ver sus detalles.' } }
        },
        {
          selector: '#tour-quick-actions',
          side: 'left',
          text: { es: { title: 'Acciones Rápidas', description: 'Atajos para crear campañas, ver donadores o explorar campañas disponibles.' } }
        }
      );

      mobileSteps.push(
        {
          selector: '.brand-logo',
          side: 'bottom',
          text: { es: { title: 'Bloodo.net', description: 'El logo te lleva al inicio de la plataforma.' } }
        },
        {
          selector: '.menu-toggle',
          side: 'bottom',
          text: { es: { title: 'Menú', description: 'Tocá este botón para abrir el menú de navegación con todas las opciones.' } }
        },
        {
          selector: '#tour-donar',
          side: 'bottom',
          text: { es: { title: 'Donar', description: 'Contribuí al funcionamiento de la plataforma con una donación.' } }
        },
        {
          selector: '.btn-profile',
          side: 'bottom',
          text: { es: { title: 'Tu Perfil', description: 'Accedé a tu información personal, editá tu perfil o cerrá sesión.' } }
        },
        {
          selector: '.home-header',
          side: 'bottom',
          text: { es: { title: 'Tu Dashboard', description: 'Resumen personalizado según tu rol.' } }
        },
        {
          selector: '#tour-hero-blood',
          side: 'bottom',
          text: { es: { title: 'Recolección de Sangre', description: 'Litros totales recolectados en todas las campañas.' } }
        },
        {
          selector: '#tour-stats',
          side: 'bottom',
          text: { es: { title: 'Estadísticas', description: 'Resumen clave de tu actividad.' } }
        },
        {
          selector: '#tour-recent-campaigns',
          side: 'bottom',
          text: { es: { title: 'Campañas Recientes', description: 'Accedé a las últimas campañas con un click.' } }
        },
        {
          selector: '#tour-quick-actions',
          side: 'bottom',
          text: { es: { title: 'Acciones Rápidas', description: 'Atajos para navegar la plataforma.' } }
        }
      );
    }

    if (isCampaignList) {
      steps.push(
        {
          selector: '#tour-campaign-list-header',
          side: 'bottom',
          text: { es: { title: 'Campañas de Donación', description: 'Acá se listan todas las campañas disponibles. Podés explorar las que te interesen.' } }
        },
        {
          selector: '#tour-campaign-list-search',
          side: 'bottom',
          text: { es: { title: 'Buscador', description: 'Buscá campañas por título. El buscador es sensible a acentos: escribí "Córdoba" para encontrar lo que necesitás, no "Cordoba".' } }
        },
        {
          selector: '#tour-campaign-list-cards',
          side: 'top',
          text: { es: { title: 'Explorá Campañas', description: 'Hacé click en cualquier campaña para ver sus detalles completos, fechas y dirección.' } }
        }
      );

      mobileSteps.push(
        {
          selector: '#tour-campaign-list-header',
          side: 'bottom',
          text: { es: { title: 'Campañas de Donación', description: 'Listado de campañas disponibles para donar.' } }
        },
        {
          selector: '#tour-campaign-list-search',
          side: 'bottom',
          text: { es: { title: 'Buscador', description: 'Buscá por título. Sensible a acentos: escribí "Córdoba", no "Cordoba".' } }
        },
        {
          selector: '#tour-campaign-list-cards',
          side: 'bottom',
          text: { es: { title: 'Explorá Campañas', description: 'Tocá una campaña para ver sus detalles.' } }
        }
      );
    }

    if (isCampaignDetail) {
      steps.push(
        {
          selector: '.campaign-detail-header',
          side: 'bottom',
          text: { es: { title: 'Detalle de Campaña', description: 'Acá encontrás toda la info de la campaña: fechas, ubicación y requisitos de sangre.' } }
        },
        {
          selector: '.tabs',
          side: 'bottom',
          text: { es: { title: 'Pestañas', description: 'Cambiar entre Información General y Métricas. Las métricas son estimativas según los donadores suscritos.' } }
        }
      );

      if (this.isDonor) {
        steps.push({
          selector: '#tour-campaign-detail-subscribe',
          side: 'top',
          text: { es: { title: 'Suscripción', description: 'Suscribite para donar en esta campaña o desuscribite si ya no podés asistir.' } }
        });
      }

      mobileSteps.push(
        {
          selector: '.campaign-detail-header',
          side: 'bottom',
          text: { es: { title: 'Detalle', description: 'Info completa de la campaña.' } }
        },
        {
          selector: '.tabs',
          side: 'bottom',
          text: { es: { title: 'Pestañas', description: 'Info General y Métricas estimativas de la campaña.' } }
        }
      );

      if (this.isDonor) {
        mobileSteps.push({
          selector: '#tour-campaign-detail-subscribe',
          side: 'bottom',
          text: { es: { title: 'Suscripción', description: 'Tocá para suscribirte o desuscribirte.' } }
        });
      }
    }

    if (isMyCampaigns) {
      steps.push(
        {
          selector: '#tour-my-campaigns-header',
          side: 'bottom',
          text: { es: { title: 'Mis Campañas', description: 'Acá gestionás las campañas que creaste. Creá nuevas con el botón de arriba.' } }
        },
        {
          selector: '#tour-my-campaigns-cards',
          side: 'top',
          text: { es: { title: 'Tus Campañas', description: 'Cada card tiene acciones: ver detalle, ver suscritos, editar, finalizar o eliminar la campaña.' } }
        }
      );

      mobileSteps.push(
        {
          selector: '#tour-my-campaigns-header',
          side: 'bottom',
          text: { es: { title: 'Mis Campañas', description: 'Gestioná tus campañas creadas.' } }
        },
        {
          selector: '#tour-my-campaigns-cards',
          side: 'bottom',
          text: { es: { title: 'Tus Campañas', description: 'Cada card tiene opciones: detalle, suscritos, editar, finalizar y eliminar.' } }
        }
      );
    }

    if (isMyDonations) {
      steps.push(
        {
          selector: '#tour-my-donations-header',
          side: 'bottom',
          text: { es: { title: 'Mis Donaciones', description: 'Acá seguís el historial de tus donaciones y tu información de salud personal.' } }
        },
        {
          selector: '#tour-my-donations-subscriptions',
          side: 'bottom',
          text: { es: { title: 'Mis Suscripciones', description: 'Las campañas a las que te suscribiste. Acá podés ver fechas, ubicación y desuscribirte si ya no podés asistir.' } }
        },
        {
          selector: '#tour-my-donations-stats',
          side: 'top',
          text: { es: { title: 'Estadísticas Estimativas', description: 'Campañas asistidas, sangre donada y vidas salvadas. Son valores estimados según tus suscripciones.' } }
        },
        {
          selector: '#tour-my-donations-health',
          side: 'top',
          text: { es: { title: 'Información de Salud', description: 'Tipo de sangre, IMC, última donación y próxima fecha elegible para donar.' } }
        }
      );

      mobileSteps.push(
        {
          selector: '#tour-my-donations-header',
          side: 'bottom',
          text: { es: { title: 'Mis Donaciones', description: 'Historial de donaciones e info de salud.' } }
        },
        {
          selector: '#tour-my-donations-subscriptions',
          side: 'bottom',
          text: { es: { title: 'Mis Suscripciones', description: 'Campañas suscriptas con fechas, ubicación y opción de desuscribirse.' } }
        },
        {
          selector: '#tour-my-donations-stats',
          side: 'bottom',
          text: { es: { title: 'Estadísticas', description: 'Valores estimados: campañas, sangre donada y vidas salvadas.' } }
        },
        {
          selector: '#tour-my-donations-health',
          side: 'bottom',
          text: { es: { title: 'Salud', description: 'Tipo de sangre, IMC y fechas de donación.' } }
        }
      );
    }

    if (steps.length === 0) return;

    this.guidedTour.startTour({
      options: {
        overlayOpacity: 0.6,
        showProgress: false,
        allowClose: true,
        locale: 'es',
        labels: { next: 'Siguiente', prev: 'Anterior', done: 'Finalizar', close: 'Cerrar' }
      },
      steps,
      mobileSteps,
      onComplete: () => { this.stopTourWatcher(); this.stopAutoAdvance(); }
    });

    setTimeout(() => {
      this.startAutoAdvance();
      this.startTourWatcher();
    }, 100);
  }

  private startTourWatcher(): void {
    this.tourActive = true;
    if (this.tourObserver) this.tourObserver.disconnect();
    this.tourObserver = new MutationObserver(() => this.ensureProgressBar());
    this.tourObserver.observe(document.body, { childList: true, subtree: true, attributes: true });
    this.ensureProgressBar();
  }

  private stopTourWatcher(): void {
    this.tourActive = false;
    if (this.tourObserver) { this.tourObserver.disconnect(); this.tourObserver = null; }
  }

  private ensureProgressBar(): void {
    if (!this.tourActive) return;
    const popover = document.querySelector('.guided-tour-popover');
    if (!popover) return;

    if (!popover.querySelector('.guided-tour-progress-bar')) {
      const bar = document.createElement('div');
      bar.className = 'guided-tour-progress-bar';
      popover.appendChild(bar);
      this.progressStart = Date.now();
      this.animateProgress(bar);
    }

    if (!popover.hasAttribute('data-tour-listener')) {
      popover.setAttribute('data-tour-listener', 'true');
      popover.addEventListener('click', (e: Event) => {
        const target = e.target as HTMLElement;
        if (target.closest('.guided-tour-progress-bar')) return;
        const nextBtn = popover.querySelector('.guided-tour-popover__btn--primary') as HTMLElement;
        if (nextBtn) nextBtn.click();
        this.restartAutoAdvance();
      });
    }
  }

  private animateProgress(bar: HTMLElement): void {
    if (this.progressRaf) cancelAnimationFrame(this.progressRaf);
    const duration = 5000;
    const tick = () => {
      const elapsed = Date.now() - this.progressStart;
      const pct = Math.min(elapsed / duration * 100, 100);
      bar.style.width = pct + '%';
      if (pct < 100) {
        this.progressRaf = requestAnimationFrame(tick);
      }
    };
    this.progressRaf = requestAnimationFrame(tick);
  }

  private startAutoAdvance(): void {
    this.stopAutoAdvance();
    this.autoAdvanceTimer = setTimeout(() => {
      const nextBtn = document.querySelector('.guided-tour-popover__btn--primary') as HTMLElement;
      if (nextBtn) nextBtn.click();
    }, 5000);
  }

  private stopAutoAdvance(): void {
    if (this.autoAdvanceTimer) {
      clearTimeout(this.autoAdvanceTimer);
      this.autoAdvanceTimer = null;
    }
  }

  private restartAutoAdvance(): void {
    this.stopAutoAdvance();
    this.startAutoAdvance();
    setTimeout(() => this.ensureProgressBar(), 50);
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
