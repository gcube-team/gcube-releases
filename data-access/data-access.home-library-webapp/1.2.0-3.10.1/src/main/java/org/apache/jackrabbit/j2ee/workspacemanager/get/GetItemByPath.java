package org.apache.jackrabbit.j2ee.workspacemanager.get;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

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
import org.apache.jackrabbit.util.Text;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Delete Aces for a resource.
 */

public class GetItemByPath extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(GetItemByPath.class);


	private static final long serialVersionUID = 1L;

	public GetItemByPath() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

//		response.setContentType("text/plain");
	request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		final String path = new String(request.getParameter(ServletParameter.PATH).getBytes("iso-8859-1"), "UTF-8");
//		final String path = URLDecoder.decode(request.getParameter(ServletParameter.PATH), "UTF-8");
		final String login = request.getParameter(ServletParameter.LOGIN);

		logger.info("Servlet GetItemByPath called with parameters: [path: "+ path + "]");

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


			try {
				item = getDelegateItemByPath(session, path, login);
			} catch (Exception e) {
				logger.error("Error getting item by path " + path, e);
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

	private ItemDelegate getDelegateItemByPath(SessionImpl session, String absPath, String login) throws Exception {

		Node node = session.getNode(absPath);
		NodeManager wrap = new NodeManager(node, login);

		return wrap.getItemDelegate();

	}



}