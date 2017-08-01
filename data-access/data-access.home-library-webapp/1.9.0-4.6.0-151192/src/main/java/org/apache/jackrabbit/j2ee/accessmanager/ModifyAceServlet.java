package org.apache.jackrabbit.j2ee.accessmanager;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jcr.Item;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class ModifyAceServlet extends HttpServlet {

	public static final String PRINCIPAL_ID					= "principalId";
	public static final String RESOURCE_PATH				= "resourcePath";
	public static final String ORDER						= "order";
	public static final String PRIVILEGE					= "privilege@";

	private Logger logger = LoggerFactory.getLogger(ModifyAceServlet.class);
	private static final long serialVersionUID = 1L;

	public ModifyAceServlet() {
		super();
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.debug("Servlet Modify Ace called ......");
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		SessionImpl session = null;
		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());

		boolean modified = false;
		XStream xstream = new XStream();
		String xmlConfig = null;

		String adminId = request.getSession()
				.getServletContext()
				.getInitParameter("user");
		char[] adminPass = request.getSession()
				.getServletContext()
				.getInitParameter("pass").toCharArray();
		try {

			session = (SessionImpl) rep
					.login(new SimpleCredentials(adminId, adminPass));

			String resourcePath = new String(request.getParameter(RESOURCE_PATH).getBytes("iso-8859-1"), "UTF-8");
			String principalId = request.getParameter(PRINCIPAL_ID);
			String order = request.getParameter(ORDER);
			logger.debug("Set ACL to node " + resourcePath + " for user " + principalId + " order " + order);

			Map<String, String> privileges = new HashMap<String, String>();

			Enumeration<?> parameterNames = request.getParameterNames();
			while (parameterNames.hasMoreElements()) {
				Object nextElement = parameterNames.nextElement();
				if (nextElement instanceof String) {
					String paramName = (String)nextElement;
					if (paramName.startsWith(PRIVILEGE)) {
						String privilegeName = paramName.substring(10);
						String parameterValue = request.getParameter(paramName);
						privileges.put(privilegeName, parameterValue);
						logger.debug("privilege@ " + privilegeName +" - parameterValue: " + parameterValue + " - principal: "+ principalId);						
					}
				}
			}

			try{
				modified = modifyAce(session, resourcePath, principalId, privileges, order);
			} catch (RepositoryException e) {
				logger.error("ACL not found");				
			}


			xmlConfig = xstream.toXML(modified);
			response.setContentLength(xmlConfig.length()); 
			out.println(xmlConfig);


		} catch (RepositoryException e) {

			modified = false;


			//			xmlConfig = xstream.toXML(modified);

			xmlConfig = xstream.toXML(e);

			response.setContentLength(xmlConfig.length()); 
			out.println(xmlConfig);		
			out.println(xmlConfig);		

		} finally {
			if(session != null)
				session.logout();

			out.close();
			out.flush();
		}	

	}


	public boolean modifyAce(SessionImpl jcrSession, String resourcePath,
			String principalId, Map<String, String> privileges, String order)
					throws RepositoryException {
		if (jcrSession == null) {
			//			throw new RepositoryException("JCR Session not found");
			return false;
		}

		if (principalId == null) {
			//			throw new RepositoryException("principalId was not submitted.");
			return false;
		}

		PrincipalManager principalManager = AccessControlUtil.getPrincipalManager(jcrSession);

		Principal principal = principalManager.getPrincipal(principalId);

		if (resourcePath == null) {
			return false;
		}

		Item item = jcrSession.getItem(resourcePath);
		if (item != null) {
			resourcePath = item.getPath();
		} else {
			return false;
		}

		// Collect the modified privileges from the request.
		Set<String> grantedPrivilegeNames = new HashSet<String>();
		Set<String> deniedPrivilegeNames = new HashSet<String>();
		Set<String> removedPrivilegeNames = new HashSet<String>();

		Set<Entry<String, String>> entrySet = privileges.entrySet();

		for (Entry<String, String> entry : entrySet) {
			String privilegeName = entry.getKey();
			if (privilegeName.startsWith("privilege@")) {
				privilegeName = privilegeName.substring(10);
			}

//			logger.debug("privileges: ");
			String parameterValue = entry.getValue();
			if (parameterValue != null && parameterValue.length() > 0) {
				if ("granted".equals(parameterValue)) {
//					logger.debug("-granted: " + privilegeName);
					grantedPrivilegeNames.add(privilegeName);
				} else if ("denied".equals(parameterValue)) {
//					logger.debug("-denied: " + privilegeName);
					deniedPrivilegeNames.add(privilegeName);
				} else if ("none".equals(parameterValue)){
//					logger.debug("-none: " + privilegeName);
					removedPrivilegeNames.add(privilegeName);
				}
			}
		}

		// Make the actual changes.
		try {
			logger.debug("Make the actual changes");

			AccessControlUtil.replaceAccessControlEntry(jcrSession, resourcePath, principal,
					grantedPrivilegeNames.toArray(new String[grantedPrivilegeNames.size()]),
					deniedPrivilegeNames.toArray(new String[deniedPrivilegeNames.size()]),
					removedPrivilegeNames.toArray(new String[removedPrivilegeNames.size()]),
					order);

			if (jcrSession.hasPendingChanges()) {
				jcrSession.save();
			}
		} catch (RepositoryException re) {
			//			return false;			
			throw new RepositoryException("Failed to create ace.", re);
		}

		return true;
	}



}