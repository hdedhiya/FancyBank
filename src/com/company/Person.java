package com.company;

import java.sql.PreparedStatement;

//Generic Person with username and password
public class Person {
    private String username;
    private String password;
    private String type;

    public Person (String u, String p){
        username = u;
        password = p;
        type = "";
    }

    public String getUsername() {
        return username;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String p){
        password = p;
    }

	public String getType() {
		return type;
	}

	public void setType(String t) {
		type = t;
	}

    public static Person getUser(String u, String p) {
        //just for testing
        String sql = "select * from user where username = '" + u + "' and password = '" + p + "'";
        PreparedStatement pst = null;
        Person user = null;
        SQLConnection sc = new SQLConnection();
        try {
            pst = (PreparedStatement) sc.getConn().prepareStatement(sql);
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
            sc.close();
        }catch (Exception e) {
            System.out.println("don't get any");
        }
        return user;
    }

    public static boolean addUser(String u, String p, String t) {
        boolean success = true;
        String sql = "insert into user (username, password, type) values (?, ?, ?)";
        PreparedStatement pst = null;
        SQLConnection sc = new SQLConnection();
        try {
            pst = (PreparedStatement) sc.getConn().prepareStatement(sql);
            pst.setString(1, u);
            pst.setString(2, p);
            pst.setString(3, t);
            pst.execute();
            sc.close();
        }catch(Exception e) {
            System.out.println("Failed to add " + t + " !");
            success = false;
        }
        return success;
    }

    public static boolean changeCustomerPW(String u, String p) {
        String sql = "update user set password = '" + p + "' where username = '" + u + "'";
        System.out.println(sql);
        PreparedStatement pst = null;
        SQLConnection sc = new SQLConnection();
        try {
            pst = (PreparedStatement) sc.getConn().prepareStatement(sql);
            pst.execute();
            sc.close();
            return true;
        }catch (Exception e) {
            //e.getStackTrace();
            System.out.println("Failed to change customer's password!");
            return false;
        }
    }
    
}
