package org.gcube.application.aquamaps.aquamapsspeciesview.servlet;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.maps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Filter;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FilterType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.OrderDirection;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.Tags;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.SpeciesFilter;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.SpeciesSearchDescriptor;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsspeciesview.servlet.utils.Utils;
import org.gcube.application.framework.core.session.ASLSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SpeciesServlet  extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5433023032821426866L;

	private static final Logger logger = LoggerFactory.getLogger(SpeciesServlet.class);

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req, resp);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException  {
		handleRequest(req, resp);
	}
	
	
	private static final SpeciesOccursumFields[] genericSearchFields= new SpeciesOccursumFields[] {
		SpeciesOccursumFields.scientific_name,
		SpeciesOccursumFields.english_name,
		SpeciesOccursumFields.french_name,
		SpeciesOccursumFields.spanish_name,
		SpeciesOccursumFields.genus,
		SpeciesOccursumFields.species,
		SpeciesOccursumFields.fbname ,
	};
	
	
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
				
				List<Filter> genericSearch=new ArrayList<Filter>();
				List<Filter> advancedSearch=new ArrayList<Filter>();
				if(session.hasAttribute(Tags.SPECIES_SEARCH_FILTER)){
					SpeciesSearchDescriptor descriptor=(SpeciesSearchDescriptor) session.getAttribute(Tags.SPECIES_SEARCH_FILTER);
					if(descriptor.getGenericSearchFieldValue()!=null){
						String[] keywords=descriptor.getGenericSearchFieldValue().split(" ");
						for(String keyword:keywords)
							for(SpeciesOccursumFields f:genericSearchFields)
								genericSearch.add(new Filter(FilterType.contains, new Field(f+"", keyword, FieldType.STRING)));
					}
					for(SpeciesFilter f: descriptor.getAdvancedFilterList()){
						advancedSearch.add(new Filter(FilterType.valueOf(f.getOperator()+""),new Field(f.getName(),f.getValue()+"",FieldType.valueOf(f.getType()+""))));
					}
				}
				
				int hspenId=(Integer) session.getAttribute(ResourceType.HSPEN+"");
				
				
				
				PagedRequestSettings settings= new PagedRequestSettings(limit, startIndex, sortColumn, OrderDirection.valueOf(sortDir));
				sb.append(maps().build().getJSONSpecies(hspenId, genericSearch, advancedSearch, settings));

				response.setContentType(Tags.JSONUTF8);		
				response.getWriter().write(sb.toString());
				response.setStatus(HttpServletResponse.SC_OK);
			}catch(Exception e){
				e.printStackTrace();
				logger.trace("Error from server", e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}catch(Exception e){
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}	
	}

}
