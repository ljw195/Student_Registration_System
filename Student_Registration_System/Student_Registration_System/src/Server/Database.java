package Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	private static String dbOldUrl = "jdbc:mysql://localhost:3306/course_catalog";
	private static String dbNewUrl = "jdbc:mysql://localhost:3306/administration_system?serverTimezone=UTC";
	private static String dbUser = "root";
	private static String dbPwd = "aptx4869";
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	// ���һ�������ݿ�����
	public static Connection getOldConnection() throws SQLException {
		System.out.println("�������ݿ�...");
		return DriverManager.getConnection(dbOldUrl, dbUser, dbPwd);
	}

	// ���һ�������ݿ�����
	public static Connection getNewConnection() throws SQLException {
		System.out.println("�������ݿ�...");
		return DriverManager.getConnection(dbNewUrl, dbUser, dbPwd);
	}

	// �ر����ݿ�����
	public static void freeDB(ResultSet rs, Statement st, Connection conn) throws SQLException {

		if (rs != null)
			rs.close();
		if (st != null)
			st.close();
		if (conn != null)
			conn.close();
		System.out.println("�ر����ݿ�");
	}
}