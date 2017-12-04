package org.gcube.common.geoserverinterface.test;

import java.util.ArrayList;

import org.gcube.common.geoserverinterface.GeoCaller;
import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeoserverMethodResearch;
import org.gcube.common.geoserverinterface.bean.CswRecord;


public class TestGeoCallerMethods {
	
	public static void main(String[] args) {

		String geonetworkUrl = "http://localhost:8080/geonetwork";
		String geonetworkUsername = "admin";
		String geonetworkPassword = "admin";
		
		String geoserverUrl = "http://localhost:8080/geoserver";
//		String geoserverUrl = null;
//		String geoserverUrl = "http://node25.d.d4science.research-infrastructures.eu:8080/geoserver";
		String geoserverUsername = "admin";
		String geoserverPassword = "gcube@geo2010";
		
		GeoserverMethodResearch geoserverMethodReserch = GeoserverMethodResearch.MOSTUNLOAD;

//		GeoCallerConfigurationInterface.MAXTRY = 5;
		
		
		GeoCaller geoCaller = null;
		try {
			
			geoCaller = new GeoCaller(geonetworkUrl, geonetworkUsername, geonetworkPassword, geoserverUrl, geoserverUsername, geoserverPassword, geoserverMethodReserch);
			
//			geoCaller = new GeoCaller(geoserverUrl, geoserverUsername, geoserverPassword);
			
			System.out.println("Current Geoserver: " + geoCaller.getCurrentWmsGeoserver()); //Show current geoserver
			
			ArrayList<String> list = geoCaller.getWmsGeoserverList();
			
			for(String a: list){
				System.out.println("Geoserver Element List: " +a);
			}
			
			ArrayList<String> list2 = geoCaller.listLayers();
			
			for(String a: list2)
				System.out.println("Layer: " + a);
			
			
			//GET LAYER TITLE EXAMPLE
			String res = geoCaller.getLayerTitleByWms("topp","states");
			
			System.out.println(res);
			
			
			
			//EXAMPLE CHANGE GEOSERVER
//			geoserverUrl = "http://node25.d.d4science.research-infrastructures.eu:8080/geoserver";
//			geoCaller.setWmsGeoserver(geoserverUrl, geoserverUsername, geoserverPassword);
//			
//			ArrayList<String> list3 = geoCaller.listLayers();
//			
//			for(String a: list3)
//				System.out.println("Layer: " + a);
//				
			
//			System.out.println(geoCaller.searchLayerByTitleIsEqualTo("TrueMarble"));
//			System.out.println(geoCaller.searchLayerByTitleIsEqualTo("USA POPULATION"));
			
			
			 System.out.println("\n\n\nSearch By AnyText \n\n");
			 try {
				ArrayList<CswRecord> arrayCsw = new ArrayList<CswRecord>();
				arrayCsw = geoCaller.getGeonetworkCswRecordsBySearch("USA POPULATION", 0);
				
				if(arrayCsw.size() == 0)
					System.out.println("Object not found");
				
				int i = 0;
				for(CswRecord cso : arrayCsw){
					System.out.println("\nCSW Object "+ ++i +"...");
					System.out.println("Identifier: " + cso.getIdentifier());
					System.out.println("Title: " +cso.getTitle());
					for(String uri : cso.getURI())
						System.out.println("URI: " +uri);
				}
					
				
			 } catch (Exception e) {
			 e.printStackTrace();
			 }
			
//			System.out.println(geoCaller.getGeonetworkLayerInfoBySearchService("USA POPULATION", GeonetworkCategory.ANY, true));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	

}
