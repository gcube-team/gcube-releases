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
		
		final String login = request.getParameter(ServletParameter.LOGIN);
		final String uuid = request.getParameter(ServletParameter.UUID);	
		final String id = request.getParameter(ServletParameter.ID);	
		final String user = request.getParameter(ConfigRepository.USER);	
		final String pass = request.getParameter(ConfigRepository.PASSWORD);

		logger.info("Servlet LockSession called with parameters: [uuid: "+ uuid + "]");

		Repository rep = RepositoryAccessServlet
				.getRepository(getServletContext());

		SessionManager sessionManager = null;

		XStream xstream = null;
		String xmlConfig = null;
		List<ItemDelegate> children = null;

		try {
			xstream = new XStream(new DomDriver("UTF-8"));

			sessionManager = SessionManager.getInstance(rep);
			if (!sessionManager.sessionExists(uuid)){
				SessionImpl session = sessionManager.newSession(login, user, pass.toCharArray());
				LockManager lockManager = session.getWorkspace().getLockManager();
				Lock lock = lockManager.lock(session.getNodeByIdentifier(id).getPath(), true, true, 0, "");
				logger.trace("LOCK on Node: " + lock.getNode().getPath());
				session.save();
				logger.info("Getting new session, with uuid " + uuid + ", lock node with id " + id);				
			}


			//			children = getChildren(session, id, login);

			xmlConfig = xstream.toXML(children);
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);

		} catch (Exception e) {
			logger.error("Error repository ex " + e);
			xmlConfig = xstream.toXML(e.toString());
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);

		} finally {
			//			manager.releaseSession(uuid);

			out.close();
			out.flush();

		}
	}





}