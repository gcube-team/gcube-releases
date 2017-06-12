package org.gcube.dataanalysis.lexicalmatcher.analysis.core;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;


public class EngineConfiguration extends LexicalEngineConfiguration{

	public void configure(String absoluteFilePath){
		Properties props = new Properties();
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(absoluteFilePath);
			props.load(fis);
		}catch(Exception e){
			e.printStackTrace();
		}
		finally
		{
			try{
				fis.close();
			}catch(Exception e){}
		}
	}
	
	//database parameters
	private String databaseDriver = "org.postgresql.Driver";
	private String databaseURL = null;
	private String databaseUserName = null;
	private String databasePassword = null;
	private String databaseDialect = null;
	private String databaseIdleConnectionTestPeriod = null;
	private String databaseAutomaticTestTable = null;
	//Algorithm Parameters
	private String configPath;
	private String cachePath;
	private String distributionTable;
	private Boolean createTable = false;
	private Boolean nativeGeneration = false;
	private Boolean type2050 = false;
	private Boolean useDB = true;
	private Boolean writeSummaryLog = false;
	private String hcafTable;
	private String hspenTable;
	private String originHspenTable;
	private String maxminLatTable;
	private String speciesID;
	private String csquareCode;
	private String occurrenceCellsTable;
	private String remoteCalculatorEndpoint;
	private String serviceUserName;
	private String remoteEnvironment;
	private Integer numberOfThreads;

	private String tableStore;
	private HashMap<String,String> generalProperties;
	
	
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



	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}



	public String getConfigPath() {
		return configPath;
	}



	public void setDistributionTable(String distributionTable) {
		this.distributionTable = distributionTable;
	}



	public String getDistributionTable() {
		return distributionTable;
	}



	public void setHcafTable(String hcafTable) {
		this.hcafTable = hcafTable;
	}



	public String getHcafTable() {
		return hcafTable;
	}



	public void setCreateTable(Boolean createTable) {
		this.createTable = createTable;
	}



	public Boolean createTable() {
		return createTable;
	}



	public void setNativeGeneration(Boolean nativeGeneration) {
		this.nativeGeneration = nativeGeneration;
	}



	public Boolean isNativeGeneration() {
		return nativeGeneration;
	}



	public void setType2050(Boolean type2050) {
		this.type2050 = type2050;
	}



	public Boolean isType2050() {
		return type2050;
	}



	public void setSpeciesID(String speciesID) {
		this.speciesID = speciesID;
	}



	public String getSpeciesID() {
		return speciesID;
	}



	public void setCsquareCode(String csquareCode) {
		this.csquareCode = csquareCode;
	}



	public String getCsquareCode() {
		return csquareCode;
	}



	public void setNumberOfThreads(Integer numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}



	public Integer getNumberOfThreads() {
		return numberOfThreads;
	}



	public void setHspenTable(String hspenTable) {
		this.hspenTable = hspenTable;
	}



	public String getHspenTable() {
		return hspenTable;
	}



	public void setUseDB(Boolean writeOnDB) {
		this.useDB = writeOnDB;
	}



	public Boolean useDB() {
		return useDB;
	}



	public void setOccurrenceCellsTable(String occurrenceCellsTable) {
		this.occurrenceCellsTable = occurrenceCellsTable;
	}



	public String getOccurrenceCellsTable() {
		return occurrenceCellsTable;
	}



	public void setOriginHspenTable(String originHspenTable) {
		this.originHspenTable = originHspenTable;
	}



	public String getOriginHspenTable() {
		return originHspenTable;
	}



	public void setRemoteCalculator(String remoteCalculator) {
		this.remoteCalculatorEndpoint = remoteCalculator;
	}



	public String getRemoteCalculator() {
		return remoteCalculatorEndpoint;
	}



	public void setServiceUserName(String serviceUserName) {
		this.serviceUserName = serviceUserName;
	}



	public String getServiceUserName() {
		return serviceUserName;
	}



	public void setCachePath(String cachePath) {
		this.cachePath = cachePath;
	}



	public String getCachePath() {
		return cachePath;
	}



	public void setWriteSummaryLog(Boolean writeSummaryLog) {
		this.writeSummaryLog = writeSummaryLog;
	}



	public Boolean getWriteSummaryLog() {
		return writeSummaryLog;
	}



	public void setMaxminLatTable(String maxminLatTable) {
		this.maxminLatTable = maxminLatTable;
	}



	public String getMaxminLatTable() {
		return maxminLatTable;
	}



	public void setGeneralProperties(HashMap<String,String> generalProperties) {
		this.generalProperties = generalProperties;
	}



	public HashMap<String,String> getGeneralProperties() {
		return generalProperties;
	}



	public void setRemoteEnvironment(String remoteEnvironment) {
		this.remoteEnvironment = remoteEnvironment;
	}



	public String getRemoteEnvironment() {
		return remoteEnvironment;
	}



	public String getTableStore() {
		return tableStore;
	}



	public void setTableStore(String tableStore) {
		this.tableStore = tableStore;
	}



	
	
}
