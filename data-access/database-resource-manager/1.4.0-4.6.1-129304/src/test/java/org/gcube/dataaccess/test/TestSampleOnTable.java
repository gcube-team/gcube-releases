package org.gcube.dataaccess.test;
//package org.gcube.dataanalysis.test;
//
//import java.io.IOException;
//
//import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
//import org.gcube.dataanalysis.databases.utils.DatabaseManagement;
//
//public class TestSampleOnTable {
//
//	public static void main(String[] args) {
//
//		// connection to database
//		DatabaseManagement mgt = new DatabaseManagement("");
//
//		
//		
//		
//		//Postgres Database
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
//		// SampleOnTable operation
//
//		try {
//			// for database postgres, if a table is not in lower case format, it
//			// is necessary to include the table name in quotes ""
//			mgt.sampleOnTable("Divisions", "public");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//
//			AnalysisLogger
//					.getLogger()
//					.debug("In TestSampleOnTable->EXCEPTION: "+ e);
//		}
//
//		// SmartSampleOnTable operation
//
//		// mgt.smartSampleOnTable(tableName);
//		
//		
//		
//		
//		
//		//MYSQL Database
//		
////		try {
////			mgt.createConnection(
////					"root",
////					"test",
////					"com.mysql.jdbc.Driver",
////					"org.hibernate.dialect.MySQLDialect",
//////					"jdbc:mysql://146.48.87.169:3306/col2oct2010",
////					"jdbc:mysql://146.48.87.169:3306/aquamaps",
////					"hcaf_d");
////		} catch (IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////
////		// SampleOnTable operation
////
////		try {
////			// for database postgres, if a table is not in lower case format, it
////			// is necessary to include the table name in quotes ""
////			mgt.sampleOnTable("hcaf_d", "aquamaps");
////		} catch (Exception e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////
////			AnalysisLogger
////					.getLogger()
////					.debug("In TestSampleOnTable->EXCEPTION: "+ e);
////		}
//
//	}
//
//}
