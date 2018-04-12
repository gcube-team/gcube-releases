package org.apache.jackrabbit.j2ee.accessmanager.privileges;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.servlets.acl.JCRAccessControlManager;
import org.apache.jackrabbit.j2ee.workspacemanager.session.SessionManager;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class CanDelete extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(CanDelete.class);
	private static final long serialVersionUID = 1L;

	public CanDelete() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		String login = request.getParameter(ServletParameter.PORTAL_LOGIN);
		String sessionId = request.getParameter(ServletParameter.UUID);
		boolean isRoot = Boolean.valueOf(request.getParameter(ServletParameter.ISROOT));
		String id = null;
		try{
			id = request.getParameter(ServletParameter.ID);
		} catch (Exception e) {
			logger.info("CanModifyProperties servlet - ID path not set ");
		}
		
		String absPath = null;
		try{
			absPath = request.getParameter(ServletParameter.ABS_PATH);
//			absPath = new String(request.getParameter(ServletParameter.ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
		} catch (Exception e) {
			logger.info("CanDelete servlet - ABS path not set ");
		}
		
		
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
				
				JCRAccessControlManager accessManager = new JCRAccessControlManager(session, sessionManager.getLogin(request));
				
				if (absPath==null){
					Node node = session.getNodeByIdentifier(id);
					absPath = node.getPath();
				}
				Boolean flag = accessManager.canDelete(session.getUserID(), absPath, isRoot);
				
				logger.debug("Can " + session.getUserID() + " delete node " + absPath + "? " + flag);
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
			if (!exist){
				sessionManager.releaseSession(sessionId);
			}
			out.close();
			out.flush();
		}
	}



	
	
	
	
	

}

