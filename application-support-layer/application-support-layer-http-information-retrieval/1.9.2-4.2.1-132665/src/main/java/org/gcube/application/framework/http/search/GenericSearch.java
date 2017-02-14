package org.gcube.application.framework.http.search;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

import org.apache.commons.lang.StringEscapeUtils;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.http.anonymousaccess.management.AuthenticationResponse;
import org.gcube.application.framework.http.anonymousaccess.management.CallAuthenticationManager;
import org.gcube.application.framework.search.library.exception.InitialBridgingNotCompleteException;
import org.gcube.application.framework.search.library.exception.InternalErrorException;
import org.gcube.application.framework.search.library.exception.QuerySyntaxException;
import org.gcube.application.framework.search.library.exception.NoSearchMasterEPRFoundException;
import org.gcube.application.framework.search.library.exception.URIRetrievalFromISCacheException;
import org.gcube.application.framework.search.library.exception.gRS2BufferException;
import org.gcube.application.framework.search.library.exception.gRS2CreationException;
import org.gcube.application.framework.search.library.exception.gRS2ReaderException;
import org.gcube.application.framework.search.library.exception.gRS2RecordDefinitionException;
import org.gcube.application.framework.search.library.impl.SearchHelper;
import org.gcube.application.framework.search.library.interfaces.ResultSetConsumerI;
import org.gcube.application.framework.search.library.model.CollectionInfo;
import org.gcube.application.framework.search.library.model.ISearchClient;
import org.gcube.application.framework.search.library.model.Query;
import org.gcube.application.framework.search.library.model.Field;
import org.gcube.application.framework.search.library.model.SearchASLException;
import org.gcube.application.framework.search.library.util.SearchType;
//import org.gcube.search.exceptions.SearchException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class GenericSearch
 */
public class GenericSearch extends HttpServlet {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(GenericSearch.class);
	
	private static final long serialVersionUID = 1L;

	private static final String operationID = "Search";
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GenericSearch() {
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

		HttpSession session = request.getSession();
		ASLSession mysession = null;
		mysession = SessionManager.getInstance().getASLSession(session.getId(), username);
		
		ResultSetConsumerI rs = null;
		
		/* Submit search query */
		Query q = new Query();
		
//		//For android, return all available fields in the results, by setting the semantic enrichment flag to true 
		if(request.getParameter("allFields") != null && request.getParameter("allFields").equalsIgnoreCase("true")){
			logger.info("allFields flag is true, getting all fields!");
			q.setSemanticEnrichment(true);
		}
		else
			logger.info("allFields flag was false or not set -> return normal fileds!");
		
//		logger.debug("SCOPE IS: "+SessionManager.getInstance().getASLSession(session.getId(), username).getScope());
		
		try {
			List<String> paramsList = new ArrayList<String> ();
			String[] parameters = request.getParameterValues("searchTerms");
			
			for(String param: parameters)
				paramsList.add(param);
			
			rs = q.genericSearch(mysession, paramsList, new SearchClientImpl());
			
		} 
		catch(InitialBridgingNotCompleteException e) {
			logger.error("Exception:", e);
			response.sendError(500, "Internal Server Error!");
			return;
		}
		catch(InternalErrorException e) {
			logger.error("Exception:", e);
			response.sendError(500, "Internal Server Error!");
			return;
		}
		catch(QuerySyntaxException e) {
			logger.error("Exception:", e);
			return;
		} catch (SearchASLException e) {
			logger.error("Exception:", e);
			response.sendError(500, "Search service has failed to fulfill the request!");
		}
		
		/* Preparing the response */
		String numResults = request.getParameter("count");
		
		int num;
		if (numResults != null)
			num = Integer.parseInt(numResults);
		else {
			num = 10;	//default value
			numResults = "10";
		}
		
		
		int off_t;
		String offset = request.getParameter("startIndex");
		if (offset != null) {
			off_t = Integer.parseInt(offset);
		}
		else {
				off_t = 0;
				offset = "0";
		}
		
		HashMap<CollectionInfo, ArrayList<CollectionInfo>> collectionInfos;
		try {
			logger.info("Getting Available Collections");
			SearchHelper s_h = new SearchHelper(username, session.getId());
			collectionInfos = s_h.getAvailableCollections();
			logger.info("collectionInfos.size()" + collectionInfos.size());
			logger.info("collectionInfos.keySet().size()"+collectionInfos.keySet().size());
			
			for(CollectionInfo ci : collectionInfos.keySet()){
				logger.info("ci.getName()"+ci.getName()); 
				logger.info("ci.getPresentationFields().toArray().toString()"+ci.getPresentationFields().toArray().toString());
				logger.info("ci.getBrowsableFields().toArray().toString()" + ci.getBrowsableFields().toArray().toString());
			}
			
			if (collectionInfos != null)
				logger.info("Number of collection infos: " + collectionInfos.size());
		} catch (Exception e) {
			// Send error - Unauthorized
			response.sendError(401, "Login to a VRE is needed");
			return;
		}
		
		List<String> xmlResults = new ArrayList<String>();

		mysession = SessionManager.getInstance().getASLSession(session.getId(), username);
		
		try {
//			rs.setOnlyPresentables();
			xmlResults = rs.getResultsToText(num, off_t, mysession);
		}
		catch (gRS2ReaderException e1) {
			logger.error("Exception:", e1);
			return;
		}
		catch (gRS2RecordDefinitionException e1) {
			logger.error("Exception:", e1);
			return;
		}
		catch (gRS2BufferException e1) {
			logger.error("Exception:", e1);
			return;
		}
		
		/************************************************/
		if (request.getParameter("responseType") == null || request.getParameter("responseType").equals("") ||request.getParameter("responseType").equals("json")) {
			/* Create the JSON object for the response */
			response.setContentType("application/json");

			// languages
			JSONArray jArrayL = new JSONArray();
			List<String> languages = q.getAvailableLanguages();
			if(languages!=null){
				jArrayL.addAll(languages);
				JSONObject languagesJS = new JSONObject();
				languagesJS.put("Languages", jArrayL);
			}

			// browse fields
			JSONArray jArrayB = new JSONArray();
			List<Field> browseFields = q.getAvailableBrowseFields();
			if(browseFields!=null){
				for (int i = 0; i < browseFields.size(); i++) {
					JSONObject bJson = new JSONObject();
					bJson.put("id", browseFields.get(i).getId());
					bJson.put("name", browseFields.get(i).getName());
					jArrayB.add(bJson);
				}
			}

			// sort fields
			JSONArray jArraySo = new JSONArray();
			List<Field> sortFields = q.getAvailableSortFields();
			if(sortFields!=null){
				for (int i = 0; i < sortFields.size(); i++) {
					JSONObject bJson = new JSONObject();
					bJson.put("id", sortFields.get(i).getId());
					bJson.put("name", sortFields.get(i).getName());
					jArraySo.add(bJson);
				}
			}

			// search fields
			JSONArray jArraySe = new JSONArray();
			List<Field> searchFields = q.getAvailableSearchFields();
			if(searchFields!=null){
				for (int i = 0; i < searchFields.size(); i++) {
					JSONObject bJson = new JSONObject();
					bJson.put("id", searchFields.get(i).getId());
					bJson.put("name", searchFields.get(i).getName());
					jArraySe.add(bJson);
				}
			}

			JSONObject responseJS = new JSONObject();

			responseJS.put("Languages", jArrayL);
			responseJS.put("AvailableBrowseFields", jArrayB);
			responseJS.put("AvailableSortFields", jArraySo);
			responseJS.put("AvailableSearchFields", jArraySe);

			PrintWriter out = response.getWriter();
			out.print(responseJS);

			out.flush();
			
			out.close();
			
		}
		/************************************************/

		else {
		
			
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = null;
			try {
				docBuilder = dbfac.newDocumentBuilder();
			}
			catch (ParserConfigurationException e) {
				logger.error("Exception:", e);
				return;
			} 
	
			
			Document doc = docBuilder.newDocument();
			Element root = doc.createElement("Results");
			doc.appendChild(root);
	
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {
				builder = factory.newDocumentBuilder();
			}
			catch (ParserConfigurationException e) {
				logger.error("Exception:", e);
				return;
			}
			Document document = null;

			
			for (int j = 0; j < xmlResults.size(); j++) {
				logger.info("Result: " + xmlResults.get(j));
				try {
//					ByteArrayInputStream encXML =  new  ByteArrayInputStream(xmlResults.get(j).getBytes("UTF8"));
//					document = builder.parse(encXML);
					document = builder.parse(new InputSource(new StringReader(xmlResults.get(j))));
				} catch (SAXException e) {
					logger.error("Exception:", e);
					return;
				}
	
				Element element = document.getDocumentElement();
				Node dup = doc.importNode(element, true);
				root.appendChild(dup);
			}
			
			
//			String xmlResultsStr = new String();
//			for(String xmlRes : xmlResults)
//				xmlResultsStr += xmlRes;
//			logger.debug("Results XML concatenated representation: "+xmlResultsStr);
			
			
			response.setContentType("text/xml; charset=UTF-8");
			PrintWriter out = response.getWriter();
			StringWriter writer = new StringWriter();
			try {
				DOMSource domSource = new DOMSource(doc);
				StreamResult result = new StreamResult(writer);
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				transformer.transform(domSource, result);
			} 
			catch (Exception e) {
				logger.error("Exception:", e);
			}
			finally {
				out.write(writer.toString());
				out.flush();
				out.close();
			}
			
		}

		return;
	}

}


