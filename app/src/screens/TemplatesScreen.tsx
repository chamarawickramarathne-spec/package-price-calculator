import React, { useEffect, useMemo, useState } from 'react';
import {
  ActivityIndicator,
  FlatList,
  Pressable,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { colors, fontSizes, radii, spacing, typography } from '../theme';
import type { PackageTemplate } from '../types';
import { fetchTemplates } from '../lib/api';
import { groupTemplateEquipment, fetchTemplateEquipment, fetchEquipmentValuesByIds } from '../lib/api';
import { useCalculator, type CartLine } from '../state/calculator';

interface Props {
  onContinue: () => void;
}

export function TemplatesScreen({ onContinue }: Props) {
  const { dispatch } = useCalculator();
  const [templates, setTemplates] = useState<PackageTemplate[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let alive = true;
    setLoading(true);
    setError(null);
    fetchTemplates()
      .then((rows) => {
        if (!alive) return;
        setTemplates(rows);
      })
      .catch((err) => {
        if (!alive) return;
        setError(err instanceof Error ? err.message : String(err));
      })
      .finally(() => {
        if (alive) setLoading(false);
      });
    return () => {
      alive = false;
    };
  }, []);

  const [selectedId, setSelectedId] = useState<number | null>(null);

  async function pick(template: PackageTemplate) {
    setSelectedId(template.id);
    try {
      const links = await fetchTemplateEquipment([template.id]);
      const quantities = groupTemplateEquipment(links, template.id);
      const ids = [...quantities.keys()];
      const values = await fetchEquipmentValuesByIds(ids);
      const valueById = new Map(values.map((v) => [v.id, v]));
      const gear = new Map<number, CartLine>();
      for (const [equipmentId, qty] of quantities) {
        const v = valueById.get(equipmentId);
        gear.set(equipmentId, {
          equipmentId,
          name: v?.name ?? '',
          value: v?.value ?? 0,
          quantity: qty,
        });
      }
      dispatch({
        type: 'LOAD_TEMPLATE',
        template: {
          ...template,
        },
      });
      dispatch({ type: 'SET_GEAR', gear });
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err));
    }
  }

  const sorted = useMemo(
    () => [...templates].sort((a, b) => a.name.localeCompare(b.name)),
    [templates]
  );

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator color={colors.primary} />
      </View>
    );
  }

  if (error) {
    return (
      <View style={styles.center}>
        <Text style={styles.error}>{error}</Text>
        <Pressable style={styles.retry} onPress={() => {}}>
          <Text style={styles.retryText}>Retry</Text>
        </Pressable>
      </View>
    );
  }

  return (
    <FlatList
      data={sorted}
      keyExtractor={(item) => String(item.id)}
      renderItem={({ item }) => (
        <Pressable
          style={[styles.card, selectedId === item.id && styles.cardSelected]}
          onPress={() => pick(item)}
        >
          <Text style={styles.cardTitle}>{item.name}</Text>
          {item.description ? (
            <Text style={styles.cardDesc}>{item.description}</Text>
          ) : null}
        </Pressable>
      )}
      contentContainerStyle={styles.list}
      ListHeaderComponent={
        <Text style={styles.heading}>Pick a starting template</Text>
      }
    />
  );
}

const styles = StyleSheet.create({
  center: { flex: 1, alignItems: 'center', justifyContent: 'center', gap: spacing.md },
  list: { padding: spacing.lg, gap: spacing.sm },
  heading: { ...typography.header, marginBottom: spacing.md },
  card: {
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: radii.md,
    padding: spacing.lg,
    backgroundColor: colors.surface,
    gap: spacing.xs,
  },
  cardSelected: {
    borderColor: colors.primary,
    backgroundColor: colors.surfaceAlt,
  },
  cardTitle: { ...typography.title, color: colors.text },
  cardDesc: { ...typography.body, color: colors.textMuted },
  error: { color: colors.danger, textAlign: 'center' },
  retry: {
    backgroundColor: colors.primary,
    paddingHorizontal: spacing.lg,
    paddingVertical: spacing.sm,
    borderRadius: radii.pill,
  },
  retryText: { color: colors.textOnPrimary, fontWeight: '700' },
});
