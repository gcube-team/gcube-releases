package org.gcube.dataaccess.test;
//package org.gcube.dataanalysis.test;
//
//import java.util.Scanner;
//import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
//import org.gcube.dataanalysis.databases.lexer.MySQLLexicalAnalyzer;
//import org.gcube.dataanalysis.databases.lexer.PostgresLexicalAnalyzer;
//
//
///** Class that tests the query's parsing by means of a lexical analyzer. It allows to filter a query no read-only compliant. */
//public class TestParsing {
//
//	
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//
//		 String query="INSERT INTO ANTIQUES VALUES (21, 01, 'Ottoman', 200.00);";
//
//		// String query =
//		// "SELECT COUNT(*) FROM EMPLOYEESTATISTICSTABLE WHERE POSITION = 'Staff';";
//		// String
//		// query="SELECT COUNT(*) FROM EMPLOYEESTATISTICSTABLE WHERE POSITION = 'Staff';";
//
//		// String query =
//		// "SELECT EMPLOYEEIDNO FROM EMPLOYEESTATISTICSTABLE WHERE SALARY<40000 OR BENEFITS<10000;";
//
//		// String
//		// query="SELECT EMPLOYEEIDNO FROM EMPLOYEESTATISTICSTABLE WHERE POSITION='Manager' AND SALARY>60000 OR BENEFITS>12000;";
//
//		// String
//		// query="SELECT EMPLOYEEIDNO FROM EMPLOYEESTATISTICSTABLE WHERE SALARY>=50000;";
//
//		// String
//		// query="SELECT EMPLOYEEIDNO FROM EMPLOYEESTATISTICSTABLE WHERE SALARY<=50000;";
//
//		// String
//		// query="SELECT SUM(SALARY), AVG(SALARY) FROM EMPLOYEESTATISTICSTABLE;";
//
//		// String
//		// query="SELECT FirstName, LastName, Address, City, State FROM EmployeeAddressTable;";
//
//		// String
//		// query="SELECT EMPLOYEEIDNO FROM EMPLOYEESTATISTICSTABLE WHERE SALARY<>50000;";
//
//		// String query="INSERT   INTO ANTIQUES from select * from hcaf_d;";
//
//		// String query = "COPY lori FROM lavoro";
//
////		String query = "COPY lori FROM \"lavoro\";";
//
//		 
//		 
//
//		// String query="SELECT TITLE, DIRECTOR "
//		// +"FROM MOVIE "
//		// +"WHERE MOVIE_ID IN"
//		// +"("
//		// +"("
//		// +"SELECT MOVIE_ID "
//		// +"FROM ACTOR "
//		// +"WHERE NAME=?Tom Cruise? "
//		// +"UNION "
//		// +"SELECT "
//		// +"MOVIE_ID "
//		// +"FROM ACTOR "
//		// +"WHERE NAME=?Kelly McGillis? "
//		// +")"
//		// +"INTERSECT "
//		// +"SELECT MOVIE_ID "
//		// +"FROM KEYWORD "
//		// +"WHERE KEYWORD=?drama? "
//		// +");";
//
//		// String query="select * from `drop` where `drop`.id>10;";
//
//		// System.out.println("Inserisci la query");
//		// Scanner scanIn = new Scanner(System.in);
//		//
//		// String query=scanIn.nextLine();
//		//
//		// scanIn.close();
//
//		// StringTokenizer string=new StringTokenizer(query, " ()[]{}<;>=,",
//		// false);
//		//
//		// ArrayList<String> tokenslist=new ArrayList<String>();
//		//
//		// //StringTokenizer
//		// AnalysisLogger.getLogger().debug("------------ Tokenizer ----------- ");
//		//
//		//
//		// int count = string.countTokens();
//		//
//		// for (int i=0; i< count; i++){
//		//
//		// String token=string.nextToken();
//		//
//		//
//		// tokenslist.add(token);
//		//
//		// AnalysisLogger.getLogger().debug("TestParsing->: "+ token);
//		//
//		// }
//
//		AnalysisLogger.getLogger().debug("TestParsing->: Query " + query);
//		// System.out.println();
//
//		boolean AllowedQuery = false;
//
//		// LexicalAnalyzer lexer=new LexicalAnalyzer();
//		// AllowedQuery=lexer.analyze(query);
//
//		System.out.println("Specifica il tipo di piattaforma");
//
//		Scanner scanIn = new Scanner(System.in);
//		String platform = scanIn.nextLine();
//		scanIn.close();
//
//		if (platform.toLowerCase().contains("postgres")) {
//
//			PostgresLexicalAnalyzer obj = new PostgresLexicalAnalyzer();
//
//			AllowedQuery = obj.analyze(query);
//
//		}
//
//		if (platform.toLowerCase().contains("mysql")) {
//
//			MySQLLexicalAnalyzer obj = new MySQLLexicalAnalyzer();
//
//			AllowedQuery = obj.analyze(query);
//
//		}
//
//		if (AllowedQuery == true) {
//			
//			AnalysisLogger.getLogger().debug("TestParsing->: filtered Query");
//
//		} else {
//			AnalysisLogger.getLogger().debug(
//					"TestParsing->: not filtered query");
//		}
//
//	}
//
//}
