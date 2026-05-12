package com.quantity.measurement.model;

// UC15: Generic internal model used within service layer for type-safe operations
// Currently not actively used - service layer uses Quantity directly with raw types
// Kept for future type-safe refactoring
public class QuantityModel<U> {

    private final double value;
    private final U unit;

    public QuantityModel(double value, U unit) {
        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public U getUnit() {
        return unit;
    }
}