package org.gcube.portlets.user.geoexplorer.test;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;
import org.gcube.portlets.user.geoexplorer.server.service.GeonetworkInstance;
import org.gcube.portlets.user.geoexplorer.server.service.GisPublisherSearch;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
import org.gcube.spatial.data.geoutility.wms.WmsGetStyles;

public class GeoExplorerQueryTest {

//	public static final String TEXT_TO_SEARCH = "sarda sarda";
	
//	public static final String TEXT_TO_SEARCH = "fao";
	
	public static final String TEXT_TO_SEARCH = "";

	public static Logger logger = Logger.getLogger(GeoExplorerQueryTest.class);

	private static HttpSession session;

	public static <T> void main(String[] args) throws Exception {

		
		String defaultScope="/gcube/devsec/devVRE";
		
//		String defaultScope = "/d4science.research-infrastructures.eu/gCubeApps/EcologicalModelling";
		ScopeProvider.instance.set(defaultScope);


		logger.trace("Text query to: "+TEXT_TO_SEARCH);
		
		GeonetworkInstance gn = new GeonetworkInstance(defaultScope);
		gn.readConfigurationAndInstance(true,null);
		
//		String geoNetworkUrl = "http://geoserver-last.d4science-ii.research-infrastructures.eu/geonetwork";
//		String geoNetworkUser = "admin";
//		String geoNetworkPwd = "admin";
//		String geoNetworkScope = "/d4science.research-infrastructures.eu/gCubeApps/EcologicalModelling";
//		
//		GeonetworkInstance gn = new GeonetworkInstance(geoNetworkUrl, geoNetworkUser, geoNetworkPwd, true);

//		gn.readConfigurationAndInstance(true);
		logger.trace("-- GeonetworkInstance OK");
		
		
		GeoNetworkReader gReader = gn.getGeonetworkReader();
		logger.trace("-- GeoNetworkReader OK");
		
//		GNSearchRequest req = getRequest();
//		logger.trace("-- GNSearchRequest OK");
		
		GisPublisherSearch gisPublisherSearch = new GisPublisherSearch(gReader);
		
		
		ArrayList<String> listGeoserver = new ArrayList<String>();
		listGeoserver.add("http://geoserver.d4science-ii.research-infrastructures.eu/geoserver");
		gisPublisherSearch.setListUrlInternalGeoserver(listGeoserver);
		

		int limit = 20;
		int start = 0;
		try {
			
			logger.trace("-- GeoNetworkReader submit query");
			gisPublisherSearch.search(true, TEXT_TO_SEARCH, session);// EXECUTE QUERY

			List<LayerItem> layerItem = gisPublisherSearch.getListLayerItemFromBufferGNMetadata(start, limit);
			

			
			WmsGetStyles wmsGetStyles = new WmsGetStyles();

			
			printLayerItems(layerItem, 500);
			logger.trace("-- returned "+layerItem.size() +" items");
			
			Thread.sleep(2000);
			
			gisPublisherSearch.getFetching().getPersistence().removeAll();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error in layers csw loader ", e);
		}

	}
	
	public static void printLayerItems(List<LayerItem> layerItems, int max){
		
		for (int i = 0; i < layerItems.size(); i++) {
			
			logger.trace(layerItems.get(i));

			if(i==max)
				break;
		}	
	}
	
	

}
