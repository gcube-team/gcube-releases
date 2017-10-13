package org.apache.jackrabbit.j2ee.usermanager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class GroupMembershipServlet extends HttpServlet {

	public static final String GROUP_NAME						= "groupName";

	private static final long serialVersionUID = 1L;
	
	private Logger logger = LoggerFactory.getLogger(GroupMembershipServlet.class);
	
	public GroupMembershipServlet() {
		super();
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.info("Servlet GroupMembershipServlet called ......");

		String message = "";
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		SessionImpl session = null;
		
		String adminId = request.getSession()
				.getServletContext()
				.getInitParameter("user");
		char[] adminPass = request.getSession()
				.getServletContext()
				.getInitParameter("pass").toCharArray();
		try {

			session = (SessionImpl) rep
					.login(new SimpleCredentials(adminId, adminPass));
			final String groupName = request.getParameter(GROUP_NAME);

			final List<String> members = new ArrayList<String>();

			//create a new group
			final UserManager userManager = session.getUserManager();

			Authorizable authorizable = userManager.getAuthorizable(groupName);

			if (authorizable.isGroup()) {
				Group group = (Group) authorizable;
				Iterator<Authorizable> iter = group.getMembers();
				while (iter.hasNext()) {
					Authorizable auth = iter.next();
					if (!auth.isGroup()) {
						members.add(auth.getID());
					}
				}
			}
			
			XStream xstream = new XStream();
			String xmlConfig = xstream.toXML(members);

			out.println(xmlConfig);
			
		} catch (RepositoryException e) {

			message = e.getMessage();
			response.setContentLength(message.length()); 
			out.println(message);	

		} finally {
			if(session != null)
				session.logout();

			out.close();
			out.flush();
		}	
	}

}