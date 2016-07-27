package org.apache.jackrabbit.j2ee.workspacemanager.servlets.acl;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.Privilege;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.ConfigRepository;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.apache.jackrabbit.j2ee.accessmanager.AccessControlUtil;
import org.apache.jackrabbit.j2ee.workspacemanager.ServletParameter;
import org.apache.jackrabbit.j2ee.workspacemanager.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GetDeniedMap extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(GetDeniedMap.class);
	private static final long serialVersionUID = 1L;


	public GetDeniedMap() {
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
				Map<String, List<String>> map = getDeniedMap(absPath, session);
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



	public Map<String, List<String>> getDeniedMap(String absPath, SessionImpl session) throws RepositoryException {

		Map<String, List<String>> map = null;

		try{
			map = new HashMap<String, List<String>>();
			Map<Principal, AccessRights> accessMap = new LinkedHashMap<Principal, AccessRights>();
			AccessControlEntry[] entries = getDeclaredAccessControlEntries(session, absPath);

			if (entries != null) {
				for (AccessControlEntry ace : entries) {
					List<String> privilegesList = null;
					Principal principal = ace.getPrincipal();
					//				System.out.println("Principal " + principal.getName());
					AccessRights accessPrivileges = accessMap.get(principal);
					if (accessPrivileges == null) {
						accessPrivileges = new AccessRights();
						accessMap.put(principal, accessPrivileges);
					}

					accessPrivileges.getDenied().addAll(Arrays.asList(ace.getPrivileges()));
					Set<Privilege> deniedPrivileges = accessPrivileges.getDenied();
					for(Privilege priv: deniedPrivileges){
						//					System.out.println("Denied--> " + priv.getName());
						if (privilegesList==null)
							privilegesList = new ArrayList<String>();
						privilegesList.add(priv.getName());
					}
					map.put(principal.getName(), privilegesList);	
				}
			}
		} catch (Exception e) {
			logger.error("Impossible to get Denied map");

		}

		return map;
	}


	//TO REMOVE FROM HERE
	/**
	 * Wrapper class that holds the set of Privileges that are granted 
	 * and/or denied for a specific principal.
	 */
	public static class AccessRights {
		private Set<Privilege> granted = new HashSet<Privilege>();
		private Set<Privilege> denied = new HashSet<Privilege>();

		private transient static ResourceBundle resBundle = null; 
		private ResourceBundle getResourceBundle(Locale locale) {
			if (resBundle == null || !resBundle.getLocale().equals(locale)) {
				resBundle = ResourceBundle.getBundle(getClass().getPackage().getName() + ".PrivilegesResources", locale);
			}
			return resBundle;
		}


		public Set<Privilege> getGranted() {
			return granted;
		}
		public Set<Privilege> getDenied() {
			return denied;
		}

		public String getPrivilegeSetDisplayName(Locale locale) {
			if (denied != null && !denied.isEmpty()) {
				//if there are any denied privileges, then this is a custom privilege set
				return getResourceBundle(locale).getString("privilegeset.custom");
			} else {
				if (granted.isEmpty()) {
					//appears to have an empty privilege set
					return getResourceBundle(locale).getString("privilegeset.none");
				}

				if (granted.size() == 1) {
					//check if the single privilege is jcr:all or jcr:read
					Iterator<Privilege> iterator = granted.iterator();
					Privilege next = iterator.next();
					if ("jcr:all".equals(next.getName())) {
						//full control privilege set
						return getResourceBundle(locale).getString("privilegeset.all");
					} else if ("jcr:read".equals(next.getName())) {
						//readonly privilege set
						return getResourceBundle(locale).getString("privilegeset.readonly");
					} 
				} else if (granted.size() == 2) {
					//check if the two privileges are jcr:read and jcr:write
					Iterator<Privilege> iterator = granted.iterator();
					Privilege next = iterator.next();
					Privilege next2 = iterator.next();
					if ( ("jcr:read".equals(next.getName()) && "jcr:write".equals(next2.getName())) ||
							("jcr:read".equals(next2.getName()) && "jcr:write".equals(next.getName())) ) {
						//read/write privileges
						return getResourceBundle(locale).getString("privilegeset.readwrite");
					}
				}

				//some other set of privileges
				return getResourceBundle(locale).getString("privilegeset.custom");
			}
		}
	}

	//TO REMOVE FROM HERE
	private AccessControlEntry[] getDeclaredAccessControlEntries(SessionImpl session, String absPath) throws RepositoryException {
		AccessControlManager accessControlManager = AccessControlUtil.getAccessControlManager(session);
		AccessControlPolicy[] policies = accessControlManager.getPolicies(absPath);
		for (AccessControlPolicy accessControlPolicy : policies) {
			if (accessControlPolicy instanceof AccessControlList) {
				AccessControlEntry[] accessControlEntries = ((AccessControlList)accessControlPolicy).getAccessControlEntries();
				return accessControlEntries;
			}
		}
		return new AccessControlEntry[0];
	}
}
