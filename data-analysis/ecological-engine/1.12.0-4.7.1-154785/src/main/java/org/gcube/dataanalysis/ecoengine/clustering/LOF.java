package org.gcube.dataanalysis.ecoengine.clustering;

import java.lang.reflect.Field;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.ecoengine.utils.DynamicEnum;
import org.gcube.dataanalysis.ecoengine.utils.ResourceFactory;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.set.SimpleExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.DataRow;
import com.rapidminer.example.table.DoubleArrayDataRow;
import com.rapidminer.example.table.ExampleTable;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.clustering.Cluster;
import com.rapidminer.operator.clustering.ClusterModel;
import com.rapidminer.tools.Ontology;
import com.rapidminer.tools.OperatorService;

public class LOF extends DBScan {

	String minimal_points_lower_bound = "1";
	String minimal_points_upper_bound = "10";
	String lof_threshold = "2";
	String distance_function = "euclidian distance";
	static String lofcolumn = "lof";
	static String lofcolumntype = "real";
	
	LOFenum enuFunctions = new LOFenum();

	enum LOFenumType {
	}

	class LOFenum extends DynamicEnum {
		public Field[] getFields() {
			Field[] fields = LOFenumType.class.getDeclaredFields();
			return fields;
		}
	}

	@Override
	public void init() throws Exception {
		status = 0;
		if ((config!=null) && (initrapidminer))
			config.initRapidMiner();
		AnalysisLogger.getLogger().debug("Initialized Rapid Miner ");
		AnalysisLogger.getLogger().debug("Initializing Database Connection");
		dbHibConnection=DatabaseUtils.initDBSession(config);
		//create the final table
		try{
			AnalysisLogger.getLogger().debug("dropping table "+OccurrencePointsClusterTable);
		String dropStatement = DatabaseUtils.dropTableStatement(OccurrencePointsClusterTable);
		AnalysisLogger.getLogger().debug("dropping table "+dropStatement);
		DatabaseFactory.executeSQLUpdate(dropStatement, dbHibConnection);
		}catch(Exception e){
			AnalysisLogger.getLogger().debug("Could not drop table "+OccurrencePointsClusterTable);
		}
		//create Table
		AnalysisLogger.getLogger().debug("Creating table "+OccurrencePointsClusterTable);
		String [] features =  FeaturesColumnNames.split(AlgorithmConfiguration.getListSeparator());
		String columns = "";
		
		for (int i=0;i<features.length;i++){
			columns +=features[i]+" real";
			if (i<features.length-1)
				columns+=",";
		}
		
		String createStatement = "create table "+OccurrencePointsClusterTable+" ( "+columns+")";
//		String createStatement = new DatabaseUtils(dbHibConnection).buildCreateStatement(OccurrencePointsTable,OccurrencePointsClusterTable);
		AnalysisLogger.getLogger().debug("Statement: "+createStatement);
		DatabaseFactory.executeSQLUpdate(createStatement, dbHibConnection);
		//add two columns one for cluster and another for outliers
		AnalysisLogger.getLogger().debug("Adding Columns");
		DatabaseFactory.executeSQLUpdate(DatabaseUtils.addColumnStatement(OccurrencePointsClusterTable, lofcolumn, lofcolumntype), dbHibConnection);
		DatabaseFactory.executeSQLUpdate(DatabaseUtils.addColumnStatement(OccurrencePointsClusterTable, outliersColumn, outliersColumnType), dbHibConnection);
		AnalysisLogger.getLogger().debug("Getting Samples");
		//build samples
		getSamples();
		status = 10f;
	}

	@Override
	public void setConfiguration(AlgorithmConfiguration config) {
		if (config != null) {
			minimal_points_lower_bound = config.getParam("minimal_points_lower_bound");
			minimal_points_upper_bound = config.getParam("minimal_points_upper_bound");
			distance_function = config.getParam("distance_function");
			lof_threshold  = config.getParam("lof_threshold");
			
			OccurrencePointsTable = config.getParam("PointsTable").toLowerCase();
			OccurrencePointsClusterLabel = config.getParam("PointsClusterLabel");
			OccurrencePointsClusterTable = config.getParam("PointsClusterTable").toLowerCase();
			FeaturesColumnNames = config.getParam("FeaturesColumnNames");

			this.config = config;
		}

	}

	@Override
	public void compute() throws Exception {
		try {

			if ((config == null) || minimal_points_lower_bound == null || minimal_points_upper_bound == null || distance_function == null) {
				throw new Exception("LOF: Error incomplete parameters");
			}
			status = 10f;
			AnalysisLogger.getLogger().debug("LOF: Settin up the cluster");
			// take elements and produce example set
			com.rapidminer.operator.preprocessing.outlier.LOFOutlierOperator clusterer = (com.rapidminer.operator.preprocessing.outlier.LOFOutlierOperator) OperatorService.createOperator("LOFOutlierDetection");
			clusterer.setParameter("minimal_points_lower_bound", minimal_points_lower_bound);
			clusterer.setParameter("minimal_points_upper_bound", minimal_points_upper_bound);
			clusterer.setParameter("distance_function", distance_function);

			IOContainer innerInput = new IOContainer(points);

			AnalysisLogger.getLogger().debug("LOF: Clustering...");
			long ti = System.currentTimeMillis();
			IOContainer output = clusterer.apply(innerInput);
			AnalysisLogger.getLogger().debug("LOF: ...ELAPSED CLUSTERING TIME: " + (System.currentTimeMillis() - ti));
			AnalysisLogger.getLogger().debug("LOF: ...Clustering Finished");
			status = 70f;

			IOObject[] outputvector = output.getIOObjects();

			BuildClusterTable(outputvector);
		} catch (Exception e) {
			throw e;
		} finally {
			shutdown();
			status = 100f;
		}
	}

	@Override
	protected void BuildClusterTable(IOObject[] outputvector) throws Exception {

		StringBuffer bufferRows = new StringBuffer();
		SimpleExampleSet output = (SimpleExampleSet) outputvector[0];
		MemoryExampleTable met = (MemoryExampleTable) output.getExampleTable();
		int numofcolumns = met.getAttributeCount();
		int numofrows = met.size();
		double lofthr = 2;
		if (lof_threshold!=null) 
			try{lofthr = Double.parseDouble(lof_threshold);}catch(Exception e){};
		AnalysisLogger.getLogger().debug("LOF: using lof threshold :"+lofthr);
		
		for (int i = 0; i < numofrows; i++) {

			DataRow dr = met.getDataRow(i);
			Attribute outlierAtt = met.getAttribute(numofcolumns - 1);
			bufferRows.append("(");
			
			for (int j=0;j<numofcolumns-2;j++){
				Attribute att = met.getAttribute(j);
				bufferRows.append(dr.get(att)+",");
			}
			double lofscore = dr.get(outlierAtt);
			if (lofscore>Double.MAX_VALUE)
				lofscore = Float.MAX_VALUE;
			boolean outlier = (lofscore>=lofthr);
			
			bufferRows.append(lofscore+","+outlier+")");
			
			if (i<numofrows-1)
				bufferRows.append(",");
		}

		AnalysisLogger.getLogger().debug("LOF: Finished in retrieving and building output to write");
		

		String columnsNames = FeaturesColumnNames + ","+lofcolumn+","+outliersColumn;
//		System.out.println(DatabaseUtils.insertFromBuffer(OccurrencePointsClusterTable, columnsNames, bufferRows));
		
		if (bufferRows.length() > 0) {

			AnalysisLogger.getLogger().debug("Writing into DB");
//			AnalysisLogger.getLogger().debug("Query to execute: "+DatabaseUtils.insertFromBuffer(OccurrencePointsClusterTable, columnsNames, bufferRows));
			DatabaseFactory.executeSQLUpdate(DatabaseUtils.insertFromBuffer(OccurrencePointsClusterTable, columnsNames, bufferRows), dbHibConnection);
			AnalysisLogger.getLogger().debug("Finished with writing into DB");
		} else
			AnalysisLogger.getLogger().debug("Nothing to write in the buffer");

		status = 100;
		AnalysisLogger.getLogger().debug("Status: " + status);

	}

	@Override
	public List<StatisticalType> getInputParameters() {
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		List<TableTemplates> templateOccs = new ArrayList<TableTemplates>();
		templateOccs.add(TableTemplates.GENERIC);

		InputTable p1 = new InputTable(templateOccs, "PointsTable", "Table containing points or observations. Max 4000 points", "pointstable");
		ColumnTypesList p2 = new ColumnTypesList("PointsTable", "FeaturesColumnNames", "column Names for the features", false);

		PrimitiveType p0 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, "PointsClusterLabel", "table name of the resulting distribution", "Cluster_");
		ServiceType p3 = new ServiceType(ServiceParameters.RANDOMSTRING, "PointsClusterTable", "table name of the distribution", "occcluster_");

		PrimitiveType p4 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, "minimal_points_lower_bound", "locality (usually called k): minimal number of nearest neighbors", "2");
		PrimitiveType p5 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, "minimal_points_upper_bound", "maximum number of nearest neighbors to take into account for outliers evaluation", "10");
		
		if (LOFenumType.values().length<2) {
			enuFunctions.addEnum(LOFenumType.class, "euclidian distance");
			enuFunctions.addEnum(LOFenumType.class, "squared distance");
			enuFunctions.addEnum(LOFenumType.class, "cosine distance");
			enuFunctions.addEnum(LOFenumType.class, "inverted cosine distance");
			enuFunctions.addEnum(LOFenumType.class, "angle");
		}
		
		PrimitiveType p6 = new PrimitiveType(Enum.class.getName(), LOFenumType.values(), PrimitiveTypes.ENUMERATED, "distance_function", "the distance function to use in the calculation", "euclidian distance");
		PrimitiveType p7 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, "lof_threshold", "the LOF score threshold over which the point is an outlier (usually 2)", "2");
		
		parameters.add(p1);
		parameters.add(p2);
		parameters.add(p0);
		parameters.add(p3);
		parameters.add(p4);
		parameters.add(p5);
		parameters.add(p6);
		parameters.add(p7);

		DatabaseType.addDefaultDBPars(parameters);
		return parameters;
	}

	@Override
	public String getDescription() {
		return "Local Outlier Factor (LOF). A clustering algorithm for real valued vectors that relies on Local Outlier Factor algorithm, i.e. an algorithm for finding anomalous data points by measuring the local deviation of a given data point with respect to its neighbours. A Maximum of 4000 points is allowed.";
	}

	ResourceFactory resourceManager;

	public String getResourceLoad() {
		if (resourceManager == null)
			resourceManager = new ResourceFactory();
		return resourceManager.getResourceLoad(1);
	}

	@Override
	public String getResources() {
		return ResourceFactory.getResources(100f);
	}

	
	public static void main(String[] args) throws Exception {
		long t0 = System.currentTimeMillis();

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
//		config.setParam("PointsTable", "presence_basking_cluster");
//		config.setParam("FeaturesColumnNames", "centerlat" + AlgorithmConfiguration.getListSeparator() + "centerlong");
		config.setParam("PointsTable", "hcaf_d");
//		config.setParam("FeaturesColumnNames", "depthmin" + AlgorithmConfiguration.getListSeparator() + "depthmax");
		config.setParam("FeaturesColumnNames", "depthmin" + AlgorithmConfiguration.getListSeparator() + "depthmax"+ AlgorithmConfiguration.getListSeparator()+"depthmean"+ 
									AlgorithmConfiguration.getListSeparator()+"sstanmean"+
									AlgorithmConfiguration.getListSeparator()+"sstmnmax"+
									AlgorithmConfiguration.getListSeparator()+"sstmnmin"+
									AlgorithmConfiguration.getListSeparator()+"sbtanmean"+
									AlgorithmConfiguration.getListSeparator()+"salinitymean"+
									AlgorithmConfiguration.getListSeparator()+"salinitymax");
//		config.setParam("FeaturesColumnNames", "depthmin");
		config.setParam("PointsClusterTable", "occCluster_lof");

		config.setParam("minimal_points_lower_bound", "1");
		config.setParam("minimal_points_upper_bound", "100");
		config.setParam("distance_function", "euclidean distance");

		config.setParam("DatabaseUserName", "gcube");
		config.setParam("DatabasePassword", "d4science2");
		config.setParam("DatabaseURL", "jdbc:postgresql://146.48.87.169/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");

		LOF cluster = new LOF();
		cluster.setConfiguration(config);
		cluster.init();
		cluster.compute();

		System.out.println("ELAPSED " + (System.currentTimeMillis() - t0));

	}
	
}
