package com.company;

import javax.swing.*;

//basic instantiation
public class Main {

    public static void main(String[] args) {




        Currency[] cs = new Currency[3];

        cs[0] = new Currency(CurrencyUnit.USD);
        cs[1] = new Currency(CurrencyUnit.CAD);
        cs[2] = new Currency(CurrencyUnit.GBP);

        Bank b = new Bank(cs, 5, 10, 2,1, .1, .2,500, 1);

        b.placeComponents();
    }






}