package org.gcube.common.geoserverinterface.test;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.geoserverinterface.GeoCaller;
import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeoserverMethodResearch;
import org.gcube.common.geoserverinterface.GeoserverCaller;
import org.gcube.common.geoserverinterface.bean.CswRecord;

public class TestGeoCaller {
	
	public static void main(String[] args) {

		String geonetworkUrl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geonetwork";
		String geonetworkUsername = "admin";
		String geonetworkPassword = "admin";
		
		String geoserverUrl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver";
		String geoserverUsername = "admin";
		String geoserverPassword = "gcube@geo2010";
		
		//select the geo network choice method for writing layers
		GeoserverMethodResearch geoserverMethodReserch = GeoserverMethodResearch.MOSTUNLOAD;
//		GeoserverMethodResearch geoserverMethodReserch = GeoserverMethodResearch.RANDOM;
		
		GeoCaller geoCaller = null;
		
		try {
			
			//instantiate a new Geo network caller with a geoserver failover	
			geoCaller = new GeoCaller(geonetworkUrl, geonetworkUsername, geonetworkPassword, geoserverUrl, geoserverUsername, geoserverPassword, geoserverMethodReserch);
			
			System.out.println("Current Geoserver: " + geoCaller.getCurrentWmsGeoserver()); //Show current geoserver
			
			//get the complete list of geoservers
			ArrayList<String> list = geoCaller.getWmsGeoserverList();
			
			for(String a: list){
				System.out.println("Geoserver Element List: " +a);
			}
			
			//find a group by geo network
			String geoserverFound = geoCaller.getGeoServerForGroup("group4b2c95c50-ec8c-48aa-a575-98131b48580f");
			System.out.println(">>>>>>>>>>>>>>>>>Testing GROUP search - GROUP FOUND ON GEOSERVER "+geoserverFound);
			
			//find a layer by geo server
			String geoserverFound2 = geoCaller.getGeoServerForLayer("lbregmacerosrarisquamosus20120314023438958");
			System.out.println(">>>>>>>>>>>>>>>>>Testing LAYER search - LAYER FOUND ON GEOSERVER: "+geoserverFound2);
			
			//find geoserver for a title: es fish genus + species
			String geoserverFound3 = geoCaller.getGeoServerForName("Merluccius angustimanus",false);
			
			System.out.println(">>>>>>>>>>>>>>>>>Testing LAYER TITLE SEARCH - FOUND ON GEOSERVER "+geoserverFound3);
			 
			 //find a list of layers on a certain selected geoserver
			 ArrayList<String> workspaces = new ArrayList<String>();
			ArrayList<String> layers = new ArrayList<String>();
			
			workspaces.add("aquamaps");
			layers.add("v_eb851650072b311e1bb95855bc31c2d07");
			
			workspaces.add("aquamaps");
			layers.add("m_eb851650072b311e1bb95855bc31c2d07");
			
			
			 GeoserverCaller geoserver = new GeoserverCaller(geoserverFound, geoserverUsername, geoserverPassword);
					 
			List<String> titles = geoserver.getLayerTitlesByWms(workspaces, layers);
			int len = titles.size();
			
			for (int i=0;i<len;i++)
				System.out.println(">>>>>>>>>>>>>>>>>FOUND title \""+titles.get(i)+"\" for layer \""+ workspaces.get(i) +":"+layers.get(i)+"\"");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	

}
