package com.company;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

//class that defines an account with basic methods with abstract methods to fill in later in Checking, Savings, and Loan
public abstract class Account {
    private double balance;
    private double rate;
    private static Integer count = 0;
    private Integer index;
    private LinkedHashMap<Integer, Transaction> ts;
    private Integer transactionCounter;
    private AccountType accountType;

    public Account(AccountType accountType) {
    	this.accountType = accountType;
    }
    
    public Account(double b, Currency c, double fee, double r){
        transactionCounter = 0;
        balance = (b * c.getConversionToBaseRate()) - fee;
        index = count;
        count += 1;
        ts = new LinkedHashMap();
        rate = r;
    }

    public double getBalance() {
        return balance;
    }

    public double getRate(){
        return rate;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public AccountType getAccountType(){
        return accountType;
    }

    public void setAccountType(AccountType aT){
        accountType = aT;
    }

    public void addBalance(double b){
        balance += b;
    }

    public void removeBalance(double b){
        balance -= b;
    }

    public void setIndex(int index) {
    	this.index = index;
    }
    
    public Integer getIndex(){
        return index;
    }

    public abstract String toString();

    //static functions that query the DB

    public static boolean addTransaction(int accountNum, AccountType accountType, String transactionType, String initBalance, String finalBalance, String fee) {
        long millis=System.currentTimeMillis();
        java.sql.Date d=new java.sql.Date(millis);
        String sql = "insert into transaction (accountNum, accountType, transactionType, initBalance, finalBalance, fee, date) values (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = null;
        Account account = null;
        SQLConnection sc = new SQLConnection();

        try {
            pst = (PreparedStatement) sc.getConn().prepareStatement(sql);
            pst.setString(1, Integer.toString(accountNum));
            pst.setString(2, accountType.toString());
            pst.setString(3, transactionType);
            pst.setString(4, initBalance);
            pst.setString(5, finalBalance);
            pst.setString(6, fee);
            pst.setDate(7, d);
            pst.execute();
            sc.close();
            return true;
        }catch (Exception e) {
            System.out.println("Failed to add cutomer's transaction!");
            return false;
        }
    }

    //get transactions
    public static ArrayList<Transaction> getTransactions(int acNum){
        String sql = "select * from transaction where accountNum = '" + acNum + "'";
        PreparedStatement pst = null;
        List<Transaction> transactions = new ArrayList<Transaction>();
        SQLConnection sc = new SQLConnection();
        try {
            pst = (PreparedStatement) sc.getConn().prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Transaction transaction = null;
                String aT = rs.getString("accountType");
                AccountType accountType = AccountType.getTypeByString(aT);
                int accountNum = rs.getInt("accountNum");
                String transactionType = rs.getString("transactionType");
                double initBalance = rs.getDouble("initBalance");
                double finalBalance = rs.getDouble("finalBalance");
                double fee = rs.getDouble("fee");
                Date date = rs.getDate("date");
                transaction = new Transaction(accountType, accountNum, transactionType, initBalance, finalBalance, fee, date);
                transactions.add(transaction);
            }
            sc.close();
        }catch (Exception e) {
            System.out.println("don't get any");
        }
        return (ArrayList<Transaction>) transactions;
    }

    public static boolean applySavingsInterest(double interestRate, double minBalance){
        String sql = "select * from account where accountType = 'Savings'";
        PreparedStatement pst = null;
        List<Account> accounts = new ArrayList<Account>();
        SQLConnection sc = new SQLConnection();
        try {
            pst = (PreparedStatement) sc.getConn().prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                double balance = Double.parseDouble(rs.getString("balance"));
                if (balance > minBalance){
                    //updateAccount(rs.getInt("accountNum"), balance + balance*interestRate);
                    addTransaction(rs.getInt("accountNum"), AccountType.SAVINGACCOUNT, "Apply Interest", String.valueOf(balance), String.valueOf(balance + balance*interestRate), String.valueOf(0));
                }
            }
            sc.close();
            return true;
        }catch (Exception e) {
            System.out.println("don't get any");
            return false;
        }
    }

    public static boolean applyLoanInterestRate(double interestRate){
        String sql = "select * from account where accountType = 'Loan'";
        PreparedStatement pst = null;
        List<Account> accounts = new ArrayList<Account>();
        SQLConnection sc = new SQLConnection();
        try {
            pst = (PreparedStatement) sc.getConn().prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                double balance = Double.parseDouble(rs.getString("balance"));
                if (balance < 0) {
                    //updateAccount(rs.getInt("accountNum"), balance + balance * interestRate);
                    addTransaction(rs.getInt("accountNum"), AccountType.LOANACCOUNT, "Apply Interest", String.valueOf(balance), String.valueOf(balance + balance * interestRate), String.valueOf(0));
                }
            }
            sc.close();
            return true;
        }catch (Exception e) {
            System.out.println("don't get any");
            return false;
        }
    }

}
