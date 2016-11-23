package org.gcube.application.framework.http.content.access;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gcube.application.framework.contentmanagement.content.impl.DigitalObject;
import org.gcube.application.framework.contentmanagement.util.ThumbnailConstants;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.http.anonymousaccess.management.AccessConstants;
import org.gcube.application.framework.http.anonymousaccess.management.AccessManager;
import org.gcube.application.framework.http.anonymousaccess.management.AuthenticationResponse;
import org.gcube.application.framework.http.anonymousaccess.management.CallAuthenticationManager;
import org.gcube.application.framework.http.anonymousaccess.management.FunctionAccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class ThumbnailViewer
 */
public class ThumbnailViewer extends HttpServlet {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(ThumbnailViewer.class);
	
	private static final long serialVersionUID = 1L;
	
	private static final String operationID = "GetThumbnails";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ThumbnailViewer() {
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
		
		
		try {
			String oid = request.getParameter("OID");
			String documentUri = request.getParameter("documentURI");
		//	String username = request.getParameter("username");
			int width = Integer.parseInt(request.getParameter("width"));
			int height = Integer.parseInt(request.getParameter("height"));
			String options = request.getParameter("options");
			
			OutputStream out = response.getOutputStream();
			ASLSession aslSession = SessionManager.getInstance().getASLSession(request.getSession().getId(), username);
			
			DigitalObject digObj = null;
			if (documentUri == null || documentUri.equals("")) {
				digObj = new DigitalObject(aslSession, oid, null);
			} else {
				digObj = new DigitalObject(aslSession, documentUri);
			}
			String mime = "image/png";
			String name = digObj.getCollectionName();
			response.addHeader("content_name", name);
			
			response.setContentType(mime);
			
			//logger.info("About to get the thumbnail...");
			//byte[] buf = digObj.getThumbnail(width, height, options+ "," + ThumbnailConstants.FORCE_CREATE);
			//out.write(buf);
			logger.info("Thumbnail successfully received!");
			out.flush();
			out.close();
		} catch (Exception e) {
			response.setContentType("unknown/unknown");
			logger.error("Exception:", e);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
