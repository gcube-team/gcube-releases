package org.gcube.application.aquamaps.aquamapsportlet.servlet;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.Tags;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientObjectType;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.db.DBManager;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.utils.Utils;
import org.gcube.application.aquamaps.aquamapsservice.client.proxies.DataManagement;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.OrderDirection;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.SystemTable;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobServlet extends CustomServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2979246371174057352L;
	private static final Logger logger = LoggerFactory.getLogger(JobServlet.class);

	protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int startIndex=0;
		int limit=100;
		try{
			startIndex=Integer.parseInt(request.getParameter(Tags.START));
			limit=Integer.parseInt(request.getParameter(Tags.LIMIT));
		}catch (NumberFormatException e){
			//Some requestes could not have start / offset
		}

		try {
			ASLSession session=Utils.getSession(request.getSession());
			response.setContentType(Tags.JSONUTF8);
			response.setStatus(HttpServletResponse.SC_OK);
			String sortColumn=request.getParameter(Tags.sort);
			String sortDir=request.getParameter(Tags.dir);
			if(sortColumn==null)sortColumn=SubmittedFields.title+"";
			if(sortDir==null)sortDir=Tags.ASC;

			String toReturnJSON=null;

			if(request.getParameter(Tags.AQUAMAPS_TYPE)!=null){
				//Local DB request
				toReturnJSON=DBManager.getInstance(session.getScope()).
						getJSONObjectsByType(session.getUsername(),ClientObjectType.valueOf(request.getParameter(Tags.AQUAMAPS_TYPE)),startIndex,limit,sortColumn,sortDir);

			}else{
				// Remote call 
				String scope=session.getScope().toString();
				logger.debug("Serving request to remote service in scope "+scope);
				ScopeProvider.instance.set(session.getScope().toString());
				DataManagement dm=dataManagement().build();
				String submittedTableName=dm.getSystemTableName(SystemTable.SUBMITTED_MAP_REQUESTS);

				List<Field> filter=new ArrayList<Field>();
				filter.add(new Field(SubmittedFields.author+"",session.getUsername(),FieldType.STRING));
				PagedRequestSettings settings=new PagedRequestSettings(limit, startIndex, sortColumn, OrderDirection.valueOf(sortDir));
				if(request.getParameter(Tags.SETTINGS)!=null){
					//Show jobs
					filter.add(new Field(SubmittedFields.isaquamap+"",false+"",FieldType.BOOLEAN));
				}else{
					String flag=(String) session.getAttribute(Tags.submittedShowAquaMaps);
					boolean showAquaMaps=(flag!=null)?Boolean.parseBoolean(flag):true;
					filter.add(new Field(SubmittedFields.isaquamap+"",showAquaMaps+"",FieldType.BOOLEAN));
					//					if(session.hasAttribute(Tags.submittedObjectDate)) filter.add(new Field(SubmittedFields.submissiontime+"",(String)session.getAttribute(Tags.submittedObjectDate),FieldType.Date));
					if(session.hasAttribute(Tags.submittedJobId)) filter.add(new Field(SubmittedFields.jobid+"",(String)session.getAttribute(Tags.submittedJobId),FieldType.INTEGER));
					if(session.hasAttribute(Tags.submittedObjectStatus)) filter.add(new Field(SubmittedFields.status+"",(String)session.getAttribute(Tags.submittedObjectStatus),FieldType.STRING));
					if(session.hasAttribute(Tags.submittedObjectType)) filter.add(new Field(SubmittedFields.type+"",(String)session.getAttribute(Tags.submittedObjectType),FieldType.STRING));
				}


				toReturnJSON=dm.getJSONView(settings, submittedTableName, filter);
			}

			response.getWriter().write(toReturnJSON);

		} catch (Exception e) {
			logger.error("Exception on retrieving informations from server",e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

	}

}
