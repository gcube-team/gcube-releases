package org.apache.jackrabbit.j2ee.workspacemanager.servlets.post;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Node;
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


public class AddNode extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(AddNode.class);
	private static final long serialVersionUID = 1L;

	public AddNode() {
		super();
	}

	protected void doPost(HttpServletRequest request,
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
				//				logger.info(sessionId + " already exists, get it");
			}
			else{				 
				session = sessionManager.newSession(login, user, pass);
				sessionId = session.toString();
				//				logger.info(sessionId + " does not exist, a new session has been created " + session.toString());
			}


			String parentId = null;
			String id = null;
			try{
				parentId = request.getParameter(ServletParameter.PARENT_ID);
				id = request.getParameter(ServletParameter.ID);
				logger.info("Servlet AddNode called with id: "+ id + " to parentId " + parentId);
				ItemDelegate new_item = addNodeToParent(session, parentId, id);

				xmlConfig = xstream.toXML(new_item);
				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			} catch (Exception e) {
				logger.error("Error adding item with id: " + id + " to parentId " + parentId, e);
				xmlConfig = xstream.toXML(e.toString());
				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			}

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
	 * Add node id to parent
	 * @param session
	 * @param parentId
	 * @param id
	 * @return
	 */
	private ItemDelegate addNodeToParent(SessionImpl session, String parentId,
			String id) {
		ItemDelegate item = null;
		try{
			Node parent = session.getNodeByIdentifier(parentId);
			Node node = parent.addNode(id);
			NodeManager wrap = new NodeManager(node, "");

			item = wrap.getItemDelegate();

		}catch (Exception e) {
			logger.error("impossible to add item " + id + " to parentId " + parentId, e.getMessage());
		}
		return item;
	}

}
