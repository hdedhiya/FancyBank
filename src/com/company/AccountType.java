package com.company;

import java.awt.*;

public enum AccountType {
    SAVINGACCOUNT("Saving Account"),
    CHECHINGACCOUNT("Checking Account"),
    LOANACCOUNT("Loan Account"),
    SECURITYACCOUNT("Security Account");

    private final String accountText;
    AccountType(String accountText) {
        this.accountText = accountText;
    }

    @Override
    public String toString() {
        return accountText;
    }
}
