import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { Dummy } from '../../models/dummy';
import {DatePipe, CommonModule} from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-dummy-list',
  standalone: true,
  imports: [
    CommonModule,
    DatePipe,
    MatTableModule,
    MatButtonModule
  ],
  templateUrl: './dummy-list.html',
  styleUrls: ['./dummy-list.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DummyList {
  /** Lista de dummies provista por el componente padre */
  @Input() dummies: Dummy[] = [];

  /** Emite el Dummy seleccionado para editar */
  @Output() edit = new EventEmitter<Dummy>();

  /** Emite el id del Dummy que se quiere eliminar */
  @Output() remove = new EventEmitter<string | undefined>();

  /** Columnas visibles en la tabla Material */
  displayedColumns = ['id', 'name', 'description', 'serialNumber', 'createdAt', 'actions'];

  get dataSource() {
    return this.dummies || [];
  }

  onEdit(dummy: Dummy) {
    this.edit.emit(dummy);
  }

  onRemove(dummy: Dummy) {
    this.remove.emit(dummy.id);
  }
}
