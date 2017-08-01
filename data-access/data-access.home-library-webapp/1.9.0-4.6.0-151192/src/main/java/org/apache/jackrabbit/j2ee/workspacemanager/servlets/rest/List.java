package org.apache.jackrabbit.j2ee.workspacemanager.servlets.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
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
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.apache.jackrabbit.j2ee.workspacemanager.session.SessionManager;
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
		String hidden = request.getParameter(ServletParameter.SHOW_HIDDEN);

		boolean showHidden = false;
		if (hidden!=null)
			showHidden = Boolean.parseBoolean(hidden);
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

			Map<String, Boolean> children = new HashMap<String, Boolean>();
			Workspace workspace = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
			logger.info("*** Get workspace of " + workspace.getRoot().getPath());
				
			absPath = Utils.cleanPath(workspace, absPath);
			logger.info("absPath " +absPath);
			WorkspaceItem item = workspace.getItemByPath(absPath);
			logger.info("item "+  item.getPath());
			if(item.isFolder()){
				logger.info("is folder? " + item.isFolder());
				WorkspaceFolder folder = (WorkspaceFolder) item;
				java.util.List<WorkspaceItem> list = folder.getAllChildren((Boolean) showHidden);
				logger.info(list.toString());
				for(WorkspaceItem child: list){			
					String name = null;
					if (child.getId().equals(child.getIdSharedFolder())){
						JCRWorkspaceSharedFolder shared = (JCRWorkspaceSharedFolder) child;
						if (shared.isVreFolder())
							name = shared.getDisplayName();
					}
					if (name==null)
						name = child.getName();
					children.put(name, child.isFolder());
				}
			}

			xmlConfig = xstream.toXML(children);
			out.println(xmlConfig);

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

}

