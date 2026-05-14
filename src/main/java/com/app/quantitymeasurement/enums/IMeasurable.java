package com.app.quantitymeasurement.enums;

public interface IMeasurable {

    double getConversionFactor();

    double convertToBaseUnit(double value);

    double convertFromBaseUnit(double value);

    String getMeasurementType();

    IMeasurable getUnitInstance(String name);

    @FunctionalInterface
    interface SupportsArithmetic {
        boolean isSupported();
    }

    SupportsArithmetic supportsArithmetic = () -> true;

    default boolean supportsArithmetic() {
        return supportsArithmetic.isSupported();
    }

    default void validateOperationSupport(String operation) {
    }
}