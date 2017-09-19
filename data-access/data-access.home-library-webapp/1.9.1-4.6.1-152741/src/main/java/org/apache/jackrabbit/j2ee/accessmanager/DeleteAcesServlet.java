package org.apache.jackrabbit.j2ee.accessmanager;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.Item;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.AccessControlPolicyIterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;


/**
 * Delete Aces for a resource.
 */

public class DeleteAcesServlet extends HttpServlet {

	public static final String ABS_PATH					= "absPath";
	public static final String APPLY_TO					= "applyTo";

	private Logger logger = LoggerFactory.getLogger(DeleteAcesServlet.class);

	private static final long serialVersionUID = 1L;

	public DeleteAcesServlet() {
		super();
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.debug("Servlet Delete Aces called ......");

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		Repository rep = RepositoryAccessServlet.getRepository(getServletContext());
		SessionImpl session = null;

		XStream xstream = null;
		String xmlConfig = null;
		boolean modified = false;
		String absPath = null;
		String[] applyTo =  null;

		String adminId = request.getSession()
				.getServletContext()
				.getInitParameter("user");
		char[] adminPass = request.getSession()
				.getServletContext()
				.getInitParameter("pass").toCharArray();
		try {

			session = (SessionImpl) rep
					.login(new SimpleCredentials(adminId, adminPass));
			xstream = new XStream();

			absPath = URLDecoder.decode(request.getParameter(ServletParameter.ABS_PATH), "UTF-8");
//			absPath = new String(request.getParameter(ABS_PATH).getBytes("iso-8859-1"), "UTF-8");
			applyTo = request.getParameterValues(APPLY_TO);

			try{
				//delete aces	
				modified = deleteAces(session, absPath, applyTo);
			} catch (RepositoryException e) {
				logger.error("ACL not found");				
			}

		} catch (RepositoryException e) {
			logger.error("Error Deleting ACL");

		} finally {
			xmlConfig = xstream.toXML(modified);
			response.setContentLength(xmlConfig.length()); 
			out.println(xmlConfig);		

			if(session != null)
				session.logout();

			out.close();
			out.flush();

			if (modified)
				logger.debug("removed privilege to node "+ absPath );
			else
				logger.debug("cannot remove privilege to node "+ absPath);
		}	
	}


	private boolean deleteAces(SessionImpl jcrSession,
			String resourcePath, String[] principalNamesToDelete) throws RepositoryException {

		if (principalNamesToDelete == null) {
			return false;
		} else {
			if (jcrSession == null) {
				return false;
			}

			if (resourcePath == null) {
				return false;
			}

			Item item = jcrSession.getItem(resourcePath);
			if (item != null) {
				resourcePath = item.getPath();
			} else {
				return false;
			}

			//load the principalIds array into a set for quick lookup below
			Set<String> pidSet = new HashSet<String>();
			pidSet.addAll(Arrays.asList(principalNamesToDelete));

			try {
				AccessControlManager accessControlManager = AccessControlUtil.getAccessControlManager(jcrSession);
				AccessControlList updatedAcl = getAccessControlList(accessControlManager, resourcePath, false);

				//keep track of the existing Aces for the target principal
				AccessControlEntry[] accessControlEntries = updatedAcl.getAccessControlEntries();
				List<AccessControlEntry> oldAces = new ArrayList<AccessControlEntry>();
				for (AccessControlEntry ace : accessControlEntries) {
					if (pidSet.contains(ace.getPrincipal().getName())) {
						oldAces.add(ace);
					}
				}

				//remove the old aces
				if (!oldAces.isEmpty()) {
					for (AccessControlEntry ace : oldAces) {
						updatedAcl.removeAccessControlEntry(ace);
					}
				}

				//apply the changed policy
				accessControlManager.setPolicy(resourcePath, updatedAcl);

				jcrSession.save();
			} catch (RepositoryException re) {
//				re.printStackTrace();
				return false;
				//				throw new RepositoryException("Failed to delete access control.", re);
			}
		}
		return true;
	}



	protected AccessControlList getAccessControlList(
			final AccessControlManager accessControlManager,
			final String resourcePath, final boolean mayCreate)
					throws RepositoryException {

		// check for an existing access control list to edit
		AccessControlPolicy[] policies = accessControlManager.getPolicies(resourcePath);
		for (AccessControlPolicy policy : policies) {
			if (policy instanceof AccessControlList) {
				return (AccessControlList) policy;
			}
		}

		// no existing access control list, try to create if allowed
		if (mayCreate) {
			AccessControlPolicyIterator applicablePolicies = accessControlManager.getApplicablePolicies(resourcePath);
			while (applicablePolicies.hasNext()) {
				AccessControlPolicy policy = applicablePolicies.nextAccessControlPolicy();
				if (policy instanceof AccessControlList) {
					return (AccessControlList) policy;
				}
			}
		}

		// neither an existing nor a create AccessControlList is available, fail
		throw new RepositoryException(
				"Unable to find or create an access control policy to update for "
						+ resourcePath);

	}



}