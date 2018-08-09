package org.gcube.application.aquamaps.aquamapsportlet.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.Tags;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.db.DBManager;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.utils.Utils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.OrderDirection;
import org.gcube.application.framework.core.session.ASLSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SelectionServlet extends CustomServlet {

	private static final Logger logger = LoggerFactory.getLogger(SelectionServlet.class);
	private static final long serialVersionUID = 7237729162027277043L;

	protected void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String requested=request.getParameter(Tags.SELECTION_attribute_NAME);
		try{
			ASLSession session=Utils.getSession(request.getSession());	
		if(requested!=null){
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
			if(sortColumn==null)sortColumn=SpeciesFields.speciesid+"";
			if(sortDir==null)sortDir="ASC";		
			
		
			response.setContentType(Tags.JSONUTF8);
			response.setStatus(HttpServletResponse.SC_OK);
			if(requested.equals(Tags.SELECTED_Species)){
				
				String aquamapsObjectTitle=request.getParameter(Tags.AQUAMAPS_TITLE);
				String aquamapsObjectId=request.getParameter(Tags.AQUAMAPS_ID);
				if(aquamapsObjectTitle!=null){
					//toCreate aquaMaps Object basket
					response.getWriter().write(DBManager.getInstance(session.getScope()).getObjectJSONBasket(session.getUsername(), aquamapsObjectTitle, startIndex, limit,sortColumn,sortDir));
				}else if(aquamapsObjectId!=null){
					//fetched created object basket
					int id=Integer.parseInt(request.getParameter(Tags.AQUAMAPS_ID));
					response.getWriter().write(DBManager.getInstance(session.getScope()).getFetchedJSONBasket(id, startIndex, limit,sortColumn,sortDir));
				}else{ //Serving user basket
					response.getWriter().write(DBManager.getInstance(session.getScope()).getUserJSONBasket(session.getUsername(), startIndex, limit,sortColumn,sortDir));
				}

			}else if(requested.equals(Tags.SELECTED_AREAS))
				response.getWriter().write(DBManager.getInstance(session.getScope()).getJSONAreaSelection(session.getUsername(), 
						new PagedRequestSettings(limit, startIndex,sortColumn, OrderDirection.valueOf(sortDir))));
			else response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED); //invalid parameter value
					
		}else response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("Unexpected Exception",e);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	
	}



}
