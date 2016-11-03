/**
 *
 */
package org.gcube.portlets.user.workspace.server.notifications;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portlets.user.workspace.client.model.InfoContactModel;
import org.gcube.portlets.user.workspace.server.util.DifferenceBetweenInfoContactModel;
import org.gcube.portlets.user.workspace.server.util.UserUtil;
import org.gcube.portlets.user.workspace.server.util.WsUtil;

/**
 * The Class NotificationsProducer.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 */
public class NotificationsProducer {

	protected ScopeBean scope;

	protected static Logger logger = Logger.getLogger(NotificationsProducer.class);

	protected NotificationsManager notificationsMng;
	protected ASLSession aslSession;
	protected String userId;


	/**
	 * Instantiates a new notifications producer.
	 *
	 * @param aslSession the asl session
	 * @param request the request
	 */
	public NotificationsProducer(ASLSession aslSession, HttpServletRequest request) {
		this.notificationsMng = WsUtil.getNotificationManager(aslSession, request);
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
	 * @param sharedFolder the shared folder
	 */
	public void notifyFolderSharing(final List<InfoContactModel> listContacts, final WorkspaceSharedFolder sharedFolder) {

		new Thread(){
			@Override
			public void run() {

				logger.trace("Send notifies folder sharing is running...");

				for (InfoContactModel infoContactModel : listContacts) {
					try{
						//NOTIFIES ONLY THE USERS THAT ARE DIFFERENT FROM CURRENT USER
						if(infoContactModel.getLogin().compareTo(userId)!=0){

							logger.debug("Sending notification new share folder "+sharedFolder.getName()+" for user "+infoContactModel.getLogin());

							//DEBUG
//							System.out.println("Sending notification new share folder "+sharedFolder.getName()+" for user "+infoContactModel.getLogin());

							boolean notify = notificationsMng.notifyFolderSharing(infoContactModel.getLogin(), sharedFolder);


							if(!notify)
								logger.error("An error occured when notify user: "+infoContactModel.getLogin());
						}
					}catch (Exception e) {
						logger.error("An error occured in notifyFolderSharing ", e);
//						e.printStackTrace();
					}
				}

				logger.trace("notifies share folder is completed");
			}
		}.start();


	}



	/**
	 * Runs a new thread to notify the contacts passed in input.
	 *
	 * @param listSharedContact the list shared contact
	 * @param folderItem the folder item
	 * @param itemOldName the item old name
	 * @param itemNewName the item new name
	 * @param idsharedFolder the idshared folder
	 */
	public void notifyFolderRenamed(final List<InfoContactModel> listSharedContact, final WorkspaceItem folderItem, final String itemOldName, final String itemNewName, final String idsharedFolder) {

		new Thread(){
			@Override
			public void run() {

				logger.trace("Send notifies shared folder was renamed is running...");

				try {

					if(NotificationsUtil.checkIsRootFolderShared(folderItem.getId(), idsharedFolder)){
						logger.trace("Notification isn't sent because the event is on root shared folder");
						return;
					}

				} catch (InternalErrorException e1) {
					logger.error("An error occurred in checkIsRootFolderShared ", e1);
					return;
				}

				for (InfoContactModel infoContactModel : listSharedContact) {
					try{
						//NOTIFIES ONLY THE USERS THAT ARE DIFFERENT FROM CURRENT USER
						if(infoContactModel.getLogin().compareTo(userId)!=0){

							logger.trace("Sending notification share folder "+itemOldName+" was renamed as " + itemNewName+ "for user "+infoContactModel.getLogin());

							//DEBUG
							System.out.println("Sending notification share folder "+itemOldName+" was renamed as " + itemNewName+ "for user "+infoContactModel.getLogin());

							boolean notify = notificationsMng.notifyFolderRenaming(infoContactModel.getLogin(), itemOldName, itemNewName, idsharedFolder);


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
	 * Runs a new thread to notify the contacts passed in input.
	 *
	 * @param listSharedContact the list shared contact
	 * @param previousName the previous name
	 * @param item the item
	 * @param sharedFolder the shared folder
	 */
	public void notifyItemRenamed(final List<InfoContactModel> listSharedContact, final String previousName, final WorkspaceItem item, final WorkspaceSharedFolder sharedFolder) {

		new Thread(){
			@Override
			public void run() {

				logger.trace("Send notifies shared item was updated is running...");

				for (InfoContactModel infoContactModel : listSharedContact) {
					try{
						//NOTIFIES ONLY THE USERS THAT ARE DIFFERENT FROM CURRENT USER
						if(infoContactModel.getLogin().compareTo(userId)!=0){

							logger.trace("Sending notification to user "+infoContactModel.getLogin() + " updated item "+item.getName());

							//DEBUG
							System.out.println("Sending notification to user "+infoContactModel.getLogin() + " updated item "+item.getName());

//							notificationsMng.notifyItemRenaming(infoContactModel.getLogin(), previousName, item, sharedFolder);

							boolean notify = notificationsMng.notifyItemRenaming(infoContactModel.getLogin(), previousName, item, sharedFolder);

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
	 * Runs a new thread to notify the updated (add/remove) contacts to sharing.
	 *
	 * @param listSharedContact - list of contacts already shared
	 * @param listSharingContact - list of "new" contacts to share
	 * @param sharedFolder - the shared folder
	 */
	public void notifyUpdatedUsersToSharing(final List<InfoContactModel> listSharedContact, final List<InfoContactModel> listSharingContact, final WorkspaceSharedFolder sharedFolder) {

		new Thread() {
			  @Override
			  public void run() {

				  try{

					  //NEW USER SHARED
					  DifferenceBetweenInfoContactModel diff = new DifferenceBetweenInfoContactModel(listSharingContact, listSharedContact);

					  List<InfoContactModel> listNewContactsShared = diff.getDifferentsContacts();

					  logger.info("list new contacts shared: "+listNewContactsShared.size());

					  if(listNewContactsShared.size()>0){

						  if(listNewContactsShared.size()==1){ //CASE ONLY ONE CONTACS WAS ADDED

							  InfoContactModel infoContactModel = listNewContactsShared.get(0);

							  for (InfoContactModel contact : listSharedContact) { //NOTIFIES ALREADY SHARED CONTACTS THAT A NEW USER WAS ADDED

									try{

										logger.trace("Sending notification to user "+contact.getLogin()+", added user "+ infoContactModel.getLogin() +" to share folder "+sharedFolder.getName());

										//DEBUG
	//									System.out.println("Sending notification added user "+ infoContactModel.getLogin() +" to share folder "+sharedFolder.getName() + " for user "+contact.getLogin());

										boolean notify = notificationsMng.notifyFolderAddedUser(contact.getLogin(), sharedFolder, infoContactModel.getLogin());

										if(!notify)
											logger.error("An error occured when notifies user: "+contact.getLogin());

									}catch (Exception e) {
										logger.error("An error occured in notifyFolderAddedUser ", e);
//										e.printStackTrace();
									}
							}

							List<InfoContactModel> listCts = new ArrayList<InfoContactModel>();
							listCts.add(infoContactModel);
							notifyFolderSharing(listCts, sharedFolder); //NOTIFIER NEW USER OF SHARING FOLDER

						  }else{ //CASE MORE THEN ONE CONTACT WAS ADDED

							  List<String> listLogins = UserUtil.getListLoginByInfoContactModel(listNewContactsShared);

							  for (InfoContactModel contact : listSharedContact) { //NOTIFIES ALREADY SHARED CONTACTS THATH A NEW USER WAS ADDED

									try{

										logger.trace("Sending notification to user "+contact.getLogin()+", added "+listLogins.size()+" users to share folder "+sharedFolder.getName());

										//DEBUG
	//									System.out.println("Sending notification added user "+ infoContactModel.getLogin() +" to share folder "+sharedFolder.getName() + " for user "+contact.getLogin());

										boolean notify = notificationsMng.notifyFolderAddedUsers(contact.getLogin(), sharedFolder, listLogins);

										if(!notify)
											logger.error("An error occured when notifies user: "+contact.getLogin());

									}catch (Exception e) {
										logger.error("An error occured in notifyFolderAddedUser ", e);
//										e.printStackTrace();
									}
							}

							notifyFolderSharing(listNewContactsShared, sharedFolder); //NOTIFIER NEW USER OF SHARING FOLDER

						  }
					  }

					  //USER REMOVED FROM SHARE
					  DifferenceBetweenInfoContactModel diff2 = new DifferenceBetweenInfoContactModel(listSharedContact, listSharingContact);

					  List<InfoContactModel> listRemovedUsersFromShare = diff2.getDifferentsContacts();

					  logger.info("list removed contacts from share: "+listRemovedUsersFromShare.size());

					  if(listRemovedUsersFromShare.size()>0){

						  for (InfoContactModel contact : listRemovedUsersFromShare)
							  notifyFolderRemovedUser(contact, sharedFolder);
					  }

					}catch (Exception e) {
						logger.error("An error occured in notifyAddedUserToSharing ", e);
//						e.printStackTrace();
					}
				}

		}.start();

	}


	/**
	 * Runs a new thread to notify the contacts passed in input.
	 *
	 * @param listContacts the list contacts
	 * @param unShareFolderId the un share folder id
	 * @param unSharedFolderName the un shared folder name
	 */
	public void notifyFolderUnSharing(final List<InfoContactModel> listContacts, final String unShareFolderId, final String unSharedFolderName) {


		new Thread() {
			  @Override
			  public void run() {

//				  printContacts(listContacts);
				  logger.trace("Send notifies folder un share is running...");

				  for (InfoContactModel infoContactModel : listContacts) {
						try{

							//NOTIFIES ONLY THE USERS THAT ARE DIFFERENT FROM CURRENT USER
							if(infoContactModel.getLogin().compareTo(userId)!=0){

								logger.trace("Sending notification to user "+infoContactModel.getLogin() +" unshare folder "+unSharedFolderName);

								//DEBUG
//								System.out.println("Sending notification to user "+infoContactModel.getLogin() +" un shared folder "+unSharedFolder.getName());
//								boolean notify = notificationsMng.notifyFolderRemovedUser(infoContactModel.getLogin(), (WorkspaceSharedFolder) unSharedFolder);

								boolean notify = notificationsMng.notifyFolderUnsharing(infoContactModel.getLogin(), unShareFolderId, unSharedFolderName);
								if(!notify)
									logger.error("An error occured when notifies user: "+infoContactModel.getLogin());
							}
						}catch (Exception e) {
							logger.error("An error occured in notifyFolderUnSharing ", e);
//							e.printStackTrace();
						}
					}

				  logger.trace("notifies of un share notifications is completed");
			  }

		}.start();

	}

	/**
	 * Notify folder removed user.
	 *
	 * @param userUnShared the user un shared
	 * @param shareFolder the share folder
	 */
	public void notifyFolderRemovedUser(final InfoContactModel userUnShared, final WorkspaceSharedFolder shareFolder) {

		new Thread() {
			  @Override
			  public void run() {

				  logger.trace("Send notifies removed user from shared folder is running...");
				  try{
						//NOTIFIES ONLY THE USERS THAT ARE DIFFERENT FROM CURRENT USER
						if(userUnShared.getLogin().compareTo(userId)!=0){

							logger.debug("Sending notification to user "+userUnShared.getLogin() +" unshared from folder "+shareFolder.getName());
							boolean notify = notificationsMng.notifyFolderRemovedUser(userUnShared.getLogin(), shareFolder);

							if(!notify)
								logger.error("An error occured when notifies user: "+userUnShared.getLogin());
						}

					}catch (Exception e) {
						logger.error("An error occured in notifyFolderRemovedUser ", e);
					}

				  logger.trace("notifies of un unshare user is completed");
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
	public void notifyAddedItemToSharing(final List<InfoContactModel> listContacts, final WorkspaceItem workspaceItem, final WorkspaceSharedFolder sharedFolder) {

		new Thread() {
			  @Override
			  public void run() {

//				  printContacts(listContacts);
				  logger.trace("Send notifies added item in sharedfolder is running...");

				  //DEBUG
				  System.out.println("Send notifies added item in sharedfolder is running...");

				  for (InfoContactModel infoContactModel : listContacts) {
						try{

							//NOTIFIES ONLY THE USERS THAT ARE DIFFERENT FROM CURRENT USER
							if(infoContactModel.getLogin().compareTo(userId)!=0){

								logger.info("Sending notification to user "+infoContactModel.getLogin() +" added item [id: "+workspaceItem.getId() +"] name: "+workspaceItem.getName()+" in shared folder "+sharedFolder.getName());

								//DEBUG
//								System.out.println("Send notify folder un share user "+infoContactModel.getLogin());

								boolean notify = notificationsMng.notifyAddedItem(infoContactModel.getLogin(), workspaceItem, sharedFolder);

								if(!notify){
									logger.error("An error occured when notify user: "+infoContactModel.getLogin());
									//DEBUG
//									System.out.println("An error occured when notify user: "+infoContactModel.getLogin());
								}
							}
						}catch (Exception e) {
							logger.error("An error occured in notifyAddedItemToSharing ", e);
//							e.printStackTrace();
						}
					}

				  logger.trace("notifies of added item in shared folder is completed");

				  //DEBUG
//				  System.out.println("notifies of added item in shared folder is completed");
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
	public void notifyUpdatedItemToSharing(final List<InfoContactModel> listContacts, final WorkspaceItem workspaceItem, final WorkspaceSharedFolder sharedFolder) {

		new Thread() {
			  @Override
			  public void run() {

//				  printContacts(listContacts);
				  logger.trace("Send notifies updated item in shared folder is running...");

				  //DEBUG
//				  System.out.println("Send notifies updated item in shared folder is running...");

				  for (InfoContactModel infoContactModel : listContacts) {
						try{

							//NOTIFIES ONLY THE USERS THAT ARE DIFFERENT FROM CURRENT USER
							if(infoContactModel.getLogin().compareTo(userId)!=0){

								logger.trace("Sending notification to user "+infoContactModel.getLogin() +" updated item "+workspaceItem.getName()+" in shared folder "+sharedFolder.getName());

								//DEBUG
//								System.out.println("Sending notification to user "+infoContactModel.getLogin() +" updated item "+workspaceItem.getName()+" in shared folder "+sharedFolder.getName());

								//DEBUG
//								System.out.println("Send notify folder un share user "+infoContactModel.getLogin());

								boolean notify = notificationsMng.notifyUpdatedItem(infoContactModel.getLogin(), workspaceItem, sharedFolder);

								if(!notify){
									logger.error("An error updated when notify user: "+infoContactModel.getLogin());
									//DEBUG
//									System.out.println("An error updated when notify user: "+infoContactModel.getLogin());
								}
							}
						}catch (Exception e) {
							logger.error("An error updated in notifyAddedItemToSharing ", e);
							e.printStackTrace();
						}
					}

				  logger.trace("notifies of updated item in shared folder is completed");

				  //DEBUG
//				  System.out.println("notifies of updated item in shared folder is completed");
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
	public void notifyMovedItemToSharing(final List<InfoContactModel> listContacts, final WorkspaceItem workspaceItem, final WorkspaceSharedFolder sharedFolder) {


		new Thread() {
			  @Override
			  public void run() {

				  logger.trace("Sending notification remove item in shared folder is running...");
//				  printContacts(listContacts);

				  try {

						if(NotificationsUtil.checkIsRootFolderShared(workspaceItem.getId(), sharedFolder.getId())){
							logger.trace("Notification isn't sent because the event is on root shared folder");
							return;
						}

				  } catch (InternalErrorException e1) {
						logger.error("An error occurred in checkIsRootFolderShared ", e1);
						return;
				  }

				  logger.trace("Sending notification moved item in shared folder is running...");

//				  System.out.println("Sending notification moved item in shared folder is running...");

				  for (InfoContactModel infoContactModel : listContacts) {
						try{

							//NOTIFIES ONLY THE USERS THAT ARE DIFFERENT FROM CURRENT USER
							if(infoContactModel.getLogin().compareTo(userId)!=0){

								logger.trace("Sending notification  to user "+infoContactModel.getLogin() +" moved item "+workspaceItem.getName()+" in shared folder "+sharedFolder.getName());

								//DEBUG
//								System.out.println("Sending notification  to user "+infoContactModel.getLogin() +" moved item "+workspaceItem.getName()+" in shared folder "+sharedFolder.getName());

								boolean notify = notificationsMng.notifyMovedItem(infoContactModel.getLogin(), workspaceItem, sharedFolder);

								if(!notify){
									logger.error("An error occured when notify user: "+infoContactModel.getLogin());

									//DEBUG
//									System.out.println("An error occured when notify user: "+infoContactModel.getLogin());
								}
							}
						}catch (Exception e) {
							logger.error("An error occurred in notifyMovedItemToSharing ", e);
							e.printStackTrace();
						}
					}

				  logger.trace("notifies of moved item in shared folder is completed");

				  //DEBUG
//				  System.out.println("notifies of moved item in shared folder is completed");
			  }



		}.start();

	}


	/**
	 * Runs a new thread to notify the contacts passed in input.
	 *
	 * @param listContacts the list contacts
	 * @param itemName the item name
	 * @param sharedFolder the shared folder
	 */
	public void notifyRemovedItemToSharing(final List<InfoContactModel> listContacts, final String itemName, final WorkspaceSharedFolder sharedFolder) {


		new Thread() {
			  @Override
			  public void run() {

				  logger.trace("Sending notification remove item in shared folder is running...");
//				  printContacts(listContacts);

				  if(itemName==null || itemName.isEmpty()){
					  logger.trace("Notification isn't sent - itemName is null or empty");
					  return;
				  }

				  if(sharedFolder==null){
					  logger.trace("Notification isn't sent - sharedFolder is null");
				  }

				  logger.trace("Sending notification removed item in shared folder is running...");

//				  System.out.println("Sending notification removed item in shared folder is running...");

				  for (InfoContactModel infoContactModel : listContacts) {
						try{

							//NOTIFIES ONLY THE USERS THAT ARE DIFFERENT FROM CURRENT USER
							if(infoContactModel.getLogin().compareTo(userId)!=0){

								logger.trace("Sending notification  to user "+infoContactModel.getLogin() +" removed item "+itemName+" in shared folder "+sharedFolder.getName());

								//DEBUG
//								System.out.println("Sending notification  to user "+infoContactModel.getLogin() +" removed item "+itemName+" in shared folder "+sharedFolder.getName());

								boolean notify = notificationsMng.notifyRemovedItem(infoContactModel.getLogin(), itemName, sharedFolder);

								if(!notify){
									logger.error("An error occured when notify user: "+infoContactModel.getLogin());

									//DEBUG
//									System.out.println("An error occured when notify user: "+infoContactModel.getLogin());
								}
							}
						}catch (Exception e) {
							logger.error("An error occurred in notifyRemovedItemToSharing ", e);
//							e.printStackTrace();
						}
					}

				  logger.trace("notifies of moved item in shared folder is completed");

				  //DEBUG
//				  System.out.println("notifies of moved item in shared folder is completed");
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
				  logger.trace("Send notifies shared folder deleted is running...");

				  for (InfoContactModel infoContactModel : listContacts) {
						try{

							//NOTIFIES ONLY THE USERS THAT ARE DIFFERENT FROM CURRENT USER
							if(infoContactModel.getLogin().compareTo(userId)!=0){

								logger.trace("Sending notification to user "+infoContactModel.getLogin() +" deleted shared folder "+folderNameDeleted);

								//DEBUG
								System.out.println("Sending notification to user "+infoContactModel.getLogin() +" deleted shared folder "+folderNameDeleted);

								//TODO
//								boolean notify = notificationsMng.

//								if(!notify)
//									logger.error("An error occured when notifies user: "+infoContactModel.getLogin());
							}
						}catch (Exception e) {
							logger.error("An error occured in notifySharedFolderDeleted ", e);
//							e.printStackTrace();
						}
					}

				  logger.trace("notifies of deleted shared foder is completed");
			  }

		}.start();

	}


	/**
	 * Runs a new thread to notify the contacts passed in input.
	 *
	 * @param userToNotify the user to notify
	 * @param sharedFolder the shared folder
	 */
	public void notifyAdministratorUpgrade(final InfoContactModel userToNotify, final WorkspaceSharedFolder sharedFolder){

		new Thread() {
			  @Override
			  public void run() {

//				  printContacts(listContacts);
				  logger.trace("Send notifies administrator upgrade is running...");
				  if(userToNotify==null || userToNotify.getLogin()==null){
					  logger.warn("Notification abort user to notify is null...");
					  return;
				  }

				  if(sharedFolder==null){
					  logger.warn("Notification abort sharedFolder to notify is null...");
					  return;
				  }
				  try{
					  String login = userToNotify.getLogin();
					  logger.trace("Send notifies administrator upgrade for login: "+login);

					  boolean notify = notificationsMng.notifyAdministratorUpgrade(login, sharedFolder);
					  logger.trace("Notification sent correctly? "+notify);
				  }catch (Exception e) {
						logger.error("An error occured in notifyAdministratorUpgrade ", e);
				  }


				  logger.trace("notifies of administrator upgrade completed");
			  }

		}.start();

	}

	/**
	 * Runs a new thread to notify the contacts passed in input.
	 *
	 * @param userToNotify the user to notify
	 * @param sharedFolder the shared folder
	 */
	public void notifyAdministratorDowngrade(final InfoContactModel userToNotify, final WorkspaceSharedFolder sharedFolder){

		new Thread() {
			  @Override
			  public void run() {

//				  printContacts(listContacts);
				  logger.trace("Send notifies administrator Downgrade is running...");
				  if(userToNotify==null || userToNotify.getLogin()==null){
					  logger.warn("Notification abort user to notify is null...");
					  return;
				  }

				  if(sharedFolder==null){
					  logger.warn("Notification abort sharedFolder to notify is null...");
					  return;
				  }
				  try{
					  String login = userToNotify.getLogin();
					  logger.trace("Send notifies administrator Downgrade for login: "+login);

					  boolean notify = notificationsMng.notifyAdministratorDowngrade(login, sharedFolder);
					  logger.trace("Notification sent correctly? "+notify);
				  }catch (Exception e) {
						logger.error("An error occured in notifyAdministratorDowngrade ", e);
				  }


				  logger.trace("notifies of administrator Downgrade completed");
			  }

		}.start();

	}

	//DEBUG
	/**
	 * Prints the contacts.
	 *
	 * @param listContacts the list contacts
	 */
	private void printContacts(List<InfoContactModel> listContacts){

		System.out.println("Print contacts");
		for (InfoContactModel infoContactModel : listContacts) {
			System.out.println(infoContactModel);
		}
		System.out.println("End print contacts");
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	/*public static void main(String[] args) throws Exception
	{
		String sessionID = "1";
		String user = "francesco.mangiacrapa";
		String scopeString = "/gcube/devsec/devVRE";
		String fullName = "Francesco Mangiacrapa";

		ScopeBean scope;
		ASLSession session;

		session = SessionManager.getInstance().getASLSession(sessionID, user);
		scope = new ScopeBean(scopeString);
		session.setScope(scope.toString());
		session.setUserAvatarId(user + "Avatar");
		session.setUserFullName(fullName);


		NotificationsProducer feeder = new NotificationsProducer(session);

	}*/

}
