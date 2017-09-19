package org.gcube.application.datamanagementfacilityportlet.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileServlet extends HttpServlet {
	private static final long serialVersionUID = -1935638790611247408L;
	private static final Logger logger = LoggerFactory.getLogger(FileServlet.class);

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req, resp);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException  {
		handleRequest(req, resp);
	}


	protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



	
		
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

	

	}
}