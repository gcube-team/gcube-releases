package org.gcube.dataaccess.test;
//package org.gcube.dataanalysis.test;
//
//import java.io.IOException;
//
//import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
//import org.gcube.dataanalysis.databases.utils.DatabaseManagement;
//
//public class RegressionRandomSampleOnTable {
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		AnalysisLogger.getLogger().debug("Executing: " + "Postgres");
//		testPostgres();
//
//		AnalysisLogger.getLogger().debug("Executing: " + "Mysql1");
////		 testMysql1();
//		 
//		 AnalysisLogger.getLogger().debug("Executing: " + "Mysql2");
////		 testMysql2();
//		 
//		 AnalysisLogger.getLogger().debug("Executing: " + "Mysql3");
////		 testMysql3();
//	}
//
//	// Postgres database
//	private static void testPostgres() {
//
//		// connection to database
//		DatabaseManagement mgt = new DatabaseManagement("");
//
//		try {
//			mgt.createConnection(
//					"postgres",
//					"d4science2",
//					"org.postgresql.Driver",
//					"org.hibernate.dialect.PostgreSQLDialect",
//					"jdbc:postgresql://geoserver-dev.d4science-ii.research-infrastructures.eu:5432/aquamapsdb",
//					"aquamapsdb");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		// SmartSampleOnTable operation
//
//		try {
//			// for database postgres, if a table is not in lower case format, it
//			// is necessary to include the table name in quotes ""
//			mgt.randomSampleOnTable("Divisions", "public");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//
//			AnalysisLogger.getLogger().debug(
//					"In RegressionRandomSampleOnTable->EXCEPTION: " + e);
//		}
//
//	}
//	
//	// Mysql database
//		private static void testMysql1() {
//
//			// connection to database
//			DatabaseManagement mgt = new DatabaseManagement("");
//
//			try {
//				mgt.createConnection("root", "test", "com.mysql.jdbc.Driver",
//						"org.hibernate.dialect.MySQLDialect",
//						// "jdbc:mysql://146.48.87.169:3306/col2oct2010",
//						"jdbc:mysql://146.48.87.169:3306/aquamaps", "hcaf_d");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			// SmartSampleOnTable operation
//
//			try {
//				// for database postgres, if a table is not in lower case format, it
//				// is necessary to include the table name in quotes ""
//				mgt.randomSampleOnTable("hcaf_d", "aquamaps");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//
//				AnalysisLogger.getLogger().debug(
//						"In RegressionRandomSampleOnTable->EXCEPTION: " + e);
//			}
//
//		}
//		
//		// Mysql database
//				private static void testMysql2() {
//					
//					// connection to database
//					DatabaseManagement mgt = new DatabaseManagement("");
//
//					try {
//						mgt.createConnection("root", "test", "com.mysql.jdbc.Driver",
//								"org.hibernate.dialect.MySQLDialect",
//								 "jdbc:mysql://146.48.87.169:3306/col2oct2010",
////								"jdbc:mysql://146.48.87.169:3306/aquamaps", 
//								"example");
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//					// SmartSampleOnTable operation
//
//					try {
//						// for database postgres, if a table is not in lower case format, it
//						// is necessary to include the table name in quotes ""
//						mgt.randomSampleOnTable("example", "col2oct2010");
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//
//						AnalysisLogger.getLogger().debug(
//								"In RegressionRandomSampleOnTable->EXCEPTION: " + e);
//					}
//					
//					
//				}
//				
//				//Mysql database
//				private static void testMysql3() {
//					
//					// connection to database
//					DatabaseManagement mgt = new DatabaseManagement("");
//
//					try {
//						mgt.createConnection("root", "test", "com.mysql.jdbc.Driver",
//								"org.hibernate.dialect.MySQLDialect",
//								 "jdbc:mysql://146.48.87.169:3306/col2oct2010",
////								"jdbc:mysql://146.48.87.169:3306/aquamaps", 
//								"common_names");
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//					// SmartSampleOnTable operation
//
//					try {
//						// for database postgres, if a table is not in lower case format, it
//						// is necessary to include the table name in quotes ""
//						mgt.randomSampleOnTable("common_names", "col2oct2010");
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//
//						AnalysisLogger.getLogger().debug(
//								"In RegressionRandomSampleOnTable->EXCEPTION: " + e);
//					}
//					
//					
//				}
//
//}
