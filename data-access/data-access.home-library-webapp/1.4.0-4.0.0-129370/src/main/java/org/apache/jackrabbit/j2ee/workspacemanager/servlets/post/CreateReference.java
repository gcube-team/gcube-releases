package org.apache.jackrabbit.j2ee.workspacemanager.servlets.post;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
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
	String login;
	String sessionId;
	
	public CreateReference() {
		super();
	}

	
	 // Method to handle Get method request.
	protected void doGet(HttpServletRequest request,
	                     HttpServletResponse response)
	      throws ServletException, IOException {
		  doPost(request, response);
	  }
	  
	  
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		login = request.getParameter(ServletParameter.LOGIN);
		sessionId = request.getParameter(ServletParameter.UUID);
		final String user = request.getParameter(ConfigRepository.USER);
		final char[] pass = request.getParameter(ConfigRepository.PASSWORD).toCharArray();

		String srcID = request.getParameter(ServletParameter.SRC_ID);
		String destID = request.getParameter(ServletParameter.DEST_ID);

		logger.info("Servlet CreateReference called with parameters: [srcID: "+ srcID + " - destID: " + destID +"]");

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
			if (exist)			
				session = sessionManager.getSession(sessionId);
			else{				 
				session = sessionManager.newSession(login, user, pass);
				sessionId = session.toString();
			}

			ItemDelegate new_item = createReference(session, srcID, destID);
			xmlConfig = xstream.toXML(new_item);
			out.println(xmlConfig);
		} catch (Exception e) {
			if (!exist)
				sessionManager.releaseSession(sessionId);
//			System.out.println("SERVLET EXCEPTION");
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());

		} finally {
			if (!exist)
				sessionManager.releaseSession(sessionId);
			out.close();
			out.flush();
		}
	}



	private ItemDelegate createReference(SessionImpl session, String srcID, String destID) throws RepositoryException {
			
		ItemDelegate item = null;
		Node srcNode = null;
		Node destNode = null;
		try{
			srcNode = session.getNodeByIdentifier(srcID);
			srcNode.addMixin(JcrConstants.MIX_REFERENCEABLE);

			destNode = session.getNodeByIdentifier(destID);

			Node link = destNode.addNode(srcID, PrimaryNodeType.NT_WORKSPACE_REFERENCE);
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
			throw new RepositoryException("Error creating an internal link of node " + srcNode.getPath() +" : " + e.getMessage());
		}
		return item;
	}


}
