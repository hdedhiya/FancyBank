package com.company;

//basic instantiation
public class Main {

    public static void main(String[] args) {




        Currency[] cs = new Currency[3];

        cs[0] = new Currency('$', 1.0, "US dollars");
        cs[1] = new Currency('&' , .5, "Canadian dollars");
        cs[2] = new Currency('@', 2.0, "UK pounds");

        Bank b = new Bank(cs, 5, 10, 2,1, .1, .2,500, 1);

        b.placeComponents();
    }






}