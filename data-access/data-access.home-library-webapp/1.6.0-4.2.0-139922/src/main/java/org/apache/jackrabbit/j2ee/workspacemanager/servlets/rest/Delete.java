package org.apache.jackrabbit.j2ee.workspacemanager.servlets.rest;

import java.io.IOException;
import java.io.PrintWriter;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.apache.jackrabbit.j2ee.workspacemanager.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class Delete extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(Delete.class);
	private static final long serialVersionUID = 1L;
//	private static final String PORTAL_LOGIN = "hl:portalLogin";
//	private static final String OWNER_NODE = "hl:owner";


	public Delete() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request,response);

	}
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String sessionId = request.getParameter(ServletParameter.UUID);

		//		String scope = request.getParameter(ServletParameter.SCOPE);	
		//		String serviceName = request.getParameter(ServletParameter.SERVICE_NAME);
		//		String login = request.getParameter(ServletParameter.PORTAL_LOGIN);
		String path = request.getParameter(ServletParameter.ABS_PATH);

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
			}
			else{				 
				session = sessionManager.newSession(request);
				sessionId = session.toString();
			}

			//			boolean canDelete = false;
			Boolean flag = false;
			try {   

				//				String login = sessionManager.getLogin(sessionId);
				//
				//				Node node = session.getNode(path);
				//				String owner = null;
				//				if (node.hasProperty(PORTAL_LOGIN))
				//					owner = node.getProperty(PORTAL_LOGIN).getString();
				//				else if (node.hasNode(OWNER_NODE))
				//					owner = node.getNode(OWNER_NODE).getProperty(PORTAL_LOGIN).getString();
				//
				//				if (owner.equals(login))
				//					canDelete = true;
				//				else{
				//
				//					PrincipalManager principalManager = AccessControlUtil.getPrincipalManager(session);
				//
				//					Principal principal = principalManager.getPrincipal(login);
				//
				//					JackrabbitAccessControlManager  jacm = (JackrabbitAccessControlManager) session.getAccessControlManager();
				//					Set<Principal> principals = new HashSet<Principal>();
				//					principals.add(principal);
				//					logger.info("Check if " + principal.getName() + " can delete node " + path);
				//					canDelete = jacm.hasPrivileges(path, principals, new Privilege[] {
				//							jacm.privilegeFromName(CustomPrivilege.JCR_REMOVE_CHILD_NODES), jacm.privilegeFromName(CustomPrivilege.NO_LIMIT)
				//					});
				//
				//					logger.info("Can " + principal.getName() + " delete node " + path + "? " + canDelete);
				//				}
				//
				//				if (canDelete){
				Workspace workspace = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
				path = Utils.cleanPath(workspace, path);
				WorkspaceItem item = workspace.getItemByPath(path);
				//					if (item.isFolder())
				item.remove();


				//	System.out.println(node.getPrimaryNodeType());
				//					Boolean isFolder = (node.getPrimaryNodeType().getName().equals(Utils.NT_WORKSPACE_FOLDER) || node.getPrimaryNodeType().getName().equals(Utils.NT_WORKSPACE_SHARED_FOLDER));
				//
				//
				//					session.removeItem(path);
				//					session.save();
				//
				//					GCUBEStorage storage = new GCUBEStorage(sessionManager.getLogin(request));
				//					if (isFolder)
				//						storage.removeRemoteFolder(path);
				//					else
				//						storage.removeRemoteFile(path);

				flag = true;
				xmlConfig = xstream.toXML(flag);
				
				//				}else
				//					xmlConfig = xstream.toXML("No privilege to Delete node");
				//
				//				response.setContentLength(xmlConfig.length());
				//				out.println(xmlConfig);

			} catch (Exception e) {
				logger.error("Error deleting item: " + path, e);
				xmlConfig = xstream.toXML(e.toString());
				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (!exist){
				sessionManager.releaseSession(sessionId);
				//				logger.info("Released session " + sessionId);
			}
			out.close();
			out.flush();
		}
	}



}
