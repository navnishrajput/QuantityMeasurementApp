package com.qunantity.measurement.enums;

public enum LengthUnit {

    FEET(1.0),

    INCH(1.0/12),

    YARD(3.0),

    CM(0.0328084);

    private final double toFeetFactor;



    LengthUnit(double toFeetFactor) {

        this.toFeetFactor = toFeetFactor;

    }



    public double toFeet(double value) {

        return value * toFeetFactor;

    }

}
