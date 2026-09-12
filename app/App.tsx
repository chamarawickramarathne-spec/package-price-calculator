import React, { useState, useCallback } from 'react';
import { StatusBar } from 'expo-status-bar';
import { SafeAreaView, StyleSheet, View } from 'react-native';
import { CalculatorProvider, useCalculator } from './src/state/calculator';
import { StepIndicator } from './src/components/StepIndicator';
import { TemplatesScreen } from './src/screens/TemplatesScreen';
import { GearScreen } from './src/screens/GearScreen';
import { LaborScreen } from './src/screens/LaborScreen';
import { FinalScreen } from './src/screens/FinalScreen';
import { colors, spacing } from './src/theme';

export default function App() {
  return (
    <CalculatorProvider>
      <StatusBar style="light" />
      <SafeAreaView style={styles.safe}>
        <View style={styles.app}>
          <AppShell />
        </View>
      </SafeAreaView>
    </CalculatorProvider>
  );
}

function AppShell() {
  const [step, setStep] = useState(0);
  const { activeTemplateId } = useCalculator();
  const goNext = useCallback(() => setStep((s) => Math.min(s + 1, 3)), []);
  const goBack = useCallback(() => setStep((s) => Math.max(s - 1, 0)), []);
  const reset = useCallback(() => setStep(0), []);

  return (
    <View style={styles.shell}>
      <StepIndicator current={step} templateResolved={activeTemplateId !== null} />
      <View style={styles.screen}>
        {step === 0 && <TemplatesScreen onContinue={goNext} />}
        {step === 1 && <GearScreen onBack={goBack} onContinue={goNext} />}
        {step === 2 && <LaborScreen onBack={goBack} onContinue={goNext} />}
        {step === 3 && <FinalScreen onBack={goBack} onReset={reset} />}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  safe: { flex: 1, backgroundColor: colors.background },
  app: { flex: 1, maxWidth: 768, width: '100%', alignSelf: 'center' },
  shell: { flex: 1 },
  screen: { flex: 1, padding: spacing.lg },
});
