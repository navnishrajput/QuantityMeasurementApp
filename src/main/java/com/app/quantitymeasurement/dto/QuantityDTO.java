package com.app.quantitymeasurement.dto;

public class QuantityDTO {

    private final double value;
    private final String unit;
    private final String measurementType;
    private final boolean hasError;
    private final String errorMessage;

    public QuantityDTO(double value, String unit, String measurementType) {
        this.value = value;
        this.unit = unit;
        this.measurementType = measurementType;
        this.hasError = false;
        this.errorMessage = null;
    }

    public QuantityDTO(String errorMessage) {
        this.value = 0.0;
        this.unit = null;
        this.measurementType = null;
        this.hasError = true;
        this.errorMessage = errorMessage;
    }

    public double getValue() { return value; }

    public String getUnit() { return unit; }

    public String getMeasurementType() { return measurementType; }

    public boolean hasError() { return hasError; }

    public String getErrorMessage() { return errorMessage; }

    @Override
    public String toString() {
        if (hasError) {
            return "Error: " + errorMessage;
        }
        return "Quantity(" + value + ", " + unit + ")";
    }
}