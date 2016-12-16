package org.gcube.portlets.admin.invitessent.server;

import java.util.ArrayList;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portal.databook.server.DBCassandraAstyanaxImpl;
import org.gcube.portal.databook.server.DatabookStore;
import org.gcube.portal.databook.shared.Invite;
import org.gcube.portal.databook.shared.InviteStatus;
import org.gcube.portal.databook.shared.ex.InviteIDNotFoundException;
import org.gcube.portal.databook.shared.ex.InviteStatusNotFoundException;
import org.gcube.portlets.admin.invitessent.client.InvitesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
*
* @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
*/
@SuppressWarnings("serial")
public class InvitesServiceImpl extends RemoteServiceServlet implements InvitesService {
	
	private static final Logger _log = LoggerFactory.getLogger(InvitesServiceImpl.class);
	
	public static final String TEST_USER = "test.user";
	public static final String TEST_SCOPE = "/gcube/devsec/devVRE";
	

	private DatabookStore store;
	public void init() {
		store = new DBCassandraAstyanaxImpl();
	}
	public void destroy() {
		store.closeConnection();
	}
	/**
	 * when packaging test will fail if the user is not set to test.user
	 * @return .
	 */
	public String getDevelopmentUser() {
		String user = TEST_USER;
//		user = "massimiliano.assante";
		return user;
	}
	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			_log.warn("USER IS NULL setting testing user and Running OUTSIDE PORTAL");
			user = getDevelopmentUser();
			SessionManager.getInstance().getASLSession(sessionID, user).setScope(TEST_SCOPE);
		}		
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
		
	@Override
	public ArrayList<Invite> getInvites(InviteStatus[] statuses) {
		ASLSession session = getASLSession();
		String userName = session.getUsername();
		
		if (userName.compareTo("test.user") == 0) {
			_log.debug("Found " + userName + " returning nothing");
			return null;
		}
		String vreid = session.getScope();
	
		ArrayList<Invite> toReturn = new ArrayList<Invite>();
		try {
			toReturn.addAll(store.getInvitedEmailsByVRE(vreid, statuses));
		} catch (InviteIDNotFoundException | InviteStatusNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return toReturn;
	}

  
}
