package org.apache.jackrabbit.j2ee.workspacemanager.servlets.get;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.NodeManager;
import org.apache.jackrabbit.j2ee.workspacemanager.session.SessionManager;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class Move extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(Move.class);
	private static final long serialVersionUID = 1L;


	public Move() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String sessionId = request.getParameter(ServletParameter.UUID);
		logger.info("Move servlet called with session id parameter " + sessionId);

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
			if (exist){				
				session = sessionManager.getSession(sessionId);
			} else {
				session = sessionManager.newSession(request);
				sessionId = session.toString();
			}

			String srcAbsPath = null;
			String destAbsPath = null;
			try{
				srcAbsPath = request.getParameter(ServletParameter.SRC_ABS_PATH);
				destAbsPath = request.getParameter(ServletParameter.DEST_ABS_PATH);
				//				srcAbsPath = new String(request.getParameter(ServletParameter.SRC_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
				//				destAbsPath = new String(request.getParameter(ServletParameter.DEST_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
				logger.info("Servlet Move called with parameters: [srcAbsPath: "+ srcAbsPath + " - destAbsPath: " + destAbsPath +"]");
				ItemDelegate new_item = moveItem(session, srcAbsPath, destAbsPath, sessionManager.getLogin(request));
				xmlConfig = xstream.toXML(new_item);
				//				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			} catch (Exception e) {
				logger.error("Error moving item from path: " + srcAbsPath +  " - to: " + destAbsPath, e);
				xmlConfig = xstream.toXML(e.toString());
				//	response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			}

		} catch (Exception e) {
			logger.error("Error repository ex " + e);
			xmlConfig = xstream.toXML(e.toString());
			//	response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);

		} finally {
			if (!exist){
				sessionManager.releaseSession(sessionId);
				//				logger.info("Released session " + sessionId);
			}
			out.close();
			//			out.flush();
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
	private ItemDelegate moveItem(Session session, String srcAbsPath, String destAbsPath, String login) throws RepositoryException {

		String dest = destAbsPath.trim();
		session.move(srcAbsPath, dest);
		session.save();

		NodeManager myNode = null;
		try {
			myNode = new NodeManager(session.getNode(dest), login);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return myNode.getItemDelegate();
	}



}
