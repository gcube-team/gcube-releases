package org.gcube.application.framework.http.oaipmh;

import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.http.oaipmh.Data.Pair;
import org.gcube.application.framework.search.library.impl.SearchHelper;
import org.gcube.application.framework.search.library.model.CollectionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCubeTools {

	private static final Logger logger = LoggerFactory.getLogger(GCubeTools.class);
	
	/**
	 * 
	 * @param username
	 * @param sessionID
	 * @return null in case of exception, empty if nothing found.
	 */
	public static HashMap<CollectionInfo, ArrayList<CollectionInfo>> getGCubeCollections(String username, String sessionID){
		HashMap<CollectionInfo, ArrayList<CollectionInfo>> collectionInfos = new HashMap<CollectionInfo, ArrayList<CollectionInfo>>();
		try {
			SearchHelper s_h = new SearchHelper(username, sessionID);
			collectionInfos = s_h.getAvailableCollections();
		} catch (Exception e) {
			logger.debug("Exception: ",e);
			return null;
		}
		return collectionInfos;
	}
	
	public static HashMap<CollectionInfo, ArrayList<CollectionInfo>> getGCubeCollections(ASLSession aslSession){
		HashMap<CollectionInfo, ArrayList<CollectionInfo>> collectionInfos = new HashMap<CollectionInfo, ArrayList<CollectionInfo>>();
		try {
			SearchHelper s_h = new SearchHelper(aslSession);
			collectionInfos = s_h.getAvailableCollections();
		} catch (Exception e) {
			logger.debug("Exception: ",e);
			return null;
		}
		return collectionInfos;
	}
	
	
	
	
	
	
}
