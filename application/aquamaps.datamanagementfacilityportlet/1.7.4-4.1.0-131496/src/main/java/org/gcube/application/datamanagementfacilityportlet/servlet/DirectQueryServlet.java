package org.gcube.application.datamanagementfacilityportlet.servlet;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.CustomQueryDescriptorStubs;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.OrderDirection;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.Tags;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectQueryServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6079055136467951664L;


	private static final Logger logger = LoggerFactory.getLogger(DirectQueryServlet.class);

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
			ASLSession session=Utils.getSession(request.getSession());
			ScopeProvider.instance.set(Utils.getSession(request.getSession()).getScope().toString());
			response.setContentType(Tags.JSONUTF8);
			response.setStatus(HttpServletResponse.SC_OK);

			String JSONResponse=Tags.EMPTY_JSON;

			if(session.hasAttribute(Tags.currentDirectQuery)){  
				CustomQueryDescriptorStubs desc=(CustomQueryDescriptorStubs) session.getAttribute(Tags.currentDirectQuery);
				PagedRequestSettings settings= new PagedRequestSettings(limit, startIndex, orderColumn, OrderDirection.valueOf(orderDirection));
				JSONResponse=dataManagement().build().getJSONView(settings, desc.actualTableName(), null);
			}
			response.getWriter().write(JSONResponse);

		}catch(Exception e ){
			logger.error("Unexpected error while trying to handle request ", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

}
