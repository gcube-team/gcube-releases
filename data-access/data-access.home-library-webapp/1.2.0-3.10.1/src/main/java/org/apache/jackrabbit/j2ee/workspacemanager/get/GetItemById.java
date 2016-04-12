package org.apache.jackrabbit.j2ee.workspacemanager.get;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
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

public class GetItemById extends HttpServlet {


	private static Logger logger = LoggerFactory.getLogger(GetItemById.class);
	private static final long serialVersionUID = 1L;

	public GetItemById() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		final String id = request.getParameter(ServletParameter.ID);
		final String login = request.getParameter(ServletParameter.LOGIN);
		
		logger.info("Servlet GetItemById called with parameters: [id: "+ id + "]");
		
		Repository rep = RepositoryAccessServlet
				.getRepository(getServletContext());
		SessionImpl session = null;

		XStream xstream = null;
		String xmlConfig = null;
		ItemDelegate item = null;
		try {
			xstream = new XStream(new DomDriver("UTF-8"));
			session = (SessionImpl) rep
					.login(new SimpleCredentials(request.getParameter(ConfigRepository.USER), request.getParameter(ConfigRepository.PASSWORD).toCharArray()));


			// Boolean b = Boolean.valueOf(all);
			try {
				item = getDelegateItemById(session, id, login);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			xmlConfig = xstream.toXML(item);
			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);

		} catch (RepositoryException e) {
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

	private ItemDelegate getDelegateItemById(SessionImpl session,
			String identifier, String login) throws Exception {

		Node node = session.getNodeByIdentifier(identifier);
		
//		logger.info(identifier + " -> "+ node.getPath());
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