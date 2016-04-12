package org.apache.jackrabbit.j2ee.workspacemanager.lock;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jcr.Repository;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.ConfigRepository;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.ServletParameter;
import org.apache.jackrabbit.j2ee.workspacemanager.SessionManager;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Delete Aces for a resource.
 */

public class ReleaseSession extends HttpServlet {
	private static Logger logger = LoggerFactory.getLogger(ReleaseSession.class);


	private static final long serialVersionUID = 1L;

	public ReleaseSession() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {


		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

//		final String login = request.getParameter(ConfigRepository.USER);	
//		final String pass = request.getParameter(ConfigRepository.PASSWORD);
		final String uuid = request.getParameter(ServletParameter.UUID);	
		
//		logger.info("Servlet ReleaseSession called");

		Repository rep = RepositoryAccessServlet
				.getRepository(getServletContext());

		SessionManager sessionManager = null;

		XStream xstream = null;
		String xmlConfig = null;

		try {
			xstream = new XStream(new DomDriver("UTF-8"));
			sessionManager = SessionManager.getInstance(rep);
			
//			logger.info("Release session " + uuid);		
			sessionManager.releaseSession(uuid);

//			xmlConfig = xstream.toXML(session.toString());
//			response.setContentLength(xmlConfig.length());
//			out.println(xmlConfig);

		} catch (Exception e) {
			logger.error("Error repository ex " + e);
			xmlConfig = xstream.toXML(e.toString());
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);

		} finally {

			out.close();
			out.flush();

		}
	}





}