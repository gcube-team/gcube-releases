package org.gcube.common.geoserverinterface.test;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.geoserverinterface.GeoCaller;
import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeoserverMethodResearch;
import org.gcube.common.geoserverinterface.GeoserverCaller;
import org.gcube.common.geoserverinterface.bean.CswRecord;

public class TestGeoCaller2 {
	
	public static void main(String[] args) {

		String geonetworkUrl = "http://geoserver.d4science-ii.research-infrastructures.eu/geonetwork";
		String geonetworkUsername = "admin";
		String geonetworkPassword = "admin";
		
		String geoserverUrl = "http://geoserver.d4science-ii.research-infrastructures.eu/geoserver";
		String geoserverUsername = "admin";
		String geoserverPassword = "gcube@geo2010";
		
		//select the geo network choice method for writing layers
		GeoserverMethodResearch geoserverMethodReserch = GeoserverMethodResearch.MOSTUNLOAD;
//		GeoserverMethodResearch geoserverMethodReserch = GeoserverMethodResearch.RANDOM;
		
		GeoCaller geoCaller = null;
		
		try {
			
			//instantiate a new Geo network caller with a geoserver failover	
			geoCaller = new GeoCaller(geonetworkUrl, geonetworkUsername, geonetworkPassword, geoserverUrl, geoserverUsername, geoserverPassword, geoserverMethodReserch);
			
			//find a layer by geo server
			String geoserverFound2 = geoCaller.getGeoServerForLayer("compl_4f9a7758_0507_4256_9acf_7c49948d49d0");
			System.out.println(">>>>>>>>>>>>>>>>>Testing LAYER search - LAYER FOUND ON GEOSERVER: "+geoserverFound2);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	

}
