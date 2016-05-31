package org.apache.jackrabbit.j2ee.usermanager;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.ConfigRepository;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class IsGroupServlet extends HttpServlet {

	public static final String GROUP_NAME					= "groupName";

	private static final long serialVersionUID = 1L;
	
	private Logger logger = LoggerFactory.getLogger(IsGroupServlet.class);

	public IsGroupServlet() {
		super();
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.info("Servlet CreateGroupServlet called ......");
		
		boolean found = false;
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		SessionImpl session = null;
		
		XStream xstream = null;
		String xmlConfig = null;
		
		try {
			xstream = new XStream();
			session = (SessionImpl) rep
					.login(new SimpleCredentials(request.getParameter(ConfigRepository.USER), request.getParameter(ConfigRepository.PASSWORD).toCharArray()));

			final String groupName = request.getParameter(GROUP_NAME);

			//create a new group
			final UserManager userManager = session.getUserManager();

			Authorizable authorizable = userManager.getAuthorizable(groupName);
	        
	        if (authorizable == null) {
	        	found = false;
	        } else {
	            if (authorizable.isGroup())
	            	found = true;
	            else
	            	found = false;
	        }

			xmlConfig = xstream.toXML(found);

			response.setContentLength(xmlConfig.length()); 
			out.println(xmlConfig);


		} catch (RepositoryException e) {

			found = false;
			
			xmlConfig = xstream.toXML(found);
			
			response.setContentLength(xmlConfig.length()); 
			out.println(xmlConfig);		

		} finally {
			if(session != null)
				session.logout();

			out.close();
			out.flush();
		}	
	}


}