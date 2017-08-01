/**
 * 
 */
package org.gcube.portlets.user.tdw.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.portlets.user.tdw.server.datasource.DataSource;
import org.gcube.portlets.user.tdw.server.datasource.Direction;
import org.gcube.portlets.user.tdw.server.util.SessionUtil;
import org.gcube.portlets.user.tdw.shared.ServletParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * 
 */
public class TabularDataServlet extends HttpServlet {

	private static final long serialVersionUID = 3995054634540860599L;

	protected Logger log = LoggerFactory.getLogger(TabularDataServlet.class);

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
		log.debug("TabularDataServlet handleRequest");

		try {
			// TD SESSION ID
			String tdSessionIdParameter = request
					.getHeader(ServletParameters.TD_SESSION_ID);
			log.trace("tdSessionIdParameter: " + tdSessionIdParameter);
			int tdSessionId = Integer.parseInt(tdSessionIdParameter);

			// SORTING DIRECTION
			String sortDirectionParameter = request
					.getParameter(ServletParameters.SORTDIR);
			log.trace("sortDirectionParameter: " + sortDirectionParameter);
			Direction direction = (sortDirectionParameter == null) ? Direction.ASC
					: Direction.valueOf(sortDirectionParameter);

			// SORTING COLUMN
			String sortColumn = request
					.getParameter(ServletParameters.SORTFIELD);
			log.trace("sortColumn: " + sortColumn);

			// START
			String startParameter = request
					.getParameter(ServletParameters.OFFSET);
			log.trace("startParameter: " + startParameter);
			int start = Integer.parseInt(startParameter);

			// LIMIT
			String limitParameter = request
					.getParameter(ServletParameters.LIMIT);
			log.trace("limitParameter: " + limitParameter);
			int limit = Integer.parseInt(limitParameter);

			DataSource dataSource = SessionUtil.getDataSource(
					request.getSession(), tdSessionId);
			String json = dataSource.getDataAsJSon(start, limit, sortColumn,
					direction);
			response.getOutputStream().write(json.getBytes());
			response.setStatus(HttpServletResponse.SC_OK);
			// logger.trace("JSON: "+json);
			log.trace("Response sent (" + json.length() + " bytes)");

		} catch (Exception e) {
			log.error("Error processing the json data request", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error processing the json data request: " + e.getMessage());
			return;
		}
	}

}
