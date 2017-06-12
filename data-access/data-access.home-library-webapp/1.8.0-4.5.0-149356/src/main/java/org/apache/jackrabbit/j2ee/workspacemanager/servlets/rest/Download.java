package org.apache.jackrabbit.j2ee.workspacemanager.servlets.rest;

import java.io.IOException;

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

public class Download extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(Download.class);
	private static final long serialVersionUID = 1L;
//	private static final int BYTES_DOWNLOAD = 4096;

	public Download() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {


		String absPath = request.getParameter(ServletParameter.ABS_PATH);

		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		Session session = null;
		XStream xstream = null;
		String sessionId = null;
//		String xmlConfig;
		
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

			String login = sessionManager.getLogin(sessionId);

			
			Utils.downloadByPath(request, response, xstream, session, absPath, login);


		} catch (Exception e) {
			logger.error("Error repository ex " + e);

		} finally {
			if (!exist)
				sessionManager.releaseSession(sessionId);		

		}
	}

	
}

