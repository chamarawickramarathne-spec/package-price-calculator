-- =========================================================================
-- Package Price Calculator - Row Level Security (RLS) for the anon key
-- Run AFTER sql/schema/supabase_schema.sql and sql/seed/supabase_live_data.sql
--
-- Security model:
--   SELECT  -> category_types, equipment_types, equipment_details,
--             templates, template_equipment          (public catalog)
--   INSERT  -> equipment_details (public "Add Gear"),
--             packages, package_equipment (save quote),
--             access_logs (visit logging)
--   NO SELECT on packages (saved quotes are private to the device),
--   NO UPDATE / DELETE anywhere for anonymous users.
-- =========================================================================

-- ============================ Grants ============================
GRANT SELECT ON TABLE public.category_types, public.equipment_types,
    public.equipment_details, public.templates, public.template_equipment TO anon;

GRANT INSERT ON TABLE public.equipment_details, public.packages,
    public.package_equipment, public.access_logs TO anon;

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

-- ============================ Public catalog: SELECT ============================
CREATE POLICY anon_select_categories   ON public.category_types     FOR SELECT TO anon USING (true);
CREATE POLICY anon_select_equip_types  ON public.equipment_types    FOR SELECT TO anon USING (true);
CREATE POLICY anon_select_equipment    ON public.equipment_details  FOR SELECT TO anon USING (true);
CREATE POLICY anon_select_templates    ON public.templates          FOR SELECT TO anon USING (true);
CREATE POLICY anon_select_template_eq  ON public.template_equipment FOR SELECT TO anon USING (true);

-- ============================ Anonymous INSERT ============================
CREATE POLICY anon_insert_equipment       ON public.equipment_details FOR INSERT TO anon WITH CHECK (true);
CREATE POLICY anon_insert_packages        ON public.packages          FOR INSERT TO anon WITH CHECK (true);
CREATE POLICY anon_insert_package_equip   ON public.package_equipment FOR INSERT TO anon WITH CHECK (true);
CREATE POLICY anon_insert_access_logs     ON public.access_logs       FOR INSERT TO anon WITH CHECK (true);

-- NOTE: No SELECT/UPDATE/DELETE policies for anon on packages,
-- package_equipment or access_logs. Anonymous clients cannot read or
-- modify stored quotes/logs; saved-package history is kept locally.
--
-- Future authentication (Supabase Auth) can add the 'authenticated' role
-- with full access to packages owned by the signed-in user.