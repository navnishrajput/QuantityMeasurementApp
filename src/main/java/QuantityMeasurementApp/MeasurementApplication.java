package QuantityMeasurementApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MeasurementApplication {

    static class Feet{
        private final double value;
        public Feet(double value){
            this.value = value;
        }
        public boolean equals(Object obj){
            if(this == obj) return  true;
            if(obj == null || getClass() != obj.getClass()){
                return  false;
            }
            Feet other = (Feet)obj;
            return Double . compare(this.value, other.value) ==0;
        }
    }


    static class Inch{
        private final double value;
        public Inch(double value){
            this.value = value;
        }
        public boolean equals(Object obj){
            if(this == obj) return  true;
            if(obj == null || getClass() != obj.getClass()){
                return  false;
            }
            Inch other = (Inch)obj;
            return Double . compare(this.value, other.value) ==0;
        }

    }

    public static void main(String[] args) {


        SpringApplication.run(MeasurementApplication.class, args);
        System.out.println("Feet answer: ");
        Feet f1 = new Feet(34.5);
        Feet f2 = new Feet(34.5);
        System.out.print("Answer: ");
        System.out.println(f1.equals(f2));


        System.out.println("Inch answer: ");
        Inch i1 = new Inch(12.2);
        Inch i2 = new Inch(12.2);
        System.out.print("Answer: ");
        System.out.println(i1.equals(i2));

    }


}
