package org.apache.jackrabbit.j2ee.workspacemanager.servlets.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlManager;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.accessmanager.AccessControlUtil;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.apache.jackrabbit.j2ee.workspacemanager.SessionManager;
import org.apache.jackrabbit.j2ee.workspacemanager.servlets.Utils;
import org.apache.jackrabbit.j2ee.workspacemanager.util.CustomPrivilege;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class List extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(List.class);
	private static final long serialVersionUID = 1L;

	public List() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String sessionId = request.getParameter(ServletParameter.UUID);
		String absPath = request.getParameter(ServletParameter.ABS_PATH);
//		String login = request.getParameter(ServletParameter.PORTAL_LOGIN);

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
				session = sessionManager.newSession(request);
				sessionId = session.toString();
			}
			//			AccessControlManager accessControlManager = session.getAccessControlManager();
			//			boolean canRead = accessControlManager.hasPrivileges(absPath, new Privilege[] {
			//					accessControlManager.privilegeFromName(CustomPrivilege.JCR_READ)
			//			});

			String login = sessionManager.getLogin(sessionId);
			
			PrincipalManager principalManager = AccessControlUtil.getPrincipalManager(session);

			Principal principal = principalManager.getPrincipal(login);
		

			JackrabbitAccessControlManager  jacm = (JackrabbitAccessControlManager) session.getAccessControlManager();
			Set<Principal> principals = new HashSet<Principal>();
			principals.add(principal);
			logger.info("Check if " + principal.getName() + " can read node " + absPath);
			boolean canRead = jacm.hasPrivileges(absPath, principals, new Privilege[] {
					jacm.privilegeFromName(CustomPrivilege.JCR_READ)
			});

			logger.info("Can " + principal.getName() + " read node " + absPath + "? " + canRead);		
			
			if (canRead)				
				listFolder(request, response, out, xstream, session, absPath);
			else
				xmlConfig = xstream.toXML("No privilege to read the folder");

		} catch (Exception e) {
			logger.error("Error repository ex " + e);
			xmlConfig = xstream.toXML(e.toString());
			//			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);

		} finally {
			if (!exist){
				sessionManager.releaseSession(sessionId);
				//				logger.info("Released session " + sessionId);
			}
			out.close();
			//			out.flush();
		}
	}

	private void listFolder(HttpServletRequest request,
			HttpServletResponse response, PrintWriter out, XStream xstream,
			Session session, String folderPath) {
		String xmlConfig;
		Map<String, Boolean> children = null;

		try {   
			children = new HashMap<String, Boolean>();

			logger.info("Servlet ListFolder called with parameters: [folderPath: "+ folderPath + "]");
			NodeIterator iterator = session.getNode(folderPath).getNodes();

			while(iterator.hasNext()) {

				Node node = iterator.nextNode();

				String path = null;
				try {
					path = node.getPath();
					String name = path.substring(path.lastIndexOf('/') + 1);
					if ((name.equals("Trash") || (name.equals("MySpecialFolders") ||(name.startsWith("rep:")) || (name.startsWith("hl:")) || (name.startsWith(".")))))
						continue;
				} catch (RepositoryException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					Boolean flag = node.getPrimaryNodeType().getName().equals(Utils.NT_WORKSPACE_FOLDER);
					children.put(node.getName(), flag);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			xmlConfig = xstream.toXML(children);
			//	System.out.println(xmlConfig);
			//			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);


		} catch (Exception e) {
			logger.error("Error getting children for path: " + folderPath, e);
			xmlConfig = xstream.toXML(e.toString());
			//			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);
		}


	}
}

