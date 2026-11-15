import { Pipe, PipeTransform } from '@angular/core';

const LABELS: Record<string, string> = {
  POSITIVE: 'Positivo',
  NEGATIVE: 'Negativo',
  MALE: 'Masculino',
  FEMALE: 'Femenino',
  OTHER: 'Otro',
  ADMIN: 'Administrador',
  DONOR: 'Donador',
  ORGANIZER: 'Organizador',
  A: 'A',
  B: 'B',
  AB: 'AB',
  O: 'O',
  EMP: 'Empresa',
  PER: 'Persona',
  ACTIVA: 'Activa',
  FINALIZADA: 'Finalizada'
};

@Pipe({
  name: 'enumLabel',
  standalone: true
})
export class EnumLabelPipe implements PipeTransform {
  transform(value: string | null | undefined): string {
    if (!value) return '';
    if (value.includes('_')) {
      return value.split('_').map(part => LABELS[part] || part).join(' ');
    }
    return LABELS[value] || value;
  }
}
