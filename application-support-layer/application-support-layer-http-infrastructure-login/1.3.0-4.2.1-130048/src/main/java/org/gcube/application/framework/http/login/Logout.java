package org.gcube.application.framework.http.login;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class Logout
 */
public class Logout extends HttpServlet {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(Logout.class);
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Logout() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//	String username = request.getParameter("username");
		HttpSession session = request.getSession();
		String username = (String)session.getAttribute("logon.isDone");
//		ASLSession mysession = SessionManager.getInstance().getASLSession(request.getSession().getId(), username);
//		mysession.invalidate();
		SessionManager.getInstance().getASLSession(request.getSession().getId(), username).invalidate();
		request.getSession().invalidate();
		logger.info("The user logged out - The session is invalid");
		
		// TODO: remove cookies with user credentials?
		return;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
