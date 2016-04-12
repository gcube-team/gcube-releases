package org.gcube.dataaccess.databases.sampler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Random;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataaccess.databases.utils.ConnectionManager;
import org.hibernate.SessionFactory;

/**
 * Class that allows to perform different types of Sample operations on a table:
 * SampleOnTable, SmartSampleOnTable, RandomSampleOnTable
 */

public class Sampler {

	// query to perform sample operation on the table
	// private static final String queryForSampleOnTablePostgres =
	// "select %1$s from \"%2$s\" limit 100";
	private static final String queryForSampleOnTablePostgres = "select %1$s from %2$s limit 100";
	private static final String queryForSampleOnTableMysql = "select %1$s from %2$s limit 100";

	// query to perform a smart sample operation randomly on the table
	// private static final String queryForSmartSampleOnTablePostgres =
	// "select %1$s from \"%2$s\" order by random() limit 200";
	private static final String queryForSmartSampleOnTablePostgres = "select %1$s from %2$s order by random() limit 200";
	private static final String queryForSmartSampleOnTableMysql = "select %1$s from %2$s order by rand() limit 200";
	// private static final String queryForSmartSampleOnTablePostgres =
	// "select * from \"%1$s\" order by random() limit 200";
	// private static final String queryForSmartSampleOnTableMysql =
	// "select * from %1$s order by rand() limit 200";

	// query to perform a smart sample operation on the table considering the
	// threshold
	// private static final String
	// queryForSmartSampleWithThresholdOnTablePostgres =
	// "select %1$s from \"%2$s\" limit 200 offset %3$s";
	private static final String queryForSmartSampleWithThresholdOnTablePostgres = "select %1$s from %2$s limit 200 offset %3$s";
	private static final String queryForSmartSampleWithThresholdOnTableMysql = "select %1$s from %2$s limit 200 offset %3$s";

	// query to perform a sample operation randomly on a table
	// private static final String queryForRandomSampleOnTablePostgres =
	// "select %1$s from \"%2$s\" order by random() limit 100";
	// query to perform a smart sample operation on the table considering the
	// threshold
	// private static final String
	// queryForRandomSampleWithThresholdOnTablePostgres =
	// "select %1$s from \"%2$s\" limit 100 offset %3$s";
	private static final String queryForRandomSampleWithThresholdOnTablePostgres = "select %1$s from %2$s limit 100 offset %3$s";
	private static final String queryForRandomSampleWithThresholdOnTableMysql = "select %1$s from %2$s limit 100 offset %3$s";
	private static final String queryForRandomSampleOnTableMysql = "select %1$s from %2$s order by rand() limit 100";
	private static final String queryForRandomSampleOnTablePostgres = "select %1$s from %2$s order by random() limit 100";

	// query to get columns' name
	private static final String queryForColumnsPostgres = "SELECT column_name FROM information_schema.COLUMNS WHERE table_name ='%1$s' and table_schema='%2$s' order by ordinal_position asc"; // order
																																																// rows
																																																// in
																																																// ascending
																																																// order
																																																// on
																																																// the
																																																// ordinal_position
																																																// attribute
	private static final String queryForColumnsMysql = "SELECT column_name FROM information_schema.COLUMNS WHERE table_name ='%1$s' and table_schema='%2$s'order by ordinal_position asc";

	private static final String MYSQL = "MySQL";
	private static final String POSTGRES = "Postgres";

	private List<String> listColumns = null;

	public Sampler() {

	}

	// retrieve the first 100 rows of a table
	public List<Object> sampleOnTable(ConnectionManager connection,
			SessionFactory dbSession, String DBType, String tableName,
			String schemaName, List<String> DataTypeColumns) throws Exception {

		AnalysisLogger.getLogger().debug(
				"Sampler->starting the Sample on table operation");

		AnalysisLogger.getLogger().debug(
				"Sampler->retrieving the first 100 rows");

		// preparing the query to get the first 100 rows of a table

		List<Object> resultSet = null;

		String querySampleOnTable = null;

		// get a formatted list columns

		String listAttributes = null;
		listAttributes = getQuery(connection, dbSession, DBType, tableName,
				schemaName, DataTypeColumns);

		// preparing the query

		if (DBType.equals(POSTGRES)) {

			// the full name equal to "schemaname.tablename"
			tableName = schemaName + "." + "\"" + tableName + "\"";

			querySampleOnTable = String.format(queryForSampleOnTablePostgres,
					listAttributes, tableName);

		}

		if (DBType.equals(MYSQL)) {

			// the full name equal to "dbname.tablename"
			tableName = schemaName + "." + tableName;

			querySampleOnTable = String.format(queryForSampleOnTableMysql,
					listAttributes, tableName);

		}

		AnalysisLogger.getLogger()
				.debug("Sampler->preparing to submit the query: "
						+ querySampleOnTable);

		resultSet = connection.executeQuery(querySampleOnTable, dbSession);

		AnalysisLogger.getLogger().debug(
				"Sampler->query submitted successfully");

		if (resultSet == null) {
			AnalysisLogger
					.getLogger()
					.debug("Sampler->Error: The table has not rows. Sample operation not possible");

			throw new Exception(
					"The resulting table has not rows. Sample operation not possible");

		}

		// return the first 100 rows
		return resultSet;

	}

	// preparing the query to get the first 100 rows of a table
	private String getQuery(ConnectionManager connection,
			SessionFactory dbSession, String DBType, String tableName,
			String schemaName, List<String> DataTypeColumns) throws Exception {

		// List<String> listColumns = null;

		// get columns list
		listColumns = getListColumns(connection, dbSession, DBType, tableName,
				schemaName);

		// String querySampleOnTable = null;
		String listAttributes = null;

		if (listColumns != null) {

			// preparing the query to get the first 100 rows of a table

			// preparing the query with formatted list column names

			listAttributes = "";
			String attribute = null;

			for (int i = 0; i < listColumns.size(); i++) {

				if (DBType.equals(POSTGRES)) {

					//to manage postgis data types
					if((DataTypeColumns.get(i).equals("geometry")) || (DataTypeColumns.get(i).equals("geography"))){
						attribute = "st_astext("+listColumns.get(i)+") as "+listColumns.get(i)+", ";
						
						if (i == (listColumns.size() - 1)) {
							attribute = "st_astext("+listColumns.get(i)+") as "+listColumns.get(i);
						}
					}else{
						// attribute = "CAST(" + listColumns.get(i) + " as text), ";
						attribute = "CAST(" + "\"" +listColumns.get(i)+ "\""
								+ " as character varying), ";

						if (i == (listColumns.size() - 1)) {

							// attribute = "CAST(" + listColumns.get(i) +
							// " as text)";
							attribute = "CAST(" + "\"" + listColumns.get(i)+ "\""
									+ " as character varying)";

						}
					}
//					//to manage postgis data types
//					if((DataTypeColumns.get(i).equals("geometry")) || (DataTypeColumns.get(i).equals("geography"))){
//						attribute = "SUBSTRING(st_astext("+listColumns.get(i)+") FROM 1 FOR 255), ";
//						
//						if (i == (listColumns.size() - 1)) {
//							attribute = "SUBSTRING(st_astext("+listColumns.get(i)+") FROM 1 FOR 255)";
//						}
//					}else{
//						// attribute = "CAST(" + listColumns.get(i) + " as text), ";
//						attribute = "CAST(" + "\"" +listColumns.get(i)+ "\""
//								+ " as character varying(255)), ";
//
//						if (i == (listColumns.size() - 1)) {
//
//							// attribute = "CAST(" + listColumns.get(i) +
//							// " as text)";
//							attribute = "CAST(" + "\"" + listColumns.get(i)+ "\""
//									+ " as character varying(255))";
//
//						}
//					}

				}

				// for a value whose datatype is char or varchar a cast to utf8
				// is performed while for other datatypes in order to return a
				// correct value a cast to binary and a second to char are
				// performed.(because a cast of large numerical values to char
				// are truncated)

				if (DBType.equals(MYSQL)) {

					if (DataTypeColumns.get(i).contains("char")) {

						// attribute = "CAST(" + listColumns.get(i) +
						// " as CHAR CHARACTER SET utf8), ";
						// attribute = "CONVERT(" + listColumns.get(i) +
						// ", CHAR), ";
						attribute = "CAST(" + "`" + listColumns.get(i) + "`"
								+ " as CHAR CHARACTER SET utf8), ";

						if (i == (listColumns.size() - 1)) {

							// attribute = "CAST(" + listColumns.get(i) +
							// " as CHAR CHARACTER SET utf8)";

							// attribute = "CONVERT(" + listColumns.get(i) +
							// ", BINARY)";

							attribute = "CAST(" + "`" + listColumns.get(i)
									+ "`" + " as CHAR CHARACTER SET utf8)";

						}

					} else {

						attribute = "CAST(CAST("
								+ "`"
								+ listColumns.get(i)
								+ "`"
								+ " as BINARY) as CHAR CHARACTER SET utf8), ";

						if (i == (listColumns.size() - 1)) {

							// attribute = "CAST(" + listColumns.get(i) +
							// " as CHAR CHARACTER SET utf8)";

							// attribute = "CONVERT(" + listColumns.get(i) +
							// ", BINARY)";

							attribute = "CAST(CAST("
									+ "`"
									+ listColumns.get(i)
									+ "`"
									+ " as BINARY) as CHAR CHARACTER SET utf8)";

						}

					}

//					if (DataTypeColumns.get(i).contains("char")) {
//
//						// attribute = "CAST(" + listColumns.get(i) +
//						// " as CHAR CHARACTER SET utf8), ";
//						// attribute = "CONVERT(" + listColumns.get(i) +
//						// ", CHAR), ";
//						attribute = "CAST(" + "`" + listColumns.get(i) + "`"
//								+ " as CHAR(255) CHARACTER SET utf8), ";
//
//						if (i == (listColumns.size() - 1)) {
//
//							// attribute = "CAST(" + listColumns.get(i) +
//							// " as CHAR CHARACTER SET utf8)";
//
//							// attribute = "CONVERT(" + listColumns.get(i) +
//							// ", BINARY)";
//
//							attribute = "CAST(" + "`" + listColumns.get(i)
//									+ "`" + " as CHAR(255) CHARACTER SET utf8)";
//
//						}
//
//					} else {
//
//						attribute = "CAST(CAST("
//								+ "`"
//								+ listColumns.get(i)
//								+ "`"
//								+ " as BINARY) as CHAR(255) CHARACTER SET utf8), ";
//
//						if (i == (listColumns.size() - 1)) {
//
//							// attribute = "CAST(" + listColumns.get(i) +
//							// " as CHAR CHARACTER SET utf8)";
//
//							// attribute = "CONVERT(" + listColumns.get(i) +
//							// ", BINARY)";
//
//							attribute = "CAST(CAST("
//									+ "`"
//									+ listColumns.get(i)
//									+ "`"
//									+ " as BINARY) as CHAR(255) CHARACTER SET utf8)";
//
//						}
//
//					}

				}

				listAttributes = listAttributes + attribute;

			}

		}

		return listAttributes;

	}

	// get list columns of a table
	private List<String> getListColumns(ConnectionManager connection,
			SessionFactory dbSession, String DBType, String tableName,
			String schemaName) throws Exception {

		AnalysisLogger.getLogger().debug("Sampler->retrieving column names");

		// preparing the query to get columns' names
		String queryColumns = null;

		// build the query for database postgres. The parameter "schemaName" is
		// the schema name.
		if (DBType.equals(POSTGRES)) {

			queryColumns = String.format(queryForColumnsPostgres, tableName,
					schemaName);

		}

		// build the query for database mysql. The parameter "schemaName" is the
		// database name.
		if (DBType.equals(MYSQL)) {

			queryColumns = String.format(queryForColumnsMysql, tableName,
					schemaName);

		}

		List<Object> columnsSet = null;
		List<String> listColumns = null;

		columnsSet = connection.executeQuery(queryColumns, dbSession);

		AnalysisLogger.getLogger().debug(
				"Sampler->query submitted successfully: " + queryColumns);

		if (columnsSet != null) {

			listColumns = new ArrayList<String>();

			// //print check
			// AnalysisLogger.getLogger().debug(
			// "DatabaseManagement->size: " + columnsSet.size());

			for (int i = 0; i < columnsSet.size(); i++) {

				Object element = columnsSet.get(i);

				// //print check
				// AnalysisLogger.getLogger().debug(
				// "Sampler->values: " + element);

				ArrayList<Object> listvalues = new ArrayList<Object>(
						((LinkedHashMap<String, Object>) element).values());

				// //print check
				// AnalysisLogger.getLogger().debug(
				// "Sampler->values: " + listvalues);

				listColumns.add((String) listvalues.get(0));

			}

		}

		return listColumns;

	}

	// retrieve 100 rows of a table randomly that have the maximum number of
	// columns not null
	public List<Object> smartSampleOnTable(ConnectionManager connection,
			SessionFactory dbSession, String DBType, String tableName,
			String schemaName, long NumRows, List<String> DataTypeColumns)
			throws Exception {

		AnalysisLogger.getLogger().debug(
				"Sampler->starting the Smart Sample on table operation");

		if (NumRows == 0) {
			throw new Exception(
					"The table has 0 rows");
		}

		// // computation of the iterations number
		// int NIterations = computeNumberIterations(NumRows);
		//
		// AnalysisLogger.getLogger().debug(
		// "Sampler->Iterations number: " + NIterations);
		//
		// // computation of the 100 rows randomly
		//
		// AnalysisLogger.getLogger().debug("Sampler->retrieving rows");

		List<Object> rows = null;

		// if is rows number <= 700000 then the pure smart sample procedure is
		// performed otherwise a not pure smart sample procedure is performed in
		// order to solve a bug with the random function in postgres

		// if ((NumRows > 700000) && (DBType.equals(POSTGRES))) { // Postgres
		// // compute the smart sample on a table
		// rows = computeSmartSampleWithThreshold(connection, dbSession,
		// DBType, tableName, schemaName, NumRows, DataTypeColumns);
		//
		// }

		if (NumRows > 700000) {
			// compute the smart sample on a table
			rows = computeSmartSampleWithThreshold(connection, dbSession,
					DBType, tableName, schemaName, NumRows, DataTypeColumns);
		} else {

			// computation of the iterations number
			int NIterations = computeNumberIterations(NumRows);

			AnalysisLogger.getLogger().debug(
					"Sampler->Iterations number: " + NIterations);

			// computation of the 100 rows randomly

			AnalysisLogger.getLogger().debug("Sampler->retrieving rows");

			// compute the smart sample on a table
			rows = computeSmartSample(connection, dbSession, DBType, tableName,
					schemaName, NIterations, DataTypeColumns,
					DataTypeColumns.size());
		}

		// if ((NumRows <= 700000) && (DBType.equals(POSTGRES))) { // Postgres
		//
		// // computation of the iterations number
		// int NIterations = computeNumberIterations(NumRows);
		//
		// AnalysisLogger.getLogger().debug(
		// "Sampler->Iterations number: " + NIterations);
		//
		// // computation of the 100 rows randomly
		//
		// AnalysisLogger.getLogger().debug("Sampler->retrieving rows");
		//
		// // compute the smart sample on a table
		// rows = computeSmartSample(connection, dbSession, DBType, tableName,
		// schemaName, NIterations, DataTypeColumns,
		// DataTypeColumns.size());
		//
		// }
		//
		// else if ((NumRows > 700000) && (DBType.equals(POSTGRES))) { //
		// Postgres
		// // compute the smart sample on a table
		// rows = computeSmartSampleWithThreshold(connection, dbSession,
		// DBType, tableName, schemaName, NumRows, DataTypeColumns);
		//
		// } else { // MySQL
		//
		// // computation of the iterations number
		// int NIterations = computeNumberIterations(NumRows);
		//
		// AnalysisLogger.getLogger().debug(
		// "Sampler->Iterations number: " + NIterations);
		//
		// // computation of the 100 rows randomly
		//
		// AnalysisLogger.getLogger().debug("Sampler->retrieving rows");
		//
		// // compute the smart sample on a table
		// rows = computeSmartSample(connection, dbSession, DBType, tableName,
		// schemaName, NIterations, DataTypeColumns,
		// DataTypeColumns.size());
		//
		// }

		if (rows == null) {

			AnalysisLogger
					.getLogger()
					.debug("Sampler->Error: the Smart Sample operation on table  has not returned rows");

			throw new Exception(
					"The Smart Sample operation on table has not returned rows");

		}

		AnalysisLogger.getLogger().debug("Sampler->rows retrieved");

		// return the first 100 rows
		return rows;

	}

	private int computeNumberIterations(long NumRows) {

		AnalysisLogger.getLogger().debug(
				"Sampler->processing iterations number");

		AnalysisLogger.getLogger().debug("Sampler->rows number: " + NumRows);

		// build the formula k=(((-0.8)*NumRows)/10000)+1
		double k = (((-0.8) * NumRows) / 10000) + 1.0;

		// if the the parameter k is negative, the sign must be changed
		double paramK = 0.0;
		if (Double.compare(k, 0.0) < 0) {

			paramK = k * (-1);

		} else {
			paramK = k;

		}

		AnalysisLogger.getLogger().debug(
				"Sampler->parameter K value: " + paramK);

		long NumElements = Math.min(NumRows, (long) 10000);

		AnalysisLogger.getLogger().debug(
				"Sampler->choosing the min value of elements: " + NumElements);

		// to build the formula NIterations=(k/200)*Nelementi

		double NumIterations = (paramK / 200) * NumElements;

		AnalysisLogger.getLogger().debug(
				"Sampler->iterations number: " + NumIterations);

		double Iterations = Math.max(Math.round(NumIterations), 1);

		AnalysisLogger.getLogger()
				.debug("Sampler-> choosing the max value of iterations: "
						+ Iterations);

		double NumIts = Math.min(Iterations, 2.0);

		AnalysisLogger.getLogger().debug(
				"Sampler-> choosing the min value of iterations: " + NumIts);

		// to round the value (with a rint logic)
		return (int) (Math.rint(NumIts));

	}

	// compute the SmartSampleTable. It extracts 200 rows randomly for each
	// iteration. Then it checks if the row with index equal to 100 has the max
	// score (that is equal to the columns' number). In this case the row list
	// is cut in order to return the first 100 rows.
	private List<Object> computeSmartSample(ConnectionManager connection,
			SessionFactory dbSession, String DBType, String tablename,
			String schemaName, int NIterations, List<String> DataTypeColumns,
			int ColumnSize) throws Exception {

		List<Object> resultSet = null;

		String query = null;

		boolean removal = false;

		// map that contains for each row two information: the index
		// corresponding to the row and the score number that is the columns'
		// number with not null value
		// HashMap<Integer, Integer> MapRows = new HashMap<Integer, Integer>();

		List<RowScore> listRows = new ArrayList<RowScore>();

		// get a formatted list columns

		String listAttributes = null;
		listAttributes = getQuery(connection, dbSession, DBType, tablename,
				schemaName, DataTypeColumns);

		// compute score for each row of the table

		// build the query for database postgres
		if (DBType.equals(POSTGRES)) {

			// the full name equal to "schemaname.tablename"
			tablename = schemaName + "." + "\"" + tablename + "\"";

			query = String.format(queryForSmartSampleOnTablePostgres,
					listAttributes, tablename);

		}
		// build the query for database mysql
		if (DBType.equals(MYSQL)) {

			// the full name equal to "dbname.tablename"
			tablename = schemaName + "." + tablename;

			query = String.format(queryForSmartSampleOnTableMysql,
					listAttributes, tablename);

		}

		AnalysisLogger.getLogger().debug(
				"Sampler->building the query extracting 200 rows randomly");

		Object[] columnArray = null;

		// define columns number with the threshold
		AnalysisLogger.getLogger().debug(
				"Sampler-> column array dimension: " + ColumnSize);

		double thresholdRank = ((ColumnSize) * 80);
		thresholdRank = thresholdRank / 100;

		double valCeil = Math.round(thresholdRank);

		AnalysisLogger.getLogger().debug(
				"Sampler-> number column generated by the threshold: "
						+ thresholdRank + " rounded value: " + valCeil);

		// extract 200 rows randomly for each iteration
		extractionRows: for (int i = 0; i < NIterations; i++) {

			// System.out.println("index iteration: " + i);

			AnalysisLogger.getLogger().debug(
					"Sampler->executing the query: " + query);

			resultSet = connection.executeQuery(query, dbSession);

			if (resultSet != null) {

				int numrows = resultSet.size();

				AnalysisLogger.getLogger().debug(
						"Sampler->rows number: " + numrows);

				AnalysisLogger
						.getLogger()
						.debug("Sampler->computing the score and sort the row list in a reverse natural order");

				// build the list with 200 rows
				for (int j = 0; j < numrows; j++) {

					Object element = resultSet.get(j);

					ArrayList<Object> listvalues = new ArrayList<Object>(
							((LinkedHashMap<String, Object>) element).values());

					columnArray = listvalues.toArray();

					// compute the score for each row of the table
					int score = computeColumnScore(columnArray);

					// //check row score
					// AnalysisLogger.getLogger().debug(
					// "Sampler->row: " + j + " " + "score: " + score);

					RowScore rs = new RowScore(element, score);

					// insert the row in the list
					listRows.add(rs);

					// //check the sorting of a row
					// AnalysisLogger
					// .getLogger()
					// .debug("Sampler->sorting the list in a reverse natural order");

					// sort by reverse natural order
					Collections.sort(listRows, Collections.reverseOrder());

					// After each iteration there is a check to verify the row
					// (corresponding to the element with index=100). If the
					// score
					// of
					// this row is equal to the columns' number then the
					// previous
					// rows
					// in the list have all columns value not null

					// int size = listRows.size();

					// to check if the row with index 100 has the max score

					// thresholdRank 80%

					if (listRows.size() >= 100) {
						int value = listRows.get(99).getScore();

						// if (value == columnArray.length) {

						// AnalysisLogger.getLogger().debug(
						// "Sampler-> column array dimension: "
						// + columnArray.length);
						//
						// double thresholdRank = ((columnArray.length) * 80);
						// thresholdRank = thresholdRank / 100;
						//
						// double valCeil = Math.round(thresholdRank);
						//
						// AnalysisLogger.getLogger().debug(
						// "Sampler-> number column generated by the threshold: "
						// + thresholdRank
						// + " rounded value: " + valCeil);

						if (value >= (int) valCeil) {

							// //check row score
							// for (int k = 0; k < listRows.size(); k++) {
							//
							//
							// AnalysisLogger.getLogger().debug(
							// "Sampler->row with index: " + k
							// + " score "
							// + listRows.get(k).getScore());
							// }

							removal = true;

							AnalysisLogger.getLogger().debug(
									"Sampler->row 100 with score: " + value);

							AnalysisLogger.getLogger().debug(
									"Sampler->starting the removal operation");

							// Remove the elements from index 100 if the list's
							// size
							// is
							// greater than 100
							if (listRows.size() > 100) {

								int numElemToDelete = listRows.size() - 100;

								AnalysisLogger.getLogger().debug(
										"Sampler->number of rows to delete: "
												+ numElemToDelete);

								while (numElemToDelete != 0) {

									listRows.remove(100);

									numElemToDelete = numElemToDelete - 1;

								}

							}

							break extractionRows;

						}
					}

				}

			}

			else {

				return null;

			}

		}

		// Remove the elements from index 100 if the list's size is
		// greater than 100 and if this operation has not been done previously.

		if ((listRows.size() > 100) && (removal == false)) {

			// print check
			// for (int k = 0; k < listRows.size(); k++) {
			//
			// AnalysisLogger.getLogger().debug(
			// "Sampler->row with index: " + k + " score "
			// + listRows.get(k).getScore());
			// }

			AnalysisLogger.getLogger().debug(
					"Sampler->starting the removal operation");

			int numElemToDelete = listRows.size() - 100;

			AnalysisLogger.getLogger().debug(
					"Sampler->number of rows to delete: " + numElemToDelete);

			// cut the list of rows in order to have only 100 rows
			while (numElemToDelete != 0) {

				RowScore row = listRows.remove(100);

				// AnalysisLogger.getLogger().debug(
				// "Sampler->removing row with score: " + row.getScore());

				numElemToDelete = numElemToDelete - 1;

			}

		}

		// return the list of 100 rows
		List<Object> rows = new ArrayList<Object>();

		AnalysisLogger.getLogger().debug(
				"Sampler->preparing the result (the row list): ");

		for (int i = 0; i < listRows.size(); i++) {

			// //check rows added in the final result
			// AnalysisLogger.getLogger().debug(
			// "Sampler->adding row with index: " + i + " " +
			// listRows.get(i).getRow());

			rows.add(listRows.get(i).getRow());
		}

		return rows;

	}

	// compute the SmartSampleTable considering the threshold 700000 on the rows
	// number . It extracts 200 rows randomly for each
	// iteration. Then it checks if the row with index equal to 100 has the max
	// score (that is equal to the columns' number). In this case the row list
	// is cut in order to return the first 100 rows.
	private List<Object> computeSmartSampleWithThreshold(
			ConnectionManager connection, SessionFactory dbSession,
			String DBType, String tablename, String schemaName, long NumRows,
			List<String> DataTypeColumns) throws Exception {

		// Define threshold
		int threshold = 700000;

		int X, Y;

		// Generate randomly two indexes used to execute two queries

		Random rn = new Random();

		if ((threshold + 200) <= NumRows) {

			AnalysisLogger.getLogger().debug(
					"Sampler-> 700000+200 <= rows number");

			X = rn.nextInt(threshold + 1) + 200; // generate a number in
													// range [200-700000]

			AnalysisLogger.getLogger().debug("Sampler->X index: " + X);

			// Generate a Y index

			// Define Lower and Upper Index (LI and UL) of a range

			int LI = X - 200;
			int UI = X + 200;

			AnalysisLogger.getLogger().debug(
					"Sampler->Lower Index of the range: " + LI);

			AnalysisLogger.getLogger().debug(
					"Sampler->Upper Index of the range: " + UI);

			int a;

			do {

				a = rn.nextInt(threshold + 1) + 0;

			} while (!((a < UI) || (a > LI)));

			Y = a;

			AnalysisLogger.getLogger().debug("Sampler->Y index: " + Y);

		} else {

			AnalysisLogger.getLogger().debug(
					"Sampler-> 700000+200 > rows number");
			int offset = ((int) NumRows - threshold);
			int valForUpperIndex = 200 - offset;
			int UpperIndex = threshold - valForUpperIndex;

			// Generate an X index
			X = rn.nextInt(UpperIndex + 1) + 200; // generate a number in
													// range
													// [200-UpperIndex]

			AnalysisLogger.getLogger().debug("Sampler->X index: " + X);

			// Generate a Y index

			// Define Lower and Upper Index (LI and UL) of a range

			int LI = X - 200;
			int UI = X + 200;

			AnalysisLogger.getLogger().debug(
					"Sampler->Lower Index of the range: " + LI);

			AnalysisLogger.getLogger().debug(
					"Sampler->Upper Index of the range: " + UI);

			int a;

			do {

				a = rn.nextInt(UpperIndex + 1) + 0;

			} while (!((a < UI) || (a > LI)));

			Y = a;

		}

		int[] indexes = new int[2];

		indexes[0] = X;
		indexes[1] = Y;

		// AnalysisLogger.getLogger().debug("Sampler->X index: " + indexes[0]);

		// AnalysisLogger.getLogger().debug("Sampler->Y index: " + indexes[1]);

		// start sample operation

		List<Object> resultSet = null;

		String query = null;

		boolean removal = false;

		// map that contains for each row two information: the index
		// corresponding to the row and the score number that is the columns'
		// number with not null value
		// HashMap<Integer, Integer> MapRows = new HashMap<Integer, Integer>();

		List<RowScore> listRows = new ArrayList<RowScore>();

		// get a formatted list columns

		String listAttributes = null;
		listAttributes = getQuery(connection, dbSession, DBType, tablename,
				schemaName, DataTypeColumns);

		// compute score for each row of the table

		// // build the query for database postgres
		// if (DBType.equals(POSTGRES)) {
		//
		// query = String.format(
		// queryForSmartSampleWithThresholdOnTablePostgres,
		// listAttributes, tablename);
		//
		// }
		// // build the query for database mysql
		// if (DBType.equals(MYSQL)) {
		//
		// query = String.format(queryForSmartSampleOnTableMysql,
		// listAttributes, tablename);
		//
		// }

		AnalysisLogger.getLogger().debug(
				"Sampler->building the query extracting 200 rows randomly");

		Object[] columnArray = null;

		// extract 200 rows randomly for each iteration

		// Define the two queries.One query uses the X index, one query uses the
		// Y index. Each query extract 200 rows.

		// computation for the smart procedure

		extractionRows: for (int i = 0; i < 2; i++) {

			// build the query for database postgres
			if (DBType.equals(POSTGRES)) {

				// the full name equal to "schemaname.tablename"
				String tableName = "";
				tableName = schemaName + "." + "\"" + tablename + "\"";
				
                query = String.format(
						queryForSmartSampleWithThresholdOnTablePostgres,
						listAttributes, tableName, indexes[i]);
			}

			// build the query for database mysql
			if (DBType.equals(MYSQL)) {
				// the full name equal to "dbname.tablename"
				String tableName = "";
				tableName = schemaName + "." + tablename;
           
				query = String.format(
						queryForSmartSampleWithThresholdOnTableMysql,
						listAttributes, tableName, indexes[i]);
			}

			AnalysisLogger.getLogger().debug(
					"Sampler->executing the query: " + query);

			resultSet = connection.executeQuery(query, dbSession);

			if (resultSet != null) {

				int numrows = resultSet.size();

				AnalysisLogger.getLogger().debug(
						"Sampler->rows number: " + numrows);

				AnalysisLogger
						.getLogger()
						.debug("Sampler->computing the score and sorting the row list in a reverse natural order");

				// build the list with 200 rows
				for (int j = 0; j < numrows; j++) {

					Object element = resultSet.get(j);

					ArrayList<Object> listvalues = new ArrayList<Object>(
							((LinkedHashMap<String, Object>) element).values());

					columnArray = listvalues.toArray();

					// compute the score for each row of the table
					int score = computeColumnScore(columnArray);

					// //check row score
					// AnalysisLogger.getLogger().debug(
					// "Sampler->row: " + j + " " + "score: " + score);

					RowScore rs = new RowScore(element, score);

					// insert the row in the list
					listRows.add(rs);

					// //check the sorting of a row
					// AnalysisLogger
					// .getLogger()
					// .debug("Sampler->sorting the list in a reverse natural order");

					// sort by reverse natural order
					Collections.sort(listRows, Collections.reverseOrder());

					// After each iteration there is a check to verify the row
					// (corresponding to the element with index=100). If the
					// score
					// of
					// this row is equal to the columns' number then the
					// previous
					// rows
					// in the list have all columns value not null

					// int size = listRows.size();

					// to check if the row with index 100 has the max score

					// threshold 80%

					if (listRows.size() >= 100) {
						int value = listRows.get(99).getScore();

						// if (value == columnArray.length) {

						// print check
						// AnalysisLogger.getLogger().debug(
						// "Sampler-> column array dimension: "
						// + columnArray.length);

						double thresholdRank = ((columnArray.length) * 80);
						thresholdRank = thresholdRank / 100;

						double valCeil = Math.round(thresholdRank);

						// print check
						// AnalysisLogger.getLogger().debug(
						// "Sampler-> threshold: " + thresholdRank
						// + " rounded value: " + valCeil);

						if (value >= (int) valCeil) {

							// //check row score
							// for (int k = 0; k < listRows.size(); k++) {
							//
							// AnalysisLogger.getLogger().debug(
							// "Sampler->row with index: " + k
							// + " score "
							// + listRows.get(k).getScore());
							// }

							removal = true;

							AnalysisLogger.getLogger().debug(
									"Sampler->row 100 with score: " + value);

							AnalysisLogger.getLogger().debug(
									"Sampler->starting the removal operation");

							// Remove the elements from index 100 if the list's
							// size
							// is
							// greater than 100
							if (listRows.size() > 100) {

								int numElemToDelete = listRows.size() - 100;

								AnalysisLogger.getLogger().debug(
										"Sampler->number of rows to delete: "
												+ numElemToDelete);

								while (numElemToDelete != 0) {

									listRows.remove(100);

									numElemToDelete = numElemToDelete - 1;

								}

							}

							break extractionRows;

						}
					}

				}

			}

			else {

				return null;

			}
		}

		// Remove the elements from index 100 if the list's size is
		// greater than 100 and if this operation has not been done previously.

		if ((listRows.size() > 100) && (removal == false)) {

			// check score of the row list

			// for (int k = 0; k < listRows.size(); k++) {
			//
			// AnalysisLogger.getLogger().debug(
			// "Sampler->row with index: " + k + " score "
			// + listRows.get(k).getScore());
			// }

			AnalysisLogger.getLogger().debug(
					"Sampler->starting the removal operation");

			int numElemToDelete = listRows.size() - 100;

			AnalysisLogger.getLogger().debug(
					"Sampler->number of rows to delete: " + numElemToDelete);

			// cut the list of rows in order to have only 100 rows
			while (numElemToDelete != 0) {

				RowScore row = listRows.remove(100);

				// AnalysisLogger.getLogger().debug(
				// "Sampler->removing row with score: " + row.getScore());

				numElemToDelete = numElemToDelete - 1;

			}

		}

		// return the list of 100 rows
		List<Object> rows = new ArrayList<Object>();

		AnalysisLogger.getLogger().debug(
				"Sampler->preparing the result (the row list): ");

		for (int i = 0; i < listRows.size(); i++) {

			// check rows added in the final result
			// AnalysisLogger.getLogger().debug(
			// "Sampler->adding row with index: " + i + " " +
			// listRows.get(i).getRow());

			rows.add(listRows.get(i).getRow());

		}

		return rows;

	}

	// compute the score for each array (the score is the number of table
	// columns with value not null and not empty)
	private int computeColumnScore(Object[] columnArray) {

		int score = 0;

		for (int i = 0; i < columnArray.length; i++) {

			if (columnArray[i] != null) {

				if (!(columnArray[i].toString().equals(""))) {

					score++;

				}

			}

		}

		return score;

	}

	// // retrieve 100 rows of a table randomly
	// public List<Object> randomSampleOnTable(ConnectionManager connection,
	// SessionFactory dbSession, String DBType, String tableName,
	// String schemaName, List<String> DataTypeColumns) throws Exception {
	//
	// AnalysisLogger.getLogger().debug(
	// "Sampler->starting the Random Sample on table operation");
	//
	// AnalysisLogger.getLogger().debug("Sampler->retrieving the 100 rows");
	//
	// // preparing the query to get the first 100 rows of a table
	//
	// List<Object> resultSet = null;
	//
	// String querySampleOnTable = null;
	//
	// // get a formatted list columns
	//
	// String listAttributes = null;
	// listAttributes = getQuery(connection, dbSession, DBType, tableName,
	// schemaName, DataTypeColumns);
	//
	// // preparing the query
	//
	// if (DBType.equals(POSTGRES)) {
	//
	// querySampleOnTable = String.format(
	// queryForRandomSampleOnTablePostgres, listAttributes,
	// tableName);
	//
	// }
	//
	// if (DBType.equals(MYSQL)) {
	//
	// querySampleOnTable = String
	// .format(queryForRandomSampleOnTableMysql, listAttributes,
	// tableName);
	//
	// }
	//
	// AnalysisLogger.getLogger()
	// .debug("Sampler->preparing to submit the query: "
	// + querySampleOnTable);
	//
	// resultSet = connection.executeQuery(querySampleOnTable, dbSession);
	//
	// AnalysisLogger.getLogger().debug(
	// "Sampler->query submitted successfully");
	//
	// if (resultSet == null) {
	// AnalysisLogger
	// .getLogger()
	// .debug("Sampler->Error: The resulting table has not rows. Sample operation not possible");
	//
	// throw new Exception(
	// "The resulting table has not rows. Sample operation not possible");
	//
	// }
	//
	// // return the first 100 rows
	// return resultSet;
	//
	// }

	// retrieve 100 rows of a table randomly
	public List<Object> randomSampleOnTable(ConnectionManager connection,
			SessionFactory dbSession, String DBType, String tableName,
			String schemaName, long NumRows, List<String> DataTypeColumns)
			throws Exception {

		AnalysisLogger.getLogger().debug(
				"Sampler->starting the Random Sample on table operation");

		AnalysisLogger.getLogger().debug("Sampler->retrieving the 100 rows");

		// preparing the query to get the first 100 rows of a table

		List<Object> resultSet = null;

		String querySampleOnTable = null;

		// get a formatted list columns

		String listAttributes = null;

		listAttributes = getQuery(connection, dbSession, DBType, tableName,
				schemaName, DataTypeColumns);

		// preparing the query

		// if is rows number <= 700000 then the pure random sample procedure is
		// performed otherwise a not pure random sample procedure is performed
		// in
		// order to solve a bug with the random function in postgres

		// if ((NumRows <= 700000) && (DBType.equals(POSTGRES))) { // Postgres
		//
		// //the full name equal to "schemaname.tablename"
		// tableName=schemaName+"."+ "\""+tableName+"\"";
		//
		// querySampleOnTable = String.format(
		// queryForRandomSampleOnTablePostgres, listAttributes,
		// tableName);
		//
		// }

		if (NumRows <= 700000) {

			if (DBType.equals(POSTGRES)) {
				// the full name equal to "schemaname.tablename"
				tableName = schemaName + "." + "\"" + tableName + "\"";

				querySampleOnTable = String.format(
						queryForRandomSampleOnTablePostgres, listAttributes,
						tableName);
			}

			if (DBType.equals(MYSQL)) {
				// the full name equal to "dbname.tablename"
				tableName = schemaName + "." + tableName;
				querySampleOnTable = String.format(
						queryForRandomSampleOnTableMysql, listAttributes,
						tableName);
			}

		}

		// if ((NumRows > 700000) && (DBType.equals(POSTGRES))) { // Postgres

		if (NumRows > 700000) {

			// generate an index randomly to execute the query

			// Define threshold
			int threshold = 700000;
			int X;

			// generate an index used to execute the query
			Random rn = new Random();

			if ((threshold + 100) <= NumRows) {

				X = rn.nextInt(threshold + 1) + 100; // generate a number in
														// range [100-700000]
				AnalysisLogger.getLogger().debug("Sampler->X index: " + X);

			}

			else {

				AnalysisLogger.getLogger().debug(
						"Sampler-> 700000+100 > rows number");

				int offset = ((int) NumRows - threshold);
				int valForUpperIndex = 100 - offset;
				int UpperIndex = threshold - valForUpperIndex;

				// Generate an X index
				X = rn.nextInt(UpperIndex + 1) + 100; // generate a number in
														// range
														// [100-UpperIndex]

				AnalysisLogger.getLogger().debug("Sampler->X index: " + X);

			}

			if (DBType.equals(POSTGRES)) {
				// the full name equal to "schemaname.tablename"
				tableName = schemaName + "." + "\"" + tableName + "\"";

				querySampleOnTable = String.format(
						queryForRandomSampleWithThresholdOnTablePostgres,
						listAttributes, tableName, X);

			}

			if (DBType.equals(MYSQL)) { // MySQL

				// the full name equal to "dbname.tablename"
				tableName = schemaName + "." + tableName;

				// querySampleOnTable = String
				// .format(queryForRandomSampleOnTableMysql, listAttributes,
				// tableName);

				querySampleOnTable = String.format(
						queryForRandomSampleWithThresholdOnTableMysql,
						listAttributes, tableName, X);

			}

		}

		// if (DBType.equals(MYSQL)) { // MySQL
		//
		// // the full name equal to "dbname.tablename"
		// tableName = schemaName + "." + tableName;
		//
		// querySampleOnTable = String
		// .format(queryForRandomSampleOnTableMysql, listAttributes,
		// tableName);
		//
		// }

		AnalysisLogger.getLogger()
				.debug("Sampler->preparing to submit the query: "
						+ querySampleOnTable);

		resultSet = connection.executeQuery(querySampleOnTable, dbSession);

		AnalysisLogger.getLogger().debug(
				"Sampler->query submitted successfully");

		if (resultSet == null) {
			AnalysisLogger
					.getLogger()
					.debug("Sampler->Error: The resulting table has not rows. Sample operation not possible");

			throw new Exception(
					"The resulting table has not rows. Sample operation not possible");

		}

		// return the first 100 rows
		return resultSet;

	}

	// to retrieve the columns names of a table
	public List<String> getListColumns() {

		return listColumns;

	}

}
