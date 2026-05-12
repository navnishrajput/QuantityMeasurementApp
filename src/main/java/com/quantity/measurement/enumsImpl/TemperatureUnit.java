package com.quantity.measurement.enumsImpl;

import com.quantity.measurement.enums.IMeasurable;
import java.util.function.Function;

public enum TemperatureUnit implements IMeasurable {

    CELSIUS(1.0,
            celsius -> celsius,
            celsius -> celsius),

    FAHRENHEIT(1.0,
            fahrenheit -> (fahrenheit - 32) * 5.0 / 9.0,
            celsius -> (celsius * 9.0 / 5.0) + 32),

    KELVIN(1.0,
            kelvin -> kelvin - 273.15,
            celsius -> celsius + 273.15);

    private final double conversionFactor;
    private final Function<Double, Double> toBaseUnit;
    private final Function<Double, Double> fromBaseUnit;

    // UC14: Temperature does not support arithmetic operations
    private static final SupportsArithmetic supportsArithmetic = () -> false;

    TemperatureUnit(double conversionFactor,
                    Function<Double, Double> toBaseUnit,
                    Function<Double, Double> fromBaseUnit) {
        this.conversionFactor = conversionFactor;
        this.toBaseUnit = toBaseUnit;
        this.fromBaseUnit = fromBaseUnit;
    }

    @Override
    public double getConversionFactor() {
        return conversionFactor;
    }

    @Override
    public double convertToBaseUnit(double value) {
        validate(value);
        return toBaseUnit.apply(value);
    }

    @Override
    public double convertFromBaseUnit(double value) {
        validate(value);
        return fromBaseUnit.apply(value);
    }

    @Override
    public boolean supportsArithmetic() {
        return false;
    }

    @Override
    public void validateOperationSupport(String operation) {
        throw new UnsupportedOperationException(
                "Temperature does not support " + operation +
                        " operation. Arithmetic operations are not meaningful for absolute temperatures."
        );
    }

    @Override
    public String getMeasurementType() {
        return "TEMPERATURE";
    }

    @Override
    public IMeasurable getUnitInstance(String name) {
        return TemperatureUnit.valueOf(name.toUpperCase());
    }

    private void validate(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Invalid value");
        }
    }
}