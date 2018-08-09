package org.gcube.application.aquamaps.aquamapsportlet.servlet;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.Tags;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.utils.Utils;
import org.gcube.application.aquamaps.aquamapsservice.client.proxies.DataManagement;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Job;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.OrderDirection;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OccurrenceCellsServlet extends CustomServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8721935904371361436L;

	private static final Logger logger = LoggerFactory.getLogger(OccurrenceCellsServlet.class);

	protected void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String speciesId= request.getParameter(SpeciesFields.speciesid+"");
		logger.trace("Occurrence cells : serving request for speciesId "+speciesId);		
		response.setContentType(Tags.JSONUTF8);
		response.setStatus(HttpServletResponse.SC_OK);
		if(speciesId==null){
			response.getWriter().write("{\""+Tags.TOTAL_COUNT+"\" : 0, \""+Tags.DATA+"\":[]");
		}else{
			try{
				//TODO manage userSelection customization
				int startIndex=0;
				int limit=100;
				String sortColumn=request.getParameter(Tags.sort);
				String sortDir=request.getParameter(Tags.dir);
				try{
					startIndex=Integer.parseInt(request.getParameter(Tags.START));
					limit=Integer.parseInt(request.getParameter(Tags.LIMIT));
				}catch (NumberFormatException e){
					//Some requestes could not have start / offset
				}
				ASLSession session=Utils.getSession(request.getSession());
				ScopeProvider.instance.set(session.getScope().toString());
				DataManagement dm=dataManagement().build();
				String occurrenceTable=null;

				//Trying to get current settings 

				try{
					Job obj=Utils.loadSettings(session, false, false);
					occurrenceTable=obj.getSourceHSPEN().getSourceOccurrenceCellsTables().get(0);
					if (occurrenceTable==null) throw new Exception ("Occurrence table not set in session.");
				}catch(Exception e){
					List<Field> defaults=dm.getDefaultSources();
					logger.debug("Trying to use occurrence default...");
					//Trying to use default
					for(Field f:defaults){
						if(f.name().equals(ResourceType.OCCURRENCECELLS)) occurrenceTable=dm.loadResource(f.getValueAsInteger()).getTableName();
					}
					if(occurrenceTable==null){
						// getFrom HSPEN
						for(Field f:defaults){
							if(f.name().equals(ResourceType.HSPEN)) occurrenceTable=dm.loadResource(f.getValueAsInteger()).getSourceOccurrenceCellsTables().get(0);
						}	
					}
				}

				List<Field> filter=new ArrayList<Field>();
				filter.add(new Field(SpeciesOccursumFields.speciesid+"",speciesId,FieldType.STRING));




				response.getWriter().write(dataManagement().build()
						.getJSONView(new PagedRequestSettings(limit, startIndex, sortColumn, OrderDirection.valueOf(sortDir)), occurrenceTable, filter));
			}catch(Exception e){
				logger.trace("Exception on retrieving informations from server",e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}

	}

}
