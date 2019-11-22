package org.gcube.dataaccess.databases.structure;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.hibernate.SessionFactory;
import org.gcube.dataaccess.databases.utils.DatabaseFactory;

/** Class that allows to manage the Postgres database. */
public class PostgresTableStructure extends AbstractTableStructure {

	public PostgresTableStructure(String Schemaname, String TableName,
			SessionFactory dbSession) throws Exception {
		super(Schemaname, TableName, dbSession);
		// TODO Auto-generated constructor stub
	}

	// Method that allows to build the structure for a Postgres database.
	@Override
	protected void buildStructure(SessionFactory dbSession) throws Exception {

		String queryForStructure = getQueryForTableStructure(dbSession);

		String queryStructure = String.format(queryForStructure, tableName,
				databaseName);

		List<Object> resultSet = DatabaseFactory.executeSQLQuery(
				queryStructure, dbSession);

		// if (resultSet==null){
		//
		// throw new Exception("The resulting table has not rows");
		//
		// }

		// manage an error that postgres does not signal. In this case the
		// "queryForStructure" query is case sensitive so the field "table_name"
		// must be set to the table name well formatted.
		if (resultSet == null) {
			AnalysisLogger
					.getLogger()
					.debug("PostgresTableStructure->Error: Results not available. Check that the database and schema names are correct"
							+ queryStructure);

			throw new Exception(
					"Results not available. Check that the database and schema names are correct");

		}

		AnalysisLogger.getLogger().debug(
				"PostgresTableStructure->Building Structure with query: "
						+ queryStructure);

		int resultsNumber = resultSet.size();

		for (int i = 0; i < resultsNumber; i++) {

			try {
				Object result = resultSet.get(i);
				Object[] resultArray = (Object[]) result;

				// retrieve the column name
				String columnname = ((String) resultArray[0]);
				if (columnname.equalsIgnoreCase("class"))
					columnname = "classcolumn";

				ColumnNames.add(columnname);

				// retrieve the nullable value
				String yesno = (String) resultArray[2];
				if (yesno.equalsIgnoreCase("YES"))
					IsNullables.add(true);
				else
					IsNullables.add(false);

				// retrieve the data type
				String type = (String) resultArray[3];

				if ((resultArray[1] != null)
						&& (resultArray[3].toString().equals("integer"))) {

					if ((resultArray[1]).toString().startsWith("nextval('")) {

						type = "serial";

					}
					// else{
					//
					// //recover the default value
					// // DefaultValues.add(resultArray[1]).toString());
					// DefaultValues.add((String)(resultArray[1]));
					//
					//
					// }

					if (resultArray[4] != null) {
						String tot = "";

						charset = resultArray[4].toString();

						tot = type + "(" + charset + ")";

						// retrieve the data type
						TypesList.add(tot);

					}

					else {
						// retrieve the data type
						TypesList.add(type);

					}

				} else if (type.equals("USER-DEFINED")) {

					type = (String) resultArray[5];

					// retrieve the character maximun lenght
					if (resultArray[4] != null) {
						String tot = "";

						charset = resultArray[4].toString();

						tot = type + "(" + charset + ")";

						// retrieve the data type
						TypesList.add(tot);

					} else {
						// retrieve the data type
						TypesList.add(type);
					}

				} else {

					// retrieve the character maximun lenght

					if (resultArray[4] != null) {
						String tot = "";

						charset = resultArray[4].toString();

						tot = type + "(" + charset + ")";

						// retrieve the data type
						TypesList.add(tot);

					}

					else {
						// retrieve the data type
						TypesList.add(type);

					}

				}

				// recover the default value
				if ((resultArray[1] == null)
						|| ((resultArray[1]).toString().startsWith("nextval('"))) {

					DefaultValues.add(null);

				} else {

					DefaultValues.add((String) (resultArray[1]));
				}

				// String tot = "";
				//
				// if (resultArray[4] != null) {
				//
				// charset = resultArray[4].toString();
				// // AnalysisLogger.getLogger().debug(
				// // "PostgresTableStructure->charset: " + charset);
				//
				// // String type = (String)resultArray[3];
				//
				//
				// // if((resultArray[3].toString().equals("integer")) &&
				// (resultArray[1]).toString().startsWith("nextval('")){
				// //
				// //
				// // type="serial";
				// //
				// // }
				// // if (type.equals("USER-DEFINED")){
				// //
				// // type=(String)resultArray[5];
				// //
				// // }
				//
				// tot = type + "(" + charset + ")";
				//
				// TypesList.add(tot);
				//
				//
				// }

				// else {
				// String type = (String)resultArray[3];
				// String coldefault=(String)resultArray[1];
				//
				// // if((type.equals("integer")) && (coldefault!=null) &&
				// (coldefault.startsWith("nextval('"))){
				//
				// if((type.equals("integer")) && (coldefault!=null)) {
				//
				//
				// type="serial";
				//
				// }
				//
				// TypesList.add(type);
				//
				// }

			} catch (Exception e) {
				// e.printStackTrace();
				throw e;
			}
		}

		parseIndexes(dbSession);

	}

	// Method that allows to recover the keys of a table.
	private void parseIndexes(SessionFactory dbSession) throws Exception {

		// Query that retrieves keys

		String queryForIndexes = getQueryForIndexes(dbSession);

		String queryStructure = String.format(queryForIndexes, tableName);
		List<Object> resultSet = DatabaseFactory.executeSQLQuery(
				queryStructure, dbSession);

		AnalysisLogger.getLogger().debug(
				"PostgresTableStructure->Building Structure with query adding keys: "
						+ queryStructure);

		if (resultSet != null) {
			int resultsNumber = resultSet.size();

			for (int i = 0; i < resultsNumber; i++) {

				Object result = resultSet.get(i);
				Object[] resultArray = (Object[]) result;

				String columnKey = (String) resultArray[1];

				if (columnKey.equals("PRIMARY KEY"))
					ColumnKeys.add((String) resultArray[3]);
				else if (columnKey.equals("UNIQUE KEY"))
					UniqueKeys.add((String) resultArray[3]);
				else if (columnKey.equals("FOREIGN KEY"))
					UniqueKeys.add((String) resultArray[3]);

			}

		}

	}

	// Method that returns the query to build the table's structure.
	@Override
	protected String getQueryForTableStructure(SessionFactory dbSession)
			throws Exception {
		// TODO Auto-generated method stub

		// String queryForStructure =
		// "SELECT table_schema,table_name,column_name,column_default,is_nullable,data_type,character_maximum_length,character_set_name FROM information_schema.COLUMNS WHERE table_name ='%1$s' and table_schema='%2$s';";
		String queryForStructure = "SELECT column_name,column_default,is_nullable,data_type,character_maximum_length,udt_name  FROM information_schema.COLUMNS WHERE table_name ='%1$s' and table_schema='%2$s' order by ordinal_position asc;";

		return queryForStructure;

	}

	// Method that returns the query to show the create statement
	@Override
	protected String getQueryForIndexes(SessionFactory dbSession)
			throws Exception {
		// TODO Auto-generated method stub

		String queryForIndexes = "SELECT tc.constraint_name,"
				+ "tc.constraint_type,tc.table_name,kcu.column_name,tc.is_deferrable,tc.initially_deferred,rc.match_option AS match_type,rc.update_rule AS on_update,"
				+ "rc.delete_rule AS on_delete,ccu.table_name AS references_table,ccu.column_name AS references_field FROM information_schema.table_constraints tc "
				+ "LEFT JOIN information_schema.key_column_usage kcu ON tc.constraint_catalog = kcu.constraint_catalog AND tc.constraint_schema = kcu.constraint_schema AND tc.constraint_name = kcu.constraint_name "
				+ "LEFT JOIN information_schema.referential_constraints rc ON tc.constraint_catalog = rc.constraint_catalog AND tc.constraint_schema = rc.constraint_schema AND tc.constraint_name = rc.constraint_name "
				+ "LEFT JOIN information_schema.constraint_column_usage ccu ON rc.unique_constraint_catalog = ccu.constraint_catalog AND rc.unique_constraint_schema = ccu.constraint_schema AND rc.unique_constraint_name = ccu.constraint_name "
				+ "where tc.table_name='%1$s' and tc.constraint_type<>'CHECK'";

		return queryForIndexes;

	}

}
