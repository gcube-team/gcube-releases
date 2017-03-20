package org.apache.jackrabbit.j2ee.usermanager;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class DeleteAuthorizableServlet extends HttpServlet {

	public static final String GROUP_NAME						= "groupName";
	public static final String TRUE								= "true";
	public static final String FALSE							= "false";
	
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(DeleteAuthorizableServlet.class);
	
	public DeleteAuthorizableServlet() {
		super();
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		
		boolean modified = false;
		
		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		SessionImpl session = null;
		
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

			
			logger.info("Servlet DeleteAuthorizableServlet called with parameters GROUP_NAME: " + groupName);
			
			//create a new group
			final UserManager userManager = session.getUserManager();

			Authorizable authorizable = userManager.getAuthorizable(groupName);

			if (authorizable.isGroup()) {				 
				Group group = (Group) authorizable;
				group.remove();
			}
			else{
				User user = (User) authorizable;
				user.remove();
			}
			session.save();
			
			modified = true;
			xmlConfig = xstream.toXML(modified);

			response.setContentLength(xmlConfig.length()); 
			out.println(xmlConfig);
			
		} catch (RepositoryException e) {

			modified = false;
			
			xmlConfig = xstream.toXML(modified);
			
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