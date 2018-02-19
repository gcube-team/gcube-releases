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

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;


public class ChangePass extends HttpServlet {

	public static final String USER_NAME					= "userName";

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(ChangePass.class);

	public ChangePass() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.info("Servlet ChangePass called ......");
		List<String> users = null;
		String message = "";
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		SessionImpl session = null;

		String user = request.getSession()
				.getServletContext()
				.getInitParameter("user");
		char[] pass = request.getSession()
				.getServletContext()
				.getInitParameter("pass").toCharArray();
		try {

			session = (SessionImpl) rep
					.login(new SimpleCredentials(user, pass));

			final UserManager userManager = ((JackrabbitSession)
					session).getUserManager();


			Authorizable authorizable = userManager.getAuthorizable("workspacerep.imarine");
			User myuser = (User) authorizable;
			myuser.changePassword("gcube2010*onan");

			session.save();



			XStream xstream = new XStream();
			String xmlConfig = xstream.toXML(true);

			//			message = users;
			//			response.setContentLength(message.size()); 
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