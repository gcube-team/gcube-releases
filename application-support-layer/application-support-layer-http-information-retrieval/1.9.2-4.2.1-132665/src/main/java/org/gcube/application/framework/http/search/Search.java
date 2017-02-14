package org.gcube.application.framework.http.search;

import java.io.ByteArrayInputStream;
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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.lang.StringEscapeUtils;
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
import org.gcube.application.framework.search.library.model.CollectionInfo;
import org.gcube.application.framework.search.library.model.Criterion;
import org.gcube.application.framework.search.library.model.Field;
import org.gcube.application.framework.search.library.model.GeospatialInfo;
import org.gcube.application.framework.search.library.model.Query;
import org.gcube.application.framework.search.library.model.SearchASLException;
import org.gcube.application.framework.search.library.util.Order;
import org.gcube.application.framework.search.library.util.Point;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class Search
 */
public class Search extends HttpServlet {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(Search.class);
	
	private static final long serialVersionUID = 1L;

	private static final String operationID = "Search";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Search() {
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


		//		String searchType = request.getParameter("searchType");
		if (request.getParameter("searchType") == null) {
			response.sendError(400, HTTPErrorMessages.MissingParameter("searchType"));
			return;
		}

		String searchType = request.getParameter("searchType");
		//response.setCharacterEncoding("UTF-8");


		HttpSession session = request.getSession();
		ASLSession mysession = null;
		mysession = SessionManager.getInstance().getASLSession(session.getId(), username);
		ResultSetConsumerI rs = null;

		Query q = new Query();
		String[] selectedCollections;
		List<String> selcols = new ArrayList<String>();
		String[] criteriaIds = null;
		String[] criteriaValues = null;
		String criterion = new String();
		HashMap<CollectionInfo, ArrayList<CollectionInfo>> collectionInfos;
		String order = null;
		String distinct = null;
		String language = new String();
		String browseBy = new String();

		//For android, return all available fields in the results, by setting the semantic enrichment flag to true 
		if(request.getParameter("allFields") != null && request.getParameter("allFields").equalsIgnoreCase("true")){
			logger.info("allFields flag is true, getting all fields!");
			q.setSemanticEnrichment(true);
		}
		else
			logger.info("allFields flag was false or not set -> return normal fileds!");

		
		try {
			logger.info("Getting Available Collections");
			SearchHelper s_h = new SearchHelper(username, session.getId());
			collectionInfos = s_h.getAvailableCollections();
			if (collectionInfos != null)
				logger.info("Number of collection infos: " + collectionInfos.size());
		} catch (Exception e) {
			// Send error - Unauthorized
			response.sendError(401, "Login to a VRE is needed");
			return;
		}
		boolean found = false;

		JSONObject seleColsParam;
		JSONArray selectedCollectionsJS = null;
		if (searchType.equals("browse") || searchType.equals("simple") || searchType.equals("advanced") || searchType.equals("geospatial")) {


			
			try {
				seleColsParam = (JSONObject) JSONSerializer.toJSON(request.getParameter("selectedCollections"));
				selectedCollectionsJS = (JSONArray)seleColsParam.get("SelectedCollections");
			} catch (Exception e) {
				response.sendError(400, HTTPErrorMessages.WrongJSONInput("selectedCollections"));
				return;
			}
			selectedCollections = new String[selectedCollectionsJS.size()];
			for (int i = 0; i < selectedCollectionsJS.size(); i++) {
				selectedCollections[i] = (String)selectedCollectionsJS.get(i);
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
				// send error - Bad request
				response.sendError(400, "Wrong selected collections");
				return;
			}

			for (int i = 0; i < selectedCollections.length; i++) {
				selcols.add(selectedCollections[i]);
			}
			logger.info("The scope is: " + mysession.getScopeName());
			logger.info("Number of sel cols: " + selcols.size());
			q.selectCollections(selcols, true, mysession);


			if (request.getParameter("presentFields") != null) {
				JSONArray presentationFieldsJS;
				JSONObject jsonO;
				try {
					jsonO = (JSONObject) JSONSerializer.toJSON(request.getParameter("presentFields"));
					presentationFieldsJS = (JSONArray) jsonO.getJSONArray("presentFields");
				} catch (Exception e) {
					response.sendError(400, HTTPErrorMessages.WrongJSONInput("presentFields"));
					return;
				}
	
				ArrayList<String> presFields = new ArrayList<String>();
				for (int i = 0; i < presentationFieldsJS.size(); i++) {
					presFields.add((String)presentationFieldsJS.get(i));
	
				}
			}

			//TODO: UNCOMMENT!!!!!! MAVEN
			//q.setPresentationFields(presFields);
		}

		// BROWSE
		if (searchType.equals("browse")) {

			List<Field> brFields = q.getAvailableBrowseFields();
			if (brFields == null || brFields.size() ==0) {
				// send error - bad request
				response.sendError(400, "No available browse fields");
				return;
			}




			order = request.getParameter("order");
			if (order != null) {
				if (order.equalsIgnoreCase("ASC")) {
					q.setOrder(Order.ASC);
				}
				else {
					q.setOrder(Order.DESC);
				}
			}

			distinct = request.getParameter("distinct");
			if (distinct != null) {
				if (distinct.equalsIgnoreCase("true")) {
					q.setDistinct(true);
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
					// send error - bad request
					response.sendError(400, "Wrong browse field selected");
					return;
				}
			} else {
				// send error - bad request
				response.sendError(400, "No search term defined");
				return;
			}



			// TODO: also choose order and distinct


			// Submitting....
			// TODO: get this rs from the cache also
			try {
				rs = q.browse(mysession,new SearchClientImpl());
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
		} else if (searchType.equals("simple")) {
			String queryRep = "simple ";


			criterion = request.getParameter("searchTerms");

			if (criterion != null && !criterion.equals("")) {
				q.setSearchTerm(criterion);
				queryRep += criterion + " ";
			}
			else {
				response.sendError(400, "No search term specified");
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
					rsEPRs.put(q.getQueryString(), rs);
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
				rsEPRs.put(q.getQueryString(), rs);
				mysession.setAttribute("rsEPRs", rsEPRs);
			}


		}
		else if (searchType.equals("advanced") || searchType.equals("geospatial")) {
			String queryRep = "advanced ";

			for (int i = 0; i < selcols.size(); i++) {
				queryRep += selcols.get(i) + " ";
			}

			language = request.getParameter("language");
			if (language == null || language.equals("")) {
				//send error
				response.sendError(400, "No language selected");
				return;
			}
			List<String> avLanguages = q.getAvailableLanguages();
			logger.info("Language: " + language);
			for (int i = 0; i < avLanguages.size(); i++) {
				logger.info("lang: " + avLanguages.get(i));
			}
			if (!avLanguages.contains(language)) {
				//send error
				response.sendError(400, "The language parameter is wrong.");
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

			//	String criteriaString = request.getParameter("criteria");
			if (request.getParameter("criteria") != null) {
				JSONArray criteriaJS;
				try {
					criteriaJS = (JSONArray) JSONSerializer.toJSON(request.getParameter("criteria"));
				} catch (Exception e) {
					response.sendError(400, HTTPErrorMessages.WrongJSONInput("criteria"));
					return;
				}

				ArrayList<String> criteriaIdsList = new ArrayList<String>();
				ArrayList<String> criteriaValuesList = new ArrayList<String>();
				for (int i = 0; i < criteriaJS.size(); i++) {
					JSONObject criterionJS = (JSONObject) JSONSerializer.toJSON(criteriaJS.get(i));
					criteriaIdsList.add(criterionJS.getString("id"));
					criteriaValuesList.add(criterionJS.getString("value"));
					logger.info("---------------------- Adding criterion: " + criterionJS.getString("id"));
					logger.info("---------------------- Adding criterion value: " + criterionJS.getString("value"));
				}
				//			if (criteriaString != null) {
				//				String[] nameValuePairs = criteriaString.split(",");
				String sortBy = (String)request.getParameter("sortBy");

				List<Field> avSearchFields = q.getAvailableSearchFields();
				List<String> avSF = new ArrayList<String>();

				for (int m = 0; m < avSearchFields.size(); m++) {
					avSF.add(avSearchFields.get(m).getId());
				}


				//				ArrayList<String> criteriaIdsList = new ArrayList<String>();
				//				ArrayList<String> criteriaValuesList = new ArrayList<String>();

				//				for (int i = 0; i < nameValuePairs.length; i++) {
				//					String[] nameValue = nameValuePairs[i].split("_");
				//					criteriaIdsList.add(nameValue[0]);
				//					criteriaValuesList.add(nameValue[1]);
				//				}
				criteriaIds = new String[criteriaIdsList.size()];
				criteriaIdsList.toArray(criteriaIds);


				criteriaValues = new String[criteriaValuesList.size()];
				criteriaValuesList.toArray(criteriaValues);


				if (criteriaIds != null) {
					for (int j = 0; j < criteriaIds.length; j++) {
						if (!avSF.contains(criteriaIds[j])) {
							// send error - bad request
							response.sendError(400, "Wrong criterion selected");
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
						// send error - Bad request
						response.sendError(400, "Wrong sortable field given");
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

					rsEPRs.put(q.getQueryString(), rs);
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
				} //response.sendError(500);
				//}
				catch (QuerySyntaxException e) {
					// TODO Auto-generated catch block
					logger.error("Exception:", e);
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

				rsEPRs.put(q.getQueryString(), rs);
				mysession.setAttribute("rsEPRs", rsEPRs);
			}

		} else if (searchType.equals("quick")){
			// quick
			String queryRep = "quick ";
			criterion = request.getParameter("searchTerms");
			if (criterion == null) {
				// send error - Bad Request
				response.sendError(400, "No search term specified");
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
					//finally {
					//response.sendError(500);
					//}
					rsEPRs.put(q.getQueryString(), rs);
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
				rsEPRs.put(q.getQueryString(), rs);
				mysession.setAttribute("rsEPRs", rsEPRs);
			}
		} else {
			// Wrong search type
			// send error - Bad Request
			response.sendError(400, "Wrong Search Type selected");
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
			rs.setOnlyPresentables();
			xmlResults = rs.getResultsToText(num, ofst, mysession);
		} catch (gRS2ReaderException e1) {
			logger.error("Exception:", e1);
		} catch (gRS2RecordDefinitionException e1) {
			logger.error("Exception:", e1);
		} catch (gRS2BufferException e1) {
			logger.error("Exception:", e1);
		}



		logger.info("NUMBER OF HTTP RESULTS: " + xmlResults.size());

		if (request.getParameter("responseType") == null || request.getParameter("responseType").equals("json")) {
			response.setContentType("application/json");
			JSONObject resultsJS = new JSONObject();
			JSONArray rsArrayJS = new JSONArray();
			for (int j = 0; j < xmlResults.size(); j++) {
				logger.info("Result: " + xmlResults.get(j));
				JSONObject resultJS = new JSONObject();
				resultJS.put("Record", xmlResults.get(j));
				rsArrayJS.add(resultJS);
			}
			resultsJS.put("Results", rsArrayJS);

			PrintWriter out = response.getWriter();
			out.print(resultsJS);
			out.flush();
			out.close();
		}

		else {
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
//					ByteArrayInputStream encXML =  new  ByteArrayInputStream(xmlResults.get(j).getBytes("UTF8"));
//					document = builder.parse(encXML);
					document = builder.parse(new InputSource(new StringReader(xmlResults.get(j))));
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


			logger.info("Writing resultsString!!");

			out.write(writer.toString());
			out.close();	

			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
