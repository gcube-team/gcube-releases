package org.apache.jackrabbit.j2ee.workspacemanager.post;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;
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
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Receive a delegate item
 */

public class CreateReference extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(CreateReference.class);

	private static final long serialVersionUID = 1L;

	public CreateReference() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {


		response.setContentType("text/html; charset=UTF-8");

		PrintWriter out = response.getWriter();

		final String srcID = request.getParameter(ServletParameter.SRC_ID);
		final String destID = request.getParameter(ServletParameter.DEST_ID);
		final String login = request.getParameter(ServletParameter.LOGIN);

		logger.info("Servlet CreateReference called with parameters: [ srcID: "+ srcID + "; destID: " +destID + "; login: " +login+ "]");

		Repository rep = RepositoryAccessServlet
				.getRepository(getServletContext());
		SessionImpl session = null;

		XStream xstream = null;
		String xmlConfig = null;
		//		List<ItemDelegate> children = null;
		try {
			xstream = new XStream(new DomDriver("UTF-8"));
			session = (SessionImpl) rep
					.login(new SimpleCredentials(request.getParameter(ConfigRepository.USER), request.getParameter(ConfigRepository.PASSWORD).toCharArray()));



			ItemDelegate new_item = createReference(session, srcID, destID, login);

			xmlConfig = xstream.toXML(new_item);
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

	
	private ItemDelegate createReference(SessionImpl session, String srcID, String destID, String login) {
		ItemDelegate item = null;
		Node srcNode = null;
		Node destNode = null;
		try{
			srcNode = session.getNodeByIdentifier(srcID);
			srcNode.addMixin(JcrConstants.MIX_REFERENCEABLE);

			destNode = session.getNodeByIdentifier(destID);

			Node link = destNode.addNode(srcNode.getName(), srcNode.getPrimaryNodeType().getName());
			link.setProperty(NodeProperty.REFERENCE.toString(), srcNode);
			session.save();

			logger.info("References to " + srcNode.getPath() + ":");
			for (Property reference : JcrUtils.getReferences(srcNode)) {
				logger.info("- " + reference.getPath());
			}

			NodeManager wrap = new NodeManager(link, login);
			item = wrap.getItemDelegate();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}


}
