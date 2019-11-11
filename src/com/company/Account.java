package com.company;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

//class that defines an account with basic methods with abstract methods to fill in later in Checking, Savings, and Loan
public abstract class Account {
    private double balance;
    private double rate;
    private static Integer count = 0;
    private Integer index;
    private LinkedHashMap<Integer, Transaction> ts;
    private Integer transactionCounter;
    private AccountType accountType;

    public Account(AccountType accountType) {
    	this.accountType = accountType;
    }
    
    public Account(double b, Currency c, double fee, double r){
        transactionCounter = 0;
        balance = (b * c.getConversionToBaseRate()) - fee;
        index = count;
        count += 1;
        ts = new LinkedHashMap();
        rate = r;
    }

    public double getBalance() {
        return balance;
    }

    public double getRate(){
        return rate;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public AccountType getAccountType(){
        return accountType;
    }

    public void setAccountType(AccountType aT){
        accountType = aT;
    }

    public void addBalance(double b){
        balance += b;
    }

    public void removeBalance(double b){
        balance -= b;
    }

    public void setIndex(int index) {
    	this.index = index;
    }
    
    public Integer getIndex(){
        return index;
    }

    public Integer getCount(){
        return count;
    }

    public void addTransaction(Transaction t){
        ts.put(transactionCounter, t);
        transactionCounter += 1;
    }

    public Collection<Transaction> getAllTransactions(){
        return ts.values();
    }

    public abstract Transaction applyInterest();

    public abstract String toString();

}
