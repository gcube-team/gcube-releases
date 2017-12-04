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

import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class CreateUserServlet extends HttpServlet {

	public static final String USER_NAME					= "userName";
	public static final String PASS							= "pwd";

	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(CreateUserServlet.class);
	
	
	public CreateUserServlet() {
		super();
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		final String userName;
		final String pass;


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
			userName = request.getParameter(USER_NAME);
			
			logger.info("Servlet CreateUserServlet called for use " + userName);
			
			pass = request.getParameter(PASS);

//			System.out.println("User: " + userName + " - Pass: " + pass);
			xstream = new XStream();

			final UserManager userManager = session.getUserManager();
			

			try {
				userManager.createUser(userName, pass);
			} catch (Exception e) {
				logger.error("Error creating user "+ userName + ", " + e);
			}

			session.save();

			modified = true;

			xmlConfig = xstream.toXML(modified);

			response.setContentLength(xmlConfig.length()); 
			out.println(xmlConfig);


		} catch (RepositoryException e) {
//			logger.error(e);
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