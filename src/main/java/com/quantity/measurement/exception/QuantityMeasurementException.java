package com.quantity.measurement.exception;

// UC15: Centralized exception for all quantity measurement errors
public class QuantityMeasurementException extends RuntimeException {

    public QuantityMeasurementException(String message) {
        super(message);
    }

    // UC15: Cause chaining constructor - reserved for wrapping lower-level exceptions
    // public QuantityMeasurementException(String message, Throwable cause) {
    //     super(message, cause);
    // }
}