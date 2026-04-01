package QuantityMeasurementApp;

import com.qunantity.measurement.enums.LengthUnit;
import com.qunantity.measurement.model.QuantityLength;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuantityLengthTest {

    // 1
    @Test
    void testEquality_FeetToFeet_SameValue() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength q2 = new QuantityLength(1.0, LengthUnit.FEET);

        assertTrue(q1.equals(q2));
    }

    // 2
    @Test
    void testEquality_InchToInch_SameValue() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.INCH);
        QuantityLength q2 = new QuantityLength(1.0, LengthUnit.INCH);

        assertTrue(q1.equals(q2));
    }

    // 3
    @Test
    void testEquality_FeetToInch_EquivalentValue() {
        QuantityLength feet = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength inch = new QuantityLength(12.0, LengthUnit.INCH);

        assertTrue(feet.equals(inch));
    }

    // 4
    @Test
    void testEquality_InchToFeet_EquivalentValue() {
        QuantityLength inch = new QuantityLength(12.0, LengthUnit.INCH);
        QuantityLength feet = new QuantityLength(1.0, LengthUnit.FEET);

        assertTrue(inch.equals(feet));
    }

    // 5
    @Test
    void testEquality_FeetToFeet_DifferentValue() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength q2 = new QuantityLength(2.0, LengthUnit.FEET);

        assertFalse(q1.equals(q2));
    }

    // 6
    @Test
    void testEquality_InchToInch_DifferentValue() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.INCH);
        QuantityLength q2 = new QuantityLength(2.0, LengthUnit.INCH);

        assertFalse(q1.equals(q2));
    }

    // 7
    @Test
    void testEquality_InvalidUnit() {
        // In your design, invalid unit = null (since enum restricts values)
        assertThrows(IllegalArgumentException.class, () -> {
            new QuantityLength(1.0, null);
        });
    }

    // 8
    @Test
    void testEquality_NullUnit() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);

        assertNotEquals(null, q1);
    }

    // 9
    @Test
    void testEquality_SameReference() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);

        assertTrue(q1.equals(q1));
    }

    // 10
    @Test
    void testEquality_NullComparison() {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);

        assertFalse(q1.equals(null));
    }


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

        assertEquals(
                new QuantityLength(1.0, LengthUnit.CM),
                new QuantityLength(0.3937008, LengthUnit.INCH)
        );

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


}