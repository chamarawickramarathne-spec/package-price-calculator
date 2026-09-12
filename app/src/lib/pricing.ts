export interface PriceLine {
  equipmentId: number;
  name: string;
  value: number;
  quantity: number;
  lineTotal: number;
}

export interface PriceBreakdown {
  equipmentLines: PriceLine[];
  equipmentTotal: number;
  laborHours: number;
  hourlyRate: number;
  laborTotal: number;
  additionalCosts: number;
  subtotal: number;
  marginPercentage: number;
  marginAmount: number;
  finalTotal: number;
}

export interface PricingInput {
  catalog: Map<number, { name: string; value: number }>;
  quantities: Map<number, number>;
  laborHours: number;
  hourlyRate: number;
  additionalCosts: number;
  marginPercentage: number;
}

export function computePrice(input: PricingInput): PriceBreakdown {
  const equipmentLines: PriceLine[] = [];
  let equipmentTotal = 0;

  for (const [equipmentId, quantity] of input.quantities) {
    if (quantity <= 0) continue;
    const item = input.catalog.get(equipmentId);
    if (!item) continue;
    const lineTotal = item.value * quantity;
    equipmentTotal += lineTotal;
    equipmentLines.push({
      equipmentId,
      name: item.name,
      value: item.value,
      quantity,
      lineTotal,
    });
  }

  const laborTotal = input.laborHours * input.hourlyRate;
  const subtotal = equipmentTotal + laborTotal + input.additionalCosts;
  const marginAmount = (subtotal * input.marginPercentage) / 100;
  const finalTotal = subtotal + marginAmount;

  return {
    equipmentLines,
    equipmentTotal,
    laborHours: input.laborHours,
    hourlyRate: input.hourlyRate,
    laborTotal,
    additionalCosts: input.additionalCosts,
    subtotal,
    marginPercentage: input.marginPercentage,
    marginAmount,
    finalTotal,
  };
}

export function gearToCatalog(
  gear: Map<number, { name: string; value: number; quantity: number }>
): PricingInput {
  const catalog = new Map<number, { name: string; value: number }>();
  const quantities = new Map<number, number>();
  for (const [equipmentId, line] of gear) {
    if (line.quantity <= 0) continue;
    catalog.set(equipmentId, { name: line.name, value: line.value });
    quantities.set(equipmentId, line.quantity);
  }
  return {
    catalog,
    quantities,
    laborHours: 0,
    hourlyRate: 0,
    additionalCosts: 0,
    marginPercentage: 0,
  };
}