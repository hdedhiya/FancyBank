package com.company;

import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLConnection {
	//change the "test" here to the name of your own database
	private static final String URL="jdbc:mysql://localhost:3306/fancybank?useUnicode=true&characterEncoding=utf8";
	
	private static final String NAME="root";//username
	private static final String PASSWORD="xjz950724";//password
	public java.sql.Connection conn = null;
	
	public void TheSqlConnection(){
	    //1.load the driver
	    try {
	        Class.forName("com.mysql.jdbc.Driver");
	    } catch (ClassNotFoundException e) {
	        System.out.println("Failed to load the driver");
	        e.printStackTrace();
	    }try {
	        conn = DriverManager.getConnection(URL, NAME, PASSWORD);
	        System.out.println("Successfully connected!");    
	
	    }catch (SQLException e){
	        System.out.println("Failed to connect to mysql!");
	        //check your username and password
	        e.printStackTrace();
	    }
	}
	
	//user login
	public Person getUser(String u, String p) {
		//just for testing
		String sql = "select * from user where username = '" + u + "' and password = '" + p + "'"; 
		PreparedStatement pst = null;
		Person user = null;
		try {
		    pst = (PreparedStatement) conn.prepareStatement(sql);
		    java.sql.ResultSet rs = pst.executeQuery();
		    while (rs.next()) {
		    	String username = rs.getString("username");
		    	String password = rs.getString("password");
		        String type = rs.getString("type");
		        if(type.equals("customer")) {
		        	user = new BankCustomer(username, password);
		        }else if(type.equals("banker")) {
		        	user = new Banker(username, password);
		        }
		        user.setType(type);
		       }
		}catch (Exception e) {
		    System.out.println("don't get any");
		}
		return user;
	}
	
	//user register
	public boolean addUser(String u, String p, String t) {
		boolean success = true;
		String sql = "insert into user (username, password, type) values (?, ?, ?)";
		PreparedStatement pst = null;
		try {
			pst = (PreparedStatement)conn.prepareStatement(sql);
			pst.setString(1, u);
	        pst.setString(2, p);
	        pst.setString(3, t);
			pst.execute();
		}catch(Exception e) {
			System.out.println("Failed to add " + t + " !");
			success = false;
		}
		return success;
	}
	
	/**
	 * Customer operations
	 * */
		
	//change customer password
	public void changeCustomerPW(String u, String p) {
		String sql = "update customer set password = '" + p + "' where username = '" + u + "'"; 
	    PreparedStatement pst = null;
	    try {
	        pst = (PreparedStatement) conn.prepareStatement(sql);
	        pst.execute();
	    }catch (Exception e) {
	        System.out.println("Failed to change cutomer's password!");
	   	}
	}
	
	//get customers accounts
	public ArrayList<Account> getAccounts(String u){
		String sql = "select * from account where username = '" + u + "'"; 
	    PreparedStatement pst = null;
	    List<Account> accounts = new ArrayList<Account>();
	    try {
	        pst = (PreparedStatement) conn.prepareStatement(sql);
	        java.sql.ResultSet rs = pst.executeQuery();
	        while (rs.next()) {
	        	Account account = null;
	        	String accountType = rs.getString("accountType");
	        	if(accountType.equals("Checking")) {
	        		account = new Checking("Checking");
	        	}else if(accountType.equals("Savings")) {
	        		account = new Savings("Savings");
	        	}else if(accountType.equals("Loan")) {
	        		account = new Loan("Loan");
	        	}
	        	int accountNum = rs.getInt("accountNum");
	        	double balance = Double.parseDouble(rs.getString("balance"));
	        	account.setIndex(accountNum);
	        	account.setBalance(balance);;
	            accounts.add(account);
	        }
	    }catch (Exception e) {
	        System.out.println("don't get any");
	   	}
	    return (ArrayList<Account>) accounts;
	}
	
	//add customer account
	public void addAccount(String accountType, String username, String rate, String balance) {
		String sql = "insert into account (accountType, username, rate, balance) values (?, ?, ?, ?)"; 
	    PreparedStatement pst = null;
	    Account account = null;
	    try {
	        pst = (PreparedStatement) conn.prepareStatement(sql);
	        pst.setString(1, accountType);
	        pst.setString(2, username);
	        pst.setString(3, rate);
	        pst.setString(4, balance);
	        pst.execute();
	    }catch (Exception e) {
	        System.out.println("Failed to add cutomer's account!");
	   	}
	}
	
	//get a specific account
	public Account getAccount(int accountNum) {
		String sql = "select * from account where accountNum = '" + accountNum + "'";
		PreparedStatement pst = null;
		Account account = null;
		try {
			pst = (PreparedStatement) conn.prepareStatement(sql);
			java.sql.ResultSet rs = pst.executeQuery();
	        while (rs.next()) {
	        	String accountType = rs.getString("accountType");
	        	if(accountType.equals("Checking")) {
	        		account = new Checking("Checking");
	        	}else if(accountType.equals("Savings")) {
	        		account = new Savings("Savings");
	        	}else if(accountType.equals("Loan")) {
	        		account = new Loan("Loan");
	        	}
	        	double balance = Double.parseDouble(rs.getString("balance"));
	        	account.setIndex(accountNum);
	        	account.setBalance(balance);
	        }
		}catch (Exception e) {
	        System.out.println("don't get any");
	   	}
		return account;
	}
	
	//delete a specific account
	public void deleteAccount(int accountNum){
	    String sql = "delete from account where accountNum = '" + accountNum + "'";
	    PreparedStatement pst = null;
	    try {
	    	pst = (PreparedStatement) conn.prepareStatement(sql);
	    	pst.execute();
	    }catch (Exception e) {
	        System.out.println("Failed to delete account " + accountNum);
	   	}
	}
	
	//deposit and withdraw
	public void updateAccount(int accountNum, double amt){
		String sql = "update account set balance = '" + amt + "' where accountNum = '" + accountNum + "'";
	    PreparedStatement pst = null;
	    try {
	    	pst = (PreparedStatement) conn.prepareStatement(sql);
	    	pst.execute();
	    }catch (Exception e) {
	        System.out.println("Failed to update account " + accountNum);
	   	}
	}
	
	//add transaction
	public void addTransaction(int accountNum, String accountType, String transactionType, String initBalance, String finalBalance, String fee) {
		long millis=System.currentTimeMillis();
		java.sql.Date d=new java.sql.Date(millis);
		String sql = "insert into transaction (accountNum, accountType, transactionType, initBalance, finalBalance, fee, date) values (?, ?, ?, ?, ?, ?, ?)";
	    PreparedStatement pst = null;
	    Account account = null;

		try {
	        pst = (PreparedStatement) conn.prepareStatement(sql);
	        pst.setString(1, Integer.toString(accountNum));
	        pst.setString(2, accountType);
	        pst.setString(3, transactionType);
	        pst.setString(4, initBalance);
	        pst.setString(5, finalBalance);
	        pst.setString(6, fee);
	        pst.setDate(7, d);
	        pst.execute();
	    }catch (Exception e) {
	        System.out.println("Failed to add cutomer's transaction!");
	   	}
	}
	
	//get transactions
	public ArrayList<Transaction> getTransactions(int acNum){
		String sql = "select * from transaction where accountNum = '" + acNum + "'"; 
	    PreparedStatement pst = null;
	    List<Transaction> transactions = new ArrayList<Transaction>();
	    try {
	        pst = (PreparedStatement) conn.prepareStatement(sql);
	        java.sql.ResultSet rs = pst.executeQuery();
	        while (rs.next()) {
	        	Transaction transaction = null;
	        	String accountType = rs.getString("accountType");
	        	int accountNum = rs.getInt("accountNum");
	        	String transactionType = rs.getString("transactionType");
	        	double initBalance = rs.getDouble("initBalance");
	        	double finalBalance = rs.getDouble("finalBalance");
	        	double fee = rs.getDouble("fee");
	        	Date date = rs.getDate("date");
	        	transaction = new Transaction(accountType, accountNum, transactionType, initBalance, finalBalance, fee, date);
	            transactions.add(transaction);
	        }
	    }catch (Exception e) {
	        System.out.println("don't get any");
	   	}
	    return (ArrayList<Transaction>) transactions;
	}

	
	
	/**
	 * Banker operations
	 * */
	
	//get banker

	public void applySavingsInterest(double interestRate, double minBalance){
		String sql = "select * from account where accountType = 'Savings'";
		PreparedStatement pst = null;
		List<Account> accounts = new ArrayList<Account>();
		try {
			pst = (PreparedStatement) conn.prepareStatement(sql);
			java.sql.ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				double balance = Double.parseDouble(rs.getString("balance"));
				if (balance > minBalance){
					updateAccount(rs.getInt("accountNum"), balance + balance*interestRate);
					addTransaction(rs.getInt("accountNum"), "Savings", "Apply Interest", String.valueOf(balance), String.valueOf(balance + balance*interestRate), String.valueOf(0));
				}
			}
		}catch (Exception e) {
			System.out.println("don't get any");
		}
	}

	public void applyLoanInterestRate(double interestRate){
		String sql = "select * from account where accountType = 'Loan'";
		PreparedStatement pst = null;
		List<Account> accounts = new ArrayList<Account>();
		try {
			pst = (PreparedStatement) conn.prepareStatement(sql);
			java.sql.ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				double balance = Double.parseDouble(rs.getString("balance"));
				if (balance < 0) {
					updateAccount(rs.getInt("accountNum"), balance + balance * interestRate);
					addTransaction(rs.getInt("accountNum"), "Loan", "Apply Interest", String.valueOf(balance), String.valueOf(balance + balance * interestRate), String.valueOf(0));
				}
			}
		}catch (Exception e) {
			System.out.println("don't get any");
		}
	}

	public ArrayList<Transaction> queryTransactionsByDate(Date d){
		String sql = "select * from transaction where date = '" + d + "'";
		PreparedStatement pst = null;
		List<Transaction> transactions = new ArrayList<Transaction>();
		try {
			pst = (PreparedStatement) conn.prepareStatement(sql);
			java.sql.ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				Transaction transaction = null;
				String accountType = rs.getString("accountType");
				int accountNum = rs.getInt("accountNum");
				String transactionType = rs.getString("transactionType");
				double initBalance = rs.getDouble("initBalance");
				double finalBalance = rs.getDouble("finalBalance");
				double fee = rs.getDouble("fee");
				Date date = rs.getDate("date");
				transaction = new Transaction(accountType, accountNum, transactionType, initBalance, finalBalance, fee, date);
				transactions.add(transaction);
			}
		}catch (Exception e) {
			System.out.println("don't get any");
		}
		return (ArrayList<Transaction>) transactions;
	}


	
	public void insert(){
	    
	}
	
	public void close(){
		//shut down the connection
		if(conn!=null){
	        try {
	            conn.close();
	        }catch (SQLException e){
	        // TODO Auto-generated catch block
	            e.printStackTrace();
	            conn = null;
	        }
	     }
	}
}
