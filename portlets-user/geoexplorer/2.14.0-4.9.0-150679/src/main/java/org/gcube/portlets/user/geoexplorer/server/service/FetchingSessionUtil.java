/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.server.service;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.server.datafetcher.FetchingBuffer;
import org.gcube.portlets.user.geoexplorer.server.datafetcher.FetchingSession;
import org.gcube.portlets.user.geoexplorer.server.datafetcher.MetadataBuffer;
import org.gcube.portlets.user.geoexplorer.server.datafetcher.converter.CloseableIterator;
import org.gcube.portlets.user.geoexplorer.server.service.dao.DaoManager;
import org.gcube.portlets.user.geoexplorer.server.service.dao.MetadataPersistence;
import org.gcube.portlets.user.geoexplorer.server.util.HttpSessionUtil;
import org.gcube.portlets.user.geoexplorer.shared.FetchingElement;
import org.gcube.portlets.user.geoexplorer.shared.GeonetworkMetadata;



public class FetchingSessionUtil {
	
	public static Logger logger = Logger.getLogger(FetchingSessionUtil.class);
	
	@SuppressWarnings("unchecked")
	public static FetchingSession<? extends FetchingElement> createFetchingSession(CloseableIterator<? extends FetchingElement> source, HttpSession httpSession, int totalMetadata) throws Exception
	{
		
		String scope = HttpSessionUtil.getScopeInstance(httpSession);
		logger.trace("Scope is: "+scope);

		logger.trace("Instancing MetadataPersistence..");
		MetadataPersistence persistence = new MetadataPersistence(DaoManager.getEntityManagerFactory(httpSession, scope));
		
		logger.trace("Creating MetadataFetchingSession...");
		return (FetchingSession<GeonetworkMetadata>) createMetadataFetchingSession((CloseableIterator<GeonetworkMetadata>) source, persistence, totalMetadata);
	}

	protected static FetchingSession<GeonetworkMetadata> createMetadataFetchingSession(CloseableIterator<GeonetworkMetadata> source, MetadataPersistence persistence, int totalMetadata) throws Exception
	{

//		MetadataPersistence persistence = new MetadataPersistence(DaoManager.getEntityManagerFactory(session));
		FetchingBuffer<GeonetworkMetadata> buffer = new MetadataBuffer<GeonetworkMetadata>(persistence);
		FetchingSession<GeonetworkMetadata> fetchingSession = new FetchingSession<GeonetworkMetadata>(source, buffer, totalMetadata, persistence);
		logger.trace("FetchingSession created");
		fetchingSession.startFetching();
		logger.trace("FetchingSession start fetching..");
		return fetchingSession;
		
	}

}
