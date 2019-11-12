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

	public SQLConnection(){
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

	public java.sql.Connection getConn(){
		return conn;
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
