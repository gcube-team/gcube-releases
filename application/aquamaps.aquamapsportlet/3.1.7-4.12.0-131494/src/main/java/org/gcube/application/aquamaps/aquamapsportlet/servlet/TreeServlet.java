package org.gcube.application.aquamaps.aquamapsportlet.servlet;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.Tags;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.utils.Utils;
import org.gcube.application.aquamaps.aquamapsservice.client.proxies.DataManagement;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.MetaSourceFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.OrderDirection;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.SystemTable;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TreeServlet extends CustomServlet {
	private static final long serialVersionUID = -3832557273021635458L;


	private static final Logger logger = LoggerFactory.getLogger(TreeServlet.class);



	protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



		String type=request.getParameter(Tags.TREE_TYPE);
		logger.trace("TreeServlet-handleRequest type : "+type);
		if(type==null){				
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}else{		
			ArrayList<Field> filter=new ArrayList<Field>();
			
			if(type!=null)
				filter.add(new Field(MetaSourceFields.type+"",ResourceType.valueOf(type)+"",FieldType.STRING));
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
				ASLSession session=Utils.getSession(request.getSession());
				ScopeProvider.instance.set(session.getScope().toString());
				DataManagement dm=dataManagement().build();
				String submittedTableName=dm.getSystemTableName(SystemTable.DATASOURCES_METADATA);
				response.getWriter().write(
						dm.getJSONView(new PagedRequestSettings(limit, startIndex, sortColumn, OrderDirection.valueOf(sortDir)), submittedTableName, filter));
//						utils.getConfiguration().getAMInterface(session.getScope()).getJSONResources(new PagedRequestSettings(limit, startIndex, OrderDirection.fromString(sortDir), sortColumn), filter));
				
			}catch(Exception e){
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}	
		}

	}


}
