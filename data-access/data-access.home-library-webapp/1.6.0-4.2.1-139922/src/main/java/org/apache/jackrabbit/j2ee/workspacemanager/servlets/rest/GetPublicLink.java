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

public class GetPublicLink extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(GetPublicLink.class);
	private static final long serialVersionUID = 1L;

	public GetPublicLink() {
		super();
	}


	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String sessionId = request.getParameter(ServletParameter.UUID);
		String absPath = request.getParameter(ServletParameter.ABS_PATH);
		String shortUrl = request.getParameter(ServletParameter.SHORT_URL);
		//		String login = request.getParameter(ServletParameter.PORTAL_LOGIN);
		boolean isShort = false;
		if (shortUrl!=null)
			isShort = Boolean.parseBoolean(shortUrl);
		
		logger.info("Calling GetPublicLink servlet with parameters --> sessionID: " + sessionId + " - absPath: "+ absPath + " - shortUrl: " + isShort);
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

			Workspace workspace = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
			
			absPath = Utils.cleanPath(workspace, absPath);
			logger.info("---> CLEAN PATH " + absPath);
			WorkspaceItem item = workspace.getItemByPath(absPath);
			String url = null;
			if(!item.isFolder())
				url = item.getPublicLink(isShort);
			xmlConfig = xstream.toXML(url);
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

