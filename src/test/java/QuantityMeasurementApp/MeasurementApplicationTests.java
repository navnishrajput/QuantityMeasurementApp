package QuantityMeasurementApp;

import com.qunantity.measurement.enums.LengthUnit;
import com.qunantity.measurement.model.QuantityLength;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuantityLengthTest {
    private final double EPSILON = 1e-6;

    @Test
    void testEquality_YardToYard_SameValue() {
        assertEquals(new QuantityLength(1.0, LengthUnit.YARD), new QuantityLength(1.0, LengthUnit.YARD));
    }

    @Test
    void testEquality_YardToYard_DifferentValue() {
        assertNotEquals(new QuantityLength(1.0, LengthUnit.YARD), new QuantityLength(2.0, LengthUnit.YARD));
    }

    @Test
    void testEquality_YardToFeet_EquivalentValue() {
        assertEquals(new QuantityLength(1.0, LengthUnit.YARD), new QuantityLength(3.0, LengthUnit.FEET));
    }

    @Test
    void testEquality_FeetToYard_EquivalentValue() {
        assertEquals(new QuantityLength(3.0, LengthUnit.FEET), new QuantityLength(1.0, LengthUnit.YARD));
    }

    @Test
    void testEquality_YardToInches_EquivalentValue() {
        assertEquals(new QuantityLength(1.0, LengthUnit.YARD), new QuantityLength(36.0, LengthUnit.INCH));
    }

    @Test
    void testEquality_InchesToYard_EquivalentValue() {
        assertEquals(new QuantityLength(36.0, LengthUnit.INCH), new QuantityLength(1.0, LengthUnit.YARD));
    }

    @Test
    void testEquality_YardToFeet_NonEquivalentValue() {
        assertNotEquals(new QuantityLength(1.0, LengthUnit.YARD), new QuantityLength(2.0, LengthUnit.FEET));
    }

    @Test
    void testEquality_CentimeterToInch_EquivalentValue() {
        assertEquals(new QuantityLength(1.0, LengthUnit.CM), new QuantityLength(0.3937008, LengthUnit.INCH));
    }

    @Test
    void testEquality_CentimeterToFeet_NonEquivalentValue() {
        assertNotEquals(new QuantityLength(1.0, LengthUnit.CM), new QuantityLength(1.0, LengthUnit.FEET));
    }

    @Test
    void testEquality_MultiUnit_TransitiveProperty() {
        QuantityLength yard = new QuantityLength(1.0, LengthUnit.YARD);
        QuantityLength feet = new QuantityLength(3.0, LengthUnit.FEET);
        QuantityLength inch = new QuantityLength(36.0, LengthUnit.INCH);

        assertEquals(yard, feet);
        assertEquals(feet, inch);
        assertEquals(yard, inch);
    }

    @Test
    void testEquality_YardWithNullUnit() {
        assertThrows(IllegalArgumentException.class, () -> new QuantityLength(1.0, null));
    }

    @Test
    void testEquality_YardSameReference() {
        QuantityLength q = new QuantityLength(1.0, LengthUnit.YARD);
        assertEquals(q, q);
    }

    @Test
    void testEquality_YardNullComparison() {
        QuantityLength q = new QuantityLength(1.0, LengthUnit.YARD);
        assertNotEquals(q, null);
    }

    @Test
    void testEquality_CentimetersWithNullUnit() {
        assertThrows(IllegalArgumentException.class, () -> new QuantityLength(1.0, null));
    }

    @Test
    void testEquality_CentimetersSameReference() {
        QuantityLength q = new QuantityLength(1.0, LengthUnit.CM);
        assertEquals(q, q);
    }

    @Test
    void testEquality_CentimetersNullComparison() {
        QuantityLength q = new QuantityLength(1.0, LengthUnit.CM);
        assertNotEquals(q, null);
    }

    @Test
    void testEquality_AllUnits_ComplexScenario() {
        QuantityLength yard = new QuantityLength(2.0, LengthUnit.YARD);
        QuantityLength feet = new QuantityLength(6.0, LengthUnit.FEET);
        QuantityLength inch = new QuantityLength(72.0, LengthUnit.INCH);

        assertEquals(yard, feet);
        assertEquals(feet, inch);
        assertEquals(yard, inch);
    }

   // private static final double EPSILON = 1e-6;

    @Test
    void testConversion_FeetToInches() {
        double result = QuantityLength.convert(1.0, LengthUnit.FEET, LengthUnit.INCH);
        assertEquals(12.0, result, EPSILON);
    }

    @Test
    void testConversion_InchesToFeet() {
        double result = QuantityLength.convert(24.0, LengthUnit.INCH, LengthUnit.FEET);
        assertEquals(2.0, result, EPSILON);
    }

    @Test
    void testConversion_YardsToInches() {
        double result = QuantityLength.convert(1.0, LengthUnit.YARD, LengthUnit.INCH);
        assertEquals(36.0, result, EPSILON);
    }

    @Test
    void testConversion_InchesToYards() {
        double result = QuantityLength.convert(72.0, LengthUnit.INCH, LengthUnit.YARD);
        assertEquals(2.0, result, EPSILON);
    }

    @Test
    void testConversion_CentimetersToInches() {
        double result = QuantityLength.convert(2.54, LengthUnit.CM, LengthUnit.INCH);
        assertEquals(1.0, result, EPSILON);
    }

    @Test
    void testConversion_FeetToYards() {
        double result = QuantityLength.convert(6.0, LengthUnit.FEET, LengthUnit.YARD);
        assertEquals(2.0, result, EPSILON);
    }

    @Test
    void testConversion_ZeroValue() {
        double result = QuantityLength.convert(0.0, LengthUnit.FEET, LengthUnit.INCH);
        assertEquals(0.0, result, EPSILON);
    }

    @Test
    void testConversion_NegativeValue() {
        double result = QuantityLength.convert(-1.0, LengthUnit.FEET, LengthUnit.INCH);
        assertEquals(-12.0, result, EPSILON);
    }

    @Test
    void testConversion_RoundTrip() {
        double value = 5.0;

        double converted = QuantityLength.convert(value, LengthUnit.FEET, LengthUnit.INCH);
        double back = QuantityLength.convert(converted, LengthUnit.INCH, LengthUnit.FEET);

        assertEquals(value, back, EPSILON);
    }

    @Test
    void testConversion_InvalidUnit_Throws() {
        assertThrows(IllegalArgumentException.class, () -> {
            QuantityLength.convert(1.0, null, LengthUnit.INCH);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            QuantityLength.convert(1.0, LengthUnit.FEET, null);
        });
    }

    @Test
    void testConversion_NaNOrInfinite_Throws() {
        assertThrows(IllegalArgumentException.class, () -> {
            QuantityLength.convert(Double.NaN, LengthUnit.FEET, LengthUnit.INCH);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            QuantityLength.convert(Double.POSITIVE_INFINITY, LengthUnit.FEET, LengthUnit.INCH);
        });
    }

    @Test
    void testConversion_PrecisionTolerance() {
        double result = QuantityLength.convert(1.0, LengthUnit.CM, LengthUnit.INCH);
        assertEquals(0.3937, result, 1e-3);
    }

    @Test
    void testAddition_SameUnit_FeetPlusFeet() {
        QuantityLength result = new QuantityLength(1.0, LengthUnit.FEET).add(new QuantityLength(2.0, LengthUnit.FEET));

        assertEquals(new QuantityLength(3.0, LengthUnit.FEET), result);
    }

    @Test
    void testAddition_SameUnit_InchPlusInch() {
        QuantityLength result = new QuantityLength(5.0, LengthUnit.INCH).add(new QuantityLength(7.0, LengthUnit.INCH));

        assertEquals(new QuantityLength(12.0, LengthUnit.INCH), result);
    }

    @Test
    void testAddition_CrossUnit_FeetPlusInches() {
        QuantityLength result = new QuantityLength(1.0, LengthUnit.FEET).add(new QuantityLength(12.0, LengthUnit.INCH));

        assertEquals(new QuantityLength(2.0, LengthUnit.FEET), result);
    }

    @Test
    void testAddition_CrossUnit_InchPlusFeet() {
        QuantityLength result = new QuantityLength(12.0, LengthUnit.INCH).add(new QuantityLength(1.0, LengthUnit.FEET));

        assertEquals(new QuantityLength(24.0, LengthUnit.INCH), result);
    }

    @Test
    void testAddition_CrossUnit_YardPlusFeet() {
        QuantityLength result = new QuantityLength(1.0, LengthUnit.YARD).add(new QuantityLength(3.0, LengthUnit.FEET));

        assertEquals(new QuantityLength(2.0, LengthUnit.YARD), result);
    }

    @Test
    void testAddition_CrossUnit_CentimeterPlusInch() {
        QuantityLength result = new QuantityLength(2.54, LengthUnit.CM).add(new QuantityLength(1.0, LengthUnit.INCH));

        assertEquals(new QuantityLength(5.08, LengthUnit.CM), result);
    }

    @Test
    void testAddition_Commutativity() {
        QuantityLength a = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength b = new QuantityLength(12.0, LengthUnit.INCH);

        double result1 = a.add(b).toFeet();
        double result2 = b.add(a).toFeet();

        assertEquals(result1, result2, EPSILON);
    }

    @Test
    void testAddition_WithZero() {
        QuantityLength result = new QuantityLength(5.0, LengthUnit.FEET).add(new QuantityLength(0.0, LengthUnit.INCH));

        assertEquals(new QuantityLength(5.0, LengthUnit.FEET), result);
    }

    @Test
    void testAddition_NegativeValues() {
        QuantityLength result = new QuantityLength(5.0, LengthUnit.FEET).add(new QuantityLength(-2.0, LengthUnit.FEET));

        assertEquals(new QuantityLength(3.0, LengthUnit.FEET), result);
    }

    @Test
    void testAddition_NullSecondOperand() {
        QuantityLength q = new QuantityLength(1.0, LengthUnit.FEET);

        assertThrows(IllegalArgumentException.class, () -> {
            q.add(null);
        });
    }

    @Test
    void testAddition_LargeValues() {
        QuantityLength result = new QuantityLength(1e6, LengthUnit.FEET).add(new QuantityLength(1e6, LengthUnit.FEET));

        assertEquals(new QuantityLength(2e6, LengthUnit.FEET), result);
    }

    @Test
    void testAddition_SmallValues() {
        QuantityLength result = new QuantityLength(0.001, LengthUnit.FEET)
                .add(new QuantityLength(0.002, LengthUnit.FEET));

        assertEquals(new QuantityLength(0.003, LengthUnit.FEET), result);
    }

    // ================= UC7 (Instance Method Based) =================
    @Test
    void testAddition_ExplicitTargetUnit_Feet() {
        QuantityLength result = new QuantityLength(1.0, LengthUnit.FEET)
                .add(new QuantityLength(12.0, LengthUnit.INCH), LengthUnit.FEET);

        assertEquals(new QuantityLength(2.0, LengthUnit.FEET), result);
    }

    @Test
    void testAddition_ExplicitTargetUnit_Inches() {
        QuantityLength result = new QuantityLength(1.0, LengthUnit.FEET)
                .add(new QuantityLength(12.0, LengthUnit.INCH), LengthUnit.INCH);

        assertEquals(new QuantityLength(24.0, LengthUnit.INCH), result);
    }

    @Test
    void testAddition_ExplicitTargetUnit_Yards() {
        QuantityLength result = new QuantityLength(1.0, LengthUnit.FEET)
                .add(new QuantityLength(12.0, LengthUnit.INCH), LengthUnit.YARD);

        assertEquals(new QuantityLength(2.0 / 3.0, LengthUnit.YARD), result);
    }

    @Test
    void testAddition_ExplicitTargetUnit_Centimeters() {
        QuantityLength result = new QuantityLength(2.54, LengthUnit.CM)
                .add(new QuantityLength(1.0, LengthUnit.INCH), LengthUnit.CM);

        assertEquals(new QuantityLength(5.08, LengthUnit.CM), result);
    }

    @Test
    void testAddition_ExplicitTargetUnit_SameAsFirstOperand() {
        QuantityLength result = new QuantityLength(1.0, LengthUnit.YARD)
                .add(new QuantityLength(2.0, LengthUnit.YARD), LengthUnit.YARD);

        assertEquals(new QuantityLength(3.0, LengthUnit.YARD), result);
    }

    @Test
    void testAddition_ExplicitTargetUnit_SameAsSecondOperand() {
        QuantityLength result = new QuantityLength(3.0, LengthUnit.FEET)
                .add(new QuantityLength(6.0, LengthUnit.FEET), LengthUnit.FEET);

        assertEquals(new QuantityLength(9.0, LengthUnit.FEET), result);
    }

    @Test
    void testAddition_ExplicitTargetUnit_Commutativity() {
        QuantityLength a = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength b = new QuantityLength(12.0, LengthUnit.INCH);

        double r1 = a.add(b, LengthUnit.FEET).toFeet();
        double r2 = b.add(a, LengthUnit.FEET).toFeet();

        assertEquals(r1, r2, EPSILON);
    }

    @Test
    void testAddition_ExplicitTargetUnit_WithZero() {
        QuantityLength result = new QuantityLength(5.0, LengthUnit.FEET)
                .add(new QuantityLength(0.0, LengthUnit.INCH), LengthUnit.YARD);

        assertEquals(new QuantityLength(5.0 / 3.0, LengthUnit.YARD), result);
    }

    @Test
    void testAddition_ExplicitTargetUnit_NegativeValues() {
        QuantityLength result = new QuantityLength(5.0, LengthUnit.FEET)
                .add(new QuantityLength(-2.0, LengthUnit.FEET), LengthUnit.INCH);

        assertEquals(new QuantityLength(36.0, LengthUnit.INCH), result);
    }

    @Test
    void testAddition_ExplicitTargetUnit_NullTargetUnit() {
        assertThrows(IllegalArgumentException.class, () -> {
            new QuantityLength(1.0, LengthUnit.FEET)
                    .add(new QuantityLength(1.0, LengthUnit.FEET), null);
        });
    }

    @Test
    void testAddition_ExplicitTargetUnit_LargeToSmallScale() {
        QuantityLength result = new QuantityLength(1000.0, LengthUnit.FEET)
                .add(new QuantityLength(500.0, LengthUnit.FEET), LengthUnit.INCH);

        assertEquals(new QuantityLength(18000.0, LengthUnit.INCH), result);
    }

    @Test
    void testAddition_ExplicitTargetUnit_SmallToLargeScale() {
        QuantityLength result = new QuantityLength(12.0, LengthUnit.INCH)
                .add(new QuantityLength(12.0, LengthUnit.INCH), LengthUnit.YARD);

        assertEquals(new QuantityLength(2.0 / 3.0, LengthUnit.YARD), result);
    }

    @Test
    void testAddition_ExplicitTargetUnit_PrecisionTolerance() {
        QuantityLength result = new QuantityLength(1.0, LengthUnit.CM)
                .add(new QuantityLength(1.0, LengthUnit.CM), LengthUnit.INCH);

        assertEquals(new QuantityLength(0.7874, LengthUnit.INCH), result);
    }

    @Test
    void testAddition_ExplicitTargetUnit_AllUnitCombinations() {
        LengthUnit[] units = LengthUnit.values();
        for (LengthUnit u1 : units) {
            for (LengthUnit u2 : units) {
                for (LengthUnit target : units) {
                    QuantityLength q1 = new QuantityLength(1.0, u1);
                    QuantityLength q2 = new QuantityLength(1.0, u2);

                    QuantityLength result = q1.add(q2, target);
                    double expected = QuantityLength.convert(1.0, u1, target) +
                            QuantityLength.convert(1.0, u2, target);
                    assertEquals(expected, result.toConvert(target), 1e-6,
                            "Failed for units: " + u1 + ", " + u2 + " -> " + target);
                }
            }
        }
    }
    @Test
    void testLengthUnitEnum_FeetConstant() {
        assertEquals(1.0, LengthUnit.FEET.getConversionFactor());
    }

    @Test
    void testLengthUnitEnum_InchesConstant() {
        assertEquals(1.0 / 12.0, LengthUnit.INCH.getConversionFactor(), EPSILON);
    }

    @Test
    void testLengthUnitEnum_YardsConstant() {
        assertEquals(3.0, LengthUnit.YARD.getConversionFactor());
    }

    @Test
    void testLengthUnitEnum_CentimetersConstant() {
        assertEquals(0.0328084, LengthUnit.CM.getConversionFactor(), EPSILON);
    }

    @Test
    void testConvertToBaseUnit_FeetToFeet() {
        double result = LengthUnit.FEET.convertToBaseUnit(5.0);
        assertEquals(5.0, result, EPSILON);
    }

    @Test
    void testConvertToBaseUnit_InchesToFeet() {
        double result = LengthUnit.INCH.convertToBaseUnit(12.0);
        assertEquals(1.0, result, EPSILON);
    }

    @Test
    void testConvertToBaseUnit_YardsToFeet() {
        double result = LengthUnit.YARD.convertToBaseUnit(1.0);
        assertEquals(3.0, result, EPSILON);
    }

    @Test
    void testConvertToBaseUnit_CentimetersToFeet() {
        // 30.48 cm is approximately 1 foot
        double result = LengthUnit.CM.convertToBaseUnit(30.48);
        assertEquals(1.0, result, 1e-3);
    }

    @Test
    void testConvertFromBaseUnit_FeetToFeet() {
        double result = LengthUnit.FEET.convertFromBaseUnit(2.0);
        assertEquals(2.0, result, EPSILON);
    }

    @Test
    void testConvertFromBaseUnit_FeetToInches() {
        double result = LengthUnit.INCH.convertFromBaseUnit(1.0);
        assertEquals(12.0, result, EPSILON);
    }

    @Test
    void testConvertFromBaseUnit_FeetToYards() {
        double result = LengthUnit.YARD.convertFromBaseUnit(3.0);
        assertEquals(1.0, result, EPSILON);
    }

    @Test
    void testConvertFromBaseUnit_FeetToCentimeters() {
        double result = LengthUnit.CM.convertFromBaseUnit(1.0);
        assertEquals(30.48, result, 1e-3);
    }

    // ================= UC8: Refactored QuantityLength Tests =================

    @Test
    void testQuantityLengthRefactored_Equality() {
        QuantityLength oneFoot = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength twelveInches = new QuantityLength(12.0, LengthUnit.INCH);

        // Verifies equals() delegates to unit.convertToBaseUnit()
        assertEquals(oneFoot, twelveInches);
    }

    @Test
    void testQuantityLengthRefactored_ConvertTo() {
        QuantityLength oneFoot = new QuantityLength(1.0, LengthUnit.FEET);
        double convertedValue = oneFoot.toConvert(LengthUnit.INCH);

        assertEquals(12.0, convertedValue, EPSILON);
    }

    @Test
    void testQuantityLengthRefactored_Add() {
        QuantityLength oneFoot = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength twelveInches = new QuantityLength(12.0, LengthUnit.INCH);

        // Result in FEET (first operand unit)
        QuantityLength result = oneFoot.add(twelveInches);

        assertEquals(new QuantityLength(2.0, LengthUnit.FEET), result);
    }

    @Test
    void testQuantityLengthRefactored_AddWithTargetUnit() {
        QuantityLength oneFoot = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength twelveInches = new QuantityLength(12.0, LengthUnit.INCH);

        // Result in YARDS (explicit target)
        QuantityLength result = oneFoot.add(twelveInches, LengthUnit.YARD);

        assertEquals(new QuantityLength(2.0 / 3.0, LengthUnit.YARD), result);
    }

    @Test
    void testQuantityLengthRefactored_NullUnit() {
        assertThrows(IllegalArgumentException.class, () -> {
            new QuantityLength(1.0, null);
        });
    }

    @Test
    void testQuantityLengthRefactored_InvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            new QuantityLength(Double.NaN, LengthUnit.FEET);
        });
    }

    @Test
    void testRoundTripConversion_RefactoredDesign() {
        double originalValue = 10.0;
        // Convert FEET -> INCH -> FEET
        double toInch = LengthUnit.INCH.convertFromBaseUnit(
                LengthUnit.FEET.convertToBaseUnit(originalValue));
        double backToFeet = LengthUnit.FEET.convertFromBaseUnit(
                LengthUnit.INCH.convertToBaseUnit(toInch));

        assertEquals(originalValue, backToFeet, EPSILON);
    }

    @Test
    void testBackwardCompatibility_UC1EqualityTests() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength q2 = new QuantityLength(1.0, LengthUnit.FEET);
        assertEquals(q1, q2);
    }

    @Test
    void testArchitecturalScalability_MultipleCategories() {
        assertNotNull(LengthUnit.FEET);
    }

    @Test
    void testUnitImmutability() {
        LengthUnit unit = LengthUnit.FEET;
        assertEquals(1.0, unit.getConversionFactor());
    }

    @Test
    void testQuantityLengthRefactored_AddWithTargetUnit_Yards() {
        QuantityLength oneFoot = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength twelveInches = new QuantityLength(12.0, LengthUnit.INCH);

        QuantityLength result = oneFoot.add(twelveInches, LengthUnit.YARD);

        assertEquals(0.6666666666666666, result.getValue(), EPSILON);
        assertEquals(LengthUnit.YARD, result.getUnit());
    }

    @Test
    void testBackwardCompatibility_UC5ConversionTests() {
        double result = QuantityLength.convert(1.0, LengthUnit.YARD, LengthUnit.INCH);
        assertEquals(36.0, result, EPSILON);
    }
    @Test
    void testQuantityLengthRefactored_AddWithTargetUnit_Commutativity() {
        QuantityLength a = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength b = new QuantityLength(12.0, LengthUnit.INCH);

        QuantityLength result1 = a.add(b, LengthUnit.YARD);
        QuantityLength result2 = b.add(a, LengthUnit.YARD);

        assertEquals(result1.getValue(), result2.getValue(), EPSILON);
        assertEquals(result1.getUnit(), result2.getUnit());
    }

}