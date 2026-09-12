import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { colors, spacing } from '../theme';

const STEPS = ['Template', 'Gear', 'Labor', 'Final'];

export function StepIndicator({ current, templateResolved }: { current: number; templateResolved?: boolean }) {
  return (
    <View style={styles.row}>
      {STEPS.map((label, index) => {
        const done = index < current || (index === 0 && (templateResolved ?? false));
        const active = index === current;
        return (
          <React.Fragment key={label}>
            {index > 0 && <View style={styles.connector} />}
            <View style={styles.step}>
              <View
                style={[
                  styles.dot,
                  active && styles.dotActive,
                  done && styles.dotDone,
                ]}
              >
                <Text style={styles.dotText}>{done ? 'Ã¢Å“â€œ' : index + 1}</Text>
              </View>
              <Text
                style={[styles.label, (active || done) && styles.labelActive]}
              >
                {label}
              </Text>
            </View>
          </React.Fragment>
        );
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    paddingHorizontal: spacing.md,
    paddingTop: spacing.md,
    paddingBottom: spacing.sm,
  },
  step: { alignItems: 'center', width: 64 },
  connector: {
    flex: 1,
    height: 2,
    backgroundColor: colors.border,
    alignSelf: 'center',
    marginHorizontal: 2,
    marginTop: -10,
  },
  dot: {
    width: 28,
    height: 28,
    borderRadius: 14,
    backgroundColor: colors.surfaceAlt,
    alignItems: 'center',
    justifyContent: 'center',
  },
  dotActive: { backgroundColor: colors.primary },
  dotDone: { backgroundColor: colors.success },
  dotText: { color: colors.textOnPrimary, fontWeight: '700', fontSize: 13 },
  label: { marginTop: spacing.xs, fontSize: 11, color: colors.textMuted },
  labelActive: { color: colors.text },
});