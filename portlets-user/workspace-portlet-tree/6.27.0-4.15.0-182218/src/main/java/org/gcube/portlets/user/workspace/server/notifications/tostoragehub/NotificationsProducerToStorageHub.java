/**
 *
 */
package org.gcube.portlets.user.workspace.server.notifications.tostoragehub;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.applicationsupportlayer.social.shared.SocialFileItem;
import org.gcube.applicationsupportlayer.social.shared.SocialSharedFolder;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceSharedFolder;
import org.gcube.portlets.user.workspace.server.util.PortalContextInfo;
import org.gcube.portlets.user.workspace.server.util.WsUtil;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class NotificationsProducerToStorageHub.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Oct 5, 2018
 */
public class NotificationsProducerToStorageHub {

	protected ScopeBean scope;

	protected static Logger logger = LoggerFactory.getLogger(NotificationsProducerToStorageHub.class);

	protected NotificationsManager notificationsMng;
	protected HttpSession httpSession;
	protected String userId;


	/**
	 * Instantiates a new notifications producer.
	 *
	 * @param httpServletRequest the http servlet request
	 */
	public NotificationsProducerToStorageHub(HttpServletRequest httpServletRequest) {
		PortalContextInfo info = WsUtil.getPortalContext(httpServletRequest);
		this.notificationsMng = WsUtil.getNotificationManager(httpServletRequest);
		this.userId = info.getUsername();
	}


	/**
	 * Runs a new thread to notify the contacts passed in input.
	 *
	 * @param workspace the workspace
	 * @param listContacts the list contacts
	 * @param workspaceItem the workspace item
	 * @param sharedFolder the shared folder
	 */
	public void notifyAddedItemToSharing(final org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace, final List<InfoContactModel> listContacts, final WorkspaceItem workspaceItem, final WorkspaceSharedFolder sharedFolder) {

		new Thread() {
			  @Override
			  public void run() {

//				  printContacts(listContacts);
				  logger.debug("Send notifies added item in sharedfolder is running...");

				  for (InfoContactModel infoContactModel : listContacts) {
						try{

							//NOTIFIES ONLY THE USERS THAT ARE DIFFERENT FROM CURRENT USER
							if(infoContactModel.getLogin().compareTo(userId)!=0){

								logger.debug("Sending notification to user "+infoContactModel.getLogin() +" added item [id: "+workspaceItem.getId() +"] name: "+workspaceItem.getName()+" in shared folder "+sharedFolder.getName());
								//boolean notify = notificationsMng.notifyAddedItem(infoContactModel.getLogin(), NotificationMapperToStorageHub.toSocialItem(workspace, workspaceItem), NotificationMapperToStorageHub.toSocialSharedFolder(sharedFolder));
								SocialFileItem toSocialItem = NotificationMapperToStorageHub.toSocialItem(workspace, workspaceItem);
								SocialSharedFolder toSocialFolder = NotificationMapperToStorageHub.toSocialSharedFolder(sharedFolder);
								boolean notify = notificationsMng.notifyAddedItem(infoContactModel.getLogin(), toSocialItem, toSocialFolder);

								if(!notify){
									logger.error("An error occured when notify user: "+infoContactModel.getLogin());
								}
							}
						}catch (Exception e) {
							logger.error("An error occured in notifyAddedItemToSharing ", e);
						}
					}

				  logger.debug("notifies of added item in shared folder is completed");
			  }

		}.start();
	}


	/**
	 * Notify moved item to sharing.
	 *
	 * @param workspace the workspace
	 * @param listContacts the list contacts
	 * @param workspaceItem the workspace item
	 * @param sourceRootSharedFolder the shared folder
	 */
	public void notifyMovedItemToSharing(final org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace, final List<InfoContactModel> listContacts, final WorkspaceItem workspaceItem, final WorkspaceSharedFolder sourceRootSharedFolder) {


		new Thread() {
			  @Override
			  public void run() {

				  logger.debug("Sending notification remove item in shared folder is running...");

				  for (InfoContactModel infoContactModel : listContacts) {
						try{

							//NOTIFIES ONLY THE USERS THAT ARE DIFFERENT FROM CURRENT USER
							if(infoContactModel.getLogin().compareTo(userId)!=0){

								logger.debug("Sending notification  to user "+infoContactModel.getLogin() +" moved item "+workspaceItem.getName()+" in shared folder "+sourceRootSharedFolder.getName());
								boolean notify = notificationsMng.notifyMovedItem(infoContactModel.getLogin(), NotificationMapperToStorageHub.toSocialItem(workspace, workspaceItem), NotificationMapperToStorageHub.toSocialSharedFolder(sourceRootSharedFolder));

								if(!notify){
									logger.error("An error occured when notify user: "+infoContactModel.getLogin());
								}
							}
						}catch (Exception e) {
							logger.error("An error occurred in notifyMovedItemToSharing ", e);
							e.printStackTrace();
						}
					}

				  logger.debug("notifies of moved item in shared folder is completed");
			  }



		}.start();

	}



	/**
	 * Notify folder renamed.
	 *
	 * @param listSharedContact the list shared contact
	 * @param folderItem the folder item
	 * @param itemOldName the item old name
	 * @param itemNewName the item new name
	 * @param idRootSharedFolder the id root shared folder
	 */
	public void notifyFolderRenamed(final List<InfoContactModel> listSharedContact, final WorkspaceItem folderItem, final String itemOldName, final String itemNewName, final String idRootSharedFolder) {

		new Thread(){
			@Override
			public void run() {

				logger.trace("Send notifies shared folder was renamed is running...");

				if(NotificationStorageHubUtil.checkIsRootFolderShared(folderItem.getId(), idRootSharedFolder)){
					logger.info("Notification not sent because the event is on root shared folder");
					return;
				}

				for (InfoContactModel infoContactModel : listSharedContact) {
					try{
						//NOTIFIES ONLY THE USERS THAT ARE DIFFERENT FROM CURRENT USER
						if(infoContactModel.getLogin().compareTo(userId)!=0){

							logger.debug("Sending notification share folder "+itemOldName+" was renamed as " + itemNewName+ "for user "+infoContactModel.getLogin());
							boolean notify = notificationsMng.notifyFolderRenaming(infoContactModel.getLogin(), itemOldName, itemNewName, idRootSharedFolder);

							if(!notify)
								logger.error("An error occured when notify user: "+infoContactModel.getLogin());
						}
					}catch (Exception e) {
						logger.error("An error occured in notifyFolderRenamed ", e);
//						e.printStackTrace();
					}
				}

				logger.trace("notifies share folder was renamed is completed");
			}
		}.start();


	}



	/**
	 * Notify item renamed.
	 *
	 * @param listSharedContact the list shared contact
	 * @param previousName the previous name
	 * @param item the item
	 * @param sharedFolder the shared folder
	 * @param workspace the workspace
	 */
	public void notifyItemRenamed(final List<InfoContactModel> listSharedContact, final String previousName, final WorkspaceItem item, final WorkspaceSharedFolder sharedFolder, final org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace) {

		new Thread(){
			@Override
			public void run() {

				logger.trace("Send notifies shared item was updated is running...");

				for (InfoContactModel infoContactModel : listSharedContact) {
					try{
						//NOTIFIES ONLY THE USERS THAT ARE DIFFERENT FROM CURRENT USER
						if(infoContactModel.getLogin().compareTo(userId)!=0){

							logger.debug("Sending notification to user "+infoContactModel.getLogin() + " updated item "+item.getName());
							boolean notify = notificationsMng.notifyItemRenaming(infoContactModel.getLogin(), previousName,  NotificationMapperToStorageHub.toSocialItem(workspace, item), NotificationMapperToStorageHub.toSocialSharedFolder(sharedFolder));

							if(!notify)
								logger.error("An error occured when notify user: "+infoContactModel.getLogin());
						}
					}catch (Exception e) {
						logger.error("An error occured in notifyItemUpdated ", e);
//						e.printStackTrace();
					}
				}

				logger.trace("notifies shared item was updated is completed");
			}
		}.start();


	}
	
	/**
	 * Notify removed item to sharing.
	 *
	 * @param listContacts the list contacts
	 * @param itemName the item name
	 * @param sharedFolder the shared folder
	 */
	public void notifyRemovedItemToSharing(final List<InfoContactModel> listContacts, final String itemName, final WorkspaceSharedFolder sharedFolder) {


		new Thread() {
			  @Override
			  public void run() {
				  
				  logger.trace("Sending notification removed item in shared folder is running...");
				  if(itemName==null || itemName.isEmpty()){
					  logger.trace("Notification isn't sent - itemName is null or empty");
					  return;
				  }

				  if(sharedFolder==null){
					  logger.debug("Impossible to send notification - sharedFolder is null");
				  }

				  for (InfoContactModel infoContactModel : listContacts) {
						try{

							//NOTIFIES ONLY THE USERS THAT ARE DIFFERENT FROM CURRENT USER
							if(infoContactModel.getLogin().compareTo(userId)!=0){

								logger.debug("Sending notification  to user "+infoContactModel.getLogin() +" removed item "+itemName+" in the shared folder "+sharedFolder.getName());
								//DEBUG
//								System.out.println("Sending notification  to user "+infoContactModel.getLogin() +" removed item "+itemName+" in shared folder "+sharedFolder.getName());
								boolean notify = notificationsMng.notifyRemovedItem(infoContactModel.getLogin(), itemName, NotificationMapperToStorageHub.toSocialSharedFolder(sharedFolder));
								if(!notify){
									logger.error("An error occured when notify user: "+infoContactModel.getLogin());
								}
							}
						}catch (Exception e) {
							logger.error("An error occurred in notifyRemovedItemToSharing ", e);
						}
					}

				  logger.debug("notifies of moved item in shared folder is completed");
			  }
		}.start();

	}
	
	/**
	 * Runs a new thread to notify the contacts passed in input.
	 *
	 * @param listContacts the list contacts
	 * @param folderNameDeleted the folder name deleted
	 */
	public void notifySharedFolderDeleted(final List<InfoContactModel> listContacts, final String folderNameDeleted) {


		new Thread() {
			  @Override
			  public void run() {
//				  printContacts(listContacts);
				  logger.trace("Sending notification shared folder deleted is running...");
				  for (InfoContactModel infoContactModel : listContacts) {
						try{

							//NOTIFIES ONLY THE USERS THAT ARE DIFFERENT FROM CURRENT USER
							if(infoContactModel.getLogin().compareTo(userId)!=0){
								logger.trace("Sending notification to user "+infoContactModel.getLogin() +" deleted shared folder "+folderNameDeleted);
								//THIS NOTIFCATION HAS BEEN REMOVED
							}
						}catch (Exception e) {
							logger.error("An error occured in notifySharedFolderDeleted ", e);
						}
					}
				  
				  logger.debug("notifies of deleted shared foder is completed");
			  }

		}.start();

	}

}
