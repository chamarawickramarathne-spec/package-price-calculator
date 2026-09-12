export const colors = {
  background: '#0F172A',
  surface: '#1E293B',
  surfaceAlt: '#273449',
  border: '#334155',
  primary: '#6366F1',
  primaryPressed: '#4F46E5',
  accent: '#22D3EE',
  success: '#34D399',
  warning: '#FBBF24',
  danger: '#F87171',
  text: '#F8FAFC',
  textMuted: '#94A3B8',
  textOnPrimary: '#FFFFFF',
};

export const spacing = {
  xs: 4,
  sm: 8,
  md: 12,
  lg: 16,
  xl: 24,
  xxl: 32,
};

export const radii = {
  sm: 8,
  md: 12,
  lg: 16,
  pill: 999,
};

export const fontSizes = {
  sm: 13,
  md: 15,
  lg: 17,
  xl: 22,
  xxl: 28,
};

export const typography = {
  header: { fontWeight: '700' as const },
  title: { fontSize: fontSizes.xl, fontWeight: '600' as const },
  subtitle: { fontSize: fontSizes.lg, fontWeight: '600' as const },
  body: { fontSize: fontSizes.md },
  caption: { fontSize: fontSizes.sm, color: colors.textMuted },
  muted: { color: colors.textMuted },
};
