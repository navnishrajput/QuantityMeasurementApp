package com.quantity.measurement.repository;

import com.quantity.measurement.entity.QuantityMeasurementEntity;
import java.util.List;

// UC15: Repository interface - abstracts data access behind ISP-compliant contract
public interface IQuantityMeasurementRepository {

    void save(QuantityMeasurementEntity entity);

    List<QuantityMeasurementEntity> findAll();

    List<QuantityMeasurementEntity> findByOperation(String operation);

    // UC15: Clear cache - used for testing reset between test runs
    void clear();

    // UC15: Future extension points - reserved for database integration
    // Optional<QuantityMeasurementEntity> findById(Long id);
    // void deleteById(Long id);
    // List<QuantityMeasurementEntity> findByDateRange(LocalDateTime start, LocalDateTime end);
}