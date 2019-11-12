package com.company;

import java.lang.reflect.Array;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StockMarket {
    ArrayList<Stock> stockMarket;
    public StockMarket(){
        stockMarket = getMarket();
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


    public static void updateStockMarket(int shares, String code) {
        String sql = "update market set share = '" + shares + "' where code = '" + code + "'";
        PreparedStatement pst = null;
        SQLConnection sc = new SQLConnection();
        try {
            pst = (PreparedStatement) sc.getConn().prepareStatement(sql);
            pst.execute();
            sc.close();
        } catch (Exception e) {
            System.out.println("Failed to update Stock market");
        }

    }


    public static void updateMarketPrice(double price, String code) {
        String sql = "update market set price = '" + price + "' where code = '" + code + "'";
        PreparedStatement pst = null;
        SQLConnection sc = new SQLConnection();
        try {
            pst = (PreparedStatement) sc.getConn().prepareStatement(sql);
            pst.execute();
            sc.close();
        } catch (Exception e) {
            System.out.println("Failed to update Stock market");
        }
    }

    public static ArrayList<Stock> getMarket() {
        String sql = "select * from market";
        //System.out.println(sql);
        PreparedStatement pst = null;
        List<Stock> stocks = new ArrayList<Stock>();
        SQLConnection sc = new SQLConnection();
        try {
            pst = (PreparedStatement) sc.getConn().prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                //System.out.println(rs.getString("companyName"));
                Stock stock = null;
                String companyName = rs.getString("companyName");
                String code = rs.getString("code");
                double price = Double.parseDouble(rs.getString("price"));
                int share = Integer.parseInt(rs.getString("share"));
                stock = new Stock(companyName, code, price, share);
                stocks.add(stock);
            }
            sc.close();
        }catch (Exception e) {
            System.out.println("don't get stock");
        }
        return (ArrayList<Stock>) stocks;
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
