package org.apache.jackrabbit.j2ee.workspacemanager.servlets.get;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
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

public class Copy extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(Copy.class);
	private static final long serialVersionUID = 1L;

	public Copy() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String sessionId = request.getParameter(ServletParameter.UUID);

		String srcAbsPath = request.getParameter(ServletParameter.SRC_ABS_PATH);
		String destAbsPath = request.getParameter(ServletParameter.DEST_ABS_PATH);
		
//		String srcAbsPath = new String(request.getParameter(ServletParameter.SRC_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
//		String destAbsPath = new String(request.getParameter(ServletParameter.DEST_ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
		Boolean subgraph = false;

		logger.info("Servlet Copy called with parameters: [srcAbsPath: "+ srcAbsPath + " - destAbsPath: " + destAbsPath + " - remove subgraph? : " + subgraph +"]");


		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		Session session = null;
		XStream xstream = null;
		String xmlConfig = null;
		SessionManager sessionManager = null;
		boolean exist = false;
		try {
			subgraph = Boolean.parseBoolean(request.getParameter(ServletParameter.SUBGRAPH));

			xstream = new XStream(new DomDriver("UTF-8"));

			sessionManager = SessionManager.getInstance(rep);
			exist = sessionManager.sessionExists(sessionId); 
			if (exist){				
				session = sessionManager.getSession(sessionId);
			} else {
				session = sessionManager.newSession(request);
				sessionId = session.toString();
			}
			ItemDelegate new_item = null;
			if (subgraph)
				new_item = copyItemNoSubgraph(session, srcAbsPath, destAbsPath, sessionManager.getLogin(request));
			else				
				new_item = copyItem(session, srcAbsPath, destAbsPath, sessionManager.getLogin(request));

			xmlConfig = xstream.toXML(new_item);
			out.println(xmlConfig);

		} catch (Exception e) {
			if (!exist)
				sessionManager.releaseSession(sessionId);
			throw new ServletException(e);

		} finally {
			if (!exist){
				sessionManager.releaseSession(sessionId);
			}
			out.close();
			out.flush();
		}
	}


	private ItemDelegate copyItemNoSubgraph(Session session, String srcAbsPath, String destAbsPath, String login) throws RepositoryException{
		ItemDelegate item = null;
		try{

			session.getWorkspace().copy(srcAbsPath, destAbsPath);
			Node node = session.getNode(destAbsPath);
			NodeIterator children = node.getNodes();
			while(children.hasNext()){
				Node child = children.nextNode();
				if (child.getName().startsWith("hl:"))
					continue;
				try{
					child.remove();
					session.save();
				} catch (Exception e) {
					logger.error("Error deleting node " + child.getPath());
				}
			}
			NodeManager wrap = new NodeManager(node, login);
			item = wrap.getItemDelegate();

		} catch (Exception e) {
			throw new RepositoryException("Error getting copy of node " + srcAbsPath +" : " + e.getMessage());
		}
		return item;
	}

	private ItemDelegate copyItem(Session session, String srcAbsPath, String destAbsPath, String login) throws RepositoryException{
		ItemDelegate item = null;
		try{
			session.getWorkspace().copy(srcAbsPath, destAbsPath);
			Node node = session.getNode(destAbsPath);
			//			System.out.println("DESTINATION NODE IF EXISTS " + node.getPath());
			NodeManager wrap = new NodeManager(node, login);
			item = wrap.getItemDelegate();

		} catch (Exception e) {
			e.printStackTrace();
			throw new RepositoryException("Error getting copy of node " + srcAbsPath +" : " + e.getMessage());
		}
		return item;
	}





}
