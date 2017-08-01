package org.apache.jackrabbit.j2ee.workspacemanager.servlets.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.session.SessionManager;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.util.zip.UnzipUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class Unzip extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(Unzip.class);
	private static final long serialVersionUID = 1L;

	public Unzip() {
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

			//			String portalLogin = sessionManager.getLogin(request);
			unzip(request, response, out, xstream, session);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error repository ex " + e.getMessage());
			xmlConfig = xstream.toXML(e.toString());
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);

		} finally {
			if (!exist){
				sessionManager.releaseSession(sessionId);
				//				logger.info("Released session " + sessionId);
			}
			out.close();
			out.flush();
		}
	}




	/**
	 * Upload a file
	 * @param request
	 * @param response
	 * @param out
	 * @param xstream
	 * @param session
	 * @param portalLogin
	 */
	private void unzip(HttpServletRequest request, HttpServletResponse response, PrintWriter out, XStream xstream, Session session) {

		String xmlConfig;
		String name = null;

		try {   

			name = request.getParameter(ServletParameter.NAME);
			String parentPath = request.getParameter(ServletParameter.PARENT_PATH);
			
		
			boolean replace = Boolean.parseBoolean(request.getParameter(ServletParameter.REPLACE));
			boolean hardReplace = Boolean.parseBoolean(request.getParameter(ServletParameter.HARD_REPLACE));

			logger.info("Called REST API Unzip with paremeters: " + ServletParameter.NAME + ": " + name + " - " + ServletParameter.PARENT_PATH + ": " + parentPath + " - " +ServletParameter.REPLACE + ": " + replace+ " - " +ServletParameter.HARD_REPLACE + ": " + hardReplace );
			InputStream inputStream = request.getInputStream();
			Workspace workspace = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
			
			parentPath = Utils.cleanPath(workspace, parentPath);
			
			WorkspaceFolder parent = (WorkspaceFolder) workspace.getItemByPath(parentPath);
	
			WorkspaceFolder unzipFolder = UnzipUtil.unzip(parent, inputStream, name, replace, hardReplace);

			xmlConfig = xstream.toXML(unzipFolder.getPath());
			//	System.out.println(xmlConfig);
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);


		} catch (Exception e) {
			logger.error("Error unzipping file: " + name, e);
			xmlConfig = xstream.toXML(e.toString());
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);
		}

	}

}
