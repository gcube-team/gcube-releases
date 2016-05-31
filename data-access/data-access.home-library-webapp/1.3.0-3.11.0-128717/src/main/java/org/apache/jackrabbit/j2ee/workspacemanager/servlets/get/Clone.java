package org.apache.jackrabbit.j2ee.workspacemanager.servlets.get;

import java.io.IOException;
import java.io.PrintWriter;
import javax.jcr.Node;
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
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class Clone extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(Clone.class);
	private static final long serialVersionUID = 1L;


	public Clone() {
		super();
	}

	protected void doGet(HttpServletRequest request,
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
			} else {
				session = sessionManager.newSession(login, user, pass);
				sessionId = session.toString();
			}

			String srcAbsPath = null;
			String destAbsPath = null;
			String removeExisting = null;
			try{
				srcAbsPath = new String(request.getParameter(ServletParameter.SRC_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");	
				destAbsPath = new String(request.getParameter(ServletParameter.DEST_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
				removeExisting = request.getParameter(ServletParameter.REMOVE_EXISTING);	
				logger.info("Servlet Clone called with parameters: [srcAbsPath: "+ srcAbsPath + " - destAbsPath: " + destAbsPath + " - removeExisting: " + removeExisting +"]");
				ItemDelegate new_item = cloneItem(session, srcAbsPath, destAbsPath, Boolean.valueOf(removeExisting), login);
				xmlConfig = xstream.toXML(new_item);
				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			} catch (Exception e) {
				logger.error("Error cloning item from path: " + srcAbsPath +  " - to: " + destAbsPath + " - removeExisting? "+ removeExisting, e);
				xmlConfig = xstream.toXML(e.toString());
				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			}


		} catch (Exception e) {
			logger.error("Error repository ex " + e);
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
	 * Clone item
	 * @param session
	 * @param srcAbsPath
	 * @param destAbsPath
	 * @param removeExisting
	 * @param login
	 * @return
	 */
	private ItemDelegate cloneItem(SessionImpl session, String srcAbsPath, String destAbsPath, boolean removeExisting, String login){
		Node node;
		ItemDelegate item = null;
		try{
			session.getWorkspace().clone(session.getWorkspace().getName(), srcAbsPath, destAbsPath, removeExisting);
			node = session.getNode(destAbsPath);
			NodeManager wrap = new NodeManager(node, login);
			item = wrap.getItemDelegate();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}



}
