package org.gcube.dataanalysis.ecoengine.utils;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.sql.Connection;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.hibernate.SessionFactory;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

public class DatabaseUtils {

	static String queryDesc = "SELECT column_name,data_type, character_maximum_length, is_nullable FROM information_schema.COLUMNS WHERE table_name ='%1$s'";
	static String queryColumns = "SELECT column_name FROM information_schema.COLUMNS WHERE table_name ='%1$s'";
	static String queryForKeys = "SELECT b.column_name as name, a.constraint_type  as type FROM information_schema.table_constraints as a join information_schema.key_column_usage as b on a.table_name ='%1$s' and a.constraint_name = b.constraint_name";
	static String genCreationStatement = "CREATE TABLE %1$s ( %2$s  %3$s );";
	static String updateColValues = "UPDATE %1$s SET %2$s = %3$s.%4$s FROM %3$s WHERE %1$s.%5$s = %3$s.%6$s ;";
	static String addColumn = "ALTER TABLE %1$s ADD COLUMN %2$s %3$s;";
	SessionFactory referencedbConnection;

	public DatabaseUtils(SessionFactory referencedbConnection) {
		this.referencedbConnection = referencedbConnection;
	}

	public static String createUpdateStatement(String tableToUpdate, String fieldToUpdate, String tableFromUpdate, String columnFromUpdate, String tableToUpKey, String tableFromUpKey) {
		return String.format(updateColValues, tableToUpdate, fieldToUpdate, tableFromUpdate, columnFromUpdate, tableFromUpdate, tableToUpdate, tableToUpKey, tableFromUpdate, tableFromUpKey);
	}

	public static String addColumnStatement(String tableToUpdate, String columnName, String columnKey) {
		return String.format(addColumn, tableToUpdate, columnName, columnKey);
	}

	private String primaryK;
	private String primaryKColName;
	
	public static long estimateNumberofRows(String table, SessionFactory dbconnection) throws Exception{
		List<Object> explain = DatabaseFactory.executeSQLQuery("EXPLAIN SELECT * FROM "+table, dbconnection);
		String explained = "" + explain.get(0);
		explained = explained.substring(explained.lastIndexOf("rows="));
		explained = explained.substring(explained.indexOf('=') + 1, explained.indexOf(' '));
		return Long.parseLong(explained);
	}
	
	private void getPrimaryKeys(List<Object> keys, String table) {
		int keynum = 0;
		if (keys != null)
			keynum = keys.size();
		StringBuffer pkeybuffer = new StringBuffer();
		primaryKColName = "";
		for (int i = 0; i < keynum; i++) {
			Object[] valueKey = (Object[]) keys.get(i);
			String colname = (String) valueKey[0];
			String type = (String) valueKey[1];
			if (type.equals("PRIMARY KEY")) {
				if (pkeybuffer.length() > 0)
					pkeybuffer.append(",");
				pkeybuffer.append(colname);
				if (primaryKColName.length() > 0)
					primaryKColName = ",";

				primaryKColName = primaryKColName + colname;
			}

		}
		if (pkeybuffer.length() > 0)
			primaryK = ", CONSTRAINT " + table + "_idx PRIMARY KEY (" + pkeybuffer.toString() + ")";
	}

	public String getPrimaryKey() {
		return primaryKColName;
	}

	private String columnDescrs;

	private void getColumnsDesc(List<Object> columnsDescs) {
		int num = columnsDescs.size();
		StringBuffer colbuffer = new StringBuffer();

		for (int i = 0; i < num; i++) {
			Object[] descriptions = (Object[]) columnsDescs.get(i);
			String colname = "" + descriptions[0];
			String type = "" + descriptions[1];
			String len = "" + descriptions[2];
			String isnullable = "" + descriptions[3];
			if (len != null && len.length() > 0 && !len.equals("null"))
				type = type + "(" + len + ")";
			if (isnullable != null && isnullable.equalsIgnoreCase("NO"))
				isnullable = "NOT NULL";
			else
				isnullable = "";

			colbuffer.append(colname + " " + type + " " + isnullable);
			if (i < num - 1) {
				colbuffer.append(",");
			}
		}
		columnDescrs = colbuffer.toString();
	}

	public List<Object> columns;

	public List<Object> getColumnDecriptions() {
		return columns;
	}

	public String getColumnName(int index) {
		return "" + ((Object[]) getColumnDecriptions().get(index))[0];
	}

	public String getColumnType(int index) {
		return "" + ((Object[]) getColumnDecriptions().get(index))[1];
	}

	public static String duplicateTableStatement(String tableFrom, String tableTo) {
		return "select * into " + tableTo + " from " + tableFrom;
	}

	public static String createBlankTableFromAnotherStatement(String tableFrom, String tableTo) {
		return "select * into " + tableTo + " from (select * from " + tableFrom + " limit 0) a";
	}

	public static String dropTableStatement(String table) {
		return "drop table " + table;
	}

	public static String getDinstictElements(String table, String columns, String filter) {
		return "select distinct " + columns + " from " + table + " " + filter + " order by " + columns;
	}

	public static String getOrderedElements(String table, String key, String column) {
		return "select " + key + "," + column + " from " + table + " order by " + key;
	}

	public static String sumElementsStatement(String table, String column) {
		return "select sum(" + column + ") from " + table;
	}

	public static String getColumnsElementsStatement(String table, String columns, String filter) {
		return "select " + columns + " from " + table + " " + filter;
	}

	public static String countElementsStatement(String table) {
		return "select count(*) from " + table;
	}

	public static String update(String table, String valueColumnName, String value, String keyColumnName, String key) {
		return "UPDATE " + table + "	SET  " + valueColumnName + " = '" + value + "'" + " WHERE " + keyColumnName + " = '" + key + "'";
	}

	public static String insertIntoColumn(String table, String keyColumnName, String valueColumnName, List<Object> couplesColumnAndKeys) {

		StringBuffer buffer = new StringBuffer();
		int ncols = couplesColumnAndKeys.size();
		if (ncols > 0) {
			buffer.append("insert into " + table + "	(" + keyColumnName + "," + valueColumnName + ") values ");
			for (int i = 0; i < ncols; i++) {
				Object[] couples = (Object[]) couplesColumnAndKeys.get(i);
				String key = "" + couples[0];
				String value = "" + couples[1];
				buffer.append("(" + key + "," + value + ")");

				if (i < ncols - 1)
					buffer.append(", ");
			}
		}
		return buffer.toString();
	}

	public static String insertFromBuffer(String table, String columnsNames, StringBuffer values) {

		return "insert into " + table + " (" + columnsNames + ") values " + values;
	}

	public static void insertChunksIntoTable(String table, String columnsNames, List<String[]> values, int chunkSize,SessionFactory dbconnection) throws Exception{
		insertChunksIntoTable(table, columnsNames, values, chunkSize,dbconnection, true) ;
	}
	
	public static void insertChunksIntoTable(String table, String columnsNames, List<String[]> values, int chunkSize,SessionFactory dbconnection, boolean correctApos) throws Exception{
		
		int valuesize = values.size();
		StringBuffer sb = new StringBuffer();
		int stopIndex =0; 
		for (int i=0;i<valuesize;i++){
			String[] row = values.get(i);
			sb.append("(");
			for (int j=0;j<row.length;j++){
				String preprow = row[j].replaceAll("^'", "").replaceAll("'$", "");
				if (correctApos)
					preprow=preprow.replace("'", ""+(char)96);
				if (preprow.equalsIgnoreCase("NULL"))
					sb.append(preprow);
				else
					sb.append("'"+preprow+"'");
				if  (j<row.length-1)
					sb.append(",");
			}
			sb.append(")");
			if (stopIndex>0 && stopIndex%chunkSize==0){
				DatabaseFactory.executeSQLUpdate(insertFromBuffer(table, columnsNames, sb), dbconnection);
				stopIndex=chunkSize;
				sb = new StringBuffer();
			}
			else if (i<valuesize-1)
				sb.append(",");
		}
		
		if (stopIndex<valuesize-1){
			if (sb.length()>0){
//				System.out.println(sb);
				try{
					DatabaseFactory.executeSQLUpdate(insertFromBuffer(table, columnsNames, sb), dbconnection);
				}catch(Exception e){
					System.out.println("Query:"+sb);
					throw e;
				}
				
			}
		}
			
	}

	
	public static String insertFromString(String table, String columnsNames, String values) {

		return "insert into " + table + " (" + columnsNames + ") values " + values;
	}

	public static String deleteFromBuffer(String table, StringBuffer couples) {

		return "delete from " + table + " where " + couples;
	}

	public static String copyFileToTableStatement(String file, String table) {
		return "COPY " + table + " FROM '" + file + "' DELIMITERS ';' WITH NULL AS 'null string'";
	}

	public static String copyFileFromTableStatement(String file, String table, String delimiter, boolean withheader) {

		String withheaderS = "";
		if (withheader)
			withheaderS = " CSV HEADER";

		return "COPY " + table + " TO '" + file + "' DELIMITERS '" + delimiter + "' WITH NULL AS '' " + withheaderS;
	}

	public static String updateTableColumnFromOther(String tableName, String fieldToUpdate, String otherTable, String otherColumn, String keyColumn, String otherKeyColumn) {
		return "UPDATE " + tableName + " SET " + fieldToUpdate + " = " + otherTable + "." + otherColumn + "  FROM " + otherTable + " WHERE " + tableName + "." + keyColumn + "=" + otherTable + "." + otherKeyColumn;
	}

	public static String updateColumn(String table, String keyColumnName, String valueColumnName, List<Object> couplesColumnAndKeys) {

		StringBuffer buffer = new StringBuffer();
		int ncols = couplesColumnAndKeys.size();
		for (int i = 0; i < ncols; i++) {
			Object[] couples = (Object[]) couplesColumnAndKeys.get(i);
			String key = "" + couples[0];
			String value = "" + couples[1];
			buffer.append("UPDATE " + table + "	SET  " + valueColumnName + " = '" + value + "'" + " WHERE " + keyColumnName + " = '" + key + "'");

			if (i < ncols - 1)
				buffer.append(";\n");
		}

		return buffer.toString();
	}

	public String buildCreateStatement(String originaltable, String destinationTable) {

		// take the structure of table 1
		List<Object> keys = DatabaseFactory.executeSQLQuery(String.format(queryForKeys, originaltable), referencedbConnection);
		getPrimaryKeys(keys, destinationTable);
		columns = DatabaseFactory.executeSQLQuery(String.format(queryDesc, originaltable), referencedbConnection);
		getColumnsDesc(columns);
		String creationStatement = String.format(genCreationStatement, destinationTable, columnDescrs, primaryK);

		return creationStatement;
	}

	public static String getColumnsNamesStatement(String table) {
		String statement = String.format(queryColumns, table);
		return statement;
	}

	public static void createBigTable(boolean createTable, String table, String dbdriver, String dbuser, String dbpassword, String dburl, String creationStatement, SessionFactory dbHibConnection) throws Exception {
		if (createTable) {
			try {
				AnalysisLogger.getLogger().debug("Dropping previous table if exists");
				DatabaseFactory.executeSQLUpdate("drop table " + table, dbHibConnection);
			} catch (Exception e) {
				// e.printStackTrace();
			}
			AnalysisLogger.getLogger().debug("Creating Big Table");
			DatabaseFactory.executeUpdateNoTransaction(creationStatement, dbdriver, dbuser, dbpassword, dburl, true);
		}
	}

	public static void createRemoteTableFromFile(String filePath, String tablename, String delimiter, boolean hasHeader, String username, String password, String databaseurl) throws Exception {

		Connection conn = DatabaseFactory.getDBConnection("org.postgresql.Driver", username, password, databaseurl);
		CopyManager copyManager = new CopyManager((BaseConnection) conn);
		FileInputStream fis = new FileInputStream(filePath);
		copyManager.copyIn(String.format("COPY %s FROM STDIN WITH DELIMITER '%s' %s ", tablename, delimiter, (hasHeader) ? "CSV HEADER" : "CSV"), fis);
		conn.close();
		fis.close();
	}

	public static void createLocalFileFromRemoteTable(String filePath, String tablename, String delimiter, String username, String password, String databaseurl) throws Exception {

		Connection conn = DatabaseFactory.getDBConnection("org.postgresql.Driver", username, password, databaseurl);
		CopyManager copyManager = new CopyManager((BaseConnection) conn);
		FileWriter fw = new FileWriter(filePath);
		copyManager.copyOut(String.format("COPY %s TO STDOUT WITH DELIMITER '%s' NULL AS '' ", tablename, delimiter), fw);
		conn.close();
		fw.close();
	}

	public static SessionFactory initDBSession(AlgorithmConfiguration config) {
		SessionFactory dbHibConnection = null;
		try {
			if ((config != null) && (config.getConfigPath() != null)) {
				String defaultDatabaseFile = config.getConfigPath() + AlgorithmConfiguration.defaultConnectionFile;

				config.setDatabaseDriver(config.getParam("DatabaseDriver"));
				config.setDatabaseUserName(config.getParam("DatabaseUserName"));
				config.setDatabasePassword(config.getParam("DatabasePassword"));
				config.setDatabaseURL(config.getParam("DatabaseURL"));

				dbHibConnection = DatabaseFactory.initDBConnection(defaultDatabaseFile, config);
			}
		} catch (Exception e) {
			System.out.println("ERROR IN DB INITIALIZATION : " + e.getLocalizedMessage());
			e.printStackTrace();
			// AnalysisLogger.getLogger().trace(e);
		}
		return dbHibConnection;
	}

	public static void closeDBConnection(SessionFactory dbHibConnection) {
		try {
			dbHibConnection.close();
		} catch (Exception e) {

		}
	}

}
