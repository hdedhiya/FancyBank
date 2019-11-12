package com.company;

//generic currency class that is defined in Main and given to Bank on instantiation/
public class Currency {
    private char icon;
    private double conversionToBaseRate;
    private String textName;
    public Currency(char i, double rate, String n){
        icon = i;
        conversionToBaseRate = rate;
        textName = n;
    }

    @Override
    public String toString() {
        return textName + " " + icon;
    }

    public char getIcon() {
        return icon;
    }

    public double getConversionToBaseRate() {
        return conversionToBaseRate;
    }

    public String getTextName() {
        return textName;
    }
}
