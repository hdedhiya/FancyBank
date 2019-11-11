package com.company;

import java.util.ArrayList;
import java.util.Iterator;


public class SecurityAccount {
    ArrayList<Stock> stocks;
    StockMarket stockMarket;
    Savings savings;
    public SecurityAccount(ArrayList<Stock>stocks, StockMarket stockMarket,Savings s){
        this.stocks=stocks;
        this.stockMarket=stockMarket;
        this.savings=s;
    }
    public double queryPrice(String name,String code,int shares){
        return stockMarket.queryPrice(name,code,shares);
    }
    public int buyStock(String name,String code,int shares) {
        double result = queryPrice(name, code, shares);
        if (result >0){
            Stock s = stockMarket.buyStock(name, code, shares);
            savings.removeBalance(s.getPrice() * shares);
            stocks.add(s);
            return 1;//success
        }
        else{
            return (int)result;//-1 for not enough shares; -2 for stock not exists
        }
    }
    public double viewProfit(){
        Iterator<Stock> iterator=stocks.iterator();
        double profit=0;
        while(iterator.hasNext()){
            Stock s=iterator.next();
            profit+=stockMarket.queryProfit(s.getName(),s.getCode(),s.getShares(),s.getPrice());
        }
        return profit;
    }

    public double sellStock(String name,String code){
        Iterator<Stock> iterator=stocks.iterator();
        while(iterator.hasNext()){
            Stock s =iterator.next();
            if(s.getName().equals(name)&&s.getCode().equals(code)){
                double money=stockMarket.sellStock(s);
                if(money!=-1) {
                    savings.addBalance(money);
                    stocks.remove(s);
                    return money;
                }
                else
                    return -1;//does not exist
            }
        }
        return -1;//the stock does not exist
    }

    public void sellAllStock(){
        Iterator<Stock> iterator=stocks.iterator();
        while(iterator.hasNext()){
            Stock s =iterator.next();
            double money=stockMarket.sellStock(s);
            if(money!=-1){
                savings.addBalance(money);
                stocks.remove(s);
            }
        }
    }

    public double showPurchasePower(){
        return savings.getBalance();
    }

    @Override
    public String toString(){
        String str="";
        Iterator<Stock>it =stocks.iterator();
        Integer i=1;
        while(it.hasNext()){
            Stock s =it.next();
            str+=i.toString()+": "+s.toString()+"\n";
            i+=1;
        }
        return str;
    }
    public String viewMarket(){
        return stockMarket.toString();
    }
}
