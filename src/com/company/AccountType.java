package com.company;

import java.awt.*;

public enum AccountType {
    SAVINGACCOUNT("Saving"),
    CHECHINGACCOUNT("Checking"),
    LOANACCOUNT("Loan");

    private final String accountText;
    AccountType(String accountText) {
        this.accountText = accountText;
    }

    @Override
    public String toString() {
        return accountText;
    }
}
