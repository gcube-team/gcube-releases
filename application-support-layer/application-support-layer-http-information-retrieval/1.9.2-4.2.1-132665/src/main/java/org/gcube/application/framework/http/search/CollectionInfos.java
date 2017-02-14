package org.gcube.application.framework.http.search;



import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.http.anonymousaccess.management.AuthenticationResponse;
import org.gcube.application.framework.http.anonymousaccess.management.CallAuthenticationManager;
import org.gcube.application.framework.http.error.messages.HTTPErrorMessages;
import org.gcube.application.framework.search.library.exception.InitialBridgingNotCompleteException;
import org.gcube.application.framework.search.library.exception.InternalErrorException;
import org.gcube.application.framework.search.library.impl.SearchHelper;
import org.gcube.application.framework.search.library.model.Field;
import org.gcube.application.framework.search.library.model.Query;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class CollectionInfos
 */
public class CollectionInfos extends HttpServlet {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(CollectionInfos.class);
	
	private static final long serialVersionUID = 1L;

	private static final String operationID = "ShowCollectionInfos";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CollectionInfos() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		//-- Check if the user is authenticated
		AuthenticationResponse authenticationResp = CallAuthenticationManager.authenticateCall(request, operationID);
		if (!authenticationResp.isAuthenticated()) {
			response.sendError(401, authenticationResp.getUnauthorizedErrorMessage());
			return;
		}

		String username = authenticationResp.getUserId();

		HashMap<org.gcube.application.framework.search.library.model.CollectionInfo, ArrayList<org.gcube.application.framework.search.library.model.CollectionInfo>> collectionInfos = null;
		try {
			SearchHelper s_h = new SearchHelper(username, request.getSession().getId());
			collectionInfos = s_h.getAvailableCollections();
		} catch (Exception e) {
			response.sendError(401, "Login to a VRE is needed");
			return;
		}
		ASLSession mysession = SessionManager.getInstance().getASLSession(session.getId(), username);

		/* Read JSON input */
		if (request.getParameter("selectedCollections") == null) {
			response.sendError(400, "Wrong Parameters given. No collections selected.");
			return;
		}
		JSONObject jsonO;
		try {
			jsonO = (JSONObject) JSONSerializer.toJSON(request.getParameter("selectedCollections"));
		} catch (Exception e) {
			response.sendError(400, HTTPErrorMessages.WrongJSONInput("selectedCollections"));
			return;
		}
		if (jsonO == null) {
			response.sendError(400, "selectedCollections parameter must be in JSON format.");
			return;
		}
		
		
		JSONArray selColArray = (JSONArray)jsonO.get("SelectedCollections");


		String[] selectedCollections = new String[selColArray.size()];
		for (int i = 0; i < selColArray.size(); i++) {
			selectedCollections[i] = (String)selColArray.get(i);
		}
		if (selectedCollections == null || selectedCollections.length == 0) {
			response.sendError(400, "No collections selected");
			return;
		}
		Query q = new Query();
		List<String> selCols = new ArrayList<String>();
		List<String> avCols = new ArrayList<String>();
		for (org.gcube.application.framework.search.library.model.CollectionInfo key:collectionInfos.keySet()) {
			ArrayList<org.gcube.application.framework.search.library.model.CollectionInfo> cols = collectionInfos.get(key);
			for (int i = 0; i < cols.size(); i++) {
				org.gcube.application.framework.search.library.model.CollectionInfo col = cols.get(i);
				avCols.add(col.getId());
			}
		}
		for (int i = 0; i < selectedCollections.length; i++) {
			if (!avCols.contains(selectedCollections[i])) {
				// send error - bad request
				response.sendError(400, "Wrong selected collection id given");
				return;
			}
			selCols.add(selectedCollections[i]);
		}
		try {
			q.selectCollections(selCols, true, mysession);
		} catch (Exception e) {
			response.sendError(400, "Wrong collection id given");
			return;
		}



		/************************************************/
		if (request.getParameter("responseType") == null || request.getParameter("responseType").equals("") ||request.getParameter("responseType").equals("json")) {
			/* Create the JSON object for the response */
			response.setContentType("application/json");

			// languages
			JSONArray jArrayL = new JSONArray();
			List<String> languages = q.getAvailableLanguages();
			jArrayL.addAll(languages);
			JSONObject languagesJS = new JSONObject();
			languagesJS.put("Languages", jArrayL);

			// browse fields
			JSONArray jArrayB = new JSONArray();
			List<Field> browseFields = q.getAvailableBrowseFields();
			for (int i = 0; i < browseFields.size(); i++) {
				JSONObject bJson = new JSONObject();
				bJson.put("id", browseFields.get(i).getId());
				bJson.put("name", browseFields.get(i).getName());
				jArrayB.add(bJson);
			}

			// sort fields
			JSONArray jArraySo = new JSONArray();
			List<Field> sortFields = q.getAvailableSortFields();
			for (int i = 0; i < sortFields.size(); i++) {
				JSONObject bJson = new JSONObject();
				bJson.put("id", sortFields.get(i).getId());
				bJson.put("name", sortFields.get(i).getName());
				jArraySo.add(bJson);
			}

			// search fields
			JSONArray jArraySe = new JSONArray();
			List<Field> searchFields = q.getAvailableSearchFields();
			for (int i = 0; i < searchFields.size(); i++) {
				JSONObject bJson = new JSONObject();
				bJson.put("id", searchFields.get(i).getId());
				bJson.put("name", searchFields.get(i).getName());
				jArraySe.add(bJson);
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
			} catch (ParserConfigurationException e) {
				logger.error("Exception:", e);
			} 

			Document doc = docBuilder.newDocument();
			Element root = doc.createElement("CollectionsInfos");
			doc.appendChild(root);
			Element availableLanguagesEl = doc.createElement("AvailableLanguages");
			List<String> languages = q.getAvailableLanguages();
			if (languages != null) {
				for (int i = 0; i < languages.size(); i++) {
					Element languageEl = doc.createElement("language");
					Element languageNameEl = doc.createElement("name");
					languageNameEl.setTextContent(languages.get(i));
					languageEl.appendChild(languageNameEl);

					availableLanguagesEl.appendChild(languageEl);
				}
			}

			root.appendChild(availableLanguagesEl);
			Element browseFieldsEl = doc.createElement("AvailableBrowseFields");
			List<Field> browseFields = q.getAvailableBrowseFields();
			if (browseFields != null) {
				for (int i = 0; i < browseFields.size(); i++) {
					Element browseFieldEl = doc.createElement("browseField");
					Element fieldIdEl = doc.createElement("id");
					fieldIdEl.setTextContent(browseFields.get(i).getId());
					Element fieldNameEl = doc.createElement("name");
					fieldNameEl.setTextContent(browseFields.get(i).getLabel());

					browseFieldEl.appendChild(fieldIdEl);
					browseFieldEl.appendChild(fieldNameEl);
					browseFieldsEl.appendChild(browseFieldEl);
				}
			}
			root.appendChild(browseFieldsEl);

			Element sortFieldsEl = doc.createElement("AvailablesortFields");
			List<Field> sortFields = q.getAvailableSortFields();
			if (sortFields != null) {
				for (int i = 0; i < sortFields.size(); i++) {
					Element sortFieldEl = doc.createElement("sortField");
					Element fieldIdEl = doc.createElement("id");
					fieldIdEl.setTextContent(sortFields.get(i).getId());
					Element fieldNameEl = doc.createElement("name");
					fieldNameEl.setTextContent(sortFields.get(i).getLabel());

					sortFieldEl.appendChild(fieldIdEl);
					sortFieldEl.appendChild(fieldNameEl);
					sortFieldsEl.appendChild(sortFieldEl);
				}
			}
			root.appendChild(sortFieldsEl);



			Element searchFieldsEl = doc.createElement("AvailableSearchFields");
			String language = request.getParameter("language");
			if (language != null) {
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
			}
				List<Field> searchFields = q.getAvailableSearchFields();
				for (int i = 0; i < searchFields.size(); i ++) {
					Element searchFieldEl = doc.createElement("searchField");
					Element fieldIdEl = doc.createElement("id");
					fieldIdEl.setTextContent(searchFields.get(i).getId());
					Element fieldNameEl = doc.createElement("name");
					fieldNameEl.setTextContent(searchFields.get(i).getLabel());

					searchFieldEl.appendChild(fieldIdEl);
					searchFieldEl.appendChild(fieldNameEl);
					searchFieldsEl.appendChild(searchFieldEl);
				}
				root.appendChild(searchFieldsEl);
		//	} 

			response.setContentType("text/xml");
			PrintWriter out = response.getWriter();
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
			logger.info(xmlToStr);
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
