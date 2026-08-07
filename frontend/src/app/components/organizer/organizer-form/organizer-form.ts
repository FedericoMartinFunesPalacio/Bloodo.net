import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { OrganizerEmpService } from '../../../services/organizer-emp.service';
import { OrganizerPerService } from '../../../services/organizer-per.service';
import { ToastService } from '../../../services/toast.service';
import { EnumLabelPipe } from '../../../pipes/enum-label.pipe';
import { RequestOrganizerEmp } from '../../../models/organizer-emp';
import { RequestOrganizerPer } from '../../../models/organizer-per';
import { Gender } from '../../../models/donor';
import {RequestUser} from '../../../models/user';
import {AuthService} from '../../../services/auth.service';
import { isValidEmail, isValidPhone, isValidDocument, isValidBirthdate } from '../../../utils/validators';
import { toISODate } from '../../../utils/date-utils';
import { MatIcon } from '@angular/material/icon';
import { LoadingComponent } from '../../reusable/loading/loading';

type OrganizerType = 'emp' | 'per' | null;

@Component({
  selector: 'app-organizer-form',
  standalone: true,
  imports: [CommonModule, FormsModule, EnumLabelPipe, MatIcon, LoadingComponent],
  templateUrl: './organizer-form.html',
  styleUrls: ['./organizer-form.css']
})
export class OrganizerFormComponent implements OnInit {
  isEditMode = false;
  organizerId: number | null = null;
  organizerType: OrganizerType = null;
  loading = false;
  userDTO: RequestUser;

  formEmp: RequestOrganizerEmp = {
    fullName: '',
    document: '',
    direction: '',
    email: '',
    phoneNumber: ''
  };

  formPer: RequestOrganizerPer = {
    firstName: '',
    lastName: '',
    birthdate: '',
    document: '',
    direction: '',
    gender: Gender.MALE,
    email: '',
    phoneNumber: ''
  };

  readonly genders = Object.values(Gender);

  constructor(
    private organizerEmpService: OrganizerEmpService,
    private organizerPerService: OrganizerPerService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private toast: ToastService
  ) {
    const navigation = this.router.getCurrentNavigation();
    this.userDTO = navigation?.extras?.state?.['user'];

    if (this.userDTO) {
      this.formEmp.email = this.userDTO.email || '';
      this.formEmp.phoneNumber = this.userDTO.phone || '';
      this.formPer.email = this.userDTO.email || '';
      this.formPer.phoneNumber = this.userDTO.phone || '';
    }
  }

  ngOnInit(): void {
    const type = this.route.snapshot.paramMap.get('type') as OrganizerType;
    const id = this.route.snapshot.paramMap.get('id');

    if (type && (type === 'emp' || type === 'per')) {
      this.organizerType = type;
    }

    if (id && this.organizerType) {
      this.isEditMode = true;
      this.organizerId = +id;
      this.loadOrganizerForEdit(+id, this.organizerType);
    }
  }

  selectType(type: OrganizerType): void {
    this.organizerType = type;
  }

  private loadOrganizerForEdit(id: number, type: OrganizerType): void {
    this.loading = true;

    if (type === 'emp') {
      this.organizerEmpService.getOrganizerEmpById(id).subscribe({
        next: (org) => {
          this.formEmp = {
            fullName: org.fullName,
            document: org.document,
            direction: org.direction,
            email: org.email,
            phoneNumber: org.phoneNumber
          };
          this.loading = false;
        },
        error: (err) => {
          this.toast.error('Error al cargar los datos del organizador');
          this.loading = false;
          console.error('Error loading organizer emp:', err);
        }
      });
    } else {
      this.organizerPerService.getOrganizerPerById(id).subscribe({
        next: (org) => {
          this.formPer = {
            firstName: org.firstName,
            lastName: org.lastName,
            birthdate: toISODate(org.birthdate),
            document: org.document,
            direction: org.direction,
            gender: org.gender,
            email: org.email,
            phoneNumber: org.phoneNumber
          };
          this.loading = false;
        },
        error: (err) => {
          this.toast.error('Error al cargar los datos del organizador');
          this.loading = false;
          console.error('Error loading organizer per:', err);
        }
      });
    }
  }

  onSubmit(): void {
    if (this.organizerType === 'emp') {
      this.onSubmitEmp();
    } else {
      this.onSubmitPer();
    }
  }

  private onSubmitEmp(): void {
    if (!this.validateEmpForm()) {
      return;
    }

    this.loading = true;

    if (this.isEditMode && this.organizerId) {
      this.organizerEmpService.updateOrganizerEmp(this.organizerId, this.formEmp).subscribe({
        next: () => {
          this.toast.success('Organización actualizada exitosamente');
          this.loading = false;
          this.authService.getCurrentUser().subscribe({
            next : (user) => {
              if (user?.role === 'ADMIN') {
                setTimeout(() => {
                  this.router.navigate(['/organizers']);
                }, 1500);
              } else {
                setTimeout(() => {
                  this.router.navigate(['/home']);
                }, 1500);
              }
            }});
        },
        error: (err) => {
          this.toast.error('Error al actualizar la organización');
          this.loading = false;
          console.error('Error updating organizer emp:', err);
        }
      });
    } else {
      this.organizerEmpService.createOrganizerEmp(this.formEmp).subscribe({
        next: (orgEmpResponse) => {
          if (orgEmpResponse.id != null) {
            this.userDTO.roleId = orgEmpResponse.id;
            this.authService.register(this.userDTO).subscribe({
              next: () => {
                this.loading = false;
                this.toast.success('Registro exitoso. Ahora puedes iniciar sesión.');
                setTimeout(() => {
                  this.router.navigate(['/auth']);
                }, 1500);
              },
              error: (err) => {
                this.loading = false;
                this.toast.error('Error al registrar el usuario');
                console.error('Error al registrar el usuario:', err);
              }
            });
          }
        },
        error: (err) => {
          this.toast.error('Error al registrar la organización');
          this.loading = false;
          console.error('Error creating organizer emp:', err);
        }
      });
    }
  }

  private onSubmitPer(): void {
    if (!this.validatePerForm()) {
      return;
    }

    this.loading = true;

    if (this.isEditMode && this.organizerId) {
      this.organizerPerService.updateOrganizerPer(this.organizerId, this.formPer).subscribe({
        next: () => {
          this.toast.success('Organizador actualizado exitosamente');
          this.loading = false;
          setTimeout(() => {
            this.router.navigate(['/home']);
          }, 1500);
        },
        error: (err) => {
          this.toast.error('Error al actualizar el organizador');
          this.loading = false;
          console.error('Error updating organizer per:', err);
        }
      });
    } else {
      this.organizerPerService.createOrganizerPer(this.formPer).subscribe({
        next: (orgPerResponse) => {
          if (orgPerResponse.id != null) {
            this.userDTO.roleId = orgPerResponse.id;
            this.authService.register(this.userDTO).subscribe({
              next: () => {
                this.loading = false;
                this.toast.success('Registro exitoso. Ahora puedes iniciar sesión.');
                setTimeout(() => {
                  this.router.navigate(['/auth']);
                }, 1500);
              },
              error: (err) => {
                this.loading = false;
                this.toast.error('Error al registrar el usuario');
                console.error('Error al registrar el usuario:', err);
              }
            });
          }
        },
        error: (err) => {
          this.toast.error('Error al registrar el organizador');
          this.loading = false;
          console.error('Error creating organizer per:', err);
        }
      });
    }
  }

  private validateEmpForm(): boolean {
    if (!this.formEmp.fullName.trim()) {
      this.toast.warning('El nombre de la organización es requerido');
      return false;
    }
    if (!this.formEmp.document.trim()) {
      this.toast.warning('El documento es requerido');
      return false;
    }
    if (!isValidDocument(this.formEmp.document)) {
      this.toast.warning('El documento no tiene un formato válido (sin espacios, guion para CUIT, 7-13 caracteres)');
      return false;
    }
    if (!this.formEmp.direction.trim()) {
      this.toast.warning('La dirección es requerida');
      return false;
    }
    if (!this.formEmp.email.trim()) {
      this.toast.warning('El email es requerido');
      return false;
    }
    if (!isValidEmail(this.formEmp.email)) {
      this.toast.warning('El email no tiene un formato válido');
      return false;
    }
    if (!this.formEmp.phoneNumber.trim()) {
      this.toast.warning('El teléfono es requerido');
      return false;
    }
    if (!isValidPhone(this.formEmp.phoneNumber)) {
      this.toast.warning('El teléfono debe tener entre 8 y 13 dígitos, sin espacios ni signos');
      return false;
    }
    return true;
  }

  private validatePerForm(): boolean {
    if (!this.formPer.firstName.trim()) {
      this.toast.warning('El nombre es requerido');
      return false;
    }
    if (!this.formPer.lastName.trim()) {
      this.toast.warning('El apellido es requerido');
      return false;
    }
    if (!this.formPer.birthdate) {
      this.toast.warning('La fecha de nacimiento es requerida');
      return false;
    }
    if (!isValidBirthdate(this.formPer.birthdate)) {
      this.toast.warning('Debés tener al menos 18 años para registrarte');
      return false;
    }
    if (!this.formPer.document.trim()) {
      this.toast.warning('El documento es requerido');
      return false;
    }
    if (!isValidDocument(this.formPer.document)) {
      this.toast.warning('El documento no tiene un formato válido (sin espacios, guion para CUIT, 7-13 caracteres)');
      return false;
    }
    if (!this.formPer.direction.trim()) {
      this.toast.warning('La dirección es requerida');
      return false;
    }
    if (!this.formPer.email.trim()) {
      this.toast.warning('El email es requerido');
      return false;
    }
    if (!isValidEmail(this.formPer.email)) {
      this.toast.warning('El email no tiene un formato válido');
      return false;
    }
    if (!this.formPer.phoneNumber.trim()) {
      this.toast.warning('El teléfono es requerido');
      return false;
    }
    if (!isValidPhone(this.formPer.phoneNumber)) {
      this.toast.warning('El teléfono debe tener entre 8 y 13 dígitos, sin espacios ni signos');
      return false;
    }
    return true;
  }

  goBack(): void {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/home']);
    } else {
      this.router.navigate(['/auth']);
    }
  }
}
