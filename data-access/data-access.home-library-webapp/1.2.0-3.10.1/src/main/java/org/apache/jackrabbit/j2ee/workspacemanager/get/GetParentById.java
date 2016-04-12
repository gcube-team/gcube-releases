package org.apache.jackrabbit.j2ee.workspacemanager.get;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Node;
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
 * Delete Aces for a resource.
 */

public class GetParentById extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(GetParentById.class);
	
	public static final String NT_WORKSPACE_TRASH				= "nthl:trashFolder";
	public static final String NT_WORKSPACE_FOLDER 				= "nthl:workspaceItem";
	public static final String NT_WORKSPACE_SHARED_FOLDER		= "nthl:workspaceSharedItem";
	public static final String NT_WORKSPACE_FOLDER_ITEM			= "nthl:workspaceLeafItem";
	public static final String NT_WORKSPACE_FILE 				= "nthl:externalFile";
	public static final String NT_WORKSPACE_IMAGE 				= "nthl:externalImage";
	public static final String NT_WORKSPACE_PDF_FILE 			= "nthl:externalPdf";
	public static final String NT_WORKSPACE_URL 				= "nthl:externalUrl";
	public static final String NT_GCUBE_ITEM 					= "nthl:gCubeItem";
	public static final String NT_TRASH_ITEM 					= "nthl:trashItem";
	
	private static final long serialVersionUID = 1L;

	public GetParentById() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");
//		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		final String id = request.getParameter(ServletParameter.ID);	
		final String login = request.getParameter(ServletParameter.LOGIN);
		
		logger.info("Servlet GetParentById called with parameters: [id: "+ id + "]");

		Repository rep = RepositoryAccessServlet
				.getRepository(getServletContext());
		SessionImpl session = null;

		XStream xstream = null;
		String xmlConfig = null;
		ItemDelegate parent = null;
		try {
			xstream = new XStream(new DomDriver("UTF-8"));
			session = (SessionImpl) rep
					.login(new SimpleCredentials(request.getParameter(ConfigRepository.USER), request.getParameter(ConfigRepository.PASSWORD).toCharArray()));


			// Boolean b = Boolean.valueOf(all);


			parent = getParent(session, id, login);

			xmlConfig = xstream.toXML(parent);
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
	
	
	private ItemDelegate getParent(SessionImpl session,
			String identifier, String login) throws Exception {

		Node node = session.getNodeByIdentifier(identifier).getParent();
		NodeManager wrap = new NodeManager(node, login);
		ItemDelegate item = null;
		try {
			item = wrap.getItemDelegate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}



}