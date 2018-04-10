package com.mannu;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbCon {
	private static Connection con;

	public static Connection getConnection() {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con=DriverManager.getConnection("jdbc:sqlserver://192.168.84.90;user=sa;password=Karvy@123;database=management");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return con;
	}

	
}
