package org.gcube.portlets.user.results.server.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.gcube.application.framework.contentmanagement.content.impl.DigitalObject;
import org.gcube.application.framework.contentmanagement.content.impl.DigitalObjectType;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.core.util.SessionConstants;
import org.gcube.application.framework.search.library.impl.ResultSetConsumer;
import org.gcube.application.framework.search.library.impl.SearchHelper;
import org.gcube.application.framework.search.library.interfaces.ResultSetConsumerI;
import org.gcube.application.framework.search.library.model.Criterion;
import org.gcube.application.framework.search.library.model.Query;
import org.gcube.application.framework.search.library.model.QueryGroup;
import org.gcube.application.framework.search.library.util.DisableButtons;
import org.gcube.application.framework.search.library.util.FindFieldsInfo;
import org.gcube.application.framework.search.library.util.SearchType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.results.client.ResultsetService;
import org.gcube.portlets.user.results.client.components.TreeNode;
import org.gcube.portlets.user.results.client.constants.StringConstants;
import org.gcube.portlets.user.results.client.model.BasketModelItem;
import org.gcube.portlets.user.results.client.model.BasketSerializable;
import org.gcube.portlets.user.results.client.model.Client_DigiObjectInfo;
import org.gcube.portlets.user.results.client.model.ResultNumber;
import org.gcube.portlets.user.results.client.model.ResultObj;
import org.gcube.portlets.user.results.client.model.ResultType;
import org.gcube.portlets.user.results.client.model.ResultsContainer;
import org.gcube.portlets.user.results.client.util.QueryDescriptor;
import org.gcube.portlets.user.results.client.util.QuerySearchType;
import org.gcube.portlets.user.results.shared.ContentInfo;
import org.gcube.portlets.user.results.shared.ContentType;
import org.gcube.portlets.user.results.shared.GenericTreeRecordBean;
import org.gcube.portlets.user.results.shared.OaiDCRecordBean;
import org.gcube.portlets.user.results.shared.ObjectType;
import org.gcube.portlets.user.results.shared.ResultRecord;
import org.gcube.portlets.user.results.shared.SearchableFieldBean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The <code> NewresultsetServiceImpl </code> Implementation of the services for the portlet 
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2012 (2.0) 
 */
public class NewresultsetServiceImpl extends RemoteServiceServlet implements ResultsetService {

	/** Logger */
	private static Logger _log = Logger.getLogger(NewresultsetServiceImpl.class);

	public static final String DEFAULT_BASKET_DIR = "My Default Basket";
	public static final String SESSION_DEFAULT_BASKET_DIR = "SessionResultsPortletBasket";
	public static final String WORKSPACE_AREA_ATTRIBUTE_NAME = "WORKSPACEAREA";
	public static final String CURRENT_OPEN_BASKET_ATTRIBUTE_NAME = "CURRENTBASKET";
	public static final String ANN_COLS_SCOPED_IDS_ATTRIBUTE_NAME = "SCOPED_ANNCOLS_IDS";
	public static final String CURRENT_QUERY = "CURRENTQUERY";
	public static final String CURRENT_RESULTS_NO = "CURRENT_RESULTS_NUMBER";

	private static final long serialVersionUID = -1619078127853728843L;

	public final static String RESULTS_PER_PAGE_ATTRIBUTE_NAME = "RESULTS_NUMBER_PER_PAGE";

	private static final int DEFAULTQNO = 0;

	/**
	 * used to avoid multiple IS request for the Storage EPR discovery, 
	 * if multiple request come  within 1 minute it uses the same EPR
	 */
	protected static Date lastCall;	
	/**
	 * used to avoid multiple IS request for the Storage EPR discovery, 
	 * if multiple request come  within 1 minute it uses the same EPR
	 */
	protected String SERVICE_URI = "";

	/**
	 * 
	 *
	 */
	public void decreaseStartingPoint() {
		Integer tmp = retrieveStartingPoint();
		int resultsPerPage = readResultsPerPageFromSession();
		tmp = new Integer(tmp.intValue() - resultsPerPage);
		if (tmp.intValue() > 0)
			storeStartingPoint(tmp);
	}

	/**
	 * Return a  List<BasketModelItem> given a basketid
	 * it uses the hobe library and store the read basket in the session
	 * 
	 */
	public List<BasketModelItem> getBasketContent(String basketId) {
		// TODO SLOW Performance. Check it
		// now it returns an empty list at first to work as a placeholder
		getASLSession().setAttribute("WORKSPACE.LAST_OPEN_BASKET", basketId);
		List<BasketModelItem> children = new LinkedList<BasketModelItem>();// NewresultsetServiceUtil.fillBasket((WorkspaceFolder) item);
		try {
			storeBasketInSession(new BasketSerializable(basketId, NewresultsetServiceUtil.getDefaultBasketWorkspaceFolder(this).getName(), NewresultsetServiceUtil.getDefaultBasketWorkspaceFolder(this).getPath(), children));
		} catch (InternalErrorException e) {
			e.printStackTrace();
		}
		_log.debug("Basket " + basketId + " stored in session");
		return children;
	}
	/**
	 *
	 * If you have search for each collection, you request the available collection names 
	 * in order to present them in the drop down menu.
	 */
	public String[] getCollectionNames() {
		if (StringConstants.DEBUG) {
			String[] toReturn = {"FARM TIME SERIES", "FAO MAPS TRAPS"};
			return toReturn;
		}
		else {
			try {
				_log.debug("getCollectionNames()");
				ASLSession session = getASLSession();
				int id = ((Integer) session.getAttribute(SessionConstants.activePresentationQueryNo)).intValue();
				SearchHelper sh = new SearchHelper(session);
				QueryGroup queries = sh.getQuery(id);
				String[] collections = new String[queries.getQueries().size()];
				int qid = DEFAULTQNO;
				if(session.getAttribute("QeuryIndexToPresent") != null)
				{
					qid = Integer.parseInt(  session.getAttribute("QeuryIndexToPresent").toString());
				}
				System.out.println("internal query no:" + qid);
				if(collections.length < 2)
					return null;
				else {
					for(int i = 0; i< collections.length; i++)	{
						Query q = queries.getQuery(i);
						String colId = q.getSelectedRealCollections(session).get(0);
						if(qid == i)
							//This is the selected (collection) results to be presented
							collections[i] = "selected:" + FindFieldsInfo.findCollectionInfo(colId, sh.getAvailableCollections()).getName();
						else
							collections[i] = FindFieldsInfo.findCollectionInfo(colId, sh.getAvailableCollections()).getName();
					}
					for (String col : collections) {
						_log.debug("COL: "  + col);
					}
					//
					return collections;
				}
			} catch (Exception e) {
				return null;
			}
		}

	}
	
	public Integer getCurrentQueryIndexNumber() {
		ASLSession session = getASLSession();
		Integer index = ((Integer)session.getAttribute(SessionConstants.activePresentationQueryNo));
		return index;
	}

	/**
	 * the current D4SSession
	 * @return .
	 */
	protected ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);

		_log.debug("Session ID is:" + sessionID + "*  user= *" + user + "*" );
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}

	/**
	 * 
	 * @param querDescription the query description
	 */
	private void storeQueryInSession(QueryDescriptor querDescription) {
		ASLSession session = getASLSession();
		session.setAttribute(CURRENT_QUERY, querDescription);
	}

	/**
	 * 
	 * @param querDescription the query description
	 */
	public QueryDescriptor getQueryDescFromSession() {
		ASLSession session = getASLSession();
		if ( session.getAttribute(CURRENT_QUERY) == null)
			return new QueryDescriptor("NOQUERY", null, null , "", "", QuerySearchType.BROWSE, "");
		return (QueryDescriptor) session.getAttribute(CURRENT_QUERY);
	}


	/**
	 * reads from the file system and returns the user workspace as TreeNode object
	 * 
	 * returns the Default basket if there is no basket in session, else the basket in session id
	 */
	public String getDefaultBasket() {
		_log.debug("getDefaultBasket()");
		// This means that the basket is already in session
		if (readBasketFromSession() != null)
			return "-1";
		else {
			try {
				//	Workspace workspaceArea = getWorkspaceArea();
				//TODO on 24
				//	WorkspaceFolder basket = workspaceArea.getRoot();
				WorkspaceFolder basket = NewresultsetServiceUtil.getDefaultBasketWorkspaceFolder(this);
				_log.debug("Get default basket returning basket with name -> " + basket.getName() + " ID -> " + basket.getId());
				//getASLSession().setAttribute("WORKSPACE.LAST_OPEN_BASKET",  basket.getId());
				return  basket.getId();
			} catch (InternalErrorException e) {
				e.printStackTrace();
			}
		}
		return "Could not open default basket";
	}

	/**
	 * used to get the additional info for the digital objects,
	 * @param currPosition in resultset
	 * object's mime type
	 * object's length (in bytes)
	 * amount of available annotations 
	 */
	@SuppressWarnings("unchecked")
	public Client_DigiObjectInfo getDigitalObjectInitialInfo(int currPosition) {

		ASLSession session = getASLSession();
		Vector<ResultObj> res = (Vector<ResultObj>) session.getAttribute(SessionConstants.theResultObjects);

		if(res == null)
			return null;

		int resultsPerPage = readResultsPerPageFromSession();
		int index = ((currPosition % resultsPerPage) == 0) ? resultsPerPage-1 : (currPosition % resultsPerPage) -1;
		ResultObj tmp = res.get(currPosition-1);
		String oURI = tmp.getObjectURI();
		String collectionID = tmp.getCollectionID();

		if (tmp.getObjectURI().startsWith("http://") && tmp.getObjectURI().contains("/tree/"))
			return new Client_DigiObjectInfo(oURI, oURI, -1, "text/xml", collectionID);
		else if (tmp.getObjectURI().startsWith("http://"))
			return new Client_DigiObjectInfo(oURI, oURI, -1, "text/url", collectionID);
		else
			return new Client_DigiObjectInfo(oURI, oURI, -1, null, collectionID);
	}


	/** 
	 * @return the first results
	 */
	private List<DigitalObject> getFirst(ResultSetConsumerI resConsumer) {
		DisableButtons dButtons = new DisableButtons();
		ASLSession session = getASLSession();
		storeStartingPoint(new Integer(1));
		int resultsPerPage = readResultsPerPageFromSession();

		List<DigitalObject> rsb;
		try {
			rsb = resConsumer.getFirst(resultsPerPage, dButtons, session);
		} catch (Exception e) {
			_log.error("Failed to retrieve the search results.", e);
			throw new NullPointerException();

		}

		_log.debug("back = "+((retrieveStartingPoint() > resultsPerPage) ? "TRUE" : "FALSE"));
		_log.debug("forward = "+(!dButtons.getForward()?"TRUE":"FALSE"));

		session.setAttribute("back", (retrieveStartingPoint() > resultsPerPage) ? "TRUE" : "FALSE");
		session.setAttribute("forward", (!dButtons.getForward()?"TRUE":"FALSE"));
		return rsb;

	}

	/**
	 * @return the next RESULTS_NO_PER_PAGE results
	 */
	private List<DigitalObject> getNext(ResultSetConsumerI resConsumer) {
		DisableButtons dButtons = new DisableButtons();
		ASLSession session = getASLSession();
		int resultsPerPage = readResultsPerPageFromSession();
		List<DigitalObject> rsb;
		try {
			rsb = resConsumer.getNext(resultsPerPage, dButtons, session);
		} catch (Exception e) {
			rsb = new ArrayList<DigitalObject>();
			e.printStackTrace();
		}
		increaseStartingPoint();
		_log.debug("back = "+((retrieveStartingPoint() > resultsPerPage) ? "TRUE" : "FALSE"));
		_log.debug("forward = "+(!dButtons.getForward()?"TRUE":"FALSE"));
		session.setAttribute("back", (retrieveStartingPoint() > resultsPerPage) ? "TRUE" : "FALSE");
		session.setAttribute("forward", (!dButtons.getForward()?"TRUE":"FALSE"));

		String extraText = (resConsumer.getTotalRead()) ? "" : " and counting";
		session.setAttribute(NewresultsetServiceImpl.CURRENT_RESULTS_NO, new ResultNumber(true, resConsumer.getNumOfResultsRead(), extraText));
		return rsb;
	}
	/**
	 * @param sameResults (if true returns the same RESULTS_NO_PER_PAGE results)
	 * @return results
	 */
	private List<DigitalObject> getPrevious(ResultSetConsumerI resConsumer, boolean sameResults) {

		DisableButtons dButtons = new DisableButtons();
		ASLSession session = getASLSession();
		int resultsPerPage = readResultsPerPageFromSession();
		List<DigitalObject> rsb;
		try {
			rsb = resConsumer.getPrevious(resultsPerPage, dButtons, session);
		} catch (Exception e) {
			rsb = new ArrayList<DigitalObject>();
			e.printStackTrace();
		}
		decreaseStartingPoint();
		_log.debug("back = "+((retrieveStartingPoint() > resultsPerPage) ? "TRUE" : "FALSE"));
		_log.debug("forward = "+(!dButtons.getForward()?"TRUE":"FALSE"));
		session.setAttribute("back", (retrieveStartingPoint() > resultsPerPage) ? "TRUE" : "FALSE");
		session.setAttribute("forward", (!dButtons.getForward()?"TRUE":"FALSE"));

		return rsb;
	}




	/** 
	 * Queries the search service and get the results
	 * 
	 * @param mode if mode == 0 get the first record of the resultset, mode == 1 get the previous, mode = 2 get the next
	 * 
	 */
	@SuppressWarnings("unchecked")
	public ResultsContainer getResultFromSearchService(int mode) {
		_log.info("called getResultFromSearchService()...");
		QueryDescriptor queryToSave = null;
		if (StringConstants.DEBUG)
			return null;
		else {
			//Retrieving the active ResultSetConsumer based on search type, and query-group and query ids :-)
			ASLSession session = getASLSession();
			SearchHelper searchH = new SearchHelper(session);
			//if null no one has performed a search
			int qgid = ((Integer)session.getAttribute(SessionConstants.activePresentationQueryNo)).intValue();
			_log.info("Active presentation query number is -> " + qgid);
			QueryGroup queries = searchH.getQuery(qgid);
			int qid = DEFAULTQNO;
			if(session.getAttribute("QeuryIndexToPresent") != null) {
				qid = Integer.parseInt(  session.getAttribute("QeuryIndexToPresent").toString());
			}
			Query q = queries.getQuery(qid);

			String term = "";
			term = q.getQueryDescription();

			//store the query in the session
			queryToSave = new QueryDescriptor(term);
			QuerySearchType searchType = getSearchQueryType(q);
			queryToSave.setType(searchType);
			if (searchType == QuerySearchType.SIMPLE)
				queryToSave.setSimpleTerm(q.getSearchTerm());
			else if (searchType == QuerySearchType.BROWSE || searchType == QuerySearchType.BROWSE_FIELDS)
				queryToSave.setBrowseBy(q.getBrowseByFieldName());
			else if (searchType == QuerySearchType.ADVANCED) {
				List<Criterion> criteria = q.getCriteria();
				if(criteria != null && !criteria.isEmpty())
				{
					ArrayList<SearchableFieldBean> selectedCriteriaBean = new ArrayList<SearchableFieldBean>();
					for(Criterion c : criteria)
						selectedCriteriaBean.add(new SearchableFieldBean(c.getSearchFieldName(), c.getSearchFieldValue()));
					queryToSave.setAdvancedFields(selectedCriteriaBean);	
				}
			}
			
			//This case covers the submission of a CQL directly. It should ignore the Query object's properties
			else if (searchType == QuerySearchType.GENERIC && session.getAttribute("CQL_QUERY") != null) {
				queryToSave.setType(QuerySearchType.CQL_QUERY);
				String actualQuery = (String)(session.getAttribute("CQL_QUERY"));
				queryToSave.setSimpleTerm(actualQuery);
				queryToSave.setDescription(actualQuery);
				session.removeAttribute("CQL_QUERY");
			}
			else
				queryToSave.setSimpleTerm(q.getSearchTerm());

			try {
				queryToSave.setSelectedCollections(q.getSelectedCollectionNames(getASLSession()));
			} catch (Exception e1) {

			}
			_log.debug("******************   STORING quer desc in the session:\n 	term = q.getTerm();" + term + " TYPE:" + searchType) ;
			//Giota added the CQL query here to the description
			queryToSave.setDescription(q.getQueryString());
			if (queryToSave.getDescription() == null)
				queryToSave.setDescription("no description");
			storeQueryInSession(queryToSave);

			ResultSetConsumerI resConsumer;

			resConsumer = q.getSearchResults(session);
			_log.debug("user asked for search");


			//Retrieving the results (as Digital Objects).
			_log.debug("get Result was called and mode=" + mode);

			//ASL FAKE RESULTS	ResultSetConsumerI resConsumer = new ResultSetConsumer("", "advancedSearch");

			List<DigitalObject> digitalObjectsList = null;
			try {
				if(mode == 0)
					digitalObjectsList =  getFirst(resConsumer);
				else if(mode == 1)
					digitalObjectsList =  getPrevious(resConsumer, false);
				else if(mode == 2)
					digitalObjectsList = getNext(resConsumer);
				else
					digitalObjectsList = getFirst(resConsumer);
			}			
			catch (NullPointerException e ) {
				String[] options = new String[1];
				String exception = getASLSession().getAttribute(SessionConstants.searchException).toString();
				if (exception.contains("SocketTimeoutException"))
					exception = "The system is overloaded, please try again in few minutes";
				options[0] = exception;
				_log.error("RETURNING ERROR: for" + options[0]);
				return new ResultsContainer(null, options, ResultType.ERROR, new HashMap<String, String>());
			}
			//this session variable holds the last records to show
			session.setAttribute("ResultSet_LastResults", digitalObjectsList);

			//get the resultObjects from the session (if any)
			Vector<ResultObj> allResultsContainer = (Vector<ResultObj>) session.getAttribute("theResultObjects");

			//if they dont exist, instances itDigitalObject
			if(allResultsContainer == null)
				allResultsContainer = new Vector<ResultObj>();

			//adds the new results only when they need to be fetched from resultset consumer 
			if (allResultsContainer.size() < retrieveStartingPoint()) {
				for(DigitalObject dilObj: digitalObjectsList)	{
					/*
					 * ResultObj is gwt serializable, I return a Vector<ResultObj> constructed by using a DigitalObject list
					 */			
					ResultObj res = new ResultObj();
					res.setObjectURI(dilObj.getURI());
					res.setTitle(dilObj.getTitle());
					res.setHtmlText(dilObj.getHTMLrepresentation());
					res.setCurrUserName(getASLSession().getUsername());
					res.setCollectionID(dilObj.getCollectionID());
					res.setCollectionName(dilObj.getCollectionName());

					_log.debug("Title" + res.getTitle() + " User " + res.getCurrUserName());

					ResultRecord resultRec = new ResultRecord(dilObj.getObjectId(),dilObj.getTitle(), "dc", session.getScopeName(),"" , dilObj.getCollectionID(), res.getHtmlText(), "", dilObj.getRank());
					res.setResultRec(resultRec);
					allResultsContainer.add(res);
				}
			}

			/**
			 * no results were found
			 */
			_log.debug("Counting Results....");
			if (allResultsContainer.size() == 0) {
				String[] options = new String[1];
				options[0] = NewresultsetServiceUtil.getDisplayableQuery(queryToSave);
				_log.debug("***********************************************RETURNING Counting Results.... -> 0 " + options[0]);
				return new ResultsContainer(null, options, ResultType.NO_RESULTS, new HashMap<String, String>());
			}

			//			******			
			String[] options = new String[7];
			options[0] = "1";
			if (session.getAttribute(SessionConstants.startingPoint) != null) {
				options[0] = session.getAttribute(SessionConstants.startingPoint).toString();
			}
			options[1] = session.getAttribute("back").toString();
			options[2] = session.getAttribute("forward").toString();
			options[3] = ""+readResultsPerPageFromSession();
			options[4] = "";		

			//			write the resultObjects in the session
			session.setAttribute("theResultObjects", allResultsContainer);


			_log.debug("end of getResult(), starting point=" + options[0] + 
					" theResultObjects size in session: " + allResultsContainer.size());
			_log.debug("back: " + options[1]);
			_log.debug("next: " + options[2]);


			ResultSetConsumer actualConsumer = (ResultSetConsumer) resConsumer;

			//if is a browse field search i need to communicate the client to
			//treat these results differently, using another class to display then options[5] == 0 means that

			options[5] = q.getSearchType().equals(SearchType.BrowseFields ) ? "false" : "true";

			if (options[0].equals("1")) { //the is the first time, need to count the results
				startResultsCounter(queryToSave, actualConsumer, session, q);
				options[4] = " .. counting ";		
			}			
			options[6] = NewresultsetServiceUtil.getDisplayableQuery(queryToSave);
			String extraText = (resConsumer.getTotalRead()) ? "" : " and counting";
			session.setAttribute(NewresultsetServiceImpl.CURRENT_RESULTS_NO, new ResultNumber(true, resConsumer.getNumOfResultsRead(), extraText));

			//ResultsContainer toReturn = new ResultsContainer(allResultsContainer, options, ResultType.RESULTS, getExternalLinks());
			ResultsContainer toReturn = new ResultsContainer(allResultsContainer, options, ResultType.RESULTS, new HashMap<String, String>());
			return toReturn;

		}
	}

	public String getObjectsPayload(String objectURI) {
		try {
			_log.debug("Trying to get the payload of the object with URI --> " + objectURI);
			String content = DigitalObject.getContent(objectURI, getASLSession().getScopeName());
			_log.debug("Payload -------------------------------------->>>>>>>>>>>>>>>>>>\n" + content);
			return content;
		} catch (Exception e) {
			return null;
		}
	}

	public GenericTreeRecordBean getObjectInfo(String objectURI) {
		GenericTreeRecordBean recordBean = null;
		try {
			ObjectType type;
			String payload;

			DigitalObject digobj = new DigitalObject(getASLSession(), objectURI);
			payload = digobj.getContent();
			DigitalObjectType digtype = digobj.getType();
			if (digtype.equals(DigitalObjectType.OAI)) {
				type = ObjectType.OAI;
				recordBean = new OaiDCRecordBean(type, payload);
			}
			else if (digtype.equals(DigitalObjectType.SPD)) {
				type = ObjectType.SPD;
				recordBean = new GenericTreeRecordBean(type, payload);
			}
			else if (digtype.equals(DigitalObjectType.FIGIS)) {
				type = ObjectType.FIGIS;
				recordBean = new GenericTreeRecordBean(type, payload);
			}
			else {
				type = ObjectType.GENERIC;
				recordBean = new GenericTreeRecordBean(type, payload);
			}
			_log.debug("Payload of this object is --> " + payload);
			_log.debug("Type of this object is --> " + type);	
		} catch (Exception e) {
			return null;
		}
		return recordBean;
	}

	public TreeMap<String, List<String>> getContentURLs(GenericTreeRecordBean recordBean) {
		TreeMap<String, List<String>> contentURLs = null;
		String payload = recordBean.getPayload();
		if (recordBean.getType() == ObjectType.OAI) {
			contentURLs = parseOAIDCPayload(payload);
		}
		else if (recordBean.getType() == ObjectType.FIGIS) {
			contentURLs = parseFIGISPayload(payload); 
		}
		return contentURLs;
	}

	private TreeMap<String, List<String>> parseOAIDCPayload(String payload) {
		TreeMap<String, List<String>> contentURLs = new TreeMap<String, List<String>>();
		List<String> mainURLs = new ArrayList<String>();
		List<String> altURLs = new ArrayList<String>();
		try {
			Document doc = parseXMLFileToDOM(payload);
			NodeList list = doc.getElementsByTagName("content");
			if (list != null) {
				for(int i=0; i < list.getLength(); i++ ) {
					Element contentNode = (Element)list.item(i);
					String contentType = "";
					String contentURL = "";
					NodeList ctList = contentNode.getElementsByTagName("contentType");
					if(ctList != null && ctList.getLength() > 0) {
						Element el = (Element)ctList.item(0);
						contentType = el.getFirstChild().getNodeValue();
					}
					NodeList urlList = contentNode.getElementsByTagName("url");
					if(urlList != null && urlList.getLength() > 0) {
						Element el = (Element)urlList.item(0);
						contentURL = el.getFirstChild().getNodeValue();
					}
					_log.debug("CONTENT URL: TYPE --- URL -> " + contentType + " --- " + contentURL);
					if (contentType.equalsIgnoreCase("main"))
						mainURLs.add(contentURL);
					else
						altURLs.add(contentURL);
				}
				contentURLs.put(ContentType.MAIN.toString(), mainURLs);
				contentURLs.put(ContentType.ALTERNATIVE.toString(), altURLs);
			}
			else
				return null;
		} catch (Exception e) {
			return null;
		}
		return contentURLs;
	}

	private TreeMap<String, List<String>> parseFIGISPayload(String payload) {
		TreeMap<String, List<String>> contentURLs = new TreeMap<String, List<String>>();
		List<String> mainURLs = new ArrayList<String>();
		try {
			Document doc = parseXMLFileToDOM(payload);
			NodeList list = doc.getElementsByTagName("factsheet_url");
			if (list != null) {
				for(int i=0; i < list.getLength(); i++ ) {
					Element el = (Element)list.item(i);
					String contentURL = el.getFirstChild().getNodeValue();
					mainURLs.add(contentURL);
				}
				contentURLs.put(ContentType.MAIN.toString(), mainURLs);
			}
			else
				return null;
		} catch (Exception e) {
			return null;
		}
		return contentURLs;
	}

	private static Document parseXMLFileToDOM(String XMLdoc) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbFactory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(XMLdoc)));
		return doc;
	}


	/**
	 * 
	 * @param q
	 * @return
	 */
	private QuerySearchType getSearchQueryType(Query q) {
		String searchType = q.getSearchType();
		if (searchType.equals(SearchType.SimpleSearch))
			return QuerySearchType.SIMPLE;
		else if (searchType.equals(SearchType.AdvancedSearch))
			return QuerySearchType.ADVANCED;
		else if (searchType.equals(SearchType.Browse))
			return QuerySearchType.BROWSE;
		else if (searchType.equals(SearchType.BrowseFields))
			return QuerySearchType.BROWSE_FIELDS;
		else 
			return QuerySearchType.GENERIC;
	}

	/**
	 * starts the thread for counting the results
	 * @param consumer
	 * @param session
	 */
	private void startResultsCounter(QueryDescriptor query, ResultSetConsumer consumer, ASLSession session, Query q) {
		boolean startThread = false;
		QueryDescriptor lastQuery = null;
		if (session.getAttribute("CURRQUERY") == null) {
			session.setAttribute("CURRQUERY", query);
			lastQuery = query;
			startThread = true;
			for (int i = 0; i < 10; i++) 
				_log.debug("--------------------------------------Query not in session start thread");
		}
		else { 
			try {
				lastQuery = (QueryDescriptor) session.getAttribute("CURRQUERY"); 
			}
			catch (Exception e) {
				lastQuery = new QueryDescriptor();
			}

			if (! lastQuery.getDescription().equals(query.getDescription())) {
				startThread = true;
				session.setAttribute("CURRQUERY", query);
				for (int i = 0; i < 10; i++) 
					_log.debug("-------------------------------------Query is NOT THE SAME starting thread thread");
			}
			else
				for (int i = 0; i < 10; i++) 
					_log.debug("-------------------------------------Query is THE SAME no thread");

			_log.debug("Query: \n" + lastQuery.getDescription() + "\nvs.\n" + query.getDescription());
		}
		if (startThread) {	


		}
	}

	/**
	 * reads the records from the session
	 * @return A <code>ResultsContainer</code> (Vector<ResultObj>, Optional paramters)  which contains all the information 
	 */
	@SuppressWarnings("unchecked")
	public ResultsContainer getResultsFromSession()
	{
		_log.debug("retrieveResults() ");

		ASLSession session = getASLSession();
		if ( session.getAttribute(SessionConstants.theResultObjects) == null)
			return null;

		Vector<ResultObj> res = (Vector<ResultObj>) session.getAttribute(SessionConstants.theResultObjects);


		String[] startingPoint = { "1" };
		if (session.getAttribute(SessionConstants.startingPoint) != null) {
			startingPoint[0] = (String) session.getAttribute(SessionConstants.startingPoint);
		}

		ResultsContainer resContainer = new ResultsContainer(res, startingPoint, ResultType.RESULTS, null);
		return resContainer;
	}

	/**
	 * 
	 * @return an instance of the user WorkspaceArea
	 * @throws HomeNotFoundException 
	 * @throws InternalErrorException 
	 * @throws WorkspaceFolderNotFoundException 
	 * @throws WorkspaceNotFoundException
	 * @throws InternalErrorException
	 * @throws HomeNotFoundException
	 */
	protected Workspace getWorkspaceArea() throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException 	{
		return HomeLibrary.getUserWorkspace(getASLSession().getUsername());	
	}


	/**
	 * reads from the file system and returns the user workspace as TreeNode object
	 */
	public TreeNode getWorkspaceTree() {
		//TODO slow performance
		WorkspaceFolder basket = null;
		try {
			basket = NewresultsetServiceUtil.getDefaultBasketWorkspaceFolder(this);
			return NewresultsetServiceUtil.fillWorkspaceTree(basket);
		} catch (InternalErrorException e) {e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 *
	 */
	public void increaseStartingPoint() {
		if (retrieveStartingPoint() == null)
			storeStartingPoint(new Integer(1));
		Integer tmp = retrieveStartingPoint();
		int resultsPerPage = readResultsPerPageFromSession();
		tmp = new Integer(tmp.intValue() + resultsPerPage);
		storeStartingPoint(tmp);
	}
	/**
	 * {@inheritDoc}
	 */
	public void init() {
	}


	/**
	 * Check whether the porlet has been loaded after a search or not
	 * @return true if the user performed a search, false otherwise
	 */
	public boolean isSearchActive() {
		if (StringConstants.DEBUG)
			return true;
		else {
			//Retrieving the active ResultSetConsumer based on search type, and query-group and query ids :-)
			ASLSession session = getASLSession();

			if (getCollectionNames() == null) {
				_log.debug("getCollectionNames null");
			} else {
				String[] cols = getCollectionNames();
				for (int i = 0; i < cols.length; i++) 
					_log.debug(cols[i]);			
			}
			boolean active = (session.getAttribute(SessionConstants.activePresentationQueryNo) != null);
			_log.debug("isSearchActive() ? " + active);
			_log.debug("session.getAttribute(SessionConstants.activePresentationQueryNo= " + 
					session.getAttribute(SessionConstants.activePresentationQueryNo));
			return active;
		}		
	}
	/**
	 * Changes the internal query (in the query group) to be presented - based on collection name
	 * @param qid .
	 */
	public void loadResults(String qid) {
		//Changes the internal query (in the query group) to be presented - based on collection name
		ASLSession session = getASLSession();
		ResultSetConsumer.removeSessionVariables(session);
		session.setAttribute("QeuryIndexToPresent", qid);
		_log.debug("loadResults()");
	}

	/**
	 * need to get the saved elements from HL and the new elements from the session
	 * @param basket
	 */

	public BasketSerializable readBasketFromSession() {
		BasketSerializable toReturn = null;
		try {
			ASLSession session = getASLSession();
			toReturn = (BasketSerializable) session.getAttribute(CURRENT_OPEN_BASKET_ATTRIBUTE_NAME);
			if (toReturn != null) {
				String basketId = toReturn.getId();
				List<BasketModelItem> currItems = toReturn.getItems();
				//TODO get only the current items from the session do not get the old ones !
				List<BasketModelItem> savedItems = getBasketContent(basketId);
				//List<BasketModelItem> savedItems = new ArrayList<BasketModelItem>();
				for (BasketModelItem item : currItems) 	
					if (item.isNew()) {
						_log.debug("New Item: " + item.getName());
						savedItems.add(item);
					}

				toReturn.setItems(savedItems);
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return toReturn; 
	}


	/**
	 * returns the number of results per page  read from the session for browsing
	 * @return
	 */
	private int readResultsPerPageFromSession() {
		ASLSession session = getASLSession();
		if (session.getAttribute(RESULTS_PER_PAGE_ATTRIBUTE_NAME) == null)
			return 10;
		else
			return ((Integer) session.getAttribute(RESULTS_PER_PAGE_ATTRIBUTE_NAME)).intValue();		
	}

	/**
	 * remove a single item in session
	 * @param item
	 * TODO: check if any error
	 */
	public Boolean removeBasketItemFromSession(BasketModelItem item) {
		BasketSerializable basket = readBasketFromSession();
		if (basket != null) {
			List<BasketModelItem> items = basket.getItems();
			if (items == null)
				return true;
			boolean removeResult = false;
			for (BasketModelItem itemToCheck : items) {
				if (item.getUri() != null && itemToCheck.getUri().equals(item.getUri())) {
					removeResult = items.remove(itemToCheck);
					break;
				}
				else if (item.getSearchType() != null && item.getDescription().equals(itemToCheck.getDescription())) {
					removeResult = items.remove(itemToCheck);
					break;
				}
			}
			storeBasketInSession(basket);
			_log.debug("Item Removed from session " + (item.getUri()));
			return new Boolean(removeResult);
		}
		return true;
	}

	/** 
	 * From where in the results to start presenting
	 */
	public Integer retrieveStartingPoint() {
		_log.debug("retrieveStartingPoint()");
		ASLSession session = getASLSession();
		if (session.getAttribute(SessionConstants.startingPoint) == null)
			return new Integer(1);
		return (Integer)session.getAttribute(SessionConstants.startingPoint);		
	}


	/**
	 * Calls the home library to save the current basket in session permanently	 * 
	 *
	 */
	public boolean saveBasket() {
		return NewresultsetServiceUtil.saveBasket(this);
	}

	/**
	 * save the manifestation in default basket, overwrites if the file name exists already
	 * 
	 * @param name the name in the Workspace
	 * @param desc the desc 
	 * @param mimeType its mimetype
	 * @param payLoad a File instance 
	 */
	//	private boolean saveInWorkSpace(String name, String desc, String mimeType, File payLoad) {
	//		try {
	//
	//			// Read the pdf input stream
	//			InputStream inputStream = new BufferedInputStream(new FileInputStream(payLoad));
	//			Workspace wp = getWorkspaceArea();
	//			WorkspaceFolder toSaveIn = wp.getRoot();
	//
	//			if (toSaveIn.exists(name)) {
	//				_log.debug("Item exists already, deleting and creating new one");
	//				toSaveIn.removeChild(toSaveIn.find(name));
	//			}
	//
	//			wp.createExternalFile(name, name, mimeType, inputStream, toSaveIn.getId());
	//			return true;
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//			return false;
	//		} 
	//
	//	}


	/**
	 * 
	 * @param basket
	 */
	protected void storeBasketInSession(BasketSerializable basket) {
		ASLSession session = getASLSession();
		session.setAttribute(CURRENT_OPEN_BASKET_ATTRIBUTE_NAME, basket);
	}

	/**
	 * add a single item in session
	 * @param item
	 * TODO: check if any error
	 */
	public Boolean storeBasketItemInSession(BasketModelItem item) {
		BasketSerializable basket = readBasketFromSession();

		if (basket != null) {
			List<BasketModelItem> items = basket.getItems();

			if (items == null) {
				_log.debug("Items in session for current basket are null");
				items = new LinkedList<BasketModelItem>();
			}
			for (BasketModelItem i : items)
				_log.debug("Previous item in basket in session -> " + i.getUri());

			items.add(item);
			_log.debug("Item stored in session: NAME/URI" + item.getName() + " / " + item.getUri());
			_log.debug("Number of Items: " + items.size());
		}
		storeBasketInSession(basket);
		for (BasketModelItem it : basket.getItems())
			_log.debug("Item -> " + it.getUri());
		return new Boolean(true);
	}


	/**
	 *  
	 * From where in the results to start presenting
	 */
	public void storeStartingPoint(Integer start)	{
		ASLSession session = getASLSession();
		session.setAttribute("startingPoint", start);		
	}

	/**
	 * @return a ResultNumber instance containing the num of results found so far
	 *  plus a boolean that say if is still counting or not
	 */
	public ResultNumber getResultsNo() {
		ASLSession session = getASLSession();

		if (session.getAttribute(NewresultsetServiceImpl.CURRENT_RESULTS_NO) == null)
			return new ResultNumber(false, 0, "");

		return (ResultNumber) session.getAttribute(NewresultsetServiceImpl.CURRENT_RESULTS_NO);
	}

	/**
	 * 
	 * @param queryTerm the query term to search
	 */
	public void submitSimpleQuery(String queryTerm) {
		ASLSession session = getASLSession();

		_log.debug("submitSimpleQuery: " + getQueryDescFromSession().getBrowseBy());

		SearchHelper shelper = new SearchHelper(session);
		QueryGroup queryGroup = shelper.getActiveQueryGroup();
		Query queryObj = queryGroup.getQuery(DEFAULTQNO);
		//queryObj.setSortBy(getQueryDescFromSession().getSortBy());
		//queryObj.setDistinct(false);
		queryObj.setSearchTerm("\"" + queryTerm + "\"");
		_log.debug("\n**** setDistinct false ");
		session.setAttribute("browseFieldResult", new Boolean(true));
		session.setAttribute("sortByValue", queryTerm);
		session.setAttribute(SessionConstants.activePresentationQueryNo, new Integer(shelper.getActiveQueryGroupNo()));
		_log.debug("CALLING BROWSE, sortByValue:" + queryTerm);
		session.removeAttribute(RESULTS_PER_PAGE_ATTRIBUTE_NAME);

		try {
			//queryObj.browse(session);
			queryObj.search(session, true, new SearchClientImpl());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		int id = shelper.createQuery(shelper.getActiveQueryGroupNo());
		shelper.setActiveQueryGroup(id);

	}

	protected String getRealPath() {
		return this.getServletContext().getRealPath("");
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	protected String getXSLT(ObjectType type) throws IOException {
		// Check more types here
		String xslt;
		if (type == ObjectType.OAI)
			xslt = "/oaidc_xslt.xsl";
		else
			xslt = "/figis_xslt.xsl";

		FileInputStream fis = new FileInputStream(getRealPath() + xslt);
		InputStreamReader isr = new InputStreamReader(fis);

		BufferedReader filebuf = null;
		String nextStr = null;
		String toReturn = new String();
		try {
			filebuf = new BufferedReader(isr);
			nextStr = filebuf.readLine(); 
			while (nextStr != null) {
				toReturn += nextStr ;
				nextStr = filebuf.readLine(); 
			}
			filebuf.close(); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return toReturn;
	}

	public String transformMetadata(String payload, ObjectType type) {
		try {
			String xslt = getXSLT(type);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer tr = tf.newTransformer(new StreamSource(new ByteArrayInputStream(xslt.getBytes())));
			StringWriter strWriter = new StringWriter();
			tr.transform(new StreamSource(new StringReader(payload)), new StreamResult(strWriter));
			String transformed = strWriter.toString();
			return transformed;
		} catch (Exception e) {
			_log.debug("Failed to transform the metadata of the OAI object", e);
			return null;
		}
	}

	public ContentInfo getContentToSave(String objectURI) {
		URL url = createURLForMainContent(objectURI);
		if (url != null) {
			ContentInfo ci = getActualContentForObject(url);
			return ci;
		}
		return null;
	}

	private URL createURLForMainContent(String objectURI) {
		URL url = null;
		DigitalObject digobj = new DigitalObject(getASLSession(), objectURI);
		ObjectType type = ObjectType.GENERIC;
		DigitalObjectType digtype = digobj.getType();
		if (digtype.equals(DigitalObjectType.OAI)) {
			type = ObjectType.OAI;
		}
		//	else if (digtype.equals(DigitalObjectType.SPD)) {
		//		type = ObjectType.SPD;
		//	}
		else if (digtype.equals(DigitalObjectType.FIGIS)) {
			type = ObjectType.FIGIS;
		}
		if (type != ObjectType.GENERIC) {

			HttpServletRequest request = this.getThreadLocalRequest();
			String file = "/aslHttpContentAccess/ContentViewer;jsessionid=" + 
					request.getSession().getId() + "?username=" + request.getSession().getAttribute("username") +
					"&documentURI=" + objectURI;
			try {
				url =  new URL(request.getScheme(),  request.getServerName(),request.getServerPort(), file);
			} catch (MalformedURLException e) {

			}
		}
		_log.debug("Content Viewer URL for this object is --> " + url.toString());
		return url;

	}

	private ContentInfo getActualContentForObject(URL url) {
		try {
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("Cookie", "JSESSIONID=" + URLEncoder.encode(this.getThreadLocalRequest().getSession().getId(), "UTF-8"));
			InputStream in = conn.getInputStream();
			return new ContentInfo(url, conn.getContentType(), in);
		} catch (IOException e) {
			return null;
		}
	}
}
