package org.gcube.dataanalysis.ecoengine.evaluation.bioclimate;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Evaluator;
import org.gcube.dataanalysis.ecoengine.models.cores.aquamaps.Hspen;
import org.gcube.dataanalysis.ecoengine.processing.factories.EvaluatorsFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.ecoengine.utils.Operations;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.hibernate.SessionFactory;
import org.jfree.data.category.DefaultCategoryDataset;

import com.mchange.v1.util.ArrayUtils;

public class BioClimateAnalysis {

	private static String countHighProbabilityCells = "select count(*) from %1$s where probability>%2$s";
	private static String countSeaCells = "select count(*) from %1$s as a join %2$s as b on a.oceanarea>0 and a.csquarecode=b.csquarecode and ((a.iceconann<b.iceconann-%3$s or a.iceconann>b.iceconann+%3$s) or " + "(a.salinitymean<b.salinitymean-%3$s or a.salinitymean>b.salinitymean+%3$s) or (a.sstanmean<b.sstanmean-%3$s or a.sstanmean>b.sstanmean+%3$s))";
	private static String iceLeakage = "select count(*) from %1$s as a join %2$s as b on a.csquarecode=b.csquarecode and (a.iceconann<b.iceconann) and a.oceanarea>0";
	// private static String takeRangeOfDepths = "select distinct depthmin, max(depthmax) from %1$s  group by depthmin order by depthmin";
	private static String takeRangeOfDepths = "select distinct depthmin, depthmax from %1$s order by depthmin";
	private static String takeRangeOfParam = "select %1$s from %2$s where %1$s is not null %3$s order by %1$s";
	private static String countNumberOfSpeciesPerRange = "select count(*) from %1$s where %4$s>%2$s and %4$s<=%3$s ";
	private static String countNumberOfSpecies = "select count(*) from %1$s where depthmin<%2$s and depthmin>=%3$s and depthmax<%4$s and depthmax>=%5$s";
	private static String countProbabilityPerArea = "select count(*) from %1$s as a join hcaf_s as b on b.%2$s = %3$s and a.probability > #THRESHOLD# and a.csquarecode=b.csquarecode";

	public static String salinityDefaultRange = "salinitymin>27.44 and salinitymin<=36.57";
	public static String salinityMinFeature = "salinitymin";

	private static enum FIELD {
		iceconann, sstanmean, salinitymean
	};

	private static String takeAvgSelection = "select avg(%1$s),%2$s from %3$s %4$s group by %2$s order by %2$s";
	private static String[] selectionCriteria = { "faoaream", "lme" };
	private static String[] timeseriesNames = { "Ice Conc.", "Sea Surface Temperature", "Salinity" };
	private static String[] quantitiesNames = { FIELD.iceconann.name(), FIELD.sstanmean.name(), FIELD.salinitymean.name() };
	private static String[] criteriaFilters = { "where faoaream>0", "where lme>0" };
	private static String[] criteriaNames = { "FaoArea", "LME" };

	// private static String takeSubHspec = "select %1$s from #CLAUSE# order by %1$s";

	private static String meanVal = "select avg(%2$s) from %1$s where oceanarea>0";
	// private static String meanVal = "select %2$s from %1$s where csquarecode = '1311:478:4'";

	private String configPath;
	private String temporaryDirectory;

	protected SessionFactory referencedbConnection;

	private String[] csquareTable;
	private String[] finalDistributionTable;

	// hspec
	private int[] highProbabilityCells;
	private double[] discrepancies;
	// hcaf
	private double[] avgIce;
	private double[] avgSST;
	private double[] avgSalinity;

	private ComputationalAgent eval;

	private float status;
	private boolean liveRender;
	boolean doHcafAn;
	boolean doHspecAn;

	private LexicalEngineConfiguration config;

	static int width = 680;
	static int height = 420;
	static int defaultNumOfFeatureClusters = 10;

	public static void main(String[] args) throws Exception {
		String configPath = "./cfg/";
		String databaseUrl = "jdbc:postgresql://localhost/testdb";
		String databaseUser = "gcube";
		String databasePassword = "d4science2";
		BioClimateAnalysis bioClimate = new BioClimateAnalysis(configPath, configPath, databaseUrl, databaseUser, databasePassword, true);
		bioClimate.produceGraphs2D();
	}

	private static String[] SERIES = { "High Probability Cells Trend (>%1$s)", "Number of Changing Cells", "Reducing Ice Concentration Trend", "High Probability Cells Trend Derivative", "Average Trends", "Ice Concentration", "Sea Surface Temperature", "Salinity" };

	public void produceGraphs2D() throws Exception {
		DefaultCategoryDataset testpoints = new DefaultCategoryDataset();
		// double[] points = Operations.parabolicInterpolation(-200d, 200d, 100);
		double[] points = Operations.parabolicInterpolation(0.1926, 0.1727, 20);
		// double[] points = Operations.inverseParabolicInterpolation(-200d, 300d, 50);
		// double[] points = Operations.inverseExponentialInterpolation(29d, 30d, 10);
		for (int i = 0; i < points.length; i++) {
			// System.out.println(points[i]);
			testpoints.addValue(points[i], "Points", "" + i);
		}

		BioClimateGraph lineg9 = new BioClimateGraph("parabols", Operations.getMax(points), Operations.getMin(points));
		lineg9.render(testpoints);
	}

	private HashMap<String, Image> producedImages;

	public HashMap<String, Image> getProducedImages() {
		return producedImages;
	}

	private void produceGraphs(String[] csquareTableNames, String[] hspecTableNames, float threshold) throws Exception {

		csquareTableNames = checkTableNames(csquareTableNames);
		hspecTableNames = checkTableNames(hspecTableNames);

		producedImages = new HashMap<String, Image>();
		int numberOfTrends = highProbabilityCells.length;

		// create the datasets...
		DefaultCategoryDataset probabilityTrend = new DefaultCategoryDataset();
		DefaultCategoryDataset discrepanciesTrend = new DefaultCategoryDataset();

		DefaultCategoryDataset avgIceD = new DefaultCategoryDataset();
		DefaultCategoryDataset avgSSTD = new DefaultCategoryDataset();
		DefaultCategoryDataset avgSalinityD = new DefaultCategoryDataset();

		for (int i = 0; i < numberOfTrends; i++) {
			if (doHcafAn) {
				avgIceD.addValue(avgIce[i], "Ice Conc.", csquareTableNames[i]);
				avgSSTD.addValue(avgSST[i], "SST", csquareTableNames[i]);
				avgSalinityD.addValue(avgSalinity[i], "Salinity", csquareTableNames[i]);
			}
			if (doHspecAn) {
				probabilityTrend.addValue(highProbabilityCells[i], "Number Of Cells", hspecTableNames[i]);
				if (i > 0) {
					discrepanciesTrend.addValue(discrepancies[i], "Mean Discrepancy Respect to Prev. Distrib.", finalDistributionTable[i]);
				}
			}
		}

		if (doHspecAn) {

			double min = Operations.getMin(discrepancies);
			discrepancies[0] = min;

			if (liveRender) {
				BioClimateGraph lineg1 = new BioClimateGraph(String.format(SERIES[0], threshold), Operations.getMax(highProbabilityCells), Operations.getMin(highProbabilityCells));
				BioClimateGraph lineg4 = new BioClimateGraph(SERIES[3], Operations.getMax(discrepancies), min);
				lineg4.render(discrepanciesTrend);
				lineg1.render(probabilityTrend);
			}

			producedImages.put("Probability_Trend", BioClimateGraph.renderStaticImgObject(width, height, probabilityTrend, String.format(SERIES[0], threshold), Operations.getMax(highProbabilityCells), Operations.getMin(highProbabilityCells)));
			producedImages.put("Probability_Discrepancies_Trend", BioClimateGraph.renderStaticImgObject(width, height, discrepanciesTrend, SERIES[3], Operations.getMax(discrepancies), min));

		}
		if (doHcafAn) {

			if (liveRender) {
				BioClimateGraph lineg6 = new BioClimateGraph(SERIES[5], Operations.getMax(avgIce), Operations.getMin(avgIce));
				BioClimateGraph lineg7 = new BioClimateGraph(SERIES[6], Operations.getMax(avgSST), Operations.getMin(avgSST));
				BioClimateGraph lineg8 = new BioClimateGraph(SERIES[7], Operations.getMax(avgSalinity), Operations.getMin(avgSalinity));
				lineg6.render(avgIceD);
				lineg7.render(avgSSTD);
				lineg8.render(avgSalinityD);
			}

			producedImages.put("Average_Ice_Concentration", BioClimateGraph.renderStaticImgObject(width, height, avgIceD, SERIES[5], Operations.getMax(avgIce), Operations.getMin(avgIce)));
			producedImages.put("Average_SST", BioClimateGraph.renderStaticImgObject(width, height, avgSSTD, SERIES[6], Operations.getMax(avgSST), Operations.getMin(avgSST)));
			producedImages.put("Average_Salinity", BioClimateGraph.renderStaticImgObject(width, height, avgSalinityD, SERIES[7], Operations.getMax(avgSalinity), Operations.getMin(avgSalinity)));

		}

		AnalysisLogger.getLogger().trace("Produced All Images");

	}

	public void hcafEvolutionAnalysis(String[] hcafTable, String[] hcafTableNames) throws Exception {
		globalEvolutionAnalysis(hcafTable, null, hcafTableNames, null, null, null, 0f);
	}

	public void hspecEvolutionAnalysis(String[] hspecTables, String[] hspecTableNames, String probabilityColumn, String csquareColumn, float threshold) throws Exception {
		globalEvolutionAnalysis(null, hspecTables, null, hspecTableNames, probabilityColumn, csquareColumn, threshold);
	}

	private String[] checkTableNames(String[] tablesNames) {
		ArrayList<String> newtables = new ArrayList<String>();
		if ((tablesNames == null) || (tablesNames.length == 0))
			return tablesNames;
		for (String table : tablesNames) {
			int i = 1;
			String originalTable = table;
			while (newtables.contains(table)) {
				table = originalTable + "_" + i;
				i++;
			}
			newtables.add(table);
		}
		String[] tables = new String[tablesNames.length];
		for (int j = 0; j < tablesNames.length; j++) {
			tables[j] = newtables.get(j);
		}
		return tables;
	}

	public void produceCharts(HashMap<String, HashMap<String, double[]>> GeoMap, String[] tablesNames) {
		// produce a char for each feature
		tablesNames = checkTableNames(tablesNames);
		producedImages = new HashMap<String, Image>();
		for (String featurename : GeoMap.keySet()) {
			DefaultCategoryDataset chart = new DefaultCategoryDataset();
			HashMap<String, double[]> timeseries = GeoMap.get(featurename);
			double absmax = -Double.MAX_VALUE;
			double absmin = Double.MAX_VALUE;
			for (String timeserie : timeseries.keySet()) {
				double[] points = timeseries.get(timeserie);
				for (int i = 0; i < points.length; i++) {
					if (points[i] > absmax)
						absmax = points[i];
					if (points[i] < absmin)
						absmin = points[i];
					chart.addValue(points[i], timeserie, tablesNames[i]);
				}
			}
			if (liveRender) {
				BioClimateGraph lineg1 = new BioClimateGraph(featurename, absmax, absmin);
				lineg1.render(chart);
			}
			producedImages.put(featurename.replace(" ", "_"), BioClimateGraph.renderStaticImgObject(width, height, chart, featurename, absmax, absmin));
		}
	}

	/**
	 * Generates a chart for hspens in time according to a certain interval in the parameter e.g. : a chart for several salinity intervals
	 * 
	 * @param hspenTables
	 * @param hspenTableNames
	 * @param parameterName
	 * @param condition
	 * @throws Exception
	 */
	public void speciesEvolutionAnalysis(String[] hspenTables, String[] hspenTableNames, String parameterName, String condition) throws Exception {
		try {
			referencedbConnection = DatabaseFactory.initDBConnection(configPath + AlgorithmConfiguration.defaultConnectionFile, config);
			AnalysisLogger.getLogger().debug("ReferenceDB initialized");
			status = 0f;

			int numbOfTables = (hspenTables != null) ? hspenTables.length : 0;

			if (numbOfTables > 0) {

				// a map for each range of features: depth[0,100] ,depth [100,200]
				HashMap<String, HashMap<String, double[]>> GeoMap = new HashMap<String, HashMap<String, double[]>>();

				float statusstep = 80f / (float) numbOfTables;
				if (condition != null && (condition.length() > 0))
					condition = "and " + condition;
				// take the spectrum of depths
				AnalysisLogger.getLogger().trace("Range query: " + String.format(takeRangeOfParam, parameterName, hspenTables[0], condition));

				List<Object> paramrange = DatabaseFactory.executeSQLQuery(String.format(takeRangeOfParam, parameterName, hspenTables[0], condition), referencedbConnection);

				int numberOfParams = paramrange.size();
				double absolutePMin = Double.parseDouble("" + ((Object) paramrange.get(0)));
				double absolutePMax = Double.parseDouble("" + ((Object) paramrange.get(numberOfParams - 1)));
				double step = (absolutePMax - absolutePMin) / (double) defaultNumOfFeatureClusters;
				int pClusters[] = new int[defaultNumOfFeatureClusters + 1];
				pClusters[0] = 0;
				for (int i = 1; i < pClusters.length; i++) {
					double pToFind = absolutePMin + step * (i + 1);
					int k = 0;
					for (Object row : paramrange) {
						if (Double.parseDouble("" + ((Object) row)) > pToFind)
							break;
						k++;
					}
					if (k >= numberOfParams)
						k = numberOfParams - 1;

					pClusters[i] = k;
				}

				// for each table
				for (int i = 0; i < numbOfTables; i++) {
					double pmax = 0;
					// for each cluster build up a chart
					for (int j = 1; j < pClusters.length; j++) {

						double prevpmax = MathFunctions.roundDecimal(Double.parseDouble("" + (Object) paramrange.get(pClusters[j - 1])), 2);
						pmax = MathFunctions.roundDecimal(Double.parseDouble("" + (Object) paramrange.get(pClusters[j])), 2);

						if (prevpmax != pmax) {
							// take the number of elements for this range
							String countSpeciesQuery = String.format(countNumberOfSpeciesPerRange, hspenTables[i], prevpmax, pmax, parameterName);
							AnalysisLogger.getLogger().trace("count elements query: " + countSpeciesQuery);

							List<Object> elementsInRange = DatabaseFactory.executeSQLQuery(countSpeciesQuery, referencedbConnection);
							int nelements = (elementsInRange == null) ? 0 : Integer.parseInt("" + elementsInRange.get(0));

							AnalysisLogger.getLogger().trace("Number of elements for " + hspenTables[i] + " in (" + prevpmax + " - " + pmax + ")" + " : " + nelements);

							// take the chart for this range
							String chartName = parameterName + " envelope for interval (" + prevpmax + " ; " + pmax + ")";
							// build the chart
							HashMap<String, double[]> submap = GeoMap.get(chartName);
							if (submap == null) {
								submap = new HashMap<String, double[]>();
								GeoMap.put(chartName, submap);
							}

							String timeseries = "number of species";
							double[] elements = submap.get(timeseries);
							if (elements == null) {
								elements = new double[numbOfTables];
								submap.put(timeseries, elements);
							}
							elements[i] = nelements;
						}
					}

					status = status + statusstep;
				}

				status = 80f;
				produceCharts(GeoMap, hspenTableNames);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			status = 100f;
			try{
			referencedbConnection.close();
			}catch(Exception e){}
		}
	}

	/**
	 * Generates a chart for hspec probability > thr in each Fao Area and LME
	 * 
	 * @param hspecTables
	 * @param hspecTablesNames
	 * @throws Exception
	 */

	public void speciesGeographicEvolutionAnalysis2(String[] hspecTables, String[] hspecTablesNames, float threshold) throws Exception {
		try {
			referencedbConnection = DatabaseFactory.initDBConnection(configPath + AlgorithmConfiguration.defaultConnectionFile, config);
			AnalysisLogger.getLogger().debug("ReferenceDB initialized");
			status = 0f;

			int numbOfTables = (hspecTables != null) ? hspecTables.length : 0;

			if (numbOfTables > 0) {

				// a map for each feature. each sub map contains a trend for faoaream, lme etc.
				HashMap<String, HashMap<String, double[]>> GeoMap = new HashMap<String, HashMap<String, double[]>>();

				float statusstep = 80f / (float) numbOfTables;
				// for each table
				for (int i = 0; i < numbOfTables; i++) {
					// for each criterion to apply: fao area, lme etc.
					for (int j = 0; j < criteriaNames.length; j++) {
						List<Object> listCriterion = DatabaseFactory.executeSQLQuery(DatabaseUtils.getDinstictElements("hcaf_s", selectionCriteria[j], criteriaFilters[j]), referencedbConnection);
						for (Object code : listCriterion) {
							String code$ = "" + code;
							String query = String.format(countProbabilityPerArea, hspecTables[i], selectionCriteria[j], code$);
							query = query.replace("#THRESHOLD#", "" + threshold);
							AnalysisLogger.getLogger().trace("Executing query for counting probabilities: " + query);
							List<Object> counts = DatabaseFactory.executeSQLQuery(query, referencedbConnection);
							AnalysisLogger.getLogger().trace("Query Executed");
							int countPerArea = (counts == null) ? 0 : Integer.parseInt("" + counts.get(0));

							String chartName = "Hspec (prob>0.8) for " + criteriaNames[j] + "_" + code$; // put the code and the value in the timeseries associated to the feature name
							HashMap<String, double[]> submap = GeoMap.get(chartName);
							if (submap == null) {
								submap = new HashMap<String, double[]>();
								GeoMap.put(chartName, submap);
							}

							String timeseries = "number of occupied cells";
							double[] elements = submap.get(timeseries);
							if (elements == null) {
								elements = new double[numbOfTables];
								submap.put(timeseries, elements);
							}

							elements[i] = countPerArea;

						}

					}
					status = status + statusstep;
				}

				status = 80f;
				produceCharts(GeoMap, hspecTablesNames);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			status = 100f;
			referencedbConnection.close();
		}
	}

	public void speciesGeographicEvolutionAnalysis(String[] hspecTables, String[] hspecTablesNames, float threshold) throws Exception {
		try {
			referencedbConnection = DatabaseFactory.initDBConnection(configPath + AlgorithmConfiguration.defaultConnectionFile, config);
			AnalysisLogger.getLogger().debug("ReferenceDB initialized");
			status = 0f;

			int numbOfTables = (hspecTables != null) ? hspecTables.length : 0;

			if (numbOfTables > 0) {

				// a map for each feature. each sub map contains a trend for faoaream, lme etc.
				HashMap<String, HashMap<String, double[]>> GeoMap = new HashMap<String, HashMap<String, double[]>>();

				float statusstep = 80f / (float) numbOfTables;
				// for each table
				for (int i = 0; i < numbOfTables; i++) {
					String tmpanalysisTable = "tmpanalysis" + ("" + UUID.randomUUID()).replace("-", "").replace("_", "");
					try {
						DatabaseFactory.executeSQLUpdate("drop table " + tmpanalysisTable, referencedbConnection);
					} catch (Exception ee) {
						AnalysisLogger.getLogger().trace("table " + tmpanalysisTable + " does not exist");
					}
					String preparationQuery = "create table " + tmpanalysisTable + " as select a.faoaream, lme,count(*) from %1$s as a where a.probability > #THRESHOLD# group by faoaream,lme;";
					preparationQuery = String.format(preparationQuery, hspecTables[i]);
					preparationQuery = preparationQuery.replace("#THRESHOLD#", "" + threshold);
					AnalysisLogger.getLogger().trace("Executing query for counting probabilities: " + preparationQuery);

					DatabaseFactory.executeSQLUpdate(preparationQuery, referencedbConnection);

					AnalysisLogger.getLogger().trace("Query Executed");

					// for each criterion to apply: fao area, lme etc.
					for (int j = 0; j < criteriaNames.length; j++) {
						String criteriaQuery = String.format("select %1$s,sum(count) from " + tmpanalysisTable + " %2$s group by %1$s;", selectionCriteria[j], criteriaFilters[j]);
						AnalysisLogger.getLogger().trace("Executing query for counting probabilities: " + criteriaQuery);
						List<Object> codeSums = DatabaseFactory.executeSQLQuery(criteriaQuery, referencedbConnection);
						for (Object codeSum : codeSums) {
							String code$ = "" + ((Object[]) codeSum)[0];
							int countPerArea = (((Object[]) codeSum)[1] == null) ? 0 : Integer.parseInt("" + ((Object[]) codeSum)[1]);
							AnalysisLogger.getLogger().trace("Analyzing " + selectionCriteria[j] + " with code " + code$ + " count " + countPerArea);

							String chartName = "Hspec (prob>0.8) for " + criteriaNames[j] + "_" + code$;
							// put the code and the value in the timeseries associated to the feature name
							HashMap<String, double[]> submap = GeoMap.get(chartName);
							if (submap == null) {
								submap = new HashMap<String, double[]>();
								GeoMap.put(chartName, submap);
							}
							String timeseries = "number of occupied cells";
							double[] elements = submap.get(timeseries);
							if (elements == null) {
								elements = new double[numbOfTables];
								submap.put(timeseries, elements);
							}

							elements[i] = countPerArea;

						}

					}
					
					try {
						DatabaseFactory.executeSQLUpdate("drop table " + tmpanalysisTable, referencedbConnection);
					} catch (Exception ee) {
						ee.printStackTrace();
						AnalysisLogger.getLogger().trace("table " + tmpanalysisTable + " does not exist");
					}
					
					status = status + statusstep;
				}

				status = 80f;
				produceCharts(GeoMap, hspecTablesNames);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			status = 100f;
			referencedbConnection.close();
		}
	}

	/**
	 * Generates a geographic trend for each hspec feature: ice con, salinity, sst in each fao area
	 * 
	 * @param hcafTable
	 * @param hcafTableNames
	 * @throws Exception
	 */
	public void geographicEvolutionAnalysis(String[] hcafTable, String[] hcafTableNames) throws Exception {
		try {
			referencedbConnection = DatabaseFactory.initDBConnection(configPath + AlgorithmConfiguration.defaultConnectionFile, config);
			AnalysisLogger.getLogger().debug("ReferenceDB initialized");
			doHcafAn = true;
			doHspecAn = true;
			status = 0f;
			this.csquareTable = hcafTable;

			int numbOfTables = (hcafTable != null) ? hcafTable.length : 0;

			if (numbOfTables > 0) {

				// a map for each feature. each sub map contains a trend for faoaream, lme etc.
				HashMap<String, HashMap<String, double[]>> GeoMap = new HashMap<String, HashMap<String, double[]>>();

				float statusstep = 80f / (float) numbOfTables;
				// for each table
				for (int i = 0; i < numbOfTables; i++) {
					// for each criterion to apply: fao area, lme etc.
					for (int j = 0; j < criteriaNames.length; j++) {
						// for each quantity to display: ice concentration
						for (int k = 0; k < quantitiesNames.length; k++) {
							String query = String.format(takeAvgSelection, quantitiesNames[k], selectionCriteria[j], hcafTable[i], criteriaFilters[j]);
							AnalysisLogger.getLogger().debug("Query to be executed : " + query);
							// take couples (avg,code)
							List<Object> quantityCriterion = DatabaseFactory.executeSQLQuery(query, referencedbConnection);
							// for each row
							for (Object element : quantityCriterion) {
								Object[] row = (Object[]) element;
								// take avg value
								double value = (row[0] == null) ? 0 : Double.parseDouble("" + row[0]);
								// take code for criterion
								String code = "" + row[1];

								String chartName = timeseriesNames[k] + " for " + criteriaNames[j] + "_" + code;
								// put the code and the value in the timeseries associated to the feature name
								HashMap<String, double[]> submap = GeoMap.get(chartName);
								if (submap == null) {
									submap = new HashMap<String, double[]>();
									GeoMap.put(chartName, submap);
								}

								String timeseries = criteriaNames[j] + "_" + code;
								double[] elements = submap.get(timeseries);
								if (elements == null) {
									elements = new double[numbOfTables];
									submap.put(timeseries, elements);
								}
								elements[i] = value;
							}
						}
					}
					status = status + statusstep;
				}

				status = 80f;
				produceCharts(GeoMap, hcafTableNames);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			status = 100f;
			referencedbConnection.close();
		}
	}

	/**
	 * Generates a chart for each hspec feature Generates a chart for hspec prob > thr and performs a discrepancy analysis on hspec
	 * 
	 * @param hcafTable
	 * @param hspecTables
	 * @param hcafTablesNames
	 * @param hspecTableNames
	 * @param probabilityColumn
	 * @param csquareColumn
	 * @throws Exception
	 */
	public void globalEvolutionAnalysis(String[] hcafTable, String[] hspecTables, String[] hcafTablesNames, String[] hspecTableNames, String probabilityColumn, String csquareColumn, float threshold) throws Exception {
		try {
			referencedbConnection = DatabaseFactory.initDBConnection(configPath + AlgorithmConfiguration.defaultConnectionFile, config);
			AnalysisLogger.getLogger().debug("ReferenceDB initialized");
			doHcafAn = true;
			doHspecAn = true;

			if (hcafTable == null)
				doHcafAn = false;
			if (hspecTables == null)
				doHspecAn = false;

			status = 0f;
			this.csquareTable = hcafTable;
			this.finalDistributionTable = hspecTables;

			int numbOfPoints = (hcafTable != null) ? hcafTable.length : hspecTables.length;

			highProbabilityCells = new int[numbOfPoints];
			discrepancies = new double[numbOfPoints];
			avgIce = new double[numbOfPoints];
			avgSST = new double[numbOfPoints];
			avgSalinity = new double[numbOfPoints];

			float statusstep = 80f / (float) numbOfPoints;
//			 create temp table puppa as select count(*), probability > 0.8 as aboveThreshold, sum(probability) as partialprobability from hspec_2050_suitable group by probability >0.8; 
//			select count(*), probability > 0.8 as aboveThreshold, sum(probability) as partialprobability from hspec_2050_suitable group by probability >0.8;
//			select sum(count) as count, 3 as x from puppa union select count,2 as x from puppa where abovethreshold = true union select sum(partialprobability) as count, 1 as x from puppa order by x desc;
			
			for (int i = 0; i < numbOfPoints; i++) {
				if (doHspecAn)
					highProbabilityCells[i] = calcHighProbabilityCells(hspecTables[i], threshold);

				if (doHcafAn) {
					avgIce[i] = avgValue(hcafTable[i], FIELD.iceconann.name());
					avgSST[i] = avgValue(hcafTable[i], FIELD.sstanmean.name());
					avgSalinity[i] = avgValue(hcafTable[i], FIELD.salinitymean.name());
					AnalysisLogger.getLogger().trace("(" + hcafTable[i] + "): " + " ICE " + avgIce[i] + " SST " + avgSST[i] + " SAL " + avgSalinity[i]);
				}

				if (doHspecAn) {
					if (i == 0) {
						discrepancies[i] = 1.0;
					} else {
						// OLD CALCULATION discrepancies[i] = MathFunctions.roundDecimal(calcDiscrepancy(configPath, temporaryDirectory, hspecTables[i], hspecTables[i - 1], probabilityColumn, csquareColumn, 0.1f), 5);
//						discrepancies[i] = MathFunctions.roundDecimal(calcOverDiscrepancy(configPath, temporaryDirectory, hspecTables[i], hspecTables[i - 1], probabilityColumn, csquareColumn, 0.1f), 5);
						discrepancies[i] = highProbabilityCells[i]-highProbabilityCells[i-1];
					}
					AnalysisLogger.getLogger().trace("(" + hspecTables[i] + "): DISCREPANCY " + discrepancies[i] + " HIGH PROB CELLS " + highProbabilityCells[i]);
				}

				// AnalysisLogger.getLogger().trace("(" + hcafTable[i] + "," + hspecTables[i] + "): HIGH PROB CELLS " + highProbabilityCells[i] + " DISCREPANCY " + discrepancies[i] + " ICE " + avgIce[i] + " SST " + avgSST[i] + " SAL " + avgSalinity[i]);

				status = status + statusstep;
			}
			status = 80f;
			produceGraphs(hcafTablesNames, hspecTableNames, threshold);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			status = 100f;
			try{
			referencedbConnection.close();
			}catch(Exception e2){}
		}
	}

	// init connections
	public BioClimateAnalysis(String configPath, String persistencePath, String databaseURL, String databaseUserName, String databasePassword, boolean liveRender) throws Exception {
		this.configPath = configPath;
		this.temporaryDirectory = persistencePath;
		if (!configPath.endsWith("/"))
			configPath += "/";
		if (!persistencePath.endsWith("/"))
			this.temporaryDirectory += "/";

		AnalysisLogger.setLogger(configPath + AlgorithmConfiguration.defaultLoggerFile);
		config = new LexicalEngineConfiguration();
		config.setDatabaseURL(databaseURL);
		config.setDatabaseUserName(databaseUserName);
		config.setDatabasePassword(databasePassword);

		this.liveRender = liveRender;
	}

	public int calcHighProbabilityCells(String hspec, double probabilty) throws Exception {
		AnalysisLogger.getLogger().trace("Calculating High Prob Cells: "+String.format(countHighProbabilityCells, hspec, probabilty));
		List<Object> countage = DatabaseFactory.executeSQLQuery(String.format(countHighProbabilityCells, hspec, probabilty), referencedbConnection);
		int count = Integer.parseInt("" + countage.get(0));
		AnalysisLogger.getLogger().trace("Calc High Prob Cells: " + count);
		return count;
	}

	public double avgValue(String hcaf1, String field) throws Exception {

		List<Object> countage = DatabaseFactory.executeSQLQuery(String.format(meanVal, hcaf1, field), referencedbConnection);
		double count = 0;
		if (countage != null && countage.size() > 0)
			count = Double.parseDouble("" + countage.get(0));

		return count;
	}

	public int countIceLeakageCells(String hcaf1, String hcaf2) throws Exception {

		List<Object> countage = DatabaseFactory.executeSQLQuery(String.format(iceLeakage, hcaf1, hcaf2), referencedbConnection);
		int count = Integer.parseInt("" + countage.get(0));
		return count;
	}

	public int countSeaCells(String hcaf1, String hcaf2, double threshold) throws Exception {
		// System.out.println(String.format(countSeaCells, hcaf1, hcaf2,threshold));
		List<Object> countage = DatabaseFactory.executeSQLQuery(String.format(countSeaCells, hcaf1, hcaf2, threshold), referencedbConnection);
		int count = Integer.parseInt("" + countage.get(0));
		return count;
	}

	public float getStatus() {
		return status;
	}

	public double calcOverDiscrepancy(String configPath, String persistencePath, String firstTable, String secondTable, String probabilityColumnName, String csquareColumn, float comparisonThreshold) throws Exception {

		List<Object> nelementsQ1 = DatabaseFactory.executeSQLQuery(DatabaseUtils.countElementsStatement(firstTable), referencedbConnection);
		int nelements = Integer.parseInt("" + nelementsQ1.get(0));
		AnalysisLogger.getLogger().trace("Number Of elements1: " + nelementsQ1);
		List<Object> nelementsQ2 = DatabaseFactory.executeSQLQuery(DatabaseUtils.countElementsStatement(secondTable), referencedbConnection);
		int nelements2 = Integer.parseInt("" + nelementsQ2.get(0));
		AnalysisLogger.getLogger().trace("Number Of elements2: " + nelementsQ1);

		List<Object> sumFirst = DatabaseFactory.executeSQLQuery(DatabaseUtils.sumElementsStatement(firstTable, probabilityColumnName), referencedbConnection);
		double sum1 = Double.parseDouble("" + sumFirst.get(0));
		AnalysisLogger.getLogger().trace("Sum1: " + sum1);

		List<Object> sumSecond = DatabaseFactory.executeSQLQuery(DatabaseUtils.sumElementsStatement(secondTable, probabilityColumnName), referencedbConnection);
		double sum2 = Double.parseDouble("" + sumSecond.get(0));
		AnalysisLogger.getLogger().trace("Sum2: " + sum1);

		double d = (double) (sum2 - sum1) / (double) (nelements + nelements2);
		return d;
	}

	public double calcDiscrepancy(String configPath, String persistencePath, String firstTable, String secondTable, String probabilityColumnName, String csquareColumn, float comparisonThreshold) throws Exception {

		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath(configPath);
		config.setPersistencePath(persistencePath);
		config.setNumberOfResources(1);
		config.setAgent("DISCREPANCY_ANALYSIS");

		config.setParam("FirstTable", firstTable);
		config.setParam("SecondTable", secondTable);

		config.setParam("FirstTableCsquareColumn", csquareColumn);
		config.setParam("SecondTableCsquareColumn", csquareColumn);

		config.setParam("FirstTableProbabilityColumn", probabilityColumnName);
		config.setParam("SecondTableProbabilityColumn", probabilityColumnName);

		config.setParam("ComparisonThreshold", "" + comparisonThreshold);

		config.setParam("MaxSamples", "" + 30000);

		eval = EvaluatorsFactory.getEvaluators(config).get(0);
		eval.compute();
		PrimitiveType output = (PrimitiveType) eval.getOutput();
		
		HashMap<String, String> out = (HashMap<String, String>)output.getContent();

		Double d = Double.parseDouble(out.get("MEAN"));
		return d;
	}

}
