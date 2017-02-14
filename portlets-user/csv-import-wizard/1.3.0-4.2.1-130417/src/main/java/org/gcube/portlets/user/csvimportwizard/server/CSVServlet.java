/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.portlets.user.csvimportwizard.server.csv.CSVFileUtil;
import org.gcube.portlets.user.csvimportwizard.server.csv.CSVImportSession;
import org.gcube.portlets.user.csvimportwizard.server.csv.CSVImportSessionManager;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CSVServlet extends HttpServlet {

	protected static Logger logger = LoggerFactory.getLogger(CSVServlet.class);

	
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

		logger.trace("CSVServlet");
		long startTime = System.currentTimeMillis();

		String sessionId = request.getHeader("sessionId");

		logger.trace("sessionId: "+sessionId);

		CSVImportSession importSession = CSVImportSessionManager.getInstance().getSession(sessionId);

		if (importSession == null)
		{
			logger.error("Error getting the upload session, no session found for id "+sessionId);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "ERROR-Error getting the user session, no session found for id "+sessionId);
			return ;
		}

		logger.trace("opening as csv under import");

		response.setContentType("application/json; charset=utf-8");
		Charset outputCharset = Charset.forName("Utf-8");

		//ByteArrayOutputStream os = new ByteArrayOutputStream();
		CSVFileUtil.toJson(new FileInputStream(importSession.getCsvFile()), response.getOutputStream(), outputCharset, importSession.getParserConfiguration(), 100);
		
		/*logger.trace("json: "+os.toString());
		response.getOutputStream().write(os.toByteArray());*/

		response.setStatus(HttpServletResponse.SC_OK);
		logger.trace("Response in "+(System.currentTimeMillis()-startTime));
	}

}
