package org.apache.jackrabbit.j2ee.workspacemanager.lock;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.ConfigRepository;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.apache.jackrabbit.j2ee.workspacemanager.ServletParameter;
import org.apache.jackrabbit.j2ee.workspacemanager.SessionManager;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibrary.model.exceptions.InternalErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Delete Aces for a resource.
 */

public class UnlockSession extends HttpServlet {
	private static Logger logger = LoggerFactory.getLogger(UnlockSession.class);


	private static final long serialVersionUID = 1L;

	public UnlockSession() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {


		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		final String uuid = request.getParameter(ServletParameter.UUID);	
		final String id = request.getParameter(ServletParameter.ID);


//		logger.info("Servlet UnlockSession called with parameters: [uuid: "+ uuid + "]");

		Repository rep = RepositoryAccessServlet
				.getRepository(getServletContext());
		//		SessionImpl session = null;
		SessionManager sessionManager = null;

		XStream xstream = null;
		String xmlConfig = null;
		List<ItemDelegate> children = null;

		try {
			xstream = new XStream(new DomDriver("UTF-8"));

			sessionManager = SessionManager.getInstance(rep);
			if (sessionManager.sessionExists(uuid)){
				SessionImpl session = sessionManager.getSession(uuid);

				LockManager lockManager = session.getWorkspace().getLockManager();
				String pathNode = session.getNodeByIdentifier(id).getPath();
				if (lockManager.isLocked(pathNode)){
					lockManager.unlock(pathNode);
					logger.trace("Remove Lock from node: " + pathNode);
				}
			}

			//			session = manager.getSession(uuid, request.getParameter(ConfigRepository.USER), request.getParameter(ConfigRepository.PASSWORD).toCharArray());

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
			sessionManager.releaseSession(uuid);

//			logger.info("RELESED SESSION WITH UUID " + uuid);

			out.close();
			out.flush();

		}
	}




}