package Boardex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class Main {
	// Field
		private Scanner scanner = new Scanner(System.in);
		private Connection conn;

		// Constructor
		public Main() {
			try {
				Class.forName("oracle.jdbc.OracleDriver");
				conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xe", "java", "1234");
			} catch (Exception e) {
				e.printStackTrace();
//				exit();
			}
		}

		public void list() {
			System.out.println("");
			System.out.println("[게시물 목록]");
			System.out.println("-----------------------------------------------------------------------");
			System.out.printf("%-6s%-12s%-16s%-40s","no","writer","date","title");
			
		}
	public static void main(String[] args) {

		
	}

}
