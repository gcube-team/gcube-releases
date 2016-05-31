package org.apache.jackrabbit.j2ee.workspacemanager.servlets.post;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.ConfigRepository;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.apache.jackrabbit.j2ee.workspacemanager.ServletParameter;
import org.apache.jackrabbit.j2ee.workspacemanager.SessionManager;
import org.apache.jackrabbit.j2ee.workspacemanager.servlets.Utils;
import org.apache.jackrabbit.j2ee.workspacemanager.storage.GCUBEStorage;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;
import org.gcube.common.homelibary.model.util.WorkspaceItemAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class CreateReference extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(CreateReference.class);
	private static final long serialVersionUID = 1L;


	public CreateReference() {
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


			String srcID = null;
			String destID = null;
			String name = null;
			try{
				srcID = request.getParameter(ServletParameter.SRC_ID);
				destID = request.getParameter(ServletParameter.DEST_ID);
				name = request.getParameter(ServletParameter.NAME);
				logger.info("Servlet CreateReference called with parameters: [srcID: "+ srcID + " - destID: " + destID +"]");
				ItemDelegate new_item = createReference(session, srcID, destID, name, login);
				xmlConfig = xstream.toXML(new_item);
				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			} catch (Exception e) {
				logger.error("Error creating reference of node id: " + srcID +  " - to destination id: " + destID, e);
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
	 * Create Reference
	 * @param session
	 * @param srcID
	 * @param destID
	 * @param login
	 * @return
	 */
	private ItemDelegate createReference(SessionImpl session, String srcID, String destID, String name, String login) {
		ItemDelegate item = null;
		Node srcNode = null;
		Node destNode = null;
		try{
			srcNode = session.getNodeByIdentifier(srcID);
			srcNode.addMixin(JcrConstants.MIX_REFERENCEABLE);

			destNode = session.getNodeByIdentifier(destID);

			Node link = destNode.addNode(name, PrimaryNodeType.NT_WORKSPACE_REFERENCE);
			link.setProperty(NodeProperty.REFERENCE.toString(), srcNode);
			link.setProperty(NodeProperty.PORTAL_LOGIN.toString(), login);
			link.setProperty(NodeProperty.TITLE.toString(), srcNode.getName());
			link.setProperty(NodeProperty.LAST_ACTION.toString(), WorkspaceItemAction.CREATED.toString());
			session.save();	

			logger.info("References to " + srcNode.getPath() + ":");
			for (Property reference : JcrUtils.getReferences(srcNode)) {
				logger.info("- " + reference.getPath().replace(NodeProperty.REFERENCE.toString(), ""));
			}

			NodeManager wrap = new NodeManager(link, login);
			item = wrap.getItemDelegate();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}


}
