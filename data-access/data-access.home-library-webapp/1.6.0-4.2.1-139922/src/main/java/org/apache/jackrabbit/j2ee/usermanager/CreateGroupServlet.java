package org.apache.jackrabbit.j2ee.usermanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Calendar;

import javax.jcr.Binary;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
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
import org.apache.jackrabbit.j2ee.privilegemanager.CreateCostumePrivilegeServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class CreateGroupServlet extends HttpServlet {

	public static final String GROUP_NAME					= "groupName";
	public static final String DISPLAY_NAME					= "displayName";
	public static final String DISPLAY_NAME_LABEL			= "hl:displayName";

	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(CreateGroupServlet.class);
	public CreateGroupServlet() {
		super();
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.info("Servlet CreateGroupServlet called ......");

		boolean modified = false;
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		SessionImpl session = null;

		XStream xstream = null;
		String xmlConfig = null;

		String user = request.getSession()
				.getServletContext()
				.getInitParameter("user");
		char[] pass = request.getSession()
				.getServletContext()
				.getInitParameter("pass").toCharArray();
		try {

			session = (SessionImpl) rep
					.login(new SimpleCredentials(user, pass));

			xstream = new XStream();

			final String groupName = request.getParameter(GROUP_NAME);
			final String displayName = request.getParameter(DISPLAY_NAME);
			logger.info("GroupName: " + groupName + " - DisplayName: " + displayName);

			Value displayNameValue = new Value() {

				@Override
				public String getString() throws ValueFormatException,
				IllegalStateException, RepositoryException {
					return displayName;
				}

				@Override
				public InputStream getStream() throws RepositoryException {
					return null;
				}

				@Override
				public long getLong() throws ValueFormatException, RepositoryException {
					return 0;
				}

				@Override
				public double getDouble() throws ValueFormatException, RepositoryException {
					return 0;
				}

				@Override
				public BigDecimal getDecimal() throws ValueFormatException,
				RepositoryException {
					return null;
				}

				@Override
				public Calendar getDate() throws ValueFormatException, RepositoryException {
					return null;
				}

				@Override
				public boolean getBoolean() throws ValueFormatException,
				RepositoryException {
					return false;
				}

				@Override
				public Binary getBinary() throws RepositoryException {
					return null;
				}

				@Override
				public int getType() {
					return 0;
				}
			};
			//create a new group
			final UserManager userManager = session.getUserManager();

			Authorizable authorizable = userManager.getAuthorizable(groupName);

			if (authorizable != null) {
				// principal already exists!
				modified = false;
			} else {
				Group group = userManager.createGroup(new Principal() {

					@Override
					public String getName() {
						return groupName;
					}					
				});

				group.setProperty(DISPLAY_NAME_LABEL, displayNameValue);
				session.save();
				modified = true;
			}

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