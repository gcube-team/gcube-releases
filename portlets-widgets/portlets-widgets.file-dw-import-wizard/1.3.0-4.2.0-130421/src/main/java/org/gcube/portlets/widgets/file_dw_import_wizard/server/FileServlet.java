/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.gcube.portlets.widgets.file_dw_import_wizard.server.file.FileUtil;
import org.gcube.portlets.widgets.file_dw_import_wizard.server.file.ImportSessionManager;
import org.gcube.portlets.widgets.file_dw_import_wizard.server.file.ImportSessions;
import org.gcube.portlets.widgets.file_dw_import_wizard.shared.FileType;

public class FileServlet extends HttpServlet {


	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4197748678713054285L;

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		handleRequest(req, resp);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		handleRequest(req, resp);
	}

	protected void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		long startTime = System.currentTimeMillis();

		String sessionId = request.getHeader("sessionId");


		ImportSessions importSession = ImportSessionManager.getInstance()
				.getSession(sessionId);

		if (importSession == null) {
		
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"ERROR-Error getting the user session, no session found for id "
							+ sessionId);
			return;
		}


		if (importSession.getType() == FileType.GENERAL) {
			response.setContentType("application/json; charset=utf-8");
			Charset outputCharset = Charset.forName("Utf-8");
			FileUtil.toJson(new FileInputStream(importSession.getFile()),
					response.getOutputStream(), outputCharset, 100);
		} else {
			response.setContentType("application/zip");
			Charset outputCharset = Charset.forName("ISO-8859-1");

			FileUtil.toJson(new FileInputStream(importSession.getFile()),
					response.getOutputStream(), outputCharset, 100);

		}
		// ByteArrayOutputStream os = new ByteArrayOutputStream();

		/*
		 * logger.trace("json: "+os.toString());
		 * response.getOutputStream().write(os.toByteArray());
		 */

		response.setStatus(HttpServletResponse.SC_OK);
	}

}
