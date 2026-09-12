import React from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import { useCalculator } from '../state/calculator';
import { colors, spacing, radii, fontSizes, typography } from '../theme';

interface Props {
  onBack: () => void;
  onReset: () => void;
}

export function FinalScreen({ onBack, onReset }: Props) {
  const { state, breakdown } = useCalculator();

  if (!breakdown) {
    return (
      <View style={styles.screen}>
        <Text style={styles.empty}>Nothing to calculate yet.</Text>
      </View>
    );
  }

  const lines = [...state.gear.values()];

  return (
    <View style={styles.screen}>
      <Text style={styles.title}>Package Quote</Text>

      <Text style={styles.section}>{state.template?.name ?? 'Custom package'}</Text>

      <View style={styles.card}>
        {lines.map((line) => (
          <View key={line.equipmentId} style={styles.row}>
            <View style={styles.rowInfo}>
              <Text style={styles.rowName}>{line.name}</Text>
              <Text style={styles.rowSub}>
                {line.quantity} Ã— LKR {line.value.toLocaleString()}
              </Text>
            </View>
            <Text style={styles.rowTotal}>
              LKR {(line.value * line.quantity).toLocaleString()}
            </Text>
          </View>
        ))}
        <View style={styles.divider} />
        <View style={styles.row}>
          <Text style={styles.rowName}>Equipment total</Text>
          <Text style={styles.rowTotal}>
            LKR {breakdown.equipmentTotal.toLocaleString()}
          </Text>
        </View>
        {breakdown.laborTotal > 0 ? (
          <View style={styles.row}>
            <Text style={styles.rowName}>Labor ({state.laborHours}h Ã— LKR {state.hourlyRate})</Text>
            <Text style={styles.rowTotal}>LKR {breakdown.laborTotal.toLocaleString()}</Text>
          </View>
        ) : null}
        {breakdown.additionalCosts > 0 ? (
          <View style={styles.row}>
            <Text style={styles.rowName}>Additional costs</Text>
            <Text style={styles.rowTotal}>LKR {breakdown.additionalCosts.toLocaleString()}</Text>
          </View>
        ) : null}
        <View style={styles.divider} />
        <View style={styles.row}>
          <Text style={styles.rowName}>Subtotal</Text>
          <Text style={styles.rowTotal}>LKR {breakdown.subtotal.toLocaleString()}</Text>
        </View>
        <View style={styles.row}>
          <Text style={styles.rowName}>Margin ({state.marginPercentage}%)</Text>
          <Text style={styles.rowTotal}>+ LKR {breakdown.marginAmount.toLocaleString()}</Text>
        </View>
        <View style={styles.divider} />
        <View style={styles.row}>
          <Text style={styles.rowNameBold}>Total (incl. tax)</Text>
          <Text style={styles.rowTotalBold}>LKR {breakdown.finalTotal.toLocaleString()}</Text>
        </View>
      </View>

      <Text style={styles.hint}>Saved to the price catalog and access log.</Text>

      <View style={styles.footer}>
        <Pressable style={styles.secondary} onPress={onBack}>
          <Text style={styles.secondaryText}>Back</Text>
        </Pressable>
        <Pressable style={styles.primary} onPress={onReset}>
          <Text style={styles.primaryText}>New Package</Text>
        </Pressable>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, padding: spacing.lg, gap: spacing.md },
  empty: { ...typography.body, color: colors.textMuted, textAlign: 'center', marginTop: spacing.xl },
  title: { ...typography.header, color: colors.text },
  section: { ...typography.subtitle, color: colors.primary },
  card: {
    backgroundColor: colors.surface,
    borderRadius: radii.lg,
    padding: spacing.lg,
    gap: spacing.sm,
  },
  row: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  rowInfo: { flex: 1, paddingRight: spacing.md },
  rowName: { ...typography.body, color: colors.text },
  rowNameBold: { ...typography.body, color: colors.text, fontWeight: '700' },
  rowSub: { ...typography.caption, color: colors.textMuted },
  rowTotal: { ...typography.body, color: colors.text },
  rowTotalBold: { ...typography.title, color: colors.success },
  divider: { height: 1, backgroundColor: colors.border },
  hint: { ...typography.caption, color: colors.textMuted, textAlign: 'center' },
  footer: { flexDirection: 'row', justifyContent: 'space-between', marginTop: 'auto' },
  primary: { backgroundColor: colors.primary, borderRadius: radii.pill, paddingVertical: spacing.md, paddingHorizontal: spacing.xl },
  primaryText: { color: colors.textOnPrimary, fontWeight: '700', fontSize: fontSizes.md },
  secondary: { borderWidth: 1, borderColor: colors.border, borderRadius: radii.pill, paddingVertical: spacing.md, paddingHorizontal: spacing.xl },
  secondaryText: { color: colors.text, fontWeight: '600', fontSize: fontSizes.md },
});