/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portlets.user.statisticalmanager.server.util.SessionUtil;
import org.gcube.portlets.user.statisticalmanager.server.util.StorageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class DownloadServlet extends HttpServlet {
	private static Logger logger = LoggerFactory
			.getLogger(DownloadServlet.class);

	private static final long serialVersionUID = -8423345575690165644L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		logger.info("DownloadServlet");

		HttpSession session = req.getSession();

		if (session == null) {
			logger.error("Error getting the session, no session valid found: "
					+ session);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"ERROR-Error getting the user session, no session found "
							+ session);
			return;
		}
		logger.debug("DownloadServlet session id: " + session.getId());


		ASLSession aslSession;
		try {
			aslSession = SessionUtil.getASLSession(session);
			String token = SessionUtil.getToken(aslSession);
			logger.debug("UserToken: " + token);

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServletException(e.getLocalizedMessage());
		}
		
		
		String requestType = (String) req.getParameter("type");
		String smpUrl = (String) req.getParameter("url");
		String fileName = req.getParameter("name");

		System.out.println("DOWNLOAD SERVLET");
		System.out.println("SMP URL: " + smpUrl);
		System.out.println("File name: " + fileName);

		try {
			if (requestType == null
					|| !requestType.toUpperCase().contentEquals("IMAGES"))
				resp.setHeader("Content-Disposition", "attachment; filename=\""
						+ fileName + "\"");

			InputStream inputStream = StorageUtil
					.getStorageClientInputStream(smpUrl);

			OutputStream out = resp.getOutputStream();
			IOUtils.copy(inputStream, resp.getOutputStream());
			out.close();
		} catch (Exception e) {
			System.out.println("Error during external item sending " + smpUrl);
			// e.printStackTrace();
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error during data retrieving: " + e.getMessage());
			return;
		}
	}

}
