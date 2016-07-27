/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.server;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portlets.user.td.gwtservice.server.file.CSVFileUploadSession;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVFileUtil;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Returns a representation of the csv file in JSON
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class CSVImportFileServlet extends HttpServlet {

	protected static Logger logger = LoggerFactory.getLogger(CSVImportFileServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -4197748678713054285L;

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req, resp);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException  {
		handleRequest(req, resp);
	}

	protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.info("CSVImportFileServlet");
		long startTime = System.currentTimeMillis();

				
		HttpSession session = request.getSession();
		
		if(session==null){
			logger.error("Error getting the upload session, no session valid found: "+session);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "ERROR-Error getting the user session, no session found"+session);
			return ;
		}
		logger.info("CSVImportFileServlet import session id: "+session.getId());

		try {
			ASLSession aslSession = SessionUtil.getAslSession(session);
			String token = SessionUtil.getToken(aslSession);
			logger.debug("UserToken: " + token);

		} catch (TDGWTServiceException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServletException(e.getLocalizedMessage());
		}
		
		
		CSVFileUploadSession fileUploadSession=SessionUtil.getCSVFileUploadSession(session);
				
		if (fileUploadSession == null)
		{
			logger.error("Error getting the upload session, no fileUploadSession found: "+fileUploadSession);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "ERROR-Error getting the user session, no fileUploadSession found: "+fileUploadSession);
			return ;
		}

		response.setContentType("application/json; charset=utf-8");
		Charset outputCharset = Charset.forName("Utf-8");

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		CSVFileUtil.toJson(new FileInputStream(fileUploadSession.getCsvFile()), os, outputCharset, fileUploadSession.getParserConfiguration(), 100);
		
		logger.trace("json: "+os.toString());
		System.out.println("json: "+os.toString());
		response.getOutputStream().write(os.toByteArray());

		//CSVFileUtil.toJson(new FileInputStream(importSession.getCsvFile()), response.getOutputStream(), outputCharset, importSession.getParserConfiguration(), 100);
		
		response.setStatus(HttpServletResponse.SC_OK);
		logger.trace("Response in "+(System.currentTimeMillis()-startTime));
	}

}
