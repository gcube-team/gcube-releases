package org.gcube.portlets.user.geoexplorer.server.service;

import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;
import it.geosolutions.geonetwork.util.GNSearchResponse.GNMetadata;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.client.Constants;
import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;
import org.gcube.portlets.user.geoexplorer.server.datafetcher.FetchingSession;
import org.gcube.portlets.user.geoexplorer.server.datafetcher.converter.CloseableIterator;
import org.gcube.portlets.user.geoexplorer.server.util.MetadataConverter;
import org.gcube.portlets.user.geoexplorer.shared.FetchingElement;
import org.gcube.portlets.user.geoexplorer.shared.GeonetworkMetadata;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
import org.gcube.spatial.data.geonetwork.LoginLevel;


/**
 * The Class GisPublisherSearch.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 27, 2016
 */
public class GisPublisherSearch {

	private static final int TIME_SLEEPING = 5;

	protected static Logger logger = Logger.getLogger(GisPublisherSearch.class);

	private GeoNetworkReader geonetworkReader = null;
	private FetchingSession<GeonetworkMetadata> fetching = null;

	private String lastTextToSearch = "";

	private List<String> listUrlInternalGeoserver = new ArrayList<String>();

	/**
	 * Instantiates a new gis publisher search.
	 */
	public GisPublisherSearch(){} //FOR SERIALIZATION

	/**
	 * Instantiates a new gis publisher search.
	 *
	 * @param geonetworkReader the geonetwork reader
	 */
	public GisPublisherSearch(GeoNetworkReader geonetworkReader){
		this.geonetworkReader = geonetworkReader;
	}

	/**
	 * Search.
	 *
	 * @param sortByTitle the sort by title
	 * @param textToSearch the text to search
	 * @param httpSession the http session
	 * @throws Exception the exception
	 */
	public void search(boolean sortByTitle, String textToSearch, HttpSession httpSession) throws Exception{
		this.lastTextToSearch = textToSearch;
		GNSearchRequest req=getRequest(sortByTitle, textToSearch);

//		System.out.println("sortByTitle "+sortByTitle);
//		System.out.println("textToSearch "+textToSearch);
//		System.out.println("httpSession "+httpSession.getId());

		logger.trace("geonetworkReader quering...");
		geonetworkReader.login(LoginLevel.DEFAULT);
		GNSearchResponse resp=geonetworkReader.query(req); //EXECUTE QUERY

		logger.trace("geonetworkReader response returned...");
		StreamIterator<GNMetadata> stream = new StreamIterator<GNMetadata>(resp.iterator());
		logger.trace("stream iterator created");

		CloseableIterator<FetchingElement> output = IteratorChainBuilder.buildChain(stream, null);
		logger.trace("iterator chained");

		this.fetching = (FetchingSession<GeonetworkMetadata>) FetchingSessionUtil.createFetchingSession(output, httpSession, resp.getCount());
	}

	/**
	 * Gets the request.
	 *
	 * @param sortByTitle the sort by title
	 * @param textToSearch the text to search
	 * @return the request
	 */
	public GNSearchRequest getRequest(boolean sortByTitle, String textToSearch) {

		GNSearchRequest req = new GNSearchRequest();

		if(sortByTitle)
			req.addConfig(GNSearchRequest.Config.sortBy, "title");

		if(textToSearch==null || textToSearch.isEmpty()){
			req.addParam(GNSearchRequest.Param.any, textToSearch);
			logger.trace("search by any text");
		}else{
			req.addParam(GNSearchRequest.Param.title, textToSearch);
			req.addConfig(GNSearchRequest.Config.similarity, Integer.toString(Constants.QUERY_SIMILARITY));
			logger.trace("search by title");
		}
		logger.trace("text to search "+textToSearch);

		return req;
	}

	/**
	 * Gets the list layer item from buffer gn metadata.
	 *
	 * @param start the start
	 * @param limit the limit
	 * @return the list layer item from buffer gn metadata
	 * @throws Exception the exception
	 */
	public List<LayerItem> getListLayerItemFromBufferGNMetadata(int start, int limit) throws Exception{

		boolean pageComplete = false;
		int end;
		int offset;
		boolean fetchingComplete = false;
//		HashMap<String, LayerItem> hashResults = new HashMap<String, LayerItem>(); //UUID - LayerItem
		logger.trace("inputs start: "+start + " limit: "+limit);
		List<GeonetworkMetadata> data = new ArrayList<GeonetworkMetadata>();
		List<LayerItem> listLayersItem = new ArrayList<LayerItem>();

		while(pageComplete == false){

			fetchingComplete = fetching.isComplete();
			end = Math.min(start+limit,fetching.getBufferSize());
			offset = Math.max(end-start, 0);

			logger.trace("chunk selected data bounds [start: "+start+" end: " +end+ " offset: "+offset+"]");
			logger.trace("fetching is completed: "+fetchingComplete);

			if(offset<limit && !fetchingComplete){ //DATA IS NOT AVAILABLE
				logger.trace("offset is minor of limit, now sleeping "+TIME_SLEEPING+ " ms");
				Thread.sleep(TIME_SLEEPING);
				logger.trace("alive...");
			}else{
				//BOUNDS VALIDATION
				if(start>=0 && limit>=0){
					logger.trace("buffer get data bounds [start: "+start+" limit: " +limit+"] is available");
					logger.trace("pageComplete");
					pageComplete = true;
					data = new ArrayList<GeonetworkMetadata>(fetching.getBuffer().getList(start, limit));
				}
			}

			for (GeonetworkMetadata gnMeta  : data) {
				logger.trace("converting... "+gnMeta.getUuid());
				LayerItem layerItem = MetadataConverter.getLayerItemFromMetadata(geonetworkReader, gnMeta);

				if(layerItem!=null){
					boolean isInternalGeoserver  = false;
					for (String internalGeoserverUrl : listUrlInternalGeoserver) {
						logger.trace("compare external geoserver... "+layerItem.getGeoserverUrl() + " "+internalGeoserverUrl);
						isInternalGeoserver = isSameBaseUri(layerItem.getGeoserverUrl(), internalGeoserverUrl);
						logger.trace("is internal geoserver... "+isInternalGeoserver);
						layerItem.setIsInternalLayer(isInternalGeoserver);
					}
				}
//				listLayersItem.add(MetadataConverter.getLayerItemFromMetadata(geonetworkReader, gnMeta));
				listLayersItem.add(layerItem);
			}

			/*ExecutorService executor = Executors.newFixedThreadPool(5);
			List<MetadataConverterWorkerThread> worksers =new ArrayList<MetadataConverterWorkerThread>(data.size());
			for (GeonetworkMetadata gnMeta : data) {
				logger.trace("converting... "+gnMeta.getUuid());
//				String geoNetworkUrl = "http://geoserver-dev2.d4science-ii.research-infrastructures.eu/geonetwork";
//				String geoNetworkPwd = "Geey6ohz";
//				String geoNetworkUser = "admin";
				MetadataConverterWorkerThread worker =new MetadataConverterWorkerThread(geonetworkReader, gnMeta.getUuid());
				worksers.add(worker);
				executor.execute(worker);
			}
			executor.shutdown();
			while (!executor.isTerminated()) {
		    }
			// while (!executor.isTerminated()) {
			// }
			logger.trace("Finished all threads");
			for (MetadataConverterWorkerThread metadataConverterWorkerThread : worksers) {
				LayerItem layerItem = metadataConverterWorkerThread.getLayerItem();

				if(layerItem!=null){
					boolean isInternalGeoserver  = false;

					for (String internalGeoserverUrl : listUrlInternalGeoserver) {

						logger.trace("compare external geoserver... "+layerItem.getGeoserverUrl() + " "+internalGeoserverUrl);
						isInternalGeoserver = isSameBaseUri(layerItem.getGeoserverUrl(), internalGeoserverUrl);

						logger.trace("is internal geoserver... "+isInternalGeoserver);
						layerItem.setIsInternalLayer(isInternalGeoserver);
					}
				}

//				listLayersItem.add(MetadataConverter.getLayerItemFromMetadata(geonetworkReader, gnMeta));
				listLayersItem.add(layerItem);
			}*/
		}
		//DEBUG
//		printLayers(listLayers);
		return listLayersItem;

	}

	/**
	 * Checks if is same base uri.
	 *
	 * @param uri1 the uri1
	 * @param uri2 the uri2
	 * @return true, if is same base uri
	 */
	private boolean isSameBaseUri(String uri1, String uri2){

		boolean isSame = false;

		if(uri1 == null || uri2 == null)
			return false;

		if(uri1!=null && uri2!=null){
			String baseUrl1 = getBaseUrlFromUri(uri1);
			String baseUrl2 = getBaseUrlFromUri(uri2);
			logger.trace("compare internal geoserver... "+baseUrl1 + " "+baseUrl2);
			if(baseUrl1.compareToIgnoreCase(baseUrl2)==0)
				isSame = true;
		}
		return isSame;
	}

	/**
	 * Gets the base url from uri.
	 *
	 * @param uri the uri
	 * @return the base url from uri
	 */
	private String getBaseUrlFromUri(String uri){

		String baseUrl = "";
		int lastIndexOfSlash = uri.lastIndexOf("/");

		if(lastIndexOfSlash>0)
			baseUrl = uri.substring(0, lastIndexOfSlash);

//		int lastIndexOfDots = uri.lastIndexOf(":");
//
//		if(lastIndexOfDots>0)
//			baseUrl = uri.substring(0, lastIndexOfDots);

		return baseUrl;
	}

	/**
	 * Prints the layers.
	 *
	 * @param listLayers the list layers
	 */
	private void printLayers(List<LayerItem> listLayers){

		int i = 0;
		for (LayerItem layerItem : listLayers) {
			logger.trace(++i +") " + layerItem);
		}


	}

	/**
	 * Sets the geonetwork reader.
	 *
	 * @param geonetworkReader the new geonetwork reader
	 */
	public void setGeonetworkReader(GeoNetworkReader geonetworkReader) {
		this.geonetworkReader = geonetworkReader;
	}

	/**
	 * Gets the fetching.
	 *
	 * @return the fetching
	 */
	public FetchingSession<GeonetworkMetadata> getFetching() {
		return fetching;
	}

	/**
	 * Gets the last text to search.
	 *
	 * @return the last text to search
	 */
	public String getLastTextToSearch() {
		return lastTextToSearch;
	}

	/**
	 * Sets the last text to search.
	 *
	 * @param lastTextToSearch the new last text to search
	 */
	public void setLastTextToSearch(String lastTextToSearch) {
		this.lastTextToSearch = lastTextToSearch;
	}

	/**
	 * Gets the list url internal geoserver.
	 *
	 * @return the list url internal geoserver
	 */
	public List<String> getListUrlInternalGeoserver() {
		return listUrlInternalGeoserver;
	}

	/**
	 * Sets the list url internal geoserver.
	 *
	 * @param listUrlInternalGeoserver the new list url internal geoserver
	 */
	public void setListUrlInternalGeoserver(List<String> listUrlInternalGeoserver) {
		this.listUrlInternalGeoserver = listUrlInternalGeoserver;
	}

}
