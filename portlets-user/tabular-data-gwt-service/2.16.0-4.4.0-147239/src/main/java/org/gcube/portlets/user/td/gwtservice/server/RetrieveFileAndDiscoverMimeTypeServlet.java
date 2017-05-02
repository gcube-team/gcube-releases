/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gcube.portlets.user.td.gwtservice.server.util.ServiceCredentials;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Returns a file from storage and discover mime type
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class RetrieveFileAndDiscoverMimeTypeServlet extends HttpServlet {
	private static final long serialVersionUID = -1649268678733476057L;

	private static Logger logger = LoggerFactory
			.getLogger(RetrieveFileAndDiscoverMimeTypeServlet.class);

	private static final String ATTRIBUTE_STORAGE_URI = "storageURI";

	// private static final int BUFSIZE = 4096;

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
		try {
			logger.info("RetrieveFileAndDiscoverMimeTypeServlet");
			long startTime = System.currentTimeMillis();

			HttpSession session = request.getSession();

			if (session == null) {
				logger.error("Error getting the session, no session valid found: "
						+ session);
				response.sendError(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"ERROR-Error getting the user session, no session found "
								+ session);
				return;
			}
			logger.debug("RetrieveFileAndDiscoverMimeTypeServlet session id: "
					+ session.getId());
			
			@SuppressWarnings("unused")
			ServiceCredentials serviceCredentials;

			String scopeGroupId = request.getHeader(Constants.CURR_GROUP_ID);
			if (scopeGroupId == null || scopeGroupId.isEmpty()) {
				scopeGroupId = request.getParameter(Constants.CURR_GROUP_ID);
				if (scopeGroupId == null || scopeGroupId.isEmpty()) {
					logger.error("CURR_GROUP_ID is null, it is a mandatory parameter in custom servlet: "
							+ scopeGroupId);
					throw new ServletException(
							"CURR_GROUP_ID is null, it is a mandatory parameter in custom servlet: "
									+ scopeGroupId);
				}
			}

			try {
				serviceCredentials = SessionUtil.getServiceCredentials(request,
						scopeGroupId);

			} catch (TDGWTServiceException e) {
				logger.error(
						"Error retrieving credentials:"
								+ e.getLocalizedMessage(), e);
				throw new ServletException(e.getLocalizedMessage());
			}
			
			String uri = (String) request.getParameter(ATTRIBUTE_STORAGE_URI);
			logger.debug("Request storage uri: " + uri);

			if (uri == null || uri.isEmpty()) {
				logger.error("Error getting request uri: " + uri);
				response.sendError(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"ERROR-Error getting request uri: " + session);
				return;
			}

			/*
			 * 
			 * FilesStorage storage = new FilesStorage(); InputStream inMime =
			 * storage.retrieveInputStream(uri);
			 * 
			 * 
			 * TikaConfig tika = new TikaConfig();
			 * 
			 * 
			 * MediaType mimeType=null; try { mimeType =
			 * tika.getDetector().detect( inMime, new Metadata());
			 * 
			 * } catch(Throwable e){
			 * logger.error("RetrieveFileAndDiscoverMimeTypeServlet parse: "
			 * +e.getLocalizedMessage()); } finally { inMime.close(); }
			 * 
			 * 
			 * if (mimeType == null) { response.setContentType("unknown");
			 * logger.debug("Discover Mime Type: unknown"); } else {
			 * response.setContentType(mimeType.toString());
			 * logger.debug("Discover Mime Type: "+mimeType.toString());
			 * 
			 * }
			 * 
			 * InputStream in = storage.retrieveInputStream(uri);
			 * 
			 * OutputStream out = response.getOutputStream();
			 * 
			 * byte[] byteBuffer = new byte[BUFSIZE];
			 * 
			 * 
			 * int length = 0; while ((in != null) && ((length =
			 * in.read(byteBuffer)) != -1)) { out.write(byteBuffer, 0, length);
			 * } response.setStatus(HttpServletResponse.SC_OK); in.close();
			 * out.close();
			 */
			logger.trace("Response in "
					+ (System.currentTimeMillis() - startTime));

		} catch (Throwable e) {
			logger.error("Error retrieving file from storage: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			response.sendError(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error retrieving file from storage: "
							+ e.getLocalizedMessage());
			return;
		}
	}
}
