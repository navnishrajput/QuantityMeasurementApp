package com.app.quantitymeasurement.service;

import com.app.quantitymeasurement.dto.QuantityDTO;

public interface IQuantityMeasurementService {

    QuantityDTO compare(QuantityDTO dto1, QuantityDTO dto2);

    QuantityDTO convert(QuantityDTO dto, String targetUnit);

    QuantityDTO add(QuantityDTO dto1, QuantityDTO dto2);

    QuantityDTO add(QuantityDTO dto1, QuantityDTO dto2, String targetUnit);

    QuantityDTO subtract(QuantityDTO dto1, QuantityDTO dto2);

    QuantityDTO subtract(QuantityDTO dto1, QuantityDTO dto2, String targetUnit);

    QuantityDTO divide(QuantityDTO dto1, QuantityDTO dto2);

    // QuantityDTO multiply(QuantityDTO dto1, QuantityDTO dto2);
    // QuantityDTO multiply(QuantityDTO dto1, QuantityDTO dto2, String targetUnit);
}