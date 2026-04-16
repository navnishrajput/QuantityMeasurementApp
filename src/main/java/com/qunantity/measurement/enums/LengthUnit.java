package com.qunantity.measurement.enums;

public enum LengthUnit {
    FEET(1.0),
    INCH(1.0 / 12.0),
    YARD(3.0),
    CM(0.0328084);

    private final double toFeetFactor;

    LengthUnit(double toFeetFactor) {
        this.toFeetFactor = toFeetFactor;
    }
    public double getConversionFactor() {
        return this.toFeetFactor;
    }
    // UC8: Responsibility to convert value to feet
    public double convertToBaseUnit(double value) {
        return value * toFeetFactor;
    }

    // UC8: Responsibility to convert feet back to this unit
    public double convertFromBaseUnit(double feetValue) {
        return feetValue / toFeetFactor;
    }

    // Keeping your original method names for logic consistency
    public double toFeet(double value) {
        return convertToBaseUnit(value);
    }

    public double fromFeet(double value) {
        return convertFromBaseUnit(value);
    }
}