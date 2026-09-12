import Constants from 'expo-constants';
import { Platform } from 'react-native';

const fromEnvUrl = process.env.EXPO_PUBLIC_SUPABASE_URL;
const fromEnvKey = process.env.EXPO_PUBLIC_SUPABASE_ANON_KEY;
const extraUrl = (Constants.expoConfig?.extra as Record<string, unknown> | undefined)?.supabaseUrl as
  | string
  | undefined     ;
const extraKey = (Constants.expoConfig?.extra as Record<string, unknown> | undefined)?.supabaseAnonKey as
  | string
  | undefined;

export const SUPABASE_URL: string =
  fromEnvUrl || extraUrl || 'https://vpuzpcyrrteadmolqycl.supabase.co';

export const SUPABASE_ANON_KEY: string =
  fromEnvKey || extraKey || '';

export const canReachSupabase = SUPABASE_URL.startsWith('https://') && SUPABASE_ANON_KEY.length > 20;

export const SITE_URL = 'https://packagepricecalculator.hireartist.studio';

export const IS_WEB = Platform.OS === 'web';
export const IS_ANDROID = Platform.OS === 'android';

export const LEGACY_NOTICE =
  'This is a fresh React Native (Expo SDK 57) front end. The Supabase tables are provisioned; run the SQL scripts (sql/schema, sql/seed, sql/migrations) in the Supabase SQL Editor before first use.';
