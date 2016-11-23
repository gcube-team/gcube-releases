package org.gcube.application.framework.http.search;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.http.anonymousaccess.management.AuthenticationResponse;
import org.gcube.application.framework.http.anonymousaccess.management.CallAuthenticationManager;
import org.gcube.application.framework.http.error.messages.HTTPErrorMessages;
import org.gcube.application.framework.search.library.exception.InitialBridgingNotCompleteException;
import org.gcube.application.framework.search.library.exception.InternalErrorException;
import org.gcube.application.framework.search.library.exception.NoSearchMasterEPRFoundException;
import org.gcube.application.framework.search.library.exception.QuerySyntaxException;
import org.gcube.application.framework.search.library.exception.URIRetrievalFromISCacheException;
import org.gcube.application.framework.search.library.exception.gRS2BufferException;
import org.gcube.application.framework.search.library.exception.gRS2CreationException;
import org.gcube.application.framework.search.library.exception.gRS2ReaderException;
import org.gcube.application.framework.search.library.exception.gRS2RecordDefinitionException;
import org.gcube.application.framework.search.library.impl.SearchHelper;
import org.gcube.application.framework.search.library.interfaces.ResultSetConsumerI;
import org.gcube.application.framework.search.library.model.Criterion;
import org.gcube.application.framework.search.library.model.Field;
import org.gcube.application.framework.search.library.model.GeospatialInfo;
import org.gcube.application.framework.search.library.model.Query;
import org.gcube.application.framework.search.library.model.SearchASLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//import com.sun.org.apache.xml.internal.serialize.DOMSerializer;
//import com.sun.org.apache.xml.internal.serialize.OutputFormat;
//import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import org.gcube.application.framework.search.library.model.CollectionInfo;
import org.gcube.application.framework.search.library.util.Order;
import org.gcube.application.framework.search.library.util.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class OpenSearch
 */
public class OpenSearch extends HttpServlet {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(OpenSearch.class);
	
	private static final long serialVersionUID = 1L;

	private static final String operationID = "OpenSearch";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OpenSearch() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//-- Check if the user is authenticated
		AuthenticationResponse authenticationResp = CallAuthenticationManager.authenticateCall(request, operationID);
		if (!authenticationResp.isAuthenticated()) {
			response.sendError(401, authenticationResp.getUnauthorizedErrorMessage());
			return;
		}

		String username = authenticationResp.getUserId();

		// Get searchType parameter
		String searchType = request.getParameter("searchType");
		if (searchType == null || searchType.equals("")) {
			response.setContentType("application/rss+xml");
			writeOpenSearchError(HTTPErrorMessages.MissingParameter("searchType"), response.getOutputStream());
			return;
		}

		HttpSession session = request.getSession();
		ASLSession mysession = null;
		mysession = SessionManager.getInstance().getASLSession(session.getId(), username);

		Query q = new Query();
		String[] selectedCollections;
		List<String> selcols = new ArrayList<String>();
		String[] criteria = null;
		String[] criteriaIds = null;
		String[] criteriaNames = null;
		String[] criteriaValues = null;
		String criterion = new String();
		HashMap<CollectionInfo, ArrayList<CollectionInfo>> collectionInfos;
		String order = null;
		String distinct = null;
		String allCriteriaString = null;
		String language = new String();
		String browseBy = new String();
		ResultSetConsumerI rs = null;


		logger.info("Getting Available Collections");
		SearchHelper s_h = new SearchHelper(username, session.getId());
		try {
			collectionInfos = s_h.getAvailableCollections();
		}
		catch(InitialBridgingNotCompleteException e) {
			response.sendError(500, "Internal Server Error!");
			return;
		}
		catch(InternalErrorException e) {
			response.sendError(500, "Internal Server Error!");
			return;
		}
		if (collectionInfos != null)
			logger.info("Number of collection infos: " + collectionInfos.size());

		boolean found = false;
		String selectedCollectionsString = request.getParameter("selectedCollections");
		String selCollectionsString = selectedCollectionsString;


		if (searchType.equals("browse") || searchType.equals("simple") || searchType.equals("advanced") || searchType.equals("geospatial")) {
			logger.info("In here");
			selectedCollections = selectedCollectionsString.split(",");
			if (selectedCollections == null || selectedCollections.length == 0) {
				// Indicate Error in Open Search Format
				response.setContentType("application/rss+xml");
				writeOpenSearchError(HTTPErrorMessages.MissingParameter("selectedCollections"), response.getOutputStream());
				return;
			}
			logger.info("Passed here");
			for (int i = 0; i < selectedCollections.length; i++) {
				for (CollectionInfo colInfo:collectionInfos.keySet()) {
					ArrayList<CollectionInfo> cols = collectionInfos.get(colInfo);
					for (int j = 0; j < cols.size(); j++) {
						if (cols.get(j).getId().equals(selectedCollections[i])) {
							found = true;
							break;
						}
					}

					if (found)
						break;
				}
			}

			logger.info("Found is: " + found);
			if (!found) {
				response.setContentType("application/rss+xml");
				writeOpenSearchError("Wrong selected collection IDs given.", response.getOutputStream());
				return;
			}

			for (int i = 0; i < selectedCollections.length; i++) {
				selcols.add(selectedCollections[i]);
			}
			logger.info("The scope is: " + mysession.getScopeName());
			logger.info("Number of sel cols: " + selcols.size());
			q.selectCollections(selcols, true, mysession);


			String presentationFieldsString = request.getParameter("presentFields");
			if (presentationFieldsString != null && presentationFieldsString.length() != 0) {
				String[] presentationFields = presentationFieldsString.split(",");
				ArrayList<String> presFields = new ArrayList<String>();
				for (int p = 0; p < presentationFields.length; p++) {
					presFields.add(presentationFields[p]);
				}

				//TODO: UNCOMMENT!!!!!!!! MAVEN
				//q.setPresentationFields(presFields);
			}
		}

		// BROWSE
		if (searchType.equals("browse")) {

			List<Field> brFields = q.getAvailableBrowseFields();
			if (brFields == null || brFields.size() ==0) {
				// send error - bad request

				// Indicate Error in Open Search Format
				response.setContentType("application/rss+xml");
				writeOpenSearchError("There are no available browse fields for the selected collections.", response.getOutputStream());
				return;
			}



			String queryRep = "browse ";

			for (int i = 0; i < selcols.size(); i++) {
				queryRep += selcols.get(i) + " ";
			}

			order = request.getParameter("order");
			if (order != null) {
				if (order.equalsIgnoreCase("ASC")) {
					q.setOrder(Order.ASC);
					queryRep += Order.ASC;
				}
				else {
					q.setOrder(Order.DESC);
					queryRep += Order.DESC;
				}
			}

			distinct = request.getParameter("distinct");
			if (distinct != null) {
				if (distinct.equalsIgnoreCase("true")) {
					q.setDistinct(true);
					queryRep += "distinct";
				}
				else {
					q.setDistinct(false);
				}
			}

			browseBy = request.getParameter("browseBy");
			q.setBrowseBy(browseBy);
			if (browseBy != null && !browseBy.equals("")) {
				found = false;
				for (int i = 0; i < brFields.size(); i++) {
					if (brFields.get(i).getId().equals(browseBy)) {
						found = true;
						break;
					}
				}

				if (!found) {
					// Indicate Error in Open Search Format
					response.setContentType("application/rss+xml");
					writeOpenSearchError("Wrong browse fields chosen.", response.getOutputStream());
					return;
				}
			} else {
				// Indicate Error in Open Search Format
				response.setContentType("application/rss+xml");
				writeOpenSearchError("You need to specify the search term as a parameter.", response.getOutputStream());
				return;
			}


			queryRep += browseBy + " ";

			// TODO: also choose order and distinct


			// Submitting....
			// TODO: get this rs from the cache also
			try {
				rs = q.browse(mysession, new SearchClientImpl());
			} catch(InitialBridgingNotCompleteException e) {
				response.sendError(500, "Internal Server Error!");
				return;
			}
			catch(InternalErrorException e) {
				response.sendError(500, "Internal Server Error!");
				return;
			} catch (SearchASLException e) {
				logger.error("Exception:", e);
				response.sendError(500, "Search service has failed to fulfill the request!");
			}
		}

		else if (searchType.equals("simple")) {
			String queryRep = "simple ";


			criterion = request.getParameter("searchTerms");

			if (criterion != null && !criterion.equals("")) {
				q.setSearchTerm(criterion);
				queryRep += criterion + " ";
			}
			else {
				// Indicate Error in Open Search Format
				response.setContentType("application/rss+xml");
				writeOpenSearchError("No search term specified.", response.getOutputStream());
				return;
			}

			String sortBy = (String) request.getParameter("sortBy");

			if (sortBy != null && !sortBy.equals(""))
				q.setSortBy(sortBy);

			HashMap<String, ResultSetConsumerI> rsEPRs = (HashMap<String, ResultSetConsumerI>) mysession.getAttribute("rsEPRs");

			if (rsEPRs != null) {
				rs = rsEPRs.get(queryRep);
				if (rs == null) {
					logger.info("New rsEPR!!");
					try {
						rs = q.search(mysession, true, new SearchClientImpl());
					} catch (QuerySyntaxException e) {
						// TODO Auto-generated catch block
						logger.error("Exception:", e);
						return;
					} catch (NoSearchMasterEPRFoundException e) {
						// TODO Auto-generated catch block
						logger.error("Exception:", e);
					}
					catch(InitialBridgingNotCompleteException e) {
						response.sendError(500, "Internal Server Error!");
						return;
					}
					catch(InternalErrorException e) {
						response.sendError(500, "Internal Server Error!");
						return;
					} catch (SearchASLException e) {
						logger.error("Exception:", e);
						response.sendError(500, "Search service has failed to fulfill the request!");
					}
					//							 finally {
					//								response.sendError(500);
					//							}
					rsEPRs.put(queryRep, rs);
					mysession.setAttribute("rsEPRs", rsEPRs);
				}
				else
					logger.info("Use the same rsEPR :)");
			}
			else {
				rsEPRs = new HashMap<String, ResultSetConsumerI>();
				logger.info("New epr!! - adding attribute to the session");
				try {
					rs = q.search(mysession, true, new SearchClientImpl());
				} catch (QuerySyntaxException e) {
					logger.error("Exception:", e);
				} catch (NoSearchMasterEPRFoundException e) {
					logger.error("Exception:", e);
				}
				catch(InitialBridgingNotCompleteException e) {
					response.sendError(500, "Internal Server Error!");
					return;
				}
				catch(InternalErrorException e) {
					response.sendError(500, "Internal Server Error!");
					return;
				} catch (SearchASLException e) {
					logger.error("Exception:", e);
					response.sendError(500, "Search service has failed to fulfill the request!");
				}
				rsEPRs.put(queryRep, rs);
				mysession.setAttribute("rsEPRs", rsEPRs);
			}
		}


		else if (searchType.equals("advanced") || searchType.equals("geospatial")) {
			//q = new Query();
			String queryRep = "advanced ";

			for (int i = 0; i < selcols.size(); i++) {
				queryRep += selcols.get(i) + " ";
			}

			language = request.getParameter("language");
			if (language == null || language.equals("")) {
				// Indicate Error in Open Search Format
				response.setContentType("application/rss+xml");
				writeOpenSearchError("No language specified.", response.getOutputStream());
				return;
			}
			List<String> avLanguages = q.getAvailableLanguages();
			logger.info("Language: " + language);
			for (int i = 0; i < avLanguages.size(); i++) {
				logger.info("lang: " + avLanguages.get(i));
			}
			if (!avLanguages.contains(language)) {
				// Indicate Error in Open Search Format
				response.setContentType("application/rss+xml");
				writeOpenSearchError("Wrong language parameter.", response.getOutputStream());
				return;
			}
			
			try {
				q.setSelectedLanguage(language, mysession);
			}
			catch(InitialBridgingNotCompleteException e) {
				response.sendError(500, "Internal Server Error!");
				return;
			}
			catch(InternalErrorException e) {
				response.sendError(500, "Internal Server Error!");
				return;
			}
				
			String criteriaString = request.getParameter("criteria");
			if (criteriaString != null) {
				allCriteriaString = criteriaString;
				String[] nameValuePairs = criteriaString.split(",");
				String sortBy = (String)request.getParameter("sortBy");

				List<Field> avSearchFields = q.getAvailableSearchFields();
				List<String> avSF = new ArrayList<String>();

				for (int m = 0; m < avSearchFields.size(); m++) {
					avSF.add(avSearchFields.get(m).getId());
				}


				ArrayList<String> criteriaIdsList = new ArrayList<String>();
				//ArrayList<String> criteriaNamesList = new ArrayList<String>();
				ArrayList<String> criteriaValuesList = new ArrayList<String>();

				for (int i = 0; i < nameValuePairs.length; i++) {
					String[] nameValue = nameValuePairs[i].split("_");
					criteriaIdsList.add(nameValue[0]);
					//criteriaNamesList.add(nameValue[1]);
					criteriaValuesList.add(nameValue[1]);
				}
				criteriaIds = new String[criteriaIdsList.size()];
				criteriaIdsList.toArray(criteriaIds);

				//criteriaNames = new String[criteriaNamesList.size()];
				//criteriaNamesList.toArray(criteriaNames);

				criteriaValues = new String[criteriaValuesList.size()];
				criteriaValuesList.toArray(criteriaValues);


				if (criteriaIds != null) {
					for (int j = 0; j < criteriaIds.length; j++) {
						if (!avSF.contains(criteriaIds[j])) {
							// Indicate Error in Open Search Format
							response.setContentType("application/rss+xml");
							writeOpenSearchError("Wrong criterion selected.", response.getOutputStream());
							return;
						}
						Criterion newCrit = new Criterion();
						newCrit.setSearchFieldId(criteriaIds[j]);
						newCrit.setSearchFieldName(criteriaIds[j]);
						newCrit.setSearchFieldValue(criteriaValues[j]);
						queryRep += criteriaIds[j] + " " + criteriaValues[j] + " ";
						q.addCriterion(newCrit);
					} 
				}

				List<Field> sortFields = q.getAvailableSortFields();
				ArrayList<String> sortFieldIds = new ArrayList<String>();
				for (int i = 0; i < sortFields.size(); i++) {
					sortFieldIds.add(sortFields.get(i).getId());
				}
				if (sortBy != null) {
					if (!sortFieldIds.contains(sortBy)) {
						// Indicate Error in Open Search Format
						response.setContentType("application/rss+xml");
						writeOpenSearchError("Wrong sortable field given.", response.getOutputStream());
						return;
					}
					q.setSortBy(sortBy);
					queryRep += "sortBy" + " " + sortBy;
				}
			}
			if (searchType.equals("geospatial")) {

				String startinDate = request.getParameter("start");
				String[] startingArray = startinDate.split("-");
				String startYear = startingArray[0];
				String startMonth = startingArray[1];
				String[] splitDay = startingArray[2].split("T");
				String startDay = splitDay[0];


				String endinDate = request.getParameter("end");
				startingArray = endinDate.split("-");
				String endYear = startingArray[0];
				String endMonth = startingArray[1];
				String[] endDaySplit = startingArray[2].split("T");
				String endDay = endDaySplit[0];



				queryRep += startDay + "" + startMonth + "" + startYear + "" + endDay + "" + endMonth + "" + endYear + " ";



				String[] latitude = new String[4];
				String[] longitude = new String[4]; 
				String bbox = request.getParameter("bbox");
				String[] coordinates = bbox.split(",");

				latitude[0] = coordinates[0];
				latitude[1] = coordinates[2];
				latitude[2] = coordinates[4];
				latitude[3] = coordinates[6];

				longitude[0] = coordinates[1];
				longitude[1] = coordinates[3];
				longitude[2] = coordinates[5];
				longitude[3] = coordinates[7];


				String relation = request.getParameter("relation");
				Calendar cal = Calendar.getInstance();
				if (cal == null) 
					logger.info("The calendar is null :)");
				Calendar cal1 = Calendar.getInstance();
				Date startingDate = cal.getTime();
				Date endingDate = cal1.getTime();

				if (startDay != null && startMonth != null && startYear != null && endDay != null && endMonth != null && endYear != null) {


					int dayInt = Integer.parseInt(startDay);
					int monthInt = Integer.parseInt(startMonth);
					int yearInt = Integer.parseInt(startYear);



					logger.info("Year: " + yearInt);
					cal.set(Calendar.YEAR, yearInt);
					cal.set(Calendar.MONTH, monthInt);
					cal.set(Calendar.DAY_OF_MONTH, dayInt);

					dayInt = Integer.parseInt(endDay);
					monthInt = Integer.parseInt(endMonth);
					yearInt = Integer.parseInt(endYear);

					cal1.set(Calendar.YEAR, yearInt);
					cal1.set(Calendar.MONTH, monthInt);
					cal1.set(Calendar.DAY_OF_MONTH, dayInt);

				} else {
					startingDate = null;
					endingDate = null;
				}

				Point[] point = new Point[latitude.length];
				for (int i = 0; i < latitude.length; i++) {
					point[i] = new Point();
					queryRep += latitude[i] + " " + longitude[i] + " ";
					Double lat = Double.parseDouble(latitude[i]);
					logger.info("latitude: " + lat);
					if (point[i] == null) {
						logger.info("The point is null");
					}
					point[i].setLatitude(lat);	
					Double longt = Double.parseDouble(longitude[i]);
					point[i].setLongitude(longt);
				}

				queryRep += relation;

				GeospatialInfo geoInfo = new GeospatialInfo();
				if (startingDate != null && endingDate != null) {
					geoInfo.setStartingDate(startingDate);
					geoInfo.setEndingDate(endingDate);
				}
				geoInfo.setBounds(point);
				geoInfo.setRelation(relation);



				q.setGeospatial(geoInfo);
			}

			HashMap<String, ResultSetConsumerI> rsEPRs = (HashMap<String, ResultSetConsumerI>) mysession.getAttribute("rsEPRs");
			logger.info("The queryRep is: " + queryRep);
			if (rsEPRs != null) {
				rs = rsEPRs.get(queryRep);
				if (rs == null) {
					logger.info("New epr!!");

					try {
						rs = q.search(mysession, false, new SearchClientImpl());
					} catch (QuerySyntaxException e) {
						logger.error("Exception:", e);
					} catch (NoSearchMasterEPRFoundException e) {
						logger.error("Exception:", e);
					}
					catch(InitialBridgingNotCompleteException e) {
						response.sendError(500, "Internal Server Error!");
						return;
					}
					catch(InternalErrorException e) {
						response.sendError(500, "Internal Server Error!");
						return;
					} catch (SearchASLException e) {
						logger.error("Exception:", e);
						response.sendError(500, "Search service has failed to fulfill the request!");
					}

					rsEPRs.put(queryRep, rs);
					mysession.setAttribute("rsEPRs", rsEPRs);
				}
				else 
					logger.info("Use the same rsEPR :)");
			}
			else {
				rsEPRs = new HashMap<String, ResultSetConsumerI>();
				logger.info("New epr!!! - adding attribute to the session");

				try {
					rs = q.search(mysession, false, new SearchClientImpl());
				} catch (QuerySyntaxException e) {
					logger.error("Exception:", e);
				} catch (NoSearchMasterEPRFoundException e) {
					logger.error("Exception:", e);
				}
				catch(InitialBridgingNotCompleteException e) {
					response.sendError(500, "Internal Server Error!");
					return;
				}
				catch(InternalErrorException e) {
					response.sendError(500, "Internal Server Error!");
					return;
				} catch (SearchASLException e) {
					logger.error("Exception:", e);
					response.sendError(500, "Search service has failed to fulfill the request!");
				}

				rsEPRs.put(queryRep, rs);
				mysession.setAttribute("rsEPRs", rsEPRs);
			}

		}

		else if (searchType.equals("quick")){
			// quick
			String queryRep = "quick ";
			criterion = request.getParameter("searchTerms");
			if (criterion == null) {
				// Indicate Error in Open Search Format
				response.setContentType("application/rss+xml");
				writeOpenSearchError("No search term specified.", response.getOutputStream());
				return;
			}

			queryRep += criterion;
			HashMap<String, ResultSetConsumerI> rsEPRs = (HashMap<String, ResultSetConsumerI>) mysession.getAttribute("rsEPRs");

			if (rsEPRs != null) {
				rs = rsEPRs.get(queryRep);
				if (rs == null) {
					logger.info("New epr!!");
					try {
						rs = q.quickSearch(mysession, criterion, new SearchClientImpl());
					} catch (SearchASLException e) {
						logger.error("Exception:", e);
						response.sendError(500, "Search service has failed to fulfill the request!");
					} 
					rsEPRs.put(queryRep, rs);
					mysession.setAttribute("rsEPRs", rsEPRs);
				}
				else 
					logger.info("Use the same rsEPR :)");
			}
			else {
				rsEPRs = new HashMap<String, ResultSetConsumerI>();
				logger.info("New epr!!! - adding attribute to the session");
				try {
					rs = q.quickSearch(mysession, criterion, new SearchClientImpl());
				} catch (SearchASLException e) {
					logger.error("Exception:", e);
					response.sendError(500, "Search service has failed to fulfill the request!");
				} 
				rsEPRs.put(queryRep, rs);
				mysession.setAttribute("rsEPRs", rsEPRs);
			}
		}

		else {
			// Wrong search type
			// Indicate Error in Open Search Format
			response.setContentType("application/rss+xml");
			writeOpenSearchError("Wrong Search Type selected.", response.getOutputStream());
			return;
		}


		String numResults = request.getParameter("count");
		int num;
		if (numResults != null)
			num = Integer.parseInt(numResults);
		else {
			num = 10;	//default value
			numResults = "10";
		}
		int ofst;
		String offset = request.getParameter("startIndex");
		if (offset != null) {
			if (offset.equals("0") && searchType.equals("browse"))
				ofst = 1;
			else
				ofst = Integer.parseInt(offset);
		}
		else {
			if (!searchType.equals("browse")) {
				ofst = 0;		//default value
				offset = "0";
			}
			else {
				ofst = 1;
				offset = "1";
			}
		}
		List<String> xmlResults = new ArrayList<String>();

		mysession = SessionManager.getInstance().getASLSession(session.getId(), username);
		try {
			xmlResults = rs.getResultsToText(num, ofst, mysession);
		} catch (gRS2ReaderException e1) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e1);
		} catch (gRS2RecordDefinitionException e1) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e1);
		} catch (gRS2BufferException e1) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e1);
		}



		logger.info("NUMBER OF HTTP RESULTS: " + xmlResults.size());

		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = dbfac.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error("Exception:", e);
		} 

		Document doc = docBuilder.newDocument();
		Element root = doc.createElement("Results");
		doc.appendChild(root);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
		}
		Document document = null;

		for (int j = 0; j < xmlResults.size(); j++) {
			logger.info("Result: " + xmlResults.get(j));
			try {
				ByteArrayInputStream encXML =  new  ByteArrayInputStream(xmlResults.get(j).getBytes("UTF8"));
				document = builder.parse(encXML);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				logger.error("Exception:", e);
			}

			Element element = document.getDocumentElement();
			Node dup = doc.importNode(element, true);
			root.appendChild(dup);
		}

		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
		String xmlToStr = "";
		StringWriter writer = new StringWriter();
		try {
			DOMSource domSource = new DOMSource(doc);
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.transform(domSource, result);		
		} catch (Exception e) {
			logger.error("Exception:", e);
		}

		//!!!!!
		logger.info(request.getContextPath() + "contextPath");
		String contextPath = request.getContextPath();

		if (searchType.equals("browse")) {
			String resultsRSS = transformToRSS(writer.toString(), contextPath);

			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			DocumentBuilder build = null;
			try {
				build = fact.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				logger.error("Exception:", e);
			}
			Document rssDoc = null;
			try {
				rssDoc = build.parse(new InputSource(new StringReader(resultsRSS)));
			} catch (SAXException e) {
				logger.error("Exception:", e);
			}

			String resultsOpenSearch = openSearchWrapperBrowse(rssDoc, selCollectionsString, browseBy, order, distinct, offset, numResults);
			logger.info("i am browse: " + resultsOpenSearch);
			out.write(resultsOpenSearch);
			out.close();
			return;
		}
		else if (searchType.equals("simple")) {
			String resultsRSS = transformToRSS(writer.toString(), contextPath);

			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			DocumentBuilder build = null;
			try {
				build = fact.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				logger.error("Exception:", e);
			}
			Document rssDoc = null;
			try {
				rssDoc = build.parse(new InputSource(new StringReader(resultsRSS)));
			} catch (SAXException e) {
				logger.error("Exception:", e);
			}

			String scope = request.getParameter("scope");
			String resultsOpenSearch = openSearchWrapperSimple(rssDoc, selCollectionsString, criterion,/* schema, language,*/ offset, numResults, scope);
			logger.info("i am simple: " + resultsOpenSearch);
			out.write(resultsOpenSearch);
			out.close();
			return;
		}
		else if (searchType.equals("advanced") || (searchType.equals("geospatial"))) {
			String resultsRSS = transformToRSS(writer.toString(), contextPath);

			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			DocumentBuilder build = null;
			try {
				build = fact.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				logger.error("Exception:", e);
			}
			Document rssDoc = null;
			try {
				rssDoc = build.parse(new InputSource(new StringReader(resultsRSS)));
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				logger.error("Exception:", e);
			}
			String resultsOpenSearch = "";
			resultsOpenSearch = openSearchWrapperCombined(rssDoc, selCollectionsString, allCriteriaString, language, offset, numResults);
			logger.info("i am combine: " + resultsOpenSearch);
			out.write(resultsOpenSearch);
			out.close();
			return;
		}
		else if (searchType.equals("quick")){
			String resultsRSS = transformToRSS(writer.toString(), contextPath);

			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			DocumentBuilder build = null;
			try {
				build = fact.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				logger.error("Exception:", e);
			}
			Document rssDoc = null;
			try {
				rssDoc = build.parse(new InputSource(new StringReader(resultsRSS)));
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				logger.error("Exception:", e);
			}

			String scope = request.getParameter("scope");
			String resultsOpenSearch = openSearchWrapperQuick(rssDoc, criterion, offset, numResults, scope);
			logger.info("i am quick: " + resultsOpenSearch);
			out.write(resultsOpenSearch);
			out.close();
			return;
		}
	}
	
	protected String openSearchWrapperCombined (Document resultsRSS, String selectedCollections, String criteria, String language, String startIndex, String itemsPerPage) {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = dbfac.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error("Exception:", e);
		}

		Document resultsOS = docBuilder.newDocument();
		Element root = resultsRSS.getDocumentElement();
		resultsOS.importNode(root, true);

		NodeList channelNodes = resultsRSS.getElementsByTagName("channel");
		Node channelNode = channelNodes.item(0);
		Element osRootEl = (Element)channelNode;

		osRootEl.setAttribute("xmlns:opensearch", "http://a9.com/-/spec/opensearch/1.1/");
		osRootEl.setAttribute("xmlns:atom", "http://www.w3.org/2005/Atom");
		osRootEl.setAttribute("xmlns:d4science", "http://d4science.org");


		// declare also the namespaces for the query attributes????

		if (startIndex != null) {
			Element openSearchEl = resultsRSS.createElement("opensearch:startIndex");
			openSearchEl.setTextContent(startIndex);
			//	osRootEl.appendChild(openSearchEl);
			channelNode.insertBefore(openSearchEl, osRootEl.getFirstChild());
		}
		if (itemsPerPage != null) {
			Element openSearchEl = resultsRSS.createElement("opensearch:itemsPerPage");
			openSearchEl.setTextContent(itemsPerPage);
			//	osRootEl.appendChild(openSearchEl);
			channelNode.insertBefore(openSearchEl, osRootEl.getFirstChild());
		}

		// I REMOVED the Query element for this search because it requires
		// double attributes 
		Element openSearchEl = resultsRSS.createElement("opensearch:Query");
		openSearchEl.setAttribute("role", "request");
		openSearchEl.setAttribute("d4science:searchType", "advanced");
		openSearchEl.setAttribute("d4science:openSearch", "true");
		if (itemsPerPage != null) {
			openSearchEl.setAttribute("count", itemsPerPage);
		}

		//		for (int i = 0; i < selectedCollections.size(); i++) {
		//			//Paraviazei Well-formness Constraint!!!!!
		//			openSearchEl.setAttribute("d4science:selectedCollections", selectedCollections.get(i));
		//		}

		openSearchEl.setAttribute("d4science:selectedCollections", selectedCollections);
		openSearchEl.setAttribute("criteria", criteria);

		osRootEl.appendChild(openSearchEl);
		channelNode.insertBefore(openSearchEl, osRootEl.getFirstChild());


		String rssToStr = "";
		StringWriter writer = new StringWriter();
		try {
			DOMSource domSource = new DOMSource(resultsRSS);
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
		}
		catch (Exception e) {
			logger.error("Exception:", e);
		}

		rssToStr = writer.toString();
		logger.info("lala is:" + rssToStr);

		return rssToStr;
	}
	
	protected String openSearchWrapperQuick (Document resultsRSS, String searchTerms, String startIndex, String itemsPerPage, String scope) {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = dbfac.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error("Exception:", e);
		}

		Document resultsOS = docBuilder.newDocument();
		Element root = resultsRSS.getDocumentElement();
		resultsOS.importNode(root, true);

		NodeList channelNodes = resultsRSS.getElementsByTagName("channel");
		Node channelNode = channelNodes.item(0);
		Element osRootEl = (Element) channelNode;

		osRootEl.setAttribute("xmlns:opensearch", "http://a9.com/-/spec/opensearch/1.1/");
		osRootEl.setAttribute("xmlns:atom", "http://www.w3.org/2005/Atom");
		osRootEl.setAttribute("xmlns:d4science", "http://d4science.org");


		if (startIndex != null) {
			Element openSearchEl = resultsRSS.createElement("opensearch:startIndex");
			openSearchEl.setTextContent(startIndex);
			//	osRootEl.appendChild(openSearchEl);
			channelNode.insertBefore(openSearchEl, osRootEl.getFirstChild());
		}
		if (itemsPerPage != null) {
			Element openSearchEl = resultsRSS.createElement("opensearch:itemsPerPage");
			openSearchEl.setTextContent(itemsPerPage);
			//	osRootEl.appendChild(openSearchEl);
			channelNode.insertBefore(openSearchEl, osRootEl.getFirstChild());
		}


		Element openSearchEl = resultsRSS.createElement("opensearch:Query");
		openSearchEl.setAttribute("role", "request");
		openSearchEl.setAttribute("d4science:searchType", "simple");
		openSearchEl.setAttribute("d4science:openSearch", "true");
		openSearchEl.setAttribute("searchTerms", searchTerms);
		openSearchEl.setAttribute("d4science:scope", scope);
		if (itemsPerPage != null) {
			openSearchEl.setAttribute("count", itemsPerPage);
		}

		channelNode.insertBefore(openSearchEl, osRootEl.getFirstChild());

		String rssToStr = "";
		StringWriter writer = new StringWriter();
		try {
			DOMSource domSource = new DOMSource(resultsRSS);
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
		}
		catch (Exception e) {
			logger.error("Exception:", e);
		}

		rssToStr = writer.toString();
		logger.info("lala is:" + rssToStr);

		return rssToStr;
	}
	
	protected String openSearchWrapperSimple (Document resultsRSS, String selectedCollections, String searchTerms,/* String schema, String language, */ String startIndex, String itemsPerPage, String scope) {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = dbfac.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error("Exception:", e);
		}

		Document resultsOS = docBuilder.newDocument();
		Element root = resultsRSS.getDocumentElement();
		resultsOS.importNode(root, true);

		NodeList channelNodes = resultsRSS.getElementsByTagName("channel");
		Node channelNode = channelNodes.item(0);
		Element osRootEl = (Element) channelNode;

		osRootEl.setAttribute("xmlns:opensearch", "http://a9.com/-/spec/opensearch/1.1/");
		osRootEl.setAttribute("xmlns:atom", "http://www.w3.org/2005/Atom");
		osRootEl.setAttribute("xmlns:d4science", "http://d4science.org");

		if (startIndex != null) {
			Element openSearchEl = resultsRSS.createElement("opensearch:startIndex");
			openSearchEl.setTextContent(startIndex);
			//	osRootEl.appendChild(openSearchEl);
			channelNode.insertBefore(openSearchEl, osRootEl.getFirstChild());
		}
		if (itemsPerPage != null) {
			Element openSearchEl = resultsRSS.createElement("opensearch:itemsPerPage");
			openSearchEl.setTextContent(itemsPerPage);
			//	osRootEl.appendChild(openSearchEl);
			channelNode.insertBefore(openSearchEl, osRootEl.getFirstChild());
		}


		Element openSearchEl = resultsRSS.createElement("opensearch:Query");
		openSearchEl.setAttribute("role", "request");
		openSearchEl.setAttribute("d4science:searchType", "simple");
		openSearchEl.setAttribute("d4science:openSearch", "true");
		openSearchEl.setAttribute("d4science:scope", scope);
		openSearchEl.setAttribute("searchTerms", searchTerms);
		if (itemsPerPage != null) {
			openSearchEl.setAttribute("count", itemsPerPage);
		}


		openSearchEl.setAttribute("d4science:selectedCollections", selectedCollections);


		channelNode.insertBefore(openSearchEl, osRootEl.getFirstChild());

		String rssToStr = "";
		StringWriter writer = new StringWriter();
		try {
			DOMSource domSource = new DOMSource(resultsRSS);
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
		}
		catch (Exception e) {
			logger.error("Exception:", e);
		}

		rssToStr = writer.toString();
		logger.info("lala is:" + rssToStr);

		return rssToStr;
	}

	protected String transformToRSS (String resultsXML, String contextPath) {
		// TODO : See if xslt needs to be fixed
		String xslt ="<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"><xsl:template match=\"/\"><rss version=\"2.0\"><channel><title>D4Science Results</title><description>Results coming from D4Science search machine</description><link>http://www.di.uoa.gr</link><xsl:apply-templates select=\"Results\" /></channel></rss></xsl:template><xsl:template match=\"Results\"><xsl:for-each select=\"RSRecord\"><item><xsl:variable name=\"title_el\" select=\"descendant::title\"/><xsl:if test =\"$title_el\"><title><xsl:value-of select=\"descendant::title\" /></title></xsl:if><xsl:if test =\"not($title_el)\"><title>ResultSetRecord</title></xsl:if><RSRecord xmlns=\"http://d4science.namespace.org\"><xsl:copy-of select=\"node()\" /></RSRecord></item></xsl:for-each></xsl:template><xsl:template match=\"RSRecord\"><xsl:value-of select=\"./*\" /></xsl:template></xsl:stylesheet>";
		File xsltFile = new File("xsltFile");
		FileWriter out = null;
		try {
			out = new FileWriter(xsltFile);
			out.write(xslt);
			out.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e1);
		}



		Source xsltSource = new StreamSource(xsltFile);
		ByteArrayOutputStream ba_stream = new ByteArrayOutputStream();

		// the factory pattern supports different XSLT processors
		TransformerFactory transFact = TransformerFactory.newInstance();
		Transformer trans = null;
		try {
			trans = transFact.newTransformer(xsltSource);
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
		}
		if (resultsXML == null)
			logger.info("klaps");
		if (trans == null)
			logger.info("kloups");
		try {
			trans.transform(new StreamSource(new ByteArrayInputStream(resultsXML.getBytes())), new StreamResult(ba_stream));
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
		}
		if (ba_stream != null) {
			xsltFile.delete();
			return ba_stream.toString();
		} else {
			xsltFile.delete();
			return null;
		}

	}
	
	
	protected String openSearchWrapperBrowse (Document resultsRSS, String selectedCollection, String searchTerms, String order, String distinct, String startIndex, String itemsPerPage ) {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = dbfac.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error("Exception:", e);
		}

		Document resultsOS = docBuilder.newDocument();
		Element root = resultsRSS.getDocumentElement();
		resultsOS.importNode(root, true);

		NodeList channelNodes = resultsRSS.getElementsByTagName("channel");
		Node channelNode = channelNodes.item(0);
		Element osRootEl = (Element)channelNode;
		//	osRootEl.setAttribute("lala", "lili");

		osRootEl.setAttribute("xmlns:opensearch", "http://a9.com/-/spec/opensearch/1.1/");
		osRootEl.setAttribute("xmlns:atom", "http://www.w3.org/2005/Atom");
		osRootEl.setAttribute("xmlns:d4science", "http://d4science.org");


		// declare also the namespaces for the query attributes????

		if (startIndex != null) {
			Element openSearchEl = resultsRSS.createElement("opensearch:startIndex");
			openSearchEl.setTextContent(startIndex);
			//	osRootEl.appendChild(openSearchEl);
			channelNode.insertBefore(openSearchEl, osRootEl.getFirstChild());
		}
		if (itemsPerPage != null) {
			Element openSearchEl = resultsRSS.createElement("opensearch:itemsPerPage");
			openSearchEl.setTextContent(itemsPerPage);
			//	osRootEl.appendChild(openSearchEl);
			channelNode.insertBefore(openSearchEl, osRootEl.getFirstChild());
		}


		Element openSearchEl = resultsRSS.createElement("opensearch:Query");
		openSearchEl.setAttribute("role", "request");
		openSearchEl.setAttribute("d4science:searchType", "browse");
		openSearchEl.setAttribute("d4science:openSearch", "true");
		openSearchEl.setAttribute("searchTerms", searchTerms);
		if (order != null) {
			openSearchEl.setAttribute("d4science:order", order);
		}
		if (itemsPerPage != null) {
			openSearchEl.setAttribute("count", itemsPerPage);
		}

		openSearchEl.setAttribute("d4science:selectedCollection", selectedCollection);



		if (order != null) {
			openSearchEl.setAttribute("d4science:order", order);
		}
		if (distinct != null) {
			openSearchEl.setAttribute("d4science:distinct", distinct);
		}

		channelNode.insertBefore(openSearchEl, osRootEl.getFirstChild());

		String rssToStr = "";
		StringWriter writer = new StringWriter();
		try {
			DOMSource domSource = new DOMSource(resultsRSS);
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
		}
		catch (Exception e) {
			logger.error("Exception:", e);
		}

		rssToStr = writer.toString();
		logger.info("lala is:" + rssToStr);

		return rssToStr;

	}

	private void writeOpenSearchError(String errorMessage, OutputStream responseStream) throws IOException {
		// Indicate Error in OpenSearch Format
		Document error = getOpenSearchError("Request Syntax Error", errorMessage);
		try {
		     TransformerFactory tf = TransformerFactory.newInstance();
		     Transformer trans = tf.newTransformer();
		     trans.transform(new DOMSource(error), new StreamResult(responseStream));
		} catch (TransformerConfigurationException e) {
			logger.debug("Failed to initiate dom transformer to transform Opensearch error to output xml");
		} catch (TransformerException e) {
			logger.debug("Could not transform Opensearch error to output xml");
		}
		responseStream.flush();
		responseStream.close();
	}


	Document getOpenSearchError (String title, String description) {
		try {
			logger.info("The title is: " + title + " and the description is: " + description);
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element root = doc.createElement("rss");
			doc.appendChild(root);
			root.setAttribute("version", "2.0");
			root.setAttribute("xmlns:openSearch", "http://a9.com/-/spec/opensearch/1.1/");

			Element channelEl = doc.createElement("channel");
			Element titleEl = doc.createElement("title");
			titleEl.setTextContent(title);
			channelEl.appendChild(titleEl);
			Element descriptionEl = doc.createElement("description");
			descriptionEl.setTextContent(description);
			channelEl.appendChild(descriptionEl);
			Element resultsEl = doc.createElement("openSearch:totalResults");
			resultsEl.setTextContent("1");
			channelEl.appendChild(resultsEl);
			Element startIndexEl = doc.createElement("openSearch:startIndex");
			startIndexEl.setTextContent("1");
			channelEl.appendChild(startIndexEl);
			Element itemsEl = doc.createElement("openSearch:itemsPerPage");
			itemsEl.setTextContent("1");
			channelEl.appendChild(itemsEl);

			Element itemEl = doc.createElement("item");
			Element titleInEl = doc.createElement("title");
			titleInEl.setTextContent("Error");
			Element descriptionInEl = doc.createElement("description");
			descriptionInEl.setTextContent(description);
			itemEl.appendChild(titleInEl);
			itemEl.appendChild(titleInEl);

			channelEl.appendChild(itemEl);

			root.appendChild(channelEl);

			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			logger.info("errorString: " + writer.toString());

			return doc;
		} catch (Exception e) {
			logger.error("Exception:", e);
			return null;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
