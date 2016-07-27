package org.gcube.application.aquamaps.aquamapsspeciesview.servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.OrderDirection;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.Tags;
import org.gcube.application.aquamaps.aquamapsspeciesview.servlet.db.DBManager;
import org.gcube.application.aquamaps.aquamapsspeciesview.servlet.utils.Utils;
import org.gcube.application.framework.core.session.ASLSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapServlet extends HttpServlet {
	private static final long serialVersionUID = 5433023032821426866L;

	private static final Logger logger = LoggerFactory.getLogger(MapServlet.class);

	private static final ArrayList<Field> basicFilters=new ArrayList<Field>();
	private static final Submitted submittedModel=new Submitted(0);
 
	static{
		submittedModel.setStatus(SubmittedStatus.Completed);
		submittedModel.setIsAquaMap(true);
		submittedModel.setToDelete(false);
		
		basicFilters.add(submittedModel.getField(SubmittedFields.status));
		basicFilters.add(submittedModel.getField(SubmittedFields.isaquamap));
		basicFilters.add(submittedModel.getField(SubmittedFields.todelete));
	}
	
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req, resp);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException  {
		handleRequest(req, resp);
	}
	
	
	
	
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
			if(sortColumn==null)sortColumn=SubmittedFields.searchid+"";
			if(sortDir==null)sortDir=Tags.ASC;
			try{
//				ArrayList<Field> filter=new ArrayList<Field>(basicFilters);
////				Field coverage=new Field(SubmittedFields.speciescoverage+"",AquaMapsObject.generateMD5fromIds(arg0, arg1))
////				filter.add
//				
//				sb.append(utils.getConfiguration().getPublisherInterface(session.getScope()).getJsonSubmittedByFilters(filter, settings));

				
				
				
				PagedRequestSettings settings= new PagedRequestSettings(limit, startIndex, sortColumn, OrderDirection.valueOf(sortDir));
				response.getWriter().write(DBManager.getInstance(session.getScope()).getMaps(session.getUsername(), settings));
				
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
