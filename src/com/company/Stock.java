package com.company;

public class Stock {
    private int index;
    private String name;
    private Double price;
    private String code;
    private Integer shares;
    public Stock(String name,String code){
        this.name=name;
        this.code=code;
        price=0.0;
        shares=0;
        index = -1;
    }
    public Stock(String name,String code,Double price,Integer shares){
        this.name=name;
        this.code=code;
        this.price=price;
        this.shares=shares;
    }
    public Stock(){

    }
    public String getName() {return name;}
    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public Integer getShares() {
        return shares;
    }

    public void setShares(Integer shares) {
        this.shares = shares;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        String str="name:"+name+" "+
                "code:"+code+" "+
                "prices:"+price.toString()+" "+
                "shares:"+shares.toString();
        return str;
    }
}
