package org.apache.jackrabbit.j2ee.workspacemanager.servlets.acl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
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
import org.apache.jackrabbit.j2ee.workspacemanager.session.SessionManager;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GetACLByUser extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(GetACLByUser.class);
	private static final long serialVersionUID = 1L;
	
	private static final String WRITE_ALL 		= "hl:writeAll";
	private static final String READ 			= "jcr:read";
	private static final String WRITE 			= "jcr:write";	
	private static final String ADMINISTRATOR 	= "jcr:all";;
	
	private static final String READ_ONLY 		= "jcr:read";
	private static final String WRITE_OWNER 	= "jcr:write";

	

	public GetACLByUser() {
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
				String acl = getACLByUser(login, absPath, session);
				xmlConfig = xstream.toXML(acl);
				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			} catch (Exception e) {
				logger.error("Error Getting ALC of item: " + absPath , e);
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


	/**
	 *  Get ACL by username
	 * @param user
	 * @param absPath
	 * @return an ACLType privilege
	 * @throws InternalErrorException
	 */
	public String getACLByUser(String login, String absPath, Session session) throws Exception {

		try {
			
			AccessControlManager accessControlManager = session.getAccessControlManager();

			Node node = session.getNode(absPath);
			String owner = getOwner(node);
			if (owner.equals(login)){
				return ADMINISTRATOR;
			}else if (accessControlManager.hasPrivileges(absPath, new Privilege[] {
					accessControlManager.privilegeFromName(ADMINISTRATOR)
			})){
				return ADMINISTRATOR;
			}else if (accessControlManager.hasPrivileges(absPath, new Privilege[] {
					accessControlManager.privilegeFromName(WRITE_ALL)
			})){
				return WRITE_ALL;
			}else if (accessControlManager.hasPrivileges(absPath, new Privilege[] {
					accessControlManager.privilegeFromName(WRITE)
			})){
				return WRITE_OWNER;
			}else if (accessControlManager.hasPrivileges(absPath, new Privilege[] {
					accessControlManager.privilegeFromName(READ)
			})){
				return READ_ONLY;
			}
		} catch (RepositoryException e) {
			throw new Exception("ACLType Unknown " + e);
		}
		return null;

	}

	private String getOwner(Node node) throws PathNotFoundException, RepositoryException {
		String portalLogin;
		try{
			portalLogin = node.getProperty(NodeProperty.PORTAL_LOGIN.toString()).getString();
		}catch (Exception e) {
			Node nodeOwner = node.getNode(NodeProperty.OWNER.toString());		
			portalLogin = nodeOwner.getProperty(NodeProperty.PORTAL_LOGIN.toString()).getString();
		}
		return portalLogin;

	}
	
	

}
