package org.apache.jackrabbit.j2ee.accessmanager.privileges;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.apache.jackrabbit.j2ee.workspacemanager.SessionManager;
import org.apache.jackrabbit.j2ee.workspacemanager.util.CustomPrivilege;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class CanModifyProperties extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(CanModifyProperties.class);
	private static final long serialVersionUID = 1L;


	public CanModifyProperties() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String login = request.getParameter(ServletParameter.PORTAL_LOGIN);
		String sessionId = request.getParameter(ServletParameter.UUID);
		String absPath = new String(request.getParameter(ServletParameter.ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
		boolean isRoot = Boolean.valueOf(request.getParameter(ServletParameter.ISROOT));
		
		
		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		Session session = null;
		XStream xstream = null;
		String xmlConfig = null;
		SessionManager sessionManager = null;
		boolean exist = false;
		try {
			xstream = new XStream(new DomDriver("UTF-8"));

			sessionManager = SessionManager.getInstance(rep);
			exist = sessionManager.sessionExists(sessionId); 
			if (exist){				
				session = sessionManager.getSession(sessionId);
			} else {
				session = sessionManager.newSession(login);
				sessionId = session.toString();
			}

			try{
				Boolean flag = canAddChildren(session.getUserID(), absPath, isRoot, session);
				xmlConfig = xstream.toXML(flag);
				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			} catch (Exception e) {
				logger.error("Error checking ACL CanAddChildren of item: " + absPath , e);
				xmlConfig = xstream.toXML(e.toString());
				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			}

		} catch (Exception e) {
			logger.error("Error repository ex " + e);
			xmlConfig = xstream.toXML(e.toString());
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);

		} finally {
			if (!exist){
				sessionManager.releaseSession(sessionId);
			}
			out.close();
			out.flush();
		}
	}




	public static boolean canAddChildren(String login, String absPath, Boolean root, Session session) throws Exception {

		try {
			AccessControlManager accessControlManager = session.getAccessControlManager();

			boolean canDelete = false;

			if (root)
				canDelete = accessControlManager.hasPrivileges(absPath, new Privilege[] {
						accessControlManager.privilegeFromName(CustomPrivilege.JCR_MODIFY_PROPERTIES), accessControlManager.privilegeFromName(CustomPrivilege.NO_LIMIT), accessControlManager.privilegeFromName(CustomPrivilege.REMOVE_ROOT)
				});
			else
				canDelete = accessControlManager.hasPrivileges(absPath, new Privilege[] {
						accessControlManager.privilegeFromName(CustomPrivilege.JCR_MODIFY_PROPERTIES), accessControlManager.privilegeFromName(CustomPrivilege.NO_LIMIT)
				});

			return canDelete;

		} catch (RepositoryException e) {
			return false;
		}
	}




}

