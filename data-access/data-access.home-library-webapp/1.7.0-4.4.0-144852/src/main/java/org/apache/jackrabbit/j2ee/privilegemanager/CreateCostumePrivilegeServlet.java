package org.apache.jackrabbit.j2ee.privilegemanager;

import java.io.IOException;
import java.io.PrintWriter;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;


/**
 * Delete Aces for a resource.
 */

public class CreateCostumePrivilegeServlet extends HttpServlet {

	public static final String NAME							= "name";
	public static final String PRIVILEGE					= "privilege";
	
	private Logger logger = LoggerFactory.getLogger(CreateCostumePrivilegeServlet.class);

	private static final long serialVersionUID = 1L;

	public CreateCostumePrivilegeServlet() {
		super();
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.debug("Servlet Create Costume Privilege called ......");

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		SessionImpl session = null;

		XStream xstream = null;
		String xmlConfig = null;
		boolean modified = false;

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

			final String name = request.getParameter(NAME);
			String[] privileges = request.getParameterValues(PRIVILEGE);
			if (privileges == null)
				privileges = new String[] {};
			//delete aces	
			createCostumePrivilege(session, name, privileges);

			//			createCostumePrivilege(session, name, new String[] {});

			modified = true;

			xmlConfig = xstream.toXML(modified);
			response.setContentLength(xmlConfig.length()); 
			out.println(xmlConfig);

		} catch (RepositoryException e) {
			logger.error("Error repository ex " + e);
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


	private void createCostumePrivilege(SessionImpl session, String name, String[] declaredAggregateNames) throws RepositoryException {

		//		System.out.println("Creating the costume privilege " + name + " with privileges: " + declaredAggregateNames.toString());

		JackrabbitWorkspace jrws = (JackrabbitWorkspace) session.getWorkspace();
		PrivilegeManager privMgr = null;
		try {
			privMgr = jrws.getPrivilegeManager();
		} catch (RepositoryException e) {
			logger.error("Error creating the costume privilege " + name );
		}

		logger.debug("getRegisteredPrivileges " );

		Privilege[] privileges = privMgr.getRegisteredPrivileges();
		for (int i=0; i< privileges.length; i++){
			logger.debug(privileges[i].getName());
			//			System.out.println(privileges[i].getAggregatePrivileges().toString());
			//			System.out.println(privileges[i].getDeclaredAggregatePrivileges().toString());
		}

		logger.debug("getPrivilege " + name);
		try {
			Privilege priv = privMgr.getPrivilege(name);
			logger.debug("Privilege already exists: " + priv.getName());
		} catch (AccessControlException e) {
			logger.error("catch " + e);
			try{
				privMgr.registerPrivilege(name, false, declaredAggregateNames);
			}catch (Exception e1) {
				logger.error("Error registering privilege " + name + " - " + e1);
			}
		}
		logger.debug("getRegisteredPrivileges 2 " );
		Privilege[] privileges1 = privMgr.getRegisteredPrivileges();
		for (int i=0; i< privileges1.length; i++){
			logger.debug(privileges1[i].getName());
			//			System.out.println(privileges1[i].getAggregatePrivileges().toString());
			//			System.out.println(privileges1[i].getDeclaredAggregatePrivileges().toString());
		}

//		System.out.println("end " );

	}






}