package org.gcube.application.framework.search.library.impl;

import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.data.DataCollection;
import gr.uoa.di.madgik.rr.element.query.QueryHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.core.util.SessionConstants;
import org.gcube.application.framework.search.library.cache.CollectionsFieldsCache;
import org.gcube.application.framework.search.library.exception.InitialBridgingNotCompleteException;
import org.gcube.application.framework.search.library.exception.InternalErrorException;
import org.gcube.application.framework.search.library.exception.NoAvailableCollectionsFoundException;
import org.gcube.application.framework.search.library.interfaces.SearchInfoI;
import org.gcube.application.framework.search.library.model.CollectionInfo;
import org.gcube.application.framework.search.library.model.Field;
import org.gcube.application.framework.search.library.model.Query;
import org.gcube.application.framework.search.library.model.QueryGroup;
import org.gcube.application.framework.search.library.util.FindFieldsInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author rena - NKUA
 *
 */

public class SearchHelper implements SearchInfoI{

	ASLSession session;
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(SearchHelper.class);
	
	/**
	 *  Document factory instance
	 */
	public static final DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();


	/**
	 * @param username the username of the user that makes the request
	 * @param extSessionID the external session ID. In case of a web aplication using ASL, this is the http session ID
	 */
	public SearchHelper(String username, String extSessionID) {
		super();
		session = SessionManager.getInstance().getASLSession(extSessionID, username);

		if(session.getAttribute(SessionConstants.Queries) == null)
		{	//Set default Query
			List<QueryGroup> qGroup = new ArrayList<QueryGroup>();
			List<Query> queries = new ArrayList<Query>();
			queries.add(new Query());
			qGroup.add(new QueryGroup(queries));
			session.setAttribute(SessionConstants.Queries, qGroup);
		}
	}


	/**
	 * @param session
	 */
	public SearchHelper(ASLSession session){
		this.session=session;

		if(session.getAttribute(SessionConstants.Queries) == null)
		{	//Set default Query
			List<QueryGroup> qGroup = new ArrayList<QueryGroup>();
			List<Query> queries = new ArrayList<Query>();
			queries.add(new Query());
			qGroup.add(new QueryGroup(queries));
			session.setAttribute(SessionConstants.Queries, qGroup);
		}
	}

	/** {@inheritDoc}*/
	public List<QueryGroup> getAllQueries() {
		return (List<QueryGroup>) session.getAttribute(SessionConstants.Queries);
	}

	
	
	
	public HashMap<CollectionInfo, ArrayList<CollectionInfo>> getAvailableFTSCollections() throws InitialBridgingNotCompleteException, InternalErrorException {
		
		HashMap<CollectionInfo, ArrayList<CollectionInfo>> allCollections = (HashMap<CollectionInfo, ArrayList<CollectionInfo>>) CollectionsFieldsCache.getInstance().getCollectionsInfoForScope(session.getScopeName(), true);
		HashMap<CollectionInfo, ArrayList<CollectionInfo>> ftsCollections = new HashMap<CollectionInfo, ArrayList<CollectionInfo>>();
		
		for (CollectionInfo colGroup : allCollections.keySet()) {
			ArrayList<CollectionInfo> withFTS = new ArrayList<CollectionInfo>();
			for (CollectionInfo colInfo : allCollections.get(colGroup))
				if (colInfo.isFts()) 
					withFTS.add(colInfo);
			ftsCollections.put(colGroup, withFTS);
		}

		return ftsCollections;
	}
	
	/** {@inheritDoc}
	 * @throws InitialBridgingNotCompleteException 
	 * @throws InternalErrorException */
	public HashMap<CollectionInfo, ArrayList<CollectionInfo>> getAvailableCollections() throws InitialBridgingNotCompleteException, InternalErrorException {
//		HashMap<CollectionInfo, ArrayList<CollectionInfo>> collections = (HashMap<CollectionInfo, ArrayList<CollectionInfo>>) session.getAttribute(SessionConstants.collectionsHierarchy + session.getScopeName());
//		if (collections == null) {
//			//collections = FindFieldsInfo.joinDynamicAndStaticConfiguration(session.getScopeName());
//			logger.debug("The collections in session are null - get them from the cache");
//			collections = CollectionsFieldsCache.getInstance().getCollectionsInfoForScope(session.getScopeName(), false);
//			session.setAttribute(SessionConstants.collectionsHierarchy + session.getScopeName(), collections);
//		} else {
//			logger.debug("The collections in session are NOT null");
//		}
		HashMap<CollectionInfo, ArrayList<CollectionInfo>> collections = CollectionsFieldsCache.getInstance().getCollectionsInfoForScope(session.getScopeName(), true);
		return collections;
	}
	
	
	public ArrayList<String> getExternalCollections() throws InitialBridgingNotCompleteException, InternalErrorException {
		ArrayList<String> externals = new ArrayList<String>();
//		try {
//			ResourceRegistry.startBridging();
//		} catch (ResourceRegistryException e1) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e1);
//		}
		try {
			Set<DataCollection> collections = QueryHelper.getExternalCollectionsOfScope(session.getScopeName());
			for (DataCollection col:collections) {
				CollectionInfo colInfo = this.findCollectionInfo(col.getID());
				if (colInfo != null)
					externals.add(col.getID());
			}
		} catch (ResourceRegistryException e) {
			logger.error("An error occured while retrieving external collections from Registry", e);
			throw new InternalErrorException(e.getCause());
		}
		return externals;
	}
	
	public ArrayList<CollectionInfo> getExternalCollectionInfos() throws InitialBridgingNotCompleteException, InternalErrorException {
		ArrayList<CollectionInfo> externals = new ArrayList<CollectionInfo>();
		try {
			Set<DataCollection> collections = QueryHelper.getExternalCollectionsOfScope(session.getScopeName());
			for (DataCollection col:collections) {
				CollectionInfo colInfo = this.findCollectionInfo(col.getID());
				if (colInfo != null)
					externals.add(colInfo);
			}
		} catch (ResourceRegistryException e) {
			logger.error("An error occured while retrieving external collections from Registry", e);
			throw new InternalErrorException(e.getCause());
		}
		return externals;
	}
	
	
	/** {@inheritDoc}*/
	public QueryGroup getQuery(int qid) {
		return ((List<QueryGroup>) session.getAttribute(SessionConstants.Queries)).get(qid);
	}

	/** {@inheritDoc}*/
	public int addQuery(QueryGroup query) {
		((List<QueryGroup>) session.getAttribute(SessionConstants.Queries)).add(query);
		return ((List<QueryGroup>) session.getAttribute(SessionConstants.Queries)).size() -1;
	}

	/** {@inheritDoc}*/
	public void removeQuery(int qid) {
		((List<Query>) session.getAttribute(SessionConstants.Queries)).remove(qid);
	}
	
	/** {@inheritDoc}*/
	public int createQuery(int qid, boolean ...previous) {
		List<Query> queries = getQuery(qid).getQueries();
		List<Query> queries2 = new ArrayList<Query>(queries.size());

		boolean pre = (previous != null && previous.length > 0)?previous[0]:false;
		Query query = null;

		try {
			
			query = queries.get(0).clone(pre);
		} catch (Exception e) {
			logger.error("Exception:", e);
			query = new Query();
		}
		//If query is split in several subqueries... 
		if(queries.size() > 1)
		{
			List<String> newCollections = new ArrayList<String>();
			for(int i =0; i < queries.size(); i ++)
			{
				for(String x: queries.get(i).getSelectedCollections())
				{
					String y = new String(x);
					newCollections.add(y);
				}
			}
			query.selectCollections(newCollections , true, session, false);
		}
		queries2.add(query);
		QueryGroup newQuery = new QueryGroup(queries2);
		return addQuery(newQuery);
	}

	/** {@inheritDoc}*/
	public int getNumberOfQueryGroups() {
		List<QueryGroup> queries = getAllQueries();
		if (queries != null)
			return queries.size();
		else
			return 0;
	}
	
	
	
	public QueryGroup getActiveQueryGroup()
	{
		List<QueryGroup> queries = getAllQueries();
		Integer no = (Integer) session.getAttribute(SessionConstants.activeQueryNo);
		int i;
		if (no == null)
			i = 0;
		else
			i = no.intValue();
		return queries.get(i);
	}

	/** {@inheritDoc}*/
	public int getActiveQueryGroupNo()
	{
		Integer no = (Integer) session.getAttribute(SessionConstants.activeQueryNo);
		int i;
		if (no == null)
			i = 0;
		else
			i = no.intValue();
		return i;
	}

	/** {@inheritDoc}*/
	public void setActiveQueryGroup(int i)
	{
		session.setAttribute(SessionConstants.activeQueryNo, new Integer(i));
	}
	

	/** {@inheritDoc}
	 * @throws InitialBridgingNotCompleteException 
	 * @throws InternalErrorException */
	public HashMap<CollectionInfo, ArrayList<CollectionInfo>> refreshAvailableCollections() throws InitialBridgingNotCompleteException, InternalErrorException {

		//HashMap<CollectionInfo, ArrayList<CollectionInfo>> collections = FindFieldsInfo.joinDynamicAndStaticConfiguration(session.getScopeName());
		HashMap<CollectionInfo, ArrayList<CollectionInfo>> collections = CollectionsFieldsCache.getInstance().refreshCollectionInfoForScope(session.getScopeName(), true);
		session.setAttribute(SessionConstants.collectionsHierarchy, collections);
		return collections;
	}
	

	public CollectionInfo findCollectionInfo(String collectionId) throws InitialBridgingNotCompleteException, InternalErrorException {
		HashMap<CollectionInfo, ArrayList<CollectionInfo>> collections = getAvailableCollections();
		if (collections != null) {
			Set<CollectionInfo> groups = collections.keySet();
			for (CollectionInfo group:groups) {
				if (group.getId().equals(collectionId))
					return group;
				ArrayList<CollectionInfo> realCols = collections.get(group);
				for (int i = 0; i < realCols.size(); i++) {
					if (realCols.get(i).getId().equals(collectionId)) {
						return realCols.get(i);
					}
				}
			}
			return null;
		} else {
			try {
				throw new NoAvailableCollectionsFoundException();
			} catch (NoAvailableCollectionsFoundException e) {
				logger.debug("No Available collections were found.");
			}
			return null;
		}
	}
	
	/** {@inheritDoc}
	 * @throws InitialBridgingNotCompleteException 
	 * @throws InternalErrorException */
	public List<CollectionInfo> searchCollections(String term, String whereToSearch) throws InitialBridgingNotCompleteException, InternalErrorException
	{
		HashMap<CollectionInfo, ArrayList<CollectionInfo>> colH = this.getAvailableCollections();
		return FindFieldsInfo.searchCollectionInfo(term, whereToSearch, colH);
	}
	
	

}
