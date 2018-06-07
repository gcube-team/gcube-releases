package org.gcube.portlets.widgets.wsmail.server;

import java.util.ArrayList;
import java.util.List;

import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingSite;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingUser;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.portal.PortalContext;
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


	/**
	 * 
	 * @return the list of workspace users
	 */
	public CurrUserAndPortalUsersWrapper getWorkspaceUsers() {
		PortalContext pContext = PortalContext.getConfiguration();
		GCubeUser currentUser = pContext.getCurrentUser(getThreadLocalRequest());
		_log.info("trying to get WorkspaceUserss ..");
		WSUser currUser = null;
		ArrayList<WSUser> portalUsers = new ArrayList<WSUser>();
		try {
			UserManager um = new LiferayUserManager();
			GroupManager gm = new LiferayGroupManager();
			List<GCubeUser> users = um.listUsersByGroup(gm.getRootVO().getGroupId());
			for (GCubeUser user : users) {
				_log.trace("Trying to get additional info for "+user.getUsername());
				portalUsers.add(new WSUser(user.getUserId()+"", user.getUsername(), user.getFullname(), user.getEmail()));

			}
			currUser = new WSUser(currentUser.getUsername(), currentUser.getUsername(), currentUser.getFullname(), currentUser.getEmail());


		} catch (Exception e) {
			_log.error("Error in server get all contacts ", e);
		}
		CurrUserAndPortalUsersWrapper toReturn = new CurrUserAndPortalUsersWrapper(currUser, portalUsers);
		return toReturn;
	}
	/**
	 * 
	 */
	@Override
	public boolean sendToById(ArrayList<String> recipientIds, ArrayList<String> listAttachmentsId, String subject, String body) {
		PortalContext pContext = PortalContext.getConfiguration();
		GCubeUser currentUser = pContext.getCurrentUser(getThreadLocalRequest());

		if (listAttachmentsId == null)
			listAttachmentsId = new ArrayList<String>();
		try {
			Workspace workspace = HomeLibrary.getUserWorkspace(currentUser.getUsername());
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

			NotificationsManager nm = new ApplicationNotificationsManager(new SocialNetworkingSite(getThreadLocalRequest()), pContext.getCurrentScope(getThreadLocalRequest()), 
					new SocialNetworkingUser(
							currentUser.getUsername(),
							currentUser.getEmail(), 
							currentUser.getFullname(), 
							currentUser.getUserAvatarURL()
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



}
