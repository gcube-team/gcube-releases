package org.gcube.contentmanagement.timeseries.geotools.vti.test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.timeseries.geotools.engine.TSGeoToolsConfiguration;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;
import org.gcube.contentmanagement.timeseries.geotools.tools.PointsMapCreator;
import org.gcube.contentmanagement.timeseries.geotools.utils.OccurrencePointVector2D;
import org.gcube.contentmanagement.timeseries.geotools.utils.Tuple;

public class RegressionPointsMapGeoNetworkProd {

	public static void main (String[] args) throws Exception{
		
		TSGeoToolsConfiguration configuration = new TSGeoToolsConfiguration();
		configuration.setConfigPath("./cfg/");
		
		/*
		configuration.setTimeSeriesDatabase("jdbc:postgresql://146.48.87.169/testdb");
		configuration.setTimeSeriesUserName("gcube");
		configuration.setTimeSeriesPassword("d4science2");
*/
		configuration.setGeoServerDatabase("jdbc:postgresql://node50.p.d4science.research-infrastructures.eu/timeseriesgisdb");
		configuration.setGeoServerUserName("postgres");
		configuration.setGeoServerPassword("d4science2");
			
		GISInformation gisInfo = new GISInformation();
		gisInfo.setGeoNetworkUrl("http://geoserver.d4science-ii.research-infrastructures.eu/geonetwork");
//		gisInfo.setGeoNetworkUrl("http://146.48.87.49:8080/geonetwork");
		gisInfo.setGeoNetworkUserName("admin");
		gisInfo.setGeoNetworkPwd("admin");
		
		gisInfo.setGisDataStore("timeseriesgisdb");
		gisInfo.setGisPwd("gcube@geo2010");
		gisInfo.setGisWorkspace("aquamaps");
		gisInfo.setGisUrl("http://geoserver.d4science-ii.research-infrastructures.eu/geoserver");
//		gisInfo.setGisUrl("http://geoserver-ddddd.d4science-ii.research-infrastructures.eu/geoserver");
		gisInfo.setGisUserName("admin");

		List<OccurrencePointVector2D> xyPoints = new ArrayList<OccurrencePointVector2D>();
		
		for (int i=0;i<10;i++){
			OccurrencePointVector2D pointsvector = new OccurrencePointVector2D((float)(-180f+2f*Math.random()*180f), (float)(-90f+2f*Math.random()*90f));
			pointsvector.addMetadataToMap("test1", "testvalue1");
			pointsvector.addMetadataToMap("test2", "testvalue2");
			pointsvector.addMetadataToMap("test3", "testvalue3");
			pointsvector.addMetadataToMap("test4", "testvalue4");
			xyPoints.add(pointsvector);
		}
		
		PointsMapCreator pmcreator = new PointsMapCreator(configuration);
		String destinationMapTable = "pexample12345";
		String destinationMapName = "occurrence points";
		
		AnalysisLogger.getLogger().trace("Producing MAP: "+destinationMapTable);
		
		
		String groupName = pmcreator.createMapFromPoints(xyPoints, destinationMapTable, destinationMapName, gisInfo);
		
		AnalysisLogger.getLogger().trace("PRODUCED group: "+groupName);
	}
	
}

