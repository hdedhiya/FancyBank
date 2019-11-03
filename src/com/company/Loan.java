package com.company;

//Loan that extends Account, and applies interest appropriately
public class Loan extends Account {
    public Loan(double b, Currency c, double fee, double r){
        super(b,c, fee,r);
        setAccountType(AccountType.LOANACCOUNT);
    }

    @Override
    public Transaction applyInterest() {
        double initB = getBalance();
        if (initB < 0) {
            addBalance(initB * getRate());
            Transaction t = new Transaction(getAccountType(), getIndex(), "Apply Interest", initB, getBalance(), 0);
            addTransaction(t);
            return t;
        }
        else{
            return null;
        }

    }

    @Override
    public String toString() {
        return getAccountType().toString() + this.getIndex() + " Balance: " + this.getBalance();
    }
}
