package org.gcube.portlets.user.searchportlet.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.core.util.SessionConstants;
import org.gcube.application.framework.search.library.exception.InitialBridgingNotCompleteException;
import org.gcube.application.framework.search.library.impl.SearchHelper;
import org.gcube.application.framework.search.library.model.Query;
import org.gcube.application.framework.search.library.model.QueryGroup;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.searchportlet.client.SearchConstantsStrings;
import org.gcube.portlets.user.searchportlet.client.exceptions.CollectionRetrievalException;
import org.gcube.portlets.user.searchportlet.client.exceptions.NoCollectionsAvailableException;
import org.gcube.portlets.user.searchportlet.client.exceptions.SearchSubmissionException;
import org.gcube.portlets.user.searchportlet.client.interfaces.SearchService;
import org.gcube.portlets.user.searchportlet.shared.CollectionBean;
import org.gcube.portlets.user.searchportlet.shared.RecipientTypeConstants;
import org.gcube.portlets.user.searchportlet.shared.SearchAvailabilityType;
import org.gcube.portlets.user.searchportlet.shared.SearchTypeBean;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.servlet.ImageServletTokenUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Organization;
import com.liferay.portal.theme.ThemeDisplay;

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
	private static final String OPEN_SEARCH_TYPE = "opensearch";

	/**
	 * Init Method
	 */
	public void init(ServletConfig conf) throws ServletException {
		super.init(conf);
	}

	public String getLogoURL() {
		ThemeDisplay themeDisplay = (ThemeDisplay) this.getThreadLocalRequest().getSession().getAttribute(WebKeys.THEME_DISPLAY);
		Organization currOrg  = (Organization) this.getThreadLocalRequest().getSession().getAttribute("CURR_RE_NAME");
		if (currOrg != null) {
			long logoId = currOrg.getLogoId();
			String logoURL =  themeDisplay.getPathImage()+"/organization_logo?img_id="+ logoId +"&t" + ImageServletTokenUtil.getToken(logoId);
			return logoURL;
		}
		return null;
	}
	
	
	public HashMap<CollectionBean, ArrayList<CollectionBean>> getAvailableCollections() throws CollectionRetrievalException {
		logger.debug("Retrieving available collections tree..");
		ASLSession session = getASLsession();
		ArrayList<String> nativeCollectionsList = new ArrayList<String>();
		ArrayList<String> externalCollectionsList = new ArrayList<String>();
		
		SearchHelper shelper = new SearchHelper(session);
		HashMap<CollectionBean, ArrayList<CollectionBean>> collectionHierarchyBean = new HashMap<CollectionBean, ArrayList<CollectionBean>>();
		HashMap<org.gcube.application.framework.search.library.model.CollectionInfo, ArrayList<org.gcube.application.framework.search.library.model.CollectionInfo>> colInfo = null;
		try {
			colInfo = shelper.getAvailableFTSCollections();//getAvailableCollections();
		}catch (Exception e) {
			throw new CollectionRetrievalException();
		}

		if(colInfo != null)
		{
			Iterator<Entry<org.gcube.application.framework.search.library.model.CollectionInfo, ArrayList<org.gcube.application.framework.search.library.model.CollectionInfo>>> it = colInfo.entrySet().iterator();
			while (it.hasNext()) {
				Entry<org.gcube.application.framework.search.library.model.CollectionInfo, ArrayList<org.gcube.application.framework.search.library.model.CollectionInfo>> group = it.next();

				org.gcube.application.framework.search.library.model.CollectionInfo collectionGroup = group.getKey();
				CollectionBean colGroup = new CollectionBean(collectionGroup.getId(), collectionGroup.getName(), collectionGroup.getDescription(), collectionGroup.getRecno(), collectionGroup.getCreationDate(), null, false, true, false);

				ArrayList<org.gcube.application.framework.search.library.model.CollectionInfo> collectionsList = colInfo.get(collectionGroup);
				// for each collection of the current group
				if (collectionsList != null && !collectionsList.isEmpty()) {
					ArrayList<CollectionBean> collectionsBean = new ArrayList<CollectionBean>();
					for (org.gcube.application.framework.search.library.model.CollectionInfo col : collectionsList) {
						String type = col.getCollectionType();
						CollectionBean collection = new CollectionBean(col.getId(), col.getName(), col.getDescription(), col.getRecno(), col.getCreationDate(), type, false, true, true);
						collectionsBean.add(collection);

						if (type != null && type.equalsIgnoreCase(OPEN_SEARCH_TYPE)) {	
							externalCollectionsList.add(col.getId());
						}
						else
							nativeCollectionsList.add(col.getId());
					}
					collectionHierarchyBean.put(colGroup,collectionsBean);
				}
			}
		}
		else {
			throw new CollectionRetrievalException();
		}
		session.setAttribute(SearchConstantsStrings.SESSION_EXTERNAL_COLLECTIONS, externalCollectionsList);
		session.setAttribute(SearchConstantsStrings.SESSION_NATIVE_COLLECTIONS, nativeCollectionsList);
		
		return collectionHierarchyBean;

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

	public void setSelectedCollectionsToSession(HashSet<String> selectedCollections) {
		ASLSession session = getASLsession();
		session.setAttribute(SearchConstantsStrings.SESSION_SELECTEDCOLLECTIONS, selectedCollections);
	}

	public HashSet<String> getSelectedCollectionsFromSession() {
		ASLSession session = getASLsession();
		return (HashSet<String>)session.getAttribute(SearchConstantsStrings.SESSION_SELECTEDCOLLECTIONS);
	}
	
	public Boolean areExternalCollectionsAvailable() {
		ASLSession session = getASLsession();
		if (!((ArrayList<String>)session.getAttribute(SearchConstantsStrings.SESSION_EXTERNAL_COLLECTIONS)).isEmpty())
			return true;
		return false;
	}
	
	public Boolean areNativeCollectionsAvailable() {
		ASLSession session = getASLsession();
		if (!((ArrayList<String>)session.getAttribute(SearchConstantsStrings.SESSION_NATIVE_COLLECTIONS)).isEmpty())
			return true;
		return false;
	}

	private void handleSearchExceptions(Exception e) throws SearchSubmissionException {
		logger.error("An exception was thrown while submiting the query.", e);
		throw new SearchSubmissionException(e.getMessage());
	}

	public SearchTypeBean getSelectedSearchType() {
		return (SearchTypeBean)getASLsession().getAttribute(SearchConstantsStrings.SESSION_SEARCH_TYPE);
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

		} catch (org.gcube.application.framework.search.library.exception.InternalErrorException e) {

		}
		return SearchAvailabilityType.SEARCH_UNAVAILABLE;
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

	public String stackTraceAsString(Throwable caught) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		caught.printStackTrace(printWriter);
		return writer.toString();
	}

	/**
	 * Submits a simple query
	 * @throws NoCollectionsAvailableException 
	 * 
	 *  
	 */
	public void submitGenericQuery(String term, SearchTypeBean type, HashSet<String> selectedCollections) throws SearchSubmissionException, NoCollectionsAvailableException
	{
		long startTimeofSubmission, endTimeOfSubmission;

		startTimeofSubmission =  System.currentTimeMillis();
		logger.debug("Current time in seconds is: <<" + startTimeofSubmission/1000 + ">>");
		logger.debug("Query will be submitted now.........");

		ASLSession session = getASLsession();
		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DefaultQuery);
		queryObj.setSearchTerm(term);

		//keep these values for state
		session.setAttribute(SearchConstantsStrings.SESSION_SEARCH_TYPE, type);
		session.setAttribute(SearchConstantsStrings.SESSION_SIMPLE_TERM, term);
		session.setAttribute(SessionConstants.activePresentationQueryNo, new Integer(shelper.getActiveQueryGroupNo()));
		logger.debug("Active presentation query is: " + ((Integer)session.getAttribute(SessionConstants.activePresentationQueryNo)).intValue());

		if (type == SearchTypeBean.NATIVE) {
			ArrayList<String> nativeCollectionsList = (ArrayList<String>)session.getAttribute(SearchConstantsStrings.SESSION_NATIVE_COLLECTIONS);
			logger.debug("Going to search native collections only...");
			if (!nativeCollectionsList.isEmpty()) {
				queryObj.selectCollections(nativeCollectionsList, true, session, true);
				try {
					queryObj.search(session, true, new SearchClientImpl());
				} catch (Exception e) {
					handleSearchExceptions(e);
				}
			}
			else {
				throw new NoCollectionsAvailableException();
			}
		}
		else if (type == SearchTypeBean.EXTERNAL) {
			ArrayList<String> externalCollectionsList = (ArrayList<String>)session.getAttribute(SearchConstantsStrings.SESSION_EXTERNAL_COLLECTIONS);
			logger.debug("Going to search external collections only...");
			if (externalCollectionsList.size() > 0) {
				queryObj.selectCollections(externalCollectionsList, true, session, true);
				try {
					queryObj.search(session, true, new SearchClientImpl());
				} catch (Exception e) {
					handleSearchExceptions(e);
				}
			}
			else {
				throw new NoCollectionsAvailableException();
			}
		}
		else if (type == SearchTypeBean.GCUBESIMPLE) {
			logger.debug("Going to perform a simple search based on user's selected collections...");
			//select the collections to ASL and add here one more parameter so as to make asl clear the previous ones
			logger.debug("Going to change the selected selections. Replacing all existing ones...");
			for (String id : selectedCollections) {
				logger.debug("Adding to selected -> " + id);
			}
			queryObj.selectCollections(new ArrayList<String>(selectedCollections), true, session, true);
			try {
				queryObj.search(session, true, new SearchClientImpl());
			} catch (Exception e) {
				handleSearchExceptions(e);
			}
		}
		// have generic as default and as a fall back solution
		else {
			logger.debug("Going to perform a generic search on all the available collections...");
			try {
				ArrayList<String> terms = new ArrayList<String>();
				terms.add(term);
				queryObj.genericSearch(session, terms, true, new SearchClientImpl());
			} catch (Exception e) {
				handleSearchExceptions(e);
			}
		}

		endTimeOfSubmission = System.currentTimeMillis();
		logger.debug("Query submission ended. Current time in seconds is: <<" + endTimeOfSubmission/1000 + ">>");
		logger.debug("Total time needed for the submission of the simple query is: <<" + (endTimeOfSubmission -startTimeofSubmission)/1000 + ">> seconds");
		int id = shelper.createQuery(shelper.getActiveQueryGroupNo());
		shelper.setActiveQueryGroup(id);
	}

}
