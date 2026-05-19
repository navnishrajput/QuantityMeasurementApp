package com.app.quantitymeasurement.serviceImpl;

import com.app.quantitymeasurement.dto.QuantityDTO;
import com.app.quantitymeasurement.dto.QuantityMeasurementDTO;
import com.app.quantitymeasurement.enums.IMeasurable;
import com.app.quantitymeasurement.enumsImpl.LengthUnit;
import com.app.quantitymeasurement.enumsImpl.TemperatureUnit;
import com.app.quantitymeasurement.enumsImpl.VolumeUnit;
import com.app.quantitymeasurement.enumsImpl.WeightUnit;
import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.model.Quantity;
import com.app.quantitymeasurement.model.QuantityMeasurementEntity;
import com.app.quantitymeasurement.repository.QuantityMeasurementRepository;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuantityMeasurementServiceImpl.class);

    @Autowired
    private QuantityMeasurementRepository repository;

    @Override
    public QuantityMeasurementDTO compare(QuantityDTO dto1, QuantityDTO dto2) {
        LOGGER.debug("Comparing: {} with {}", dto1, dto2);
        try {
            Quantity q1 = createQuantity(dto1);
            Quantity q2 = createQuantity(dto2);

            if (!q1.getUnit().getClass().equals(q2.getUnit().getClass())) {
                throw new IllegalArgumentException("Different measurement types");
            }

            boolean result = q1.equals(q2);

            QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
            entity.setOperation("COMPARE");
            entity.setMeasurementType(dto1.getMeasurementType());
            entity.setInputValue1(dto1.getValue());
            entity.setInputUnit1(dto1.getUnit());
            entity.setInputValue2(dto2.getValue());
            entity.setInputUnit2(dto2.getUnit());
            entity.setResultValue(result ? 1.0 : 0.0);
            entity.setResultUnit(result ? "EQUAL" : "NOT_EQUAL");
            entity.setIsError(false);

            repository.save(entity);
            LOGGER.info("Compare result: {}", result ? "EQUAL" : "NOT_EQUAL");
            return QuantityMeasurementDTO.fromEntity(entity);

        } catch (Exception e) {
            LOGGER.error("Compare failed", e);
            return saveErrorEntity("COMPARE", dto1, dto2, null, e.getMessage());
        }
    }

    @Override
    public QuantityMeasurementDTO convert(QuantityDTO dto, String targetUnit) {
        LOGGER.debug("Converting: {} to {}", dto, targetUnit);
        try {
            Quantity q = createQuantity(dto);
            IMeasurable target = getUnit(dto.getMeasurementType(), targetUnit);
            Quantity result = q.toConvert(target);

            QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
            entity.setOperation("CONVERT");
            entity.setMeasurementType(dto.getMeasurementType());
            entity.setInputValue1(dto.getValue());
            entity.setInputUnit1(dto.getUnit());
            entity.setTargetUnit(targetUnit);
            entity.setResultValue(result.getValue());
            entity.setResultUnit(result.getUnit().toString());
            entity.setIsError(false);

            repository.save(entity);
            LOGGER.info("Convert result: {}", result);
            return QuantityMeasurementDTO.fromEntity(entity);

        } catch (Exception e) {
            LOGGER.error("Convert failed", e);
            return saveErrorEntity("CONVERT", dto, null, targetUnit, e.getMessage());
        }
    }

    @Override
    public QuantityMeasurementDTO add(QuantityDTO dto1, QuantityDTO dto2) {
        return addInternal(dto1, dto2, dto1.getUnit());
    }

    @Override
    public QuantityMeasurementDTO add(QuantityDTO dto1, QuantityDTO dto2, String targetUnit) {
        return addInternal(dto1, dto2, targetUnit);
    }

    private QuantityMeasurementDTO addInternal(QuantityDTO dto1, QuantityDTO dto2, String targetUnit) {
        LOGGER.debug("Adding: {} + {}", dto1, dto2);
        try {
            Quantity q1 = createQuantity(dto1);
            Quantity q2 = createQuantity(dto2);
            IMeasurable target = getUnit(dto1.getMeasurementType(), targetUnit);

            q1.getUnit().validateOperationSupport("ADD");

            if (!q1.getUnit().getClass().equals(q2.getUnit().getClass())) {
                throw new IllegalArgumentException("Different measurement types");
            }

            Quantity result = q1.add(q2, target);

            QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
            entity.setOperation("ADD");
            entity.setMeasurementType(dto1.getMeasurementType());
            entity.setInputValue1(dto1.getValue());
            entity.setInputUnit1(dto1.getUnit());
            entity.setInputValue2(dto2.getValue());
            entity.setInputUnit2(dto2.getUnit());
            entity.setTargetUnit(targetUnit);
            entity.setResultValue(result.getValue());
            entity.setResultUnit(result.getUnit().toString());
            entity.setIsError(false);

            repository.save(entity);
            LOGGER.info("Add result: {}", result);
            return QuantityMeasurementDTO.fromEntity(entity);

        } catch (Exception e) {
            LOGGER.error("Add failed", e);
            return saveErrorEntity("ADD", dto1, dto2, targetUnit, e.getMessage());
        }
    }

    @Override
    public QuantityMeasurementDTO subtract(QuantityDTO dto1, QuantityDTO dto2) {
        return subtractInternal(dto1, dto2, dto1.getUnit());
    }

    @Override
    public QuantityMeasurementDTO subtract(QuantityDTO dto1, QuantityDTO dto2, String targetUnit) {
        return subtractInternal(dto1, dto2, targetUnit);
    }

    private QuantityMeasurementDTO subtractInternal(QuantityDTO dto1, QuantityDTO dto2, String targetUnit) {
        LOGGER.debug("Subtracting: {} - {}", dto1, dto2);
        try {
            Quantity q1 = createQuantity(dto1);
            Quantity q2 = createQuantity(dto2);
            IMeasurable target = getUnit(dto1.getMeasurementType(), targetUnit);

            q1.getUnit().validateOperationSupport("SUBTRACT");

            if (!q1.getUnit().getClass().equals(q2.getUnit().getClass())) {
                throw new IllegalArgumentException("Different measurement types");
            }

            Quantity result = q1.subtract(q2, target);

            QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
            entity.setOperation("SUBTRACT");
            entity.setMeasurementType(dto1.getMeasurementType());
            entity.setInputValue1(dto1.getValue());
            entity.setInputUnit1(dto1.getUnit());
            entity.setInputValue2(dto2.getValue());
            entity.setInputUnit2(dto2.getUnit());
            entity.setTargetUnit(targetUnit);
            entity.setResultValue(result.getValue());
            entity.setResultUnit(result.getUnit().toString());
            entity.setIsError(false);

            repository.save(entity);
            LOGGER.info("Subtract result: {}", result);
            return QuantityMeasurementDTO.fromEntity(entity);

        } catch (Exception e) {
            LOGGER.error("Subtract failed", e);
            return saveErrorEntity("SUBTRACT", dto1, dto2, targetUnit, e.getMessage());
        }
    }

    @Override
    public QuantityMeasurementDTO divide(QuantityDTO dto1, QuantityDTO dto2) {
        LOGGER.debug("Dividing: {} / {}", dto1, dto2);
        try {
            Quantity q1 = createQuantity(dto1);
            Quantity q2 = createQuantity(dto2);

            q1.getUnit().validateOperationSupport("DIVIDE");

            if (!q1.getUnit().getClass().equals(q2.getUnit().getClass())) {
                throw new IllegalArgumentException("Different measurement types");
            }

            double result = q1.divide(q2);

            QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
            entity.setOperation("DIVIDE");
            entity.setMeasurementType(dto1.getMeasurementType());
            entity.setInputValue1(dto1.getValue());
            entity.setInputUnit1(dto1.getUnit());
            entity.setInputValue2(dto2.getValue());
            entity.setInputUnit2(dto2.getUnit());
            entity.setResultValue(result);
            entity.setResultUnit("SCALAR");
            entity.setIsError(false);

            repository.save(entity);
            LOGGER.info("Divide result: {}", result);
            return QuantityMeasurementDTO.fromEntity(entity);

        } catch (Exception e) {
            LOGGER.error("Divide failed", e);
            return saveErrorEntity("DIVIDE", dto1, dto2, null, e.getMessage());
        }
    }

    @Override
    public List<QuantityMeasurementDTO> getHistoryByOperation(String operation) {
        return QuantityMeasurementDTO.fromEntityList(repository.findByOperation(operation));
    }

    @Override
    public List<QuantityMeasurementDTO> getHistoryByType(String measurementType) {
        return QuantityMeasurementDTO.fromEntityList(repository.findByMeasurementType(measurementType));
    }

    @Override
    public List<QuantityMeasurementDTO> getErrorHistory() {
        return QuantityMeasurementDTO.fromEntityList(repository.findByIsErrorTrue());
    }

    @Override
    public long getCountByOperation(String operation) {
        return repository.countByOperationAndIsErrorFalse(operation);
    }

    private QuantityMeasurementDTO saveErrorEntity(String operation, QuantityDTO dto1,
                                                   QuantityDTO dto2, String targetUnit,
                                                   String errorMessage) {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        entity.setOperation(operation);
        entity.setMeasurementType(dto1 != null ? dto1.getMeasurementType() : null);
        entity.setInputValue1(dto1 != null ? dto1.getValue() : null);
        entity.setInputUnit1(dto1 != null ? dto1.getUnit() : null);
        entity.setInputValue2(dto2 != null ? dto2.getValue() : null);
        entity.setInputUnit2(dto2 != null ? dto2.getUnit() : null);
        entity.setTargetUnit(targetUnit);
        entity.setIsError(true);
        entity.setErrorMessage(errorMessage);

        repository.save(entity);
        return QuantityMeasurementDTO.fromEntity(entity);
    }

    private Quantity createQuantity(QuantityDTO dto) {
        IMeasurable unit = getUnit(dto.getMeasurementType(), dto.getUnit());
        return new Quantity(dto.getValue(), unit);
    }

    private IMeasurable getUnit(String measurementType, String unitName) {
        switch (measurementType.toUpperCase()) {
            case "LENGTH": return LengthUnit.valueOf(unitName.toUpperCase());
            case "WEIGHT": return WeightUnit.valueOf(unitName.toUpperCase());
            case "VOLUME": return VolumeUnit.valueOf(unitName.toUpperCase());
            case "TEMPERATURE": return TemperatureUnit.valueOf(unitName.toUpperCase());
            default: throw new QuantityMeasurementException("Unknown measurement type: " + measurementType);
        }
    }
}