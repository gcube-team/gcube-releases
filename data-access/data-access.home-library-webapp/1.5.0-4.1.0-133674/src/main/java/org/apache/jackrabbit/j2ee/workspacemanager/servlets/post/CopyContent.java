package org.apache.jackrabbit.j2ee.workspacemanager.servlets.post;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.apache.jackrabbit.j2ee.workspacemanager.SessionManager;
import org.apache.jackrabbit.j2ee.workspacemanager.servlets.Utils;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class CopyContent extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(CopyContent.class);
	private static final long serialVersionUID = 1L;


	public CopyContent() {
		super();
	}


	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String sessionId = request.getParameter(ServletParameter.UUID);

		String srcAbsPath = new String(request.getParameter(ServletParameter.SRC_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
		String destAbsPath = new String(request.getParameter(ServletParameter.DEST_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
		logger.info("Servlet CopyContent called with parameters: [srcAbsPath11: "+ srcAbsPath + " - destAbsPath11: " + destAbsPath +"]");

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
			if (exist)			
				session = sessionManager.getSession(sessionId);
			else{				 
				session = sessionManager.newSession(request);
				sessionId = session.toString();
			}

			ItemDelegate new_item = copyContetItem(session, srcAbsPath, destAbsPath, sessionManager.getLogin(request));
			xmlConfig = xstream.toXML(new_item);
			out.println(xmlConfig);
		} catch (Exception e) {
			if (!exist)
				sessionManager.releaseSession(sessionId);
			throw new ServletException(e);

		} finally {
			if (!exist)
				sessionManager.releaseSession(sessionId);
			out.close();
			out.flush();
		}
	}


	private ItemDelegate copyContetItem(Session session, String srcId, String destId, String login) throws Exception{

		Node nodeFolder = session.getNodeByIdentifier(destId);
		Node sharedNode = session.getNodeByIdentifier(srcId);
		ItemDelegate item = null;
		for (NodeIterator iterator = sharedNode.getNodes(); iterator.hasNext();) {
			Node child = (Node) iterator.next();

			if (!child.getName().startsWith(Utils.HL_NAMESPACE) 
					&& !child.getName().startsWith(Utils.JCR_NAMESPACE)
					&& !child.getName().startsWith(Utils.REP_NAMESPACE)) {			
				session.getWorkspace().copy(child.getPath(), nodeFolder.getPath() 
						+ "/" + child.getName());
			}
		}

		NodeManager wrap = new NodeManager(nodeFolder, login);
		item = wrap.getItemDelegate();

		return item;
	}


}
