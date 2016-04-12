package org.apache.jackrabbit.j2ee.workspacemanager.get;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.ConfigRepository;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.apache.jackrabbit.j2ee.workspacemanager.ServletParameter;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Delete Aces for a resource.
 */

public class GetChildrenById extends HttpServlet {
	private static Logger logger = LoggerFactory.getLogger(GetChildrenById.class);


	private static final long serialVersionUID = 1L;

	public GetChildrenById() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {


//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		final String id = request.getParameter(ServletParameter.ID);	
		final String login = request.getParameter(ServletParameter.LOGIN);	
		
		logger.info("Servlet GetChildrenById called with parameters: [id: "+ id + "]");

		Repository rep = RepositoryAccessServlet
				.getRepository(getServletContext());
		SessionImpl session = null;

		XStream xstream = null;
		String xmlConfig = null;
		List<ItemDelegate> children = null;
		try {
			xstream = new XStream(new DomDriver("UTF-8"));
			
			session = (SessionImpl) rep
					.login(new SimpleCredentials(request.getParameter(ConfigRepository.USER), request.getParameter(ConfigRepository.PASSWORD).toCharArray()));

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
			if (session != null)
				session.logout();

			out.close();
			out.flush();
			
		}
	}

	private List<ItemDelegate> getChildren(SessionImpl session, String identifier, String login) throws Exception {

		Node folderNode = session.getNodeByIdentifier(identifier);
		Boolean isHidden = folderNode.getProperty(NodeProperty.HIDDEN.toString()).getBoolean();
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
				if ((isHidden) || (name.equals("Trash") || (name.equals("MySpecialFolders") ||(name.startsWith("rep:")) || (name.startsWith("hl:")) || (name.startsWith(".")))))
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