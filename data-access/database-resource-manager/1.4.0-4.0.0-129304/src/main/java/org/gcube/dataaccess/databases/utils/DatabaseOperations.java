package org.gcube.dataaccess.databases.utils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.hibernate.SessionFactory;

/** Class that allows to performs some operations on a database */
public class DatabaseOperations {

	private String DBType = ""; // database's type

	// private static final String QueryPostgres =
	// "select count(*) from \"%1$s\" limit 1";
	// private static final String QueryPostgres =
	// "select count(*) from (select * from \"%1$s\" limit 1) as a";
	private static final String QueryPostgres = "select count(*) from (select * from %1$s limit 1) as a";
	// private static final String QueryMysql =
	// "select count(*) from (select * from `%1$s` limit 1) as a";
	private static final String QueryMysql = "select count(*) from (select * from %1$s limit 1) as a";
	// private static final String QueryMysql =
	// "select count(*) from `%1$s` limit 1";
	private static final String ActualRowsNumberQueryPostgres = "select count(*) from (select * from %1$s) as a";

	// private static final String Query = "select * from %1$s limit 1";
	// private static final String countQuery = "select count(*) from %1$s";
	// private static final String explainQuery = "explain select * from %1$s";
	// private static final String explainQueryPostgres =
	// "explain select * from \"%1$s\"";
	private static final String explainQueryPostgres = "explain select * from %1$s";
	// private static final String explainQueryMysql =
	// "explain select * from `%1$s`";
	private static final String explainQueryMysql = "explain select * from %1$s";

	private static final String MYSQL = "MySQL";
	private static final String POSTGRES = "Postgres";

	// Method that recover the schema's name of the database.
	public String getDBSchema(String configurationFile) throws Exception {

		File fl = new File(configurationFile);
		FileInputStream stream = new FileInputStream(fl);

		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(stream);

		List<Node> nodes = document
				.selectNodes("//hibernate-configuration/session-factory/property");

		Iterator<Node> nodesIterator = nodes.iterator();

		String dbschema = "";
		while (nodesIterator.hasNext()) {

			Node currentnode = nodesIterator.next();
			String element = currentnode.valueOf("@name");
			if (element.equals("connection.url")) {
				String url = currentnode.getText();
				dbschema = url.substring(url.lastIndexOf("/") + 1);
				if (dbschema.indexOf('?') > 0)
					dbschema = dbschema.substring(0, dbschema.indexOf('?'));
				AnalysisLogger.getLogger().debug(
						"DatabaseOperations->recovering the database's name: "
								+ dbschema);

				// DBType="MySQL";

				// break;
			}

			if (element.equals("connection.schemaname")) {
				String url = currentnode.getText();
				dbschema = url.substring(url.lastIndexOf("/") + 1);
				if (dbschema.indexOf('?') > 0)
					dbschema = dbschema.substring(0, dbschema.indexOf('?'));
				AnalysisLogger.getLogger().debug(
						"DatabaseOperations->recovering the schema's name: "
								+ dbschema);
				DBType = POSTGRES;
				// break;

			}

			if (DBType.equals("")) {

				DBType = MYSQL;

			}

		}

		// close stream
		stream.close();

		return dbschema;
	}

	// Method that returns the database's type
	public String getDBType() {

		return DBType;

	}

	// Method that calculate the estimated number of rows
	public long calculateElements(ConnectionManager connection, String dbType,
			String tablename, String schemaName, SessionFactory session)
			throws Exception {

		long count = 0;

		String countingQuery = null;

		if (dbType.equals(POSTGRES)) {

			// the full name equal to "schemaname.tablename"
			tablename = schemaName + "." + "\"" + tablename + "\"";

			countingQuery = String.format(QueryPostgres, tablename);

		}
		if (dbType.equals(MYSQL)) {
			// the full name equal to "dbname.tablename"
			tablename = schemaName + "." + tablename;

			countingQuery = String.format(QueryMysql, tablename);

		}

		AnalysisLogger.getLogger().debug(
				"DatabaseOperations->calculating rows' number with the query: "
						+ countingQuery);

		List<Object> result;

		// try {
		// result = DatabaseFactory.executeSQLQuery(countingQuery, session);

		result = connection.executeQuery(countingQuery, session);

		// if ((result != null) && (result.size() > 0)) {
		if (result != null) {

			Object element = result.get(0);

			ArrayList<Object> listvalues = new ArrayList<Object>(
					((LinkedHashMap<String, Object>) element).values());

			// System.out.println("Dimension: " + result.size());

			// Integer numElem = Integer.valueOf(result.get(0).toString());

			// Long numElemvalue = Long.valueOf(result.get(0).toString());

			Long numElemvalue = Long.valueOf(listvalues.get(0).toString());

			long numElem = numElemvalue.longValue();

			// if (numElem.intValue() == 0){ throw new
			// Exception("The table has not rows");}

			if (numElem > 0) {

				AnalysisLogger
						.getLogger()
						.debug("DatabaseOperations->the database has at least a row.Calculating rows' number through an estimation");

				String explain = null;

				if (dbType.equals(POSTGRES)) {

					explain = String.format(explainQueryPostgres, tablename);

				}
				if (dbType.equals(MYSQL)) {

					explain = String.format(explainQueryMysql, tablename);

				}

				// call query with explain function

				AnalysisLogger.getLogger().debug(
						"DatabaseOperations->calculating rows' number with the query: "
								+ explain);

				List<Object> resultinfo;

				// resultinfo = DatabaseFactory.executeSQLQuery(explain,
				// session);

				resultinfo = connection.executeQuery(explain, session);

				// recovery result

				if (dbType.equals(MYSQL)) {

					// Object[] resultArray = (Object[]) (resultinfo.get(0));

					Object elem = resultinfo.get(0);

					ArrayList<Object> values = new ArrayList<Object>(
							((LinkedHashMap<String, Object>) elem).values());

					// //print check
					// AnalysisLogger.getLogger().debug(
					// "DatabaseOperations->VALUE: " + values);

					BigInteger value = (BigInteger) values.get(8);

					// BigInteger value = (BigInteger) resultArray[8];

					count = value.longValue();

				}

				if (dbType.equals(POSTGRES)) {

					String var = resultinfo.get(0).toString();

					int beginindex = var.indexOf("rows");

					int lastindex = var.indexOf("width");

					var = var.substring(beginindex + 5, lastindex - 1);

					Long value = Long.valueOf(var);

					count = value.longValue();
					
					AnalysisLogger.getLogger().debug(
							"DatabaseOperations-> rows' number with explain function: "
									+ count);

					// threshold fixed to 100000 value in order to count an
					// actual rows' number if the estimated rows is < 100000
					// otherwise because PGAdmin returns a wrong rows number for
					// a table with a little rows number.

					if (count < 100000) {
						AnalysisLogger.getLogger().debug(
								"DatabaseOperations->rows' number with explain function less than the threshold with value 100000");
						// to count an actual rows number
						AnalysisLogger.getLogger().debug(
								"DatabaseOperations->calculating the actual rows' number with the query: "
										+ String.format(
												ActualRowsNumberQueryPostgres,
												tablename));

						List<Object> resultRowsNumber;
						resultRowsNumber = connection.executeQuery(String
								.format(ActualRowsNumberQueryPostgres,
										tablename), session);

						if (resultRowsNumber != null) {
							Object elem = resultRowsNumber.get(0);
							ArrayList<Object> listValues = new ArrayList<Object>(
									((LinkedHashMap<String, Object>) elem)
											.values());
							Long numElemValue = Long.valueOf(listValues.get(0)
									.toString());
							count = numElemValue.longValue();

						}
					}

				}

			}

		}

		// } catch (Exception e) {
		// TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		AnalysisLogger.getLogger().debug(
				"DatabaseOperations->rows' number calculated: " + count);

		return count;

	}

}
