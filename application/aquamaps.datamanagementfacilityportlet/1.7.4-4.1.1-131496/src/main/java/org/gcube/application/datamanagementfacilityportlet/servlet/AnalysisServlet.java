package org.gcube.application.datamanagementfacilityportlet.servlet;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.application.aquamaps.aquamapsservice.client.proxies.DataManagement;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.OrderDirection;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.SystemTable;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.Tags;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalysisServlet extends HttpServlet {
	private static final long serialVersionUID = -1935638790611247408L;
	private static final Logger logger = LoggerFactory.getLogger(AnalysisServlet.class);

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req, resp);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException  {
		handleRequest(req, resp);
	}


	protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



		
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

		response.setContentType(Tags.JSONUTF8);
		response.setStatus(HttpServletResponse.SC_OK);
		int startIndex=0;
		int limit=100;
		try{
			startIndex=Integer.parseInt(request.getParameter(Tags.START));
			limit=Integer.parseInt(request.getParameter(Tags.LIMIT));
		}catch (NumberFormatException e){
			//Some requestes could not have start / offset
		}
		
		
		String sortColumn=request.getParameter(Tags.sort);
		String sortDir=request.getParameter(Tags.dir);
		if(sortColumn==null)sortColumn=SubmittedFields.searchid+"";
		if(sortDir==null)sortDir=Tags.ASC;
		try{		
			ScopeProvider.instance.set(Utils.getSession(request.getSession()).getScope().toString());
			DataManagement dm=dataManagement().build();
			
			response.getWriter().write(dm.getJSONView(new PagedRequestSettings(limit, startIndex, sortColumn, OrderDirection.valueOf(sortDir)),dm.getSystemTableName(SystemTable.ANALYSIS_REQUESTS),null));
		}catch(Exception e){
			logger.error("Unable to serve request",e);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		//			response.getWriter().write(Tags.EMPTY_JSON);

	}
}
