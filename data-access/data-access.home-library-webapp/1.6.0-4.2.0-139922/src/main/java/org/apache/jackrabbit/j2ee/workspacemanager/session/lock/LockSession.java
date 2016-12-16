package org.apache.jackrabbit.j2ee.workspacemanager.session.lock;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.session.SessionManager;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class LockSession extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(LockSession.class);
	private static final long serialVersionUID = 1L;

	public LockSession() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String sessionId = request.getParameter(ServletParameter.UUID);
//		String uuid = request.getParameter(ServletParameter.UUID);	
		String id = request.getParameter(ServletParameter.ID);	
	
		logger.info("Servlet LockSession called with parameters: [session ID: "+ sessionId + " - on item ID: " + id +"]");
		
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

			Boolean flag = false;
			try{
			
				LockManager lockManager = session.getWorkspace().getLockManager();
				Lock lock = lockManager.lock(session.getNodeByIdentifier(id).getPath(), true, true, 0, "");
				logger.trace("LOCK on Node: " + lock.getNode().getPath());
				session.save();
				flag = true;
			
			} catch (Exception e) {
				logger.error("Error locking item with id: " + id + " in session " + sessionId, e);
			}
			
			xmlConfig = xstream.toXML(flag);
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);

		} catch (Exception e) {
			logger.error("Error repository ex " + e);
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


}
