package org.gcube.application.aquamaps.aquamapsportlet.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.Tags;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.db.DBManager;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.utils.Utils;
import org.gcube.application.framework.core.session.ASLSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;






public class PhylogenyServlet extends CustomServlet {


	private static final Logger logger = LoggerFactory.getLogger(PhylogenyServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -873906383637717415L;

	protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



		String level=request.getParameter(Tags.PHYLOGENY_LEVEL);
		logger.trace("PhylogenyServlet-handleRequest level : "+level);
		if(level==null){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}else{
			try{	
				ASLSession session=Utils.getSession(request.getSession());
				
				try {					
					response.getWriter().write(DBManager.getInstance(session.getScope()).getPhylogenyJSON(level));
					response.setStatus(HttpServletResponse.SC_OK);

				} catch (Exception e) {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					logger.trace("Exception while contacting service",e);
				}
			}catch(Exception e){
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		}

	}
}
