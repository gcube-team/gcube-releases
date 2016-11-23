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

public class SetDisplayNameServlet extends HttpServlet {

	public static final String GROUP_NAME						= "groupName";
	public static final String DISPLAY_NAME						= "displayName";
	public static final String DISPLAY_NAME_LABEL				= "hl:displayName";

	private static final long serialVersionUID = 1L;
	
	private Logger logger = LoggerFactory.getLogger(SetDisplayNameServlet.class);
	

	public SetDisplayNameServlet() {
		super();
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.info("Servlet UpdateGroupServlet called ......");

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		SessionImpl session = null;
		boolean modified = false;

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
			final String displayName = request.getParameter(DISPLAY_NAME);
			
			Value displayNameValue = getValue(session, displayName);
					
			final UserManager userManager = session.getUserManager();

			Authorizable authorizable = userManager.getAuthorizable(groupName);

			if (authorizable.isGroup()) {
				Group group = (Group) authorizable;

				group.setProperty(DISPLAY_NAME_LABEL, displayNameValue);
//				System.out.println("setting " + DISPLAY_NAME_LABEL + " : " + displayNameValue.getString());
				session.save();
			}



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

	private Value getValue(SessionImpl session, String value) throws RepositoryException {
		return session.getValueFactory().createValue(value);
	}

}