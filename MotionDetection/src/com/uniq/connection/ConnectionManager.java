package com.uniq.connection;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionManager {
	static Connection conn = null;
	static String url = "jdbc:mysql://localhost:3306/login";
	
	//static String url = "jdbc:mysql://ec2-23-21-211-172.compute-1.amazonaws.com/theft";
/**
 * Credentials of Database.
 * @return
 */
	public static Connection getConnection() {

		try {
			Class.forName("com.mysql.jdbc.Driver");

			String username = "root"; // "theft";
			String password = "root"; // "theft";

			conn = DriverManager.getConnection(url, username, password);

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

}
