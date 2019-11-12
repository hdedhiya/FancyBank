package com.company;

//Savings class
public class Savings extends Account {
    private double minAmountforInterest;
    public Savings(double b, Currency c, double fee, double r, double min){
        super(b, c, fee, r);
        setAccountType(AccountType.SAVINGACCOUNT);
        minAmountforInterest = min;
    }
    
    public Savings(AccountType accountType) {
    	super(accountType);
    }


    @Override
    public String toString() {
        return "Savings Account " + this.getIndex() + " Balance: " + this.getBalance();
    }
}
