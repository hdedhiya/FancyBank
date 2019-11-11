package com.company;

//Savings class that defines its own applyInterest
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
    public Transaction applyInterest() {
//        double initB = getBalance();
//        if (initB > minAmountforInterest){
//            addBalance(initB*getRate());
//            Transaction t = new Transaction("Savings", getIndex(), "Apply Interest", initB, getBalance(), 0);
//            addTransaction(t);
//            return t;
//        }
//        else{
//            return null;
//        }
        return null;
    }

    @Override
    public String toString() {
        return "Savings Account " + this.getIndex() + " Balance: " + this.getBalance();
    }
}
