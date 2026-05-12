package com.quantity.measurement.dto;

// UC15: Data Transfer Object - carries data between layers without domain dependencies
public class QuantityDTO {

    private final double value;
    private final String unit;
    private final String measurementType;
    private final boolean hasError;
    private final String errorMessage;

    // UC15: Success constructor - used for valid operation results
    public QuantityDTO(double value, String unit, String measurementType) {
        this.value = value;
        this.unit = unit;
        this.measurementType = measurementType;
        this.hasError = false;
        this.errorMessage = null;
    }

    // UC15: Error constructor - used when operation fails
    public QuantityDTO(String errorMessage) {
        this.value = 0.0;
        this.unit = null;
        this.measurementType = null;
        this.hasError = true;
        this.errorMessage = errorMessage;
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public String getMeasurementType() {
        return measurementType;
    }

    public boolean hasError() {
        return hasError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        if (hasError) {
            return "Error: " + errorMessage;
        }
        return "Quantity(" + value + ", " + unit + ")";
    }
}