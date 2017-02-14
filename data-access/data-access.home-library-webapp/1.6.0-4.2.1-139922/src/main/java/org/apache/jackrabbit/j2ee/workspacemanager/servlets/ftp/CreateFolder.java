package org.apache.jackrabbit.j2ee.workspacemanager.servlets.ftp;

import java.io.IOException;
import java.io.PrintWriter;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.ItemDelegateWrapper;
import org.apache.jackrabbit.j2ee.workspacemanager.session.SessionManager;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;
import org.gcube.common.homelibary.model.util.WorkspaceItemAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class CreateFolder extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(CreateFolder.class);
	private static final long serialVersionUID = 1L;

	public CreateFolder() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String sessionId = request.getParameter(ServletParameter.UUID);

//		String portalLogin = request.getParameter(ServletParameter.PORTAL_LOGIN);

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
		try {
			xstream = new XStream(new DomDriver("UTF-8"));

			sessionManager = SessionManager.getInstance(rep);
			exist = sessionManager.sessionExists(sessionId); 
			if (exist){				
				session = sessionManager.getSession(sessionId);
				//				logger.info(sessionId + " already exists, get it");
			}
			else{				 
				session = sessionManager.newSession(request);
				sessionId = session.toString();
				//				logger.info(sessionId + " does not exist, a new session has been created " + session.toString());
			}

			ItemDelegate item = createFolder(sessionManager.getLogin(request), name, description, parentPath, session);
			xmlConfig = xstream.toXML(item.getPath());
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


	private ItemDelegate createFolder(String portalLogin, String name, String description, String parentPath, Session session) throws Exception {

		ItemDelegate new_item = null;
		try {   

			Node parent = session.getNode(parentPath);
			ItemDelegate delegate = new ItemDelegate();
			delegate.setName(name);
			delegate.setTitle(name);
			delegate.setDescription(description);
			delegate.setParentId(parent.getIdentifier());
			delegate.setOwner(portalLogin);
			delegate.setPrimaryType(PrimaryNodeType.NT_WORKSPACE_FOLDER);
			delegate.setLastAction(WorkspaceItemAction.CREATED);

			ItemDelegateWrapper wrapper = new ItemDelegateWrapper(delegate, portalLogin);
			new_item = wrapper.save(session);

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return new_item;

	}
	
	

//	private void createFolder(String name, String description, String parentPat,
//			SessionImpl session) {
//
//		String xmlConfig;
//		String portalLogin;
//		String filenameWithExtension = null;
//
//		String name;
//		String description;
//		String parentPath;
//
//		try {   
//
//
//
//
//			Node parent = session.getNode(parentPath);
//
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
//			ItemDelegate new_item = wrapper.save(session);
//
//			xmlConfig = xstream.toXML(new_item.getPath());
//			//	System.out.println(xmlConfig);
//			response.setContentLength(xmlConfig.length());
//			out.println(xmlConfig);
//
//		} catch (Exception e) {
//			logger.error("Error saving inpustream for file: " + filenameWithExtension, e);
//			xmlConfig = xstream.toXML(e.toString());
//			response.setContentLength(xmlConfig.length());
//			out.println(xmlConfig);
//		}
//
//	}


}
