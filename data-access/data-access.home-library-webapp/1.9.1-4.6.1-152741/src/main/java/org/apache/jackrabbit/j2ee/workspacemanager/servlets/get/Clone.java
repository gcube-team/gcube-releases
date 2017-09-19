package org.apache.jackrabbit.j2ee.workspacemanager.servlets.get;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.jcr.Node;
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
import org.gcube.common.homelibary.model.servlet.ServletParameter;
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

		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String sessionId = request.getParameter(ServletParameter.UUID);
		String srcAbsPath = URLDecoder.decode(request.getParameter(ServletParameter.SRC_ABS_PATH), "UTF-8");
		String destAbsPath = URLDecoder.decode(request.getParameter(ServletParameter.DEST_ABS_PATH), "UTF-8");
//		String srcAbsPath = new String(request.getParameter(ServletParameter.SRC_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");	
//		String destAbsPath = new String(request.getParameter(ServletParameter.DEST_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
		String removeExisting = request.getParameter(ServletParameter.REMOVE_EXISTING);	

		logger.debug("Servlet Clone called with parameters: [srcAbsPath: "+ srcAbsPath + " - destAbsPath: " + destAbsPath + " - removeExisting: " + removeExisting +"]");

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
			
			ItemDelegate new_item = cloneItem(session, srcAbsPath, destAbsPath, Boolean.valueOf(removeExisting), sessionManager.getLogin(request));
			xmlConfig = xstream.toXML(new_item);
			out.println(xmlConfig);
		} catch (Exception e) {
			if (!exist)
				sessionManager.releaseSession(sessionId);
			throw new ServletException(e);
		} finally {
			if (!exist)
				sessionManager.releaseSession(sessionId);

			out.close();
			out.flush();
		}
	}



	private ItemDelegate cloneItem(Session session, String srcAbsPath, String destAbsPath, boolean removeExisting, String login) throws RepositoryException{
		ItemDelegate item = null;
		try{
					
			session.getWorkspace().clone(session.getWorkspace().getName(), srcAbsPath, destAbsPath, removeExisting);
			Node node = session.getNode(destAbsPath);			
			NodeManager wrap = new NodeManager(node, login);
			item = wrap.getItemDelegate();

		} catch (Exception e) {
			throw new RepositoryException("Error getting clone of node " + srcAbsPath +" : " + e.getMessage());
		}
		return item;
	}



}
