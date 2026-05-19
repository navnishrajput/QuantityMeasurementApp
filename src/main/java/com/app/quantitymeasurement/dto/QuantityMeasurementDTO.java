package com.app.quantitymeasurement.dto;

import com.app.quantitymeasurement.model.QuantityMeasurementEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuantityMeasurementDTO {

    private Long id;
    private String operation;
    private String measurementType;
    private Double inputValue1;
    private String inputUnit1;
    private Double inputValue2;
    private String inputUnit2;
    private String targetUnit;
    private Double resultValue;
    private String resultUnit;
    private Boolean isError;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static QuantityMeasurementDTO fromEntity(QuantityMeasurementEntity entity) {
        return QuantityMeasurementDTO.builder()
                .id(entity.getId())
                .operation(entity.getOperation())
                .measurementType(entity.getMeasurementType())
                .inputValue1(entity.getInputValue1())
                .inputUnit1(entity.getInputUnit1())
                .inputValue2(entity.getInputValue2())
                .inputUnit2(entity.getInputUnit2())
                .targetUnit(entity.getTargetUnit())
                .resultValue(entity.getResultValue())
                .resultUnit(entity.getResultUnit())
                .isError(entity.getIsError())
                .errorMessage(entity.getErrorMessage())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public QuantityMeasurementEntity toEntity() {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        entity.setOperation(this.operation);
        entity.setMeasurementType(this.measurementType);
        entity.setInputValue1(this.inputValue1);
        entity.setInputUnit1(this.inputUnit1);
        entity.setInputValue2(this.inputValue2);
        entity.setInputUnit2(this.inputUnit2);
        entity.setTargetUnit(this.targetUnit);
        entity.setResultValue(this.resultValue);
        entity.setResultUnit(this.resultUnit);
        entity.setIsError(this.isError != null ? this.isError : false);
        entity.setErrorMessage(this.errorMessage);
        return entity;
    }

    public static List<QuantityMeasurementDTO> fromEntityList(List<QuantityMeasurementEntity> entities) {
        return entities.stream()
                .map(QuantityMeasurementDTO::fromEntity)
                .collect(Collectors.toList());
    }
}