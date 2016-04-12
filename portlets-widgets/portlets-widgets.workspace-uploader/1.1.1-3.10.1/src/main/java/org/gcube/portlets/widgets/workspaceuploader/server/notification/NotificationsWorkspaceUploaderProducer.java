/**
 * 
 */
package org.gcube.portlets.widgets.workspaceuploader.server.notification;

import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portlets.widgets.workspaceuploader.server.util.WsUtil;
import org.gcube.portlets.widgets.workspaceuploader.shared.ContactModel;


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
	protected ASLSession aslSession;
	protected String userId;

	/**
	 * Instantiates a new notifications producer.
	 *
	 * @param aslSession the asl session
	 */
	public NotificationsWorkspaceUploaderProducer(ASLSession aslSession) {
		this.notificationsMng = WsUtil.getNotificationManager(aslSession);
		this.aslSession = aslSession;
		this.userId = aslSession.getUsername();
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
	 * Gets the asl session.
	 *
	 * @return the asl session
	 */
	public ASLSession getAslSession() {
		return aslSession;
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
				  System.out.println("Send notifies added item in sharedfolder is running...");
				  
				  for (ContactModel infoContactModel : listContacts) {
						try{

							//NOTIFIES ONLY THE USERS THAT ARE DIFFERENT FROM CURRENT USER
							if(infoContactModel.getLogin().compareTo(userId)!=0){ 
							
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
							if(infoContactModel.getLogin().compareTo(userId)!=0){ 
							
								logger.info("Sending notification to user "+infoContactModel.getLogin() +" updated item "+workspaceItem.getName()+" in shared folder "+sharedFolder.getName());
								boolean notify = notificationsMng.notifyUpdatedItem(infoContactModel.getLogin(), workspaceItem, sharedFolder);

								if(!notify){
									logger.error("An error updated when notify user: "+infoContactModel.getLogin());
								}
							}
						}catch (Exception e) {
							logger.error("An error updated in notifyAddedItemToSharing ", e);
							e.printStackTrace();
						}
					}
				  
				  logger.trace("notifies of updated item in shared folder is completed");
			  }

		}.start();
	}
	
}