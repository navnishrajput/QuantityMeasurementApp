package com.qunantity.measurement.model;

import com.qunantity.measurement.enums.LengthUnit;

public class QuantityLength {

    private final double value;
    private final LengthUnit unit;
    private final double EPSILON =1e-6;


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
       // if(!(obj instanceof QuantityLength)) return false;
        QuantityLength other = (QuantityLength) obj;

        double thisInFeet = this.toFeet();
        double otherInFeet = other.toFeet();
        return Double.compare(thisInFeet,otherInFeet)==0;
    }
 public double toConvert(LengthUnit targetUnit){
    return  convert(this.value, this.unit, targetUnit);
        
 }



    public static double convert(double value, LengthUnit sorceUnit, LengthUnit targetUnit) {

        if (sorceUnit == null || targetUnit == null) {
            throw new IllegalArgumentException("unit should not be empty");
            
        }
        if (!Double.isFinite(value)) {
throw new IllegalArgumentException("Invaild numric value!!");
            
        }
        double valueInFeet = sorceUnit.toFeet(value);

        return targetUnit.fromFeet(valueInFeet);
    }



}

