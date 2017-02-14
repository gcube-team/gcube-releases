package org.gcube.contentmanagement.lexicalmatcher.analysis.run;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.Engine;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.data.CategoryOrderedList;
import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.data.DBObjectTranslator;
import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.data.SingleResult;
import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.data.TSObjectTransformer;
import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.interfaces.Reference;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.hibernate.SessionFactory;

public class CategoryGuesser {

	/**
	 * @param args
	 * @throws Exception
	 */

	private final static int MAXRESULTS = 10;

	public static void showResults(ArrayList<SingleResult> results) {

		AnalysisLogger.getLogger().warn("CLASSIFICATION RESULT:\n");
		int i = 1;
		for (SingleResult result : results) {
			if (result.getColumn() != null)
				AnalysisLogger.getLogger().warn(i + ": " + result.getCategory() + " - " + result.getColumn() + " ; SCORE: " + result.getStringScore() + "%");
			else
				AnalysisLogger.getLogger().warn(i + ": " + result.getCategory() + " ; SCORE: " + result.getStringScore() + "%");

			i++;
		}

	}

	public static void AccuracyCalc(CategoryGuesser guesser, String configPath, String seriesName, String column, int attempts, String correctFamily, String correctColumn) throws Exception {
		AccuracyCalc(null, guesser, configPath, seriesName, column, attempts, correctFamily, correctColumn);
	}

	public static void AccuracyCalc(LexicalEngineConfiguration externalcfg, CategoryGuesser guesser, String configPath, String seriesName, String column, int attempts, String correctFamily, String correctColumn) throws Exception {

		int familyscore = 0;
		int columnscore = 0;
		// CategoryGuesser guesser = new CategoryGuesser();

		for (int i = 0; i < attempts; i++) {

			guesser.runGuesser(seriesName, column, externalcfg);
			ArrayList<SingleResult> results = guesser.getClassification();
			String result = results.toString();
			showResults(results);

			AnalysisLogger.getLogger().info("CLASSIFICATION RESULT " + result + " " + CategoryGuesser.resultString(result, correctFamily, correctColumn));

			if (CategoryGuesser.CheckCompleteResult(result, correctFamily, correctColumn))
				columnscore++;

			if (CategoryGuesser.CheckFamilyResult(result, correctFamily))
				familyscore++;

		}

		double percColumn = ((double) columnscore / (double) attempts) * 100;
		double percFamily = ((double) familyscore / (double) attempts) * 100;

		AnalysisLogger.getLogger().info("->ACCURACY ON FAMILY " + correctFamily + ":" + percFamily + " ACCURACY ON COLUMN " + correctColumn + ":" + percColumn);
	}

	public static String resultString(String result, String family, String column) {

		result = result.toUpperCase();
		family = family.toUpperCase();
		column = column.toUpperCase();

		return "FAMILY REC: " + result.contains(family) + " COLUMN REC: " + result.contains(family + "=" + column);
	}

	public static boolean CheckCompleteResult(String result, String family, String column) {

		result = result.toUpperCase();
		family = family.toUpperCase();
		column = column.toUpperCase();
		if (result.contains(family + "=" + column))
			return true;
		else
			return false;
	}

	public static boolean CheckFamilyResult(String result, String family) {

		result = result.toUpperCase();
		family = family.toUpperCase();

		if (result.contains(family + "="))
			return true;
		else
			return false;
	}

	// NOTE: The config path has to contain the two files: lexicalGuesser.properties and ALog.properties
	private static final String cfgFile = "lexicalGuesser.properties";
	private static final String LogFile = "ALog.properties";
	// singleton
	private CategoryOrderedList col;
	private Engine processor;
	private CategoryOrderedList originalCol;
	private LexicalEngineConfiguration config;
	private String configPath;
	private boolean oneshotMode;
	private static final int maxTriesClassification = 3;
	private int triesCounter;

	public CategoryGuesser(String ConfigPath) {

		triesCounter = 0;
		this.configPath = ConfigPath;
	}

	public CategoryGuesser() {
		triesCounter = 0;
		this.configPath = ".";
	}

	public void runGuesser(String seriesName, String columnName, LexicalEngineConfiguration externalConfig) throws Exception {
		runGuesser(seriesName, columnName, externalConfig, null, null);
	}

	public void runGuesser(String seriesName, String columnName) throws Exception {
		runGuesser(seriesName, columnName, null, null, null);
	}

	public void runGuesser(String seriesName, String columnName, LexicalEngineConfiguration externalConfig, String CategoryFilter, String ColumnFilter) throws Exception {
		runGuesser(seriesName, columnName, externalConfig, CategoryFilter, ColumnFilter, null);
	}

	public void runGuesser(String SingletonString, LexicalEngineConfiguration externalConfig, String CategoryFilter, String ColumnFilter) throws Exception {
		oneshotMode = true;
		runGuesser(null, null, externalConfig, CategoryFilter, ColumnFilter, SingletonString);
	}

	public void init(String categoryFilter, String columnFilter, LexicalEngineConfiguration externalConfig) throws Exception {

		String cfgFileCompletePath = configPath + "/" + cfgFile;
		AnalysisLogger.setLogger(configPath + "/" + LogFile);

		AnalysisLogger.getLogger().trace("******************INITIALIZING******************");

		config = new LexicalEngineConfiguration();
		config.configure(cfgFileCompletePath);

		if (externalConfig != null) {
			config.mergeConfig(externalConfig);
		}

		processor = new Engine(config, columnFilter, configPath);

		SessionFactory dbSession = processor.getDBSession(config);
		DBObjectTranslator dbo = new DBObjectTranslator();

		if (col == null) {
			AnalysisLogger.getLogger().trace("******************Order Category******************");
			if (externalConfig == null)
				externalConfig = new LexicalEngineConfiguration();
			dbo.buildCategoriesStructure(dbSession, externalConfig.getReferenceTable(), externalConfig.getReferenceColumn(), externalConfig.getIdColumn(), externalConfig.getNameHuman(), externalConfig.getDescription());
			col = TSObjectTransformer.transform2List(dbo, config, categoryFilter);
			AnalysisLogger.getLogger().trace("***************End Ordering********************");
			originalCol = col.generateNovelList();
		} else {
			col = originalCol.generateNovelList();
		}

		oneshotMode = false;
	}

	public void initSingleMatcher(LexicalEngineConfiguration externalConfig, String ColumnFilter) throws Exception {

		String cfgFileCompletePath = configPath + "/" + cfgFile;
		AnalysisLogger.setLogger(configPath + "/" + LogFile);

		config = new LexicalEngineConfiguration();
		config.configure(cfgFileCompletePath);

		if (externalConfig != null) {
			config.mergeConfig(externalConfig);
		}

		processor = new Engine(config, ColumnFilter, configPath);

		// in this case, the lexical matcher is invoked once, then it has to be stopped in the end
		oneshotMode = true;
	}

	public void init(String categoryFilter, String columnFilter) throws Exception {
		init(categoryFilter, columnFilter, null);
	}

	public void init(LexicalEngineConfiguration externalConfig) throws Exception {
		init(null, null, externalConfig);
	}

	public void init() throws Exception {
		init(null, null, null);
	}

	public void refreshReferences() {
		col = null;
	}

	public void runGuesser(String seriesName, String columnName, LexicalEngineConfiguration externalConfig, String CategoryFilter, String ColumnFilter, String SingletonString) throws Exception {

		String cfgFileCompletePath = configPath + "/" + cfgFile;
		AnalysisLogger.setLogger(configPath + "/" + LogFile);

		AnalysisLogger.getLogger().debug("Guessing Table " + seriesName + " column " + columnName);
		if (externalConfig != null) {
			config = new LexicalEngineConfiguration();
			config.configure(cfgFileCompletePath);
			config.mergeConfig(externalConfig);

			// NOTE FOR FUTURE OPTIMIZATION: perform the re-init only if there is a change in the Database pointing
			processor = new Engine(config, ColumnFilter, configPath);
		} else {
			if (config == null) {
				config = new LexicalEngineConfiguration();
				config.configure(cfgFileCompletePath);

			}
			if (processor == null) {
				processor = new Engine(config, ColumnFilter, configPath);
			} else
				processor.resetEngine(config, ColumnFilter, configPath);
		}

		SessionFactory dbSession = processor.getDBSession(config);
		DBObjectTranslator dbo = new DBObjectTranslator();
		
		//modification of 10/10/11 calculate structure each time
//		if (col == null) {
			AnalysisLogger.getLogger().trace("******************Order Category******************");
			dbo.buildCategoriesStructure(dbSession, config.getReferenceTable(), config.getReferenceColumn(), config.getIdColumn(), config.getNameHuman(), config.getDescription());
			col = TSObjectTransformer.transform2List(dbo, config, CategoryFilter);
			AnalysisLogger.getLogger().trace("***************End Ordering********************");
			originalCol = col.generateNovelList();
			/*
		} else {
			col = originalCol.generateNovelList();
		}
			 */
			
		AnalysisLogger.getLogger().warn("Starting Calculation...wait...");

		long t0 = System.currentTimeMillis();

		// processor.calcLike(col,seriesName, columnName);

		processor.calcLikeThread(col, seriesName, columnName, SingletonString);

		// perform processing until the table contains at least one element
		ArrayList<SingleResult> checkingResults = null;

		// if (oneshotMode)
		// checkingResults = getClassification();
		// else
		checkingResults = getClassification();

		while ((checkingResults == null || checkingResults.size() == 0) && (triesCounter < maxTriesClassification)) {
			AnalysisLogger.getLogger().warn("..another processing pass is required. Attempt number " + (triesCounter + 1));
			triesCounter++;
			float differencialThr = config.getCategoryDiscardDifferencialThreshold();
			float acceptanceThr = config.getEntryAcceptanceThreshold();
			// reduce the thresholds of 10 points and recalculate
			config.setCategoryDiscardDifferencialThreshold(Math.max(differencialThr - 20, 0));
			config.setEntryAcceptanceThreshold(Math.max(acceptanceThr - 20, 0));
			AnalysisLogger.getLogger().trace("Performing next processing pass");
			runGuesser(seriesName, columnName, null, CategoryFilter, ColumnFilter, SingletonString);
			AnalysisLogger.getLogger().debug("End processing pass");

			// if (oneshotMode)
			// checkingResults = getClassification();
			// else
			checkingResults = getClassification();

			if (triesCounter == 0)
				break;
		}

		long t1 = System.currentTimeMillis() - t0;

		AnalysisLogger.getLogger().warn("...End Calculation in " + t1 + "ms");

		triesCounter = 0;
		// close session if not more necessary
		if (oneshotMode)
			dbSession.close();
	}

	public ArrayList<SingleResult> getClassificationOLD() {

		ArrayList<SingleResult> results = new ArrayList<SingleResult>();
		int size = processor.bestCategories.size();
		for (int i = 0; i < size; i++) {
			results.add(new SingleResult(processor.bestCategories.get(i), processor.bestColumns.get(i), processor.bestScores.get(i), null, "0"));
		}

		return results;
	}

	public ArrayList<SingleResult> getDetailedMatches() {

		if (processor.getSingletonMatches() != null) {

			// use deviation to cut results
			float threshold = config.getSingleEntryRecognitionMaxDeviation();
			ArrayList<SingleResult> results = processor.getSingletonMatches();
			double minScore = 0;
			// get the best result and calculate the threshold
			if (results.size() > 0) {
				minScore = results.get(0).getScore() - threshold;
			}

			// remove poor objects
			int size = results.size();
			for (int i = 0; i < size; i++) {
				SingleResult sr = results.get(i);
				if (sr.getScore() < minScore) {
					results.remove(i);
					i--;
					size--;
				}
			}

			return processor.getSingletonMatches();
		} else
			return new ArrayList<SingleResult>();
	}

	public String getDetailedSingletonEntry() {

		if (processor.getSingletonElement() != null) {
			return processor.getSingletonElement();
		} else
			return "";
	}

	public ArrayList<SingleResult> getClassificationPlain() {

		ArrayList<SingleResult> results = new ArrayList<SingleResult>();
		int size = processor.bestCategories.size();
		double maxscore = 0;

		for (int i = 0; i < size; i++) {
			double score = processor.bestScores.get(i);
			if (maxscore < score) {
				maxscore = score;
			}
		}

		for (int i = 0; i < size; i++) {

			double score = processor.bestScores.get(i);
			// normalizing percentages!!!
			score = (score / (maxscore + ((size > 1) ? 1 : 0))) * 100;

			if (score > config.categoryDiscardDifferencialThreshold) {

				Reference ref = col.getCategory(processor.bestCategories.get(i));

				results.add(new SingleResult(processor.bestCategories.get(i), processor.bestColumns.get(i), score, ref.getTableName(), ref.getIndex()));
			}
		}

		return results;
	}

	public ArrayList<SingleResult> getClassification() {

		ArrayList<SingleResult> results = new ArrayList<SingleResult>();
		int size = processor.bestCategories.size();
		double maxscore = 0;

		BigDecimal sumElements = BigDecimal.ZERO;
		ArrayList<Double> subscores = new ArrayList<Double>();

		// calculate sum of elements and weights;
		for (int i = 0; i < size; i++) {
			BigInteger catElements = col.getScoresTable().get(processor.bestCategories.get(i)).getCategoryElements();
			sumElements = sumElements.add(new BigDecimal(catElements));
		}
/*
		if (sumElements.compareTo(BigDecimal.valueOf(10000)) < 0)
			return getClassificationPlain();
*/
		for (int i = 0; i < size; i++) {
			double score = processor.bestScores.get(i);
			// multiply for impotance
			BigInteger catElements = col.getScoresTable().get(processor.bestCategories.get(i)).getCategoryElements();

			// AnalysisLogger.getLogger().warn("\t elements "+catElements+" sum "+sumElements);

			double weight = new BigDecimal(catElements).divide(sumElements, 2, BigDecimal.ROUND_HALF_UP).doubleValue();

			if (weight >= 3)
				weight = 2 * Math.log(weight * 100) / 10f;
			else if ((weight >= 0.5) && (weight <= 1))
			{
				weight = Math.log(weight * 100) / 100.00f;
			}
			else if (weight < 0.05)
				weight = 0.05;

			AnalysisLogger.getLogger().warn("WEIGHT FOR CATEGORY " + processor.bestCategories.get(i) + "-" + processor.bestColumns.get(i) + " : " + weight + " SCORE " + score);

			// recalculate weights
			score = score * weight;
			score = Math.min(1, score);

			if (maxscore < score) {
				maxscore = score;
			}

			subscores.add(score);
		}
		// AnalysisLogger.getLogger().warn("MAX SCORE "+maxscore);

		for (int i = 0; i < size; i++) {

			// double score = processor.bestScores.get(i);
			double score = subscores.get(i);

			// AnalysisLogger.getLogger().warn("SCORE FOR CATEGORY "+processor.bestCategories.get(i)+" -COLUMN : "+processor.bestColumns.get(i)+" - "+score);

			// normalizing percentages!!!
			score = (score / (maxscore + ((size > 1) ? 1 : 0))) * 100;

			// AnalysisLogger.getLogger().warn("SCORE FOR CATEGORY "+processor.bestCategories.get(i)+" -COLUMN : "+processor.bestColumns.get(i)+" - "+score);
			if (score > config.categoryDiscardDifferencialThreshold) {
				// AnalysisLogger.getLogger().warn("SCORE "+score);
				// insert into the right place
				int index = results.size();
				int j = 0;
				for (SingleResult res : results) {
					if (res.getScore() < score) {
						index = j;
					}
					j++;
				}

				Reference ref = col.getCategory(processor.bestCategories.get(i));
				SingleResult sr = new SingleResult(processor.bestCategories.get(i), processor.bestColumns.get(i), score, ref.getTableName(), ref.getIndex());
				//control for repetitions
				if (isnotRepetition(sr, results))
					results.add(index, sr);
			}
		}

		//limit the result list after rescoring
		int s = results.size();
		if (s>MAXRESULTS){
			int diff = (size-MAXRESULTS);
			for (int i=0;i<diff;i++){
				s = results.size();
				results.remove(s-1);
			}
		}
		
		return results;
	}

	private boolean isnotRepetition(SingleResult result, ArrayList<SingleResult> previous) {

		boolean notrepeated = true;
		int size = previous.size();
		for (int i = 0; i < size; i++) {
			SingleResult sr = previous.get(i);
			if (sr.getCategory().equalsIgnoreCase(result.getCategory()) && sr.getColumn().equalsIgnoreCase(result.getColumn())) {
				notrepeated = true;
				break;
			}
		}

		return notrepeated;
	}

}
