package com.app.quantitymeasurement.serviceImpl;

import com.app.quantitymeasurement.dto.QuantityDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.enums.IMeasurable;
import com.app.quantitymeasurement.enumsImpl.LengthUnit;
import com.app.quantitymeasurement.enumsImpl.TemperatureUnit;
import com.app.quantitymeasurement.enumsImpl.VolumeUnit;
import com.app.quantitymeasurement.enumsImpl.WeightUnit;
import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.model.Quantity;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"rawtypes", "unchecked"})
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            QuantityMeasurementServiceImpl.class);
    private final IQuantityMeasurementRepository repository;

    public QuantityMeasurementServiceImpl(IQuantityMeasurementRepository repository) {
        this.repository = repository;
    }

    @Override
    public QuantityDTO compare(QuantityDTO dto1, QuantityDTO dto2) {
        LOGGER.debug("Comparing: {} with {}", dto1, dto2);
        try {
            Quantity q1 = createQuantity(dto1);
            Quantity q2 = createQuantity(dto2);

            if (!q1.getUnit().getClass().equals(q2.getUnit().getClass())) {
                throw new IllegalArgumentException("Different measurement types");
            }

            boolean result = q1.equals(q2);

            QuantityMeasurementEntity entity = new QuantityMeasurementEntity.Builder()
                    .operation("COMPARE")
                    .measurementType(dto1.getMeasurementType())
                    .inputValue1(dto1.getValue())
                    .inputUnit1(dto1.getUnit())
                    .inputValue2(dto2.getValue())
                    .inputUnit2(dto2.getUnit())
                    .resultValue(result ? 1.0 : 0.0)
                    .resultUnit(result ? "EQUAL" : "NOT_EQUAL")
                    .hasError(false)
                    .build();

            repository.save(entity);

            LOGGER.info("Compare result: {}", result ? "EQUAL" : "NOT_EQUAL");
            return new QuantityDTO(result ? 1.0 : 0.0,
                    result ? "EQUAL" : "NOT_EQUAL", "COMPARISON");

        } catch (Exception e) {
            LOGGER.error("Compare failed", e);
            QuantityMeasurementEntity errorEntity = new QuantityMeasurementEntity.Builder()
                    .operation("COMPARE")
                    .measurementType(dto1.getMeasurementType())
                    .inputValue1(dto1.getValue())
                    .inputUnit1(dto1.getUnit())
                    .inputValue2(dto2.getValue())
                    .inputUnit2(dto2.getUnit())
                    .hasError(true)
                    .errorMessage(e.getMessage())
                    .build();

            repository.save(errorEntity);
            return new QuantityDTO(e.getMessage());
        }
    }

    @Override
    public QuantityDTO convert(QuantityDTO dto, String targetUnit) {
        LOGGER.debug("Converting: {} to {}", dto, targetUnit);
        try {
            Quantity q = createQuantity(dto);
            IMeasurable target = getUnit(dto.getMeasurementType(), targetUnit);

            Quantity result = q.toConvert(target);

            QuantityMeasurementEntity entity = new QuantityMeasurementEntity.Builder()
                    .operation("CONVERT")
                    .measurementType(dto.getMeasurementType())
                    .inputValue1(dto.getValue())
                    .inputUnit1(dto.getUnit())
                    .targetUnit(targetUnit)
                    .resultValue(result.getValue())
                    .resultUnit(result.getUnit().toString())
                    .hasError(false)
                    .build();

            repository.save(entity);

            LOGGER.info("Convert result: {} -> {}", dto, result);
            return new QuantityDTO(result.getValue(),
                    result.getUnit().toString(), dto.getMeasurementType());

        } catch (Exception e) {
            LOGGER.error("Convert failed", e);
            QuantityMeasurementEntity errorEntity = new QuantityMeasurementEntity.Builder()
                    .operation("CONVERT")
                    .measurementType(dto.getMeasurementType())
                    .inputValue1(dto.getValue())
                    .inputUnit1(dto.getUnit())
                    .targetUnit(targetUnit)
                    .hasError(true)
                    .errorMessage(e.getMessage())
                    .build();

            repository.save(errorEntity);
            return new QuantityDTO(e.getMessage());
        }
    }

    @Override
    public QuantityDTO add(QuantityDTO dto1, QuantityDTO dto2) {
        return addInternal(dto1, dto2, dto1.getUnit());
    }

    @Override
    public QuantityDTO add(QuantityDTO dto1, QuantityDTO dto2, String targetUnit) {
        return addInternal(dto1, dto2, targetUnit);
    }

    private QuantityDTO addInternal(QuantityDTO dto1, QuantityDTO dto2, String targetUnit) {
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

            QuantityMeasurementEntity entity = new QuantityMeasurementEntity.Builder()
                    .operation("ADD")
                    .measurementType(dto1.getMeasurementType())
                    .inputValue1(dto1.getValue())
                    .inputUnit1(dto1.getUnit())
                    .inputValue2(dto2.getValue())
                    .inputUnit2(dto2.getUnit())
                    .targetUnit(targetUnit)
                    .resultValue(result.getValue())
                    .resultUnit(result.getUnit().toString())
                    .hasError(false)
                    .build();

            repository.save(entity);

            LOGGER.info("Add result: {}", result);
            return new QuantityDTO(result.getValue(),
                    result.getUnit().toString(), dto1.getMeasurementType());

        } catch (Exception e) {
            LOGGER.error("Add failed", e);
            QuantityMeasurementEntity errorEntity = new QuantityMeasurementEntity.Builder()
                    .operation("ADD")
                    .measurementType(dto1.getMeasurementType())
                    .inputValue1(dto1.getValue())
                    .inputUnit1(dto1.getUnit())
                    .inputValue2(dto2.getValue())
                    .inputUnit2(dto2.getUnit())
                    .targetUnit(targetUnit)
                    .hasError(true)
                    .errorMessage(e.getMessage())
                    .build();

            repository.save(errorEntity);
            return new QuantityDTO(e.getMessage());
        }
    }

    @Override
    public QuantityDTO subtract(QuantityDTO dto1, QuantityDTO dto2) {
        return subtractInternal(dto1, dto2, dto1.getUnit());
    }

    @Override
    public QuantityDTO subtract(QuantityDTO dto1, QuantityDTO dto2, String targetUnit) {
        return subtractInternal(dto1, dto2, targetUnit);
    }

    private QuantityDTO subtractInternal(QuantityDTO dto1, QuantityDTO dto2, String targetUnit) {
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

            QuantityMeasurementEntity entity = new QuantityMeasurementEntity.Builder()
                    .operation("SUBTRACT")
                    .measurementType(dto1.getMeasurementType())
                    .inputValue1(dto1.getValue())
                    .inputUnit1(dto1.getUnit())
                    .inputValue2(dto2.getValue())
                    .inputUnit2(dto2.getUnit())
                    .targetUnit(targetUnit)
                    .resultValue(result.getValue())
                    .resultUnit(result.getUnit().toString())
                    .hasError(false)
                    .build();

            repository.save(entity);

            LOGGER.info("Subtract result: {}", result);
            return new QuantityDTO(result.getValue(),
                    result.getUnit().toString(), dto1.getMeasurementType());

        } catch (Exception e) {
            LOGGER.error("Subtract failed", e);
            QuantityMeasurementEntity errorEntity = new QuantityMeasurementEntity.Builder()
                    .operation("SUBTRACT")
                    .measurementType(dto1.getMeasurementType())
                    .inputValue1(dto1.getValue())
                    .inputUnit1(dto1.getUnit())
                    .inputValue2(dto2.getValue())
                    .inputUnit2(dto2.getUnit())
                    .targetUnit(targetUnit)
                    .hasError(true)
                    .errorMessage(e.getMessage())
                    .build();

            repository.save(errorEntity);
            return new QuantityDTO(e.getMessage());
        }
    }

    @Override
    public QuantityDTO divide(QuantityDTO dto1, QuantityDTO dto2) {
        LOGGER.debug("Dividing: {} / {}", dto1, dto2);
        try {
            Quantity q1 = createQuantity(dto1);
            Quantity q2 = createQuantity(dto2);

            q1.getUnit().validateOperationSupport("DIVIDE");

            if (!q1.getUnit().getClass().equals(q2.getUnit().getClass())) {
                throw new IllegalArgumentException("Different measurement types");
            }

            double result = q1.divide(q2);

            QuantityMeasurementEntity entity = new QuantityMeasurementEntity.Builder()
                    .operation("DIVIDE")
                    .measurementType(dto1.getMeasurementType())
                    .inputValue1(dto1.getValue())
                    .inputUnit1(dto1.getUnit())
                    .inputValue2(dto2.getValue())
                    .inputUnit2(dto2.getUnit())
                    .resultValue(result)
                    .resultUnit("SCALAR")
                    .hasError(false)
                    .build();

            repository.save(entity);

            LOGGER.info("Divide result: {}", result);
            return new QuantityDTO(result, "SCALAR", "DIVISION_RESULT");

        } catch (Exception e) {
            LOGGER.error("Divide failed", e);
            QuantityMeasurementEntity errorEntity = new QuantityMeasurementEntity.Builder()
                    .operation("DIVIDE")
                    .measurementType(dto1.getMeasurementType())
                    .inputValue1(dto1.getValue())
                    .inputUnit1(dto1.getUnit())
                    .inputValue2(dto2.getValue())
                    .inputUnit2(dto2.getUnit())
                    .hasError(true)
                    .errorMessage(e.getMessage())
                    .build();

            repository.save(errorEntity);
            return new QuantityDTO(e.getMessage());
        }
    }

    private Quantity createQuantity(QuantityDTO dto) {
        IMeasurable unit = getUnit(dto.getMeasurementType(), dto.getUnit());
        return new Quantity(dto.getValue(), unit);
    }

    private IMeasurable getUnit(String measurementType, String unitName) {
        switch (measurementType.toUpperCase()) {
            case "LENGTH":
                return LengthUnit.valueOf(unitName.toUpperCase());
            case "WEIGHT":
                return WeightUnit.valueOf(unitName.toUpperCase());
            case "VOLUME":
                return VolumeUnit.valueOf(unitName.toUpperCase());
            case "TEMPERATURE":
                return TemperatureUnit.valueOf(unitName.toUpperCase());
            default:
                throw new QuantityMeasurementException("Unknown measurement type: " + measurementType);
        }
    }
}