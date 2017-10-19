package org.gcube.dataanalysis.ecoengine.transducers;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;

public class OccurrencePointsInSeaOnEarth extends OccurrencePointsMerger{
	
	//NOTE: 0.125 is the diagonal of a csquare, which is the maximum extent to which a point can lie in a csquare
	private static String inthesea="select * into %1$s from (select distinct a.* from %2$s as a join hcaf_d as b on ((b.centerlat-a.%3$s)*(b.centerlat-a.%3$s)+(b.centerlong-a.%4$s)*(b.centerlong-a.%4$s)<= 0.125) and b.oceanarea>0) as t limit 10000";
	private static String onearth="select * into %1$s from (select distinct a.* from %2$s as a join hcaf_d as b on ((b.centerlat-a.%3$s)*(b.centerlat-a.%3$s)+(b.centerlong-a.%4$s)*(b.centerlong-a.%4$s)<= 0.125) and b.landdist<=0.3) as t limit 10000";
	static String tableNameF = "OccurrencePointsTableName";
	static String filterTypeF = "FilterType";
	String tableName;
	public static enum inseasonearth {MARINE, TERRESTRIAL};
	inseasonearth filter;
	
	@Override
	public List<StatisticalType> getInputParameters() {
		List<TableTemplates> templatesOccurrence = new ArrayList<TableTemplates>();
		templatesOccurrence.add(TableTemplates.OCCURRENCE_SPECIES);
		// occurrence points tables
		PrimitiveType p0 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, finalTableNameL,"the name of the produced table", "Occ_");
		InputTable p1 = new InputTable(templatesOccurrence, tableNameF, "The table containing the occurrence points", "");
		// string parameters
		ColumnType p3 = new ColumnType(tableNameF, longitudeColumn, "column with longitude values", "decimallongitude", false);
		ColumnType p4 = new ColumnType(tableNameF, latitudeColumn, "column with latitude values", "decimallatitude", false);
		ServiceType p9 = new ServiceType(ServiceParameters.RANDOMSTRING, finalTableNameF, "Name of the resulting table", "processedOccurrences_");
		PrimitiveType p10 = new PrimitiveType(Enum.class.getName(), inseasonearth.values(), PrimitiveTypes.ENUMERATED, filterTypeF, "The filter type",""+inseasonearth.MARINE);
		
		List<StatisticalType> inputs = new ArrayList<StatisticalType>();
		inputs.add(p0);
		inputs.add(p1);
		inputs.add(p3);
		inputs.add(p4);
		inputs.add(p9);
		inputs.add(p10);
		
		DatabaseType.addDefaultDBPars(inputs);
		return inputs;
	}
	
	
	@Override
	public void init() throws Exception {

		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		lonFld = config.getParam(longitudeColumn);
		latFld = config.getParam(latitudeColumn);
		tableName = config.getParam(tableNameF);
		finalTableName = config.getParam(finalTableNameF);
		finalTableLabel= config.getParam(finalTableNameL);
		filter = inseasonearth.valueOf(config.getParam(filterTypeF));
		status = 0;
	}
	
	@Override
	public String getDescription() {
		return "A transducer algorithm that produces a table containing occurrence points by filtering them by type of area, i.e. by recognising whether they are marine or terrestrial. Works with up to 10000 points per table.";
	}
	
	@Override
	protected void prepareFinalTable() throws Exception{
		DatabaseFactory.executeSQLUpdate(DatabaseUtils.createBlankTableFromAnotherStatement(tableName, finalTableName), dbconnection);
	}
	
	@Override
	public void compute() throws Exception {

		try {
			// init DB connection
			AnalysisLogger.getLogger().trace("Initializing DB Connection");
			dbconnection = DatabaseUtils.initDBSession(config);
			AnalysisLogger.getLogger().trace("Taking Table Description");
			AnalysisLogger.getLogger().trace("Creating merged table: " + finalTableName);
			// create new merged table
			try{
			DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(finalTableName), dbconnection);
			}catch(Exception e1){}
//			prepareFinalTable();
			status = 10;
			String generationquery = "";
			if (filter==inseasonearth.MARINE)
				generationquery = String.format(inthesea,finalTableName,tableName,latFld,lonFld);
			else
				generationquery = String.format(onearth,finalTableName,tableName,latFld,lonFld);
			
			AnalysisLogger.getLogger().trace("Applying filter " + filter.name());
			AnalysisLogger.getLogger().trace("Applying query " + generationquery);
			DatabaseFactory.executeSQLUpdate(generationquery, dbconnection);
			AnalysisLogger.getLogger().trace("Final Table created!");
			
	} catch (Exception e) {
		throw e;
	} finally {
		if (dbconnection != null)
			try{
			dbconnection.close();
			}catch(Exception e2){}
		status = 100;
		AnalysisLogger.getLogger().trace("Occ Points Processing Finished and db closed");
	}
		
	}	
	
}
