package org.gcube.portlets.user.gcubewidgets.server;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.portal.PortalContext;
import org.gcube.portlets.user.gcubewidgets.client.rpc.ScopeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Organization;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.OrganizationLocalServiceUtil;

@SuppressWarnings("serial")
public class ScopeServiceImpl extends RemoteServiceServlet implements ScopeService {
	private static final Logger _log = LoggerFactory.getLogger(ScopeServiceImpl.class);



	@Override
	public boolean setScope(String portalURL) {
		String friendlyURL = extractOrgFriendlyURL(portalURL);
		if (friendlyURL == null) //the URL is not a portal URL, we are in devmode.
			return true;
		else {
			try {
				List<Group> groups = GroupLocalServiceUtil.getGroups(0, GroupLocalServiceUtil.getGroupsCount());
				for (Group g : groups) {
					if (g.isOrganization() || g.isCommunity()) 
						if (g.getFriendlyURL().compareTo(friendlyURL) == 0) {
							setScope(g);
							return true;
						}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private void setScope(Group currentGroup) throws Exception {
		String scopeToSet = buildScope(currentGroup);
		getASLSession().setScope(scopeToSet);	
	}
	
	public static String buildScope(Group currentGroup) throws Exception {
		String scopeToSet = "";
		Organization curOrg = null;
		// the group MUST BE an Organization
		if (currentGroup.isOrganization()) {
			long organizationId = currentGroup.getClassPK();
			curOrg = OrganizationLocalServiceUtil.getOrganization(organizationId);

			if (curOrg.isRoot()) {
				scopeToSet = "/"+curOrg.getName();
			} else if (isVO(curOrg)) {
				scopeToSet = "/"+curOrg.getParentOrganization().getName()+"/"+curOrg.getName();
			} else { //is a VRE
				Organization vo = curOrg.getParentOrganization();
				scopeToSet = "/"+vo.getParentOrganization().getName()+"/"+vo.getName()+"/"+curOrg.getName();
			}
		} else { //
			scopeToSet = "PORTAL";
			_log.info("Not an organization, scopeToSet set to PORTAL");				
		}

		if (curOrg == null) {		
			String rootVO = getRootConfigFromGCore();
			_log.info("CONTEXT INITIALIZED CORRECTLY From Client, setting rootvo as scope: " + rootVO);
			scopeToSet = "/"+rootVO;			
		}
		return scopeToSet;
	}

	public static boolean isVO(Organization currentOrg) throws PortalException, SystemException {		
		return (currentOrg.getParentOrganization().getParentOrganization() == null); 
	}	

	public static String getRootConfigFromGCore() {
		return PortalContext.getConfiguration().getInfrastructureName();
	}

	public static String extractOrgFriendlyURL(String portalURL) {
		String groupRegEx = "/group/";
		if (portalURL.contains(groupRegEx)) {
			_log.debug("LIFERAY PORTAL DETECTED");
			String[] splits = portalURL.split(groupRegEx);
			String friendlyURL = splits[1];
			if (friendlyURL.contains("/")) {
				friendlyURL = friendlyURL.split("/")[0];
			} else {
				friendlyURL = friendlyURL.split("\\?")[0].split("\\#")[0];
			}
			_log.trace("extracted friendly url: /" + friendlyURL);
			return "/"+friendlyURL;
		}
		return null;
	}



	private ASLSession getASLSession() {		
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		String sessionID = httpSession.getId();
		String user = (String) httpSession.getAttribute("username");
		if (user == null) {
			_log.warn("NO USER FOUND, exiting");
			return null;
		}
		else {
			_log.info("Found user=" + user);			
		}
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
}
