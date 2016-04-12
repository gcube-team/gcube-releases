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

public class GetStylesGeoExplorerQueryTest {

	public static final String TEXT_TO_SEARCH = "sarda sarda";

//	public static final String TEXT_TO_SEARCH = "fao";

//	public static final String TEXT_TO_SEARCH = "truemarble";

	public static Logger logger = Logger.getLogger(GetStylesGeoExplorerQueryTest.class);

	private static HttpSession session;

	public static <T> void main(String[] args) throws Exception {


		String defaultScope="/gcube/devsec/devVRE";
		ScopeProvider.instance.set(defaultScope);

		logger.trace("Text query to: "+TEXT_TO_SEARCH);

		GeonetworkInstance gn = new GeonetworkInstance(defaultScope);
		gn.readConfigurationAndInstance(true,null);
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

			/*for (int i = 0; i < layerItem.size(); i++) {

				LayerItem layer = layerItem.get(i);
				logger.trace(layerItem.get(i));

				List<String> styles = wmsGetStyles.getStylesFromWms(layer.getGeoserverUrl(), "", layer.getName());

				int ind=0;
				for (String string : styles) {
					System.out.println(ind +" "+string);
				}
			}	*/

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
