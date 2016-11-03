package org.gcube.application.datamanagementfacilityportlet.servlet;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.application.aquamaps.aquamapsservice.client.proxies.DataManagement;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.OrderDirection;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.SystemTable;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.Tags;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubmittedReportServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8507604825033725966L;

	
	private static final Logger logger = LoggerFactory.getLogger(SubmittedReportServlet.class);
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req, resp);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException  {
		handleRequest(req, resp);
	}
	
	protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
		
		int startIndex=0;
		int limit=100;
		String orderColumn=request.getParameter(Tags.sort);
		String orderDirection=request.getParameter(Tags.dir);
		try{
			startIndex=Integer.parseInt(request.getParameter(Tags.START));
			limit=Integer.parseInt(request.getParameter(Tags.LIMIT));
		}catch (NumberFormatException e){
			//Some requestes could not have start / offset
		}
		try{
		ScopeProvider.instance.set(Utils.getSession(request.getSession()).getScope().toString());
		response.setContentType(Tags.JSONUTF8);
		response.setStatus(HttpServletResponse.SC_OK);
		DataManagement dm=dataManagement().build();
		PagedRequestSettings settings= new PagedRequestSettings(limit, startIndex, orderColumn, OrderDirection.valueOf(orderDirection));
		response.getWriter().write(dm.getJSONView(settings, dm.getSystemTableName(SystemTable.DATASOURCE_GENERATION_REQUESTS), null));
		
		
		
		}catch(Exception e ){
			logger.error("Unexpected error while trying to handle request ", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		
//		response.getWriter().write(Tags.EMPTY_JSON);
		
	}
	
	
}
