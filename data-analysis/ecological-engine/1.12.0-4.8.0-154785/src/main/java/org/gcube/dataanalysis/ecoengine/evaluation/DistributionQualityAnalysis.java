package org.gcube.dataanalysis.ecoengine.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.DataAnalysis;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.BinominalMapping;
import com.rapidminer.example.table.DoubleArrayDataRow;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.tools.Ontology;
import com.rapidminer.tools.math.ROCData;
import com.rapidminer.tools.math.ROCDataGenerator;

public class DistributionQualityAnalysis extends DataAnalysis {

	static String getProbabilititesQuery = "select count(*) from (select distinct * from %1$s as a join %2$s as b on a.%3$s=b.%4$s and b.%5$s %6$s %7$s) as aa";
	static String getNumberOfElementsQuery = "select count(*) from %1$s";
	static String getValuesQuery = "select %5$s as distribprob (select distinct * from %1$s as a join %2$s as b on a.%3$s=b.%4$s) as b";

	float threshold = 0.1f;
	String configPath = "./cfg/";
	float acceptanceThreshold = 0.8f;
	float rejectionThreshold = 0.3f;
	double bestThreshold = 0.5d;
	private LinkedHashMap<String, String> output;

	public List<StatisticalType> getInputParameters() {
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		List<TableTemplates> templates = new ArrayList<TableTemplates>();
		templates.add(TableTemplates.HSPEC);
		templates.add(TableTemplates.TRAININGSET);
		templates.add(TableTemplates.TESTSET);
		
		List<TableTemplates> templatesOccurrences = new ArrayList<TableTemplates>();
		templatesOccurrences.add(TableTemplates.HCAF);
		
		InputTable p1 = new InputTable(templatesOccurrences,"PositiveCasesTable","A Table containing positive cases");
		InputTable p2 = new InputTable(templatesOccurrences,"NegativeCasesTable","A Table containing negative cases");
		InputTable p5 = new InputTable(templates,"DistributionTable","A probability distribution table");
		
		ColumnType p3 = new ColumnType("PositiveCasesTable", "PositiveCasesTableKeyColumn", "Positive Cases Table Key Column", "csquarecode", false);
		ColumnType p4 = new ColumnType("NegativeCasesTable", "NegativeCasesTableKeyColumn", "Negative Cases Table Key Column", "csquarecode", false);
		ColumnType p6 = new ColumnType("DistributionTable", "DistributionTableKeyColumn", "Distribution Table Key Column", "csquarecode", false);
		ColumnType p7 = new ColumnType("DistributionTable", "DistributionTableProbabilityColumn", "Distribution Table Probability Column", "probability", false);
		
		PrimitiveType p8 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, "PositiveThreshold","Positive acceptance threshold","0.8");
		PrimitiveType p9 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, "NegativeThreshold","Negative acceptance threshold","0.3");
		
		parameters.add(p1);
		parameters.add(p2);
		parameters.add(p3);
		parameters.add(p4);
		parameters.add(p5);
		parameters.add(p6);
		parameters.add(p7);
		parameters.add(p8);
		parameters.add(p9);
		
		DatabaseType.addDefaultDBPars(parameters);
		
		return parameters;
	}

	public List<String> getOutputParameters() {

		List<String> outputs = new ArrayList<String>();

		outputs.add("TRUE_POSITIVES");
		outputs.add("TRUE_NEGATIVES");
		outputs.add("FALSE_POSITIVES");
		outputs.add("FALSE_NEGATIVES");
		outputs.add("AUC");
		outputs.add("ACCURACY");
		outputs.add("SENSITIVITY");
		outputs.add("OMISSIONRATE");
		outputs.add("SPECIFICITY");
		outputs.add("BESTTHRESHOLD");

		return outputs;
	}

	private int calculateNumberOfPoints(String table) {

		String numberOfPositiveCasesQuery = String.format(getNumberOfElementsQuery, table);
		List<Object> totalPoints = DatabaseFactory.executeSQLQuery(numberOfPositiveCasesQuery, connection);
		int points = Integer.parseInt("" + totalPoints.get(0));
		return points;
	}

	private int calculateCaughtPoints(String casesTable, String distributionTable, String casesTableKeyColumn, String distributionTableKeyColumn, String distributionTableProbabilityColumn, String operator, String threshold) {

		String query = String.format(getProbabilititesQuery, casesTable, distributionTable, casesTableKeyColumn, distributionTableKeyColumn, distributionTableProbabilityColumn, operator, threshold);
		AnalysisLogger.getLogger().trace("Compare - Query to perform for caught cases:" + query);
		List<Object> caughtpoints = DatabaseFactory.executeSQLQuery(query, connection);
		int points = Integer.parseInt("" + caughtpoints.get(0));
		return points;
	}

	private double[] getPoints(String casesTable, String distributionTable, String casesTableKeyColumn, String distributionTableKeyColumn, String distributionTableProbabilityColumn, int numberOfExpectedPoints) {

		String query = String.format(getValuesQuery, casesTable, distributionTable, casesTableKeyColumn, distributionTableKeyColumn, distributionTableProbabilityColumn);

		AnalysisLogger.getLogger().trace("Compare - Query to perform for caught cases:" + query);
		List<Object> caughtpoints = DatabaseFactory.executeSQLQuery(query, connection);
		int size = 0;
		if (caughtpoints != null)
			size = caughtpoints.size();
		double[] points = new double[numberOfExpectedPoints];

		for (int i = 0; i < size; i++) {
			double element = 0;
			if (caughtpoints.get(i) != null)
				element = Double.parseDouble("" + caughtpoints.get(i));

			points[i] = element;
		}

		return points;
	}

	public LinkedHashMap<String, String> analyze() throws Exception {

		try {
			acceptanceThreshold = Float.parseFloat(config.getParam("PositiveThreshold"));
		} catch (Exception e) {
			AnalysisLogger.getLogger().debug("ERROR : " + e.getLocalizedMessage());
		}
		try {
			rejectionThreshold = Float.parseFloat(config.getParam("NegativeThreshold"));
		} catch (Exception e) {
			AnalysisLogger.getLogger().debug("ERROR : " + e.getLocalizedMessage());
		}

		String positiveCasesTable = config.getParam("PositiveCasesTable");
		String negativeCasesTable = config.getParam("NegativeCasesTable");
		String distributionTable = config.getParam("DistributionTable");
		String positiveCasesTableKeyColumn = config.getParam("PositiveCasesTableKeyColumn");
		String negativeCasesTableKeyColumn = config.getParam("NegativeCasesTableKeyColumn");
		String distributionTableKeyColumn = config.getParam("DistributionTableKeyColumn");
		String distributionTableProbabilityColumn = config.getParam("DistributionTableProbabilityColumn");
		String acceptanceThreshold = config.getParam("PositiveThreshold");
		String rejectionThreshold = config.getParam("NegativeThreshold");

		int numberOfPositiveCases = calculateNumberOfPoints(positiveCasesTable);

		int truePositives = calculateCaughtPoints(positiveCasesTable, distributionTable, positiveCasesTableKeyColumn, distributionTableKeyColumn, distributionTableProbabilityColumn, ">", acceptanceThreshold);

		int falseNegatives = numberOfPositiveCases - truePositives;

		int numberOfNegativeCases = calculateNumberOfPoints(negativeCasesTable);

		super.processedRecords = numberOfPositiveCases + numberOfNegativeCases;

		int falsePositives = calculateCaughtPoints(negativeCasesTable, distributionTable, negativeCasesTableKeyColumn, distributionTableKeyColumn, distributionTableProbabilityColumn, ">", rejectionThreshold);

		int trueNegatives = numberOfNegativeCases - falsePositives;

		double[] positivePoints = getPoints(positiveCasesTable, distributionTable, positiveCasesTableKeyColumn, distributionTableKeyColumn, distributionTableProbabilityColumn, numberOfPositiveCases);

		double[] negativePoints = getPoints(negativeCasesTable, distributionTable, negativeCasesTableKeyColumn, distributionTableKeyColumn, distributionTableProbabilityColumn, numberOfNegativeCases);

		double auc = calculateAUC(positivePoints, negativePoints, false);
		double accuracy = calculateAccuracy(truePositives, trueNegatives, falsePositives, falseNegatives);
		double sensitivity = calculateSensitivity(truePositives, falseNegatives);
		double omissionrate = calculateOmissionRate(truePositives, falseNegatives);
		double specificity = calculateSpecificity(trueNegatives, falsePositives);

		output = new LinkedHashMap<String, String>();
		output.put("TRUE_POSITIVES", "" + truePositives);
		output.put("TRUE_NEGATIVES", "" + trueNegatives);
		output.put("FALSE_POSITIVES", "" + falsePositives);
		output.put("FALSE_NEGATIVES", "" + falseNegatives);
		output.put("AUC", "" + MathFunctions.roundDecimal(auc,2));
		output.put("ACCURACY", "" + MathFunctions.roundDecimal(accuracy,2));
		output.put("SENSITIVITY", "" + MathFunctions.roundDecimal(sensitivity,2));
		output.put("OMISSIONRATE", "" + MathFunctions.roundDecimal(omissionrate,2));
		output.put("SPECIFICITY", "" + MathFunctions.roundDecimal(specificity,2));
		output.put("BESTTHRESHOLD", "" + MathFunctions.roundDecimal(bestThreshold,2));

		return output;
	}

	public double calculateSensitivity(int TP, int FN) {
		return (double) (TP) / (double) (TP + FN);
	}

	public double calculateOmissionRate(int TP, int FN) {
		return (double) (FN) / (double) (TP + FN);
	}

	public double calculateSpecificity(int TN, int FP) {
		return (double) (TN) / (double) (TN + FP);
	}

	public double calculateAccuracy(int TP, int TN, int FP, int FN) {
		return (double) (TP + TN) / (double) (TP + TN + FP + FN);
	}

	public double calculateAUC(double[] scoresOnPresence, double[] scoresOnAbsence, boolean produceChart) {

		List<Attribute> attributes = new LinkedList<Attribute>();
		Attribute labelAtt = AttributeFactory.createAttribute("LABEL", Ontology.BINOMINAL);
		BinominalMapping bm = (BinominalMapping) labelAtt.getMapping();
		bm.setMapping("1", 1);
		bm.setMapping("0", 0);

		Attribute confidenceAtt1 = AttributeFactory.createAttribute(Attributes.CONFIDENCE_NAME + "_1", Ontology.REAL);
		attributes.add(confidenceAtt1);
		attributes.add(labelAtt);

		MemoryExampleTable table = new MemoryExampleTable(attributes);
		int numOfPoints = scoresOnPresence.length + scoresOnAbsence.length;
		int numOfPresence = scoresOnPresence.length;
		int numOfAttributes = attributes.size();
		double pos = labelAtt.getMapping().mapString("1");
		double neg = labelAtt.getMapping().mapString("0");

		for (int i = 0; i < numOfPresence; i++) {
			double[] data = new double[numOfAttributes];
			data[0] = scoresOnPresence[i];
			data[1] = pos;
			table.addDataRow(new DoubleArrayDataRow(data));
		}

		for (int i = numOfPresence; i < numOfPoints; i++) {
			double[] data = new double[numOfAttributes];
			data[0] = scoresOnAbsence[i - numOfPresence];
			data[1] = neg;
			table.addDataRow(new DoubleArrayDataRow(data));
		}

		ROCDataGenerator roc = new ROCDataGenerator(acceptanceThreshold, rejectionThreshold);
		ExampleSet exampleSet = table.createExampleSet(labelAtt);
		exampleSet.getAttributes().setSpecialAttribute(confidenceAtt1, Attributes.CONFIDENCE_NAME + "_1");

		ROCData dataROC = roc.createROCData(exampleSet, false);
		double auc = roc.calculateAUC(dataROC);

		// PLOTS THE ROC!!!
		if (produceChart)
			roc.createROCPlotDialog(dataROC);

		bestThreshold = roc.getBestThreshold();
		return auc;
	}

	public static void visualizeResults(HashMap<String, Object> results) {

		for (String key : results.keySet()) {
			System.out.println(key + ":" + results.get(key));
		}
	}

	public static void main(String[] args) {

		AnalysisLogger.setLogger("./cfg/" + AlgorithmConfiguration.defaultLoggerFile);
		/*
		 * double [] pos = new double [4]; // pos[0] = 1d; pos[1] = 0.8d;pos[2]=0.7;pos[3]=0.9; // pos[0] = 1d; pos[1] = 1d;pos[2]=1;pos[3]=1; // pos[0] = 0.3d; pos[1] = 0.7d;pos[2]=0.1;pos[3]=0.9;
		 * 
		 * 
		 * double [] neg = new double [4]; // neg[0] = 0d; neg[1] = 0.3d;neg[2]=0.4;neg[3]=0.6; // neg[0] = 0d; neg[1] = 0.0d;neg[2]=0.0;neg[3]=0.0; // neg[0] = 0.7d; neg[1] = 0.3d;neg[2]=0.9;neg[3]=0.1;
		 * 
		 * DistributionQualityAnalysis quality = new DistributionQualityAnalysis(); double auc = quality.calculateAUC(pos, neg); System.out.println("AUC: "+auc);
		 * 
		 * int n = 100; double[] posRandom = new double[n]; double[] negRandom = new double[n];
		 * 
		 * for (int i=0;i<n;i++){ posRandom[i] = Math.random(); negRandom[i] = Math.random(); }
		 * 
		 * 
		 * quality = new DistributionQualityAnalysis(); auc = quality.calculateAUC(posRandom, negRandom); System.out.println("AUC: "+auc);
		 * 
		 * for (int i=0;i<n;i++){ posRandom[i] = 1; negRandom[i] = 0; }
		 * 
		 * quality = new DistributionQualityAnalysis(); auc = quality.calculateAUC(posRandom, negRandom); System.out.println("AUC: "+auc);
		 */

		int n = 100;
		double[] posRandom = new double[n];
		double[] negRandom = new double[n];

		for (int i = 0; i < n; i++) {
			posRandom[i] = Math.random();
			negRandom[i] = Math.random();
		}

		DistributionQualityAnalysis quality = new DistributionQualityAnalysis();
		double auc = quality.calculateAUC(posRandom, negRandom, true);

		for (int i = 0; i < n; i++) {
			posRandom[i] = 1;
			negRandom[i] = 0;
		}

		quality = new DistributionQualityAnalysis();
		auc = quality.calculateAUC(posRandom, negRandom, true);
//		System.out.println("AUC: " + auc);

		double[] po = { 0.16, 0.12, 0.12, 0.16, 0.58, 0.36, 0.32, 0.5, 0.65, 0.59, 0.65, 0.65, 0.65, 0.38, 0.18, 0.64, 0.28, 0.64, 0.52, 0.72, 0.74, 0.23, 0.23, 0.23, 0.21, 0.21, 0.22, 0.22, 0.24, 0.32, 0.32, 0.32, 0.32, 0.55, 0.78, 0.37, 0.87, 0.87, 0.87, 0.98, 0.98, 0.76, 0.76, 0.9, 0.88, 0.97, 0.97, 0.97, 1.0, 1.0, 0.45, 0.45, 0.19, 0.89, 0.17, 0.16, 0.1, 0.25, 0.89, 0.89, 0.9, 0.9, 0.87, 1.0, 0.48, 0.88, 0.9, 0.93, 1.0, 1.0, 0.17, 0.87, 1.0, 0.24, 0.86, 0.15, 0.74, 0.32, 1.0, 0.95, 0.52, 0.66, 0.39, 0.31, 0.47, 0.57, 0.73, 0.83, 0.86, 0.98, 0.99, 1.0, 1.0, 1.0, 1.0, 0.86, 0.43, 0.67, 0.66, 0.41, 0.52, 0.46, 0.34, 1.0, 1.0, 1.0, 0.68, 1.0, 0.98, 0.89, 0.79, 1.0, 0.88, 0.99, 1.0, 0.95, 0.95, 0.95, 0.95, 0.88, 0.96, 0.95, 0.96, 0.99, 1.0, 0.98, 0.6, 0.36, 0.15, 0.87, 0.43, 0.86, 0.34, 0.21, 0.41, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.94, 0.98, 0.71, 0.85, 0.49, 0.91, 0.75, 0.74, 0.42, 0.99, 0.43, 0.22, 0.23, 1.0, 1.0, 1.0, 1.0, 0.4, 1.0, 1.0, 1.0, 0.94, 0.95, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.94, 0.98, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.97, 0.11, 0.12, 0.19, 0.2, 0.46, 0.9, 0.84, 0.64, 1.0, 0.77, 0.56, 0.66, 0.17, 0.54, 0.2, 0.27, 0.24, 0.52, 0.74, 0.23, 0.78, 0.69, 0.46, 0.65, 0.18, 0.28, 0.66, 0.66, 0.6, 0.16, 0.24, 0.4, 0.79, 0.69, 0.81, 0.49, 0.29, 0.5, 0.46, 0.15, 0.29, 0.54, 0.29, 0.37, 0.12, 0.24, 0.16, 0.4, 0.24, 0.55, 0.68, 0.6, 0.14, 0.56, 0.17, 0.73, 0.73, 0.43, 0.72, 0.72, 0.49, 0.13, 0.37, 0.11, 0.25, 0.11, 0.74, 0.59, 0.35, 0.67, 0.83, 0.71, 0.48, 0.86, 0.94, 0.17, 0.19, 0.13, 0.27, 0.77, 0.38, 0.47, 0.49, 0.13, 0.27, 0.14, 0.4, 0.45, 0.15, 0.68, 0.37, 0.2, 0.2, 0.63, 0.35, 0.13, 0.17, 0.24, 0.85, 0.58, 0.44, 1.0, 1.0, 0.94, 0.58, 0.28, 0.36, 0.25, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		double[] ne = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

		quality = new DistributionQualityAnalysis();
		auc = quality.calculateAUC(po, ne, true);
		System.out.println("AUC: " + auc);

	}
	
	
	@Override
	public StatisticalType getOutput() {
		PrimitiveType p = new PrimitiveType(Map.class.getName(), PrimitiveType.stringMap2StatisticalMap(output), PrimitiveTypes.MAP, "AnalysisResult","Analysis of the probability distribution quality");
		return p;
	}

	@Override
	public String getDescription() {
		return "An evaluator algorithm that assesses the effectiveness of a distribution model by computing the Receiver Operating Characteristics (ROC), the Area Under Curve (AUC) and the Accuracy of a model";
	}

}
