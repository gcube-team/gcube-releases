package org.apache.jackrabbit.j2ee.workspacemanager.servlets.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.security.Privilege;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlManager;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.accessmanager.AccessControlUtil;
import org.apache.jackrabbit.j2ee.workspacemanager.session.SessionManager;
import org.apache.jackrabbit.j2ee.workspacemanager.util.CustomPrivilege;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class CreateFolder extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(CreateFolder.class);
	private static final long serialVersionUID = 1L;
	private static final String PORTAL_LOGIN = "hl:portalLogin";
	private static final String OWNER_NODE = "hl:owner";

	public CreateFolder() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request,response);

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String sessionId = request.getParameter(ServletParameter.UUID);
		//		String login = request.getParameter(ServletParameter.PORTAL_LOGIN);

		String name = request.getParameter(ServletParameter.NAME);
		String description = request.getParameter(ServletParameter.DESCRIPTION);
		String parentPath = request.getParameter(ServletParameter.PARENT_PATH);

		logger.info("Servlet CreateFolder called with parameters: [ name: " + name + " - description: " + description + "- parentPath: " + parentPath + "]");

		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());

		Session session = null;
		XStream xstream = null;
		String xmlConfig = null;
		SessionManager sessionManager = null;
		boolean exist = false;
		boolean canWrite = false;
		try {
			xstream = new XStream(new DomDriver("UTF-8"));

			sessionManager = SessionManager.getInstance(rep);
			exist = sessionManager.sessionExists(sessionId); 
			if (exist)		
				session = sessionManager.getSession(sessionId);
			else{				 
				session = sessionManager.newSession(request);
				sessionId = session.toString();
			}
			String login = sessionManager.getLogin(sessionId);

			Node node = session.getNode(parentPath);
			String owner = null;
			if (node.hasProperty(PORTAL_LOGIN))
				owner = node.getProperty(PORTAL_LOGIN).getString();
			else if (node.hasNode(OWNER_NODE))
				owner = node.getNode(OWNER_NODE).getProperty(PORTAL_LOGIN).getString();

			if (owner.equals(login))
				canWrite = true;
			else{


				PrincipalManager principalManager = AccessControlUtil.getPrincipalManager(session);		
				Principal principal = principalManager.getPrincipal(login);

				JackrabbitAccessControlManager  jacm = (JackrabbitAccessControlManager) session.getAccessControlManager();
				Set<Principal> principals = new HashSet<Principal>();
				principals.add(principal);
				logger.info("Check if " + principal.getName() + " can add nodes to " + parentPath);
				canWrite = jacm.hasPrivileges(parentPath, principals, new Privilege[] {
						jacm.privilegeFromName(CustomPrivilege.JCR_ADD_CHILD_NODES)
				});

			
			logger.info("Can " + principal.getName() + " add nodes to " + parentPath + "? " + canWrite);	
			}
			if (canWrite){
				Workspace workspace = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
				
				parentPath = Utils.cleanPath(workspace, parentPath);
				
				WorkspaceItem folder = workspace.getItemByPath(parentPath);
				WorkspaceItem item = workspace.createFolder(name, description, folder.getId());
				
//				ItemDelegate item = createFolder(login, name, description, parentPath, session);
				xmlConfig = xstream.toXML(item.getPath());
			}else
				xmlConfig = xstream.toXML("No privilege to create folder");

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error repository ex " + e.getMessage());
			xmlConfig = xstream.toXML(e.toString());
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);

		} finally {
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);
			if (!exist){
				sessionManager.releaseSession(sessionId);
				//				logger.info("Released session " + sessionId);
			}
			out.close();
			out.flush();
		}
	}


//	private ItemDelegate createFolder(String portalLogin, String name, String description, String parentPath, Session session) throws Exception {
//
//		ItemDelegate new_item = null;
//		try {   
//
//			Node parent = session.getNode(parentPath);
//			ItemDelegate delegate = new ItemDelegate();
//			delegate.setName(name);
//			delegate.setTitle(name);
//			delegate.setDescription(description);
//			delegate.setParentId(parent.getIdentifier());
//			delegate.setOwner(portalLogin);
//			delegate.setPrimaryType(PrimaryNodeType.NT_WORKSPACE_FOLDER);
//			delegate.setLastAction(WorkspaceItemAction.CREATED);
//
//			ItemDelegateWrapper wrapper = new ItemDelegateWrapper(delegate, portalLogin);
//			new_item = wrapper.save(session);
//
//		} catch (Exception e) {
//			throw new Exception(e.getMessage());
//		}
//		return new_item;
//
//	}


}
