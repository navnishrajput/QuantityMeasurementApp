package com.app.quantitymeasurement.model;

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