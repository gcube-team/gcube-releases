package org.gcube.dataanalysis.ecoengine.utils;

import java.util.HashMap;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.hibernate.SessionFactory;

public class TrainingSetsGenerator {

	static String getProbabilititesQuery = "select count(*) as distribprob from %1$s as a join %2$s as b on a.%3$s=b.%4$s and b.%5$s %6$s %7$s";
	static String getNumberOfElementsQuery = "select count(*) from %1$s";

	static String getRandomElements = "select * into %2$s from %1$s order by RANDOM() limit %3$s";
	static String getOtherElements = "select * into %4$s from %1$s where %3$s not in (select %3$s from %2$s)";
	static String dropTable = "drop table %1$s";

	float threshold = 0.1f;
	String configPath = "./cfg/";

	

	private int calculateNumberOfPoints(String table) {

		String numberOfPositiveCasesQuery = String.format(getNumberOfElementsQuery, table);
		List<Object> totalPoints = DatabaseFactory.executeSQLQuery(numberOfPositiveCasesQuery, connection);
		int points = Integer.parseInt("" + totalPoints.get(0));
		return points;
	}

	private void deleteTable(String testTable, String trainingTable) {
		try {
			DatabaseFactory.executeSQLUpdate(String.format(dropTable, testTable), connection);
		} catch (Exception e) {
		}
		try {
			DatabaseFactory.executeSQLUpdate(String.format(dropTable, trainingTable), connection);
		} catch (Exception e) {
		}
	}

	private void generatePointsTable(String table, String key, int numberOfElements) {
		String testTable = table + "_test" + numberOfElements;
		String trainingTable = table + "_training" + numberOfElements;
		deleteTable(testTable, trainingTable);
		
		String testSetQuery = String.format(getRandomElements, table, testTable, numberOfElements);
		AnalysisLogger.getLogger().trace("TestSet Creation Query: "+testSetQuery);
		try {
			DatabaseFactory.executeSQLUpdate(testSetQuery, connection);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String trainingSetQuery = String.format(getOtherElements, table, testTable, key, trainingTable);
		AnalysisLogger.getLogger().trace("TrainingSet Creation Query: "+trainingSetQuery);
		try {
			DatabaseFactory.executeSQLUpdate(trainingSetQuery, connection);
		} catch (Exception e) {
			e.printStackTrace();
		}
		AnalysisLogger.getLogger().trace("DONE!");
	}

	public void generate(AlgorithmConfiguration config) throws Exception {
		init(config);
		int numberOfElements = calculateNumberOfPoints(config.getParam("casesTable"));
		int elementsToTake = (int) (0.4 * (float) numberOfElements);
		generatePointsTable(config.getParam("casesTable"), config.getParam("columnKeyName"), elementsToTake);

	}
	SessionFactory connection;

	public void init(AlgorithmConfiguration config) throws Exception {
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		// init db connection
		connection = AlgorithmConfiguration.getConnectionFromConfig(config);
	}
}
