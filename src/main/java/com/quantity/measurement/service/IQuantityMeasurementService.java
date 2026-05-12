package com.quantity.measurement.service;

import com.quantity.measurement.dto.QuantityDTO;

// UC15: Service interface - defines business operations contract
public interface IQuantityMeasurementService {

    QuantityDTO compare(QuantityDTO dto1, QuantityDTO dto2);

    QuantityDTO convert(QuantityDTO dto, String targetUnit);

    QuantityDTO add(QuantityDTO dto1, QuantityDTO dto2);

    QuantityDTO add(QuantityDTO dto1, QuantityDTO dto2, String targetUnit);

    QuantityDTO subtract(QuantityDTO dto1, QuantityDTO dto2);

    QuantityDTO subtract(QuantityDTO dto1, QuantityDTO dto2, String targetUnit);

    QuantityDTO divide(QuantityDTO dto1, QuantityDTO dto2);

    // UC15: Future extension points - reserved for additional operations
    // QuantityDTO multiply(QuantityDTO dto1, QuantityDTO dto2);
    // QuantityDTO multiply(QuantityDTO dto1, QuantityDTO dto2, String targetUnit);
}