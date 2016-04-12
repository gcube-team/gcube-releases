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

		logger.info("Servlet GetDisplayNameServlet called ......");

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		SessionImpl session = null;

		String displayName = null;

		XStream xstream = null;
		String xmlConfig = null;

		try {
			xstream = new XStream();
			session = (SessionImpl) rep
					.login(new SimpleCredentials(request.getParameter(ConfigRepository.USER), request.getParameter(ConfigRepository.PASSWORD).toCharArray()));

			final String groupName = request.getParameter(GROUP_NAME);

			final UserManager userManager = session.getUserManager();

			Authorizable authorizable = userManager.getAuthorizable(groupName);

			if (authorizable.isGroup()) {
				Group group = (Group) authorizable;

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