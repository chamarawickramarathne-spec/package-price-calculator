import React, { useCallback, useEffect, useMemo, useState } from 'react';
import {
  ActivityIndicator,
  FlatList,
  Pressable,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { colors, fontSizes, radii, spacing, typography } from '../theme';
import type { CategoryType, ScopedEquipment } from '../types';
import { fetchCategories, fetchEquipmentByCategory } from '../lib/api';
import { useCalculator } from '../state/calculator';

interface Props {
  onBack: () => void;
  onContinue: () => void;
}

export function GearScreen({ onBack, onContinue }: Props) {
  const { state, dispatch, canGoNext, breakdown } = useCalculator();
  const [categories, setCategories] = useState<CategoryType[]>([]);
  const [activeCategory, setActiveCategory] = useState<number | null>(null);
  const [gear, setGear] = useState<ScopedEquipment[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let alive = true;
    fetchCategories()
      .then((rows) => {
        if (!alive) return;
        setCategories(rows);
        setActiveCategory(rows[0]?.id ?? null);
      })
      .catch((err: unknown) => {
        if (alive) setError(err instanceof Error ? err.message : String(err));
      })
      .finally(() => {
        if (alive) setLoading(false);
      });
    return () => {
      alive = false;
    };
  }, []);

  useEffect(() => {
    if (activeCategory == null) return;
    let alive = true;
    setLoading(true);
    fetchEquipmentByCategory(activeCategory)
      .then((rows) => {
        if (alive) setGear(rows);
      })
      .catch((err: unknown) => {
        if (alive) setError(err instanceof Error ? err.message : String(err));
      })
      .finally(() => {
        if (alive) setLoading(false);
      });
    return () => {
      alive = false;
    };
  }, [activeCategory]);

  const editQty = useCallback(
    (equipmentId: number, delta: number) => {
      const current = (state.gear.get(equipmentId)?.quantity ?? 0) + delta;
      dispatch({
        type: 'SET_LINE_QUANTITY',
        equipmentId,
        quantity: Math.max(0, current),
      });
    },
    [state.gear, dispatch]
  );

  const lines = useMemo(
    () =>
      gear
        .filter((g) => g.is_active !== 0)
        .map((g) => ({
          equipment: g,
          qty: state.gear.get(g.id)?.quantity ?? 0,
        })),
    [gear, state.gear]
  );

  return (
    <View style={styles.screen}>
      <Text style={styles.title}>Pick your gear</Text>

      <View style={styles.chips}>
        {categories.map((c) => {
          const active = c.id === activeCategory;
          return (
            <Pressable
              key={c.id}
              style={[styles.chip, active && styles.chipActive]}
              onPress={() => setActiveCategory(c.id)}
            >
              <Text style={[styles.chipText, active && styles.chipTextActive]}>
                {c.name}
              </Text>
            </Pressable>
          );
        })}
      </View>

      {loading ? (
        <View style={styles.center}>
          <ActivityIndicator color={colors.primary} />
        </View>
      ) : error ? (
        <Text style={styles.error}>{error}</Text>
      ) : (
        <FlatList
          data={lines}
          keyExtractor={(item) => String(item.equipment.id)}
          renderItem={({ item }) => (
            <View style={styles.row}>
              <View style={styles.info}>
                <Text style={styles.name}>{item.equipment.equipment}</Text>
                {item.equipment.name ? (
                  <Text style={styles.model}>{item.equipment.name}</Text>
                ) : null}
                <Text style={styles.value}>
                  LKR {item.equipment.value.toLocaleString()}
                </Text>
              </View>
              <View style={styles.stepper}>
                <Pressable
                  style={styles.stepBtn}
                  onPress={() => editQty(item.equipment.id, -1)}
                >
                  <Text style={styles.stepBtnText}>âˆ’</Text>
                </Pressable>
                <Text style={styles.qty}>{item.qty}</Text>
                <Pressable
                  style={styles.stepBtn}
                  onPress={() => editQty(item.equipment.id, 1)}
                >
                  <Text style={styles.stepBtnText}>+</Text>
                </Pressable>
              </View>
            </View>
          )}
          contentContainerStyle={styles.list}
        />
      )}

      <View style={styles.footer}>
        <Pressable style={styles.secondary} onPress={onBack}>
          <Text style={styles.secondaryText}>Back</Text>
        </Pressable>
        <Pressable
          style={[styles.primary, !canGoNext && styles.primaryDisabled]}
          disabled={!canGoNext}
          onPress={onContinue}
        >
          <Text style={styles.primaryText}>Continue</Text>
        </Pressable>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, padding: spacing.lg, gap: spacing.md },
  title: { ...typography.header, color: colors.text },
  chips: { flexDirection: 'row', flexWrap: 'wrap', gap: spacing.sm },
  chip: {
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: radii.pill,
    paddingVertical: spacing.xs + 2,
    paddingHorizontal: spacing.md,
    backgroundColor: colors.surface,
  },
  chipActive: { backgroundColor: colors.primary, borderColor: colors.primary },
  chipText: { color: colors.textMuted, fontWeight: '600' },
  chipTextActive: { color: colors.textOnPrimary },
  center: { flex: 1, alignItems: 'center', justifyContent: 'center' },
  error: { color: colors.danger, textAlign: 'center' },
  list: { gap: spacing.xs },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: colors.surface,
    borderRadius: radii.md,
    padding: spacing.md,
  },
  info: { flex: 1, paddingRight: spacing.md },
  name: { ...typography.body, color: colors.text, fontWeight: '600' },
  model: { ...typography.caption, color: colors.textMuted, marginTop: 2 },
  value: { ...typography.body, color: colors.success, marginTop: 4 },
  stepper: { flexDirection: 'row', alignItems: 'center', gap: spacing.md },
  stepBtn: {
    width: 34,
    height: 34,
    borderRadius: 17,
    backgroundColor: colors.surfaceAlt,
    alignItems: 'center',
    justifyContent: 'center',
  },
  stepBtnText: { color: colors.primary, fontSize: fontSizes.xl, fontWeight: '700' },
  qty: { ...typography.body, color: colors.text, minWidth: 24, textAlign: 'center' },
  footer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 'auto',
  },
  primary: {
    backgroundColor: colors.primary,
    borderRadius: radii.pill,
    paddingVertical: spacing.md,
    paddingHorizontal: spacing.xxl,
  },
  primaryDisabled: { opacity: 0.4 },
  primaryText: { color: colors.textOnPrimary, fontWeight: '700' },
  secondary: {
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: radii.pill,
    paddingVertical: spacing.md,
    paddingHorizontal: spacing.xxl,
  },
  secondaryText: { color: colors.text, fontWeight: '600' },
});