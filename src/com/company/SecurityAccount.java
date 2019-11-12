package com.company;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


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

    public double queryPriceForAll(){
        Iterator<Stock> iterator=stocks.iterator();
        double res = 0;
        while(iterator.hasNext()){
            Stock s =iterator.next();
            String name = s.getName();
            String code = s.getCode();
            int shares = s.getShares();
            double money=stockMarket.queryPrice(name, code, shares);
            res += money;

        }
        return res;
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


    public static boolean addStock(int accountNum, String companyName, String code, double price, int share) {
        boolean success = true;
        String sql = "insert into stock (accountNum, companyName, code, price, share) values (?, ?, ?, ?, ?)";
        PreparedStatement pst = null;
        SQLConnection sc = new SQLConnection();
        try {
            pst = (PreparedStatement)sc.getConn().prepareStatement(sql);
            pst.setString(1, Integer.toString(accountNum));
            pst.setString(2, companyName);
            pst.setString(3, code);
            pst.setString(4, Double.toString(price));
            pst.setString(5, Integer.toString(share));
            pst.execute();
            sc.close();
        }catch(Exception e) {
            System.out.println("Failed to add " + code + " !");
            success = false;
        }
        return success;
    }

    //get the chosen stock (to sell)
    public static Stock getStock(int stockNum) {
        //just for testing
        String sql = "select * from stock where stockNum = '" + stockNum + "'";
        PreparedStatement pst = null;
        Stock stock = null;
        SQLConnection sc = new SQLConnection();
        try {
            pst = (PreparedStatement) sc.getConn().prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                String companyName = rs.getString("companyName");
                String code = rs.getString("code");
                double price = Double.parseDouble(rs.getString("price"));
                int share = Integer.parseInt(rs.getString("share"));

                stock = new Stock(companyName, code, price, share);
            }
            sc.close();
        }catch (Exception e) {
            System.out.println("don't get any stock");
        }
        return stock;
    }

    //update stock shares after selling
    public static void updateStock(int stockNum, int shares){
        if(shares == 0) {
            deleteStock(stockNum);
            return;
        }
        String sql = "update stock set share = '" + shares + "' where stockNum = '" + stockNum + "'";

        PreparedStatement pst = null;
        SQLConnection sc = new SQLConnection();
        try {
            pst = (PreparedStatement) sc.getConn().prepareStatement(sql);
            pst.execute();
            sc.close();
        }catch (Exception e) {
            System.out.println("Failed to update shares of stock " + stockNum);
        }
    }

    public static void deleteStock(int stockNum){
        String sql = "delete from stock where stockNum = '" + stockNum + "'";
        PreparedStatement pst = null;
        SQLConnection sc = new SQLConnection();
        try {
            pst = (PreparedStatement) sc.getConn().prepareStatement(sql);
            pst.execute();
        }catch (Exception e) {
            System.out.println("Failed to delete stock " + stockNum);
        }
    }

    public static void sellAllStock(int accountNum) {

        String sql = "delete * from stock where accountNum = '" + accountNum + "'";
        PreparedStatement pst = null;
        SQLConnection sc = new SQLConnection();
        try {
            pst = (PreparedStatement) sc.getConn().prepareStatement(sql);
            pst.execute();
        } catch (Exception e) {
            System.out.println("Failed to delete all Stock");
        }
    }

    public static ArrayList<Stock> getStocks(int accountNum){
        String sql = "select * from stock where accountNum = '" + accountNum + "'";
        PreparedStatement pst = null;
        List<Stock> stocks = new ArrayList<Stock>();
        SQLConnection sc = new SQLConnection();
        try {
            pst = (PreparedStatement) sc.getConn().prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Stock stock = null;
                int stockNum = Integer.parseInt(rs.getString("stockNum"));
                String companyName = rs.getString("companyName");
                String code = rs.getString("code");
                double price = Double.parseDouble(rs.getString("price"));
                int share = Integer.parseInt(rs.getString("share"));
                stock = new Stock(companyName, code, price, share);
                stock.setIndex(stockNum);
                stocks.add(stock);
            }
            sc.close();
        }catch (Exception e) {
            System.out.println("don't get stock");
        }
        return (ArrayList<Stock>) stocks;
    }
}
