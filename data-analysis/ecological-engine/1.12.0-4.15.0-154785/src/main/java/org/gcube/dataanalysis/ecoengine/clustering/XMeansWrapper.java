package org.gcube.dataanalysis.ecoengine.clustering;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
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
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;

import weka.clusterers.ClusterEvaluation;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

public class XMeansWrapper extends DBScan {

	private String maxIterations;
	private String minClusters;
	private String maxClusters;
	
	public XMeansWrapper(){
		super();
		initrapidminer=false;
	}
	public static void main1(String[] args) throws Exception {
		args = new String[2];
		args[0] = "input.csv";
		args[1] = "c:/tmp/output.arff";
		// load CSV
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(args[0]));
		Instances data = loader.getDataSet();

		// save ARFF
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(new File(args[1]));
		// saver.setDestination(new File(args[1]));
		saver.writeBatch();
	}

	public class CSV2Arff {
		/**
		 * takes 2 arguments: - CSV input file - ARFF output file
		 */

	}

	public static void main(String[] args) throws Exception {
		XMeans xmeans = new XMeans();
		// xmeans.setInputCenterFile(new File("./clusterinput.arf"));
		// String[] options = {"-I 10","-M 1000","-J 1000","-L 2","-H 50","-B 1.0","-use-kdtree no","-N clusterinput.arf","-O clusterout.txt","-U 3"};
		// String[] options = {"-I 10","-M 1000","-J 1000","-L 2","-H 50","-B 1.0","-use-kdtree no","-t clusterinput.arf","-O clusterout.txt","-U 3"};
		// String optionsS = "-t c:/tmp/output.arff -O c:/tmp/clusterout.arff";
		String optionsS = "-t c:/tmp/output.arff";
		String[] options = optionsS.split(" ");
		String elements = "ciao,tutti\n5.1,3.5\n4.9,3.0\n4.7,3.2\n4.6,3.1\n5.0,3.6\n5.4,3.9\n4.6,3.4\n5.0,3.4\n4.4,2.9\n4.9,3.1\n";

		// xmeans.setInputCenterFile(new File("./clusterinput.arf"));

		CSVLoader loader = new CSVLoader();
		InputStream tis = new ByteArrayInputStream(elements.getBytes("UTF-8"));
		loader.setSource(tis);
		Instances id = loader.getDataSet();
		System.out.println("ids: "+id.numInstances());
		System.exit(0);
		xmeans.buildClusterer(id);

		// xmeans.main(options);
		// ClusterEvaluation.evaluateClusterer(xmeans, options);
		/*
		 * String[] opts = xmeans.getOptions(); for (int i=0;i<opts.length;i++){ System.out.println("options: "+opts[i]); }
		 */

		System.out.println(ClusterEvaluation.evaluateClusterer(xmeans, options));
		// ClusterEvaluation.evaluateClusterer(xmeans, options);
		System.out.println("*************");
		Instances is = xmeans.getClusterCenters();
		for (Instance i : is) {
			DenseInstance di = (DenseInstance) i;
			System.out.println("Attributes: " + i.numAttributes());
			System.out.print("->" + di.toString(0));
			System.out.println(" " + di.toString(1));

			// System.out.println(i);

			System.out.println("-------------------------------");
		}

		System.out.println(xmeans.m_Bic);

		// System.out.println(xmeans.clusterInstance(instance));
		int[] ii = xmeans.m_ClusterAssignments;
		for (int ix : ii)
			System.out.print(ix + " ");

		// xmeans.main(options);
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		List<TableTemplates> templateOccs = new ArrayList<TableTemplates>();
		templateOccs.add(TableTemplates.GENERIC);

		InputTable p1 = new InputTable(templateOccs, "OccurrencePointsTable", "Occurrence Points Table. Max 4000 points", "occurrences");
		ColumnTypesList p2 = new ColumnTypesList ("OccurrencePointsTable","FeaturesColumnNames", "column Names for the features", false);
		PrimitiveType p0 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, "OccurrencePointsClusterLabel","table name of the resulting distribution","OccCluster_");
		ServiceType p3 = new ServiceType(ServiceParameters.RANDOMSTRING, "OccurrencePointsClusterTable", "table name of the distribution", "occCluster_");

		PrimitiveType p4 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, "maxIterations", "XMeans max number of overall iterations of the clustering learning", "10");
		PrimitiveType p5 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, "minClusters", "minimum number of expected clusters", "1");
		PrimitiveType p12 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, "maxClusters", "maximum number of clusters to produce", "50");
		PrimitiveType p13 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, "min_points", "number of points which define an outlier set", "2");

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
		return "A clustering algorithm for occurrence points that relies on the X-Means algorithm, i.e. an extended version of the K-Means algorithm improved by an Improve-Structure part. A Maximum of 4000 points is allowed.";
	}

	@Override
	public void setConfiguration(AlgorithmConfiguration config) {
		if (config != null) {
			maxIterations = config.getParam("maxIterations");
			minClusters = config.getParam("minClusters");
			maxClusters = config.getParam("maxClusters");
			minPoints = config.getParam("min_points");
			OccurrencePointsTable = config.getParam("OccurrencePointsTable").toLowerCase();
			OccurrencePointsClusterLabel=config.getParam("OccurrencePointsClusterLabel");
			OccurrencePointsClusterTable = config.getParam("OccurrencePointsClusterTable").toLowerCase();
			FeaturesColumnNames = config.getParam("FeaturesColumnNames");
			this.config = config;
		}
	}

	@Override
	public void compute() throws Exception {
		try{
		if ((config == null) || maxIterations == null || minClusters == null || maxClusters == null) {
			throw new Exception("XMeans: Error incomplete parameters");
		}

		if ((samplesVector != null) && (samplesVector.length > 0)) {
			
			
			AnalysisLogger.getLogger().debug("XMeans: Setting up the cluster");
			CSVLoader loader = new CSVLoader();
			StringBuffer sb = new StringBuffer();
			
			for (int i = -1; i < samplesVector.length; i++) {
				for (int j = 0; j < samplesVector[0].length; j++) {
					if (i==-1)
						sb.append("F"+j);
					else	
						sb.append(samplesVector[i][j]);
					if (j < samplesVector[0].length - 1) {
						sb.append(",");
					} else
						sb.append("\n");
				}
			}

			InputStream tis = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
			loader.setSource(tis);
			Instances id = loader.getDataSet();
			long ti= System.currentTimeMillis();
			XMeans xmeans = new XMeans();
			xmeans.setMaxIterations(Integer.parseInt(maxIterations));
			xmeans.setMinNumClusters(Integer.parseInt(minClusters));
			xmeans.setMaxNumClusters(Integer.parseInt(maxClusters));
			xmeans.buildClusterer(id);
			AnalysisLogger.getLogger().debug("XMEANS: ...ELAPSED CLUSTERING TIME: "+(System.currentTimeMillis()-ti));
			status = 50f;
			
			// do clustering
			AnalysisLogger.getLogger().debug("XMeans: Clustering ...");
			Instances is = xmeans.getClusterCenters();
			int nClusters = is.numInstances();
			// take results
			AnalysisLogger.getLogger().debug("XMeans: Found "+nClusters+" Centroids");
			for (Instance i : is) {
				DenseInstance di = (DenseInstance) i;
				int nCluster = di.numAttributes();
				for (int k = 0; k < nCluster; k++) {
					AnalysisLogger.getLogger().debug(di.toString(k));
				}
				AnalysisLogger.getLogger().debug("-------------------------------");
			}

			int[] clusteringAssignments = xmeans.m_ClusterAssignments;
			int[] counters = new int[nClusters];
			
			for (int cluster:clusteringAssignments){
				counters[cluster]++;
			}
			
			AnalysisLogger.getLogger().debug("XMeans: Building Table");
			BuildClusterTable(clusteringAssignments, counters);

		} else
			AnalysisLogger.getLogger().debug("XMeans: Warning - Empty Training Set");
		}catch(Exception e){
			throw e;
		}
		finally{
			shutdown();
			status = 100f;
		}
	}

	protected void BuildClusterTable(int[] clusteringAssignments, int[] counters) throws Exception {
		
		
		String columnsNames = FeaturesColumnNames + "," + clusterColumn + "," + outliersColumn;
		int minpoints = Integer.parseInt(minPoints);
		AnalysisLogger.getLogger().debug("Analyzing Cluster ->" + " minpoints " + minpoints);
		
		StringBuffer bufferRows = new StringBuffer();
		int nrows = samplesVector.length;
		int ncols = samplesVector[0].length;

		AnalysisLogger.getLogger().debug("Analyzing Cluster ->" + "Building Rows to Insert");
		
		for (int k = 0; k < nrows; k++) {
			bufferRows.append("(");
			int cindex = clusteringAssignments[k];
			boolean isoutlier = (counters[cindex]<=minpoints);
			
			for (int j = 0; j < ncols; j++) {
				bufferRows.append(samplesVector[k][j]);
				bufferRows.append(",");
			}
			
			bufferRows.append(cindex + "," + isoutlier + ")");
			
			if (k < nrows - 1) {
				bufferRows.append(",");
			}
		}
		
		//TO-DO: insert row at chunks
		
		AnalysisLogger.getLogger().debug("Analyzing Cluster ->" + "Inserting rows");
		
		if (bufferRows.length() > 0) {
			AnalysisLogger.getLogger().debug("XMeans: Writing into DB");
			AnalysisLogger.getLogger().debug(DatabaseUtils.insertFromBuffer(OccurrencePointsClusterTable, columnsNames, bufferRows));
			DatabaseFactory.executeSQLUpdate(DatabaseUtils.insertFromBuffer(OccurrencePointsClusterTable, columnsNames, bufferRows), dbHibConnection);
			AnalysisLogger.getLogger().debug("XMeans: Finished with writing into DB");
		} else
			AnalysisLogger.getLogger().debug("XMeans: Nothing to write in the buffer");

		status = 95f;
		AnalysisLogger.getLogger().debug("XMeans: Status: " + status);

	}

}
