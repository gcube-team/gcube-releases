package org.gcube.portlets.widgets.wsmail.server;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingSite;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingUser;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.portal.notifications.bean.GenericItemBean;
import org.gcube.portal.notifications.thread.MessageNotificationsThread;
import org.gcube.portlets.widgets.wsmail.client.WsMailService;
import org.gcube.portlets.widgets.wsmail.shared.CurrUserAndPortalUsersWrapper;
import org.gcube.portlets.widgets.wsmail.shared.WSUser;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class WsMailServiceImpl extends RemoteServiceServlet implements WsMailService {

	private static final Logger _log = LoggerFactory.getLogger(WsMailServiceImpl.class);
	public static final String TEST_SCOPE = "/gcube/devsec";
	public static final String TEST_USER = "test.user";
	public static final String USERNAME_ATTRIBUTE = "username";
	private boolean withinPortal = false;
	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {		
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		String sessionID = httpSession.getId();
		String user = (String) httpSession.getAttribute("username");
		if (user == null) {
			_log.warn("TEST MODE - NO USER FOUND");
			ASLSession session = SessionManager.getInstance().getASLSession(sessionID, getDevelopmentUser());
			session.setScope(TEST_SCOPE);
			return session;
		}
		else {
			_log.info("LIFERAY PORTAL DETECTED user=" + user);
			withinPortal = true;
		}
		return SessionManager.getInstance().getASLSession(sessionID, user);
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
	 * 
	 * @return the list of workspace users
	 */
	public CurrUserAndPortalUsersWrapper getWorkspaceUsers() {
		ASLSession session = getASLSession();
		_log.info("trying to get WorkspaceUserss .. withinPortal="+withinPortal);
		WSUser currUser = null;
		ArrayList<WSUser> portalUsers = new ArrayList<WSUser>();
		try {
			if (withinPortal) {
				UserManager um = new LiferayUserManager();
				GroupManager gm = new LiferayGroupManager();
				List<GCubeUser> users = um.listUsersByGroup(gm.getRootVO().getGroupId());
				for (GCubeUser user : users) {
					_log.trace("Trying to get additional info for "+user.getUsername());
					if (user.getUsername().compareTo(TEST_USER) != 0) { //skip test.user
						
						portalUsers.add(new WSUser(user.getUserId()+"", user.getUsername(), user.getFullname(), user.getEmail()));
					}
				}
				currUser = new WSUser(session.getUsername(), session.getUsername(), session.getUserFullName(), session.getUserEmailAddress());
			}
			else { //test users
				portalUsers.add(new WSUser("11111", "massimiliano.assante", "Massimiliano Assante", "assante@isti.cnr.it"));
				portalUsers.add(new WSUser("11111", "giogio.giorgi", "Giorgio Giorgietti", "pippo@aol.com"));
				portalUsers.add(new WSUser("2222", "pino.pinetti", "Pino Dall'ara", "ambaradam@aol.com"));
				portalUsers.add(new WSUser("11333111", "rino.gattuso", "Rino Gattuso", "rino@acmilan.com"));
				portalUsers.add(new WSUser("114444111", "alex.delpiero", "Alessandro Del Piero", "delpiero@juventus.com"));
				portalUsers.add(new WSUser("3462", "sandro.nesta", "Alessandro Nesta", "sandro@montreal.it"));
				portalUsers.add(new WSUser("11464321", "samsung.mobile", "Samsung Mobile", "samsung@samsung.com"));	

				currUser = new WSUser("12345", getDevelopmentUser(), "Test User", "test.user@isti.cnr.it");
			}
		} catch (Exception e) {
			_log.error("Error in server get all contacts ", e);
		}
		//		for (WSUser user : toReturn) {
		//			_log.trace("Got: "+user.toString());
		//		}

		CurrUserAndPortalUsersWrapper toReturn = new CurrUserAndPortalUsersWrapper(currUser, portalUsers);
		return toReturn;
	}
	/**
	 * 
	 */
	@Override
	public boolean sendToById(ArrayList<String> recipientIds, ArrayList<String> listAttachmentsId, String subject, String body) {
		ASLSession session = getASLSession();
		if (session == null || session.getUsername().compareTo(TEST_USER) == 0) {
			_log.warn("Session is expired, returning false");
			return false;
		} 
		if (listAttachmentsId == null)
			listAttachmentsId = new ArrayList<String>();
		try {
			Workspace workspace = getWorkspace();
			_log.info("Sending message to: " + recipientIds.toString());
			String checkedSubject = subject;
			String checkedBody = body;
			String messageId = workspace.getWorkspaceMessageManager().sendMessageToPortalLogins(checkedSubject, checkedBody, listAttachmentsId, recipientIds);
			try {
				body += getPublicLinksForAttachs(workspace, listAttachmentsId);
			}
			catch (InternalErrorException|ItemNotFoundException e) {
				_log.error("Ops, could not generate publick link for some of the attachments");
			}
			_log.debug("Sending message notification to: " + recipientIds.toString());
			
			List<GenericItemBean> recipients = getUsersbyUserId(recipientIds);
			
			NotificationsManager nm = new ApplicationNotificationsManager(new SocialNetworkingSite(getThreadLocalRequest()), session.getScope(), 
					new SocialNetworkingUser(
							session.getUsername(),
							session.getUserEmailAddress(), 
							session.getUserFullName(), 
							session.getUserAvatarId()
							));
			Thread thread = new Thread(new MessageNotificationsThread(recipients, messageId, checkedSubject, body, nm));
			thread.start();

			return (messageId != null);
		} catch (Exception e) {
			_log.error("While Sending message to: " + recipientIds.toString());
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * return the User instance given his id
	 * @param recipientIds
	 * @return
	 */
	private List<GenericItemBean> getUsersbyUserId(List<String> recipientIds)  {
		List<GenericItemBean> recipients = new ArrayList<GenericItemBean>();
		for (String userid : recipientIds) {
			GCubeUser user = null;
			try {
				user = new LiferayUserManager().getUserByUsername(userid);
				recipients.add(new GenericItemBean(""+user.getUserId(), user.getUsername(), user.getFullname(), ""));
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}	
		return recipients;
	}	
	/**
	 * 
	 * @param workspace
	 * @param listAttachmentsId
	 * @return
	 * @throws ItemNotFoundException 
	 * @throws InternalErrorException 
	 */
	private String getPublicLinksForAttachs(Workspace workspace, ArrayList<String> listAttachmentsId) throws ItemNotFoundException, InternalErrorException{

		if (listAttachmentsId != null && (!listAttachmentsId.isEmpty()) ) {
			List<WorkspaceItem> attachments = new ArrayList<WorkspaceItem>();
			for (String itemId : listAttachmentsId) {
				attachments.add(workspace.getItem(itemId));
			}

			StringBuilder builder = new StringBuilder();

			if(attachments!=null && attachments.size() > 0){
				builder.append("\n\n\nThe following ");
				String msg = attachments.size()>1?"files were attached to this message:":"file was attached to this message:";
				builder.append(msg+"\n");
				for (WorkspaceItem workspaceItem : attachments) {

					if(workspaceItem.getType().equals(WorkspaceItemType.FOLDER_ITEM)) {
						FolderItem folderItem = (FolderItem) workspaceItem;
						String publicLink = "";
						String itemName = "";
						try {
							itemName = workspaceItem.getName();
							publicLink = folderItem.getPublicLink(true);
						}
						catch (InternalErrorException e) {
							_log.warn("An error occurred when creating public link for attachment, skipping file: " + itemName);
							return "";
						}
						builder.append(itemName + " ("+publicLink+")");
						builder.append("\n");
					}
				}
				_log.debug("returning public links: "+builder.toString());
				return builder.toString();
			}
			else return "";
		}	
		else return "";
	}
	/**
	 * 
	 * @return the workspace instance
	 * @throws InternalErrorException
	 * @throws HomeNotFoundException
	 * @throws WorkspaceFolderNotFoundException
	 */
	private Workspace getWorkspace() throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException {
		final ASLSession session = getASLSession();
		Workspace workspace = HomeLibrary.getUserWorkspace(session.getUsername());
		return workspace;
	}


}
