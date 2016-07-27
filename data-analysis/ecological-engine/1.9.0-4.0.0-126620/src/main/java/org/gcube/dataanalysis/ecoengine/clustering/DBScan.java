package org.gcube.dataanalysis.ecoengine.clustering;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.DatabaseParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.Clusterer;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.ecoengine.utils.ResourceFactory;
import org.gcube.dataanalysis.ecoengine.utils.Transformations;
import org.hibernate.SessionFactory;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.clustering.Cluster;
import com.rapidminer.operator.clustering.ClusterModel;
import com.rapidminer.tools.OperatorService;

public class DBScan implements Clusterer{

	protected AlgorithmConfiguration config;
	protected String epsilon;
	protected String minPoints;
	protected ExampleSet points;
	protected ArrayList<ArrayList<String>> rows;
	protected String OccurrencePointsTable;
	protected String OccurrencePointsClusterLabel;
	protected String OccurrencePointsClusterTable;
	protected String FeaturesColumnNames;
	protected float status;
	protected SessionFactory dbHibConnection;
	protected double[][] samplesVector;
	
	public static String clusterColumn = "clusterid";
	public static String clusterColumnType = "character varying";
	public static String outliersColumn = "outlier";
	public static String outliersColumnType = "boolean";
	protected boolean initrapidminer = true;
	
	public static void mainCluster(String[] args) throws Exception{
		
		
		
		String coordinates [] = {
			"55.973798,-55.297853",
			"57.279043,-57.055666",
			"55.776573,-56.440431",
			"54.622978,-52.309572",
			"56.267761,-54.594728",
			"31.052934,-70.151369",
			"34.161818,-68.129885",
			"30.372875,-61.977541",
			"24.20689,-21.547853",
			"21.453069,-21.987306",
			"21.453069,-19.526369",
			"51.013755,-20.229494"
		};
		
		
		double[][] sampleVectors = new double[coordinates.length][2];
		for (int i=0;i<coordinates.length;i++){
			String coordCop = coordinates[i];
			double first = Double.parseDouble(coordCop.substring(0,coordCop.indexOf(','))); 
			double second = Double.parseDouble(coordCop.substring(coordCop.indexOf(',')+1));
			sampleVectors[i][0] = first;
			sampleVectors[i][1] = second;
		}
		
		DBScan dbscanner = new DBScan();
		dbscanner.produceSamples(sampleVectors);
		
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setParam("epsilon", "10");
		config.setParam("min_points", "1");
		config.setConfigPath("./cfg/");
		config.initRapidMiner();
		dbscanner.setConfiguration(config);
		dbscanner.compute();
		
	}


	public static void mainRandom(String[] args) throws Exception{
		int max = 100000;
		
		
		String coordinates[] = new String[max];
		for (int j =0;j<max;j++){
			coordinates[j] = 100*Math.random()+","+100*Math.random();
		}
		
		double[][] sampleVectors = new double[coordinates.length][2];
		for (int i=0;i<coordinates.length;i++){
			String coordCop = coordinates[i];
			double first = Double.parseDouble(coordCop.substring(0,coordCop.indexOf(','))); 
			double second = Double.parseDouble(coordCop.substring(coordCop.indexOf(',')+1));
			sampleVectors[i][0] = first;
			sampleVectors[i][1] = second;
		}
		
		DBScan dbscanner = new DBScan();
		dbscanner.produceSamples(sampleVectors);
		
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setParam("epsilon", "10");
		config.setParam("min_points", "1");
		config.setConfigPath("./cfg/");
		config.initRapidMiner();
		long t0 = System.currentTimeMillis();
		dbscanner.setConfiguration(config);
		dbscanner.compute();
		System.out.println("ELAPSED "+(System.currentTimeMillis()-t0));
	}


	public static void main(String[] args) throws Exception{
		long t0 = System.currentTimeMillis();
		
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("OccurrencePointsTable","presence_basking_cluster");
		config.setParam("FeaturesColumnNames","centerlat,centerlong");
		config.setParam("OccurrencePointsClusterTable","occCluster_1");
		config.setParam("epsilon","10");
		config.setParam("min_points","1");
		
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://146.48.87.169/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		
		DBScan dbscanner = new DBScan();
		dbscanner.setConfiguration(config);
		dbscanner.init();
		dbscanner.compute();
		
		System.out.println("ELAPSED "+(System.currentTimeMillis()-t0));
		
	}
	
	@Override
	public INFRASTRUCTURE getInfrastructure() {
		return INFRASTRUCTURE.LOCAL;
	}

	long t00; 
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
		DatabaseFactory.executeSQLUpdate(DatabaseUtils.addColumnStatement(OccurrencePointsClusterTable, clusterColumn, clusterColumnType), dbHibConnection);
		DatabaseFactory.executeSQLUpdate(DatabaseUtils.addColumnStatement(OccurrencePointsClusterTable, outliersColumn, outliersColumnType), dbHibConnection);
		AnalysisLogger.getLogger().debug("Getting Samples");
		//build samples
		try{
		getSamples();
		}catch(Throwable e){
			AnalysisLogger.getLogger().debug("Error getting samples for clustering: "+e.getLocalizedMessage());
		}
		status = 10f;
	}

	@Override
	public void setConfiguration(AlgorithmConfiguration config) {
		if (config!=null){
		epsilon=config.getParam("epsilon");
		minPoints = config.getParam("min_points");
		OccurrencePointsTable = config.getParam("OccurrencePointsTable").toLowerCase();
		OccurrencePointsClusterLabel=config.getParam("OccurrencePointsClusterLabel");
		OccurrencePointsClusterTable=config.getParam("OccurrencePointsClusterTable").toLowerCase();
		FeaturesColumnNames=config.getParam("FeaturesColumnNames");
		this.config=config;
		}
	}

	protected void getSamples() throws Exception{
		t00=System.currentTimeMillis();
//		System.out.println("->"+DatabaseUtils.getColumnsElementsStatement(OccurrencePointsTable, FeaturesColumnNames, ""));
		FeaturesColumnNames=FeaturesColumnNames.replace(AlgorithmConfiguration.listSeparator, ",");
		String [] elements = FeaturesColumnNames.split(",");
//		int limit = (int)Math.pow(5000,1d/(double)elements.length);
		int N=4000;
		double k = elements.length;
		double  t=82327;
		double logG = Math.log(t)-N;
		
		int limit = N;
//		if (k>1)
//			limit = (int)Math.round(( Math.log(t)-k*logG )/k );
//			limit = (int)Math.round((double)N/k);
//		 limit = (int)(11d*Math.pow(N,2d/(k+1)));
//			limit =(int) ((double)N/(1.3d));
		
		AnalysisLogger.getLogger().debug("Clustering limit: "+limit);
		
		List<Object> samples = DatabaseFactory.executeSQLQuery(DatabaseUtils.getColumnsElementsStatement(OccurrencePointsTable, FeaturesColumnNames, "limit "+limit), dbHibConnection);
		
		int dimensions = elements.length;
		int nSamples = samples.size();
		samplesVector = new double[nSamples][dimensions];
		int ir=0;
		for (Object row:samples){
			Object[] rowArr = new Object[1];
			try{rowArr = (Object[]) row;}
			catch(ClassCastException e){
				rowArr[0] = ""+row;
			}
			int ic=0;
			for (Object elem:rowArr){
				Double feature = null;
				try{
					feature = Double.parseDouble(""+elem);
				}
				catch(Exception  e){
					//transform a string into a number
					feature = Transformations.indexString(""+elem);
				}
				samplesVector[ir][ic] = feature;
				ic++;
			}
			
			ir++;
		}
		AnalysisLogger.getLogger().debug("Building Sample Set For Miner");
		produceSamples(samplesVector);
		AnalysisLogger.getLogger().debug("Obtained "+samplesVector.length+" chunks");
	}
	
	public void produceSamples(double[][] sampleVectors) throws Exception{
		
		points = Transformations.matrix2ExampleSet(sampleVectors);
		
	}

	
	
	@Override
	public void compute() throws Exception {
		try{
		if ((config==null)||epsilon==null||minPoints==null||points==null){
			throw new Exception("DBScan: Error incomplete parameters");
		}
		
		AnalysisLogger.getLogger().debug("DBScan: Settin up the cluster");
		//take elements and produce example set
		com.rapidminer.operator.clustering.clusterer.DBScan clusterer = (com.rapidminer.operator.clustering.clusterer.DBScan) OperatorService.createOperator("DBScanClustering");
		clusterer.setParameter("local_random_seed", "-1");
		clusterer.setParameter("epsilon", epsilon);
		clusterer.setParameter("min_points", minPoints);
		clusterer.setParameter("add_cluster_attribute", "true");
		clusterer.setParameter("keep_example_set", "true");
		
		IOContainer innerInput = new IOContainer(points);
		
		AnalysisLogger.getLogger().debug("DBScan: Clustering...");
		long ti= System.currentTimeMillis();
		IOContainer output = clusterer.apply(innerInput);
		AnalysisLogger.getLogger().debug("DBScan: ...ELAPSED CLUSTERING TIME: "+(System.currentTimeMillis()-ti));
		AnalysisLogger.getLogger().debug("DBScan: ...Clustering Finished in "+(System.currentTimeMillis()-t00));
		status = 70f;
		
		IOObject[] outputvector = output.getIOObjects();
		
		BuildClusterTable(outputvector);
		
		}catch(Exception e){
			AnalysisLogger.getLogger().debug("ERROR "+e.getLocalizedMessage());
			e.printStackTrace();
			throw e;
		}
		finally{
		shutdown();
		status = 100f;
		}
	}

	
	protected void BuildClusterTable(IOObject[] outputvector) throws Exception{
		
		ClusterModel innermodel = (ClusterModel) outputvector[0];
		ExampleSet es = (ExampleSet) outputvector[1];
		String columnsNames =FeaturesColumnNames+","+clusterColumn+","+outliersColumn;
		int minpoints = Integer.parseInt(minPoints);
		AnalysisLogger.getLogger().debug("Analyzing Cluster ->"+" minpoints"+minpoints);
		int nClusters = innermodel.getClusters().size();
		float statusstep = ((100f-status)/ (float)(nClusters+1));
		
		AnalysisLogger.getLogger().debug("Start Write On DB");
		for (Cluster c : innermodel.getClusters()){
			StringBuffer bufferRows = new StringBuffer();
			//take cluster id
			int id = c.getClusterId();
			boolean outlier = false;
			//take cluster element indexes
			int npoints = c.getExampleIds().size(); 
			AnalysisLogger.getLogger().debug("Analyzing Cluster ->"+id+" with "+npoints);
			if (npoints<=minpoints)
				outlier=true;
			
				int k=0;	
			
			for (Object o:c.getExampleIds()){
				//transform into a numerical index
				int idd = (int) Double.parseDouble(""+o);
				
				//take the corresponding sample
				Example e = es.getExample(idd-1);
				//take the attributes of the sample
				Attributes attributes = e.getAttributes();
				
				//for each attribute (yet filtered on numeric ones) add to the writing row
				bufferRows.append("(");
				StringBuffer valueStrings = new StringBuffer();
				for (Attribute attribute: attributes){
					valueStrings.append(e.getValue(attribute)+",");
				}
				String towrite = valueStrings.toString();
				towrite = towrite.substring(0,towrite.length()-1);
				
				//append the clusterid and outlier
				bufferRows.append(towrite+","+id+","+outlier+")");
				if (k<npoints-1){
					bufferRows.append(",");
				}
				
				k++;
//				AnalysisLogger.getLogger().trace("DBScan: Classification : "+towrite+"->"+id+" is outlier?"+outlier);
			}
			
			if (bufferRows.length()>0){
//			AnalysisLogger.getLogger().debug("DBScan: Inserting Buffer "+DatabaseUtils.insertFromBuffer(OccurrencePointsClusterTable, columnsNames, bufferRows));
			AnalysisLogger.getLogger().debug("Writing into DB");
			DatabaseFactory.executeSQLUpdate(DatabaseUtils.insertFromBuffer(OccurrencePointsClusterTable, columnsNames, bufferRows),dbHibConnection);
			AnalysisLogger.getLogger().debug("Finished with writing into DB");
			}else 
				AnalysisLogger.getLogger().debug("Nothing to write in the buffer");
			
			float instatus = status + statusstep;
			status = Math.min(95f, instatus);
			AnalysisLogger.getLogger().debug("Status: "+status);
	}
	}
	
	
	
	@Override
	public void shutdown() {
		try{
			AnalysisLogger.getLogger().debug("Closing DB Connection ");
			if (dbHibConnection!=null)
			dbHibConnection.close();
		}catch(Exception e){
			AnalysisLogger.getLogger().debug("Could not shut down connection");
		}
	}
	
	@Override
	public float getStatus() {
		return status;
	}

	@Override
	public StatisticalType getOutput() {
		List<TableTemplates> templateHspec = new ArrayList<TableTemplates>();
		templateHspec.add(TableTemplates.CLUSTER);
		return new OutputTable(templateHspec,OccurrencePointsClusterLabel,OccurrencePointsClusterTable,"Output cluster table");
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		List<TableTemplates> templateOccs = new ArrayList<TableTemplates>();
		templateOccs.add(TableTemplates.GENERIC);
		InputTable p1 = new InputTable(templateOccs,"OccurrencePointsTable","Occurrence Points Table. Max 4000 points","occurrences");
		ColumnTypesList p2 = new ColumnTypesList ("OccurrencePointsTable","FeaturesColumnNames", "column Names for the features", false);
		PrimitiveType p0 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, "OccurrencePointsClusterLabel","table name of the resulting distribution","OccCluster_");
		ServiceType p3 = new ServiceType(ServiceParameters.RANDOMSTRING, "OccurrencePointsClusterTable","table name of the distribution","occCluster_");
		PrimitiveType p4 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, "epsilon","DBScan epsilon parameter","10");
		PrimitiveType p5 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, "min_points","DBScan minimum points parameter (identifies outliers)","1");
		
		parameters.add(p1);
		parameters.add(p2);
		parameters.add(p0);
		parameters.add(p3);
		parameters.add(p4);
		parameters.add(p5);
		
		DatabaseType.addDefaultDBPars(parameters);
		
		return parameters;
	}

	@Override
	public String getDescription() {
		return "A clustering algorithm for real valued vectors that relies on the density-based spatial clustering of applications with noise (DBSCAN) algorithm. A maximum of 4000 points is allowed.";
	}


	ResourceFactory resourceManager;
	public String getResourceLoad() {
		if (resourceManager==null)
			resourceManager = new ResourceFactory();
		return resourceManager.getResourceLoad(1);
	}


	@Override
	public String getResources() {
		if ((status>0)&&(status<100))
			return ResourceFactory.getResources(100f);
		else
			return ResourceFactory.getResources(0f);
	}


	

}
