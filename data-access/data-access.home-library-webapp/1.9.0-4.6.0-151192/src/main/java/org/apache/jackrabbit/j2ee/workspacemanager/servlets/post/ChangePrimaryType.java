package org.apache.jackrabbit.j2ee.workspacemanager.servlets.post;

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
import org.apache.jackrabbit.j2ee.workspacemanager.session.SessionManager;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class ChangePrimaryType extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(ChangePrimaryType.class);
	private static final long serialVersionUID = 1L;


	public ChangePrimaryType() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//		response.setContentType("text/plain");
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

			String id = null;
			String primaryType= null;
			try{
				id = request.getParameter(ServletParameter.ID);
				primaryType = request.getParameter(ServletParameter.PRIMARY_TYPE);
				logger.info("Servlet Change Primary Type called with parameters: [id: "+ id + " - primaryType: " + primaryType +"]");
				Node node = session.getNodeByIdentifier(id);
				node.setPrimaryType(primaryType);
				session.save();
				xmlConfig = xstream.toXML(true);
				out.println(xmlConfig);
			} catch (Exception e) {
				logger.error("Error removing item by path: " + id, e);
				xmlConfig = xstream.toXML(false);
				out.println(xmlConfig);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error repository ex " + e.getMessage());
			xmlConfig = xstream.toXML(e.toString());
			out.println(xmlConfig);

		} finally {
			if (!exist)
				sessionManager.releaseSession(sessionId);

			out.close();
		}
	}


}
