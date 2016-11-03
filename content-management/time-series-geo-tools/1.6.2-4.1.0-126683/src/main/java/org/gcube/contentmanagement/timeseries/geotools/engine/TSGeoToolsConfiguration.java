package org.gcube.contentmanagement.timeseries.geotools.engine;

public class TSGeoToolsConfiguration {
	
	
	public String getTimeSeriesUserName() {
		return TimeSeriesUserName;
	}
	public void setTimeSeriesUserName(String timeSeriesUserName) {
		TimeSeriesUserName = timeSeriesUserName;
	}
	public String getGeoServerUserName() {
		return GeoServerUserName;
	}
	public void setGeoServerUserName(String geoServerUserName) {
		GeoServerUserName = geoServerUserName;
	}
	public String getAquamapsUserName() {
		return AquamapsUserName;
	}
	public void setAquamapsUserName(String aquamapsUserName) {
		AquamapsUserName = aquamapsUserName;
	}
	public String getTimeSeriesPassword() {
		return TimeSeriesPassword;
	}
	public void setTimeSeriesPassword(String timeSeriesPassword) {
		TimeSeriesPassword = timeSeriesPassword;
	}
	public String getGeoServerPassword() {
		return GeoServerPassword;
	}
	public void setGeoServerPassword(String geoServerPassword) {
		GeoServerPassword = geoServerPassword;
	}
	public String getAquamapsPassword() {
		return AquamapsPassword;
	}
	public void setAquamapsPassword(String aquamapsPassword) {
		AquamapsPassword = aquamapsPassword;
	}
	public String getTimeSeriesDatabase() {
		return TimeSeriesDatabase;
	}
	public void setTimeSeriesDatabase(String timeSeriesDatabase) {
		TimeSeriesDatabase = timeSeriesDatabase;
	}
	public String getGeoServerDatabase() {
		return GeoServerDatabase;
	}
	public void setGeoServerDatabase(String geoServerDatabase) {
		GeoServerDatabase = geoServerDatabase;
	}
	public String getAquamapsDatabase() {
		return AquamapsDatabase;
	}
	public void setAquamapsDatabase(String aquamapsDatabase) {
		AquamapsDatabase = aquamapsDatabase;
	}
	
	public String getConfigPath() {
		return configPath;
	}
	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	public String getReferenceSpeciesTable() {
		return ReferenceSpeciesTable;
	}
	public void setReferenceSpeciesTable(String referenceSpeciesTable) {
		ReferenceSpeciesTable = referenceSpeciesTable;
	}
	public String getReferenceCountriesTable() {
		return ReferenceCountriesTable;
	}
	public void setReferenceCountriesTable(String referenceCountriesTable) {
		ReferenceCountriesTable = referenceCountriesTable;
	}
	
	public String getPersistencePath() {
		return persistencePath;
	}
	public void setPersistencePath(String persistencePath) {
		this.persistencePath = persistencePath;
	}

	private String persistencePath;
	private String configPath;
	private String TimeSeriesUserName;
	private String GeoServerUserName;
	private String AquamapsUserName;
	private String TimeSeriesPassword;
	private String GeoServerPassword;
	private String AquamapsPassword;
	private String TimeSeriesDatabase;
	private String GeoServerDatabase;
	private String AquamapsDatabase;
	private String ReferenceSpeciesTable = "ref_species";
	private String ReferenceCountriesTable = "ref_country";
}
