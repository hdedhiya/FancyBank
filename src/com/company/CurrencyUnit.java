package com.company;

public enum CurrencyUnit {
    USD("USD", 1.00, "$"),
    CNY("CNY", 0.142098, "¥"),
    EUR("EUR", 1.116716, "€"),
    GBP("GBP", 1.293608, "£"),
    HKD("HKD", 0.127609, "$"),
    CAD("CAD", 0.760986, "$"),
    JPY("JPY", 0.009240, "¥");

    private final String code;
    private final double rate;
    private final String icon;
    CurrencyUnit(String code, double rate, String icon) {
        this.code = code;
        this.rate = rate;
        this.icon = icon;
    }

    public double getRate() {
        return rate;
    }

    public String getIcon() {
        return icon;
    }

    public static double getExchangeRate(CurrencyUnit unit, CurrencyUnit toUnit) {
        return 0.0;
    }

    public double getConversionToBaseRate() {
        //base currency USD
        return this.rate;
    }

    @Override
    public String toString(){
        return this.code;
    }
}
