package com.qunantity.measurement.model;

import com.qunantity.measurement.enums.LengthUnit;

public class QuantityLength {

    private final double value;
    private final LengthUnit unit;
    private final double EPSILON = 1e-6;

    public QuantityLength(double value, LengthUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("unit can not null ");
        }
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Invalid numeric value");
        }
        this.value = value;
        this.unit = unit;
    }

    // Delegate conversion to the Unit class
    public double toFeet() {
        return unit.convertToBaseUnit(value);
    }

    // Delegate conversion logic to the Unit class (UC8 Requirement)
    public double toConvert(LengthUnit targetUnit) {
        return convert(this.value, this.unit, targetUnit);
    }

    public static double convert(double value, LengthUnit sourceUnit, LengthUnit targetUnit) {
        if (sourceUnit == null || targetUnit == null) {
            throw new IllegalArgumentException("Source/Target unit cannot be null");
        }
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Invaild numric value!!");
        }

        // Logic moved to LengthUnit
        double valueInFeet = sourceUnit.convertToBaseUnit(value);
        return targetUnit.convertFromBaseUnit(valueInFeet);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof QuantityLength)) return false;

        QuantityLength other = (QuantityLength) obj;

        double thisInFeet = this.toFeet();
        double otherInFeet = other.toFeet();

        return Math.abs(thisInFeet - otherInFeet) < EPSILON;
    }

    // UC8: Standard addition with target unit
    public QuantityLength add(QuantityLength other, LengthUnit targetUnit) {
        if (other == null || targetUnit == null) {
            throw new IllegalArgumentException("Second quantity and targetUnit must not be null");
        }
        if (!Double.isFinite(other.value)) {
            throw new IllegalArgumentException("Invalid numeric value");
        }

        // Delegate math to the Units
        double sumInFeet = this.toFeet() + other.toFeet();
        double resultValue = targetUnit.convertFromBaseUnit(sumInFeet);

        return new QuantityLength(resultValue, targetUnit);
    }

    // Convenience method using first operand unit as target
    public QuantityLength add(QuantityLength other) {
        return add(other, this.unit);
    }

    public double getValue() {
        return value;
    }

    public LengthUnit getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return value + " " + unit.name();
    }
}