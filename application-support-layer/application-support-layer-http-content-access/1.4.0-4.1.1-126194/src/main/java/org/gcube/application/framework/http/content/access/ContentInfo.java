package org.gcube.application.framework.http.content.access;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import org.gcube.application.framework.contentmanagement.content.impl.DigitalObject;
import org.gcube.application.framework.contentmanagement.content.impl.DigitalObjectType;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.http.anonymousaccess.management.AuthenticationResponse;
import org.gcube.application.framework.http.anonymousaccess.management.CallAuthenticationManager;
import org.gcube.application.framework.http.content.access.tools.ContentConstants;
import org.gcube.application.framework.http.content.access.tools.ContentParsers;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class ContentInfo
 */
public class ContentInfo extends HttpServlet {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(ContentInfo.class);
	
	private static final long serialVersionUID = 1L;
	
	private static final String operationID = "ShowContentInfos";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ContentInfo() {
        super();
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
		String documentURI = request.getParameter("documentURI");
		
		if(documentURI==null){
			logger.debug("documentURI parameter is null. did you forget it ? ");
			response.sendError(400, "No documentURI parameter provided");
			return;
		}
		else
			logger.debug("documentURI: " + documentURI);
		
		ASLSession aslSession = SessionManager.getInstance().getASLSession(request.getSession().getId(), username);
		DigitalObject digObj = new DigitalObject(aslSession, documentURI);
		String mime = "";
		long length = 0;
		
		// Get the name
		String name = digObj.getCollectionName();
		// Get the type
		String collectionType = null;
		if(digObj.getType()!=null)
			collectionType = digObj.getType().toString();
		
		logger.debug("digObj.getCollectionName(): " + name);
		
		String secondaryURLs = request.getParameter("secondaryURLs");
		if(secondaryURLs==null)
			secondaryURLs = "";
		
		String link = null;
		
		if(documentURI.contains("http://") && documentURI.contains("/tree/")){ //tree collection
			logger.info("About to get tree object");
		
			String content = digObj.getContent();
			
			//content here contains the full xml.
//			logger.debug("Object's full content is: " + content);
			
			TreeMap links = null;
			
			if(collectionType.equalsIgnoreCase(DigitalObjectType.FIGIS.toString()))
				links = ContentParsers.parseFIGIS_Payload(content);
			if(collectionType.equalsIgnoreCase(DigitalObjectType.OAI.toString()))
				links = ContentParsers.parseOAI_Payload(content);
			if(links!=null){
				if(secondaryURLs.equalsIgnoreCase("true")){
					List<String> alternativeURLs = (List<String>)links.get(ContentConstants.ALTERNATIVE_URLs);
					if((alternativeURLs!=null) && (alternativeURLs.size()>0))
						link = alternativeURLs.get(0);
				}
				else{ //main URLs
					List<String> mainURLs = (List<String>)links.get(ContentConstants.MAIN_URLs);
					if((mainURLs!=null) && (mainURLs.size()>0))
						link = mainURLs.get(0);
				}
			}
		}
		else{ //probably OpenSearch collection
			link = documentURI;
		}
		
		logger.debug("LINK is: "+link);
		
		URLConnection objConn = new URL(link).openConnection(); 
		mime = objConn.getContentType();// Get the mime type
		length = objConn.getContentLength();// Get the length

		logger.debug("MIME is: "+mime);
		logger.debug("length is: "+length);
		
		String oid = digObj.getObjectId();
		String colId = digObj.getCollectionID();
		
		logger.debug("oid is: "+oid);
		logger.debug("colId is: "+colId);
		
		
		/**********************/
		/* Create the JSON response Object */
		if (request.getParameter("responseType") == null || request.getParameter("responseType").equals("") ||request.getParameter("responseType").equals("json")) {
			response.setContentType("application/json");
			
			JSONObject responseObject = new JSONObject();
			
			try {
				if(oid!=null) responseObject.put("OID", oid);
				if(colId!=null) responseObject.put("CID", colId);
				if(name!=null) responseObject.put("Name", name);
				if(mime!=null) responseObject.put("MimeType", mime);
				responseObject.put("Length", String.valueOf(length));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			OutputStream out = response.getOutputStream();
			out.write(responseObject.toString().getBytes());
//			out.flush();
			out.close();
		}
		/**********************/
		else {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = null;
			try {
				docBuilder = dbfac.newDocumentBuilder();
			} catch (ParserConfigurationException exep) {
				logger.error("Exception:", exep);
			} 

			Document doc = docBuilder.newDocument();
			Element root = doc.createElement("ContentInfos");
			doc.appendChild(root);

			Element nameEl = doc.createElement("Name");
			nameEl.setTextContent(name);
			root.appendChild(nameEl);

			Element oidEl = doc.createElement("ObjectId");
			oidEl.setTextContent(oid);
			root.appendChild(oidEl);

			Element mimeEl = doc.createElement("MimeType");
			mimeEl.setTextContent(mime); 
			root.appendChild(mimeEl);

			Element lengthEl = doc.createElement("Length");
			lengthEl.setTextContent(String.valueOf(length));
			root.appendChild(lengthEl);

			response.setContentType("text/xml");
			PrintWriter out = response.getWriter();
			String xmlToStr = "";	
			StringWriter writer = new StringWriter();
			try {
				DOMSource domSource =new DOMSource(doc);
				StreamResult result = new StreamResult(writer);
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				transformer.transform(domSource, result);
			} catch (Exception exep) {
				logger.error("Exception:", exep);
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
