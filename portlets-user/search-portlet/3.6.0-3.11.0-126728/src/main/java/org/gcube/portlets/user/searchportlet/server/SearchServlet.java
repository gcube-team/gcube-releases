package org.gcube.portlets.user.searchportlet.server;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.core.util.SessionConstants;
import org.gcube.application.framework.search.library.exception.InitialBridgingNotCompleteException;
import org.gcube.application.framework.search.library.impl.SearchHelper;
import org.gcube.application.framework.search.library.model.Criterion;
import org.gcube.application.framework.search.library.model.Field;
import org.gcube.application.framework.search.library.model.Query;
import org.gcube.application.framework.search.library.model.QueryGroup;
import org.gcube.application.framework.search.library.util.Operator;
import org.gcube.application.framework.search.library.util.Order;
import org.gcube.application.framework.search.library.util.SearchType;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.QueryType;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.searchportlet.client.SearchConstantsStrings;
import org.gcube.portlets.user.searchportlet.client.exceptions.SearchSubmissionException;
import org.gcube.portlets.user.searchportlet.client.interfaces.SearchService;
import org.gcube.portlets.user.searchportlet.shared.BrowsableFieldBean;
import org.gcube.portlets.user.searchportlet.shared.PreviousResultsInfo;
import org.gcube.portlets.user.searchportlet.shared.RecipientTypeConstants;
import org.gcube.portlets.user.searchportlet.shared.SavedBasketQueriesInfo;
import org.gcube.portlets.user.searchportlet.shared.SearchAvailabilityType;
import org.gcube.portlets.user.searchportlet.shared.SearchableFieldBean;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Search Servlet
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class SearchServlet extends RemoteServiceServlet implements SearchService
{
	private static Logger logger = Logger.getLogger(SearchServlet.class);

	private static final int DefaultQuery = 0;
	private static final long serialVersionUID = 1L;

	/**
	 * Init Method
	 */
	public void init(ServletConfig conf) throws ServletException {
		super.init(conf);
	}

	/**
	 * This method is used to add a new criterion to the active query.
	 * 
	 * @param name The name of the criterion to be added
	 */
	public void addSearchField(SearchableFieldBean sField)
	{
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		queryObj.addCriterion(new Criterion(sField.getId(), sField.getName(), ""));
		logger.debug("Added new search field in advanced search with empty value.");
		logger.debug("Name -> " + sField.getName() + " ID -> " + sField.getId());
	}

	/**
	 * This method is used to add a new criterion on a previous query
	 * 
	 * @param name The name of the criterion to be added
	 */
	public void addSearchFieldOnPreviousQuery(SearchableFieldBean sField)
	{
		logger.debug("Add new search field in previous search with empty value.");
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getQuery(((Integer)session.getAttribute(SearchConstantsStrings.SESSION_PREVIOUSQUERYINDEX)).intValue());
		logger.debug("Index of previous query is: "+ ((Integer)session.getAttribute(SearchConstantsStrings.SESSION_PREVIOUSQUERYINDEX)).intValue());
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		queryObj.addCriterion(new Criterion(sField.getId(), sField.getName(),""));
		logger.debug("Added new criterion.......... with name: " + sField.getName());

		logger.debug("Now the criteria are... ");
		List<Criterion> tmp = queryObj.getCriteria();
		if (tmp!= null && tmp.size()>0) {
			for (int t=0; t<tmp.size(); t++)
				logger.debug((t+1) + ") CRITERION: " + tmp.get(t).getSearchFieldName() + " VALUE: " + tmp.get(t).getSearchFieldValue());
		}
		else if (tmp == null)
			logger.debug("CRITERIA ARE NULL");
		else
			logger.debug("CRITERIA SIZE IS <=0");

	}

	/**
	 * This method creates a new previous query. The new query has the same information with
	 * the existing query that it's index is passed as a parameter.
	 * 
	 * @param indexOfQueryGroupToClone The index of the existing query to clone
	 * @return the Index of the new query
	 */
	public SearchableFieldBean[] createPreviousQuery(int indexOfQueryGroupToClone) {
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		// create a new previous query and store the index of this query to the session
		logger.info("Create a new previous query for the query with index: " + indexOfQueryGroupToClone);
		int id = shelper.createQuery(indexOfQueryGroupToClone, true);
		logger.debug("The previous query was just created..... Printing some information");
		session.setAttribute(SearchConstantsStrings.SESSION_PREVIOUSQUERYINDEX, new Integer(id));
		logger.debug("Previous query index just set to the session. The index is: " + id);
		logger.debug("The search type is: " + shelper.getQuery(id).getQueries().get(DefaultQuery).getSearchType());
		List<Field> sf = null;
		logger.debug("Available search fields are....");	
		sf = shelper.getQuery(id).getQueries().get(DefaultQuery).getAvailableSearchFields();
		if (sf == null || sf.isEmpty()) {
			logger.error("This previous query does not have searchable fields and won't be used");
			return null;
		}

		SearchableFieldBean searchableFields[] = new SearchableFieldBean[sf.size()]; 
		for (int k=0; k<sf.size(); k++) {
			logger.debug("Search field name: " + sf.get(k).getName());
			searchableFields[k] = new SearchableFieldBean(sf.get(k).getId(), sf.get(k).getName(), sf.get(k).getValue());
		}
		logger.debug("The description of the query to be refined is: " + shelper.getQuery(indexOfQueryGroupToClone).getQueries().get(DefaultQuery).getQueryDescription());
		return searchableFields;
	}


	/**
	 * This method is used to create a more user friendly query description of an already performed query
	 * 
	 * @param q The query object that contains the information of the performed query
	 * @return The more user friendly description of the query in a String representation
	 */
	private String createTheDisplayQuery(Query q) {
		String displayQuery = q.getQueryDescription();
		logger.debug("The display query is: <<<<" + displayQuery + ">>>>");

		return displayQuery;
	}

	public ArrayList<String> getAvailableLanguages() {
		ASLSession session = this.getASLsession();
		SearchHelper sHelper = new SearchHelper(session);
		QueryGroup queryGroup = sHelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		return (ArrayList<String>) queryObj.getAvailableLanguages();
	}


	/**
	 * This method retrieves the browsable fields
	 * 
	 * @return the fields that can be used for browsing
	 */
	public ArrayList<BrowsableFieldBean> getBrowsableFields()
	{
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		List<Field> browsableFields = queryObj.getAvailableBrowseFields();
		if  (browsableFields != null) {
			ArrayList<BrowsableFieldBean> brFieldsBean = new ArrayList<BrowsableFieldBean>();
			for (Field bf : browsableFields) {
				String bfID = bf.getId();
				String bfName = bf.getName();
				logger.debug("Browsable field -> " + bfID + " - " + bfName);
				brFieldsBean.add(new BrowsableFieldBean(bfID, bfName));
			}
			return brFieldsBean;
		}
		return null; 
	}

	/**
	 * Get the ASL session
	 * 
	 * @return the ASL session
	 */
	private ASLSession getASLsession() {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		String username = httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
		ASLSession session = SessionManager.getInstance().getASLSession(httpSession.getId(), username);
		return session;
	}

	/**
	 * Gets the number of the selected collections, excluding group collections
	 * 
	 * @return the number of the selected collections
	 */
	public Integer getNumberOfSelectedCollections()
	{
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		int numOfRealSelectedCollections;
		try {
			numOfRealSelectedCollections = queryObj.getSelectedRealCollections(session).size();
			logger.debug("Number of real selected collections is: " + numOfRealSelectedCollections);
			return new Integer(numOfRealSelectedCollections);
		} catch (InitialBridgingNotCompleteException e) {
			e.printStackTrace();
		} catch (org.gcube.application.framework.search.library.exception.InternalErrorException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method retrieves all the previous advanced or simple queries that had been performed.
	 * It returns a linked list with all the information needed.
	 * 
	 * @return LinkedList with PreviousResultsInfo objects
	 */
	public LinkedList<PreviousResultsInfo> getPreviousQueries() {
		ASLSession session = getASLsession();
		LinkedList<PreviousResultsInfo> previousQueries = new LinkedList<PreviousResultsInfo>();

		SearchHelper shelper = new SearchHelper(session);
		if (shelper.getNumberOfQueryGroups() >= 1) {
			logger.info("Retrieving the previous queries. Only 'Advaned' and 'Simple' search queries will be used");
			List<QueryGroup> queryGroup = shelper.getAllQueries();
			for (int i=0; i<queryGroup.size(); i++) {
				/**
				 * If there are more than one query groups, and the type of search is either advanced or simple show this query as available
				 * for refinement
				 */
				Query currentQuery = queryGroup.get(i).getQuery(DefaultQuery);
				if (((currentQuery.getSearchType().equals(SearchType.AdvancedSearch)) || currentQuery.getSearchType().equals(SearchType.SimpleSearch)) && currentQuery.getAvailableSearchFields().size() > 0) {
					logger.info("Previous query found. The index of the advanced or simple query is: " + i);
					logger.info("The search type of the " + i + "  query is: " + currentQuery.getSearchType());
					PreviousResultsInfo prevInfo = new PreviousResultsInfo(currentQuery.getQueryDescription(),createTheDisplayQuery(currentQuery), i);
					previousQueries.add(prevInfo);

					logger.info("Description is: " + prevInfo.getQuery());
					logger.info("The index is: " + prevInfo.getIndexOfQueryGroup());
				}
			}
		}
		// Store this information to the session
		session.setAttribute(SearchConstantsStrings.SESSION_PREVIOUSRESULTSINFO, previousQueries);
		return previousQueries;

	}

	/**
	 * This method gets the information of a specific object that is saved as a Query in basket
	 * 
	 * @param The ID of the query that is saved in basket
	 * @return The query description as a String representation
	 * @return The SearchType of the query as a String representation. (The searchType could be:
	 * 			AdvancedSearch, SimpleSearch, GeospatialSearch, QuickSearch or GoogleSearch)
	 */
	public SavedBasketQueriesInfo getQueryFromBasket(String id) {
		String descriptionQuery = null;
		String queryType = null;
		SavedBasketQueriesInfo queryInfo = null;
		QueryType qType = null;
		Workspace root = null;
		ASLSession session = getASLsession();

		try
		{
			root = HomeLibrary.getUserWorkspace(session.getUsername());
			WorkspaceItem item = null;
			try {
				item = root.getItem(id);

				if (item.getType().equals(WorkspaceItemType.FOLDER_ITEM)) {
					FolderItem bItem = (FolderItem)item;
					if (bItem.getFolderItemType() == FolderItemType.QUERY) {
						org.gcube.common.homelibrary.home.workspace.folder.items.Query query = (org.gcube.common.homelibrary.home.workspace.folder.items.Query)bItem;
						descriptionQuery = query.getQuery();
						qType = query.getQueryType();
						if (qType != null) {
							if (qType.equals(QueryType.GOOGLE_SEARCH))
								queryType = SearchConstantsStrings.GOOGLE_SEARCH;
							else if (qType.equals(QueryType.ADVANCED_SEARCH))
								queryType = SearchConstantsStrings.ADVANCED_SEARCH;
							else if (qType.equals(QueryType.QUICK_SEARCH))
								queryType = SearchConstantsStrings.QUICK_SEARCH;
							else if (qType.equals(QueryType.GEO_SEARCH))
								queryType = SearchConstantsStrings.GEOSPATIAL_SEARCH;
							else if (qType.equals(QueryType.GENERIC_SEARCH))
								queryType = SearchConstantsStrings.GENERIC_SEARCH;
							else if (qType.equals(QueryType.BROWSE))
								queryType = SearchConstantsStrings.BROWSE_COLLECTION;
							else
								queryType = SearchConstantsStrings.SIMPLE_SEARCH;
							logger.debug("The query type that i set is : " + queryType);
						}
						// If for a reason no query type is set. Use the Advanced search
						else
							queryType = SearchConstantsStrings.SIMPLE_SEARCH;
						logger.debug("The selected item's type was a query. The description of the selected item is: << " + descriptionQuery + " >>");

						queryInfo = new SavedBasketQueriesInfo(descriptionQuery, queryType);
					}
					else
						logger.debug("The selected item's type was not query...");
				}
			} catch (ItemNotFoundException e) {
				logger.error("An exception was thrown while trying to get the query's description", e);
				e.printStackTrace();
			}
		}
		catch (InternalErrorException e)
		{
			logger.error("An internal error occured.");
		}
		catch (HomeNotFoundException e)
		{
			logger.error("Home not found");
		}
		catch (WorkspaceException e)
		{
			logger.error("Workspace not found");
		}
		return queryInfo;
	}

	/**
	 * This method is used to retrieve the number of results per page that a user has selected in browse
	 * 
	 * @return A number that declares the results per page
	 */
	public Integer getResultsNumberPerPage()
	{
		ASLSession session = getASLsession();
		return (Integer)session.getAttribute(SearchConstantsStrings.SESSION_RESULTS_NUMBER_PER_PAGE);
	}

	/**
	 * This method retrieves the available search fields
	 * 
	 * @return an array with the searchable fields
	 */
	public ArrayList<SearchableFieldBean> getSearchFields()
	{	
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		List<Field> sFields = queryObj.getAvailableSearchFields();
		if  (sFields != null && sFields.size() > 0) {
			ArrayList<SearchableFieldBean> sFieldsBean = new ArrayList<SearchableFieldBean>();
			logger.debug("Retrieving the available search fields for the current selected collections...");
			for (Field sf : sFields) {
				sFieldsBean.add(new SearchableFieldBean(sf.getId(), sf.getName(), sf.getValue()));
				logger.debug("Searchfield -> " + sf.getId() + " - " + sf.getName());
			}
			return sFieldsBean;
		}
		return null;
	}

	/**
	 * This method returns the current condition type, AND | OR
	 * 
	 * @return AND or OR condition type in a String representation
	 */
	public String getSelectedConditionType() {
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		Operator op = queryObj.getOperator();
		if (op.equals(Operator.AND))
			return(SearchConstantsStrings.CONDITIONTYPE_AND);
		return(SearchConstantsStrings.CONDITIONTYPE_OR);
	}

	/**
	 * This method returns the current condition type of a previous query, AND | OR
	 * 
	 * @return AND or OR condition type in a String representation
	 */
	public String getSelectedConditionTypeOnPreviousSearch() {
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getQuery(((Integer)session.getAttribute(SearchConstantsStrings.SESSION_PREVIOUSQUERYINDEX)).intValue());
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		Operator op = queryObj.getOperator();
		if (op.equals(Operator.AND))
			return(SearchConstantsStrings.CONDITIONTYPE_AND);
		return(SearchConstantsStrings.CONDITIONTYPE_OR);
	}


	/**
	 * This method returns the current criteria
	 * 
	 * @return A 2-dimensional string array with the name and value of the criteria
	 */
	public ArrayList<SearchableFieldBean> getSelectedFields()
	{
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		List<Criterion> criteria = queryObj.getCriteria();

		if(criteria != null && !criteria.isEmpty())
		{
			ArrayList<SearchableFieldBean> selectedCriteriaBean = new ArrayList<SearchableFieldBean>();
			for(Criterion c : criteria)
			{
				selectedCriteriaBean.add(new SearchableFieldBean(c.getSearchFieldId(), c.getSearchFieldName(), c.getSearchFieldValue()));
				logger.debug("Selected Searchfield -> " + c.getSearchFieldId() + " - " + c.getSearchFieldName());
			}
			return selectedCriteriaBean;
		}
		return null;
	}

	/**
	 * This method returns the current criteria for a previous query
	 * 
	 * @return A 2-dimensional string array with the name and value of the criteria
	 */
	public ArrayList<SearchableFieldBean> getSelectedFieldsOnPreviousSearch() {
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		logger.debug("Get the searchable fields of the previous query with index --> " + (Integer)session.getAttribute(SearchConstantsStrings.SESSION_PREVIOUSQUERYINDEX));
		QueryGroup queryGroup = shelper.getQuery(((Integer)session.getAttribute(SearchConstantsStrings.SESSION_PREVIOUSQUERYINDEX)).intValue());
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		List<Criterion> criteria = queryObj.getCriteria();

		if(criteria != null && !criteria.isEmpty())
		{
			ArrayList<SearchableFieldBean> selectedCriteriaBean = new ArrayList<SearchableFieldBean>();
			for(Criterion c : criteria)
			{
				selectedCriteriaBean.add(new SearchableFieldBean(c.getSearchFieldId(), c.getSearchFieldName(), c.getSearchFieldValue()));
				logger.debug("Previous search criteria are: \n" + " Name: " 
						+ c.getSearchFieldName() + " - Value: " + c.getSearchFieldValue());
			}
			return selectedCriteriaBean;
		}
		return null;
	}

	public String getSelectedLanguage() {
		ASLSession session = this.getASLsession();
		SearchHelper sHelper = new SearchHelper(session);
		QueryGroup queryGroup = sHelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		return queryObj.getSelectedLanguage();
	}

	/**
	 * This method retrieves the current selected tab of the search portlet
	 * 
	 * @return an Integer which identifies the current selected tab
	 */
	public Integer getSelectedTab()
	{				
		ASLSession session = getASLsession();
		logger.debug("The selected tab in servlet is: " + (Integer)session.getAttribute(SearchConstantsStrings.SESSION_SELECTEDTAB));
		return ((Integer)session.getAttribute(SearchConstantsStrings.SESSION_SELECTEDTAB));
	}


	public Boolean isSemanticSelected()
	{				
		ASLSession session = getASLsession();
		return ((Boolean)session.getAttribute(SearchConstantsStrings.SESSION_SEMANTIC_ENRICHMENT));
	}

	public Boolean isRankSelected()
	{				
		ASLSession session = getASLsession();
		return ((Boolean)session.getAttribute(SearchConstantsStrings.SESSION_RANK));
	}

	/**
	 * This method retrieves the current selected tab of the search portlet
	 * 
	 * @return an Integer which identifies the current selected tab
	 */
	public Boolean getSelectedRadioBtn()
	{				
		ASLSession session = getASLsession();
		return ((Boolean)session.getAttribute(SearchConstantsStrings.SESSION_SELECTED_RADIO_BASKET));
	}

	public boolean isAdvancedOpen() {
		ASLSession session = getASLsession();
		if (session.getAttribute(SearchConstantsStrings.SESSION_ADVANCED_OPENED) != null)
			return ((Boolean)session.getAttribute(SearchConstantsStrings.SESSION_ADVANCED_OPENED));
		else
			return false;
	}

	/**
	 * This method returns the text that was last used in a simple search
	 * 
	 * @return the last text that used in a simple search
	 */
	public String getSimpleSearchTerm() {
		ASLSession session = getASLsession();
		return (String)session.getAttribute(SearchConstantsStrings.SESSION_SIMPLE_TERM);

	}

	public void setSimpleSearchTerm(String term) {
		ASLSession session = getASLsession();
		session.setAttribute(SearchConstantsStrings.SESSION_SIMPLE_TERM, term);
	}


	/**
	 * This method retrieves the sortable fields
	 * 
	 * @return the fields that can be used for sorting
	 */
	public ArrayList<SearchableFieldBean> getSortableFields()
	{
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		List<Field> sortFields = queryObj.getAvailableSortFields();
		if  (sortFields != null) {
			ArrayList<SearchableFieldBean> sortableFieldsBean = new ArrayList<SearchableFieldBean>();
			for (Field sf : sortFields) {
				sortableFieldsBean.add(new SearchableFieldBean(sf.getId(), sf.getName(), sf.getValue()));
			}
			return sortableFieldsBean;
		}
		return null; 
	}

	private void handleSearchExceptions(Exception e) throws SearchSubmissionException {
		logger.error("An exception was thrown while submiting the query.", e);
		throw new SearchSubmissionException(e.getMessage());
	}


	public SearchAvailabilityType getSearchStatus() {
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		int numOfSelCols;
		try {
			numOfSelCols = queryObj.getSelectedRealCollections(session).size();
			if (numOfSelCols <= 0) {
				logger.debug("SEARCH STATUS --> " + SearchAvailabilityType.NO_COLLECTION_SELECTED.toString());
				return SearchAvailabilityType.NO_COLLECTION_SELECTED;
			}
			boolean hasFTS = queryObj.isFtsAvailable();
			boolean hasGeo = queryObj.isGeoAvailable();
			logger.debug("SEARCH STATUS: server response for FTS & GEO --> " + hasFTS + " & " + hasGeo);
			if (hasFTS && hasGeo) {
				logger.debug("SEARCH STATUS --> " + SearchAvailabilityType.FTS_GEO_AVAILABLE.toString());
				return SearchAvailabilityType.FTS_GEO_AVAILABLE;
			}
			else if (hasFTS && !hasGeo) {
				logger.debug("SEARCH STATUS --> " + SearchAvailabilityType.FTS_NOGEO_AVAILABLE.toString());
				return SearchAvailabilityType.FTS_NOGEO_AVAILABLE;
			}
			else if (!hasFTS && hasGeo) {
				logger.debug("SEARCH STATUS --> " + SearchAvailabilityType.GEO_NOFTS_AVAILABLE.toString());
				return SearchAvailabilityType.GEO_NOFTS_AVAILABLE;
			}
			else if (!hasFTS && !hasGeo) {
				logger.debug("SEARCH STATUS --> " + SearchAvailabilityType.NOFTS_NOGEO_AVAILABLE.toString());
				return SearchAvailabilityType.NOFTS_NOGEO_AVAILABLE;
			}
			logger.debug("SEARCH STATUS --> " + SearchAvailabilityType.SEARCH_UNAVAILABLE.toString());
			return SearchAvailabilityType.SEARCH_UNAVAILABLE;
		} catch (InitialBridgingNotCompleteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.gcube.application.framework.search.library.exception.InternalErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SearchAvailabilityType.SEARCH_UNAVAILABLE;
	}

	/**
	 * This method is used to remove a criterion.
	 * 
	 * @param searchFieldNo the internal ID of the criterion to be removed
	 */
	public void removeSearchField(int searchFieldNo)
	{
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		queryObj.removeCriterion(searchFieldNo);
		logger.debug("Criterion with index: '" + searchFieldNo + "' has just been removed from advanced search.");
	}

	/**
	 * This method is used to remove a criterion on a previous query.
	 * 
	 * @param searchFieldNo the internal ID of the criterion to be removed
	 */
	public void removeSearchFieldOnPreviousQuery(int searchFieldNo)
	{
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		int prevIndex = ((Integer)session.getAttribute(SearchConstantsStrings.SESSION_PREVIOUSQUERYINDEX)).intValue();
		logger.debug("The previous query index is --> " + prevIndex);
		QueryGroup queryGroup = shelper.getQuery(prevIndex);
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		queryObj.removeCriterion(searchFieldNo);
		logger.debug("Criterion with index: '" + searchFieldNo + "' has just been removed from previous search.");
	}

	/**
	 * This method is used to reset all the current criteria
	 */
	public void resetFields(boolean isPrevious)
	{
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup;
		if (isPrevious)
			queryGroup = shelper.getQuery(((Integer)session.getAttribute(SearchConstantsStrings.SESSION_PREVIOUSQUERYINDEX)).intValue());
		else

			queryGroup = shelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		queryObj.reset();
		logger.debug("Search criteria have been reset.");
	}


	public void sendEmailWithErrorToSupport(Throwable caught) {
		String subject = "Search Portlet - Error Notification";
		String rec[] = new String[1];
		rec[0] = "support_team@d4science.org";
		String senderEmail;
		try {
			senderEmail = "no-reply@imarine.research-infrastructures.eu";
			ErrorNotificationEmailMessageTemplate msgTemp = new ErrorNotificationEmailMessageTemplate(caught, getASLsession().getUsername(), getASLsession().getGroupName());
			EmailNotification emailNot = new EmailNotification(senderEmail, rec, subject, msgTemp.createBodyMessage(), RecipientTypeConstants.EMAIL_TO, false);
			emailNot.sendEmail();
		} catch (Exception e) {
			logger.error("Failed to send the email to the support team.", e);
		} 
	}

	public Boolean setSelectedLanguage(String language) {
		ASLSession session = this.getASLsession();
		SearchHelper sHelper = new SearchHelper(session);
		QueryGroup queryGroup = sHelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		try {
			queryObj.setSelectedLanguage(language, session);
		} catch (InitialBridgingNotCompleteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.gcube.application.framework.search.library.exception.InternalErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean ret = false;

		// Check if geospatial is available...
		ret  = queryObj.isGeoAvailable();
		return (new Boolean(ret));
	}

	/**
	 * This method sets the current selected tab to the session
	 * 
	 * @param An integer with the selected tab of the search panel
	 */
	public void setSelectedTab(Integer x)
	{				
		ASLSession session = getASLsession();
		session.setAttribute(SearchConstantsStrings.SESSION_SELECTEDTAB, x);
	}

	public void setAdvancedPanelStatus(boolean isOpened) {
		ASLSession session = getASLsession();
		session.setAttribute(SearchConstantsStrings.SESSION_ADVANCED_OPENED, new Boolean(isOpened));
	}

	public void setSelectedRadioBtn(boolean isBasketSelected) {
		ASLSession session = getASLsession();
		session.setAttribute(SearchConstantsStrings.SESSION_SELECTED_RADIO_BASKET, new Boolean(isBasketSelected));
	}

	public String stackTraceAsString(Throwable caught) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		caught.printStackTrace(printWriter);
		return writer.toString();
	}

	/**
	 * This method is used to change the condition type of the criteria.
	 * 
	 * @param newValue the condition type, AND | OR
	 * 
	 */
	public void storeConditionType(String newValue) {	
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		queryObj.setOperator((newValue.toLowerCase().equals("and"))? Operator.AND:Operator.OR);	
		logger.debug("Operator changed in the advanced query. The operator now is: '" + newValue + "'.");
	}


	/**
	 * This method is used to change the condition type of the selected fields on the previous results portlet.
	 * The condition type is stored on the 3rd item of the array with the selected field's information
	 */
	public void storeConditionTypeOnPreviousSearch(String newValue) {
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getQuery(((Integer)session.getAttribute(SearchConstantsStrings.SESSION_PREVIOUSQUERYINDEX)).intValue());
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		queryObj.setOperator((newValue.toLowerCase().equals("and"))? Operator.AND:Operator.OR);	
		logger.debug("Operator changed in the previous query. The operator now is: '" + newValue + "'.");
	}


	/**
	 * This method is used to change the name, the value or the operator of the selected field for the 'search
	 * previous results functionality, depending on the given parameters.
	 * 
	 * internal No: 0 > name of the field, 1 > value of the field, 2 > AND or OR, depending on the condition
	 */
	public void storeSelectedFieldsOnPreviousSearch(int fieldNo, int internalNo, String newValue)
	{
		ASLSession session = getASLsession();
		List<String[]> selectedFields = (List<String[]>)session.getAttribute(SearchConstantsStrings.SESSION_PREVRS_SELECTEDFIELDS);
		if(selectedFields != null)
		{
			String[] seacrhField = selectedFields.get(fieldNo);
			seacrhField[internalNo] = newValue;
			selectedFields.set(fieldNo, seacrhField);
			session.setAttribute(SearchConstantsStrings.SESSION_PREVRS_SELECTEDFIELDS,selectedFields);
		}
	}


	/**
	 * This method submits the advances query to the underlying system, by using the ASL
	 * 
	 * @param sortby which field will be used to sort the result
	 * @param order Ascending or Descending order
	 * @param keepTop 
	 * @param searchPerCollection if "True" perform a search per collection, else merge the collections
	 * @throws SearchSubmissionException 
	 * @throws Exception 
	 */
	public void submitAdvancedQuery(ArrayList<SearchableFieldBean> criteria, String sortby, String order, boolean searchPerCollection, boolean isSemanticEnriched) throws SearchSubmissionException
	{
		long startTimeofSubmission, endTimeOfSubmission;

		startTimeofSubmission =  System.currentTimeMillis();
		logger.debug("Current time in seconds is: <<" + startTimeofSubmission/1000 + ">>");
		logger.debug("Query (queries) will be submitted now.........");

		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		if (sortby != null)
			queryObj.setSortBy(sortby);
		if (order != null)
			queryObj.setOrder((order.equals(SearchConstantsStrings.ORDERTYPE_ASC))? Order.ASC:Order.DESC);
		queryObj.setSemanticEnrichment(isSemanticEnriched);
		session.setAttribute(SearchConstantsStrings.SESSION_SEMANTIC_ENRICHMENT, isSemanticEnriched);
		// advanced search here. update the correct criteria		
		if (criteria != null ) {
			int numOfActiveCriteriaAtTheQuery = queryObj.getCriteria().size();
			for (int i=0; i<criteria.size(); i++) {
				// If the criteria are already set to the query then just be sure that they have the latest values.
				if (criteria.size() == numOfActiveCriteriaAtTheQuery) {
					logger.debug("There is an existing criterion. Setting the latest value -> " + criteria.get(i).getValue());
					queryObj.updateCriterionValue(i, criteria.get(i).getValue());
				}
				// If some of the criteria have not been added yet to the query. Add them now
				else if (criteria.size() > numOfActiveCriteriaAtTheQuery) {
					for (int j=numOfActiveCriteriaAtTheQuery; j<criteria.size(); j++) {
						logger.debug("Adding new criterion to query....");
						logger.debug(criteria.get(j).getId() + " - " + criteria.get(j).getName() + " - " + criteria.get(j).getValue());
						queryObj.addCriterion(new Criterion(criteria.get(j).getId(), criteria.get(j).getName(), criteria.get(j).getValue()));
					}
				}
			}
		}

		logger.info("Starting constructing the query for advanced search.");

		session.setAttribute(SessionConstants.activePresentationQueryNo, new Integer(shelper.getActiveQueryGroupNo()));
		session.removeAttribute(SearchConstantsStrings.SESSION_RESULTS_NUMBER_PER_PAGE);

		logger.info("The value of searchPerCollection is: " + searchPerCollection);
		try {
			// User wants to search per collection
			if (searchPerCollection) {
				// Find how many queries should be constructed
				// Get the real collections, without the groups
				int numOfQueries = queryObj.getSelectedRealCollections(session).size();
				Query newQuery = null;
				// add the new queries. There is already one constructed
				for(int i=0; i<numOfQueries-1; i++) {
					newQuery = queryObj.clone(false);	
					List<String> colToRemove = newQuery.getSelectedRealCollections(session);
					colToRemove.remove(i);
					newQuery.selectCollections(colToRemove, false, session, false, true);
					queryGroup.setQuery(newQuery);
				}
				// Remove all except the last collection from the initial query
				List<String> collectionsToRemove = queryObj.getSelectedRealCollections(session);
				collectionsToRemove.remove(queryObj.getSelectedRealCollections(session).get(numOfQueries-1));
				queryObj.selectCollections(collectionsToRemove, false, session, false, true);

				//It is time to submit all the queries!
				logger.info("STARTING PERFORMING SEARCH IN ALL QUERIES!!!!!");
				logger.info("NUMBER OF QUERIES TO PERFORM IS: " + numOfQueries);
				for (int j=0; j<numOfQueries; j++) {
					logger.debug("for the " + (j+1) + "query, selected collections are:  " + queryGroup.getQuery(j).getSelectedRealCollections(session).size());
					try {
						queryGroup.getQuery(j).search(session, false, new SearchClientImpl());
					} catch (Exception e) {
						handleSearchExceptions(e);
					}
					logger.debug("for the " + (j+1) + "query, the description is: " + queryGroup.getQuery(j).getQueryDescription());
				}
			}

			// Submit only one query
			else {
				logger.debug("IN SEARCH SERVLET PRINTING INFORMATION ABOUT THE ADVANCED SEARCH AND THE QUERY");
				logger.debug("********************************************************************************");
				logger.debug("NUMBER OF REAL SELECTED COLLECTIONS IS: " + queryObj.getSelectedRealCollections(session).size());
				logger.debug("NUMBER OF SELECTED COLLECTIONS IS: " + queryObj.getSelectedCollections().size());
				logger.debug("SORT VALUE IS: " + queryObj.getSortBy());
				logger.debug("SORT ORDER IS: " + queryObj.getOrder());
				logger.debug("NUMBER OF CRITERIA IS: " + queryObj.getCriteria().size());
				logger.debug("The criteria are: ");
				for (int l=0; l<queryObj.getCriteria().size(); l++)
					logger.debug("CRITERION: (" + (l+1) + ") with name: '" + queryObj.getCriteria().get(l).getSearchFieldName() + "' with value: '" + queryObj.getCriteria().get(l).getSearchFieldValue() + "' .");
				logger.debug("********************************************************************************");

				try {
					queryObj.search(session, false, new SearchClientImpl());
				} catch (Exception e) {
					handleSearchExceptions(e);
				}
				endTimeOfSubmission = System.currentTimeMillis();
				logger.debug("QUERY description is: <<<<<<<<<" + queryObj.getQueryString() + ">>>>>>>>");
				logger.debug("Query (queries) submission ended. Current time in seconds is: <<" + endTimeOfSubmission/1000 + ">>");
				logger.debug("Total time needed for the submission of the query (queries) is: <<" + (endTimeOfSubmission -startTimeofSubmission)/1000 + ">> seconds");
			}
			logger.info("Active presentation query is: " + ((Integer)session.getAttribute(SessionConstants.activePresentationQueryNo)).intValue());
			// After performing a search create a new query. It will actually clone the current query
			int id = shelper.createQuery(shelper.getActiveQueryGroupNo());
			shelper.setActiveQueryGroup(id);
		} catch (InitialBridgingNotCompleteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.gcube.application.framework.search.library.exception.InternalErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Submits a browse query
	 * 
	 * @param sortBy which field will be used to sort the result
	 * @param sortOrder Ascending or Descending order
	 * @param typeOfBrowse Browse collection or browse only one field
	 * 
	 * @return True if the browse succeeded else False  
	 * @throws SearchSubmissionException 
	 */
	public Boolean submitBrowseQuery(BrowsableFieldBean browseBy, String sortOrder, String typeOfBrowse, int resultNumPerPage) throws SearchSubmissionException
	{		
		long startTimeofSubmission, endTimeOfSubmission;

		startTimeofSubmission =  System.currentTimeMillis();
		logger.debug("Current time in seconds is: <<" + (startTimeofSubmission/1000) + ">>");
		logger.debug("Browse query will be submitted now.........");
		Boolean ret = new Boolean(true);

		ASLSession session = getASLsession();

		logger.debug("Results number per page is: '" + resultNumPerPage + "'");
		session.setAttribute(SearchConstantsStrings.SESSION_RESULTS_NUMBER_PER_PAGE, new Integer(resultNumPerPage));

		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		queryObj.setBrowseBy(browseBy.getId());
		logger.info("Browsing collection by: " + browseBy.getName());
		queryObj.setOrder((sortOrder.toLowerCase().equals("asc"))? Order.ASC:Order.DESC);

		// browse the contents of the collection
		if (typeOfBrowse.equals(SearchConstantsStrings.BROWSE_COLLECTION))
			queryObj.setDistinct(false);
		// browse a specific field of a collection (distinct values)
		else if (typeOfBrowse.equals(SearchConstantsStrings.BROWSE_FIELD))
			queryObj.setDistinct(true);		

		session.setAttribute(SessionConstants.activePresentationQueryNo, new Integer(shelper.getActiveQueryGroupNo()));

		try {
			if (queryObj.browse(session, new SearchClientImpl()) == null) {
				logger.debug("Browse the collection returned null!!!!! Throw an alert to the user!");
				ret = false;
				return ret;
			}
		} catch (Exception e) {
			handleSearchExceptions(e);
		}
		endTimeOfSubmission = System.currentTimeMillis();
		logger.debug("Browse query submission ended. Current time in seconds is: <<" + (endTimeOfSubmission/1000) + ">>");
		logger.debug("Total time needed for the submission of the browse query is: <<" + (endTimeOfSubmission -startTimeofSubmission)/1000 + ">> seconds");
		int id = shelper.createQuery(shelper.getActiveQueryGroupNo(), false);
		shelper.setActiveQueryGroup(id);
		return ret;
	}

	/** 
	 * This method is used to show the results of a query that is already performed in the current session
	 * 
	 * @param The index of the queryGroup which the results will be shown
	 * @throws SearchSubmissionException 
	 */
	public void submitBrowseQueryOnPreviousResult(int index) throws SearchSubmissionException
	{
		ASLSession session = getASLsession();
		session.setAttribute(SessionConstants.activePresentationQueryNo, new Integer(index));
		session.removeAttribute(SearchConstantsStrings.SESSION_RESULTS_NUMBER_PER_PAGE);
		//	logger.debug("Removing session variables for the results portlet..");
		//	ResultSetConsumer.removeSessionVariables(session);
		logger.info("Browse a previous query... The index of the query to render is: " + index);
		logger.info("Active presentation query is: " + ((Integer)session.getAttribute(SessionConstants.activePresentationQueryNo)).intValue());

		//TODO remove these messages
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getQuery(index);
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		logger.debug("The query description to show results is --> " + queryObj.getQueryDescription());
		//TODO is this ok?
		try {
			if (queryObj.getSearchType().equals(SearchType.SimpleSearch)) {
				logger.debug("simple search query passing true");
				queryObj.search(session, true,new SearchClientImpl());
			}
			else
				queryObj.search(session, false,new SearchClientImpl());
		} catch (Exception e) {
			handleSearchExceptions(e);
		}

	}

	/**
	 * This method submits a query that is saved in user's basket
	 * 
	 * @param The query that will be submitted, written in gCube's query language
	 * @param The type of search that this query represents
	 * @throws SearchSubmissionException 
	 */
	public void submitGenericQuery(String query, String type) throws SearchSubmissionException {
		long startTimeofSubmission, endTimeOfSubmission;

		startTimeofSubmission =  System.currentTimeMillis();
		logger.debug("Current time in seconds is: <<" + startTimeofSubmission/1000 + ">>");
		logger.debug("Generic Query will be submitted now.........");
		ASLSession session = getASLsession();
		String searchType = SearchType.SimpleSearch;
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		logger.debug("The ID of the query group is: " + shelper.getActiveQueryGroupNo());
		logger.debug("Submitting a generic search...");
		logger.debug("The query that will be submitted is: << " + query + " >>");
		session.setAttribute("CQL_QUERY", query);
		session.setAttribute(SessionConstants.activePresentationQueryNo, new Integer(shelper.getActiveQueryGroupNo()));
		logger.info("Active presentation query is: " + ((Integer)session.getAttribute(SessionConstants.activePresentationQueryNo)).intValue());
		session.removeAttribute(SearchConstantsStrings.SESSION_RESULTS_NUMBER_PER_PAGE);
		if (type.equals(SearchConstantsStrings.ADVANCED_SEARCH))
			searchType = SearchType.AdvancedSearch;
		else if (type.equals(SearchConstantsStrings.BROWSE_COLLECTION))
			searchType = SearchType.Browse;
		else if (type.equals(SearchConstantsStrings.GENERIC_SEARCH))
			searchType = SearchType.GenericSearch;
		logger.debug("The search type of the generic query is: << " + searchType + " >>");
		// This is set only when a search is done using queries that are saved in basket.
		// It is needed so as to identify the type of search and apply the correct transformation
		//queryObj.setGenericSearchType(searchType);
		queryObj.setSearchType(searchType);

		try {
			queryObj.submitCQLQuery(session, query,new SearchClientImpl());
		} catch (Exception e) {
			handleSearchExceptions(e);	
		}

		endTimeOfSubmission = System.currentTimeMillis();
		logger.debug(searchType + "Query submission ended. Current time in seconds is: <<" + endTimeOfSubmission/1000 + ">>");
		logger.debug("Total time needed for the submission of the query is: <<" + (endTimeOfSubmission -startTimeofSubmission)/1000 + ">> seconds");
		int id = shelper.createQuery(shelper.getActiveQueryGroupNo());
		shelper.setActiveQueryGroup(id);
	}

	public void submitQueryOnPreviousResult(ArrayList<SearchableFieldBean> criteria, String sortby, String sortOrder, String showRank) throws SearchSubmissionException
	{
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getQuery(((Integer)session.getAttribute(SearchConstantsStrings.SESSION_PREVIOUSQUERYINDEX)).intValue());
		Query queryObj = queryGroup.getQuery(DefaultQuery);

		logger.info("IN SUBMIT SEARCH FOR PREVIOUS QUERY");
		logger.info("The index of the previous query is: " + ((Integer)session.getAttribute(SearchConstantsStrings.SESSION_PREVIOUSQUERYINDEX)).intValue());


		logger.debug("PRINTING INFORMATION FOR THE CURRENT PREVIOUS SEARCH......");
		logger.debug("Search type is: " + queryObj.getSearchType());
		logger.debug("Criteria and values...");
		List<Criterion> tmp = queryObj.getCriteria();
		if (tmp!= null) {

			for (int t=0; t<tmp.size(); t++)
				logger.debug((t+1) + ") CRITERION: " + tmp.get(t).getSearchFieldName() + " VALUE: " + tmp.get(t).getSearchFieldValue());
		}

		logger.debug("The operator is: " + queryObj.getOperator());
		if (sortby != null)
			queryObj.setSortBy(sortby);
		if (sortOrder != null)
			queryObj.setOrder((sortOrder.toLowerCase().equals("asc"))? Order.ASC:Order.DESC);

		int numOfActiveCriteriaAtTheQuery = queryObj.getCriteria().size();
		if (criteria != null ) {
			for (int i=0; i<criteria.size(); i++) {
				// If the criteria are already set to the query then just be sure that they have the latest values.
				if (criteria.size() == numOfActiveCriteriaAtTheQuery) {
					queryObj.updateCriterionValue(i, criteria.get(i).getValue());
				}
				// If some of the criteria have not been added yet to the query. Add them now
				else if (criteria.size() > numOfActiveCriteriaAtTheQuery) {
					for (int j=numOfActiveCriteriaAtTheQuery; j<criteria.size(); j++)
						queryObj.addCriterion(new Criterion(criteria.get(j).getId(), criteria.get(j).getName(), criteria.get(j).getValue()));
				}
			}
		}

		session.setAttribute(SessionConstants.activePresentationQueryNo, (Integer)session.getAttribute(SearchConstantsStrings.SESSION_PREVIOUSQUERYINDEX));
		session.removeAttribute(SearchConstantsStrings.SESSION_RESULTS_NUMBER_PER_PAGE);

		try {
			queryObj.search(session, false,new SearchClientImpl());
		} catch (Exception e) {
			handleSearchExceptions(e);
		}
		logger.info("Active presentation query is: " + ((Integer)session.getAttribute(SessionConstants.activePresentationQueryNo)).intValue());
	}

	/**
	 * Submits a simple query
	 * 
	 *  @param fts The keyword to search for
	 * @throws SearchSubmissionException 
	 */
	public void submitSimpleQuery(String fts, boolean setRanking, boolean isSemanticSearch, boolean searchPerCollection) throws SearchSubmissionException
	{
		long startTimeofSubmission, endTimeOfSubmission;

		startTimeofSubmission =  System.currentTimeMillis();
		logger.debug("Current time in seconds is: <<" + startTimeofSubmission/1000 + ">>");
		logger.debug("Simple Query will be submitted now.........");
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		queryObj.setSearchTerm(fts);
		queryObj.setRanking(setRanking);
		queryObj.setSemanticEnrichment(isSemanticSearch);
		session.setAttribute(SearchConstantsStrings.SESSION_SIMPLE_TERM, fts);
		session.setAttribute(SearchConstantsStrings.SESSION_SEMANTIC_ENRICHMENT, isSemanticSearch);
		session.setAttribute(SearchConstantsStrings.SESSION_RANK, setRanking);
		session.setAttribute(SessionConstants.activePresentationQueryNo, new Integer(shelper.getActiveQueryGroupNo()));
		logger.info("Active presentation query is: " + ((Integer)session.getAttribute(SessionConstants.activePresentationQueryNo)).intValue());
		session.removeAttribute(SearchConstantsStrings.SESSION_RESULTS_NUMBER_PER_PAGE);

		try {
			// User wants to search per collection
			if (searchPerCollection) {
				// Find how many queries should be constructed
				// Get the real collections, without the groups
				int numOfQueries = queryObj.getSelectedRealCollections(session).size();
				Query newQuery = null;
				// add the new queries. There is already one constructed
				for(int i=0; i<numOfQueries-1; i++) {
					newQuery = queryObj.clone(false);	
					List<String> colToRemove = newQuery.getSelectedRealCollections(session);
					colToRemove.remove(i);
					newQuery.selectCollections(colToRemove, false, session, false, true);
					queryGroup.setQuery(newQuery);
				}
				// Remove all except the last collection from the initial query
				List<String> collectionsToRemove = queryObj.getSelectedRealCollections(session);
				collectionsToRemove.remove(queryObj.getSelectedRealCollections(session).get(numOfQueries-1));
				queryObj.selectCollections(collectionsToRemove, false, session, false, true);

				//It is time to submit all the queries!
				logger.info("STARTING PERFORMING SEARCH PER COLLECTION");
				logger.info("NUMBER OF QUERIES TO PERFORM IS: " + numOfQueries);
				for (int j=0; j<numOfQueries; j++) {
					logger.debug("for the " + (j+1) + "query, selected collections are:  " + queryGroup.getQuery(j).getSelectedRealCollections(session).size());
					try {
						queryGroup.getQuery(j).search(session, true, new SearchClientImpl());
					} catch (Exception e) {
						handleSearchExceptions(e);
					}
					logger.debug("for the " + (j+1) + "query, the description is: " + queryGroup.getQuery(j).getQueryDescription());
				}
			}
			else {
				logger.debug("QUERY description is: <<<<<<<<<" + queryObj.getQueryString() + ">>>>>>>>");
				try {
					queryObj.search(session, true,new SearchClientImpl());
				} catch (Exception e) {
					handleSearchExceptions(e);
				}
			}
		} catch (InitialBridgingNotCompleteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.gcube.application.framework.search.library.exception.InternalErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		endTimeOfSubmission = System.currentTimeMillis();

		logger.debug("Simple Query submission ended. Current time in seconds is: <<" + endTimeOfSubmission/1000 + ">>");
		logger.debug("Total time needed for the submission of the simple query is: <<" + (endTimeOfSubmission -startTimeofSubmission)/1000 + ">> seconds");
		int id = shelper.createQuery(shelper.getActiveQueryGroupNo());
		shelper.setActiveQueryGroup(id);
	}

	/**
	 * This method is used to update the name of the criterion, specified by the internalNo
	 * 
	 * @param internalNo the internal ID of the criterion to be changed
	 * @param newName the name to be set to the criterion
	 */
	public void updateCriterionName(int internalNo, String newID, String newName) {
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		queryObj.updateCriterionId(internalNo, newID);
		queryObj.updateCriterionName(internalNo, newName);
		logger.debug("Criterion with index: " + internalNo + "is updated with new name: " + newName  + " and new ID: " + newID + " in advanced search.");
	}

	/**
	 * This method is used to update the name of the criterion, specified by the internalNo in a previous Query
	 * 
	 * @param internalNo the internal ID of the criterion to be changed
	 * @param newName the name to be set to the criterion
	 */
	public void updateCriterionNameOnPreviousQuery(int internalNo, String newID, String newName) {
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getQuery(((Integer)session.getAttribute(SearchConstantsStrings.SESSION_PREVIOUSQUERYINDEX)).intValue());
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		queryObj.updateCriterionId(internalNo, newID);
		queryObj.updateCriterionName(internalNo, newName);
		logger.debug("Criterion with index: " + internalNo + "is updated with  new name: " + newName + " in previous search.");
	}

	/**
	 * This method is used to update the value of the criterion, specified by the internalNo
	 * 
	 * @param internalNo the internal ID of the criterion to be changed
	 * @param newValue the value to be set to the criterion
	 */
	public void updateCriterionValue(int internalNo, String newValue) {
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		queryObj.updateCriterionValue(internalNo, newValue);
		logger.debug("Criterion with index: " + internalNo + "is updated with new value: " + newValue + " in advanced search.");
	}

	/**
	 * This method is used to update the value of the criterion, specified by the internalNo in a previous Query
	 * 
	 * @param internalNo the internal ID of the criterion to be changed
	 * @param newValue the value to be set to the criterion
	 */
	public void updateCriterionValueOnPreviousQuery(int internalNo, String newValue) {
		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);

		logger.debug("The index of the previous query is: " + ((Integer)session.getAttribute(SearchConstantsStrings.SESSION_PREVIOUSQUERYINDEX)).intValue());
		logger.debug("The internal No of the criterion that will be updated is: " + internalNo);
		QueryGroup queryGroup = shelper.getQuery(((Integer)session.getAttribute(SearchConstantsStrings.SESSION_PREVIOUSQUERYINDEX)).intValue());
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		queryObj.updateCriterionValue(internalNo, newValue);
	}

	public Boolean isSemanticAvailableForCurrentScope() {
//		String fpath =	this.getServletContext().getRealPath("/") + "config/config.properties";
//
//		String value = FileUtils.getPropertyValue(fpath, getASLsession().getScopeName());
//		logger.debug("Value of property " + getASLsession().getScopeName() + " is -> " + value);
//		if (value != null && value.equalsIgnoreCase("true"))
//			return true;
//		return false;
		boolean found = false;
		ScopeProvider.instance.set(getASLsession().getScope());
		SimpleQuery query = queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceClass/text() eq 'WebApp'")
		.addCondition("$resource/Profile/ServiceName/text() eq 'XSearchService'");

		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);
		List<GCoreEndpoint> resources = client.submit(query);
		for (GCoreEndpoint se : resources) {
			if (se != null && se.profile() != null && se.profile().endpointMap() != null) {
				String status = se.profile().deploymentData().status();
				if (status.equalsIgnoreCase("ready")){
					found = true;
					logger.info("Found xSearch endpoint in scope -> " + getASLsession().getScope());
					break;
				}				
			}
		}
		return found;
	}

	public String readPropertyFromFile(String propertyName) {

		String fpath =	this.getServletContext().getRealPath("/") + "config/config.properties";

		logger.debug("PATTHH --> " + fpath);
		String value = FileUtils.getPropertyValue(fpath, propertyName);
		logger.debug("Value of property " + propertyName + " is -> " + value);
		return value;
	}

}
