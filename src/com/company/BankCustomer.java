package com.company;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;

import java.util.LinkedHashMap;
import java.util.List;

//Customer that has a set of accounts at the bank, and can manipulate the accounts
public class BankCustomer extends Customer {

    public BankCustomer (String u, String p) {
        super(u, p);
    }

    //define static methods that interact with db
    public static ArrayList<Account> getAccounts(String u){
        String sql = "select * from account where username = '" + u + "'";
        PreparedStatement pst = null;
        List<Account> accounts = new ArrayList<Account>();
        SQLConnection sc = new SQLConnection();
        try {
            pst = (PreparedStatement) sc.getConn().prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Account account = null;
                String aT = rs.getString("accountType");
                AccountType accountType = AccountType.getTypeByString(aT);
                if(accountType == AccountType.CHECKINGACCOUNT) {
                    account = new Checking(accountType);
                }else if(accountType == AccountType.SAVINGACCOUNT) {
                    account = new Savings(accountType);
                }else if(accountType == AccountType.LOANACCOUNT) {
                    account = new Loan(accountType);
                }
                int accountNum = rs.getInt("accountNum");
                double balance = Double.parseDouble(rs.getString("balance"));
                account.setIndex(accountNum);
                account.setBalance(balance);;
                accounts.add(account);
            }
            sc.close();
        }catch (Exception e) {
            System.out.println("don't get any");
        }
        return (ArrayList<Account>) accounts;
    }

    //add customer account
    public static boolean addAccount(AccountType accountType, String username, String rate, String balance) {
        String sql = "insert into account (accountType, username, rate, balance) values (?, ?, ?, ?)";
        PreparedStatement pst = null;
        Account account = null;
        SQLConnection sc = new SQLConnection();
        try {
            pst = (PreparedStatement) sc.getConn().prepareStatement(sql);
            pst.setString(1, accountType.toString());
            pst.setString(2, username);
            pst.setString(3, rate);
            pst.setString(4, balance);
            pst.execute();
            sc.close();
            return true;
        }catch (Exception e) {
            System.out.println("Failed to add customer's account!");
            return false;
        }
    }

    //get a specific account
    public static Account getAccount(int accountNum) {
        String sql = "select * from account where accountNum = '" + accountNum + "'";
        PreparedStatement pst = null;
        Account account = null;
        SQLConnection sc = new SQLConnection();
        try {
            pst = (PreparedStatement) sc.getConn().prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String aT = rs.getString("accountType");
                AccountType accountType = AccountType.getTypeByString(aT);
                if(accountType == AccountType.CHECKINGACCOUNT) {
                    account = new Checking(accountType);
                }else if(accountType == AccountType.SAVINGACCOUNT) {
                    account = new Savings(accountType);
                }else if(accountType == AccountType.LOANACCOUNT) {
                    account = new Loan(accountType);
                }
                double balance = Double.parseDouble(rs.getString("balance"));
                account.setIndex(accountNum);
                account.setBalance(balance);
            }
            sc.close();
        }catch (Exception e) {
            System.out.println("don't get any");
        }
        return account;
    }

    //delete a specific account
    public static boolean deleteAccount(int accountNum){
        String sql = "delete from account where accountNum = '" + accountNum + "'";
        PreparedStatement pst = null;
        SQLConnection sc = new SQLConnection();
        try {
            pst = (PreparedStatement) sc.getConn().prepareStatement(sql);
            pst.execute();
            sc.close();
            return true;
        }catch (Exception e) {
            System.out.println("Failed to delete account " + accountNum);
            return false;
        }
    }

    //deposit and withdraw
    public static boolean updateAccount(int accountNum, double amt){
        String sql = "update account set balance = '" + amt + "' where accountNum = '" + accountNum + "'";
        System.out.println(sql);
        PreparedStatement pst = null;
        SQLConnection sc = new SQLConnection();
        try {
            pst = (PreparedStatement) sc.getConn().prepareStatement(sql);
            pst.execute();
            sc.close();
            return true;
        }catch (Exception e) {
            System.out.println("Failed to update account " + accountNum);
            return false;
        }
    }
}
