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
  const year = parseInt(parts[0], 10);
  return year >= 1930 && year <= new Date().getFullYear();
}

export function isFutureDate(dateStr: string): boolean {
  if (!dateStr) return false;
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const inputDate = new Date(dateStr);
  return inputDate >= today;
}
