/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TDLogsServlet extends HttpServlet {

	private static final long serialVersionUID = -737451890907300011L;
	protected static Logger logger = LoggerFactory
			.getLogger(TDLogsServlet.class);

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

		logger.debug("TDLogsServlet");
		long startTime = System.currentTimeMillis();

		HttpSession session = request.getSession();

		if (session == null) {
			logger.error("Error getting the upload session, no session valid found: "
					+ session);
			response.sendError(HttpServletResponse.SC_REQUEST_TIMEOUT,
					"ERROR-Error getting the user session, no session found"
							+ session);
			return;
		}

		logger.debug("TDLogsServlet import session id: " + session.getId());

		try {
			ASLSession aslSession = SessionUtil.getAslSession(session);
			String token = SessionUtil.getToken(aslSession);
			logger.debug("UserToken: " + token);

		} catch (TDGWTServiceException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServletException(e.getLocalizedMessage());
		}
		
		ByteArrayInputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			response.setContentType("text/html; charset=utf-8");
			String relativeWebPath = "/logs/TabularDataManagerLogBack.log";
			out = new ByteArrayOutputStream();

			String absoluteDiskPath = getServletContext().getRealPath(
					relativeWebPath);
			File file = new File(absoluteDiskPath);

			in = new ByteArrayInputStream(FileUtils.readFileToByteArray(file));

			IOUtils.copy(in, out);

			response.getOutputStream().write(out.toByteArray());

			response.setStatus(HttpServletResponse.SC_OK);

		} catch (FileNotFoundException e) {
			logger.error("File not found: " + e.getLocalizedMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					e.getLocalizedMessage());

		} catch (IOException e) {
			logger.error("IO error: " + e.getLocalizedMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					e.getLocalizedMessage());
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}

		logger.debug("Response in " + (System.currentTimeMillis() - startTime));
	}
}
