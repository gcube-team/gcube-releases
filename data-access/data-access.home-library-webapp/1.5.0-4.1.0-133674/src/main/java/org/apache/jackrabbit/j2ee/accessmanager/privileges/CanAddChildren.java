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

public class CanAddChildren extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(CanAddChildren.class);
	private static final long serialVersionUID = 1L;

	public CanAddChildren() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String login = request.getParameter(ServletParameter.PORTAL_LOGIN);
		String absPath = new String(request.getParameter(ServletParameter.ABS_PATH).getBytes("iso-8859-1"), "UTF-8");

		Session session = null;
		XStream xstream = null;
		String xmlConfig = null;
		SessionManager sessionManager = null;
		
		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		
		String sessionId =  null;
		try {
			xstream = new XStream(new DomDriver("UTF-8"));

			sessionManager = SessionManager.getInstance(rep);
			session = sessionManager.newSession(login);
			sessionId = session.toString();

			try{
				Boolean flag = canAddChildren(absPath, session);
				
				logger.info("Can " + session.getUserID() + " add children on node " +absPath + " ? " + flag);
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
			if (session!=null){
				sessionManager.releaseSession(sessionId);
			}
			out.close();
			out.flush();
		}
	}




	public static boolean canAddChildren(String absPath, Session session) throws Exception {
	
		try {
			AccessControlManager accessControlManager = session.getAccessControlManager();
			return accessControlManager.hasPrivileges(absPath, new Privilege[] {
					accessControlManager.privilegeFromName(CustomPrivilege.JCR_ADD_CHILD_NODES)
			});
		} catch (RepositoryException e) {
			return false;
		}
	}




}

