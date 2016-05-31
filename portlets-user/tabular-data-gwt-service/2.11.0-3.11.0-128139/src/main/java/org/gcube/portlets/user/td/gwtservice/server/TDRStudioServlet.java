/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.server;

import static org.gcube.data.analysis.rconnector.client.Constants.rConnector;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Returns a RSTudio link
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TDRStudioServlet extends HttpServlet {
	private static final String TAB_RESOURCE_ID_PARAMETER="TabResourceId";
	//private static final String SECURITY_EXCEPTION_RIGHTS = "Security exception, you don't have the required rights!";

	private static final long serialVersionUID = -1649268678733476057L;
	private static Logger logger = LoggerFactory
			.getLogger(TDRStudioServlet.class);
	
	

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
			logger.info("TDRStudioServlet");
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
			logger.debug("TDRSTudioServlet session id: "
					+ session.getId());

	        ASLSession aslSession=SessionUtil.getAslSession(session);
			
	        String tabResourceId=request.getParameter(TAB_RESOURCE_ID_PARAMETER);
			logger.debug("Request RStudio for TR: "+tabResourceId);
			
			URI url=rConnector().build().connect(Long.valueOf(tabResourceId));
			logger.debug("URL retrieved from rConnector: "+url.toString());
			
			//response.setStatus(HttpServletResponse.SC_OK);
			response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
			response.setHeader("Location", url.toString());
			response.setHeader("gcube-scope", aslSession.getScope());
			//response.setHeader("Set-Cookie", "PippoPlutoPaperino");
			//response.setHeader("Set-Cookie", cookieValue);
			
			logger.debug("Response: "+response.toString());
			
			logger.trace("Response in "
					+ (System.currentTimeMillis() - startTime)+"ms");
			
			
		
		} catch (Throwable e) {
			logger.error("Error TDRStudio: "
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
