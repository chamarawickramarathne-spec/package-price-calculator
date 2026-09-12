import { supabase } from './supabase';
import type {
  CategoryType,
  EquipmentType,
  EquipmentDetail,
  PackageTemplate,
  TemplateEquipment,
} from '../types';

export async function fetchCategories(): Promise<CategoryType[]> {
  const { data, error } = await supabase
    .from('category_types')
    .select('id, name, description, is_active')
    .eq('is_active', 1)
    .order('name', { ascending: true });
  if (error) throw error;
  return (data ?? []) as unknown as CategoryType[];
}

export async function fetchEquipmentTypes(
  categoryId: number
): Promise<EquipmentType[]> {
  const { data, error } = await supabase
    .from('equipment_types')
    .select('id, name, category_id')
    .eq('category_id', categoryId)
    .order('name', { ascending: true });
  if (error) throw error;
  return (data ?? []) as unknown as EquipmentType[];
}

export async function fetchEquipmentByCategory(
  categoryId: number
): Promise<EquipmentDetail[]> {
  const { data, error } = await supabase
    .from('equipment_details')
    .select('id, type, equipment, name, model, value, description, is_active')
    .eq('category_id', categoryId)
    .eq('is_active', 1)
    .order('equipment', { ascending: true });
  if (error) throw error;
  return (data ?? []) as unknown as EquipmentDetail[];
}

export async function fetchTemplates(): Promise<PackageTemplate[]> {
  const { data, error } = await supabase
    .from('templates')
    .select('*')
    .eq('is_active', 1)
    .order('name', { ascending: true });
  if (error) throw error;
  return (data ?? []) as unknown as PackageTemplate[];
}

export function groupTemplateEquipment(
  all: TemplateEquipment[],
  templateId: number
): Map<number, number> {
  const map = new Map<number, number>();
  for (const row of all) {
    if (row.template_id === templateId) {
      map.set(row.equipment_id, row.quantity);
    }
  }
  return map;
}

export async function fetchTemplateEquipment(
  templateIds: number[]
): Promise<TemplateEquipment[]> {
  const ids = templateIds.length > 0 ? templateIds : [-1];
  const { data, error } = await supabase
    .from('template_equipment')
    .select('id, template_id, equipment_id, quantity')
    .in('template_id', ids);
  if (error) throw error;
  return (data ?? []) as unknown as TemplateEquipment[];
}

export async function fetchEquipmentValuesByIds(
  equipmentIds: number[]
): Promise<Array<{ id: number; name: string; value: number }>> {
  const ids = equipmentIds.length > 0 ? equipmentIds : [-1];
  const { data, error } = await supabase
    .from('equipment_details')
    .select('id, name, value')
    .in('id', ids)
    .eq('is_active', 1);
  if (error) throw error;
  return (data ?? []) as unknown as Array<{ id: number; name: string; value: number }>;
}
