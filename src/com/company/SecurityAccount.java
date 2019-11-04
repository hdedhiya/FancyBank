package BankATM;

import java.util.ArrayList;
import java.util.Iterator;

public class SecurityAccount {
    ArrayList<Stock> stocks;
    StockMarket stockMarket;

    public SecurityAccount(ArrayList<Stock>stocks,StockMarket stockMarket){
        this.stocks=stocks;
        this.stockMarket=stockMarket;
    }
    public double queryPrice(String name,String code,int shares){
        return stockMarket.queryPrice(name,code,shares);
    }
    public void buyStock(String name,String code,int shares){
        Stock s =stockMarket.buyStock(name,code,shares);
        stocks.add(s);
    }
    public double sellStock(String name,String code){
        Iterator<Stock> iterator=stocks.iterator();

        while(iterator.hasNext()){
            Stock s =iterator.next();
            if(s.getName().equals(name)&&s.getCode().equals(code)){
                double money=stockMarket.sellStock(s);
                stocks.remove(s);
                return money;
            }
        }
        return 0.0;
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
}
