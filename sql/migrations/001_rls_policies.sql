-- =========================================================================
-- Package Price Calculator - Row Level Security (RLS)
-- Migration 001 (Interim: anonymous full CRUD)
--
-- Run AFTER sql/hiresmcq_lens_calculator_postgres.sql.
-- NOTE: the base schema drops and recreates tables, so re-apply this file
--       whenever the base schema is re-run.
--
-- SECURITY MODEL (interim)
--   This app is a single-operator tool that talks to Supabase with the
--   project anon key, which is embedded in the APK. The app manages the
--   catalog in-place (add / edit / delete equipment, taxonomy and
--   templates) and creates / edits / deletes saved packages, so the anon
--   role is granted full CRUD on the application tables.
--
--   This matches the current (open) exposure: anyone holding the anon key
--   can read or write these tables. Enabling RLS here makes the model
--   explicit and documents it. It does NOT make the data private.
--
--   HARDENING (recommended next step): enable Supabase Auth, add a
--   user_id ownership column on packages/package_equipment, and replace
--   the anon policies below with auth.uid() = user_id policies.
-- =========================================================================

BEGIN;

-- ============================ Grants ============================
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE
    public.category_types,
    public.equipment_types,
    public.equipment_details,
    public.templates,
    public.template_equipment,
    public.packages,
    public.package_equipment
TO anon;

-- access_logs is append-only from the client.
GRANT SELECT, INSERT ON TABLE public.access_logs TO anon;

GRANT USAGE ON ALL SEQUENCES IN SCHEMA public TO anon;

-- ============================ Enable RLS ============================
ALTER TABLE public.category_types      ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.equipment_types     ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.equipment_details   ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.templates           ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.template_equipment  ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.packages            ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.package_equipment   ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.access_logs         ENABLE ROW LEVEL SECURITY;

-- ============================ Policies (anon) ============================
-- Application tables: full CRUD for the anon role (interim model).
DROP POLICY IF EXISTS anon_all_category_types    ON public.category_types;
DROP POLICY IF EXISTS anon_all_equipment_types   ON public.equipment_types;
DROP POLICY IF EXISTS anon_all_equipment_details ON public.equipment_details;
DROP POLICY IF EXISTS anon_all_templates         ON public.templates;
DROP POLICY IF EXISTS anon_all_template_equip    ON public.template_equipment;
DROP POLICY IF EXISTS anon_all_packages          ON public.packages;
DROP POLICY IF EXISTS anon_all_package_equip     ON public.package_equipment;

CREATE POLICY anon_all_category_types    ON public.category_types    FOR ALL TO anon USING (true) WITH CHECK (true);
CREATE POLICY anon_all_equipment_types   ON public.equipment_types   FOR ALL TO anon USING (true) WITH CHECK (true);
CREATE POLICY anon_all_equipment_details ON public.equipment_details FOR ALL TO anon USING (true) WITH CHECK (true);
CREATE POLICY anon_all_templates         ON public.templates         FOR ALL TO anon USING (true) WITH CHECK (true);
CREATE POLICY anon_all_template_equip    ON public.template_equipment FOR ALL TO anon USING (true) WITH CHECK (true);
CREATE POLICY anon_all_packages          ON public.packages          FOR ALL TO anon USING (true) WITH CHECK (true);
CREATE POLICY anon_all_package_equip     ON public.package_equipment FOR ALL TO anon USING (true) WITH CHECK (true);

-- access_logs: append-only.
DROP POLICY IF EXISTS anon_read_access_logs  ON public.access_logs;
DROP POLICY IF EXISTS anon_write_access_logs ON public.access_logs;
CREATE POLICY anon_read_access_logs  ON public.access_logs FOR SELECT TO anon USING (true);
CREATE POLICY anon_write_access_logs ON public.access_logs FOR INSERT TO anon WITH CHECK (true);

COMMIT;
