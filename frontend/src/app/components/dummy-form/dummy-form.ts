import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { Dummy } from '../../models/dummy';
import { parseDateFromDDMMYYYY } from '../../utils/date-utils';

@Component({
  selector: 'app-dummy-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatDatepickerModule,
    MatNativeDateModule,
  ],
  templateUrl: './dummy-form.html',
  styleUrls: ['./dummy-form.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DummyForm implements OnChanges {
  /** Dummy recibido desde el componente padre para editar */
  @Input() dummy?: Dummy;

  /** Emite el Dummy creado o editado */
  @Output() save = new EventEmitter<Dummy>();

  /** Emite cuando se cancela la edición/creación */
  @Output() cancel = new EventEmitter<void>();

  form: FormGroup;

  constructor(private fb: FormBuilder) {
    this.form = this.createForm();
  }

  ngOnChanges(_: SimpleChanges): void {
    // Si llega un dummy desde el padre, actualizamos el formulario
    if (this.dummy) {
      const parsed = parseDateFromDDMMYYYY((this.dummy as any).createdAt) ?? new Date();
      this.form.patchValue({
        name: this.dummy.name,
        description: this.dummy.description,
        serialNumber: this.dummy.serialNumber,
        createdAt: parsed,
      });
    } else {
      this.form.reset({ createdAt: new Date() });
    }
  }

  private createForm(): FormGroup {
    return this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(100)]],
      description: [''],
      serialNumber: ['', [Validators.required, Validators.maxLength(50)]],
      createdAt: [new Date(), Validators.required],
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.value;
    const payload: Dummy = {
      ...(this.dummy && this.dummy.id ? { id: this.dummy.id } : {}),
      name: value.name,
      description: value.description,
      serialNumber: value.serialNumber,
      createdAt: value.createdAt instanceof Date ? value.createdAt : new Date(value.createdAt),
    };

    this.save.emit(payload);
  }

  onCancel(): void {
    this.cancel.emit();
  }

  // Helpers para plantilla
  get name() {
    return this.form.get('name');
  }

  get serialNumber() {
    return this.form.get('serialNumber');
  }
}
