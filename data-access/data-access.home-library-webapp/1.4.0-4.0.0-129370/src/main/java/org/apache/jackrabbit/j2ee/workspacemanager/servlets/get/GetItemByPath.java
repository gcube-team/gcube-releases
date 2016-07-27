package org.apache.jackrabbit.j2ee.workspacemanager.servlets.get;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GetItemByPath extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(GetItemByPath.class);
	private static final long serialVersionUID = 1L;

	public GetItemByPath() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String login = request.getParameter(ServletParameter.LOGIN);
		String sessionId = request.getParameter(ServletParameter.UUID);
		final String user = request.getParameter(ConfigRepository.USER);
		final char[] pass = request.getParameter(ConfigRepository.PASSWORD).toCharArray();

		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		SessionImpl session = null;
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
				session = sessionManager.newSession(login, user, pass);
				sessionId = session.toString();
			}

			String path = null;
			try{
				path = new String(request.getParameter(ServletParameter.PATH).getBytes("iso-8859-1"), "UTF-8");
				logger.info("Servlet GetItemByPath called with parameters: [path: "+ path + "]");
				ItemDelegate item = getDelegateItemByPath(session, path, login);
				xmlConfig = xstream.toXML(item);
//				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			} catch (PathNotFoundException e) {
				logger.error("PathNotFoundException: " + path);
				xmlConfig = xstream.toXML(path + " Path Not Found");
				//		response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			}

		} catch (Exception e) {
			logger.error("Error repository ex " + e);
			xmlConfig = xstream.toXML(e.toString());
			//	response.setContentLength(xmlConfig.length());
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


	/**
	 * Get Item by path
	 * @param session
	 * @param absPath
	 * @param login
	 * @return
	 * @throws Exception
	 */
	private ItemDelegate getDelegateItemByPath(SessionImpl session, String absPath, String login) throws Exception {

		Node node = session.getNode(absPath);
		NodeManager wrap = new NodeManager(node, login);

		return wrap.getItemDelegate();

	}




}
