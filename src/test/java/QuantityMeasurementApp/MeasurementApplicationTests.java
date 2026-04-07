package QuantityMeasurementApp;

import com.qunantity.measurement.enums.LengthUnit;
import com.qunantity.measurement.model.QuantityLength;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuantityLengthTest {

    @Test

    void testEquality_YardToYard_SameValue() {

        assertEquals(new QuantityLength(1.0, LengthUnit.YARD),

                new QuantityLength(1.0, LengthUnit.YARD));

    }



    @Test

    void testEquality_YardToYard_DifferentValue() {

        assertNotEquals(new QuantityLength(1.0, LengthUnit.YARD),

                new QuantityLength(2.0, LengthUnit.YARD));

    }



    @Test

    void testEquality_YardToFeet_EquivalentValue() {

        assertEquals(new QuantityLength(1.0, LengthUnit.YARD),

                new QuantityLength(3.0, LengthUnit.FEET));

    }



    @Test

    void testEquality_FeetToYard_EquivalentValue() {

        assertEquals(new QuantityLength(3.0, LengthUnit.FEET),

                new QuantityLength(1.0, LengthUnit.YARD));

    }



    @Test

    void testEquality_YardToInches_EquivalentValue() {

        assertEquals(new QuantityLength(1.0, LengthUnit.YARD),

                new QuantityLength(36.0, LengthUnit.INCH));

    }



    @Test

    void testEquality_InchesToYard_EquivalentValue() {

        assertEquals(new QuantityLength(36.0, LengthUnit.INCH),

                new QuantityLength(1.0, LengthUnit.YARD));

    }



    @Test

    void testEquality_YardToFeet_NonEquivalentValue() {

        assertNotEquals(new QuantityLength(1.0, LengthUnit.YARD),

                new QuantityLength(2.0, LengthUnit.FEET));

    }



    @Test

    void testEquality_CentimeterToInch_EquivalentValue() {

        assertEquals(new QuantityLength(1.0, LengthUnit.CM),

                new QuantityLength(0.3937008, LengthUnit.INCH));

    }





    @Test

    void testEquality_CentimeterToFeet_NonEquivalentValue() {

        assertNotEquals(new QuantityLength(1.0, LengthUnit.CM),

                new QuantityLength(1.0, LengthUnit.FEET));

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

        assertThrows(IllegalArgumentException.class,

                () -> new QuantityLength(1.0, null));

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

        assertThrows(IllegalArgumentException.class,

                () -> new QuantityLength(1.0, null));

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











    private static final double EPSILON = 1e-6;



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
}