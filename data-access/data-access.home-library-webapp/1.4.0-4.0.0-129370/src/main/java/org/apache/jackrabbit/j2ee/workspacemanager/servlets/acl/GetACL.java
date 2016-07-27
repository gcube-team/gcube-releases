package org.apache.jackrabbit.j2ee.workspacemanager.servlets.acl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.ConfigRepository;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.workspacemanager.ServletParameter;
import org.apache.jackrabbit.j2ee.workspacemanager.SessionManager;
import org.apache.jackrabbit.j2ee.workspacemanager.servlets.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GetACL extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(GetACL.class);
	private static final long serialVersionUID = 1L;


	public GetACL() {
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
		String absPath = new String(request.getParameter(ServletParameter.ABS_PATH).getBytes("iso-8859-1"), "UTF-8");

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

			try{
				Map<String, List<String>> map = getACL(absPath, session);
				xmlConfig = xstream.toXML(map);
				response.setContentLength(xmlConfig.length());
				out.println(xmlConfig);
			} catch (Exception e) {
				logger.error("Error Getting ALC of item: " + absPath , e);
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
			}
			out.close();
			out.flush();
		}
	}

	public Map<String, List<String>> getACL(String absPath, SessionImpl session) throws Exception {

		List<AccessControlEntry> allEntries = null;
		try {
			allEntries = new ArrayList<AccessControlEntry>(); 

			AccessControlManager accessControlManager = session.getAccessControlManager();
			AccessControlPolicy[] policies = accessControlManager.getPolicies(absPath);
			for (AccessControlPolicy accessControlPolicy : policies) {
				if (accessControlPolicy instanceof AccessControlList) {
					AccessControlEntry[] accessControlEntries = ((AccessControlList)accessControlPolicy).getAccessControlEntries();
					for (AccessControlEntry accessControlEntry : accessControlEntries) {
						allEntries.add(accessControlEntry);
					}
				}
			}
		} catch (RepositoryException e) {
			logger.error("Error getting ACL in AccessManager for node: " +  absPath, e);
			throw new Exception(e);
		}


		Map<String, List<String>> map = Utils.getMap(allEntries);


		return map;

	}

}
