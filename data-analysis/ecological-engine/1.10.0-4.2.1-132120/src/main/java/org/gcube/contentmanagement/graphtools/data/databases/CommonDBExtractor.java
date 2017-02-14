package org.gcube.contentmanagement.graphtools.data.databases;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.graphtools.abstracts.GenericDBExtractor;
import org.gcube.contentmanagement.graphtools.abstracts.SamplesTable;
import org.gcube.contentmanagement.graphtools.core.filters.Filter;
import org.gcube.contentmanagement.graphtools.data.BigSamplesTable;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.DataTypeRecognizer;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.hibernate.SessionFactory;

public class CommonDBExtractor implements GenericDBExtractor {

	SessionFactory dbSession;

	public CommonDBExtractor(SessionFactory DbSession) {

		dbSession = DbSession;
	}

	// produces a mono-dimensional table
	public SamplesTable getMonoDimTable(String table, String column) {
		BigSamplesTable monoSamples = new BigSamplesTable();

		String query = "select distinct " + column + " from " + table + ";";

		List<Object> resultSet = DatabaseFactory.executeSQLQuery(query, dbSession);

		for (Object result : resultSet) {

			try {
				Double resultcount = (Double) result;
				monoSamples.addSampleRow(column, resultcount);
			} catch (Exception e) {
			}

		}

		return monoSamples;

	}

	private String generateSelectionString(String... columns) {

		String delimiter = " , ";
		StringBuffer sb = new StringBuffer();
		int numbOfColumns = columns.length;

		for (int i = 0; i < numbOfColumns; i++) {

			String column = columns[i];
			sb.append(column);
			if (i < numbOfColumns - 1)
				sb.append(delimiter);
		}

		return sb.toString();
	}

	// SELECT field1,field5,field6,field3 FROM ts_161efa00_2c32_11df_b8b3_aa10916debe6 t where field3='Brown seaweeds';
	private static final String staticQuery = "select distinct %1$s from  %2$s where (%3$s) ";

	private static final String staticOrderBy = " order by %1$s;";

	private static final String descriptionQuery = "SELECT ordinal_position,column_name,data_type FROM information_schema.COLUMNS WHERE table_name ='%1$s'";

	// produces a bi-dimensional table, where for a single x, multiple y are allowed

	// transforms db column types to java types
	private Map<String, String> getTypes(SessionFactory dbSession, String table) {

		HashMap<String, String> typesMap = new HashMap<String, String>();
		String queryDesc = String.format(descriptionQuery, table);
		AnalysisLogger.getLogger().trace("Query for Description: " + queryDesc);
		List<Object> resultSet = DatabaseFactory.executeSQLQuery(queryDesc, dbSession);

		for (Object result : resultSet) {
			Object[] resultArray = (Object[]) result;

			String column_name = (String) resultArray[1];
			String data_type = (String) resultArray[2];
			typesMap.put(column_name, DataTypeRecognizer.transformTypeFromDB(data_type));

		}

		return typesMap;
	}

	private void updateSequence(SamplesTable sequence, Map<String, String> columnTypes, String xDimension, Object[] row, int index) {

		// set correct x label and value
		String xLabel = "";
		Double xValue = Double.valueOf(0);

		// String Type = columnTypes.get(xDimension);

		// if it is a string set the label as the entry and the value as the index
		// NOTE OLD CODE: the x axis is always meant to be in linear scale, for now
		/*
		 * if (Type.equals(BigDecimal.class.getName())) { xLabel = "" + row[0]; xValue = Double.valueOf(index); } else {
		 * 
		 * xLabel = xDimension; xValue = Double.valueOf("" + row[0]); }
		 */
		xLabel = "" + row[0];
		xValue = Double.valueOf(index);

		String label = xLabel + ";";

		// record the y value by taking the row 3 as label and row 2 as the value
		String yLabel = "";
		Double yValue = Double.valueOf(0);
		try {
			yLabel += (String) row[3];
			yValue = Double.valueOf("" + row[2]);
		} catch (Exception e) {
		}
		label += yLabel;
		sequence.addSampleRow(label, xValue, yValue);
	}

	// makes a query on the db and produces a HashMap of bi-dimensional tables including more parallel graphs
	// each group represents a graph
	// each graph has a xDimension and a label for each x (taken from x value)
	// each x can have more than one y and label
	// yValue is the column with y numeric values
	// speciesColumn is the resulting column containing the labels for the ys
	public Map<String, SamplesTable> getMultiDimTemporalTables(List<Filter> filters, Filter YRangeFilter, String table, String xDimension, String groupDimension, String yValue, String speciesColumn, String... yFilters) {
		
		LinkedHashMap<String, SamplesTable> temporalSequence = new LinkedHashMap<String, SamplesTable>();

		String selection = generateSelectionString(xDimension, groupDimension, yValue, speciesColumn);

		StringBuffer whereclause = new StringBuffer();
		int i = 0;
		for (String columnFilter : yFilters) {
			whereclause.append(speciesColumn + "='" + columnFilter + "'");
			if (i < yFilters.length - 1)
				whereclause.append(" or ");

			i++;
		}

		String query = String.format(staticQuery, selection, table, whereclause);

		if ((filters != null) && (filters.size() > 0)) {
			query+="and (";
			int kk =0;
			for (Filter f : filters) {
				
//				query += f.toString();
				if (kk==0){
					query += f.toString("");
				}
				else
					query += f.toString("or");
				kk++;
			}
			query+=")";
		}

		if (YRangeFilter != null) {
			query += "and " + yValue + ">" + YRangeFilter.getFirstNumber() + " and " + yValue + "<" + YRangeFilter.getSecondNumber() + " ";
		}

		query += String.format(staticOrderBy, xDimension);

		AnalysisLogger.getLogger().trace("Query: " + query);
		List<Object> resultSet = DatabaseFactory.executeSQLQuery(query, dbSession);
		// xdim, group, quantity, yLabel

		Map<String, String> columnTypes = getTypes(dbSession, table);
		if (resultSet != null) {
			// for each result row
			for (Object result : resultSet) {
				// take the single row
				Object[] resultArray = (Object[]) result;
				// for each temporal dimension, generate a table
				String temporalInfo = (String) resultArray[1];
				// take the table for the temporal sequence
				SamplesTable sequence = temporalSequence.get(temporalInfo);
				// if table does not exist create a new table and add it to the sequence
				if (sequence == null) {
					sequence = new BigSamplesTable();
					temporalSequence.put(temporalInfo, sequence);
					// set fake elements as headers in order to understand the labels
					for (String columnFilter : yFilters) {
						sequence.addSampleRow("header;" + columnFilter, Double.NEGATIVE_INFINITY, 0);
					}
				}
				// update the rows of the bidimensional table. Use as index the following: take the length of the sequence of values, then subtract the number of headers
				updateSequence(sequence, columnTypes, xDimension, resultArray, sequence.getNumOfDataRows() - yFilters.length);
			}
		}

		return temporalSequence;
	}

	public Map<String, SamplesTable> getMultiDimTemporalTables(String table, String xDimension, String groupDimension, String yValue, String speciesColumn, String... yFilters) {
		return getMultiDimTemporalTables(null, null, table, xDimension, groupDimension, yValue, speciesColumn, yFilters);
	}

}
