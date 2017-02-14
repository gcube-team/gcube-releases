package org.gcube.dataaccess.databases.utilsold;
//package org.gcube.dataanalysis.databases.utilsold;
//
//import java.math.BigInteger;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
////import org.gcube.databasemanagement.DBAdapter;
//import org.gcube.dataanalysis.databases.structure.MySQLTableStructure;
//import org.gcube.dataanalysis.databases.structure.AbstractTableStructure;
//import org.gcube.dataanalysis.databases.structure.PostgresTableStructure;
////import org.gcube.contentmanagement.databases.structure.MySQLTableStructure;
//import org.hibernate.SessionFactory;
//
//
///** Class that allows manage a database offering several functionalities */
//public class DatabaseManagement {
//
//	// AbstractTableStructure crossTableStructure;
//	private List<String> tablesname = new ArrayList<String>();
//	private String configPath;
//	private String sourceSchemaName;
//	private SessionFactory sourceDBSession;
//	private String DBType;
//	private AbstractTableStructure crossTableStructure;
//	// private DBAdapter typesMap;
//	private DatabaseOperations op = new DatabaseOperations();
////	private String destinationDBType;
////	private String sourceDBType;
//	MySQLTableStructure mysqlobj;
//
//	private static final String MYSQL = "MySQL";
//	private static final String POSTGRES = "Postgres";
//	private static final String selectTablesQuery = "SELECT  distinct  table_name FROM information_schema.COLUMNS where table_schema='%1$s';";
//	private static final String listSchemaNameQuery="select schema_name from information_schema.schemata where schema_name <> 'information_schema' and schema_name !~ E'^pg_'";
//
//	public DatabaseManagement(String cfgDir, String SourceFile)
//			throws Exception {
//
//		configPath = cfgDir;
//		if (!configPath.endsWith("/"))
//			configPath += "/";
//
//		sourceSchemaName = op.getDBSchema(configPath + SourceFile);
//
//		sourceDBSession = DatabaseFactory.initDBConnection(configPath
//				+ SourceFile);
//
//		
////		destinationDBType = POSTGRES;
////		sourceDBType = MYSQL;
////
////		// typesMap = new DBAdapter(configPath + "/" + sourceDBType + "2"
////		// + destinationDBType + ".properties");
//
//	}
//
//	// Get the table's names
//	public List<String> getTables() throws Exception {
//
//		String query = String.format(selectTablesQuery, sourceSchemaName);
//
//		List<Object> resultSet = DatabaseFactory.executeSQLQuery(query,
//				sourceDBSession);
//
//		for (Object result : resultSet) {
//			tablesname.add((String) result);
//		}
//
//		// Get the Database's type
//		DBType = op.getDBType();
//
//		return tablesname;
//
//	}
//	
//	
//	//Get the schema's name for the database Postgres
//	public List<String> getSchemas() throws Exception{
//
//		// Get the Database's type
//		DBType = op.getDBType();
//
//		List<String> list= new ArrayList<String>();
//
//
//
//		if (DBType.equals(POSTGRES)) {
//
//
//			List<Object> resultSet = DatabaseFactory.executeSQLQuery(listSchemaNameQuery,
//					sourceDBSession);
//
//			for (Object result : resultSet) {
//				list.add((String) result);
//			}
//		}
//
//		if (DBType.equals(MYSQL)){
//
//			list=null;
//
//
//		}
//
//
//
//		return list;
//
//	}
//
//	// Get the "Create Table" statement
//	public String getCreateTable(String tablename) throws Exception {
//
//		String createstatement = "";
//
//		if (DBType.equals(POSTGRES)) {
//
//			// for (String table : tablesname) {
//
//			crossTableStructure = getSourceTableObject(tablename);
//
//			String tableBuildQuery = crossTableStructure.buildUpCreateTable();
//
//			AnalysisLogger.getLogger().debug(
//					"DatabaseManagement->'Create Table' statement: "
//							+ tableBuildQuery);
//
//			// }
//
//		}
//
//		if (DBType.equals(MYSQL)) {
//
//			// for (String table : tablesname) {
//
//			crossTableStructure = getSourceTableObject(tablename);
//
//			try {
//
//				String createtablestatement = mysqlobj
//						.showCreateTable(sourceDBSession);
//
//				AnalysisLogger.getLogger().debug(
//						"DatabaseManagement->'Create Table' statement: "
//								+ createtablestatement);
//
//			} catch (Exception e) {
//				// TODO: handle exception
//
//				AnalysisLogger.getLogger().debug(
//						"DatabaseManagement->Exception: " + e.getMessage());
//			}
//
//			// }
//
//		}
//
//		return createstatement;
//
//	}
//
//	// Method that create the database object
//	private AbstractTableStructure getSourceTableObject(String tablename)
//			throws Exception {
//
//		if (DBType.equals(MYSQL)) {
//
//			mysqlobj = new MySQLTableStructure(sourceSchemaName, tablename,
//					sourceDBSession);
//
//			// mysqlobj = new MySQLTableStructure(sourceSchemaName, tablename,
//			// typesMap, sourceDBSession);
//
//			// return new MySQLTableStructure(sourceSchemaName, tablename,
//			// typesMap, sourceDBSession);
//			return mysqlobj;
//
//		}
//
//		else if (DBType.equals(POSTGRES)) {
//
//			PostgresTableStructure postobj = new PostgresTableStructure(
//					sourceSchemaName, tablename, sourceDBSession);
//
//			// PostgresTableStructure postobj = new PostgresTableStructure(
//			// sourceSchemaName, tablename, typesMap, sourceDBSession);
//
//			return postobj;
//
//		} else {
//			return null;
//		}
//
//	}
//
//	// Method that returns the estimated number of rows
//	public BigInteger getNumberOfRows(String tablename) throws Exception {
//
//		BigInteger rows;
//
//		rows = op.calculateElements(tablename, sourceDBSession);
//		return rows;
//
//	}
//
//}
