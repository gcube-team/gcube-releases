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
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.ConfigRepository;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class SetVersionServlet extends HttpServlet {

	public static final String USER								= "user";
	public static final String VERSION							= "version";
	public static final String VERSION_LABEL					= "hl:version";

	private static final long serialVersionUID = 1L;
	
	private Logger logger = LoggerFactory.getLogger(SetVersionServlet.class);

	public SetVersionServlet() {
		super();
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.info("Servlet SetVersionServlet called ......");

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		SessionImpl session = null;
		boolean modified = false;

		XStream xstream = null;
		String xmlConfig = null;

		try {
			xstream = new XStream();
			session = (SessionImpl) rep
					.login(new SimpleCredentials(request.getParameter(ConfigRepository.USER), request.getParameter(ConfigRepository.PASSWORD).toCharArray()));

			final String groupName = request.getParameter(USER);
			final String version = request.getParameter(VERSION);
			
			Value VersioneValue = getValue(session, version);
					
			final UserManager userManager = session.getUserManager();

			Authorizable authorizable = userManager.getAuthorizable(groupName);

			if (authorizable.isGroup()) {				 
				Group group = (Group) authorizable;
				group.setProperty(VERSION_LABEL, VersioneValue);
//				System.out.println("setting " + VERSION_LABEL + " : " + VersioneValue.getString());
				session.save();
			}
			else{
				User user = (User) authorizable;
				user.setProperty(VERSION_LABEL, VersioneValue);
//				System.out.println("setting " + VERSION_LABEL + " : " + VersioneValue.getString());
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