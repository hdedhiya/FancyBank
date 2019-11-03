package com.company;

//generic currenct class that is defined in Main and given to Bank on instantiation/
public class Currency {
    private CurrencyUnit unit;
    public Currency(CurrencyUnit unit){
        this.unit = unit;
    }

    @Override
    public String toString() {
        return unit + " " + unit.getIcon();
    }

    public double getConversionToBaseRate() {
        return unit.getConversionToBaseRate();
    }

    public String getTextName() {
        return unit.toString();
    }
}
