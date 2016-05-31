package org.apache.jackrabbit.j2ee.usermanager;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.AccessDeniedException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.ConfigRepository;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;


public class ListGroupsServlet extends HttpServlet {

	public static final String GROUP_NAME					= "groupName";

	private static final long serialVersionUID = 1L;
	
	private Logger logger = LoggerFactory.getLogger(ListGroupsServlet.class);
	
	public ListGroupsServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.info("Servlet ListGroupsServlet called ......");

		String message = "";
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		SessionImpl session = null;
		try {

			session = (SessionImpl) rep
					.login(new SimpleCredentials(request.getParameter(ConfigRepository.USER), request.getParameter(ConfigRepository.PASSWORD).toCharArray()));
		          
			final List<String> groups = listGroups(session);

			XStream xstream = new XStream();
			String xmlConfig = xstream.toXML(groups);

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

	private List<String> listGroups(SessionImpl jcrsession) throws AccessDeniedException, RepositoryException {
		final UserManager userManager = jcrsession.getUserManager();
		
		final List<String> groups = new ArrayList<String>();

		Iterator<Authorizable> iter = userManager.findAuthorizables(
				"jcr:primaryType", "rep:Group");

		while (iter.hasNext()) {
			Authorizable auth = iter.next();
			if (auth.isGroup() && (!auth.getID().equals("UserAdmin")) && (!auth.getID().equals("GroupAdmin")) && (!auth.getID().equals("administrators"))){
				groups.add(auth.getID());
			}
		}
		if (!userManager.isAutoSave()) {
			jcrsession.save();
		}
		return groups;
	
	}


}