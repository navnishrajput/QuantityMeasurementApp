package com.app.quantitymeasurement.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityDTO {

    @NotNull(message = "Value cannot be null")
    private Double value;

    @NotEmpty(message = "Unit cannot be empty")
    private String unit;

    @NotEmpty(message = "Measurement type cannot be empty")
    @Pattern(regexp = "^(LENGTH|WEIGHT|VOLUME|TEMPERATURE)$",
            message = "Measurement type must be LENGTH, WEIGHT, VOLUME, or TEMPERATURE")
    private String measurementType;

    private boolean hasError;
    private String errorMessage;

    public QuantityDTO(Double value, String unit, String measurementType) {
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
}