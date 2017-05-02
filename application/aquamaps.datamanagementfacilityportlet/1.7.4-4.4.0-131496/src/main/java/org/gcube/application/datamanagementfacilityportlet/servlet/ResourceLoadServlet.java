package org.gcube.application.datamanagementfacilityportlet.servlet;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.Tags;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceLoadServlet extends HttpServlet {

	
	private static final long serialVersionUID = -1935638790611247408L;
	private static final Logger logger = LoggerFactory.getLogger(ResourceLoadServlet.class);
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req, resp);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException  {
		handleRequest(req, resp);
	}
	
	
	protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		
		String[] parts=request.getRequestURL().toString().split("/");
		String servletName=this.getServletName();
		String resIdString=parts[parts.length-1];
		int index=0;
		StringBuilder parsedScope=new StringBuilder();
		for(int i=0;i<parts.length;i++) {
			if(parts[i].equals(servletName))index=i;
			if(index>0&&i>index&&i<parts.length-1) parsedScope.append(parts[i]+"/");
		}
		if(parsedScope.lastIndexOf("/")>0)parsedScope.deleteCharAt(parsedScope.lastIndexOf("/"));
		
		
		
		if(resIdString==null){				
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}else{				
			response.setContentType(Tags.JSONUTF8);
			response.setStatus(HttpServletResponse.SC_OK);
			
			try{
				ScopeProvider.instance.set(Utils.getSession(request.getSession()).getScope().toString());
				response.getWriter().write(dataManagement().build().getReport(CSVUtils.CSVToStringList(resIdString)).getResourceLoad());
			}catch(Exception e){
				logger.error("Incorrect Request : "+request.getRequestURL()+", request was sent by "+request.getRemoteAddr());
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}

	}
	
	
	
}
