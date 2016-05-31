package org.gcube.portlets.user.tdwx.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.portlets.user.tdwx.server.datasource.DataSourceX;
import org.gcube.portlets.user.tdwx.server.datasource.Direction;
import org.gcube.portlets.user.tdwx.server.util.SessionUtil;
import org.gcube.portlets.user.tdwx.shared.FilterInformation;
import org.gcube.portlets.user.tdwx.shared.ServletParameters;
import org.gcube.portlets.user.tdwx.shared.SortInformation;
import org.gcube.portlets.user.tdwx.shared.StaticFilterInformation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TabularDataXServlet extends HttpServlet {

	private static final long serialVersionUID = 3995054634540860599L;

	protected Logger logger = LoggerFactory
			.getLogger(TabularDataXServlet.class);

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

			// TD SESSION ID
			String tdSessionIdParameter = request
					.getHeader(ServletParameters.TD_SESSION_ID);
			logger.trace("tdSessionIdParameter: " + tdSessionIdParameter);
			int tdSessionId = Integer.parseInt(tdSessionIdParameter);

			ServletInputStream in = request.getInputStream();
			InputStreamReader buffIn = new InputStreamReader(in);
			char[] buffer = new char[1024];
			int n = 0;
			StringWriter out = new StringWriter();

			while (-1 != (n = buffIn.read(buffer))) {
				out.write(buffer, 0, n);
			}
			;
			logger.debug("Stream: " + out);

			JSONObject jsonRequest = new JSONObject(out.toString());
			logger.debug("JSON request:" + jsonRequest.toString());
			int limit = jsonRequest.getInt("limit");
			int start = jsonRequest.getInt("offset");

			logger.debug("Start: " + start + " Limit:" + limit);
			
			
			//SORT
			JSONArray sorts = null;
			SortInformation sortInfo = null;

			try {
				sorts = jsonRequest.getJSONArray("sorts");
			} catch (JSONException e) {
				logger.debug("sorts: " + e.getLocalizedMessage());
			}
			
			if (sorts != null) {
				for (int i = 0; i < sorts.length(); i++) {
					JSONObject sort = sorts.getJSONObject(i);
					sortInfo = new SortInformation(sort.getString("sortField"),
							sort.getString("sortDir"));
				}
			}
			logger.debug("SortInformation:" + sortInfo);

			Direction direction;
			String sortColumn = null;
			if (sortInfo == null) {
				direction = Direction.ASC;
			} else {
				direction = sortInfo.getSortDir().compareTo("ASC") == 0 ? Direction.ASC
						: Direction.DESC;
				sortColumn = sortInfo.getSortField();
			}

			//FILTERS
			JSONArray filters = null;
			ArrayList<FilterInformation> filtersList=new ArrayList<FilterInformation>();
			try {
				filters = jsonRequest.getJSONArray("filters");
			} catch (JSONException e) {
				logger.debug("filters: " + e.getLocalizedMessage());
			}
			
			if (filters != null) {
				for (int i = 0; i < filters.length(); i++) {
					JSONObject filter = filters.getJSONObject(i);
					FilterInformation fi= new FilterInformation(filter.getString("filterField"),
							filter.getString("filterType"),
							filter.getString("filterComparison"),
							filter.getString("filterValue"));
					filtersList.add(fi);
				}
			}
			logger.debug("Filters:" + filtersList.size());
			
			
			//STATIC FILTERS
			JSONArray staticFilters = null;
			ArrayList<StaticFilterInformation> staticFiltersList=new ArrayList<StaticFilterInformation>();
			try {
				staticFilters = jsonRequest.getJSONArray("staticFilters");
			} catch (JSONException e) {
				logger.debug("staticFilters: " + e.getLocalizedMessage());
			}
			
			if (staticFilters != null) {
				for (int i = 0; i < staticFilters.length(); i++) {
					JSONObject staticFilter = staticFilters.getJSONObject(i);
					StaticFilterInformation sfi= new StaticFilterInformation(staticFilter.getString("columnName"),
							staticFilter.getString("columnLocalId"),
							staticFilter.getString("filterValue"));
					staticFiltersList.add(sfi);
				}
			}
			logger.debug("StaticFilters:" + staticFiltersList.size());
			


			DataSourceX dataSource = SessionUtil.getDataSource(
					request.getSession(), tdSessionId);
			String json = dataSource.getDataAsJSon(start, limit, sortColumn,
					direction, filtersList, staticFiltersList);

		
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
