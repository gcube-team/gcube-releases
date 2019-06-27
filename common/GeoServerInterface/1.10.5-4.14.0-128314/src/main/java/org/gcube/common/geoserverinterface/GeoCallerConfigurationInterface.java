package org.gcube.common.geoserverinterface;

import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeoserverMethodResearch;

public abstract class GeoCallerConfigurationInterface {
	
	//Default Http Resource research parameters
	public static int TRYSLEEPTIME = 1000; //in ms
	public static int MAXTRY = 2;
	
	//Default global Geoserver Confogurations
	public static final GeoserverMethodResearch DEFAULTMETHODRESEARCH = GeoserverMethodResearch.MOSTUNLOAD;
	
//	//Default global Geonetwork Confogurations
//	public static enum GeonetworkCategory {APPLICATION, DATASETS, ANY};
//	public static enum GeoserverMethodResearch {RANDOM, MOSTUNLOAD};
//	public static enum GeoserverType {WMS, WFS};
	
}
