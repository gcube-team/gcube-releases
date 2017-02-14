package org.apache.jackrabbit.j2ee.usermanager;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.ConfigRepository;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class GetDisplayNameServlet extends HttpServlet {

	public static final String DISPLAY_NAME_LABEL				= "hl:displayName";
	public static final String GROUP_NAME						= "groupName";

	private static final long serialVersionUID = 1L;
	
	private Logger logger = LoggerFactory.getLogger(GetDisplayNameServlet.class);

	public GetDisplayNameServlet() {
		super();
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		SessionImpl session = null;

		String displayName = null;

		XStream xstream = null;
		String xmlConfig = null;

		String adminId = request.getSession()
				.getServletContext()
				.getInitParameter("user");
		char[] adminPass = request.getSession()
				.getServletContext()
				.getInitParameter("pass").toCharArray();
		try {

			session = (SessionImpl) rep
					.login(new SimpleCredentials(adminId, adminPass));
			xstream = new XStream();

			final String groupName = request.getParameter(GROUP_NAME);
			
//			logger.info("Servlet GetDisplayNameServlet called with group name: " + groupName);

			final UserManager userManager = session.getUserManager();

			Authorizable authorizable = userManager.getAuthorizable(groupName);
			
			Group group = null;
			
			if (authorizable.isGroup()) {
				try{
					group = (Group) authorizable;
				} catch (Exception e) {
					throw new ServletException("Group " + groupName + " not found " + e.getMessage());
				} 

				Value[] displayNameValue = group.getProperty(DISPLAY_NAME_LABEL);
				int size = displayNameValue.length;
				for (int i=0; i< size; i++){

					displayName = displayNameValue[i].getString();	
//					System.out.println("displayName: " + displayName);
				}
			}

			session.save();

			xmlConfig = xstream.toXML(displayName);
			response.setContentLength(xmlConfig.length()); 
			out.println(xmlConfig);

		} catch (RepositoryException e) {
			e.printStackTrace();
			xmlConfig = xstream.toXML(displayName);
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