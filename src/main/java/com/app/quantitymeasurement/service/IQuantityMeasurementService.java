package com.app.quantitymeasurement.service;

import com.app.quantitymeasurement.dto.QuantityDTO;
import com.app.quantitymeasurement.dto.QuantityMeasurementDTO;
import java.util.List;

public interface IQuantityMeasurementService {

    QuantityMeasurementDTO compare(QuantityDTO dto1, QuantityDTO dto2);

    QuantityMeasurementDTO convert(QuantityDTO dto, String targetUnit);

    QuantityMeasurementDTO add(QuantityDTO dto1, QuantityDTO dto2);

    QuantityMeasurementDTO add(QuantityDTO dto1, QuantityDTO dto2, String targetUnit);

    QuantityMeasurementDTO subtract(QuantityDTO dto1, QuantityDTO dto2);

    QuantityMeasurementDTO subtract(QuantityDTO dto1, QuantityDTO dto2, String targetUnit);

    QuantityMeasurementDTO divide(QuantityDTO dto1, QuantityDTO dto2);

    List<QuantityMeasurementDTO> getHistoryByOperation(String operation);

    List<QuantityMeasurementDTO> getHistoryByType(String measurementType);

    List<QuantityMeasurementDTO> getErrorHistory();

    long getCountByOperation(String operation);
}