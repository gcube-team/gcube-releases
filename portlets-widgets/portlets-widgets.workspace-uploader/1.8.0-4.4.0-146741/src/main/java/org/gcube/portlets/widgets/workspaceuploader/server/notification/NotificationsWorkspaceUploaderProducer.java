/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.server.notification;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingSite;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingUser;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portlets.widgets.workspaceuploader.server.util.WsUtil;
import org.gcube.portlets.widgets.workspaceuploader.shared.ContactModel;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;


/**
 * The Class NotificationsProducer.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Aug 3, 2015
 */
public class NotificationsWorkspaceUploaderProducer {

	protected ScopeBean scope;

	protected static Logger logger = Logger.getLogger(NotificationsWorkspaceUploaderProducer.class);

	protected NotificationsManager notificationsMng;

	protected String username;

	/**
	 * Instantiates a new notifications workspace uploader producer.
	 *
	 * @param httpSession the http session
	 * @param aslSession the asl session
	 * @param request the request
	 */
	public NotificationsWorkspaceUploaderProducer(String scopeGroupId, HttpSession httpSession, HttpServletRequest request) {

		this.notificationsMng = getNotificationManager(scopeGroupId, httpSession, request);
		PortalContext pContext = PortalContext.getConfiguration();
		GCubeUser currUser = pContext.getCurrentUser(request);
		this.username = currUser.getUsername();
	}

	/**
	 * Gets the notification manager.
	 *
	 * @param session the session
	 * @param request the request
	 * @return the notification manager
	 */
	public NotificationsManager getNotificationManager(String scopeGroupId, HttpSession httpSession, HttpServletRequest request)	{

		NotificationsManager notifMng = (NotificationsManager) httpSession.getAttribute(WsUtil.NOTIFICATION_MANAGER_UPLOADER);
		PortalContext pContext = PortalContext.getConfiguration();
		GCubeUser currUser = pContext.getCurrentUser(request);
		if (notifMng == null) {
			try{

				String scope = pContext.getCurrentScope(scopeGroupId);
				logger.trace("Create new NotificationsManager for user: "+currUser.getUsername() + " Scope="+scope);
				logger.trace("New ApplicationNotificationsManager with portlet class name: "+WsUtil.NOTIFICATION_PORTLET_CLASS_ID);
				logger.info("Request URI: "+request.getRequestURI());
				SocialNetworkingSite site = new SocialNetworkingSite(request);
				SocialNetworkingUser curser = new SocialNetworkingUser(currUser.getUsername(), currUser.getEmail(), currUser.getFullname(), currUser.getUserAvatarURL());


				notifMng = new ApplicationNotificationsManager(site, scope, curser, WsUtil.NOTIFICATION_PORTLET_CLASS_ID);
				httpSession.setAttribute(WsUtil.NOTIFICATION_MANAGER_UPLOADER, notifMng);
			}catch (Exception e) {
				logger.error("An error occurred instancing ApplicationNotificationsManager for user: "+currUser.getUsername(),e);
			}
		}

		return notifMng;
	}
	/**
	 * Gets the notifications mng.
	 *
	 * @return the notifications mng
	 */
	public NotificationsManager getNotificationsMng() {
		return notificationsMng;
	}

	/**
	 * Sets the notification mng.
	 *
	 * @param notificationMng the new notification mng
	 */
	public void setNotificationMng(NotificationsManager notificationMng) {
		this.notificationsMng = notificationMng;
	}


	/**
	 * Runs a new thread to notify the contacts passed in input.
	 *
	 * @param listContacts the list contacts
	 * @param workspaceItem the workspace item
	 * @param sharedFolder the shared folder
	 */
	public void notifyAddedItemToSharing(final List<ContactModel> listContacts, final WorkspaceItem workspaceItem, final WorkspaceSharedFolder sharedFolder) {

		new Thread() {
			  @Override
			  public void run() {
//				  printContacts(listContacts);
				  logger.info("Send notifies added item in sharedfolder is running...");
				  //DEBUG
				  //System.out.println("Send notifies added item in sharedfolder is running...");
				  for (ContactModel infoContactModel : listContacts) {
						try{
							//NOTIFIES ONLY THE USERS THAT ARE DIFFERENT FROM CURRENT USER
							if(infoContactModel.getLogin().compareTo(username)!=0){

								logger.info("Sending notification to user "+infoContactModel.getLogin() +" added item "+workspaceItem.getName()+" in shared folder "+sharedFolder.getName());
								boolean notify = notificationsMng.notifyAddedItem(infoContactModel.getLogin(), workspaceItem, sharedFolder);

								if(!notify){
									logger.error("An error occured when notify user: "+infoContactModel.getLogin());
								}
							}
						}catch (Exception e) {
							logger.error("An error occured in notifyAddedItemToSharing ", e);
						}
					}

				  logger.trace("notifies of added item in shared folder is completed");
			  }

		}.start();
	}


	/**
	 * Runs a new thread to notify the contacts passed in input.
	 *
	 * @param listContacts the list contacts
	 * @param workspaceItem the workspace item
	 * @param sharedFolder the shared folder
	 */
	public void notifyUpdatedItemToSharing(final List<ContactModel> listContacts, final WorkspaceItem workspaceItem, final WorkspaceSharedFolder sharedFolder) {

		new Thread() {
			  @Override
			  public void run() {
//				  printContacts(listContacts);
				  logger.info("Send notifies updated item in shared folder is running...");
				  for (ContactModel infoContactModel : listContacts) {
						try{
							//NOTIFIES ONLY THE USERS THAT ARE DIFFERENT FROM CURRENT USER
							if(infoContactModel.getLogin().compareTo(username)!=0){
								logger.info("Sending notification to user "+infoContactModel.getLogin() +" updated item "+workspaceItem.getName()+" in shared folder "+sharedFolder.getName());
								boolean notify = notificationsMng.notifyUpdatedItem(infoContactModel.getLogin(), workspaceItem, sharedFolder);

								if(!notify){
									logger.error("An error updated when notify user: "+infoContactModel.getLogin());
								}
							}
						}catch (Exception e) {
							logger.error("An error updated in notifyAddedItemToSharing ", e);
						}
					}
				  logger.trace("notifies of updated item in shared folder is completed");
			  }

		}.start();
	}

}