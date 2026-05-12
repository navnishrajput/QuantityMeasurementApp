package com.quantity.measurement.enums;

public interface IMeasurable {

    double getConversionFactor();

    double convertToBaseUnit(double value);

    double convertFromBaseUnit(double value);

    // UC15: Added for N-Tier architecture - used by service layer for DTO mapping
    String getMeasurementType();

    // UC15: Added for N-Tier architecture - used by service layer for unit resolution
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
        // Default no-op, overridden by TemperatureUnit
    }
}