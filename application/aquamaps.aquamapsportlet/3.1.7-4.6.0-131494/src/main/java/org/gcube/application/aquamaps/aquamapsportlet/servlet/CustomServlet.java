package org.gcube.application.aquamaps.aquamapsportlet.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -873906383637717415L;
	private static final Logger logger = LoggerFactory.getLogger(CustomServlet.class);
	
	
	/**
	 * 
	 */
	protected static final int BUFSIZE=1024;
	
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req, resp);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException  {
		handleRequest(req, resp);
	}
	
	
	protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.trace("CustomServlet");

		Map<String,String> params=request.getParameterMap();
		
		for(String key:params.keySet()) logger.trace(params.get(key));

		response.setStatus(HttpServletResponse.SC_OK);
	}

}
