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
        return Math.abs(thisInFeet - otherInFeet) < EPSILON;
    }
 public double toConvert(LengthUnit targetUnit){
    return  convert(this.value, this.unit, targetUnit);
        
 }
 
    public static double convert(double value, LengthUnit sourceUnit, LengthUnit targetUnit) {

        if (sourceUnit == null || targetUnit == null) {
            throw new IllegalArgumentException("unit should not be empty");
            
        }
        if (!Double.isFinite(value)) {
throw new IllegalArgumentException("Invaild numric value!!");
            
        }
        double valueInFeet = sourceUnit.toFeet(value);

        return targetUnit.fromFeet(valueInFeet);
    }


    public QuantityLength add(QuantityLength other) {
        if (other == null) throw new IllegalArgumentException("Operand cannot be null");


        double sumInFeet = this.toFeet() + other.toFeet();

        double resultValue = this.unit.fromFeet(sumInFeet);

        return new QuantityLength(resultValue, this.unit);
    }

    public static QuantityLength add(QuantityLength l1, QuantityLength l2) {
        if (l1 == null || l2 == null) throw new IllegalArgumentException("Operands cannot be null");
        return l1.add(l2);
    }


    public double getValue() {
        return value;
    }

    public LengthUnit getUnit() {
        return unit;
    }


}

