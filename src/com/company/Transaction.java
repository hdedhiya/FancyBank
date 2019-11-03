package com.company;

//Transaction class that is used in account and bank to keep track of changes.
public class Transaction {
    private String accountType;
    private int accountNumber;
    private String transactionType;
    private double initBalance;
    private double finalBalance;
    private double fee;

    public Transaction(String aT, int aN, String tT, double iB, double fB, double f){
        accountType = aT;
        accountNumber = aN;
        transactionType = tT;
        initBalance = iB;
        finalBalance = fB;
        fee = f;
    }

    public String getAccountType(){
        return accountType;
    }

    public int getAccountNumber(){
        return accountNumber;
    }

    public String getTransactionType(){
        return transactionType;
    }

    public double getInitBalance(){
        return initBalance;
    }

    public double getFinalBalance() {
        return finalBalance;
    }

    public double getFee() {
        return fee;
    }
}