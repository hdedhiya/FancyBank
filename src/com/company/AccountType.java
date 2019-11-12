package com.company;

public enum AccountType {
    //Use enum instead of strings for accountType
    SAVINGACCOUNT("Saving"),
    CHECKINGACCOUNT("Checking"),
    LOANACCOUNT("Loan");

    private final String accountText;
    AccountType(String accountText) {
        this.accountText = accountText;
    }

    @Override
    public String toString() {
        return accountText;
    }

    public static AccountType getTypeByString(String s) {
        if (s.equals("Saving")) return AccountType.SAVINGACCOUNT;
        else if (s.equals("Checking")) return AccountType.CHECKINGACCOUNT;
        else return AccountType.LOANACCOUNT;
    }
}
