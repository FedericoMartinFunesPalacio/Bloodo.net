import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { DonorService } from '../../services/donor.service';
import { ToastService } from '../../services/toast.service';
import { EnumLabelPipe } from '../../pipes/enum-label.pipe';
import { RequestDonor } from '../../models/donor';
import { BloodFactor, BloodGroup, Gender } from '../../models/donor';
import {RequestUser} from '../../models/user';
import {AuthService} from '../../services/auth.service';
import { isValidEmail, isValidPhone, isValidDocument, isValidBirthdate } from '../../utils/validators';
import { LoadingComponent } from '../loading/loading';

@Component({
  selector: 'app-donor-form',
  standalone: true,
  imports: [CommonModule, FormsModule, EnumLabelPipe, LoadingComponent],
  templateUrl: './donor-form.html',
  styleUrls: ['./donor-form.css']
})
export class DonorFormComponent implements OnInit {
  isEditMode = false;
  donorId: number | null = null;
  loading = false;
  userDTO: RequestUser;

  form: RequestDonor = {
    firstName: '',
    lastName: '',
    birthdate: '',
    document: '',
    bloodFactor: BloodFactor.POSITIVE,
    bloodGroup: BloodGroup.O,
    gender: Gender.MALE,
    height: 0,
    weight: 0,
    email: '',
    phoneNumber: ''
  };

  readonly bloodFactors = Object.values(BloodFactor);
  readonly bloodGroups = Object.values(BloodGroup);
  readonly genders = Object.values(Gender);

  constructor(
    private donorService: DonorService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private toast: ToastService
  ) {
    const navigation = this.router.getCurrentNavigation();
    this.userDTO = navigation?.extras?.state?.['user'];
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.donorId = +id;
      this.loadDonorForEdit(+id);
    } else if (this.userDTO) {
      this.form.email = this.userDTO.email || '';
      this.form.phoneNumber = this.userDTO.phone || '';
    }
  }

  private loadDonorForEdit(id: number): void {
    this.loading = true;
    this.donorService.getDonorById(id).subscribe({
      next: (donor) => {
        this.form = {
          firstName: donor.firstName,
          lastName: donor.lastName,
          birthdate: donor.birthdate,
          document: donor.document,
          bloodFactor: donor.bloodFactor,
          bloodGroup: donor.bloodGroup,
          gender: donor.gender,
          height: donor.height,
          weight: donor.weight,
          email: donor.email,
          phoneNumber: donor.phoneNumber
        };
        this.loading = false;
      },
      error: (err) => {
        this.toast.error('Error al cargar los datos del donador');
        this.loading = false;
        console.error('Error loading donor:', err);
      }
    });
  }

  onSubmit(): void {
    if (!this.validateForm()) {
      return;
    }

    this.loading = true;

    if (this.isEditMode && this.donorId) {
      this.donorService.updateDonor(this.donorId, this.form).subscribe({
        next: () => {
          this.toast.success('Donador actualizado exitosamente');
          this.loading = false;
          this.authService.getCurrentUser().subscribe({
            next : (user) => {
              if (user?.role === 'ADMIN') {
                setTimeout(() => {
                  this.router.navigate(['/donors']);
                }, 1500);
              } else {
                setTimeout(() => {
                  this.router.navigate(['/campaigns']);
                }, 1500);
              }
            }});
        },
        error: (err) => {
          this.toast.error('Error al actualizar el donador');
          this.loading = false;
          console.error('Error updating donor:', err);
        }
      });
    } else {
      this.donorService.createDonor(this.form).subscribe({
        next: (donorResponse) => {
          if (donorResponse.id != null) {
            this.userDTO.roleId = donorResponse.id;
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
          this.toast.error('Error al registrar el donador');
          this.loading = false;
          console.error('Error creating donor:', err);
        }
      });
    }
  }

  private validateForm(): boolean {
    if (!this.form.firstName.trim()) {
      this.toast.warning('El nombre es requerido');
      return false;
    }
    if (!this.form.lastName.trim()) {
      this.toast.warning('El apellido es requerido');
      return false;
    }
    if (!this.form.birthdate) {
      this.toast.warning('La fecha de nacimiento es requerida');
      return false;
    }
    if (!isValidBirthdate(this.form.birthdate)) {
      this.toast.warning('La fecha de nacimiento debe ser desde 1930');
      return false;
    }
    if (!this.form.document.trim()) {
      this.toast.warning('El documento es requerido');
      return false;
    }
    if (!isValidDocument(this.form.document)) {
      this.toast.warning('El documento no tiene un formato válido (sin espacios, guion para CUIT, 7-13 caracteres)');
      return false;
    }
    if (!this.form.email.trim()) {
      this.toast.warning('El email es requerido');
      return false;
    }
    if (!isValidEmail(this.form.email)) {
      this.toast.warning('El email no tiene un formato válido');
      return false;
    }
    if (!this.form.phoneNumber.trim()) {
      this.toast.warning('El teléfono es requerido');
      return false;
    }
    if (!isValidPhone(this.form.phoneNumber)) {
      this.toast.warning('El teléfono debe tener entre 8 y 13 dígitos, sin espacios ni signos');
      return false;
    }
    if (this.form.height <= 0) {
      this.toast.warning('La altura debe ser mayor a 0');
      return false;
    }
    if (this.form.weight <= 0) {
      this.toast.warning('El peso debe ser mayor a 0');
      return false;
    }
    return true;
  }

  onCancel(): void {
    if (this.isEditMode) {
      this.authService.getCurrentUser().subscribe({
        next : (user) => {
          if (user?.role === 'ADMIN') {
            this.router.navigate(['/donors']);
          } else {
            this.router.navigate(['/campaigns']);
          }
      }});
    } else {
      this.router.navigate(['/auth']);
    }
  }
}
