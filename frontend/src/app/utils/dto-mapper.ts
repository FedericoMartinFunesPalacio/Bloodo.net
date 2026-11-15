import { formatDateToDDMMYYYY } from './date-utils';

function toSnakeCase(str: string): string {
  return str.replace(/[A-Z]/g, letter => `_${letter.toLowerCase()}`);
}

function toCamelCase(str: string): string {
  return str.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase());
}

function mapKeysToSnakeCase(obj: Record<string, any>): Record<string, any> {
  const result: Record<string, any> = {};
  for (const key of Object.keys(obj)) {
    result[toSnakeCase(key)] = obj[key];
  }
  return result;
}

function mapKeysToCamelCase(obj: Record<string, any>): Record<string, any> {
  const result: Record<string, any> = {};
  for (const key of Object.keys(obj)) {
    result[toCamelCase(key)] = obj[key];
  }
  return result;
}

export function mapCampaignFromApi(data: Record<string, any>): Record<string, any> {
  return mapKeysToCamelCase(data);
}

export function mapCampaignArrayFromApi(data: Record<string, any>[]): Record<string, any>[] {
  return data.map(item => mapKeysToCamelCase(item));
}

export function mapDonorFromApi(data: Record<string, any>): Record<string, any> {
  return mapKeysToCamelCase(data);
}

export function mapDonorArrayFromApi(data: Record<string, any>[]): Record<string, any>[] {
  return data.map(item => mapKeysToCamelCase(item));
}

export function mapOrganizerPerFromApi(data: Record<string, any>): Record<string, any> {
  return mapKeysToCamelCase(data);
}

export function mapOrganizerPerArrayFromApi(data: Record<string, any>[]): Record<string, any>[] {
  return data.map(item => mapKeysToCamelCase(item));
}

export function mapOrganizerEmpFromApi(data: Record<string, any>): Record<string, any> {
  return mapKeysToCamelCase(data);
}

export function mapOrganizerEmpArrayFromApi(data: Record<string, any>[]): Record<string, any>[] {
  return data.map(item => mapKeysToCamelCase(item));
}

export function mapDonorToApi(donor: Record<string, any>): Record<string, any> {
  const mapped = mapKeysToSnakeCase(donor);
  if (mapped['birthdate']) {
    mapped['birthdate'] = formatDateToDDMMYYYY(mapped['birthdate']);
  }
  return mapped;
}

export function mapCampaignToApi(campaign: Record<string, any>): Record<string, any> {
  const mapped = mapKeysToSnakeCase(campaign);
  if (mapped['start_date']) {
    mapped['start_date'] = formatDateToDDMMYYYY(mapped['start_date']);
  }
  if (mapped['end_date']) {
    mapped['end_date'] = formatDateToDDMMYYYY(mapped['end_date']);
  }
  return mapped;
}

export function mapOrganizerPerToApi(org: Record<string, any>): Record<string, any> {
  const mapped = mapKeysToSnakeCase(org);
  if (mapped['birthdate']) {
    mapped['birthdate'] = formatDateToDDMMYYYY(mapped['birthdate']);
  }
  return mapped;
}

export function mapUserToApi(user: Record<string, any>): Record<string, any> {
  const mapped = mapKeysToSnakeCase(user);
  return mapped;
}

export function mapOrganizerEmpToApi(org: Record<string, any>): Record<string, any> {
  return mapKeysToSnakeCase(org);
}
