package org.gcube.dataaccess.databases.utilsold;
//package org.gcube.dataanalysis.databases.utilsold;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.math.BigInteger;
//import java.util.Iterator;
//import java.util.List;
//
//import org.dom4j.Document;
//import org.dom4j.Node;
//import org.dom4j.io.SAXReader;
//import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
//import org.hibernate.SessionFactory;
//
///** Class that allows to performs some operations on a database */
//public class DatabaseOperations {
//
//	private String DBType = "";  //database's type
//
//	private static final String Query = "select * from %1$s limit 1";
//	// private static final String countQuery = "select count(*) from %1$s";
//	private static final String explainQuery = "explain select * from %1$s";
//
//	private static final String MYSQL = "MySQL";
//	private static final String POSTGRES = "Postgres";
//	
//	
//    //Method that recover the schema's name of the database.
//	public String getDBSchema(String configurationFile) throws Exception {
//
//		File fl = new File(configurationFile);
//		FileInputStream stream = new FileInputStream(fl);
//
//		SAXReader saxReader = new SAXReader();
//		Document document = saxReader.read(stream);
//
//		List<Node> nodes = document
//				.selectNodes("//hibernate-configuration/session-factory/property");
//
//		Iterator<Node> nodesIterator = nodes.iterator();
//
//		String dbschema = "";
//		while (nodesIterator.hasNext()) {
//
//			Node currentnode = nodesIterator.next();
//			String element = currentnode.valueOf("@name");
//			if (element.equals("connection.url")) {
//				String url = currentnode.getText();
//				dbschema = url.substring(url.lastIndexOf("/") + 1);
//				if (dbschema.indexOf('?') > 0)
//					dbschema = dbschema.substring(0, dbschema.indexOf('?'));
//				AnalysisLogger.getLogger().debug(
//						"DatabaseOperations-> recovering the database's name: " + dbschema);
//				
//
//				// DBType="MySQL";
//
//				// break;
//			}
//
//			if (element.equals("connection.schemaname")) {
//				String url = currentnode.getText();
//				dbschema = url.substring(url.lastIndexOf("/") + 1);
//				if (dbschema.indexOf('?') > 0)
//					dbschema = dbschema.substring(0, dbschema.indexOf('?'));
//				AnalysisLogger.getLogger().debug(
//						"DatabaseOperations-> recovering the schema's name: " + dbschema);
//				DBType = POSTGRES;
//				// break;
//
//			}
//
//			if (DBType.equals("")) {
//
//				DBType = MYSQL;
//
//			}
//
//		}
//
//		// close stream
//		stream.close();
//
//		return dbschema;
//	}
//
//	//Method that returns the database's type
//	public String getDBType() {
//
//		return DBType;
//
//	}
//
//	//Method that calculate the estimated number of rows
//	public BigInteger calculateElements(String tablename, SessionFactory session) throws Exception{
//
//		BigInteger count = BigInteger.ZERO;
//
//		String countingQuery = String.format(Query, tablename);
//
//		AnalysisLogger.getLogger().debug(
//				"DatabaseOperations-> calculating rows' number with the query: " + countingQuery);
//
//		List<Object> result;
//
////		try {
//			result = DatabaseFactory.executeSQLQuery(countingQuery, session);
//
//			if ((result != null) && (result.size() > 0)) {
//
//				// call query with explain function
//
//				String explain = String.format(explainQuery, tablename);
//				AnalysisLogger.getLogger().debug(
//						"DatabaseOperations-> calculating rows' number with the query: " + explain);
//
//				List<Object> resultinfo;
//
//				resultinfo = DatabaseFactory.executeSQLQuery(explain, session);
//
//				// recovery result
//
//				if (DBType.equals(MYSQL)) {
//
//					Object[] resultArray = (Object[]) (resultinfo.get(0));
//
//					count = (BigInteger) resultArray[8];
//
//				}
//
//				if (DBType.equals(POSTGRES)) {
//
//					String var = resultinfo.get(0).toString();
//
//					int beginindex = var.indexOf("rows");
//
//					int lastindex = var.indexOf("width");
//
//					var = var.substring(beginindex + 5, lastindex - 1);
//
//					count = new BigInteger(var);
//
//				}
//
//			}
//
////		} catch (Exception e) {
////			 TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//
//		return count;
//
//	}
//
//}
