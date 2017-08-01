package org.apache.jackrabbit.j2ee.privilegemanager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jcr.security.AccessControlException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.jcr.security.Privilege;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.api.JackrabbitWorkspace;
import org.apache.jackrabbit.api.security.authorization.PrivilegeManager;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.versioning.JCRVersioning;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;


/**
 * Delete Aces for a resource.
 */

public class GetRegisteredPrivileges extends HttpServlet {

	
	private Logger logger = LoggerFactory.getLogger(GetRegisteredPrivileges.class);

	private static final long serialVersionUID = 1L;

	public GetRegisteredPrivileges() {
		super();
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.debug("Servlet get Registered Privileges servlet called ......");

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

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


			JCRPrivilegeManager privManager = new JCRPrivilegeManager(session);
			List<String> privileges = privManager.getRegisteredPrivileges();


			xmlConfig = xstream.toXML(privileges);
			response.setContentLength(xmlConfig.length()); 
			out.println(xmlConfig);

		} catch (Exception e) {
			logger.error("Error repository ex " + e);
			xmlConfig = xstream.toXML(e.getMessage());
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