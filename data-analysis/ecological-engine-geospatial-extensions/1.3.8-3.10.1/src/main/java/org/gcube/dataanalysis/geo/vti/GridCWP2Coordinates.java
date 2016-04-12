package org.gcube.dataanalysis.geo.vti;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;
import org.gcube.dataanalysis.geo.utils.GridCWPConverter;
import org.hibernate.SessionFactory;

public class GridCWP2Coordinates extends StandardLocalExternalAlgorithm{
	
	static String CodeColumn = "ColumnWithCodes";
	static String inputTableParameter = "InputTable";
	static String outputTableParameter = "OutputTableName";
	
	String outTable = "";
	String outTableLabel = "";
	SessionFactory connection = null;
	
	protected double currentLong;
	protected double currentLat;
	protected double currentRes;
	
	public GridCWP2Coordinates(){
	}
	
	@Override
	protected void setInputParameters() {

		List<TableTemplates> templates = new ArrayList<TableTemplates>();
		templates.add(TableTemplates.GENERIC);
		InputTable tinput = new InputTable(templates, inputTableParameter, "The table to which the algorithm will add information");
		inputs.add(tinput);

		ColumnType Dimension = new ColumnType(inputTableParameter, CodeColumn, "The column containing FAO Ocean Area codes in CWP format", "GRID", false);
		
		inputs.add(Dimension);
		
		IOHelper.addStringInput(inputs, outputTableParameter, "The name of the output table", "cwp_");
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
	public String getDescription() {
		return "An algorithm that adds longitude, latitude and resolution columns analysing a column containing FAO Ocean Area codes (CWP format).";
	}

	public String selectInformationForTransformation (AlgorithmConfiguration config, String table, int limit, int offset){
		
		String d = IOHelper.getInputParameter(config, CodeColumn);
		
		String select = "select *," + d + " as loforcs01 from " + table + " limit " + limit + " offset " + offset;
		return select;
	}
	
	
	public void rowToCoords (Object[] rowArray) {
		// take x and y
		Object grid = null;
		try{
			grid=rowArray[rowArray.length - 1];
			String gridValue = ""+(int)Double.parseDouble("" + grid);
		// generate csquarecodes
		GridCWPConverter gridder = null;
			gridder = new GridCWPConverter();
			gridder.gridCodeToPair(gridValue);
		
			currentLat= gridder.outlat;
			currentLong=gridder.outlon;
			currentRes=gridder.gridresolution;
		}catch(Exception e){
			AnalysisLogger.getLogger().debug("Error converting grid: "+grid+" - "+e.getLocalizedMessage());
			currentLat= 0;
			currentLong=0;
			currentRes=0;	
		}
	}
	
	@Override
	protected void process() throws Exception {
		status = 0;
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);

		long t0 = System.currentTimeMillis();
		String gridField = IOHelper.getInputParameter(config, CodeColumn);
		String table = IOHelper.getInputParameter(config, inputTableParameter);
		outTable = ("code_" + UUID.randomUUID()).replace("-", "");
		outTableLabel = IOHelper.getInputParameter(config, outputTableParameter);

		AnalysisLogger.getLogger().debug("GridCWP2Coordinates: received parameters: code column " + gridField +", table " + table + ", outputTable " + outTable + " outLabel " + outTableLabel);

		status = 10;
		if (gridField == null || gridField.trim().length() == 0 )
			throw new Exception("Error please provide information for the code column");
		try{
			addInformationColumsToTable(table);
			AnalysisLogger.getLogger().debug("GridCWP2Coordinates: finished in "+(System.currentTimeMillis()-t0)+" ms");
			
		} catch (Throwable e) {
			throw new Exception(e.getMessage());
		} finally {
			status = 100;
		}
	}
	
	public void addInformationColumsToTable(String table) throws Exception{
		AnalysisLogger.getLogger().debug("GridCWP2Coordinates: initializing connection");
		long t0 = System.currentTimeMillis();
		try {
			connection = DatabaseUtils.initDBSession(config);
			AnalysisLogger.getLogger().debug("GridCWP2Coordinates: database: " + config.getDatabaseURL());
			// create a new output table
			AnalysisLogger.getLogger().debug("GridCWP2Coordinates: dropping table " + outTable + " if exists");
			try {
				DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(outTable), connection);
			} catch (Exception e) {
				AnalysisLogger.getLogger().debug("GridCWP2Coordinates: table " + outTable + " does not exist yet");
			}
			AnalysisLogger.getLogger().debug("GridCWP2Coordinates: creating the new table " + outTable);
			DatabaseFactory.executeSQLUpdate(DatabaseUtils.createBlankTableFromAnotherStatement(table, outTable), connection);
			AnalysisLogger.getLogger().debug("GridCWP2Coordinates: adding new columns to " + outTable);
			DatabaseFactory.executeSQLUpdate(DatabaseUtils.addColumnStatement(outTable, "long_estim", "real"), connection);
			DatabaseFactory.executeSQLUpdate(DatabaseUtils.addColumnStatement(outTable, "lat_estim", "real"), connection);
			DatabaseFactory.executeSQLUpdate(DatabaseUtils.addColumnStatement(outTable, "res_estim", "real"), connection);
			AnalysisLogger.getLogger().debug("GridCWP2Coordinates: getting columns from " + outTable);
			// get columns names
			List<Object> names = DatabaseFactory.executeSQLQuery(DatabaseUtils.getColumnsNamesStatement(outTable), connection);
			StringBuffer colnames = new StringBuffer();
			int nnames = names.size();
			for (int i = 0; i < nnames; i++) {
				colnames.append(names.get(i));
				if (i < nnames - 1)
					colnames.append(",");
			}
			AnalysisLogger.getLogger().debug("GridCWP2Coordinates: columns are: " + colnames.toString());
			AnalysisLogger.getLogger().debug("GridCWP2Coordinates: taking chunks ... ");
			// take chunks of the table
			int initIdx = 0;
			int limit = 5000;

			long maxRows = DatabaseUtils.estimateNumberofRows(table, connection);
			AnalysisLogger.getLogger().debug("GridCWP2Coordinates: estimated number of rows " + maxRows);
			status = 20;
			while (true) {
				
				String select = selectInformationForTransformation(config, table, limit, initIdx);
				AnalysisLogger.getLogger().debug("GridCWP2Coordinates: executing query: "+select);
				AnalysisLogger.getLogger().debug("GridCWP2Coordinates: from " + initIdx + " to " + (initIdx + limit) + " limit "+limit);
				List<Object> rows = DatabaseFactory.executeSQLQuery(select, connection);
				
				if (rows == null || rows.size() == 0) {
					AnalysisLogger.getLogger().debug("GridCWP2Coordinates: no more rows");
					break;
				}
				AnalysisLogger.getLogger().debug("GridCWP2Coordinates: transforming ");
				// take x and y
				List<String[]> stringrows = new ArrayList<String[]>();
				for (Object row : rows) {
					Object[] rowArray = (Object[]) row;
//					AnalysisLogger.getLogger().debug("ROW: "+Arrays.toString(rowArray));
					rowToCoords(rowArray);
					String[] stringArray = new String[nnames];
					// convert all the objects into Strings
					for (int k = 0; k < rowArray.length-1; k++) {
						stringArray[k] = "" + rowArray[k];
					}
					stringArray[nnames-3] = ""+currentLong;
					stringArray[nnames-2] = ""+currentLat;
					stringArray[nnames-1] = ""+currentRes;
					
					stringrows.add(stringArray);
				}

				AnalysisLogger.getLogger().debug("GridCWP2Coordinates: inserting chunks into the table");
				// write the vector into the table
				DatabaseUtils.insertChunksIntoTable(outTable, colnames.toString(), stringrows, limit, connection, true);
				initIdx = initIdx+limit;
				status = Math.min(90, 20 + (70 * initIdx / maxRows));
				AnalysisLogger.getLogger().debug("GridCWP2Coordinates: status " + status);
			}

			AnalysisLogger.getLogger().debug("GridCWP2Coordinates: finished");
		} catch (Throwable e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("GridCWP2Coordinates : ERROR!: " + e.getLocalizedMessage());
			try {
				AnalysisLogger.getLogger().debug("GridCWP2Coordinates: dropping " + outTable);
				DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(outTable), connection);
			} catch (Exception e1) {
				AnalysisLogger.getLogger().debug("GridCWP2Coordinates: could not drop " + outTable);
			}
			throw new Exception(e.getMessage());
		} finally {
			shutdown();
			AnalysisLogger.getLogger().debug("GridCWP2Coordinates finished in " + (System.currentTimeMillis() - t0) + " ms");
		}
	}

	@Override
	public void init() throws Exception {
		
	}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("GridCWP2Coordinates shutdown");
		DatabaseUtils.closeDBConnection(connection);
	}
	
}

