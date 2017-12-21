package org.gcube.dataanalysis.lexicalmatcher.analysis.core;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.data.Category;

public class LexicalEngineConfiguration {

	public void configure(String absoluteFilePath) throws Exception {
		Properties props = new Properties();
		FileInputStream fis = new FileInputStream(absoluteFilePath);
		props.load(fis);
		setProperties(props);
		fis.close();
	}

	public void configureByStream(String file) throws Exception {
		Properties props = new Properties();
		InputStream is = ClassLoader.getSystemResourceAsStream(file);
		props.load(is);
		setProperties(props);
		is.close();
	}
	
	private void setProperties(Properties props) {
		categoryDiscardThreshold = Float.parseFloat(props.getProperty("categoryDiscardThreshold"));
		entryAcceptanceThreshold = Integer.parseInt(props.getProperty("entryAcceptanceThreshold"));
		chunkSize = Integer.parseInt(props.getProperty("chunkSize"));
		TimeSeriesChunksToTake = Integer.parseInt(props.getProperty("timeSeriesChunksToTake"));
		ReferenceChunksToTake = Integer.parseInt(props.getProperty("referenceChunksToTake"));
		randomTake = Boolean.parseBoolean(props.getProperty("randomTake"));
		useSimpleDistance = Boolean.parseBoolean(props.getProperty("useSimpleDistance"));
		numberOfThreadsToUse = Integer.parseInt(props.getProperty("numberOfThreadsToUse"));
		categoryDiscardDifferencialThreshold = Float.parseFloat(props.getProperty("categoryDiscardDifferencialThreshold"));
		singleEntryRecognitionMaxDeviation = Float.parseFloat(props.getProperty("singleEntryRecognitionMaxDeviation"));
	}

	public void setCategoryDiscardThreshold(float categoryDiscardThreshold) {
		this.categoryDiscardThreshold = categoryDiscardThreshold;
	}

	public float getCategoryDiscardThreshold() {
		return categoryDiscardThreshold;
	}

	public void setEntryAcceptanceThreshold(float entryAcceptanceThreshold) {
		this.entryAcceptanceThreshold = entryAcceptanceThreshold;
	}

	public float getEntryAcceptanceThreshold() {
		return entryAcceptanceThreshold;
	}

	public void setCategoryDiscardDifferencialThreshold(float categoryDiscardDifferencialThreshold) {
		this.categoryDiscardDifferencialThreshold = categoryDiscardDifferencialThreshold;
	}

	public float getCategoryDiscardDifferencialThreshold() {
		return categoryDiscardDifferencialThreshold;
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public int getChunkSize() {
		return chunkSize;
	}

	public void setRandomTake(boolean randomTake) {
		this.randomTake = randomTake;
	}

	public boolean isRandomTake() {
		return randomTake;
	}

	public void setTimeSeriesChunksToTake(int timeSeriesChunksToTake) {
		TimeSeriesChunksToTake = timeSeriesChunksToTake;
	}

	public int getTimeSeriesChunksToTake() {
		return TimeSeriesChunksToTake;
	}

	public void setReferenceChunksToTake(int referenceChunksToTake) {
		ReferenceChunksToTake = referenceChunksToTake;
	}

	public int getReferenceChunksToTake() {
		return ReferenceChunksToTake;
	}

	public void setUseSimpleDistance(boolean useSimpleDistance) {
		this.useSimpleDistance = useSimpleDistance;
	}

	public boolean isUseSimpleDistance() {
		return useSimpleDistance;
	}

	public void setNumberOfThreadsToUse(int numberOfThreadsToUse) {
		this.numberOfThreadsToUse = numberOfThreadsToUse;
	}

	public int getNumberOfThreadsToUse() {
		return numberOfThreadsToUse;
	}

	public void setSingleEntryRecognitionMaxDeviation(float singleEntryRecognitionMaxDeviation) {
		this.singleEntryRecognitionMaxDeviation = singleEntryRecognitionMaxDeviation;
	}

	public float getSingleEntryRecognitionMaxDeviation() {
		return singleEntryRecognitionMaxDeviation;
	}

	public float categoryDiscardThreshold = -Float.MIN_VALUE;
	public float entryAcceptanceThreshold = -Float.MIN_VALUE;
	public float categoryDiscardDifferencialThreshold = -Float.MIN_VALUE;
	public float singleEntryRecognitionMaxDeviation = -Float.MIN_VALUE;
	public int chunkSize = -Integer.MIN_VALUE;
	public Boolean randomTake = null;
	// if set to -1 all chunks will be analyzed
	public int TimeSeriesChunksToTake = -Integer.MIN_VALUE;
	public int ReferenceChunksToTake = -Integer.MIN_VALUE;
	public Boolean useSimpleDistance = null;
	public int numberOfThreadsToUse = -Integer.MIN_VALUE;

	// database parameters
	public String databaseDriver = null;
	public String databaseURL = null;
	public String databaseUserName = null;
	public String databasePassword = null;
	public String databaseDialect = null;
	public String databaseIdleConnectionTestPeriod = null;
	public String databaseAutomaticTestTable = null;

	// reference data parameters
	public String referenceTable = null;
	public String referenceColumn = null;
	public String idColumn = null;
	public String nameHuman = null;
	public String description = null;
	public ArrayList<Category> categories = null;

	public void setCategories(ArrayList<Category> categories) {
		this.categories = categories;
	}

	public ArrayList<Category> getCategories() {
		return this.categories;
	}

	public void mergeConfig(LexicalEngineConfiguration config) {

		if (config.getCategoryDiscardDifferencialThreshold() != -Float.MIN_VALUE)
			setCategoryDiscardDifferencialThreshold(config.getCategoryDiscardDifferencialThreshold());
		if (config.getSingleEntryRecognitionMaxDeviation() != -Float.MIN_VALUE)
			setSingleEntryRecognitionMaxDeviation(config.getSingleEntryRecognitionMaxDeviation());
		if (config.getCategoryDiscardThreshold() != -Float.MIN_VALUE)
			setCategoryDiscardThreshold(config.getCategoryDiscardThreshold());
		if (config.getChunkSize() != -Integer.MIN_VALUE)
			setChunkSize(config.getChunkSize());
		if (config.getEntryAcceptanceThreshold() != -Float.MIN_VALUE)
			setEntryAcceptanceThreshold(config.getEntryAcceptanceThreshold());
		if (config.getNumberOfThreadsToUse() != -Integer.MIN_VALUE)
			setNumberOfThreadsToUse(config.getNumberOfThreadsToUse());
		if (config.getReferenceChunksToTake() != -Integer.MIN_VALUE)
			setReferenceChunksToTake(config.getReferenceChunksToTake());
		if (config.getTimeSeriesChunksToTake() != -Integer.MIN_VALUE)
			setTimeSeriesChunksToTake(config.getTimeSeriesChunksToTake());
		if (config.randomTake != null)
			setRandomTake(config.isRandomTake());
		if (config.useSimpleDistance != null)
			setUseSimpleDistance(config.isUseSimpleDistance());
		// database information merge
		if (config.databaseDriver != null)
			setDatabaseDriver(config.databaseDriver);
		if (config.databaseDialect != null)
			setDatabaseDialect(config.databaseDialect);
		if (config.databaseAutomaticTestTable != null)
			setDatabaseAutomaticTestTable(config.databaseAutomaticTestTable);
		if (config.databaseIdleConnectionTestPeriod != null)
			setDatabaseIdleConnectionTestPeriod(config.databaseIdleConnectionTestPeriod);
		if (config.databaseUserName != null)
			setDatabaseUserName(config.databaseUserName);
		if (config.databasePassword != null)
			setDatabasePassword(config.databasePassword);
		if (config.databaseURL != null)
			setDatabaseURL(config.databaseURL);
		if (config.referenceTable != null)
			setReferenceTable(config.referenceTable);
		if (config.referenceColumn != null)
			setReferenceColumn(config.referenceColumn);
		if (config.idColumn != null)
			setIdColumn(config.idColumn);
		if (config.nameHuman != null)
			setNameHuman(config.nameHuman);
		if (config.description != null)
			setDescription(config.description);
	}

	public void setDatabaseDriver(String databaseDriver) {
		this.databaseDriver = databaseDriver;
	}

	public String getDatabaseDriver() {
		return databaseDriver;
	}

	public void setDatabaseURL(String databaseURL) {
		this.databaseURL = databaseURL;
	}

	public String getDatabaseURL() {
		return databaseURL;
	}

	public void setDatabaseUserName(String databaseUserName) {
		this.databaseUserName = databaseUserName;
	}

	public String getDatabaseUserName() {
		return databaseUserName;
	}

	public void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}

	public String getDatabasePassword() {
		return databasePassword;
	}

	public void setDatabaseDialect(String databaseDialect) {
		this.databaseDialect = databaseDialect;
	}

	public String getDatabaseDialect() {
		return databaseDialect;
	}

	public void setDatabaseIdleConnectionTestPeriod(String databaseIdleConnectionTestPeriod) {
		this.databaseIdleConnectionTestPeriod = databaseIdleConnectionTestPeriod;
	}

	public String getDatabaseIdleConnectionTestPeriod() {
		return databaseIdleConnectionTestPeriod;
	}

	public void setDatabaseAutomaticTestTable(String databaseAutomaticTestTable) {
		this.databaseAutomaticTestTable = databaseAutomaticTestTable;
	}

	public String getDatabaseAutomaticTestTable() {
		return databaseAutomaticTestTable;
	}

	public String getReferenceTable() {
		return referenceTable;
	}

	public void setReferenceTable(String referenceTable) {
		this.referenceTable = referenceTable;
	}

	public String getReferenceColumn() {
		return referenceColumn;
	}

	public void setReferenceColumn(String referenceColumn) {
		this.referenceColumn = referenceColumn;
	}

	public String getIdColumn() {
		return idColumn;
	}

	public void setIdColumn(String idColumn) {
		this.idColumn = idColumn;
	}

	public String getNameHuman() {
		return nameHuman;
	}

	public void setNameHuman(String nameHuman) {
		this.nameHuman = nameHuman;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
