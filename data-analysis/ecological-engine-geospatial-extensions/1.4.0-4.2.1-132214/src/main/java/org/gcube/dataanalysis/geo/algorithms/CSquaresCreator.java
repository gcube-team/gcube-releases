package org.gcube.dataanalysis.geo.algorithms;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalInfraAlgorithm;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.geo.utils.CSquareCodesConverter;
import org.hibernate.SessionFactory;

public class CSquaresCreator extends StandardLocalInfraAlgorithm {

	static String xDim = "Longitude_Column";
	static String yDim = "Latitude_Column";
	static String inputTableParameter = "InputTable";
	static String outputTableParameter = "OutputTableName";
	String resolutionParameter = "CSquare_Resolution";
	String codecolumnName = "csquare_code";
	String outTable = "";
	String outTableLabel = "";
	double resolution = 0.1;
	SessionFactory connection = null;

	@Override
	protected void setInputParameters() {

		List<TableTemplates> templates = new ArrayList<TableTemplates>();
		templates.add(TableTemplates.GENERIC);
		InputTable tinput = new InputTable(templates, inputTableParameter, "The table to which the algorithm adds the csquare column");
		inputs.add(tinput);

		ColumnType xDimension = new ColumnType(inputTableParameter, xDim, "The column containing Longitude information", "x", false);
		ColumnType yDimension = new ColumnType(inputTableParameter, yDim, "The column containing Latitude information", "y", false);

		inputs.add(xDimension);
		inputs.add(yDimension);
		IOHelper.addDoubleInput(inputs, resolutionParameter, "The resolution of the CSquare codes", "0.1");
		IOHelper.addStringInput(inputs, outputTableParameter, "The name of the output table", "csquaretbl_");
		DatabaseType.addDefaultDBPars(inputs);

	}

	@Override
	public StatisticalType getOutput() {
		List<TableTemplates> template = new ArrayList<TableTemplates>();
		template.add(TableTemplates.GENERIC);
		OutputTable p = new OutputTable(template, outTableLabel, outTable, "Output table");
		return p;
	}

	@Override
	public void init() throws Exception {
		AnalysisLogger.getLogger().debug("CSquareCreator  Initialized");
	}

	@Override
	public String getDescription() {
		return "An algorithm that adds a column containing the CSquare codes associated to longitude and latitude columns.";
	}

	@Override
	protected void process() throws Exception {
		status = 0;
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		long t0 = System.currentTimeMillis();
		String x = IOHelper.getInputParameter(config, xDim);
		String y = IOHelper.getInputParameter(config, yDim);
		String table = IOHelper.getInputParameter(config, inputTableParameter);
		outTable = ("code_" + UUID.randomUUID()).replace("-", "");
		resolution = Double.parseDouble(IOHelper.getInputParameter(config, resolutionParameter));
		outTableLabel = IOHelper.getInputParameter(config, outputTableParameter);

		AnalysisLogger.getLogger().debug("CSquareCreator: received parameters: x " + x + ", y " + y + ", table " + table + ", outputTable " + outTable + ", res " + resolution + " outLabel " + outTableLabel);

		status = 10;
		if (x == null || x.trim().length() == 0 || y == null || y.trim().length() == 0)
			throw new Exception("Error please provide information for the input table");
		try{
			addCodeColumToTable(table);
			AnalysisLogger.getLogger().debug("CSquareCreator: finished");
		} catch (Throwable e) {
			throw new Exception(e.getMessage());
		} finally {
			status = 100;
		}
	}

	public String selectInformationForTransformation (AlgorithmConfiguration config, String table, int limit, int offset){
		
		String x = IOHelper.getInputParameter(config, xDim);
		String y = IOHelper.getInputParameter(config, yDim);
		
		String select = "select *," + x + " as loforcs01," + y + " as laforcs01 from " + table + " limit " + limit + " offset " + offset;
		return select;
	}
	
	public String rowToCode (Object[] rowArray){
		// take x and y
		String xValue = "" + rowArray[rowArray.length - 2];
		String yValue = "" + rowArray[rowArray.length - 1];
		// generate csquarecodes
		String csquare = "";
		try {
			double xV = Double.parseDouble(xValue);
			double yV = Double.parseDouble(yValue);
			csquare = CSquareCodesConverter.convertAtResolution(yV, xV, resolution);
		} catch (Exception e) {
		}
		return csquare;
	}

	public void addCodeColumToTable(String table) throws Exception{
		AnalysisLogger.getLogger().debug("CodeCreator: initializing connection");
		long t0 = System.currentTimeMillis();
		try {
			connection = DatabaseUtils.initDBSession(config);
			AnalysisLogger.getLogger().debug("CodeCreator: database: " + config.getDatabaseURL());
			// create a new output table
			AnalysisLogger.getLogger().debug("CodeCreator: dropping table " + outTable + " if exists");
			try {
				DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(outTable), connection);
			} catch (Exception e) {
				AnalysisLogger.getLogger().debug("CodeCreator: table " + outTable + " does not exist yet");
			}
			AnalysisLogger.getLogger().debug("CodeCreator: creating the new table " + outTable);
			DatabaseFactory.executeSQLUpdate(DatabaseUtils.createBlankTableFromAnotherStatement(table, outTable), connection);
			AnalysisLogger.getLogger().debug("CodeCreator: adding new column to " + outTable);
			DatabaseFactory.executeSQLUpdate(DatabaseUtils.addColumnStatement(outTable, codecolumnName, "character varying"), connection);
			AnalysisLogger.getLogger().debug("CodeCreator: getting columns from " + outTable);
			// get columns names
			List<Object> names = DatabaseFactory.executeSQLQuery(DatabaseUtils.getColumnsNamesStatement(outTable), connection);
			StringBuffer colnames = new StringBuffer();
			int nnames = names.size();
			for (int i = 0; i < nnames; i++) {
				colnames.append(names.get(i));
				if (i < nnames - 1)
					colnames.append(",");
			}
			AnalysisLogger.getLogger().debug("CodeCreator: columns are: " + colnames.toString());
			AnalysisLogger.getLogger().debug("CodeCreator: taking chunks ... ");
			// take chunks of the table
			int initIdx = 0;
			int limit = 5000;

			long maxRows = DatabaseUtils.estimateNumberofRows(table, connection);
			AnalysisLogger.getLogger().debug("CodeCreator: estimated number of rows " + maxRows);
			status = 20;
			while (true) {
				
				String select = selectInformationForTransformation(config, table, limit, initIdx);
				AnalysisLogger.getLogger().debug("CodeCreator: executing query: "+select);
				AnalysisLogger.getLogger().debug("CodeCreator: from " + initIdx + " to " + (initIdx + limit) + " limit "+limit);
				List<Object> rows = DatabaseFactory.executeSQLQuery(select, connection);
				
				if (rows == null || rows.size() == 0) {
					AnalysisLogger.getLogger().debug("CodeCreator: no more rows");
					break;
				}
				AnalysisLogger.getLogger().debug("CodeCreator: transforming ");
				// take x and y
				List<String[]> stringrows = new ArrayList<String[]>();
				for (Object row : rows) {
					Object[] rowArray = (Object[]) row;
					String code = rowToCode(rowArray);
					rowArray[nnames-1] = code;
					String[] stringArray = new String[nnames];
					// convert all the objects into Strings
					for (int k = 0; k < stringArray.length; k++) {
						stringArray[k] = "" + rowArray[k];
					}

					stringrows.add(stringArray);
				}

				AnalysisLogger.getLogger().debug("CSquareCreator: inserting chunks into the table");
				// write the vector into the table
				DatabaseUtils.insertChunksIntoTable(outTable, colnames.toString(), stringrows, limit, connection, true);
				initIdx = initIdx+limit;
				status = Math.min(90, 20 + (70 * initIdx / maxRows));
				AnalysisLogger.getLogger().debug("CSquareCreator: status " + status);
			}

			AnalysisLogger.getLogger().debug("CSquareCreator: finished");
		} catch (Throwable e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("CSquareCreator : ERROR!: " + e.getLocalizedMessage());
			try {
				AnalysisLogger.getLogger().debug("CSquareCreator: dropping " + outTable);
				DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(outTable), connection);
			} catch (Exception e1) {
				AnalysisLogger.getLogger().debug("CSquareCreator: could not drop " + outTable);
			}
			throw new Exception(e.getMessage());
		} finally {
			shutdown();
			AnalysisLogger.getLogger().debug("CSquareCreator finished in " + (System.currentTimeMillis() - t0) + " ms");
		}
	}
	
	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("CSquareCreator shutdown");
		DatabaseUtils.closeDBConnection(connection);
	}

}

