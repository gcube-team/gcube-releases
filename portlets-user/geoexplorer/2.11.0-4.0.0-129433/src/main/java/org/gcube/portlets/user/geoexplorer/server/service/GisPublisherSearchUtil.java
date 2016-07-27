package org.gcube.portlets.user.geoexplorer.server.service;

import it.geosolutions.geonetwork.util.GNSearchRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;
import org.gcube.portlets.user.geoexplorer.server.datafetcher.FetchingSession;
import org.gcube.portlets.user.geoexplorer.server.util.MetadataConverter;
import org.gcube.portlets.user.geoexplorer.shared.GeonetworkMetadata;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;

public class GisPublisherSearchUtil {
	
	protected static Logger logger = Logger.getLogger(GisPublisherSearchUtil.class);

//	public static FetchingSession<GeonetworkMetadata> fetchingData(GeoNetworkReader geonetworkReader, boolean sortByTitle, int maxRecords, String textToSearch) throws Exception{
//		
//		GNSearchRequest req=getRequest(sortByTitle, textToSearch);
//
//		GNSearchResponse resp=geonetworkReader.query(req); //EXECUTE QUERY
//		
//		StreamIterator<GNMetadata> stream = new StreamIterator<GNMetadata>(resp.iterator());
//		logger.trace("stream iterator created");
//		
//		CloseableIterator<FetchingElement> output = IteratorChainBuilder.buildChain(stream, null);
//		logger.trace("iterator chained");
//		
//		return (FetchingSession<GeonetworkMetadata>) FetchingSessionUtil.createFetchingSession(output, null, resp.getCount());
//	}
//	
	
	
	public static GNSearchRequest getRequest(boolean sortByTitle, String textToSearch) {

		GNSearchRequest req = new GNSearchRequest();
		
		if(sortByTitle)
			req.addConfig(GNSearchRequest.Config.sortBy, "title");
		
		req.addConfig(GNSearchRequest.Config.sortBy, "title");

		req.addParam(GNSearchRequest.Param.any, textToSearch);
		
		return req;
	}
	
	public static List<LayerItem> getListLayerItemFromBufferGNMetadata(FetchingSession<GeonetworkMetadata> fetching, GeoNetworkReader gReader, int start, int limit) throws Exception{
		
		List<GeonetworkMetadata> bufferList = fetching.getBuffer().getList();
		boolean foundData = false;
		int end;
		HashMap<String, LayerItem> hashResults = new HashMap<String, LayerItem>(); //UUID - LayerItem
		
		while(foundData == false){
		
			bufferList = fetching.getBuffer().getList();
			
			end = Math.min(start+limit, bufferList.size());
			start = Math.min(start, end);
		
			logger.trace("chunk selected data bounds [start: "+start+" end: " + end+"]");
			
			List<GeonetworkMetadata> data = new ArrayList<GeonetworkMetadata>(bufferList.subList(start, end));
			
			logger.trace("buffer size : "+data.size() +" limit " + limit);
			logger.trace("fetching is completed: "+fetching.isComplete());
			
			if(data.size()<limit && !fetching.isComplete()){
				logger.trace("buffer size is minor of limit, now sleeping...");
				Thread.sleep(50);
				logger.trace("alive...");
			}
			else
				foundData = true;
			
			for (GeonetworkMetadata gnMeta  : data) {
				
				if(hashResults.get(gnMeta.getUuid())==null){ //IF LAYER ITEM IS NOT CONVERTED
					logger.trace("converting... "+gnMeta.getUuid());
					LayerItem layerItem = MetadataConverter.getLayerItemFromMetadata(gReader, gnMeta);
					hashResults.put(gnMeta.getUuid(), layerItem);
				}
			}
		}
		
		List<LayerItem> listLayers = new ArrayList<LayerItem>(hashResults.values());
		
		//DEBUG
//		printLayers(listLayers);

		return listLayers;

	}
	
	public static void printLayers(List<LayerItem> listLayers){
		
		int i = 0;
		for (LayerItem layerItem : listLayers) {
			logger.trace(++i +") " + layerItem);
		}
		
		
	}

}
