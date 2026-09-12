import React, { useState } from 'react';
import { Pressable, StyleSheet, Text, TextInput, View } from 'react-native';
import { useCalculator } from '../state/calculator';
import { colors, spacing, radii, fontSizes, typography } from '../theme';

interface Props {
  onBack: () => void;
  onContinue: () => void;
}

export function LaborScreen({ onBack, onContinue }: Props) {
  const { state, dispatch, breakdown } = useCalculator();

  const [hoursText, setHoursText] = useState(
    state.laborHours > 0 ? String(state.laborHours) : ''
  );
  const [rateText, setRateText] = useState(
    state.hourlyRate > 0 ? String(state.hourlyRate) : ''
  );
  const [costsText, setCostsText] = useState(
    state.additionalCosts > 0 ? String(state.additionalCosts) : ''
  );
  const [marginText, setMarginText] = useState(
    state.marginPercentage > 0 ? String(state.marginPercentage) : ''
  );

  function commitHours(value: string) {
    setHoursText(value);
    const n = parseFloat(value);
    if (!Number.isNaN(n)) dispatch({ type: 'SET_LABOR_HOURS', hours: Math.max(0, n) });
  }

  function commitRate(value: string) {
    setRateText(value);
    const n = parseFloat(value);
    if (!Number.isNaN(n)) dispatch({ type: 'SET_HOURLY_RATE', rate: Math.max(0, n) });
  }

  function commitCosts(value: string) {
    setCostsText(value);
    const n = parseFloat(value);
    if (!Number.isNaN(n)) dispatch({ type: 'SET_ADDITIONAL_COSTS', costs: Math.max(0, n) });
  }

  function commitMargin(value: string) {
    setMarginText(value);
    const n = parseFloat(value);
    if (!Number.isNaN(n)) dispatch({ type: 'SET_MARGIN', percentage: Math.max(0, Math.min(100, n)) });
  }

  return (
    <View style={styles.screen}>
      <Text style={styles.title}>Labor & Margin</Text>

      <View style={styles.field}>
        <Text style={styles.label}>Labor hours</Text>
        <TextInput
          style={styles.input}
          value={hoursText}
          onChangeText={commitHours}
          keyboardType="numeric"
          placeholder="0"
          placeholderTextColor={colors.textMuted}
        />
      </View>

      <View style={styles.field}>
        <Text style={styles.label}>Hourly rate (LKR)</Text>
        <TextInput
          style={styles.input}
          value={rateText}
          onChangeText={commitRate}
          keyboardType="numeric"
          placeholder="0"
          placeholderTextColor={colors.textMuted}
        />
      </View>

      <View style={styles.field}>
        <Text style={styles.label}>Additional costs (LKR)</Text>
        <TextInput
          style={styles.input}
          value={costsText}
          onChangeText={commitCosts}
          keyboardType="numeric"
          placeholder="0"
          placeholderTextColor={colors.textMuted}
        />
      </View>

      <View style={styles.field}>
        <Text style={styles.label}>Margin (%)</Text>
        <TextInput
          style={styles.input}
          value={marginText}
          onChangeText={commitMargin}
          keyboardType="numeric"
          placeholder="0"
          placeholderTextColor={colors.textMuted}
        />
      </View>

      {breakdown ? (
        <View style={styles.preview}>
          <Text style={styles.previewTitle}>Live totals</Text>
          <Text style={styles.previewLine}>
            Labor: LKR {breakdown.laborTotal.toLocaleString()}
          </Text>
          <Text style={styles.previewLine}>
            Equipment: LKR {breakdown.equipmentTotal.toLocaleString()}
          </Text>
          <Text style={styles.previewLine}>
            Subtotal: LKR {breakdown.subtotal.toLocaleString()}
          </Text>
          <Text style={styles.previewLine}>
            Final: LKR {breakdown.finalTotal.toLocaleString()}
          </Text>
        </View>
      ) : null}

      <View style={styles.footer}>
        <Pressable style={styles.secondary} onPress={onBack}>
          <Text style={styles.secondaryText}>Back</Text>
        </Pressable>
        <Pressable style={styles.primary} onPress={onContinue}>
          <Text style={styles.primaryText}>Continue</Text>
        </Pressable>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, padding: spacing.lg, gap: spacing.md },
  title: { ...typography.header, color: colors.text },
  field: { gap: spacing.xs },
  label: { ...typography.body, color: colors.textMuted },
  input: {
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: radii.md,
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.sm,
    color: colors.text,
    fontSize: fontSizes.lg,
    backgroundColor: colors.surface,
  },
  preview: {
    backgroundColor: colors.surfaceAlt,
    borderRadius: radii.md,
    padding: spacing.lg,
    gap: spacing.xs,
  },
  previewTitle: { ...typography.title, color: colors.text },
  previewLine: { ...typography.body, color: colors.textMuted },
  footer: { flexDirection: 'row', justifyContent: 'space-between', marginTop: 'auto' },
  primary: { backgroundColor: colors.primary, borderRadius: radii.pill, paddingVertical: spacing.md, paddingHorizontal: spacing.xl },
  primaryText: { color: colors.textOnPrimary, fontWeight: '700', fontSize: fontSizes.md },
  secondary: { borderWidth: 1, borderColor: colors.border, borderRadius: radii.pill, paddingVertical: spacing.md, paddingHorizontal: spacing.xl },
  secondaryText: { color: colors.text, fontWeight: '600', fontSize: fontSizes.md },
});