package org.gcube.application.aquamaps.aquamapsportlet.servlet;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.maps;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.Tags;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.utils.Utils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Filter;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.OrderDirection;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SpeciesServlet extends CustomServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5433023032821426866L;

	private static final Logger logger = LoggerFactory.getLogger(SpeciesServlet.class);
	
	protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.trace("SpeciesServlet-handleRequest");

		try{
			
			ASLSession session=Utils.getSession(request.getSession());
			StringBuilder sb=new StringBuilder();
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
			if(sortDir==null)sortDir=Tags.ASC;
			try{
				
				List<Filter> filter=(List<Filter>) session.getAttribute(Tags.SPECIES_FILTER);
				int hspenId=(Integer) session.getAttribute(ResourceType.HSPEN+"");
				ScopeProvider.instance.set(session.getScope().toString());
				sb.append(maps().build().getJSONSpecies(hspenId, null, filter, new PagedRequestSettings(limit, startIndex, sortColumn, OrderDirection.valueOf(sortDir))));

				response.setContentType(Tags.JSONUTF8);		
				response.getWriter().write(sb.toString());
				response.setStatus(HttpServletResponse.SC_OK);
			}catch(Exception e){
				e.printStackTrace();
				logger.trace("Error from server", e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}catch(Exception e){
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}	
	}

}
