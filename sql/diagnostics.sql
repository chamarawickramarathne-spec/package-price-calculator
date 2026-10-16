-- =========================================================================
-- Package Price Calculator - Database diagnostics (READ-ONLY)
--
-- Run this in the Supabase SQL Editor of the project whose URL is:
--     https://vpuzpcyrrteadmolqycl.supabase.co
--
-- It changes nothing. Copy ALL result rows and paste them back.
-- It answers three questions:
--   1. Are the app tables present? (wrong project vs. right project)
--   2. Is RLS enabled and are the anon policies present? (why 42501)
--   3. Is the seed data actually loaded? (why Content-Range: */0)
-- =========================================================================

-- 1. Where are we?
select current_database() as database, current_user as running_as;

-- 2. Do the app tables exist, and is RLS on?
select relname              as table_name,
       relrowsecurity       as rls_enabled,
       relforcerowsecurity  as rls_forced
from pg_class
where relnamespace = 'public'::regnamespace
  and relname in ('category_types','equipment_types','equipment_details',
                  'templates','template_equipment','packages',
                  'package_equipment','access_logs')
order by relname;

-- 3. Which policies exist on the app tables?
select tablename, policyname, cmd, roles
from pg_policies
where schemaname = 'public'
order by tablename, policyname;

-- 4. Row counts (proves whether the seed data loaded)
select 'category_types'    as table_name, count(*) as rows from public.category_types
union all select 'equipment_types',    count(*) from public.equipment_types
union all select 'equipment_details',  count(*) from public.equipment_details
union all select 'templates',          count(*) from public.templates
union all select 'template_equipment', count(*) from public.template_equipment
union all select 'packages',           count(*) from public.packages
union all select 'package_equipment',  count(*) from public.package_equipment
order by table_name;

-- 5. What can the anon role do on these tables?
select table_name, privilege_type
from information_schema.role_table_grants
where grantee = 'anon'
  and table_schema = 'public'
order by table_name, privilege_type;
