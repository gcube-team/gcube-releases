package org.apache.jackrabbit.j2ee.workspacemanager.servlets.post;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.AccessDeniedException;
import javax.jcr.Binary;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.SessionManager;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class RemoveData extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(RemoveData.class);
	private static final long serialVersionUID = 1L;

	public RemoveData() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);

	}
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());

		Session session = null;
		XStream xstream = null;
		String xmlConfig = null;
		SessionManager sessionManager = null;
		String sessionId = null;
		try {
			xstream = new XStream(new DomDriver("UTF-8"));

			sessionManager = SessionManager.getInstance(rep);

			session = sessionManager.newSession(request);
			sessionId = session.toString();

			NodeIterator homes = session.getRootNode().getNode("Home").getNodes();

			while(homes.hasNext()){

				Node userNode = homes.nextNode();
				String login = userNode.getName();

				if (!login.startsWith("rep:")){
					getChildren(login, userNode, session);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error repository ex " + e.getMessage());
			xmlConfig = xstream.toXML(e.toString());
			//			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);

		} finally {
			if (sessionId!=null){
				sessionManager.releaseSession(sessionId);
				//				logger.info("Released session " + sessionId);
			}
			out.close();
			//			out.flush();
		}
	}


	private static void getChildren(String login, Node userNode, Session session) throws RepositoryException {
		if (userNode.hasProperty("hl:portalLogin") && (!userNode.getName().equals("hl:owner"))){

			//			if (login.equals(userNode.getProperty("hl:portalLogin").getString())){
			//				System.out.println(userNode.getPath());

			if (userNode.hasNode("jcr:content")){

				Node content = userNode.getNode("jcr:content");

				//					if (!content.hasProperty("hl:remotePath")){

				if (content.hasProperty("jcr:data")){

					//							Binary bin = content.getProperty("jcr:data").getBinary();
					//							InputStream stream = bin.getStream();
					//							System.out.println(userNode.getPath());
					removeData(content, session);

				}
			}
			//				}
			//			}
		}

		NodeIterator iterator = userNode.getNodes();
		while(iterator.hasNext()){
			Node node = iterator.nextNode();
			getChildren(login, node, session);
		}

	}

	private static void removeData(Node nodeContent, Session session) throws AccessDeniedException, ItemExistsException, ReferentialIntegrityException, ConstraintViolationException, InvalidItemStateException, VersionException, LockException, NoSuchNodeTypeException, RepositoryException {
		try{
			ByteArrayInputStream  binaryUrl = new ByteArrayInputStream(nodeContent.getPath().getBytes());
			Binary binary = nodeContent.getSession().getValueFactory().createBinary(binaryUrl);
			nodeContent.setProperty(NodeProperty.DATA.toString(), binary);
		}catch (Exception e) {
			logger.error("error setting propery " + NodeProperty.DATA);
		}

		session.save();

	}

}
