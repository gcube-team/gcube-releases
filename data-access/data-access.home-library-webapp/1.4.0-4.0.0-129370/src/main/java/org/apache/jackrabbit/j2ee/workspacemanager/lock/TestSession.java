package org.apache.jackrabbit.j2ee.workspacemanager.lock;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
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

/**
 * Delete Aces for a resource.
 */

public class TestSession extends HttpServlet {
	private static Logger logger = LoggerFactory.getLogger(TestSession.class);


	private static final long serialVersionUID = 1L;

	public TestSession() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		boolean flag = false;

		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		final String id = request.getParameter(ServletParameter.ID);	
		final String uuid = request.getParameter(ServletParameter.UUID);	
		final String login = request.getParameter(ServletParameter.LOGIN);	

		logger.info("Servlet TestSession called with parameters: [id: "+ id + "]");

		Repository rep = RepositoryAccessServlet
				.getRepository(getServletContext());
		SessionImpl session = null;

		XStream xstream = null;
		String xmlConfig = null;
		List<ItemDelegate> children = null;

		SessionManager manager = null;
		try {
			xstream = new XStream(new DomDriver("UTF-8"));
	
			manager = SessionManager.getInstance(rep);

			flag = manager.sessionExists(uuid); 
			if (flag){
				session = manager.getSession(uuid);
				Node node = session.getNodeByIdentifier(id);
				logger.info(node.getPath() + " is locked? " + node.isLocked());
			} else
				session = manager.newSession(login, request.getParameter(ConfigRepository.USER), request.getParameter(ConfigRepository.PASSWORD).toCharArray());

			children = getChildren(session, id, login);
			xmlConfig = xstream.toXML(children);
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);

		} catch (Exception e) {
			logger.error("Error repository ex " + e);
			xmlConfig = xstream.toXML(e.toString());
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);

		} finally {
			if (!flag)
				manager.releaseSession(uuid);

			out.close();
			out.flush();

		}
	}

	private List<ItemDelegate> getChildren(SessionImpl session, String identifier, String login) throws Exception {

		Node folderNode = session.getNodeByIdentifier(identifier);
		logger.info(folderNode.getPath());
		NodeIterator iterator = folderNode.getNodes();
		List<ItemDelegate> children = new ArrayList<ItemDelegate>();
		while(iterator.hasNext()) {
			//
			Node node = iterator.nextNode();
			String path = null;
			try {
				path = node.getPath();
				//				String title = node.getName();
				String name = path.substring(path.lastIndexOf('/') + 1);
				if ((name.equals("Trash") || (name.equals("MySpecialFolders") ||(name.startsWith("rep:")) || (name.startsWith("hl:")) || (name.startsWith(".")))))
					continue;
			} catch (RepositoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//			String name = path.substring(path.lastIndexOf('/') + 1);

			ItemDelegate item = null;
			NodeManager wrap = new NodeManager(node, login);
			try {
				item = wrap.getItemDelegate();
				//				wrap.setProperties(item);
				children.add(item);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}
		return children;
	}



}