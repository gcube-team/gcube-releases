package org.gcube.application.framework.http.content.access;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.gcube.application.framework.contentmanagement.content.impl.DigitalObject;
import org.gcube.application.framework.contentmanagement.content.impl.DigitalObjectType;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.http.anonymousaccess.management.AuthenticationResponse;
import org.gcube.application.framework.http.anonymousaccess.management.CallAuthenticationManager;
import org.gcube.application.framework.http.content.access.tools.ContentConstants;
import org.gcube.application.framework.http.content.access.tools.ContentConsumers;
import org.gcube.application.framework.http.content.access.tools.ContentParsers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class ContentViewer
 */
public class ContentViewer extends HttpServlet {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(ContentViewer.class);
	
	private static final long serialVersionUID = 1L;
	
	private static final String operationID = "GetContent";
	
	//private static final int maxChar = 1024;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ContentViewer() {
		super();
	}

	protected void doHead(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

			String mime = "";
			String name = "";
			String collectionType = "";
			
			long length = 0;
			try {
				String save = request.getParameter("save");
				
				String uri = request.getParameter("documentURI");
				logger.info("The document URI: " + uri);
				
				if (uri == null || uri.equals("")) {
					// the document has not id - not found
					response.sendError(404);
					return;
				}
				
				ASLSession session = SessionManager.getInstance().getASLSession(request.getSession().getId(), username);
				DigitalObject digObj = new DigitalObject(session, uri);

				logger.debug("Session ID of request: " + request.getSession().getId());
				if(session!=null)
					logger.debug("Matches on ASL session with id:" + session.getExternalSessionID() + " of user: " 
									+session.getUsername()+ " and scope: " + session.getScope().toString());
				
				mime = digObj.getMimeType();
				length = digObj.getLength();
				name = digObj.getCollectionName();
				if(digObj.getType()!=null)
					collectionType = digObj.getType().toString();
				String oid = digObj.getObjectId();
				response.addHeader("Content-Name", name);
				response.addHeader("contentID", oid);

				String secondaryURLs = request.getParameter("secondaryURLs");
				if(secondaryURLs==null)
					secondaryURLs = "";
				
				String link = null;
				
				if(uri.contains("http://") && uri.contains("/tree/")){ //tree collection
					logger.info("About to get tree object");
				
					String content = digObj.getContent();
					
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
					logger.info("link is probably from OpenSearch...");
					if(uri.contains("?"))
						link = uri.split("?")[0];
					else
						link = uri;			
				}
				
				OutputStream out = response.getOutputStream();
				
				if( (link!=null) && (link!="")){
					URLConnection urlConn = new URL(link).openConnection();
					String mimeType = urlConn.getContentType();
					int fileSize = urlConn.getContentLength();
					logger.debug("Detected MIME type: " + mimeType);
					response.setContentType(mimeType);
					response.setContentLength(fileSize);
					ContentConsumers.getRawContent(link,out);
				}
				else
					logger.error("No data link found");

				out.flush();
				out.close();
			} catch (Exception e) {
				logger.error("Exception:", e);
			}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
