package com.quantity.measurement.model;

import com.quantity.measurement.enums.IMeasurable;
import java.util.Objects;
import java.util.function.DoubleBinaryOperator;

public class Quantity<U extends IMeasurable> {

    private static final double EPSILON = 1e-6;
    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
        if (unit == null)
            throw new NullPointerException("Unit shouldn't be null");
        if (Double.isNaN(value))
            throw new IllegalArgumentException("Invalid value");

        this.value = value;
        this.unit = unit;
    }

    // UC15: Modified to accept IMeasurable for N-Tier compatibility
    @SuppressWarnings("unchecked")
    public Quantity<U> toConvert(IMeasurable targetUnit) {
        if (targetUnit == null)
            throw new NullPointerException("Target unit cannot be null");

        double baseValue = unit.convertToBaseUnit(value);
        double converted = targetUnit.convertFromBaseUnit(baseValue);

        return new Quantity<>(converted, (U) targetUnit);
    }

    // UC13: Centralized arithmetic operation enum with lambda dispatch
    private enum ArithmeticOperation {
        ADD((a, b) -> a + b),

        SUBTRACT((a, b) -> a - b),

        DIVIDE((a, b) -> {
            if (Math.abs(b) < EPSILON) {
                throw new ArithmeticException("Division by zero");
            }
            return a / b;
        });

        // UC15: MULTIPLY - reserved for future use, not currently exposed in API
        // MULTIPLY((a, b) -> a * b);

        private final DoubleBinaryOperator op;

        ArithmeticOperation(DoubleBinaryOperator op) {
            this.op = op;
        }

        double apply(double a, double b) {
            return op.applyAsDouble(a, b);
        }
    }

    public double getValue() {
        return value;
    }

    public U getUnit() {
        return unit;
    }

    // UC13: Centralized validation - shared by add, subtract, divide
    @SuppressWarnings("unchecked")
    private void validate(Quantity<?> other, IMeasurable target, boolean requireTarget) {
        if (other == null) {
            throw new NullPointerException("Quantity must not be null");
        }

        if (requireTarget && target == null) {
            throw new NullPointerException("Target unit must not be null");
        }

        if (!this.unit.getClass().equals(other.unit.getClass())) {
            throw new IllegalArgumentException("Different measurement types");
        }
    }

    // UC13: Converts value to base unit using unit's conversion factor
    private double base(U unit, double value) {
        return unit.convertToBaseUnit(value);
    }

    // UC13: Performs arithmetic operation on base unit values
    private double operate(Quantity<U> other, ArithmeticOperation op) {
        double a = base(this.unit, this.value);
        double b = base(other.unit, other.value);
        return op.apply(a, b);
    }

    // UC13: Rounding helper - currently commented, rounding done by caller if needed
    // private double round(double value) {
    //     return Math.round(value * 1000000.0) / 1000000.0;
    // }

    @SuppressWarnings("unchecked")
    public Quantity<U> add(Quantity<?> other) {
        return add(other, this.unit);
    }

    @SuppressWarnings("unchecked")
    public Quantity<U> add(Quantity<?> other, IMeasurable targetUnit) {
        this.unit.validateOperationSupport("ADD");
        validate(other, targetUnit, true);

        Quantity<U> typedOther = (Quantity<U>) other;
        double resultBase = operate(typedOther, ArithmeticOperation.ADD);
        double converted = targetUnit.convertFromBaseUnit(resultBase);

        return new Quantity<>(converted, (U) targetUnit);
    }

    @SuppressWarnings("unchecked")
    public Quantity<U> subtract(Quantity<?> other) {
        return subtract(other, this.unit);
    }

    @SuppressWarnings("unchecked")
    public Quantity<U> subtract(Quantity<?> other, IMeasurable targetUnit) {
        this.unit.validateOperationSupport("SUBTRACT");
        validate(other, targetUnit, true);

        Quantity<U> typedOther = (Quantity<U>) other;
        double resultBase = operate(typedOther, ArithmeticOperation.SUBTRACT);
        double converted = targetUnit.convertFromBaseUnit(resultBase);

        return new Quantity<>(converted, (U) targetUnit);
    }

    @SuppressWarnings("unchecked")
    public double divide(Quantity<?> other) {
        this.unit.validateOperationSupport("DIVIDE");
        validate(other, null, false);

        Quantity<U> typedOther = (Quantity<U>) other;
        return operate(typedOther, ArithmeticOperation.DIVIDE);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Quantity<?> other = (Quantity<?>) obj;

        // UC10: Cross-category prevention via unit class comparison
        if (!this.unit.getClass().equals(other.unit.getClass())) {
            return false;
        }

        double a = this.unit.convertToBaseUnit(this.value);
        double b = other.unit.convertToBaseUnit(other.value);

        return Math.abs(a - b) < EPSILON;
    }

    @Override
    public int hashCode() {
        double base = unit.convertToBaseUnit(value);
        return Objects.hash(Math.round(base / EPSILON));
    }
}