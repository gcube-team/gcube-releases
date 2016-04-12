package org.gcube.portlets.user.collectionsnavigatorportlet.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.search.library.exception.InitialBridgingNotCompleteException;
import org.gcube.application.framework.search.library.exception.InternalErrorException;
import org.gcube.application.framework.search.library.impl.SearchHelper;
import org.gcube.application.framework.search.library.model.Query;
import org.gcube.application.framework.search.library.model.QueryGroup;
import org.gcube.application.framework.search.library.util.FindFieldsInfo;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.collectionsnavigatorportlet.shared.CollectionInfo;
import org.gcube.portlets.user.collectionsnavigatorportlet.shared.CollectionInfoModel;
import org.gcube.portlets.user.collectionsnavigatorportlet.shared.CollectionRetrievalException;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * CollectionsNavigatorPortlet servlet.
 * Contains all the functionality for the collections portlet
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class CollectionsNavigatorServlet extends RemoteServiceServlet implements
		org.gcube.portlets.user.collectionsnavigatorportlet.client.CollectionsNavigatorService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected int SMlocalID;

	private static Logger logger = Logger.getLogger(CollectionsNavigatorServlet.class);
	
	protected long time;

	protected long timer = -1;

	protected static final int interval = 60000; /* 10 minutes. */
	
	// Always use the first query of a query group.
	private static final int DefaultQuery = 0;
	
	private static final String OPEN_NODES_ATTR = "openNodes";

	/**
	 * Initialize the servlet
	 */
	public void init(ServletConfig conf) throws ServletException {
		super.init(conf);
	}

	/**
	 * Gets the internal ASL session
	 * 
	 * @return The ASL session instance
	 */
	private ASLSession getASLsession() {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		String username = httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
		ASLSession session = SessionManager.getInstance().getASLSession(httpSession.getId(), username);
		return session;
	}
	/**
	 * This method is used to update the status of all the available collections
	 * 
	 * @param collections The collections to be updated
	 * @param addCollection True or false depending on the status of the collections: selected or not
	 */
	public Boolean changeCollectionStatus(List<String> collections, boolean addCollection) throws Exception {
		boolean ret = false;
		ASLSession session = this.getASLsession();
		SearchHelper sHelper = new SearchHelper(session);
		// Get the current query group
		QueryGroup queryGroup = sHelper.getActiveQueryGroup();
		// Get the first query of the current group. It will always be one query on this phase
		Query queryObj = queryGroup.getQuery(DefaultQuery);

		// Contains the current selected collections
		List<String> selectedCollections = queryObj.getSelectedCollections();
		logger.debug("Number of current selected collections: " + selectedCollections.size());
		logger.debug("Selected collections are going to change.......");
		/* If addCollection = TRUE then add to the "selected collections" the new collections else remove the collections
		 * from the selected collections
		 */
		try {
			logger.debug("COLLECTIONS " + collections.size());
			queryObj.selectCollections(collections, addCollection, session, false);
		} catch (Exception e) {
			logger.debug("An exception was thrown while trying to select the collections.", e);
		}
		logger.debug("Now the number of selected collections is: " + queryObj.getSelectedCollections().size());
		if (queryObj.getSelectedCollections().size() > 0) {
			ret = queryObj.isGeoAvailable();
		}
		return new Boolean(ret);
	}
	
	public HashMap<CollectionInfoModel, ArrayList<CollectionInfoModel>> getAvailableCollections() throws CollectionRetrievalException {
		logger.debug("Retrieving available collections tree..");
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		HashMap<CollectionInfoModel, ArrayList<CollectionInfoModel>> collectionHierarchyBean = new HashMap<CollectionInfoModel, ArrayList<CollectionInfoModel>>();
		HashMap<org.gcube.application.framework.search.library.model.CollectionInfo, ArrayList<org.gcube.application.framework.search.library.model.CollectionInfo>> colInfo = null;
		try {
			colInfo = shelper.getAvailableCollections();
		}catch (Exception e) {
			throw new CollectionRetrievalException();
		}
		
		List<String> selectedCollections = queryObj.getSelectedCollections();
		List<String> openNodes = (List<String>)session.getAttribute(OPEN_NODES_ATTR);
		if(colInfo != null)
		{
				Iterator<Entry<org.gcube.application.framework.search.library.model.CollectionInfo, ArrayList<org.gcube.application.framework.search.library.model.CollectionInfo>>> it = colInfo.entrySet().iterator();
				while (it.hasNext()) {
					Entry<org.gcube.application.framework.search.library.model.CollectionInfo, ArrayList<org.gcube.application.framework.search.library.model.CollectionInfo>> group = it.next();
					
					org.gcube.application.framework.search.library.model.CollectionInfo collectionGroup = group.getKey();
					
					boolean isSelected = selectedCollections != null && selectedCollections.contains(collectionGroup.getId());
					boolean isOpen = openNodes != null && openNodes.contains(collectionGroup.getId());
					CollectionInfoModel colGroup = new CollectionInfoModel(collectionGroup.getId(), collectionGroup.getName(), collectionGroup.getDescription(), collectionGroup.getRecno(), collectionGroup.getCreationDate(), isSelected, isOpen, false);
					
					ArrayList<org.gcube.application.framework.search.library.model.CollectionInfo> collectionsList = colInfo.get(collectionGroup);
					// for each collection of the current group
					ArrayList<CollectionInfoModel> collectionsBean = new ArrayList<CollectionInfoModel>();
					for (org.gcube.application.framework.search.library.model.CollectionInfo col : collectionsList) {
						boolean isColSelected = selectedCollections != null && selectedCollections.contains(col.getId());
						boolean isColOpen = openNodes != null && openNodes.contains(col.getId());
						CollectionInfoModel collection = new CollectionInfoModel(col.getId(), col.getName(), col.getDescription(), col.getRecno(), col.getCreationDate(), isColSelected, isColOpen, true);
						collectionsBean.add(collection);
					}
					collectionHierarchyBean.put(colGroup,collectionsBean);
				}
		}
		else {
			throw new CollectionRetrievalException();
		}
		
		return collectionHierarchyBean;
		
	}
	
	public boolean isAllCollectionsBoxSelected() {
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		List<String> selectedCollections = queryObj.getSelectedCollections();
		if(selectedCollections != null && selectedCollections.contains("all_collections"))
			return true;
		return false;
	}
	
	/**
	 * This method identifies if the collection with the given 'collectionsID' is selected in
	 * the current active Query
	 * 
	 * @param collectionID The collection ID of the collection
	 * @return True if the collection is selected, else False
	 */
	public Boolean isCollectionSelected(String collectionID) {
		ASLSession session = this.getASLsession();
		SearchHelper sHelper = new SearchHelper(session);
		// Get the current query group
		QueryGroup queryGroup = sHelper.getActiveQueryGroup();
		// Get the first query of the current group. It will always be one query on this phase
		Query queryObj = queryGroup.getQuery(DefaultQuery);

		// Contains the current selected collections
		List<String> selectedCollections = queryObj.getSelectedCollections();
		if (selectedCollections != null && selectedCollections.size() > 0){
			for (int i=0; i<selectedCollections.size(); i++) {
				if (selectedCollections.get(i).equals(collectionID)) {
					logger.debug("Collection with ID -> " + collectionID + " is selected for the current query");
					return new Boolean(true);
				}
			}
		}
		logger.debug("Collection with ID -> " + collectionID + " is NOT selected for the current query");
		return new Boolean(false);
	}
	
	/**
	 * This method is used to hold the current status of the open nodes in the
	 * collections navigator's portlet tree.
	 * 
	 * @param collectionID The collection's ID that it's status is going to be set
	 * @param openStatus If true added this collection to the openNodes, else remove it
	 */
	public void setCollectionOpenStatus(String collectionID, boolean openStatus) {
		logger.info("Method setCollectionOpenStatus invoked!");
		ASLSession session = this.getASLsession();
		List<String> openNodes = (List<String>)session.getAttribute(OPEN_NODES_ATTR);
		if (openNodes == null)
			logger.debug("OpenNodes list is null");
			openNodes = new ArrayList<String>();
		
		if (openStatus) {
			if (!openNodes.contains(collectionID))
				logger.debug("collection with ID: " + collectionID + " added to the open nodes");
				openNodes.add(collectionID);
		}
		else {
			openNodes.remove(collectionID);
			logger.debug("collection with ID: " + collectionID + " removed from the open nodes");
		}		
		session.setAttribute(OPEN_NODES_ATTR, openNodes);
	}
	
	/**
	 * This method is used to refresh the information for the available collections.
	 * The information about the collections should be retrieved directly from the gCube system.
	 */
	public void refreshInformation() {
		logger.info("Refreshing the available collections...");
		ASLSession aslSession = this.getASLsession();
		SearchHelper shelper = new SearchHelper(aslSession);
		try {
			shelper.refreshAvailableCollections();
		} catch (InitialBridgingNotCompleteException e) {
			logger.error("Failed to refresh collections. InitialBridging is not completed.", e);
		} catch (InternalErrorException e) {
			logger.error("Failed to refresh collections. An internal error occurred.", e);
		}
	}
	
	/**
	 * This method searches through the available collection's information for the given term
	 * and returns a list that contains the collections which contain the specified term
	 * 
	 * @param keyword The term to search for
	 * @param searchIn Where to search for the term (collection's name, description or both)
	 * @return A list with the collections that contain the specified term
	 */
	public CollectionInfo[] searchForCollections(String keyword) {
		String whereToSearch = FindFieldsInfo.ALL; 
		ASLSession aslSession = this.getASLsession();
		SearchHelper shelper = new SearchHelper(aslSession);
		List<org.gcube.application.framework.search.library.model.CollectionInfo> returnedCollections;
		try {
			returnedCollections = shelper.searchCollections(keyword, whereToSearch);
			if (returnedCollections == null || returnedCollections.size() <= 0) {
				return null;
			}
			else {
				CollectionInfo cols[] = new CollectionInfo[returnedCollections.size()];
				for (int i=0; i<returnedCollections.size(); i++) {
					CollectionInfo element = new CollectionInfo();
					element.setId(returnedCollections.get(i).getId());
					element.setName(returnedCollections.get(i).getName());
					element.setDescription(returnedCollections.get(i).getDescription());
					element.setCollectionGroup(returnedCollections.get(i).isCollectionGroup());
					cols[i] = element;
					logger.debug("----> " + cols[i].getName());
				}
				return cols;
			}
		} catch (InitialBridgingNotCompleteException e) {
			
		} catch (InternalErrorException e) {
			
		}
		return null;
	}
	
}
