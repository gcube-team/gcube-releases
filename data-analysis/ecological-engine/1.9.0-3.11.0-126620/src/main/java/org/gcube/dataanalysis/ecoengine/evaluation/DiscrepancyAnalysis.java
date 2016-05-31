package org.gcube.dataanalysis.ecoengine.evaluation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.DataAnalysis;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;

public class DiscrepancyAnalysis extends DataAnalysis {

//	static String discrepancyQuery = "select distinct a.%1$s as csquareone,b.%2$s as csquaretwo,a.%3$s as firstprob,b.%4$s as secondprob from %5$s as a inner join %6$s as b on a.%1$s=b.%2$s and (a.%3$s<>b.%4$s)";
//	static String discrepancyQuery = "select distinct a.%1$s as csquareone,b.%2$s as csquaretwo,a.%3$s as firstprob,b.%4$s as secondprob from (select * from %5$s order by %1$s limit %7$s) as a inner join (select * from %6$s order by %2$s limit %7$s)  as b on a.%1$s=b.%2$s and (a.%3$s<>b.%4$s)";
	//version 3
	/*
	static String discrepancyQuery = "select * from (select distinct a.%1$s as csquareone,b.%2$s as csquaretwo,a.%3$s as firstprob,b.%4$s as secondprob from " +
			"(select %1$s , avg(%3$s) as %3$s from (select distinct * from %5$s order by %1$s limit %7$s) as aa group by %1$s) as a " +
			"left join " +
			"(select %2$s , avg(%4$s) as %4$s from (select distinct * from %6$s order by %2$s limit %7$s) as aa group by %2$s) as b " +
			"on a.%1$s=b.%2$s) as sel where firstprob<>secondprob";
	*/
	
	static String discrepancyQuery = "select * from (select distinct a.%1$s as csquareone,b.%2$s as csquaretwo,a.%3$s as firstprob,b.%4$s as secondprob from " +
			"(select %1$s , avg(%3$s) as %3$s from (select distinct * from %5$s order by %1$s limit %7$s) as aa group by %1$s) as a " +
			"left join " +
			"(select %2$s , avg(%4$s) as %4$s from (select distinct * from %6$s order by %2$s limit %7$s) as aa group by %2$s) as b " +
			"on a.%1$s=b.%2$s) as sel";
	
	static String getNumberOfElementsQuery = "select count(*) from %1$s";
	private static int minElements = 100;
	private static int maxElements = 30000;
	
	float threshold = 0.1f;
	String configPath = "./cfg/";
	
	List<Float> errors;
	double mean;
	double variance;
	double kthreshold;
	long numberoferrors;
	long numberofvectors;
	long numberofcomparisons;
	float maxerror;
	String maxdiscrepancyPoint;
	long numHigher = 0;
	long numLower = 0;
	long agreementA1B1=0;
	long agreementA0B0=0;
	long agreementA1B0=0;
	long agreementA0B1=0;
	
	private LinkedHashMap<String, String> output;

	@Override
	public List<StatisticalType> getInputParameters() {
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		List<TableTemplates> templates = new ArrayList<TableTemplates>();
		templates.add(TableTemplates.HSPEC);
		templates.add(TableTemplates.TRAININGSET);
		templates.add(TableTemplates.TESTSET);
		InputTable p1 = new InputTable(templates,"FirstTable","First Table");
		InputTable p2 = new InputTable(templates,"SecondTable","Second Table");
		ColumnType p3 = new ColumnType("FirstTable", "FirstTableCsquareColumn", "the csquares column name in the first table", "csquarecode", false);
		ColumnType p4 = new ColumnType("SecondTable", "SecondTableCsquareColumn", "the csquares column name in the second table", "csquarecode", false);
		ColumnType p5 = new ColumnType("FirstTable", "FirstTableProbabilityColumn", "the probability column in the first table", "probability", false);
		ColumnType p13 = new ColumnType("SecondTable", "SecondTableProbabilityColumn", "the probability column in the second table", "probability", false);
		PrimitiveType p6 = new PrimitiveType(Float.class.getName(), null, PrimitiveTypes.NUMBER, "ComparisonThreshold","the comparison threshold","0.1");
		PrimitiveType p7 = new PrimitiveType(Integer.class.getName(), null, PrimitiveTypes.NUMBER, "MaxSamples","the comparison threshold","10000");
		
		PrimitiveType p8 = new PrimitiveType(Float.class.getName(), null, PrimitiveTypes.NUMBER, "KThreshold", "Threshold for K-Statistic: over this threshold values will be considered 1 for agreement calculation. Default is 0.5","0.5");
		
		parameters.add(p1);
		parameters.add(p2);
		parameters.add(p3);
		parameters.add(p4);
		parameters.add(p5);
		parameters.add(p13);
		parameters.add(p6);
		parameters.add(p7);
		parameters.add(p8);
		
		DatabaseType.addDefaultDBPars(parameters);
		return parameters;
	}

	
	@Override
	public LinkedHashMap<String, String> analyze() throws Exception {

		String FirstTableCsquareColumn = config.getParam("FirstTableCsquareColumn");
		String SecondTableCsquareColumn = config.getParam("SecondTableCsquareColumn");
		String FirstTableProbabilityColumn = config.getParam("FirstTableProbabilityColumn");
		String SecondTableProbabilityColumn = config.getParam("SecondTableProbabilityColumn");
		String FirstTable = config.getParam("FirstTable");
		String SecondTable = config.getParam("SecondTable");
		String maxSamples =  config.getParam("MaxSamples");
		
		String kthresholdString =  config.getParam("KThreshold");
		kthreshold = 0.5;
		try{
			kthreshold = Double.parseDouble(kthresholdString);
		}catch(Exception e){}
		
		AnalysisLogger.getLogger().trace("Using Cohen's Kappa Threshold: "+kthreshold);
		
		int maxCompElements = maxElements;
		if (maxSamples!=null && maxSamples.length()>0){
			int maxx = Integer.parseInt(maxSamples);
			maxCompElements = maxx!=0?maxx:Integer.MAX_VALUE;
		}
		
		List<Object> takeNPoints = DatabaseFactory.executeSQLQuery(String.format(getNumberOfElementsQuery, FirstTable), connection);
		List<Object> takeMPoints = DatabaseFactory.executeSQLQuery(String.format(getNumberOfElementsQuery, SecondTable), connection);
		int nPoints = Integer.parseInt(""+takeNPoints.get(0));
		int mPoints = Integer.parseInt(""+takeMPoints.get(0));
		numberofvectors = Math.max(nPoints, mPoints); 
		
		
		if (FirstTable.equals(SecondTable)){
			output = new LinkedHashMap<String, String>();
			output.put("MEAN", "0.0");
			output.put("VARIANCE", "0.0");
			output.put("NUMBER_OF_ERRORS", "0");
			output.put("NUMBER_OF_COMPARISONS", "" + numberofvectors);
			output.put("ACCURACY", "100.0");
			output.put("RELATIVE ERROR", "-");
			output.put("MAXIMUM_ERROR", "0");
			output.put("MAXIMUM_ERROR_POINT", "-");
			output.put("COHENS_KAPPA", "1");
			output.put("COHENS_KAPPA_CLASSIFICATION_LANDIS_KOCH", MathFunctions.kappaClassificationLandisKoch(1));
			output.put("COHENS_KAPPA_CLASSIFICATION_FLEISS", MathFunctions.kappaClassificationFleiss(1));
			output.put("TREND", "STATIONARY");
			
			return output;
		}
		
		AnalysisLogger.getLogger().trace("Number Of Elements to take: "+numberofvectors);
		String query = String.format(discrepancyQuery, FirstTableCsquareColumn, SecondTableCsquareColumn, FirstTableProbabilityColumn, SecondTableProbabilityColumn, FirstTable, SecondTable,""+numberofvectors);
		
		AnalysisLogger.getLogger().debug("Discrepancy Calculation - Query to perform :" + query);
		List<Object> takePoints = DatabaseFactory.executeSQLQuery(query, connection);

		super.processedRecords = 0;
		if (takePoints != null)
			super.processedRecords = takePoints.size();

		threshold = Float.parseFloat(config.getParam("ComparisonThreshold"));
		analyzeCompareList(takePoints);
		calcDiscrepancy();
		
		float accuracy = 100;
		if (processedRecords>0)
			accuracy = (1 - (float) numberoferrors / (float) numberofcomparisons) * 100;
		
		if (maxdiscrepancyPoint==null)
			maxdiscrepancyPoint="-";
		
		AnalysisLogger.getLogger().debug("Discrepancy Calculation - Kappa values: " + "agreementA1B1 "+agreementA1B1 +" agreementA1B0 " + agreementA1B0 +"  agreementA0B1 "+agreementA0B1+" agreementA0B0 "+agreementA0B0);
		double kappa = MathFunctions.cohensKappaForDichotomy(agreementA1B1, agreementA1B0, agreementA0B1, agreementA0B0);
		AnalysisLogger.getLogger().debug("Discrepancy Calculation - Calculated Cohen's Kappa:" + kappa);
		
		output = new LinkedHashMap<String, String>();
		output.put("MEAN", "" + MathFunctions.roundDecimal(mean,2));
		output.put("VARIANCE", "" + MathFunctions.roundDecimal(variance,2));
		output.put("NUMBER_OF_ERRORS", "" + numberoferrors);
		output.put("NUMBER_OF_COMPARISONS", "" + numberofcomparisons);
		output.put("ACCURACY", "" + MathFunctions.roundDecimal(accuracy,2));
		output.put("RELATIVE ERROR", "" + MathFunctions.roundDecimal(mean/maxerror,2));
		output.put("MAXIMUM_ERROR", "" + MathFunctions.roundDecimal(maxerror,2));
		output.put("MAXIMUM_ERROR_POINT", maxdiscrepancyPoint);
		output.put("COHENS_KAPPA", "" + kappa);
		output.put("COHENS_KAPPA_CLASSIFICATION_LANDIS_KOCH", MathFunctions.kappaClassificationLandisKoch(kappa));
		output.put("COHENS_KAPPA_CLASSIFICATION_FLEISS", MathFunctions.kappaClassificationFleiss(kappa));
		
		if (numLower>numHigher)
			output.put("TREND", "CONTRACTION");
		else if (numLower<numHigher)
			output.put("TREND", "EXPANSION");
		else 
			output.put("TREND", "STATIONARY");
		
		return output;
	}


	void calcDiscrepancy() {
		double[] err = new double[errors.size()];
		int i = 0;
		for (Float e : errors) {
			err[i] = e;
			i++;
		}

		mean = 0;
		variance = 0;

		if (err.length > 0) {
			mean = MathFunctions.mean(err);
			variance = com.rapidminer.tools.math.MathFunctions.variance(err, Double.NEGATIVE_INFINITY);
		}
	}

	
	
	public void analyzeCompareList(List<Object> points) {
		errors = new ArrayList<Float>();
		
		if (points != null) {
			maxerror = 0;
			for (Object vector : points) {
				//number of comparison equals to the aggregation
				numberofcomparisons++;
				
				Object[] elements = (Object[]) vector;
				String csquare = (String) elements[0];
				float probabilityPoint1 = 0;
				if (elements[2] != null)
					probabilityPoint1 = Float.parseFloat(""+elements[2]);
				float probabilityPoint2 = 0;
				if (elements[3] != null)
					probabilityPoint2 = Float.parseFloat(""+elements[3]);
				float discrepancy = Math.abs(probabilityPoint2 - probabilityPoint1);

				if (discrepancy > threshold) {
					errors.add(Math.abs(probabilityPoint2 - probabilityPoint1));
					numberoferrors++;
					if (discrepancy > maxerror) {
						maxerror = discrepancy;
						maxdiscrepancyPoint = csquare;
					}
					if (probabilityPoint2>probabilityPoint1)
						numHigher++;
					else if (probabilityPoint2<probabilityPoint1)
						numLower++;
				}
				
				//calculations for Cohen's Kappa
				if ((probabilityPoint1>=kthreshold) && (probabilityPoint2>=kthreshold))
					agreementA1B1++;
				else if ((probabilityPoint1<kthreshold) && (probabilityPoint2<kthreshold))
					agreementA0B0++;
				if ((probabilityPoint1>=kthreshold) && (probabilityPoint2<kthreshold))
					agreementA1B0++;
				if ((probabilityPoint1<kthreshold) && (probabilityPoint2>=kthreshold))
					agreementA0B1++;
			}
		}

	}

		@Override
	public String getDescription() {
		return "An evaluator algorithm that compares two tables containing real valued vectors. It drives the comparison by relying on a geographical distance threshold and a threshold for K-Statistic.";
	}
}
