package org.gcube.portlets.user.td.gwtservice.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gcube.portlets.user.td.gwtservice.server.util.ServiceCredentials;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class TabularDataRowsXServlet extends HttpServlet {

	private static final long serialVersionUID = 3995054634540860599L;

	protected Logger logger = LoggerFactory
			.getLogger(TabularDataRowsXServlet.class);

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
		logger.debug("TabularDataServlet handleRequest");

		try {

			HttpSession session = request.getSession();

			if (session == null) {
				logger.error("Error getting the upload session, no session valid found: "
						+ session);
				response.sendError(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"ERROR-Error getting the user session, no session found"
								+ session);
				return;
			}
			logger.info("Session id: " + session.getId());

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
						"Error retrieving credentials:" + e.getLocalizedMessage(),
						e);
				throw new ServletException(e.getLocalizedMessage());
			}

			ServletInputStream in = request.getInputStream();
			InputStreamReader buffIn = new InputStreamReader(in);
			char[] buffer = new char[1024];
			int n = 0;
			StringWriter out = new StringWriter();

			while (-1 != (n = buffIn.read(buffer))) {
				out.write(buffer, 0, n);
			}
			logger.debug("Stream: " + out);

			JSONObject jsonRequest = new JSONObject(out.toString());
			logger.debug("JSON request:" + jsonRequest.toString());

			// RowIds
			JSONArray ids = null;
			ArrayList<String> rowsIds = new ArrayList<String>();
			try {
				ids = jsonRequest.getJSONArray("rowsIds");
			} catch (JSONException e) {
				logger.debug("rowsIds: " + e.getLocalizedMessage());
			}

			if (ids != null) {
				for (int i = 0; i < ids.length(); i++) {
					String filter = ids.getString(i);

					rowsIds.add(filter);
				}
			}
			logger.debug("rowsIds:" + rowsIds.size());

			/*
			 * DataSourceX dataSource = SessionUtil.getDataSource(
			 * request.getSession(), tdSessionId);
			 */
			String json = new String();// = dataSource.getRowsAsJson(rowsIds);

			response.getOutputStream().write(json.getBytes());
			response.setStatus(HttpServletResponse.SC_OK);
			// logger.trace("JSON: "+json);
			logger.trace("Response sent (" + json.length() + " bytes)");

		} catch (Exception e) {
			logger.error("Error processing the json data request", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error processing the json data request: " + e.getMessage());
			return;
		}
	}

}
