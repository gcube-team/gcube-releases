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
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.apache.jackrabbit.j2ee.workspacemanager.servlets.acl.JCRAccessControlManager;
import org.apache.jackrabbit.j2ee.workspacemanager.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class CanReadNode extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(CanReadNode.class);
	private static final long serialVersionUID = 1L;

	public CanReadNode() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String login = request.getParameter(ServletParameter.PORTAL_LOGIN);
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
			logger.info("CanReadNode servlet - ABS path not set ");
		}
		
	
		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		Session session = null;
		XStream xstream = null;
		String xmlConfig = null;
		SessionManager sessionManager = null;

		String sessionId = null;
		try {
			xstream = new XStream(new DomDriver("UTF-8"));

			sessionManager = SessionManager.getInstance(rep);
			session = sessionManager.newSession(login);
			sessionId = session.toString();

			JCRAccessControlManager accessManager = new JCRAccessControlManager(session, sessionManager.getLogin(request));
		
			if (absPath==null){
				Node node = session.getNodeByIdentifier(id);
				absPath = node.getPath();
			}
			
//			logger.info("Can " + login + " read node with path= " + absPath + " or id= " + id);
			
			Boolean flag = accessManager.canReadNode(absPath);
			
			logger.debug("Can " + session.getUserID() + " read node " + absPath + "? " + flag);
			xmlConfig = xstream.toXML(flag);
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);
			out.close();
			out.flush();
		} catch (Exception e) {
			
			logger.error("Error repository ex " + e);
			xmlConfig = xstream.toXML(e.toString());
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);
			out.close();
			out.flush();
			
		} finally {
			
			if (session!=null)
				sessionManager.releaseSession(sessionId);
		}
	}


}

