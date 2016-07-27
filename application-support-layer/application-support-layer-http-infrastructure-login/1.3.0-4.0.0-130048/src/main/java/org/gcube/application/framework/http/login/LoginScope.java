package org.gcube.application.framework.http.login;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.core.util.ASLGroupModel;
import org.gcube.application.framework.http.anonymousaccess.management.UsersManagementUtils;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class LoginScope
 */
public class LoginScope extends HttpServlet {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(LoginScope.class);
	
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginScope() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		// Get the session
		HttpSession session = request.getSession(true);
		logger.debug("Session id" + session.getId());
		String username = (String)session.getAttribute("logon.isDone");
		if (username == null) {
			response.sendError(401);
			return;

		}
		else {
			// See if the user has chosen a VRE
			String env = request.getParameter("scope");
			if (env == null) {

				// send error - Bad Request
				response.sendError(400,"No scope selected");
				return;
			}
			else {
				// The user has chosen VRE
				// Check if the user can access the selected scope
				ArrayList<String> userScopes = getUserScopes(username);
				if (!userScopes.contains(env)) {
					response.sendError(401, "User access denied in the selected scope");
					return;
				}

				//-- Set the correct parameter
				session.setAttribute("logonScope.isDone", env);	//just a marker object
				ASLSession mysession;
				mysession = SessionManager.getInstance().getASLSession(session.getId(), username);
				mysession.setScope(env);
				response.setStatus(200);
				return;
			}
		}
	}
	
	private ArrayList<String> getUserScopes(String username) {
		ArrayList<String> userScopes = new ArrayList<String>();
		UsersManagementUtils um = new UsersManagementUtils();
		Long userId = null;
		try{
			userId = um.getUserId(username);
			
			List<ASLGroupModel> groupModels = um.listGroupsByUser(userId);
			for (int i = 0; i < groupModels.size(); i++) 
				userScopes.add(um.getScope(groupModels.get(i).getGroupId()));
		}
		catch(UserRetrievalFault urf){
			logger.error("Could not retrieve user Id, thus expect empty list of scopes");
		}
		catch(GroupRetrievalFault grf){
			logger.error("Could not retrieve user's groups IDs, thus expect empty list of scopes");
		}
		return userScopes;
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
