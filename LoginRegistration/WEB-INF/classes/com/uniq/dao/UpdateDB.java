package com.uniq.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.uniq.connection.ConnectionManager;



public class UpdateDB {
	
	Connection conn = ConnectionManager.getConnection();
	
	public void registration(String email,String password, String username, String phone){
		try {
			Statement st = conn.createStatement();
			st.execute("INSERT INTO usertable(username, password, email, phone) VALUES('"+username+"', '"+password+"', '"+email+"', '"+phone+"')");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public boolean login(String email,String password, String gcm_id){
		boolean success = false;
		try {
			Statement st = conn.createStatement();
			st.execute("UPDATE usertable SET gcm_id='"+gcm_id+"' WHERE email='"+email+"'");
			ResultSet rs = st.executeQuery("SELECT password FROM usertable WHERE email = '"+email+"'");
			while(rs.next()){
				if(rs.getString("password").equals(password)){
					success = true;
				}
				else{
					success = false;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
		
	}
	
	public void updateGcm(String gcm_id) {
		
	}
	
	public String getGcm(String email) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			Statement st = conn.createStatement();
			ResultSet rt = st.executeQuery("SELECT * FROM usertable WHERE email='"+email+"'");
			if(rt != null){
				
				while(rt .next()){
					list.add(rt.getString("gcm_id"));
				}
				System.out.println("Location...."+list.toString());
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list.toString(); 
	}

}
