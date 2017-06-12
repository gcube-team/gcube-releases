package org.gcube.portlets.user.geoexplorer.server;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portlets.user.geoexplorer.client.beans.GeoexplorerMetadataStyleInterface;
import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;
import org.gcube.portlets.user.geoexplorer.client.rpc.GeoExplorerService;
import org.gcube.portlets.user.geoexplorer.server.service.GeonetworkInstance;
import org.gcube.portlets.user.geoexplorer.server.service.GisPublisherSearch;
import org.gcube.portlets.user.geoexplorer.server.util.ASLSessionUtil;
import org.gcube.portlets.user.geoexplorer.server.util.HttpSessionUtil;
import org.gcube.portlets.user.geoexplorer.server.util.MetadataConverter;
import org.gcube.portlets.user.geoexplorer.shared.MetadataItem;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.FilterConfig;
import com.extjs.gxt.ui.client.data.FilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


@SuppressWarnings("serial")
public abstract class GeoExplorerServiceImpl extends RemoteServiceServlet implements GeoExplorerService {

	protected static Logger logger = Logger.getLogger(GeoExplorerServiceImpl.class);

	protected abstract GeoExplorerServiceInterface getGeoParameters() throws Exception;
	protected abstract List<String> getInternalGeoserver() throws Exception;
	protected abstract String getGisLinkForUUID(String uuid) throws Exception;
	/**
	 *
	 * @return List of default layers item
	 * @throws Exception
	 */
	protected abstract List<String> getDefaultLayersItem() throws Exception;
	/**
	 * List of metadata styles which show in geoexplorer
	 * @param onlyIsDisplay
	 * @return
	 * @throws Exception
	 */
	protected abstract List<? extends GeoexplorerMetadataStyleInterface> getGeoexplorerStylesToShow(boolean onlyIsDisplay) throws Exception;

	/**
	 *
	 * @param scope
	 */
	protected abstract void invalidCache(ScopeBean scope);

	/**
	 *
	 * @return  List of base layers item
	 * @throws Exception
	 */
	protected abstract List<String> getBaseLayersItem() throws Exception;

//	private static final long CACHE_REFRESH_TIME = 30*60*1000;  // 30 minutes
//	private static final int CACHE_REFRESH_DELAY = 20*60*1000; //20 minute
	private static final String TITLE_FIELD = "title";
//	private Timer timerGeoExplorerCache;


	public  GisPublisherSearch getGisPublisherSearchSession(String scope){
		HttpSession httpSession = getHttpSession();
		return HttpSessionUtil.getGisPublisherSearchSession(httpSession, scope);
	}

	public GeoNetworkReader getGeonetworkReader(String scope) throws Exception{
		HttpSession httpSession = getHttpSession();
		GeoExplorerServiceInterface serviceParams = getGeoParameters();
		GeonetworkInstance geonetworkInstance = serviceParams.getGeonetworkInstance(httpSession, scope);
		return geonetworkInstance.getGeonetworkReader();
	}


	protected HttpSession getHttpSession(){
		return this.getThreadLocalRequest().getSession();
	}


	//Modified by Francesco - 04-04-2013
	@Override
	public PagingLoadResult<LayerItem> getLayers(FilterPagingLoadConfig config) throws Exception {

		if(config.getOffset()<0){
			logger.error("get Layers with offset < 0 - return empty results");
			return new BasePagingLoadResult<LayerItem>(new ArrayList<LayerItem>(), 0, 0);
		}

		HttpSession httpSession = getHttpSession();

		logger.trace("httpSessionID "+httpSession.getId());
		GeoExplorerServiceInterface serviceParams = getGeoParameters();
		if(!serviceParams.isValidGeoInstance()){
			logger.trace("geonetowork istance is setted invalid, updating...");
			serviceParams.updateGeonetworkInstance(httpSession, serviceParams.getScope());
		}

		return cswLoad(config);
	}

	//FRANCESCO M. HD REFRESH
	@Override
	public Boolean hardRefresh() throws Exception {
		logger.trace("Hard refresh on gisPublisherSearch");

		try{
			HttpSession httpSession = getHttpSession();
			GeoExplorerServiceInterface serviceParams = getGeoParameters();
			// get GisPublisherSearch

			logger.info("Hard refresh on gisPublisherSearch, found scope: "+serviceParams.getScope());

			GisPublisherSearch gisPublisherSearch = getGisPublisherSearchSession(serviceParams.getScope());

			logger.info("gisPublisherSearch is null? "+(gisPublisherSearch==null));

			if(gisPublisherSearch != null){
				gisPublisherSearch.getFetching().getPersistence().removeAll();
				//INVALID LAST GisPublisherSearch this force create new search
				HttpSessionUtil.setGisPublisherSearchSession(null, httpSession, serviceParams.getScope());
				logger.info("GisPublisherSearchSession has been setted as null/invalid");
				HttpSessionUtil.setGeonetorkInstance(httpSession, null, serviceParams.getScope());
				logger.info("GeonetorkInstance has been setted as null/invalid");
				invalidCache(new ScopeBean(serviceParams.getScope()));

//				serviceParams.updateGeonetworkInstance(httpSession, serviceParams.getScope());
//				serviceParams.setValidGeoInstance(false);
//				logger.trace("GeoInstance setted as invalid");
//				HttpSessionUtil.resetAllHashsForScope(httpSession, serviceParams.getScope());

			}else{
				logger.info("Forcing all cache to GeonetorkInstance as null");
				HttpSessionUtil.setGisPublisherSearchSession(null, httpSession, serviceParams.getScope());
				logger.info("GisPublisherSearchSession has been setted as null/invalid");
				HttpSessionUtil.setGeonetorkInstance(httpSession, null, serviceParams.getScope());
				logger.info("GeonetorkInstance has been setted as null/invalid");
				invalidCache(new ScopeBean(serviceParams.getScope()));
			}

			return true;

		}catch (Exception e) {
			String error = "Sorry, an error occurred when executing hard refresh on search";
			logger.error(error, e);
			throw new Exception(error);
		}
	}


	/**
	 * @param workspaceCache
	 * @param geonetworkCache
	 * @param config
	 * @return
	 */
	private PagingLoadResult<LayerItem> cswLoad(FilterPagingLoadConfig config) throws Exception {
		logger.trace("CSW SEARCH - QUERY PARAMETERS");
		GeoExplorerServiceInterface serviceParams = getGeoParameters();
		logger.trace("CSW SEARCH - Current scope is: "+serviceParams.getScope());

		SortInfo sortInfo = config.getSortInfo();
		final String sortField = sortInfo.getSortField();
		SortDir sortDir = sortInfo.getSortDir(); //TODO
		List<FilterConfig> filters = config.getFilterConfigs();//TODO

//		int start = config.getOffset()+1; // in csw query records start with 1

		//ADDED BY FRANCESCO
		int start = config.getOffset(); // in csw query records start with 1
		int maxRecords = config.getLimit();
		boolean sortByTitle = sortField==null ? false : sortField.equals(TITLE_FIELD);
		boolean sortAscendent = sortDir!=SortDir.DESC;

		//TEMPORARY SOLUTION - TODO FIXING ON CLIENT
		sortByTitle = true;
		logger.trace("search bounds, [start: "+start +" maxRecords: "+maxRecords+"]");

		//CHANGE ADD OTHER FILTER
		String textToSearch = filters.size()==0 ? "" : (String)filters.get(0).getValue();

		boolean isNewSearch = false;

//		textToSearch = "fao";

		List<LayerItem> layerItems = new ArrayList<LayerItem>();
		GisPublisherSearch gisPublisherSearch = getGisPublisherSearchSession(serviceParams.getScope());

		if(gisPublisherSearch==null){
			gisPublisherSearch = new GisPublisherSearch(getGeonetworkReader(serviceParams.getScope()));
			gisPublisherSearch.setListUrlInternalGeoserver(getInternalGeoserver());
			isNewSearch = true;
		}else if(gisPublisherSearch.getLastTextToSearch().compareTo(textToSearch)!=0){
			logger.trace("Last Text To Search is not equal to current text to search "+textToSearch + " new searching..." );
			isNewSearch = true;
		}

		try {

			if(gisPublisherSearch.getFetching()==null || isNewSearch){
				logger.trace("fetching is null: "+gisPublisherSearch.getFetching());
				logger.trace("isNewSearch : "+isNewSearch);

//				if(gisPublisherSearch.getFetching()!=null && !gisPublisherSearch.getFetching().isComplete()){
				if(gisPublisherSearch.getFetching()!=null){
					logger.trace("closing old fetching..");
					gisPublisherSearch.getFetching().close();
					gisPublisherSearch.getFetching().getPersistence().removeAll();
					logger.trace("old fetching closed - OK");
				}

				gisPublisherSearch.search(sortByTitle, textToSearch, getHttpSession());
				HttpSessionUtil.setGisPublisherSearchSession(gisPublisherSearch, getHttpSession(), serviceParams.getScope());
				logger.trace("created new gis publisher search at Http Session id "+getHttpSession().getId());
			}

			if(gisPublisherSearch.getFetching()==null)
				throw new Exception("An error occurred on server when fetching data search session");

			layerItems = gisPublisherSearch.getListLayerItemFromBufferGNMetadata(start, maxRecords);
//			layerItems = GisPublisherSearchUtil.getListLayerItemFromBufferGNMetadata(fetching, geonetworkInstance.getGeonetworkReader(), start, maxRecords);
			logger.trace("return "+layerItems.size()+ " layer items");

//		int start //TODO
//		boolean sortAscendent //TODO
//		int maxRecords; //TODO
//		FILTER_TYPE filterType = (filters.size()==0 ? FILTER_TYPE.NO_FILTER : FILTER_TYPE.ANY_TEXT); //TODO FILTER TYPE

		return new BasePagingLoadResult<LayerItem>(layerItems, config.getOffset(), gisPublisherSearch.getFetching().getTotalMetadata());

		} catch (Exception e) {
			logger.error("Sorry, an error has occurred on the server when loading the layers. Try with hard refresh", e);
			throw new Exception("Sorry, an error has occurred on the server when loading the layers. Try with hard refresh.");
		}

	}


	/**
	 * return scope to string
	 */
	@Override
	public String initGeoParameters() throws Exception {
		try {

			GeoExplorerServiceInterface serviceParams = getGeoParameters();
			logger.trace("In get Workspace - service params is: "+serviceParams);

			getGeonetworkReader(serviceParams.getScope()); //instancing GeonetworkInstance and storing data into DB

			return serviceParams.getScope();
		} catch (Exception e) {
			logger.error("Error in initGeoParameters", e);
			throw new Exception("Sorry, an error occurred when initializing the Geoexplorer, try again later");
		}
	}

	/**
	 * Use this method to recover geonetwork instance from http session
	 * @param httpSession
	 * @return GeonetworkInstance
	 * @throws Exception
	 */
	public static GeonetworkInstance getGeonetworkInstanceFromSession(HttpSession httpSession, String scope) throws Exception{

		GeonetworkInstance gn;
		logger.debug("Tentative get GeonetworkInstanceFromSession..");
		logger.debug("Http Session is null? "+(httpSession==null));
		logger.debug("Scope is: "+scope);

		try {
			if(httpSession!=null){
				gn = HttpSessionUtil.getGeonetworkInstance(httpSession, scope);
				if(gn == null){
					logger.warn("Found http session id "+httpSession.getId() +" with geonetwork istance at null, new instancing");
					gn = defaultInstancingGeonetwork(httpSession, scope);
				}
			}else{
				gn = defaultInstancingGeonetwork(httpSession, scope);
			}

		} catch (Exception e) {
			logger.warn("An error occurred when recovering geonetwork instance from httpsession: " + httpSession.getId());
			logger.warn("Instancing new geonetwork: " + httpSession.getId());
			gn = defaultInstancingGeonetwork(httpSession, scope);
		}

		return gn;
	}

	/**
	 *
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	private static GeonetworkInstance defaultInstancingGeonetwork(HttpSession httpSession, String scope) throws Exception{

		GeonetworkInstance gn = new GeonetworkInstance(scope);
		gn.readConfigurationAndInstance(true, httpSession);

		if(scope==null){
			logger.trace("defaultInstancingGeonetwork scope is null overriding scope value as empty");
			scope="";
		}

		HttpSessionUtil.setGeonetorkInstance(httpSession, gn, scope);
		HttpSessionUtil.setScopeInstance(httpSession, scope);

		return gn;
	}

	/**
	 * Retrieve the metadata item by UUID
	 * @param metadataUUID
	 * @param httpSession
	 * @return MetadataItem
	 * @throws Exception
	 */
	public static MetadataItem getMetadataItemByUUID(String metadataUUID, HttpSession httpSession, String scope) throws Exception{

		GeonetworkInstance gn = getGeonetworkInstanceFromSession(httpSession, scope);
		gn.authenticateOnGeoenetwork(true);

		return MetadataConverter.getMetadataItemFromMetadataUUID(gn.getGeonetworkReader(), metadataUUID);
	}

	/**
	 * Retrieve the layer item by UUID
	 *
	 * @param metadataUUID
	 * @param httpSession
	 * @return LayerItem
	 * @throws Exception
	 */
	public static LayerItem getLayerItemByUUID(String metadataUUID, HttpSession httpSession, String scope) throws Exception{

		GeonetworkInstance gn = getGeonetworkInstanceFromSession(httpSession, scope);
		gn.authenticateOnGeoenetwork(true);

		return MetadataConverter.getLayerItemFromMetadataUUID(gn.getGeonetworkReader(), metadataUUID);
	}

	/**
	 * Retrieve the xml of the metadata passed in input (UUID)
	 * @param metadataUUID
	 * @param httpSession
	 * @return the xml source of metadata
	 * @throws Exception
	 */
	public static String getRowMetadataItemByUUID(String metadataUUID, HttpSession httpSession, String scope) throws Exception{

		GeonetworkInstance gn = getGeonetworkInstanceFromSession(httpSession, scope);
		gn.authenticateOnGeoenetwork(true);

		return gn.getGeonetworkReader().getByIdAsRawString(metadataUUID);

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.client.GeoExplorerService#getDefaultLayersName()
	 */
	@Override
	public List<LayerItem> getDefaultLayers() throws Exception {
//		serviceParams = getGeoParameters(); //init

		try{
			List<String> list = getDefaultLayersItem();
			return getListLayerItemByUUIDForNewInstance(list,getHttpSession());

		}catch (Exception e) {
			logger.error("Error on retrieving DefaultLayers, returning empty List", e);
			return new ArrayList<LayerItem>();
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.geoexplorer.client.GeoExplorerService#getDefaultLayersName()
	 */
	@Override
	public List<LayerItem> getBaseLayers() throws Exception {
//		serviceParams = getGeoParameters(); //init
		try{
			List<String> list = getBaseLayersItem();
			return getListLayerItemByUUIDForNewInstance(list,getHttpSession());
		}catch (Exception e) {
			logger.error("Error on retrieving BaseLayers, returning empty List", e);
			return new ArrayList<LayerItem>();
		}

	}

	@Override
	public List<LayerItem> getListLayerItemByUUID(List<String> listMetadataUUID) throws Exception {

		HttpSession httpSession = getHttpSession();
		List<LayerItem> listLayerItem = new ArrayList<LayerItem>();
		GeoExplorerServiceInterface serviceParams = getGeoParameters();
		for (String metadataUUID : listMetadataUUID) {
			listLayerItem.add(getLayerItemByUUID(metadataUUID, httpSession, serviceParams.getScope()));
		}
		return listLayerItem;

	    /*ExecutorService executor = Executors.newFixedThreadPool(5);
	    List<MetadataConverterWorkerThread> worksers = new ArrayList<MetadataConverterWorkerThread>(listMetadataUUID.size());

	    for (String metadataUUID : listMetadataUUID) {
	    	MetadataConverterWorkerThread worker = new MetadataConverterWorkerThread(metadataUUID, httpSession, serviceParams.getScope());
            worksers.add(worker);
            executor.execute(worker);
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        logger.trace("Finished all threads");

        for (MetadataConverterWorkerThread metadataConverterWorkerThread : worksers) {
        	listLayerItem.add(metadataConverterWorkerThread.getLayerItem());
		}*/

	}

	private List<LayerItem> getListLayerItemByUUIDForNewInstance(List<String> listMetadataUUID, HttpSession httpSession) throws Exception{

		GeoExplorerServiceInterface serviceParams = getGeoParameters();
		GeonetworkInstance gn = new GeonetworkInstance(serviceParams.getScope());
		gn.readConfigurationAndInstance(true, httpSession);

		List<LayerItem> listLayerItem = new ArrayList<LayerItem>(listMetadataUUID.size());
		for (String metadataUUID : listMetadataUUID) {
			listLayerItem.add(getLayerItemByUUID(metadataUUID, httpSession, serviceParams.getScope()));
		}

		return listLayerItem;
		/*
//		for (String metadataUUID : listMetadataUUID)
//			listLayerItem.add(MetadataConverter.getLayerItemFromMetadataUUID(gn.getGeonetworkReader(), metadataUUID));

		ExecutorService executor = Executors.newFixedThreadPool(1);
	    List<MetadataConverterWorkerThread> worksers = new ArrayList<MetadataConverterWorkerThread>(listMetadataUUID.size());

	    for (String metadataUUID : listMetadataUUID) {
	    	MetadataConverterWorkerThread worker = new MetadataConverterWorkerThread(metadataUUID, httpSession, serviceParams.getScope());
            worksers.add(worker);
            executor.execute(worker);
        }

        executor.shutdown();
//	        while (!executor.isTerminated()) {
//	        }
        logger.trace("Finished all threads");

        for (MetadataConverterWorkerThread metadataConverterWorkerThread : worksers) {
        	listLayerItem.add(metadataConverterWorkerThread.getLayerItem());
		}*/

	}


	@Override
	public List<? extends GeoexplorerMetadataStyleInterface> getGeoexplorerStyles(boolean onlyIsDisplay) throws Exception {

		try{
			return getGeoexplorerStylesToShow(onlyIsDisplay);

		}catch (Exception e) {
			logger.error("Error on retrieving GeoexplorerStyles to show, returning null", e);
			return null;
		}

	}

	@Override
	public String getGisViewerLinkForUUID(String uuid) throws Exception {

		try{
			return getGisLinkForUUID(uuid);

		}catch (Exception e) {
			logger.error("Error on retrieving GeoexplorerStyles to show, returning null", e);
			return null;
		}

	}


	/**
	 * Checks if is session expired.
	 *
	 * @return true, if is session expired
	 */
	@Override
	public Boolean isSessionExpired(){

		logger.debug("Checking session expired..");
		HttpSession httpSession = getHttpSession();
		boolean sessionIsExpired = ASLSessionUtil.isSessionExpired(httpSession);
		logger.debug("Is Expired? "+sessionIsExpired);
		return sessionIsExpired;

	}
}