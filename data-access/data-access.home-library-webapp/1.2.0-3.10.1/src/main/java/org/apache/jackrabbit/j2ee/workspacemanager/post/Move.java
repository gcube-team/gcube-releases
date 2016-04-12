package org.apache.jackrabbit.j2ee.workspacemanager.post;

import java.io.IOException;
import java.io.PrintWriter;

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
import org.gcube.common.homelibrary.model.exceptions.InternalErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Receive a delegate item
 */

public class Move extends HttpServlet {
	private static Logger logger = LoggerFactory.getLogger(Move.class);
	private static final long serialVersionUID = 1L;


	public Move() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		final String srcAbsPath = new String(request.getParameter(ServletParameter.SRC_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
		final String destAbsPath = new String(request.getParameter(ServletParameter.DEST_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
		final String login = request.getParameter(ServletParameter.LOGIN);
		
		logger.info("Servlet Move called with parameters: [srcAbsPath: "+ srcAbsPath + "; destAbsPath: " +destAbsPath + "]");
		
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


			// Boolean b = Boolean.valueOf(all);

			ItemDelegate new_item = moveItem(session, srcAbsPath, destAbsPath, login);

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


		/**
		 * Moves the node at srcAbsPath (and its entire subtree) to the new location at destAbsPath. 
		 * @param session is coming form the servlet MOVE
		 * @param srcAbsPath is an absolute path to the original location 
		 * @param destAbsPath is an absolute path to the parent node of the new location, appended with the new name desired for the moved node
		 * @return the item moved
		 * @throws RepositoryException
		 * @throws InternalErrorException 
		 */
		private ItemDelegate moveItem(SessionImpl session, String srcAbsPath, String destAbsPath, String login) throws RepositoryException, InternalErrorException {
			session.move(srcAbsPath, destAbsPath);
			session.save();

			NodeManager myNode = null;
			try {
				myNode = new NodeManager(session.getNode(destAbsPath), login);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return myNode.getItemDelegate();
			
		}
	
}
