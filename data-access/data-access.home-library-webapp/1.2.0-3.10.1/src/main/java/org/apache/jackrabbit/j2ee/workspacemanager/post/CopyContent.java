package org.apache.jackrabbit.j2ee.workspacemanager.post;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.ConfigRepository;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.apache.jackrabbit.j2ee.workspacemanager.ServletParameter;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Receive a delegate item
 */

public class CopyContent extends HttpServlet {
	
	private static Logger logger = LoggerFactory.getLogger(CopyContent.class);
	
	public static final String HL_NAMESPACE					= "hl:";
	public static final String JCR_NAMESPACE				= "jcr:";
	public static final String REP_NAMESPACE				= "rep:";
	

	
	private static final long serialVersionUID = 1L;

	public CopyContent() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {


		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		final String srcAbsPath = request.getParameter(ServletParameter.SRC_ID);	
		final String destAbsPath = request.getParameter(ServletParameter.DEST_ID);	
		final String login = request.getParameter(ServletParameter.LOGIN);
		
		logger.info("Servlet CopyContent called with parameters: [srcAbsPath: "+ srcAbsPath + "; destAbsPath: " +destAbsPath + "]");

		
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



			ItemDelegate new_item = copyItem(session, srcAbsPath, destAbsPath, login);

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

	private ItemDelegate copyItem(SessionImpl session, String srcId, String destId, String login) throws Exception{
		
		Node nodeFolder = session.getNodeByIdentifier(destId);
		Node sharedNode = session.getNodeByIdentifier(srcId);
		ItemDelegate item = null;
		for (NodeIterator iterator = sharedNode.getNodes(); iterator.hasNext();) {
			Node child = (Node) iterator.next();

			if (!child.getName().startsWith(HL_NAMESPACE) 
					&& !child.getName().startsWith(JCR_NAMESPACE)
					&& !child.getName().startsWith(REP_NAMESPACE)) {			
				session.getWorkspace().copy(child.getPath(), nodeFolder.getPath() 
						+ "/" + child.getName());
			}
		}
		
		NodeManager wrap = new NodeManager(nodeFolder, login);
		item = wrap.getItemDelegate();
		
		return item;
	}

}
