# JAVA와 ORACLE을 이용한 게시판 프로그램

##사용 기술 <img src="https://img.shields.io/badge/Java-11-pink"> <img src="https://img.shields.io/badge/Oracle-11.2.0.2.0-red"> <br>
기능 : 기본적인 CRUD기능 구현
- 게시판 조회
- 게시글 추가
- 게시글 삭제 
- 게시글 수정
- 게시판 비우기

## 요구사항 명세서

![게시판 개인 포트폴리오_복사본-005](https://user-images.githubusercontent.com/97210509/212597965-f74dc7ff-221e-4993-8c80-f447aae8aee5.png)

## 다이어그램
### 클래스 다이어그램

![image](https://user-images.githubusercontent.com/97210509/208009149-6642351e-8ea7-4139-9ad5-870ca5c511c6.png)

Board와 Main은 서로 의존관계이며, 
Main에서 Board클래스를 참조하고 있음을 보여줍니다.  
참조의 형태는 객체 생성이며 이 참조는 계속 유지하지 않고 있습니다. 

### 시퀀스 다이어그램

![image](https://user-images.githubusercontent.com/97210509/208233552-f55c4b04-fc77-4bb5-bf80-ab3ca6bd37e1.png)

프로그램이 시작되면 Main클래스가 시작되며 이는 종료시까지 활성화됩니다. 
처음에 list()가 시행되며 이 역시 계속 프로그램 종료 직전까지 활성화되게 됩니다. 이 리스트는 Board의 메소드들을 시행하여 값을 가져와 보여주게 됩니다.  그 후 mainMenu를 시행하게 됩니다.

mainMenu에서는 create, read, update, delete, clear, exit의 메소드가 시행되며 각각 Board의 메소드를 사용하여 변수의 내용을 생성, 조회, 수정, 지우기, 모두 지우기의 기능을 시행하게 됩니다. exit의 경우 프로그램이 종료됩니다. 

### 유스케이스 다이어그램

![image](https://user-images.githubusercontent.com/97210509/208329419-b934d394-c61a-46d1-b2d6-f4ca10581256.png)

사용자는 이 프로그램에서 생성, 조회, 수정, 삭제, 모두 삭제, 프로그램 종료의 기능이 있으며 

조회는 게시글이 있어야 가능하며
수정 및 지우기는 해당 글 조회 후
가능하기 때문에 include 관계입니다.
## 코드 분석

### Board.java

``` java 
package ch20.oracle.sec12;

import java.util.Date;

import lombok.Data;

@Data

public class Board {

	private int bno;
	private String btitle;
	private String bcontent;
	private String bwriter;
	private Date bdate;	
		
}
```
게시판의 변수들인 게시물번호, 게시글 제목, 게시글 내용, 작성자, 작성날짜를 담고
Getter와 Setter를 람복을 통해 불러옴
(람복 실행을 위해서는 라이브러리에 lambok.jar 필요함)
게시물 번호 , bno : int
게시물 제목, btitle : String
게시물 내용, bcontet:String
작성자 이름 , bwriter: String
날짜, bdate:Date

![캡처](https://user-images.githubusercontent.com/97210509/208039248-88d4dd86-4737-44f3-91ce-70b607842400.PNG)

***
### Main.java

``` java
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
			exit();
		}
	}
```

스캐너와 OracleDB와 연결할 Connection conn을 생성

Main()생성자를 통해, Oracle과 연결


이때, 오라클 드라이버 ojdbc를 설치를 해야함


***
### Main.java -list()

``` java
public void list() {
		//타이틀 및 컬럼명 출력
		System.out.println();
		System.out.println("[게시물 목록]");
		System.out.println("-----------------------------------------------------------------------");
		System.out.printf("%-6s%-12s%-16s%-40s\n", "no", "writer", "date", "title");
		System.out.println("-----------------------------------------------------------------------");
		
		//boards 테이블에서 게시물 정보를 가져와서 출력하기
		try {
			String sql = "" +
				"SELECT bno, btitle, bcontent, bwriter, bdate " +
				"FROM boards " + 
				"ORDER BY bno DESC";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {		
				Board board = new Board();
				board.setBno(rs.getInt("bno"));
				board.setBtitle(rs.getString("btitle"));
				board.setBcontent(rs.getString("bcontent"));
				board.setBwriter(rs.getString("bwriter"));
				board.setBdate(rs.getDate("bdate"));
				System.out.printf("%-6s%-12s%-16s%-40s \n", 
						board.getBno(), 
						board.getBwriter(),
						board.getBdate(),
						board.getBtitle());
			}
			rs.close();
			pstmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
			exit();
		}
		
		//메인 메뉴 출력
		mainMenu();
	}

```
게시물 목록을 불러옴

쿼리문을 String으로 입력 후, 실행시킴

Board의 bno, btitle, bcontetn, bwriter, bdate를 통해 가져옴, 
에러발생시, 프로그램 종료

***

### Main.java - mainMenu()

``` java
public void mainMenu() {
		System.out.println();
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("메인 메뉴: 1.Create | 2.Read | 3.Clear | 4.Exit");
		System.out.print("메뉴 선택: ");
		String menuNo = scanner.nextLine();
		System.out.println();

		switch (menuNo) {
		case "1" -> create();
		case "2" -> read();
		case "3" -> clear();
		case "4" -> exit();
		}
	}
```
메인메뉴를 불러오고
사용자의 입력값을 받아
case문을 통해 시행

![image](https://user-images.githubusercontent.com/97210509/208328930-74e518bd-2980-404b-8fc5-ce5d6f399002.png)


***

### Main.java - create()

``` java
 public void create() {
		// 입력 받기
		Board board = new Board();
		System.out.println("[새 게시물 입력]");
		System.out.print("제목: ");
		board.setBtitle(scanner.nextLine());
		System.out.print("내용: ");
		board.setBcontent(scanner.nextLine());
		System.out.print("작성자: ");
		board.setBwriter(scanner.nextLine());

		// 보조 메뉴 출력
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("보조 메뉴: 1.Ok | 2.Cancel");
		System.out.print("메뉴 선택: ");
		String menuNo = scanner.nextLine();
		if (menuNo.equals("1")) {
			// boards 테이블에 게시물 정보 저장
			try {
				String sql = "" + "INSERT INTO boards (bno, btitle, bcontent, bwriter, bdate) "
						+ "VALUES (SEQ_BNO.NEXTVAL, ?, ?, ?, SYSDATE)";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, board.getBtitle());
				pstmt.setString(2, board.getBcontent());
				pstmt.setString(3, board.getBwriter());
				pstmt.executeUpdate();
				pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
		}

		// 게시물 목록 출력
		list();
	} 
```

새게시물의 제목, 내용, 작성자를 입력받아
쿼리문(select문)을 pstmt로 하여금 실행시킴

이때 ?와 값들을 바인딩함

오류발생시 exit()함수 시행

![image](https://user-images.githubusercontent.com/97210509/208328952-6690817b-41bf-4cdb-b960-7007a15e79f0.png)



***

### Main.java - read()
``` java
public void read() {
		// 입력 받기
		System.out.println("[게시물 읽기]");
		System.out.print("bno: ");
		int bno = Integer.parseInt(scanner.nextLine());

		// boards 테이블에서 해당 게시물을 가져와 출력
		try {
			String sql = "" + "SELECT bno, btitle, bcontent, bwriter, bdate " + "FROM boards " + "WHERE bno=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bno);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Board board = new Board();
				board.setBno(rs.getInt("bno"));
				board.setBtitle(rs.getString("btitle"));
				board.setBcontent(rs.getString("bcontent"));
				board.setBwriter(rs.getString("bwriter"));
				board.setBdate(rs.getDate("bdate"));
				System.out.println("#############");
				System.out.println("번호: " + board.getBno());
				System.out.println("제목: " + board.getBtitle());
				System.out.println("내용: " + board.getBcontent());
				System.out.println("작성자: " + board.getBwriter());
				System.out.println("날짜: " + board.getBdate());
				// 보조 메뉴 출력
				System.out.println("-------------------------------------------------------------------");
				System.out.println("보조 메뉴: 1.Update | 2.Delete | 3.List");
				System.out.print("메뉴 선택: ");
				String menuNo = scanner.nextLine();
				System.out.println();

				if (menuNo.equals("1")) {
					update(board);
				} else if (menuNo.equals("2")) {
					delete(board);
				}
			}
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}

		// 게시물 목록 출력
		list();
	}
```
읽을 게시물의 번호를 사용자에게 입력받아
쿼리문(조회)을 pstmt로 하여금 실행 시킴

이때, bno와 ?를 바인딩시킨 후

Board의 변수드를 getter와 setter를 통해
가져와 조회 가능케 하고

수정, 삭제, 리스트 조회를 보조메뉴를 통해
시행케 함.
 
![image](https://user-images.githubusercontent.com/97210509/208328968-c7c5ecdd-3e10-4986-acac-2c3aad80c8e5.png)


***

### Main.java - update(Board board)

``` java
public void update(Board board) {
		// 수정 내용 입력 받기
		System.out.println("[수정 내용 입력]");
		System.out.print("제목: ");
		board.setBtitle(scanner.nextLine());
		System.out.print("내용: ");
		board.setBcontent(scanner.nextLine());
		System.out.print("작성자: ");
		board.setBwriter(scanner.nextLine());

		// 보조 메뉴 출력
		System.out.println("-------------------------------------------------------------------");
		System.out.println("보조 메뉴: 1.Ok | 2.Cancel");
		System.out.print("메뉴 선택: ");
		String menuNo = scanner.nextLine();
		if (menuNo.equals("1")) { // boards 테이블에서 게시물 정보 수정
			try {
				String sql = "" + "UPDATE boards SET btitle=?, bcontent=?, bwriter=? " + "WHERE bno=?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, board.getBtitle());
				pstmt.setString(2, board.getBcontent());
				pstmt.setString(3, board.getBwriter());
				pstmt.setInt(4, board.getBno());
				pstmt.executeUpdate();
				pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
		}

		// 게시물 목록 출력
		list();
	}
```
수정할 게시물을 read에서  입력받은 것을 전달받아
쿼리문(update문)을 pstmt로 하여금 실행 시킴

이때, bno와 ?를 바인딩시킨 후

Board의 변수들을 getter와 setter를 통해
가져와 대입시키고 

보조메뉴를 통해 한 번 더 확인 후
시행케 함.

![image](https://user-images.githubusercontent.com/97210509/208328974-c69b7888-3e4d-4309-9eb4-545c182029f6.png)

 
***

### Main.java - delete(Board board)

``` java
public void delete(Board board) {
		// boards 테이블에 게시물 정보 삭제
		try {
			String sql = "DELETE FROM boards WHERE bno=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, board.getBno());
			pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
		}

		// 게시물 목록 출력
		list();
	}
```
삭제할 게시물의 번호를 사용자에게 입력받은
read()로부터 전달받아 
쿼리문(delete문)을 pstmt로 하여금 실행 시킴

이때, bno와 ?를 바인딩시킨 후

executeUpdate를 통해 쿼리문 실행 

![image](https://user-images.githubusercontent.com/97210509/208328984-af3a8d4e-1969-4b79-9a66-d54aab11f1ef.png)


***

### Main.java - clear()

``` java
public void clear() {
		System.out.println("[게시물 전체 삭제]");
		System.out.println("-------------------------------------------------------------------");
		System.out.println("보조 메뉴: 1.Ok | 2.Cancel");
		System.out.print("메뉴 선택: ");
		String menuNo = scanner.nextLine();
		if (menuNo.equals("1")) {
			// boards 테이블에 게시물 정보 전체 삭제
			try {
				String sql = "TRUNCATE TABLE boards";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.executeUpdate();
				pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
				exit();
			}
```

쿼리문(truncate문)을 pstmt로 하여금 실행 시키고
게시물 목록을 출력함

![image](https://user-images.githubusercontent.com/97210509/208328990-c42f6f89-0318-44be-9e34-5bc2251479d3.png)

