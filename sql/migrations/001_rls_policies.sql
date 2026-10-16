-- =========================================================================
-- Package Price Calculator - Row Level Security (RLS)
-- Migration 001 (Interim: anonymous SELECT + INSERT on the catalog)
--
-- Run AFTER sql/hiresmcq_lens_calculator_postgres.sql, or instead of both files
-- use the single paste-ready sql/install.sql (which includes this policy set).
-- NOTE: the base schema drops and recreates tables, so re-apply this file
--       whenever the base schema is re-run.
--
-- SECURITY MODEL (interim)
--   This app talks to Supabase with the project anon key, which is embedded in
--   the APK. The app reads the catalog and adds rows from its "add" forms
--   (equipment, categories, equipment types, templates), so the anon role is
--   granted exactly SELECT + INSERT on the five catalog tables.
--
--   Nothing in the app updates or deletes remote rows: no PATCH/DELETE
--   endpoints exist anymore, and no UPDATE/DELETE policy is created here, so
--   the anon role cannot alter or erase catalog data.
--
--   public.packages / public.package_equipment are NOT granted: since v1.1.0
--   saved packages are stored on the device (Room), so the app never touches
--   these tables remotely. public.access_logs is not granted either - the app
--   has no logging endpoints.
--
--   This matches the current exposure: anyone holding the anon key can read the
--   catalog and append rows to it. Enabling RLS here makes the model explicit
--   and documents it. It does NOT make the data private.
--
--   HARDENING (recommended next step): enable Supabase Auth and replace
--   the anon policies below with auth.uid() = user_id policies.
-- =========================================================================

BEGIN;

-- ============================ Grants ============================
GRANT SELECT, INSERT ON TABLE
    public.category_types,
    public.equipment_types,
    public.equipment_details,
    public.templates,
    public.template_equipment
TO anon;

GRANT USAGE ON ALL SEQUENCES IN SCHEMA public TO anon;

-- Tables the app never touches remotely: withdraw any earlier grant.
REVOKE ALL ON TABLE
    public.packages,
    public.package_equipment,
    public.access_logs
FROM anon;

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
-- Catalog tables: one SELECT and one INSERT policy each. No UPDATE/DELETE
-- policy exists, so the anon role can never rewrite or erase the catalog.
DROP POLICY IF EXISTS anon_all_category_types    ON public.category_types;
DROP POLICY IF EXISTS anon_all_equipment_types   ON public.equipment_types;
DROP POLICY IF EXISTS anon_all_equipment_details ON public.equipment_details;
DROP POLICY IF EXISTS anon_all_templates         ON public.templates;
DROP POLICY IF EXISTS anon_all_template_equip    ON public.template_equipment;
DROP POLICY IF EXISTS anon_sel_category_types    ON public.category_types;
DROP POLICY IF EXISTS anon_sel_equipment_types   ON public.equipment_types;
DROP POLICY IF EXISTS anon_sel_equipment_details ON public.equipment_details;
DROP POLICY IF EXISTS anon_sel_templates         ON public.templates;
DROP POLICY IF EXISTS anon_sel_template_equip    ON public.template_equipment;
DROP POLICY IF EXISTS anon_ins_category_types    ON public.category_types;
DROP POLICY IF EXISTS anon_ins_equipment_types   ON public.equipment_types;
DROP POLICY IF EXISTS anon_ins_equipment_details ON public.equipment_details;
DROP POLICY IF EXISTS anon_ins_templates         ON public.templates;
DROP POLICY IF EXISTS anon_ins_template_equip    ON public.template_equipment;

CREATE POLICY anon_sel_category_types    ON public.category_types    FOR SELECT TO anon USING (true);
CREATE POLICY anon_ins_category_types    ON public.category_types    FOR INSERT TO anon WITH CHECK (true);

CREATE POLICY anon_sel_equipment_types   ON public.equipment_types   FOR SELECT TO anon USING (true);
CREATE POLICY anon_ins_equipment_types   ON public.equipment_types   FOR INSERT TO anon WITH CHECK (true);

CREATE POLICY anon_sel_equipment_details ON public.equipment_details FOR SELECT TO anon USING (true);
CREATE POLICY anon_ins_equipment_details ON public.equipment_details FOR INSERT TO anon WITH CHECK (true);

CREATE POLICY anon_sel_templates         ON public.templates         FOR SELECT TO anon USING (true);
CREATE POLICY anon_ins_templates         ON public.templates         FOR INSERT TO anon WITH CHECK (true);

CREATE POLICY anon_sel_template_equip    ON public.template_equipment FOR SELECT TO anon USING (true);
CREATE POLICY anon_ins_template_equip    ON public.template_equipment FOR INSERT TO anon WITH CHECK (true);

-- Packages + access_logs: RLS enabled above with NO anon policy -> deny.
DROP POLICY IF EXISTS anon_all_packages      ON public.packages;
DROP POLICY IF EXISTS anon_all_package_equip ON public.package_equipment;
DROP POLICY IF EXISTS anon_read_access_logs  ON public.access_logs;
DROP POLICY IF EXISTS anon_write_access_logs ON public.access_logs;

COMMIT;
