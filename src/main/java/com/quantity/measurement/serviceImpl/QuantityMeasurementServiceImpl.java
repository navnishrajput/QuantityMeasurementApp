package com.quantity.measurement.serviceImpl;

import com.quantity.measurement.enums.IMeasurable;
import com.quantity.measurement.enumsImpl.LengthUnit;
import com.quantity.measurement.enumsImpl.TemperatureUnit;
import com.quantity.measurement.enumsImpl.VolumeUnit;
import com.quantity.measurement.enumsImpl.WeightUnit;
import com.quantity.measurement.exception.QuantityMeasurementException;
import com.quantity.measurement.model.Quantity;
import com.quantity.measurement.dto.QuantityDTO;
import com.quantity.measurement.entity.QuantityMeasurementEntity;
import com.quantity.measurement.repository.IQuantityMeasurementRepository;
import com.quantity.measurement.service.IQuantityMeasurementService;

// UC15: Service implementation - encapsulates all business logic
@SuppressWarnings({"rawtypes", "unchecked"})
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private final IQuantityMeasurementRepository repository;

    // UC15: Constructor injection - repository provided by factory
    public QuantityMeasurementServiceImpl(IQuantityMeasurementRepository repository) {
        this.repository = repository;
    }

    @Override
    public QuantityDTO compare(QuantityDTO dto1, QuantityDTO dto2) {
        try {
            Quantity q1 = createQuantity(dto1);
            Quantity q2 = createQuantity(dto2);

            // UC10: Cross-category validation
            if (!q1.getUnit().getClass().equals(q2.getUnit().getClass())) {
                throw new IllegalArgumentException("Different measurement types");
            }

            boolean result = q1.equals(q2);

            // UC15: Save operation entity to repository for history tracking
            QuantityMeasurementEntity entity = new QuantityMeasurementEntity.Builder()
                    .operation("COMPARE")
                    .inputValue1(dto1.getValue())
                    .inputUnit1(dto1.getUnit())
                    .inputValue2(dto2.getValue())
                    .inputUnit2(dto2.getUnit())
                    .resultValue(result ? 1.0 : 0.0)
                    .resultUnit(result ? "EQUAL" : "NOT_EQUAL")
                    .hasError(false)
                    .build();

            repository.save(entity);

            return new QuantityDTO(result ? 1.0 : 0.0,
                    result ? "EQUAL" : "NOT_EQUAL", "COMPARISON");

        } catch (Exception e) {
            // UC15: Error entity saved for debugging and audit
            QuantityMeasurementEntity errorEntity = new QuantityMeasurementEntity.Builder()
                    .operation("COMPARE")
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
        try {
            Quantity q = createQuantity(dto);
            IMeasurable target = getUnit(dto.getMeasurementType(), targetUnit);

            Quantity result = q.toConvert(target);

            QuantityMeasurementEntity entity = new QuantityMeasurementEntity.Builder()
                    .operation("CONVERT")
                    .inputValue1(dto.getValue())
                    .inputUnit1(dto.getUnit())
                    .targetUnit(targetUnit)
                    .resultValue(result.getValue())
                    .resultUnit(result.getUnit().toString())
                    .hasError(false)
                    .build();

            repository.save(entity);

            return new QuantityDTO(result.getValue(),
                    result.getUnit().toString(), dto.getMeasurementType());

        } catch (Exception e) {
            QuantityMeasurementEntity errorEntity = new QuantityMeasurementEntity.Builder()
                    .operation("CONVERT")
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

    // UC15: Internal helper - shared by both add() overloads to avoid code duplication
    private QuantityDTO addInternal(QuantityDTO dto1, QuantityDTO dto2, String targetUnit) {
        try {
            Quantity q1 = createQuantity(dto1);
            Quantity q2 = createQuantity(dto2);
            IMeasurable target = getUnit(dto1.getMeasurementType(), targetUnit);

            // UC14: Temperature arithmetic validation
            q1.getUnit().validateOperationSupport("ADD");

            if (!q1.getUnit().getClass().equals(q2.getUnit().getClass())) {
                throw new IllegalArgumentException("Different measurement types");
            }

            Quantity result = q1.add(q2, target);

            QuantityMeasurementEntity entity = new QuantityMeasurementEntity.Builder()
                    .operation("ADD")
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

            return new QuantityDTO(result.getValue(),
                    result.getUnit().toString(), dto1.getMeasurementType());

        } catch (Exception e) {
            QuantityMeasurementEntity errorEntity = new QuantityMeasurementEntity.Builder()
                    .operation("ADD")
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

    // UC15: Internal helper - shared by both subtract() overloads
    private QuantityDTO subtractInternal(QuantityDTO dto1, QuantityDTO dto2, String targetUnit) {
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

            return new QuantityDTO(result.getValue(),
                    result.getUnit().toString(), dto1.getMeasurementType());

        } catch (Exception e) {
            QuantityMeasurementEntity errorEntity = new QuantityMeasurementEntity.Builder()
                    .operation("SUBTRACT")
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
                    .inputValue1(dto1.getValue())
                    .inputUnit1(dto1.getUnit())
                    .inputValue2(dto2.getValue())
                    .inputUnit2(dto2.getUnit())
                    .resultValue(result)
                    .resultUnit("SCALAR")
                    .hasError(false)
                    .build();

            repository.save(entity);

            return new QuantityDTO(result, "SCALAR", "DIVISION_RESULT");

        } catch (Exception e) {
            QuantityMeasurementEntity errorEntity = new QuantityMeasurementEntity.Builder()
                    .operation("DIVIDE")
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

    // UC15: Factory method - creates Quantity from DTO by resolving unit enum
    private Quantity createQuantity(QuantityDTO dto) {
        IMeasurable unit = getUnit(dto.getMeasurementType(), dto.getUnit());
        return new Quantity(dto.getValue(), unit);
    }

    // UC15: Unit resolver - maps measurement type string to concrete enum
    private IMeasurable getUnit(String measurementType, String unitName) {
        // UC15: Future extension - add new cases for additional measurement types
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