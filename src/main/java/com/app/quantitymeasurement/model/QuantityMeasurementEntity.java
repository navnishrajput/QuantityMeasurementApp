package com.app.quantitymeasurement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "quantity_measurement_entity",
        indexes = {
                @Index(name = "idx_operation", columnList = "operation"),
                @Index(name = "idx_measurement_type", columnList = "measurementType")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityMeasurementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operation", nullable = false, length = 50)
    private String operation;

    @Column(name = "measurement_type", nullable = false, length = 50)
    private String measurementType;

    @Column(name = "input_value1", nullable = false)
    private Double inputValue1;

    @Column(name = "input_unit1", nullable = false, length = 50)
    private String inputUnit1;

    @Column(name = "input_value2")
    private Double inputValue2;

    @Column(name = "input_unit2", length = 50)
    private String inputUnit2;

    @Column(name = "target_unit", length = 50)
    private String targetUnit;

    @Column(name = "result_value")
    private Double resultValue;

    @Column(name = "result_unit", length = 50)
    private String resultUnit;

    @Column(name = "is_error", nullable = false)
    private Boolean isError = false;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}