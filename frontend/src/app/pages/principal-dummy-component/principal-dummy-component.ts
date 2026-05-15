import { Component, OnInit } from '@angular/core';
import { DummyService } from '../../services/dummy-service';
import { Dummy } from '../../models/dummy';
import { CommonModule } from '@angular/common';
import { DummyList } from '../../components/dummy-list/dummy-list';
import { DummyForm } from '../../components/dummy-form/dummy-form';
import { formatDateToDDMMYYYY, parseDateFromDDMMYYYY } from '../../utils/date-utils';

@Component({
  selector: 'app-principal-dummy-component',
  standalone: true,
  imports: [CommonModule, DummyList, DummyForm],
  templateUrl: './principal-dummy-component.html',
  styleUrls: ['./principal-dummy-component.css']
})
export class PrincipalDummyComponent implements OnInit {
  dummies: Dummy[] = [];
  selectedDummy?: Dummy;

  // Inyección estándar a través del constructor
  constructor(private ds: DummyService) {}

  ngOnInit(): void {
    this.loadDummies();
  }

  loadDummies(): void {
    this.ds.getDummies().subscribe(list => {
      // Backend ahora devuelve createdAt en 'dd-MM-yyyy' (LocalDate) — parsearlo a Date para UI
      this.dummies = (list || []).map(d => ({
        ...d,
        createdAt: d.createdAt ? (parseDateFromDDMMYYYY((d as any).createdAt) ?? new Date()) : new Date()
      }));
    });
  }

  onCreate(dummy: Dummy): void {
    // Convertir createdAt a 'dd-MM-yyyy' que espera el backend (LocalDate)
    const payload: any = {
      ...dummy,
      createdAt: formatDateToDDMMYYYY((dummy as any).createdAt || new Date())
    };

    this.ds.createDummy(payload as any).subscribe(() => this.loadDummies());
  }

  onEdit(dummy: Dummy): void {
    this.selectedDummy = dummy;
  }

  onUpdate(dummy: Dummy): void {
    // Usar id como identificador principal, si no existe usar serialNumber como fallback
    const id = dummy.id ?? String(dummy.serialNumber);

    const payload: any = {
      ...dummy,
      createdAt: formatDateToDDMMYYYY((dummy as any).createdAt || new Date())
    };

    this.ds.updateDummy(String(id), payload as any).subscribe(() => {
      this.selectedDummy = undefined;
      this.loadDummies();
    });
  }

  onRemove(idOrSerial: string | number | undefined): void {
    // Si el backend tuviera endpoint delete, aquí lo llamaríamos.
    // Como no está implementado, simulamos la eliminación en el front usando id o serialNumber.
    this.dummies = this.dummies.filter(d => d.id !== idOrSerial && d.serialNumber !== idOrSerial);
  }

  onFormSavedOrCancelled(): void {
    this.selectedDummy = undefined;
    this.loadDummies();
  }
}
