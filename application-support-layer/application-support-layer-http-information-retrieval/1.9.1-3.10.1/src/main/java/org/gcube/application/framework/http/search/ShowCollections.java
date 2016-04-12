package org.gcube.application.framework.http.search;





import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.gcube.application.framework.http.anonymousaccess.management.AuthenticationResponse;
import org.gcube.application.framework.http.anonymousaccess.management.CallAuthenticationManager;
import org.gcube.application.framework.search.library.impl.SearchHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class ShowCollections
 */
public class ShowCollections extends HttpServlet {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(ShowCollections.class);
	
	private static final long serialVersionUID = 1L;

	private static final String operationID = "ShowCollections";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ShowCollections() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//-- Verify if the call is authenticated
		HttpSession session = request.getSession();
		logger.debug("The jsession id of the request on ShowCollections is: " + session.getId());
		AuthenticationResponse authenticationResp = CallAuthenticationManager.authenticateCall(request, operationID);
		if (!authenticationResp.isAuthenticated()) {
			response.sendError(401, authenticationResp.getUnauthorizedErrorMessage());
			return;
		}

		String username = authenticationResp.getUserId();

		//get the collection infos
		HashMap<org.gcube.application.framework.search.library.model.CollectionInfo, ArrayList<org.gcube.application.framework.search.library.model.CollectionInfo>> collectionInfos = null;
		try {
			SearchHelper s_h = new SearchHelper(username, request.getSession().getId());
			collectionInfos = s_h.getAvailableCollections();
		} catch (Exception e) {
			logger.debug("Got an exception from Search Helper!");
			logger.error("Exception:", e);
			response.sendError(401, "Login to a Scope is needed");
			return;
		}

		if (request.getParameter("responseType") == null || request.getParameter("responseType").equals("") ||request.getParameter("responseType").equals("json")) {
			/******************************************/
			/* Create the JSON response */
			JSONArray jArrayCollectionGroups = new JSONArray();
			for (org.gcube.application.framework.search.library.model.CollectionInfo col:collectionInfos.keySet()) {
				JSONObject jsonColGroup = new JSONObject();
				jsonColGroup.put("name", col.getName());
				JSONArray jArrayCollections = new JSONArray();
				for (int j = 0; j < collectionInfos.get(col).size(); j++) {
					JSONObject jsonCol = new JSONObject();
					jsonCol.put("name", collectionInfos.get(col).get(j).getName());
					jsonCol.put("colId", collectionInfos.get(col).get(j).getId());

					jArrayCollections.add(jsonCol);
				}
				jsonColGroup.put("collections", jArrayCollections);
				jArrayCollectionGroups.add(jsonColGroup);
			}

			PrintWriter out = response.getWriter();
			out.print(jArrayCollectionGroups);
			out.flush();
			out.close();
		}

		else {
			PrintWriter out = response.getWriter();
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = null;
			try {
				docBuilder = dbfac.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				logger.error("Exception:", e);
			} 

			Document doc = docBuilder.newDocument();
			Element root = doc.createElement("Collections");
			doc.appendChild(root);


			for (org.gcube.application.framework.search.library.model.CollectionInfo col:collectionInfos.keySet()) {
				Element collectionGroupEl = doc.createElement("CollectionGroup");
				collectionGroupEl.setAttribute("name", col.getName());
				for (int j = 0; j < collectionInfos.get(col).size(); j++) {
					Element collectionEl = doc.createElement("Collection");

					Element collectionNameEl = doc.createElement("name");
					collectionNameEl.setTextContent(collectionInfos.get(col).get(j).getName());
					collectionEl.appendChild(collectionNameEl);

					Element collectionIdEl = doc.createElement("colId");
					collectionIdEl.setTextContent(collectionInfos.get(col).get(j).getId());
					collectionEl.appendChild(collectionIdEl);

					collectionGroupEl.appendChild(collectionEl);
				}
				root.appendChild(collectionGroupEl);
			}


			response.setContentType("text/xml");
			String xmlToStr = "";
			StringWriter writer = new StringWriter();
			try {
				DOMSource domSource = new DOMSource(doc);
				StreamResult result = new StreamResult(writer);
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				transformer.transform(domSource, result);		
			} catch (Exception e) {
				logger.error("Exception:", e);
			}
			xmlToStr = writer.toString();
			logger.debug(xmlToStr);
			out.write(xmlToStr);

			out.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
