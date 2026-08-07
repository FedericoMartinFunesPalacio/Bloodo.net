export function isValidEmail(email: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

export function isValidPhone(phone: string): boolean {
  const cleaned = phone.trim();
  if (cleaned.length < 8 || cleaned.length > 13) return false;
  return /^\d+$/.test(cleaned);
}

export function isValidDocument(doc: string): boolean {
  const cleaned = doc.trim();
  if (cleaned.length < 7 || cleaned.length > 13) return false;
  if (/\s/.test(cleaned)) return false;
  return /^\d+(-\d+)*$/.test(cleaned);
}

export function isValidBirthdate(dateStr: string): boolean {
  if (!dateStr) return false;
  const parts = dateStr.split('-');
  if (parts.length !== 3) return false;
  let year: number, month: number, day: number;
  if (parts[0].length === 4) {
    year = parseInt(parts[0], 10);
    month = parseInt(parts[1], 10);
    day = parseInt(parts[2], 10);
  } else {
    day = parseInt(parts[0], 10);
    month = parseInt(parts[1], 10);
    year = parseInt(parts[2], 10);
  }
  if (year < 1930 || year > new Date().getFullYear()) return false;
  const today = new Date();
  let age = today.getFullYear() - year;
  if (today.getMonth() + 1 < month || (today.getMonth() + 1 === month && today.getDate() < day)) {
    age--;
  }
  return age >= 18;
}

export function isFutureDate(dateStr: string): boolean {
  if (!dateStr) return false;
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const isoMatch = dateStr.match(/^(\d{4})-(\d{2})-(\d{2})$/);
  let inputDate: Date;
  if (isoMatch) {
    inputDate = new Date(+isoMatch[1], +isoMatch[2] - 1, +isoMatch[3]);
  } else {
    inputDate = new Date(dateStr + 'T00:00:00');
  }
  return inputDate >= today;
}
