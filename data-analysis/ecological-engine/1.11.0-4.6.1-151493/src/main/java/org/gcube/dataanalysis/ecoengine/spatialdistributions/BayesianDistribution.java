package org.gcube.dataanalysis.ecoengine.spatialdistributions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.SpatialProbabilityDistributionGeneric;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.hibernate.SessionFactory;

public abstract class BayesianDistribution implements SpatialProbabilityDistributionGeneric {

	protected static String FeaturesTableP = "FeaturesTable";
	protected static String FeaturesTableColumnsP = "FeaturesColumnNames";
	protected static String FinalTableLabel = "FinalTableLabel";
	protected static String FinalTableName = "FinalTableName";
	protected static String FinalTableValue = "tvalue";
	protected static String FinalTableValueType = "real";
	protected static String GroupingFactor = "GroupingFactor";
	protected static String ModelName = "ModelName";
	protected static String UserName = "UserName";
	
	protected float status = 10f;

	protected String featuresTable;
	protected String featuresTableColumns;
	protected String finalTableName;
	protected String finalTableLabel;
	protected String modelName;
	protected File modelFile;
	protected String userName;
	protected String groupingFactor;
	protected SessionFactory dbConnection;
	protected AlgorithmConfiguration config;

	@Override
	public String getMainInfoType() {
		return String.class.getName();
	}

	@Override
	public String getGeographicalInfoType() {
		return Object[].class.getName();
	}

	@Override
	public List<Object> getMainInfoObjects() {
		if (groupingFactor.length()==0){
			List<Object> lo = new ArrayList<Object>();
			lo.add("1");
			return lo;
		}
		else
			return DatabaseFactory.executeSQLQuery("select distinct " + groupingFactor + " from " + featuresTableColumns, dbConnection);
	}

	@Override
	public List<Object> getGeographicalInfoObjects() {
		return DatabaseFactory.executeSQLQuery("select distinct " + featuresTableColumns + " from " + featuresTable, dbConnection);
	}

	@Override
	public void storeDistribution(Map<Object, Map<Object, Float>> distribution) throws Exception {
		StringBuffer sb = new StringBuffer();
		
		int distribscounter=0;
		int distrsize=distribution.size();
		for (Object key : distribution.keySet()) {
			Map<Object, Float> innerdistrib = distribution.get(key);
			int counter=0;
			int innerdistrsize = innerdistrib.size();
			for (Object vector : innerdistrib.keySet()) {
				float value = innerdistrib.get(vector);
				Object[] elements = (Object[]) vector;
				if (groupingFactor.length()>0)
					sb.append("(" + key + ",");
				else
					sb.append("(");
				for (Object elem : elements) {
					sb.append(elem + ",");
				}
				sb.append(value + ")");
				if (counter<innerdistrsize-1)
					sb.append(",");
				
				counter++;
			}
			if (distribscounter<distrsize-1)
				sb.append(",");
		}
		int len = sb.length()-1;
		
		String insertBuffer = DatabaseUtils.insertFromBuffer(finalTableName, featuresTableColumns + "," + FinalTableValue, sb);
		if (groupingFactor.trim().length()>0)
			insertBuffer = DatabaseUtils.insertFromBuffer(finalTableName, groupingFactor + "," + featuresTableColumns + "," + FinalTableValue, sb);
		
//		AnalysisLogger.getLogger().debug("Insertion Query " + insertBuffer);
		AnalysisLogger.getLogger().debug("Writing Distribution into the DB ");
		DatabaseFactory.executeSQLUpdate(insertBuffer, dbConnection);
		AnalysisLogger.getLogger().debug("Done!");
	}

	@Override
	public float getInternalStatus() {
		return status;
	}

	@Override
	public String getMainInfoID(Object mainInfo) {
		if (groupingFactor.length()==0)
			return "1";
		else
			return "" + ((Object[]) mainInfo)[0];
	}

	@Override
	public String getGeographicalID(Object geoInfo) {
		return "";
	}

	@Override
	public ALG_PROPS[] getProperties() {
		ALG_PROPS[] p = { ALG_PROPS.PHENOMENON_VS_GEOINFO };
		return p;
	}

	@Override
	public void postProcess() {
		status = 100f;
		if (dbConnection!=null){
			dbConnection.close();
		}
	}


	
	@Override
	public List<StatisticalType> getInputParameters() {

		List<StatisticalType> parameters = new ArrayList<StatisticalType>();

		List<TableTemplates> templateOccs = new ArrayList<TableTemplates>();
		templateOccs.add(TableTemplates.GENERIC);
		InputTable p1 = new InputTable(templateOccs, FeaturesTableP, "a Table containing features vectors", "occurrences");
		ColumnTypesList p2 = new ColumnTypesList(FeaturesTableP, FeaturesTableColumnsP, "column names of the features", false);
		PrimitiveType p3 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, FinalTableLabel, "table name of the resulting distribution", "Distrib_");
		ServiceType p4 = new ServiceType(ServiceParameters.RANDOMSTRING, FinalTableName, "table name of the distribution", "distrib_");
		PrimitiveType p5 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, GroupingFactor, "identifier for grouping sets of vectors (blank for automatic enum)", "speciesid");
//		PrimitiveType p6 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, ModelName, "the name of a previously trained model", "neuralnet_");
		PrimitiveType p6 = new PrimitiveType(File.class.getName(), null, PrimitiveTypes.FILE, ModelName, "neuralnet_");
		ServiceType p7 = new ServiceType(ServiceParameters.USERNAME, UserName,"LDAP username");
		
		parameters.add(p1);
		parameters.add(p2);
		parameters.add(p3);
		parameters.add(p4);
		parameters.add(p5);
		parameters.add(p6);
		parameters.add(p7);
		
		DatabaseType.addDefaultDBPars(parameters);
		return parameters;
	}

	@Override
	public StatisticalType getOutput() {
		List<TableTemplates> template = new ArrayList<TableTemplates>();
		template.add(TableTemplates.TESTSET);
		return new OutputTable(template, finalTableLabel, finalTableName, "Output table");
	}

	@Override
	public void init(AlgorithmConfiguration config) throws Exception {
		status = 0;
		AnalysisLogger.getLogger().debug("Initializing Database Connection");
		this.config = config;
		// initialization of the variables
		finalTableLabel = config.getParam(FinalTableLabel);
		finalTableName = config.getParam(FinalTableName);
		featuresTable = config.getParam(FeaturesTableP);
		featuresTableColumns = config.getParam(FeaturesTableColumnsP);
		
		groupingFactor = config.getParam(GroupingFactor);
		if (groupingFactor==null) groupingFactor="";
		
		modelFile = new File(config.getParam(ModelName));
		modelName = modelFile.getName();

		userName = config.getParam(UserName);
		
		// create a new table
		dbConnection = DatabaseUtils.initDBSession(config);
		try {
			AnalysisLogger.getLogger().debug("Dropping table " + finalTableName);
			String dropStatement = DatabaseUtils.dropTableStatement(finalTableName);
			DatabaseFactory.executeSQLUpdate(dropStatement, dbConnection);
			AnalysisLogger.getLogger().debug("Table " + finalTableName + " dropped");
		} catch (Exception e) {
			AnalysisLogger.getLogger().debug("Could not drop table " + finalTableName);
		}

		// create Table
		String[] features = featuresTableColumns.split(AlgorithmConfiguration.getListSeparator());
		
		String columns = "";
		featuresTableColumns="";
		for (int i = 0; i < features.length; i++) {
			columns += features[i] + " real";
			featuresTableColumns+=features[i];
			if (i < features.length - 1){
				columns += ",";
				featuresTableColumns+=",";
			}
		}
		
		
		String createStatement = "create table "+finalTableName+" ( "+columns+", "+FinalTableValue+" "+FinalTableValueType+")";
		if (groupingFactor.length()>0){
			createStatement = "create table "+finalTableName+" ( "+groupingFactor+" character varying "+columns+", "+FinalTableValue+" "+FinalTableValueType+")";
		}
		AnalysisLogger.getLogger().debug("Creating table: " + finalTableName + " by statement: " + createStatement);
		DatabaseFactory.executeSQLUpdate(createStatement, dbConnection);
		AnalysisLogger.getLogger().debug("Table: " + finalTableName + " created");
		/*
		AnalysisLogger.getLogger().debug("Adding a new column to "+finalTableName);
		DatabaseFactory.executeSQLUpdate(DatabaseUtils.addColumnStatement(finalTableLabel, FinalTableValue, FinalTableValueType), dbConnection);
		*/
		status = 10f;

	}

}
