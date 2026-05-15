// Utility helpers para convertir entre Date y formato 'dd-MM-yyyy'
export function formatDateToDDMMYYYY(date: Date | string | undefined | null): string {
  if (!date) return '';
  const d = date instanceof Date ? date : new Date(date);
  if (isNaN(d.getTime())) return '';
  const day = String(d.getDate()).padStart(2, '0');
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const year = String(d.getFullYear());
  return `${day}-${month}-${year}`;
}

export function parseDateFromDDMMYYYY(value: string | Date | undefined | null): Date | null {
  if (!value) return null;
  if (value instanceof Date) return isNaN(value.getTime()) ? null : value;
  // Expect format dd-MM-yyyy
  const parts = value.split('-');
  if (parts.length !== 3) return null;
  const day = Number(parts[0]);
  const month = Number(parts[1]);
  const year = Number(parts[2]);
  if (Number.isNaN(day) || Number.isNaN(month) || Number.isNaN(year)) return null;
  // Note: month in Date constructor is 0-based
  const d = new Date(year, month - 1, day);
  // Validate round-trip
  if (d.getFullYear() !== year || d.getMonth() !== month - 1 || d.getDate() !== day) return null;
  return d;
}

