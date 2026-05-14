package com.app.quantitymeasurement.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class QuantityMeasurementEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private final String operation;
    private final String measurementType;
    private final double inputValue1;
    private final String inputUnit1;
    private final double inputValue2;
    private final String inputUnit2;
    private final String targetUnit;
    private final double resultValue;
    private final String resultUnit;
    private final boolean hasError;
    private final String errorMessage;
    private final LocalDateTime timestamp;

    private QuantityMeasurementEntity(Builder builder) {
        this.operation = builder.operation;
        this.measurementType = builder.measurementType;
        this.inputValue1 = builder.inputValue1;
        this.inputUnit1 = builder.inputUnit1;
        this.inputValue2 = builder.inputValue2;
        this.inputUnit2 = builder.inputUnit2;
        this.targetUnit = builder.targetUnit;
        this.resultValue = builder.resultValue;
        this.resultUnit = builder.resultUnit;
        this.hasError = builder.hasError;
        this.errorMessage = builder.errorMessage;
        this.timestamp = LocalDateTime.now();
    }

    public QuantityMeasurementEntity(Long id, String operation, String measurementType,
                                     double inputValue1, String inputUnit1,
                                     double inputValue2, String inputUnit2,
                                     String targetUnit, double resultValue,
                                     String resultUnit, boolean hasError,
                                     String errorMessage, LocalDateTime timestamp) {
        this.id = id;
        this.operation = operation;
        this.measurementType = measurementType;
        this.inputValue1 = inputValue1;
        this.inputUnit1 = inputUnit1;
        this.inputValue2 = inputValue2;
        this.inputUnit2 = inputUnit2;
        this.targetUnit = targetUnit;
        this.resultValue = resultValue;
        this.resultUnit = resultUnit;
        this.hasError = hasError;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public String getOperation() { return operation; }
    public String getMeasurementType() { return measurementType; }
    public double getInputValue1() { return inputValue1; }
    public String getInputUnit1() { return inputUnit1; }
    public double getInputValue2() { return inputValue2; }
    public String getInputUnit2() { return inputUnit2; }
    public String getTargetUnit() { return targetUnit; }
    public double getResultValue() { return resultValue; }
    public String getResultUnit() { return resultUnit; }
    public boolean hasError() { return hasError; }
    public String getErrorMessage() { return errorMessage; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        if (hasError) {
            return "Entity[" + id + "][ERROR] " + operation + ": " + errorMessage;
        }
        return "Entity[" + id + "] " + operation + ": " + inputValue1 + " " + inputUnit1 +
                (inputUnit2 != null ? " and " + inputValue2 + " " + inputUnit2 : "") +
                " = " + resultValue + " " + (resultUnit != null ? resultUnit : "");
    }

    public static class Builder {
        private String operation;
        private String measurementType;
        private double inputValue1;
        private String inputUnit1;
        private double inputValue2;
        private String inputUnit2;
        private String targetUnit;
        private double resultValue;
        private String resultUnit;
        private boolean hasError;
        private String errorMessage;

        public Builder operation(String operation) { this.operation = operation; return this; }
        public Builder measurementType(String measurementType) { this.measurementType = measurementType; return this; }
        public Builder inputValue1(double inputValue1) { this.inputValue1 = inputValue1; return this; }
        public Builder inputUnit1(String inputUnit1) { this.inputUnit1 = inputUnit1; return this; }
        public Builder inputValue2(double inputValue2) { this.inputValue2 = inputValue2; return this; }
        public Builder inputUnit2(String inputUnit2) { this.inputUnit2 = inputUnit2; return this; }
        public Builder targetUnit(String targetUnit) { this.targetUnit = targetUnit; return this; }
        public Builder resultValue(double resultValue) { this.resultValue = resultValue; return this; }
        public Builder resultUnit(String resultUnit) { this.resultUnit = resultUnit; return this; }
        public Builder hasError(boolean hasError) { this.hasError = hasError; return this; }
        public Builder errorMessage(String errorMessage) { this.errorMessage = errorMessage; return this; }

        public QuantityMeasurementEntity build() {
            return new QuantityMeasurementEntity(this);
        }
    }
}