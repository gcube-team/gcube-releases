package org.gcube.contentmanagement.timeseries.geotools.databases;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.EngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.hibernate.SessionFactory;

public class ConnectionsManager {

	private SessionFactory timeSeriesConnection;
	private String timeSeriesConnectionFile = "TimeSeriesConnection.hibernate.xml";
	private EngineConfiguration timeSeriesConfiguration;
	private SessionFactory aquamapsConnection;
	private String aquamapsConnectionFile = "AquamapsConnection.hibernate.xml";
	private EngineConfiguration aquamapsConfiguration;
	private SessionFactory geoserverConnection;
	private String geoServerConnectionFile = "GeoServerConnection.hibernate.xml";
	private EngineConfiguration geoServerConfiguration;
	private String cachePath = "";

	public SessionFactory getTimeSeriesConnection() {
		return timeSeriesConnection;
	}
	
	public static String getTableName(String tableName){
		
		return tableName.toLowerCase();
	}
	
	
	public void setAquamapsConnection(SessionFactory aquamapsConnection) {
		this.aquamapsConnection = aquamapsConnection;
	}

	public SessionFactory getAquamapsConnection() {
		return aquamapsConnection;
	}

	public void setGeoserverConnection(SessionFactory geoserverConnection) {
		this.geoserverConnection = geoserverConnection;
	}

	public SessionFactory getGeoserverConnection() {
		return geoserverConnection;
	}

	public ConnectionsManager(String cachePath) {
		this.cachePath = cachePath;
	}

	public void initTimeSeriesConnection(EngineConfiguration configuration) throws Exception {
		this.setTimeSeriesConfiguration(configuration);
		timeSeriesConnection = DatabaseFactory.initDBConnection(cachePath + timeSeriesConnectionFile, configuration);
	}

	public void initAquamapsConnection(EngineConfiguration configuration) throws Exception {
		this.setAquamapsConfiguration(configuration);
		aquamapsConnection = DatabaseFactory.initDBConnection(cachePath + aquamapsConnectionFile, configuration);
	}

	public void initGeoserverConnection(EngineConfiguration configuration) throws Exception {
		this.setGeoServerConfiguration(configuration);
		geoserverConnection = DatabaseFactory.initDBConnection(cachePath + geoServerConnectionFile, configuration);
	}

	public void GeoserverUpdate(String updateQuery) throws Exception {
		DatabaseFactory.executeSQLUpdate(updateQuery, geoserverConnection);
	}

	public void AquamapsUpdate(String updateQuery) throws Exception {
		DatabaseFactory.executeSQLUpdate(updateQuery, aquamapsConnection);
	}

	public void TimeSeriesUpdate(String updateQuery) throws Exception {
		DatabaseFactory.executeSQLUpdate(updateQuery, timeSeriesConnection);
	}

	public List<Object> GeoserverQuery(String query) throws Exception {
		return DatabaseFactory.executeSQLQuery(query, geoserverConnection);
	}

	public List<Object> AquamapsQuery(String query) throws Exception {
		return DatabaseFactory.executeSQLQuery(query, aquamapsConnection);
	}

	public List<Object> TimeSeriesQuery(String query) throws Exception {
		return DatabaseFactory.executeSQLQuery(query, timeSeriesConnection);
	}

	public void setCachePath(String cachePath) {
		this.cachePath = cachePath;
	}

	public String getCachePath() {
		return cachePath;
	}

	public void setTimeSeriesConfiguration(EngineConfiguration timeSeriesConfiguration) {
		this.timeSeriesConfiguration = timeSeriesConfiguration;
	}

	public EngineConfiguration getTimeSeriesConfiguration() {
		return timeSeriesConfiguration;
	}

	public void setAquamapsConfiguration(EngineConfiguration aquamapsConfiguration) {
		this.aquamapsConfiguration = aquamapsConfiguration;
	}

	public EngineConfiguration getAquamapsConfiguration() {
		return aquamapsConfiguration;
	}

	public void setGeoServerConfiguration(EngineConfiguration geoServerConfiguration) {
		this.geoServerConfiguration = geoServerConfiguration;
	}

	public EngineConfiguration getGeoServerConfiguration() {
		return geoServerConfiguration;
	}

	public void shutdownAll() {
		try {
			aquamapsConnection.close();
		} catch (Exception e) {
		}
		try {
			geoserverConnection.close();
		} catch (Exception e) {
		}
		try {
			timeSeriesConnection.close();
		} catch (Exception e) {
		}
	}
}
