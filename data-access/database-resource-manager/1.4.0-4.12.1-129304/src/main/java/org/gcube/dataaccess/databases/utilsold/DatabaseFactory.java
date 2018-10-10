package org.gcube.dataaccess.databases.utilsold;
//package org.gcube.dataanalysis.databases.utilsold;
//
//import java.io.ByteArrayInputStream;
//import java.util.List;
//
//import javax.xml.parsers.DocumentBuilderFactory;
//
//import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
//import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
//import org.hibernate.Query;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.cfg.Configuration;
//
//
///** Class that allows to connect to a database and to execute a query */
//public class DatabaseFactory {
//
//	
//	//Method that establish a connection with the database
//	public static SessionFactory initDBConnection(String configurationFile) throws Exception {
//		String xml = FileTools.readXMLDoc(configurationFile);
//		SessionFactory DBSessionFactory = null;
//		Configuration cfg = new Configuration();
//		cfg = cfg.configure(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes())));
//		DBSessionFactory = cfg.buildSessionFactory();
//		return DBSessionFactory;
//	}
//	
//	
//	
//	//Method that execute the query
//	public static List<Object> executeSQLQuery(String query, SessionFactory DBSessionFactory) throws Exception {
////		System.out.println("QUERY: "+query);
//		try {
//			return executeHQLQuery(query, DBSessionFactory, true);
//			
//		} catch (Exception e) {
//			// TODO: handle exception
//			throw e;
//		}
//		
//	}
//	
//	public static List<Object> executeHQLQuery(String query, SessionFactory DBSessionFactory, boolean useSQL) throws Exception{
//		Session ss = null;
//		List<Object> obj = null;
//		
//		
//		try {
//			
//			ss = DBSessionFactory.getCurrentSession();
//
//			ss.beginTransaction();
//
//			Query qr = null;
//
//			if (useSQL)
//				qr = ss.createSQLQuery(query);
//			else
//				qr = ss.createQuery(query);
//			
//			List<Object> result = null;
//			
//			AnalysisLogger.getLogger().debug("DatabaseFactory->"+qr.getQueryString());
//			try {
//				result = qr.list();
//				ss.getTransaction().commit();
//				
//				if (result == null)
//					System.out.println("Hibernate doesn't return a valid object when org.gcube.contentmanagement.lexicalmatcher retrieve UserState Object");
//
////				if (result != null && result.size() == 0)
////					System.out.println(String.format("found nothing in database for query: "+query));
//
//				if (result != null && result.size() != 0) {
//					obj = result;
//				}
//		
//				rollback(ss);
//				
//				return obj;
//				
//			} catch (Exception e) {
//				// TODO: handle exception
//				throw e;
//			}
//			
//			
//				
//
//
//			
//			
//			
//		} catch (Exception e) {
//			// TODO: handle exception
//			
//			throw e;
//		}
//		
//			
//	
//		
//
//
//	}
//	
//	
//	public static void rollback(Session ss) {
//
//		try {
//			if (ss != null && ss.getTransaction() != null)
//				ss.getTransaction().rollback();
//		} catch (Exception ex) {
//
//		} finally {
//			try {
//				ss.close();
//			} catch (Exception ee) {
//			}
//		}
//	}
//	
//	
//	
////	public static void executeSQLUpdate(String query, SessionFactory DBSessionFactory) throws  Exception {
////		executeHQLUpdate(query, DBSessionFactory, true);
////	}
//		
//	
////	public static void executeHQLUpdate(String query, SessionFactory DBSessionFactory, boolean useSQL) throws Exception{
//////		System.out.println("executing query: " + query);
////		Session ss = null;
////
////		try {
////
////			ss = DBSessionFactory.getCurrentSession();
//////			System.out.println("executing query");
////			ss.beginTransaction();
////			Query qr = null;
////
////			if (useSQL)
////				qr = ss.createSQLQuery(query);
////			else
////				qr = ss.createQuery(query);
////
////			qr.executeUpdate();
////			ss.getTransaction().commit();
////
////		} catch (Exception e) {
////			AnalysisLogger.getLogger().debug(query);
////			rollback(ss);
//////			e.printStackTrace();
////			throw e;
////		}
////	}
//	
//
//}
