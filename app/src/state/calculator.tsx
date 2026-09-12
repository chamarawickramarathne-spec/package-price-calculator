import React, {
  createContext,
  useContext,
  useMemo,
  useReducer,
} from 'react';
import type { PackageTemplate } from '../types';
import { computePrice, type PriceBreakdown } from '../lib/pricing';

export interface CartLine {
  equipmentId: number;
  name: string;
  value: number;
  quantity: number;
}

export interface CalculatorState {
  template: PackageTemplate | null;
  gear: Map<number, CartLine>;
  laborHours: number;
  hourlyRate: number;
  additionalCosts: number;
  marginPercentage: number;
}

type Action =
  | { type: 'LOAD_TEMPLATE'; template: PackageTemplate | null }
  | { type: 'SET_GEAR'; gear: Map<number, CartLine> }
  | { type: 'ADD_GEAR_LINE'; line: CartLine }
  | { type: 'SET_LINE_QUANTITY'; equipmentId: number; quantity: number }
  | { type: 'REMOVE_GEAR'; equipmentId: number }
  | { type: 'SET_LABOR_HOURS'; hours: number }
  | { type: 'SET_HOURLY_RATE'; rate: number }
  | { type: 'SET_ADDITIONAL_COSTS'; costs: number }
  | { type: 'SET_MARGIN'; percentage: number }
  | { type: 'RESET' };

const initialState: CalculatorState = {
  template: null,
  gear: new Map(),
  laborHours: 0,
  hourlyRate: 0,
  additionalCosts: 0,
  marginPercentage: 0,
};

function reduce(state: CalculatorState, action: Action): CalculatorState {
  switch (action.type) {
    case 'LOAD_TEMPLATE':
      return { ...state, template: action.template };
    case 'SET_GEAR':
      return { ...state, gear: action.gear };
    case 'ADD_GEAR_LINE': {
      const gear = new Map(state.gear);
      gear.set(action.line.equipmentId, action.line);
      return { ...state, gear };
    }
    case 'SET_LINE_QUANTITY': {
      const gear = new Map(state.gear);
      const existing = gear.get(action.equipmentId);
      if (!existing) return state;
      if (action.quantity <= 0) {
        gear.delete(action.equipmentId);
      } else {
        gear.set(action.equipmentId, { ...existing, quantity: action.quantity });
      }
      return { ...state, gear };
    }
    case 'REMOVE_GEAR': {
      const gear = new Map(state.gear);
      gear.delete(action.equipmentId);
      return { ...state, gear };
    }
    case 'SET_LABOR_HOURS':
      return { ...state, laborHours: action.hours };
    case 'SET_HOURLY_RATE':
      return { ...state, hourlyRate: action.rate };
    case 'SET_ADDITIONAL_COSTS':
      return { ...state, additionalCosts: action.costs };
    case 'SET_MARGIN':
      return { ...state, marginPercentage: action.percentage };
    case 'RESET':
      return initialState;
    default:
      return state;
  }
}

export interface CalculatorValue {
  state: CalculatorState;
  dispatch: React.Dispatch<Action>;
  activeTemplateId: number | null;
  canGoNext: boolean;
  breakdown: PriceBreakdown;
}

const CalculatorContext = createContext<CalculatorValue | null>(null);

export function CalculatorProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const [state, dispatch] = useReducer(reduce, initialState);

  const activeTemplateId = state.template ? state.template.id : null   ;
  const canGoNext = state.gear.size > 0;

  const breakdown = useMemo(
    () =>
      computePrice({
        catalog: new Map(
          [...state.gear.values()].map((line) => [
            line.equipmentId,
            { name: line.name, value: line.value },
          ])
        ),
        quantities: new Map(
          [...state.gear.values()].map((line) => [
            line.equipmentId,
            line.quantity,
          ])
        ),
        laborHours: state.laborHours,
        hourlyRate: state.hourlyRate,
        additionalCosts: state.additionalCosts,
        marginPercentage: state.marginPercentage,
      }),
    [state]
  );

  const value = useMemo(
    () => ({ state, dispatch, activeTemplateId, canGoNext, breakdown }),
    [state, activeTemplateId, canGoNext, breakdown]
  );

  return (
    <CalculatorContext.Provider value={value}>
      {children}
    </CalculatorContext.Provider>
  );
}

export function useCalculator(): CalculatorValue {
  const ctx = useContext(CalculatorContext);
  if (!ctx) {
    throw new Error('useCalculator must be used within CalculatorProvider');
  }
  return ctx;
}
