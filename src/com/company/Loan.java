package com.company;

//Loan that extends Account
public class Loan extends Account {
    public Loan(double b, Currency c, double fee, double r){
        super(b,c, fee,r);
        setAccountType(AccountType.LOANACCOUNT);
    }
    
    public Loan(AccountType accountType) {
    	super(accountType);
    }

    @Override
    public String toString() {
        return "Loan Account " + this.getIndex() + " Balance: " + this.getBalance();
    }
}
