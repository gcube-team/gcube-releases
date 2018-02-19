package org.gcube.dataanalysis.ecoengine.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.MathFunctions;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.DataAnalysis;
import org.gcube.dataanalysis.ecoengine.models.cores.pca.PrincipalComponentAnalysis;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.Operations;
import org.gcube.dataanalysis.ecoengine.utils.Transformations;

public class HabitatRepresentativeness extends DataAnalysis {

	static String getNumberOfElementsQuery = "select count(*) from %1$s #OPTIONAL#";
	static String getRandomVectors = "select %1$s from %2$s #OPTIONAL# order by RANDOM() limit %3$s";
//	static String getRandomVectors = "select %1$s from %2$s #OPTIONAL# limit %3$s";
	
	String configPath = "./cfg/";

	private LinkedHashMap<String, String> output;
	private static int minimumNumberToTake = 10000;
	private float status;
	private int  currentIterationStep;
	private float innerstatus;
	private int maxTests = 2;
	
	public List<StatisticalType> getInputParameters() {
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		List<TableTemplates> templates = new ArrayList<TableTemplates>();
		templates.add(TableTemplates.HCAF);
		templates.add(TableTemplates.TRAININGSET);
		templates.add(TableTemplates.TESTSET);

		List<TableTemplates> templatesOccurrences = new ArrayList<TableTemplates>();
		templatesOccurrences.add(TableTemplates.OCCURRENCE_AQUAMAPS);
		templatesOccurrences.add(TableTemplates.TRAININGSET);
		templatesOccurrences.add(TableTemplates.TESTSET);
		
		InputTable p1 = new InputTable(templates,"ProjectingAreaTable","A Table containing projecting area information");
		PrimitiveType p2 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, "OptionalCondition","optional filter for taking area rows","where oceanarea>0",true);
		InputTable p3 = new InputTable(templates,"PositiveCasesTable","A Table containing positive cases");
		InputTable p4 = new InputTable(templates,"NegativeCasesTable","A Table containing negative cases");
		
//		PrimitiveType p5 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, "FeaturesColumns","fetures columns names separated by comma","depthmean,depthmax,depthmin, sstanmean,sbtanmean,salinitymean,salinitybmean, primprodmean,iceconann,landdist,oceanarea");
		ColumnTypesList p5 = new ColumnTypesList ("PositiveCasesTable","FeaturesColumns", "Features columns", false);

		parameters.add(p1);
		parameters.add(p2);
		parameters.add(p3);
		parameters.add(p4);
		parameters.add(p5);
		
		DatabaseType.addDefaultDBPars(parameters);
		return parameters;
	}

	public List<String> getOutputParameters() {

		List<String> outputs = new ArrayList<String>();

		outputs.add("HRS_VECTOR");
		outputs.add("HRS");

		return outputs;
	}

	private int calculateNumberOfPoints(String table, String option) {

		String numberOfPositiveCasesQuery = String.format(getNumberOfElementsQuery, table);
		numberOfPositiveCasesQuery = numberOfPositiveCasesQuery.replace("#OPTIONAL#", (option != null) ? option : "");
		List<Object> totalPoints = DatabaseFactory.executeSQLQuery(numberOfPositiveCasesQuery, connection);
		int points = Integer.parseInt("" + totalPoints.get(0));
		return points;
	}

	private double[][] getPoints(String table, String option, String features, int numberOfElemsToTake) {

		String query = String.format(getRandomVectors, features, table, "" + numberOfElemsToTake);
		query = query.replace("#OPTIONAL#", (option != null) ? option : "");

		AnalysisLogger.getLogger().trace("Compare - Query to perform for points:" + query);
		List<Object> caughtpoints = DatabaseFactory.executeSQLQuery(query, connection);
		int size = 0;
		if (caughtpoints != null)
			size = caughtpoints.size();
		double[][] points = null;
		if (size > 0) {

			points = new double[size][((Object[]) caughtpoints.get(0)).length];

			for (int i = 0; i < size; i++) {

				if (caughtpoints.get(i) != null) {
					Object[] arrayFeatures = (Object[]) caughtpoints.get(i);
					for (int j = 0; j < arrayFeatures.length; j++) {
						double delement = arrayFeatures[j] == null ? 0d : Double.parseDouble("" + arrayFeatures[j]);
						points[i][j] = delement;
					}
				}

			}
		}
		return points;
	}

	
	
	
	private void calcHRS(String projectingAreaTable, String projectingAreaFeaturesOptionalCondition, String  FeaturesColumns, String positiveCasesTable, String negativeCasesTable,int numberOfElements) throws Exception{
		innerstatus = 0f;
		int numberOfElementsToTake = Operations.calcNumOfRepresentativeElements(numberOfElements, minimumNumberToTake);
		AnalysisLogger.getLogger().trace("HRS: TAKING "+numberOfElementsToTake+" POINTS ON "+numberOfElements+" FROM THE AREA UNDER ANALYSIS");
		// 1 - take the right number of points
		double[][] areaPoints = getPoints(projectingAreaTable, projectingAreaFeaturesOptionalCondition, FeaturesColumns, numberOfElementsToTake);
		AnalysisLogger.getLogger().trace("HRS: AREA POINTS MATRIX GENERATED");
		innerstatus = 10f;
		Operations operations = new Operations();
		// 2 - standardize the matrix
		areaPoints = operations.standardize(areaPoints);
		AnalysisLogger.getLogger().trace("HRS: MATRIX HAS BEEN STANDARDIZED");
		innerstatus = 20f;
		// 3 - calculate PCA
		PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis();
		pca.calcPCA(areaPoints);
		AnalysisLogger.getLogger().trace("HRS: PCA HAS BEEN TRAINED");
		innerstatus = 30f;
		// 4 - get the pca components for all the vector
		double[][] pcaComponents = pca.getComponentsMatrix(areaPoints);
		AnalysisLogger.getLogger().trace("HRS: PCA COMPONENT CALCULATED");
		innerstatus = 40f;
		// 5 - calculate the frequency distributions for all the pca: each row will be a frequency distribution for a pca component associated to uniform divisions of the range
		calcFrequenciesDistributionsForComponents(pcaComponents);
		AnalysisLogger.getLogger().trace("HRS: FREQUENCIES FOR COMPONENTS CALCULATED");
		innerstatus = 50f;
		// 6 - take positive points and negative points - eventually merge them
		double[][] positivePoints = null;
		if ((positiveCasesTable!=null) && (positiveCasesTable.length()>0))
				positivePoints = getPoints(positiveCasesTable, "", FeaturesColumns, numberOfElementsToTake);
		double[][] negativePoints = null;
		if ((negativeCasesTable!=null) && (negativeCasesTable.length()>0))
			negativePoints = getPoints(negativeCasesTable, "", FeaturesColumns, numberOfElementsToTake);
		double[][] habitatPoints = Transformations.mergeMatrixes(positivePoints, negativePoints);
		AnalysisLogger.getLogger().trace("HRS: HABITAT POINTS BUILT FROM POSITIVE AND NEGATIVE POINTS");
		innerstatus = 60f;
		// 7 - Standardize the points respect to previous means and variances
		habitatPoints = operations.standardize(habitatPoints, operations.means, operations.variances);
		AnalysisLogger.getLogger().trace("HRS: HABITAT POINTS HAVE BEEN STANDARDIZED RESPECT TO PREVIOUS MEANS AND VARIANCES");
		// 8 - calculate the pca components for habitat
		double[][] habitatPcaComponents = pca.getComponentsMatrix(habitatPoints);
		AnalysisLogger.getLogger().trace("HRS: HABITAT POINTS HAVE BEEN TRANSFORMED BY PCA");
		innerstatus = 70f;
		// 9 - calculate frequencies distributions for each component, respect to previous intervals
		int components = habitatPcaComponents[0].length;
		// 10 - calculate absolute differences and sum -> obtain a hrs for each PCA component = for each feature
		currentHRSVector = new double[components];

		double[][] habitatPcaPointsMatrix = Transformations.traspose(habitatPcaComponents);
		for (int i = 0; i < components; i++) {
			double[] habitatPcaPoints = habitatPcaPointsMatrix[i];
			// calculate frequency distributions respect to previous intervals
			double[] habitatPcafrequencies = Operations.calcFrequencies(intervals.get(i), habitatPcaPoints);
			habitatPcafrequencies = Operations.normalizeFrequencies(habitatPcafrequencies, habitatPcaPoints.length);
			double[] absdifference = Operations.vectorialAbsoluteDifference(habitatPcafrequencies, frequencyDistrib.get(i));
			currentHRSVector[i] = Operations.sumVector(absdifference);
		}

		AnalysisLogger.getLogger().trace("HRS: HRS VECTOR HAS BEEN CALCULATED");
		innerstatus = 90f;
		// 11 - obtain hrsScore by weighted sum of hrs respect to inverse eigenvalues - too variable, substituted with the sum of the scores
//		currentHRSScore = Operations.scalarProduct(currentHRSVector, pca.getInverseNormalizedEigenvalues());
		currentHRSScore = Operations.sumVector(currentHRSVector);
		
		AnalysisLogger.getLogger().trace("HRS: HRS SCORE HAS BEEN CALCULATED");
		innerstatus = 100f;
	}
	private double meanHRS ;
	private double [] meanHRSVector;
	private double currentHRSScore;
	private double [] currentHRSVector;
	
	public LinkedHashMap<String, String> analyze() throws Exception {

		try {
			status = 0;
			String projectingAreaTable = config.getParam("ProjectingAreaTable");
			String projectingAreaFeaturesOptionalCondition = config.getParam("OptionalCondition");
			String FeaturesColumns = config.getParam("FeaturesColumns").replace(AlgorithmConfiguration.getListSeparator(), ",");
			String positiveCasesTable = config.getParam("PositiveCasesTable");
			String negativeCasesTable = config.getParam("NegativeCasesTable");
			connection = AlgorithmConfiguration.getConnectionFromConfig(config);
			meanHRS = 0;
			int numberOfElements = calculateNumberOfPoints(projectingAreaTable, projectingAreaFeaturesOptionalCondition);
			
			for (int i=0;i<maxTests;i++){
				currentIterationStep = i;
				AnalysisLogger.getLogger().trace("ITERATION NUMBER "+(i+1));
				calcHRS(projectingAreaTable, projectingAreaFeaturesOptionalCondition, FeaturesColumns, positiveCasesTable, negativeCasesTable, numberOfElements);
				meanHRS = MathFunctions.incrementAvg(meanHRS, currentHRSScore, i);
				if (meanHRSVector==null)
					meanHRSVector = new double[currentHRSVector.length];
				
				for (int j=0;j<currentHRSVector.length;j++){
					meanHRSVector[j]=org.gcube.contentmanagement.graphtools.utils.MathFunctions.roundDecimal(MathFunctions.incrementAvg(meanHRSVector[j],currentHRSVector[j],i),2);
				}
				
				AnalysisLogger.getLogger().trace("ITERATION FINISHED "+meanHRS);
				status=Math.min(status+100f/maxTests,99f);
			}
			
			output = new LinkedHashMap<String, String>();
			output.put("HRS_VECTOR", "" + Transformations.vector2String(meanHRSVector));
			output.put("HRS", "" + org.gcube.contentmanagement.graphtools.utils.MathFunctions.roundDecimal(meanHRS,2));

			return output;
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().error("ALERT: AN ERROR OCCURRED DURING HRS CALCULATION : " + e.getLocalizedMessage());
			throw e;
		} finally {
			status=100;
			AnalysisLogger.getLogger().trace("COMPUTATION FINISHED ");
		}
	}

	List<double[]> frequencyDistrib;
	List<double[]> intervals; // uniform subdivision of the numeric ranges

	// calculate a frequency distribution for each component
	public void calcFrequenciesDistributionsForComponents(double[][] pcaComponents) {
		frequencyDistrib = null;
		if (pcaComponents.length > 0) {
			int sizeDistrib = pcaComponents[0].length;
			frequencyDistrib = new ArrayList<double[]>();
			intervals = new ArrayList<double[]>();
			double[][] pcaColumns = Transformations.traspose(pcaComponents);
			for (int i = 0; i < sizeDistrib; i++) {
				double[] pcaPoints = pcaColumns[i];
				double[] interval = Operations.uniformDivide(Operations.getMax(pcaPoints), Operations.getMin(pcaPoints), pcaPoints);
				double[] frequencies = Operations.calcFrequencies(interval, pcaPoints);
				frequencies = Operations.normalizeFrequencies(frequencies, pcaPoints.length);
				intervals.add(interval);
				frequencyDistrib.add(frequencies);
			}
		}
	}

	public static void visualizeResults(HashMap<String, Object> results) {

		for (String key : results.keySet()) {
			System.out.println(key + ":" + results.get(key));
		}
	}

	public static void main(String[] args) throws Exception {

		AnalysisLogger.setLogger("./cfg/" + AlgorithmConfiguration.defaultLoggerFile);
		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setParam("ProjectingAreaTable", "hcaf_d");
//		config.setParam("ProjectingAreaTable", "absence_data_baskingshark_random");
//		config.setParam("ProjectingAreaTable", "absence_data_baskingshark2");
		config.setParam("OptionalCondition", "where oceanarea>0");
		config.setParam("FeaturesColumns", "depthmean,depthmax,depthmin, sstanmean,sbtanmean,salinitymean,salinitybmean, primprodmean,iceconann,landdist,oceanarea");
		config.setParam("PositiveCasesTable", "presence_data_baskingshark");
		config.setParam("NegativeCasesTable", "absence_data_baskingshark_random");
//		config.setParam("NegativeCasesTable", "absence_data_baskingshark2");
		
		HabitatRepresentativeness hsrcalc = new HabitatRepresentativeness();
		hsrcalc.setConfiguration(config);
		hsrcalc.init();
		 
		 HashMap<String,String> output =  hsrcalc.analyze();
		 for (String param:output.keySet()){
			 System.out.println(param+":"+output.get(param));
		 }
		 /*
		double[][] matrix = new double[7][2];
		double[] row1 = { 2d, 3d };
		double[] row2 = { 3d, 4d };
		double[] row3 = { 4d, 5d };
		double[] row4 = { 5d, 6d };
		double[] row5 = { 2d, 3d };
		double[] row6 = { 2d, 5d };
		double[] row7 = { 3d, 4d };

		matrix[0] = row1;
		matrix[1] = row2;
		matrix[2] = row3;
		matrix[3] = row4;
		matrix[4] = row5;
		matrix[5] = row6;
		matrix[6] = row7;
*/
		// Operations operations = new Operations();
		// matrix = operations.standardize(matrix);
		// hsrcalc.calcFrequenciesDistributionsForComponents(matrix);

//		double[][] bigmat = Transformations.mergeMatrixes(null, matrix);

		System.out.println("FINISHED");
	}

	@Override
	public StatisticalType getOutput() {
		PrimitiveType p = new PrimitiveType(Map.class.getName(), PrimitiveType.stringMap2StatisticalMap(output), PrimitiveTypes.MAP, "AnalysisResult","Habitat Representativeness Score");
		return p;
	}
	
	@Override
	public float getStatus() {
		return status==100f?status: Math.min((status+(float)(currentIterationStep+1)*innerstatus/(float)maxTests),99f);
	}

	@Override
	public String getDescription() {
		return "An evaluator algorithm that calculates the Habitat Representativeness Score, i.e. an indicator of the assessment of whether a specific survey coverage or another environmental features dataset, contains data that are representative of all available habitat variable combinations in an area.";
	}

}
