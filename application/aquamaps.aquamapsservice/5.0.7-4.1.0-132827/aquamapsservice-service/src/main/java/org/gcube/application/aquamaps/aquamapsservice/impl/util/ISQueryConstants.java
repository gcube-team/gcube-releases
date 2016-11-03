package org.gcube.application.aquamaps.aquamapsservice.impl.util;


public class ISQueryConstants {

	private static final ISQueryConstants instance=new ISQueryConstants();
	
	public static ISQueryConstants get(){return instance;}
	
	//*****************GETTERS
	
	public String getGeoNetworkPlatformName(){return "geonetwork";}
	public String getGeoNetworkCategoryName(){return "Gis";}
	public String getGeoNetworkEntryName(){return "geonetwork";}
	
	
	public String getGeoServerPlatformName(){return "GeoServer";}
	public String getGeoServerCategoryName(){return "Gis";}
	public String getGeoServerEntryName(){return "geoserver";}
	public String getGeoServerAquaMapsDataStore(){return "aquamapsDataStore";}
	public String getGeoServerAquaMapsWorkspace(){return "aquamapsWorkspace";}
	public String getGeoServerAquaMapsDefaultDistributionStyle(){return "aquamapsDefaultDistributionStyle";}
	
	public String getGeoServerDBPlatformName(){return "postgis";}
	public String getGeoServerDBCategory(){return "Database";}
	public String getGeoServerDBEntryName(){return "jdbc";}
	public String getGeoServerDBAquaMapsDataStore(){return "aquamapsDataStore";}
	
	
	public String getInternalDBPlatformName(){return "postgres";}
	public String getInternalDBCategoryName(){return "Database";}
	public String getInternalDBEntryName(){return "jdbc";}
	public String getInternalDBSchemaName(){return "schema";}
	public String getInternalDBSchemaValue(){return "aquamaps";}
	
	
	public String getPublisherDBPlatformName(){return "postgres";}
	public String getPublisherDBCategoryName(){return "Database";}
	public String getPublisherDBEntryName(){return "jdbc";}
	public String getPublisherDBSchemaName(){return "schema";}
	public String getPublisherDBSchemaValue(){return "aquamapsPublisher";}
	
	public String getDBMaxConnection(){return "maxConnection";}
	public String getDBAquaMapsWorldTable(){return "aquamapsWorldTable";}
	public String getDBTableSpacePrefix(){return "tableSpacePrefix";}
	public String getDBTableSpaceCount(){return "tableSpaceCount";}
	
}
