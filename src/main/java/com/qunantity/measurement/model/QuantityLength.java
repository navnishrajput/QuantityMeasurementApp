package com.qunantity.measurement.model;

import com.qunantity.measurement.enums.LengthUnit;

public class QuantityLength {

    private double value;
    private LengthUnit unit;


    public QuantityLength(double value, LengthUnit unit) {
        if(unit == null) throw new IllegalArgumentException("unit can not null ");
        this.value = value;
        this.unit = unit;
    }

    public double toFeet(){
        return unit.toFeet(value);
    }
    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;
        QuantityLength other = (QuantityLength) obj;

        double thisInFeet = this.unit.toFeet(this.value);
        double otherInFeet = other.unit.toFeet(other.value);



        return Double.compare(thisInFeet,otherInFeet)==0;
    }

    @Override
    public String toString(){
        return value+" "+unit.name();
    }



}

