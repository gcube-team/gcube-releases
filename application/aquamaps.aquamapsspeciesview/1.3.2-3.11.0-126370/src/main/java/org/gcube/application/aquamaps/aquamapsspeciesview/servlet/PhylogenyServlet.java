package org.gcube.application.aquamaps.aquamapsspeciesview.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;






public class PhylogenyServlet  extends HttpServlet{




	/**
	 * 
	 */
	private static final long serialVersionUID = -873906383637717415L;

	protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



//		String level=request.getParameter(Tags.PHYLOGENY_LEVEL);
//		logger.trace("PhylogenyServlet-handleRequest level : "+level);
//		if(level==null){
//			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//		}else{
//			try{	
//				ASLSession session=Utils.get().getSession(request.getSession());
//				Utils utils=Utils.get();
//				try {					
//					response.getWriter().write(utils.getDb(session.getScope()).getPhylogenyJSON(level));
//					response.setStatus(HttpServletResponse.SC_OK);
//
//				} catch (Exception e) {
//					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//					logger.trace("Exception while contacting service",e);
//				}
//			}catch(Exception e){
//				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//			}
//		}

	}
}
