package org.gcube.dataanalysis.ecoengine.clustering;

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
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.DatabaseParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.utils.ResourceFactory;

import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.IOObject;
import com.rapidminer.tools.OperatorService;

public class KMeans extends DBScan{

	private String kk;
	private String maxRuns;
	private String maxOptimizations;

	public static void main(String[] args) throws Exception{
		long t0 = System.currentTimeMillis();
		
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("OccurrencePointsTable","presence_basking_cluster");
		config.setParam("FeaturesColumnNames","centerlat"+AlgorithmConfiguration.getListSeparator()+"centerlong");
		config.setParam("OccurrencePointsClusterTable","occCluster_kmeans");
		config.setParam("k","50");
		config.setParam("max_runs","10");
		config.setParam("max_optimization_steps","10");
		config.setParam("min_points","2");
		
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://146.48.87.169/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		
		KMeans cluster = new KMeans();
		cluster.setConfiguration(config);
		cluster.init();
		cluster.compute();
		
		System.out.println("ELAPSED "+(System.currentTimeMillis()-t0));
		
	}
	
	
	@Override
	public void setConfiguration(AlgorithmConfiguration config) {
		if (config!=null){
		kk=config.getParam("k");
		maxRuns= config.getParam("max_runs");
		maxOptimizations = config.getParam("max_optimization_steps");
		OccurrencePointsClusterLabel=config.getParam("OccurrencePointsClusterLabel");
		OccurrencePointsTable = config.getParam("OccurrencePointsTable").toLowerCase();
		OccurrencePointsClusterTable=config.getParam("OccurrencePointsClusterTable").toLowerCase();
		FeaturesColumnNames=config.getParam("FeaturesColumnNames");
		minPoints=config.getParam("min_points");
		this.config=config;
		}
		
	}
	
	@Override
	public void compute() throws Exception {
		try{
		if ((config==null)||kk==null||maxRuns==null||maxOptimizations==null){
			throw new Exception("KMeans: Error incomplete parameters");
		}
		
		AnalysisLogger.getLogger().debug("KMeans: Settin up the cluster");
		//take elements and produce example set
		com.rapidminer.operator.clustering.clusterer.KMeans kmeans =  (com.rapidminer.operator.clustering.clusterer.KMeans) OperatorService.createOperator("KMeans");

		kmeans.setParameter("k", kk);
		kmeans.setParameter("max_runs",maxRuns);
		kmeans.setParameter("max_optimization_steps", maxOptimizations);
		
		kmeans.setParameter("keep_example_set", "true");
		kmeans.setParameter("add_cluster_attribute", "true");
		
		
		IOContainer innerInput = new IOContainer(points);
		
		AnalysisLogger.getLogger().debug("KMeans: Clustering...");
		long ti= System.currentTimeMillis();
		IOContainer output = kmeans.apply(innerInput);
		AnalysisLogger.getLogger().debug("KMEANS: ...ELAPSED CLUSTERING TIME: "+(System.currentTimeMillis()-ti));
		AnalysisLogger.getLogger().debug("KMeans: ...Clustering Finished");
		status = 70f;
		
		IOObject[] outputvector = output.getIOObjects();

		BuildClusterTable(outputvector);
		}catch(Exception e){
		throw e;
		}
		finally{
			shutdown();
			status = 100f;
		}
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
		PrimitiveType p4 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, "k","expected Number of Clusters","3");
		PrimitiveType p5 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, "max_runs","max runs of the clustering procedure","10");
		PrimitiveType p12 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, "max_optimization_steps","max number of internal optimization steps","5");
		PrimitiveType p13 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, "min_points","number of points which define an outlier set","2");
		
		parameters.add(p1);
		parameters.add(p2);
		parameters.add(p0);
		parameters.add(p3);
		parameters.add(p4);
		parameters.add(p5);
		parameters.add(p12);
		parameters.add(p13);
		
		DatabaseType.addDefaultDBPars(parameters);
		return parameters;
	}

	@Override
	public String getDescription() {
		return "A clustering algorithm for real valued vectors that relies on the k-means algorithm, i.e. a method aiming to partition n observations into k clusters in which each observation belongs to the cluster with the nearest mean, serving as a prototype of the cluster.  A Maximum of 4000 points is allowed.";
	}


	ResourceFactory resourceManager;
	public String getResourceLoad() {
		if (resourceManager==null)
			resourceManager = new ResourceFactory();
		return resourceManager.getResourceLoad(1);
	}


	@Override
	public String getResources() {
		return ResourceFactory.getResources(100f);
	}


	

}
