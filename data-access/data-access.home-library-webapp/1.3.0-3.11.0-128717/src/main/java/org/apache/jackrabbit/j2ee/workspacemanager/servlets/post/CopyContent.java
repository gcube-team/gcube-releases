package org.apache.jackrabbit.j2ee.workspacemanager.servlets.post;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
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


			String srcAbsPath111 = null;
			String destAbsPath111 = null;
			try{
				srcAbsPath111 = new String(request.getParameter(ServletParameter.SRC_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
				destAbsPath111 = new String(request.getParameter(ServletParameter.DEST_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
				logger.info("Servlet CopyContent called with parameters: [srcAbsPath11: "+ srcAbsPath111 + " - destAbsPath11: " + destAbsPath111 +"]");
				ItemDelegate new_item = copyContetItem(session, srcAbsPath111, destAbsPath111, login);
				xmlConfig = xstream.toXML(new_item);
				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			} catch (Exception e) {
				logger.error("Error moving item from path: " + srcAbsPath111 +  " - to: " + destAbsPath111, e);
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
	 * Copy content item
	 * @param session
	 * @param srcId
	 * @param destId
	 * @param login
	 * @return
	 * @throws Exception
	 */
	private ItemDelegate copyContetItem(SessionImpl session, String srcId, String destId, String login) throws Exception{

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
