export interface CategoryType {
  id: number;
  name: string;
  description: string | null;
  sort_order: number | null;
  is_active: boolean | number;
}

export interface EquipmentType {
  id: number;
  name: string;
  category_id: number;
  description: string | null;
}

export interface EquipmentDetail {
  id: number;
  category_id: number;
  type: number;
  equipment: string;
  name: string | null;
  model: string | null;
  value: number;
  description: string | null;
  is_active: boolean | number;
}

export interface PackageTemplate {
  id: number;
  name: string;
  description: string | null;
  labor_hours: number | null;
  hourly_rate: number | null;
  margin_percentage: number | null;
  is_active: boolean | number;
}

export interface TemplateEquipment {
  id: number;
  template_id: number;
  equipment_id: number;
  quantity: number;
}

export interface ScopedEquipment {
  id: number;
  type: number;
  equipment: string;
  name: string | null;
  model: string | null;
  value: number;
  is_active: boolean | number;
}

export interface ScopedPackage {
  id: number;
  name: string;
  description: string | null;
  labor_hours: number | null;
  hourly_rate: number | null;
  margin_percentage: number | null;
  equipment: ScopedEquipment[];
}

export interface SavedPackageRecord {
  id: number;
  name: string;
  service_type: string | null;
  category_id: number | null;
  equipment_total: number;
  labor_total: number;
  additional_costs: number;
  subtotal: number;
  margin_percentage: number;
  margin_amount: number;
  final_total: number;
  created_at: string | null;
}

export interface AccessLogRecord {
  id: number;
  page: string | null;
  session_id: string | null;
  action: string | null;
  details: string | null;
  created_at: string | null;
}
