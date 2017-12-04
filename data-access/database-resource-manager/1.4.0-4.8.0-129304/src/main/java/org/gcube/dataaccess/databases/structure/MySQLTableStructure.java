package org.gcube.dataaccess.databases.structure;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.dataaccess.databases.utils.ConnectionManager;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.hibernate.SessionFactory;

/** Class that allows to manage the MySQL database. */
public class MySQLTableStructure extends AbstractTableStructure {

	public MySQLTableStructure(String Databasename, String TableName,
			SessionFactory dbSession) throws Exception {
		super(Databasename, TableName, dbSession, false);
	}

	// Method that executes the query "show create table" in order to retrieve
	// the "create table" statement
	public String showCreateTable(ConnectionManager connection,
			SessionFactory dbSession) throws Exception {

		// Retrieve the query
		String queryForIndexes = getQueryForIndexes(dbSession);
		

		try {

			// List<Object> indexSet = DatabaseFactory.executeSQLQuery(
			// String.format(queryForIndexes, tableName), dbSession);

			// List<Object> indexSet =
			// connection.executeQuery(String.format(queryForIndexes,
			// tableName), dbSession);

			// String createTableStatement = (String) (((Object[])
			// indexSet.get(0))[1]);
			
			//the full name equal to "dbname.tablename"
			tableName = this.databaseName+"."+tableName;

			List<Object> result = connection.executeQuery(
					String.format(queryForIndexes, tableName), dbSession);

			Object element = result.get(0);

			ArrayList<Object> listvalues = new ArrayList<Object>(
					((LinkedHashMap<String, Object>) element).values());

			String createTableStatement = listvalues.get(1).toString();

			AnalysisLogger.getLogger().debug(
					"MySQLTableStructure->'Create Table' statement: "
							+ createTableStatement);

			return createTableStatement;

		} catch (Exception e) {

			throw e;

		}

	}

	// Method that returns the query to build the table's structure. This method
	// is not useful for mysql.
	@Override
	protected String getQueryForTableStructure(SessionFactory dbSession)
			throws Exception {
		// TODO Auto-generated method stub

		String queryForStructure = "SELECT table_schema,table_name,column_name,column_default,is_nullable,data_type,character_maximum_length,character_set_name,column_type,column_key FROM information_schema.COLUMNS WHERE table_name ='%1$s' and table_schema='%2$s';";

		return queryForStructure;

	}

	// Method that returns the query to show the create statement
	@Override
	protected String getQueryForIndexes(SessionFactory dbSession)
			throws Exception {
		// TODO Auto-generated method stub

//		String queryForIndexes = "SHOW CREATE TABLE `%1$s`;";
		
		String queryForIndexes = "SHOW CREATE TABLE %1$s;";

		return queryForIndexes;

	}

	// This method is not useful for the database mysql because the slq query
	// "show create" makes available the create table statement.
	@Override
	protected void buildStructure(SessionFactory dbSession) throws Exception {

		// //retrieve the query
		// String queryForStructure=getQueryForTableStructure(dbSession);
		//
		// String queryStructure =
		// String.format(queryForStructure,tableName,databaseName);
		// List<Object> resultSet =
		// DatabaseFactory.executeSQLQuery(queryStructure, dbSession);
		//
		//
		// AnalysisLogger.getLogger().debug("MySQLTableStructure->Building Structure with query: "+queryStructure);
		//
		// int resultsNumber = resultSet.size();
		//
		// for (int i=0;i<resultsNumber;i++) {
		// try {
		// Object result = resultSet.get(i);
		// Object[] resultArray = (Object[]) result;
		// if (i==0){
		// charset = (String)resultArray[7];
		// }
		// String columnname = ((String)resultArray[2]).toLowerCase();
		// if (columnname.equalsIgnoreCase("class"))
		// columnname = "classcolumn";
		//
		// ColumnNames.add(columnname);
		// DefaultValues.add((String)resultArray[3]);
		// String yesno = (String)resultArray[4];
		// if (yesno.equalsIgnoreCase("YES"))
		// IsNullables.add(true);
		// else
		// IsNullables.add(false);
		// TypesList.add((String)resultArray[5]);
		// try{
		// TypesLengths.add(((BigInteger)resultArray[6]).intValue());
		// }catch(Exception e){
		// TypesLengths.add(-1);
		// }
		// CompleteTypes.add((String)resultArray[8]);
		//
		//
		//
		// String columnKey = (String)resultArray[9];
		// //
		// if (columnKey.equals("PRI"))
		// ColumnKeys.add(columnname);
		// else if (columnKey.equals("UNI"))
		// UniqueKeys.add(columnname);
		// else if (columnKey.equals("MUL"))
		// UniqueKeys.add(columnname);
		//
		// // else if ((columnKey != null) && (columnKey.length()>0))
		// //
		// System.err.println("MySQLTableStructure->KEY NOT CONTEMPLATED : "+columnKey);
		//
		// /*
		// if (columnKey.equals("PRI"))
		// ColumnKeys.add(columnname);
		// else if (columnKey.equals("UNI"))
		// UniqueKeys.add(columnname);
		// else if (columnKey.equals("MUL"))
		// UniqueKeys.add(columnname);
		//
		// else if ((columnKey != null) && (columnKey.length()>0))
		// System.err.println("MySQLTableStructure->KEY NOT CONTEMPLATED : "+columnKey);
		// */
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		//
		// // parseIndexes2(dbSession);
		// //fill up the indexes array
		//
		// //***Commentata
		//
		// // for (String index:Indexes){
		// ////
		// AnalysisLogger.getLogger().debug("unique index removal: "+index);
		// // //eliminate the unicity
		// // UniqueKeys.remove(index);
		// // }

	}

	// This method is not useful for the database mysql because the slq query
	// "show create" makes available the create table statement.

	// private void parseIndexes2 (SessionFactory dbSession) throws Exception{
	//
	//
	// //Retrieve the query
	// String queryForIndexes=getQueryForIndexes(dbSession);
	//
	// List<Object> indexSet =
	// DatabaseFactory.executeSQLQuery(String.format(queryForIndexes,tableName),
	// dbSession);
	//
	// String createTableStatement = (String)(((Object[])indexSet.get(0))[1]);
	// String [] splitted = createTableStatement.split("\n");
	//
	// for (int i=0;i<splitted.length;i++){
	// String line = splitted[i].trim();
	// if (line.contains("KEY")){
	// int start = line.indexOf("(");
	// int end = line.indexOf(")");
	// String column = line.substring(start+1,end);
	// column = column.replace("'", "").replace("`", "");
	// column = column.toLowerCase().trim();
	// // if (column.equals("class"))
	// // column = "classcolumn";
	// if (!Indexes.contains(column))
	// Indexes.add(column);
	// }
	// }
	//
	// }

}
