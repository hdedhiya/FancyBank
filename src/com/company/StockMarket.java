package com.company;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class StockMarket {
    ArrayList<Stock> stockMarket;
    public StockMarket(){
        SQLConnection sc = new SQLConnection();
        sc.TheSqlConnection();
        stockMarket = sc.getMarket();
        sc.close();
    }

    public ArrayList<Stock> getStockMarket() {
        return stockMarket;
    }

    public double queryPrice(String name, String code, int shares){
        Iterator<Stock> it= stockMarket.iterator();

        while(it.hasNext()){
            Stock s =it.next();
            if(s.getName().equals(name)&&s.getCode().equals(code)){
                if(s.getShares()<shares){
                    return -1;//not enough shares
                }
                return s.getPrice()*shares;//buy stock successfully
            }
        }
        return -2;//the stock does not exist
    }
    public double queryProfit(String name,String code,int share, double p){
        Iterator<Stock> it= stockMarket.iterator();
        while(it.hasNext()){
            Stock s =it.next();
            if(s.getName().equals(name)&&s.getCode().equals(code)){
                return s.getShares()*s.getPrice()-share*p;
            }
        }
        return 0;//the stock does not exist
    }
    public Stock buyStock(String name,String code,int shares){
        Iterator<Stock> it= stockMarket.iterator();

        while(it.hasNext()){
            Stock s =it.next();
            if(s.getName().equals(name)&&s.getCode().equals(code)){
                s.setShares(shares);
                return s;
            }
        }
        return null;
    }

    public double sellStock(Stock stock){
        Iterator<Stock> it= stockMarket.iterator();
        while(it.hasNext()){
            Stock s =it.next();
            if(s.getName().equals(stock.getName())&&s.getCode().equals(stock.getCode())){
                s.setShares(s.getShares()+stock.getShares());
                return stock.getShares()*s.getPrice();
            }
        }
        return -1;
    }

    @Override
    public String toString(){
        String str="";
        Iterator<Stock>it =stockMarket.iterator();
        Integer i=1;
        while(it.hasNext()){
            Stock s =it.next();
            str+=i.toString()+": "+s.toString()+"\n";
            i+=1;
        }
        return str;
    }
}
