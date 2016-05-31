package org.apache.jackrabbit.j2ee.workspacemanager.servlets.ftp;

import java.io.IOException;
import java.io.PrintWriter;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.ConfigRepository;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.ServletParameter;
import org.apache.jackrabbit.j2ee.workspacemanager.SessionManager;
import org.apache.jackrabbit.j2ee.workspacemanager.servlets.Utils;
import org.apache.jackrabbit.j2ee.workspacemanager.storage.GCUBEStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class Delete extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(Delete.class);
	private static final long serialVersionUID = 1L;


	public Delete() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String login = request.getParameter(ServletParameter.LOGIN);
		String sessionId = request.getParameter(ServletParameter.UUID);
		final String user = request.getParameter(ConfigRepository.USER);
		final char[] pass = request.getParameter(ConfigRepository.PASSWORD).toCharArray();

		String scope = request.getParameter(ServletParameter.SCOPE);	
		String serviceName = request.getParameter(ServletParameter.SERVICE_NAME);
//		String portalLogin = request.getParameter(ServletParameter.PORTAL_LOGIN);
		String path = request.getParameter(ServletParameter.ABS_PATH);
		
		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());

		SessionImpl session = null;
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
				session = sessionManager.newSession(login, user, pass);
				sessionId = session.toString();
				//				logger.info(sessionId + " does not exist, a new session has been created " + session.toString());
			}

			Boolean flag = false;
			try {   


				Node node = session.getNode(path);

				//	System.out.println(node.getPrimaryNodeType());
				Boolean isFolder = (node.getPrimaryNodeType().getName().equals(Utils.NT_WORKSPACE_FOLDER) || node.getPrimaryNodeType().getName().equals(Utils.NT_WORKSPACE_SHARED_FOLDER));


				session.removeItem(path);
				session.save();

				GCUBEStorage storage = new GCUBEStorage(login, scope, serviceName);
				if (isFolder)
					storage.removeRemoteFolder(path);
				else
					storage.removeRemoteFile(path);


				flag = true;

				xmlConfig = xstream.toXML(flag);
				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);

			} catch (Exception e) {
				logger.error("Error deleting item: " + path, e);
				xmlConfig = xstream.toXML(e.toString());
				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			}
		

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
