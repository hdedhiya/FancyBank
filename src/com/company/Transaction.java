package com.company;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

//Transaction class that is used in account and bank to keep track of changes.
public class Transaction {
    private AccountType accountType;
    private int accountNumber;
    private String transactionType;
    private double initBalance;
    private double finalBalance;
    private double fee;
    private Date date;

    public Transaction(AccountType aT, int aN, String tT, double iB, double fB, double f, Date d){
        accountType = aT;
        accountNumber = aN;
        transactionType = tT;
        initBalance = iB;
        finalBalance = fB;
        fee = f;
        date = d;
    }

    public AccountType getAccountType(){
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

    public Date getDate(){
        return date;
    }

    public static ArrayList<Transaction> queryTransactionsByDate(Date d){
        String sql = "select * from transaction where date = '" + d + "'";
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
}
