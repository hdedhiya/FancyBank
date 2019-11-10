package com.company;

import java.util.Collection;

import java.util.LinkedHashMap;

//Customer that has a set of accounts at the bank, and can manipulate the accounts
public class BankCustomer extends Customer {

    private LinkedHashMap<Integer, Account> accountList;

    public BankCustomer (String u, String p) {
        super(u, p);
        accountList = new LinkedHashMap<>();
    }

    public void addAccount(Integer accountNum, Account a){
        accountList.put(accountNum, a);
    }

    public Account getAccount(Integer accountNum){
        return accountList.get(accountNum);
    }

//    public Transaction removeAccount(Integer accountNum, double fee){
//        Account temp = accountList.remove(accountNum);
//        if (temp == null){
//            return null;
//        }
//        else{
//            return new Transaction(temp.getAccountType(), temp.getIndex(), "Account Deletion", temp.getBalance(), 0, fee);
//        }
//    }

    public Collection<Account> getAllAccounts(){
        return accountList.values();
    }

    public void sellAllStocks() {
        //todo
        //bank customer has the feature to sell all stocks
        System.out.println("sellAll function in development");
    }

    public void viewStockProfit() {
        //todo
        //bank customer has the feature to view all stock profit
        System.out.println("viewProfit function in development");


    }
}
