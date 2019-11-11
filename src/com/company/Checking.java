package com.company;

//Checking account that extends Account
public class Checking extends Account {
    public Checking(double b, Currency c, double fee, double r){
        super(b,c, fee, r);
        setAccountType(AccountType.CHECHINGACCOUNT);
    }
    
    public Checking(AccountType accountType) {
    	super(accountType);
    }

    @Override
    public Transaction applyInterest() {
        return null;
    }

    @Override
    public String toString() {
        return "Checking Account " + this.getIndex() + " Balance: " + this.getBalance();
    }
}
