package org.apache.jackrabbit.j2ee.workspacemanager.servlets.get;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
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
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.apache.jackrabbit.j2ee.workspacemanager.servlets.Utils;
import org.apache.jackrabbit.j2ee.workspacemanager.session.SessionManager;
import org.apache.jackrabbit.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ListFolder extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(ListFolder.class);
	private static final long serialVersionUID = 1L;

	public ListFolder() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//		response.setContentType("text/plain");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String sessionId = request.getParameter(ServletParameter.UUID);

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
			listFolder(request, response, out, xstream, session);

		} catch (Exception e) {
			logger.error("Error repository ex " + e);
			xmlConfig = xstream.toXML(e.toString());
//			response.setContentLength(xmlConfig.length());
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

	private void listFolder(HttpServletRequest request,
			HttpServletResponse response, PrintWriter out, XStream xstream,
			Session session) {
		String xmlConfig;
		String folderPath = null;
		Map<String, Boolean> children = null;

		try {   
			children = new HashMap<String, Boolean>();
			folderPath = request.getParameter(ServletParameter.ABS_PATH);
			
			logger.info("Servlet ListFolder called with parameters: [folderPath: "+ folderPath + "]");
			NodeIterator iterator = session.getNode(folderPath).getNodes();

			while(iterator.hasNext()) {

				Node node = iterator.nextNode();

				String path = null;
				try {
					path = node.getPath();
					String name = path.substring(path.lastIndexOf('/') + 1);
					if ((name.equals("Trash") || (name.equals("MySpecialFolders") ||(name.startsWith("rep:")) || (name.startsWith("hl:")) || (name.startsWith(".")))))
						continue;
				} catch (RepositoryException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					Boolean flag = node.getPrimaryNodeType().getName().equals(Utils.NT_WORKSPACE_FOLDER);
//					System.out.println("----> " + node.getName());
					children.put(node.getName(), flag);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			xmlConfig = xstream.toXML(children);
			//	System.out.println(xmlConfig);
//			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);


		} catch (Exception e) {
			logger.error("Error getting children for path: " + folderPath, e);
			xmlConfig = xstream.toXML(e.toString());
//			response.setContentLength(xmlConfig.length());
			out.println(xmlConfig);
		}


	}
}

