package org.gcube.applicationsupportlayer.social;

import java.util.Date;
import java.util.List;

import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.portal.databook.shared.ApplicationProfile;
import org.gcube.portal.databook.shared.NotificationType;
import org.gcube.portal.databook.shared.RunningJob;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
public interface NotificationsManager {
	/**
	 * use to notify a user he got a workspace folder shared
	 *
	 * @param userIdToNotify the user you want to notify
	 * @param sharedFolder the shared {@link WorkspaceSharedFolder}  
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyFolderSharing(String userIdToNotify, WorkspaceSharedFolder sharedFolder) throws Exception;
	/**
	 * use to notify a user he got a workspace folder shared
	 *
	 * @param userIdToNotify the user you want to notify
	 * @param unsharedFolderId the unshared folder id
	 * @param unsharedFolderName the unshared folder name
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyFolderUnsharing(String userIdToNotify, String unsharedFolderId, String unsharedFolderName) throws Exception;
	/**
	 * use to notify a user he got upgraded to Administrator of a folder shared
	 *
	 * @param userIdToNotify the user you want to notify
	 * @param sharedFolder the shared {@link WorkspaceSharedFolder}  
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyAdministratorUpgrade(String userIdToNotify, WorkspaceSharedFolder sharedFolder) throws Exception;
	/**
	 * use to notify a user he got downgraded from Administrator of a folder shared
	 *
	 * @param userIdToNotify the user you want to notify
	 * @param sharedFolder the shared {@link WorkspaceSharedFolder}  
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyAdministratorDowngrade(String userIdToNotify, WorkspaceSharedFolder sharedFolder) throws Exception;
	/**
	 * use to notify a user he got a workspace folder renamed
	 *
	 * @param userIdToNotify the user you want to notify
	 * @param previousName the previous name of the folder
	 * @param newName the new name of the folder
	 * @param renamedFolderId the folderId
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyFolderRenaming(String userIdToNotify, String previousName, String newName, String renamedFolderId) throws Exception;
	/**
	 * use to notify a user that a new user was added in on of his workspace shared folder
	 *
	 * @param userIdToNotify the user you want to notify
	 * @param sharedFolder the shared {@link WorkspaceSharedFolder}  
	 * @param newAddedUserId the new user that was added
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyFolderAddedUser(String userIdToNotify, WorkspaceSharedFolder sharedFolder, String newAddedUserId) throws Exception;
	/**
	 * use to notify a user that a new user was added in on of his workspace shared folder
	 *
	 * @param userIdToNotify the user you want to notify
	 * @param sharedFolder the shared {@link WorkspaceSharedFolder}  
	 * @param newAddedUserIds List of new users that were added
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyFolderAddedUsers(String userIdToNotify, WorkspaceSharedFolder sharedFolder, List<String> newAddedUserIds) throws Exception;
	/**
	 * use to notify a user that an existing user was removed from one of his workspace shared folder
	 *
	 * @param userIdToNotify the user you want to notify
	 * @param sharedFolder the shared {@link WorkspaceSharedFolder}  
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyFolderRemovedUser(String userIdToNotify, WorkspaceSharedFolder sharedFolder) throws Exception;
	/**
	 * use to notify a user he got a workspace item new in some of his workspace shared folder
	 * @param userIdToNotify the user you want to notify
	 * @param newItem the new shared {@link WorkspaceItem}  
	 * @param sharedFolder the shared folder {@link WorkspaceSharedFolder}  
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyAddedItem(String userIdToNotify, WorkspaceItem item, WorkspaceSharedFolder sharedFolder) throws Exception;
	/**
	 * use to notify a user he got a workspace item deleted from one of his workspace shared folder
	 * @param userIdToNotify the user you want to notify
	 * @param removedItem the removed {@link WorkspaceItem}  
	 * @param sharedFolder the shared folder {@link WorkspaceSharedFolder} 
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyMovedItem(String userIdToNotify, WorkspaceItem item, WorkspaceSharedFolder sharedFolder) throws Exception;
	/**
	 * use to notify a user he got a workspace item deleted from one of his workspace shared folder
	 * @param userIdToNotify the user you want to notify
	 * @param removedItem the removed {@link WorkspaceItem}  
	 * @param sharedFolder the shared folder {@link WorkspaceSharedFolder} 
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyRemovedItem(String userIdToNotify, String item, WorkspaceSharedFolder sharedFolder) throws Exception;
	
	/**
	 * use to notify a user he got a workspace item updated from one of his workspace shared folder
	 * @param userIdToNotify the user you want to notify
	 * @param updatedItem the updated shared {@link WorkspaceItem}  
	 * @param sharedFolder the shared folder {@link WorkspaceFolder} 
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyUpdatedItem(String userIdToNotify, WorkspaceItem item, WorkspaceSharedFolder sharedFolder) throws Exception;
	/**
	 * use to notify a user he got a workspace item renamed
	 *
	 * @param userIdToNotify the user you want to notify
	 * @param previousName the previous name of the folder
	 * @param renamedItem the renamed {@link WorkspaceItem}
	 * @param rootSharedFolder the root shared {@link WorkspaceSharedFolder} of the {@link WorkspaceItem}  
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyItemRenaming(String userIdToNotify, String previousName, WorkspaceItem renamedItem,  WorkspaceSharedFolder rootSharedFolder) throws Exception;
	/**
	 * 
	 * @param userIdToNotify the user you want to notify
	 * @param messageUniqueIdentifier the unique identifier of the message
	 * @param subject the subject of the message sent
	 * @param messageText the text of the message (text/plain)
	 * @param otherRecipientsFullNames the Full Names of the other recipients. if any
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyMessageReceived(String userIdToNotify, String messageUniqueIdentifier, String subject, String messageText, String ... otherRecipientsFullNames);
	/**
	 * 
	 * @param userIdToNotify the user you want to notify
	 * @param eventTitle the title of the event
	 * @param eventType the type of the event
	 * @param startDate staring date
	 * @param endingDate ending date
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyNewCalendarEvent(String userIdToNotify, String eventTitle, String eventType, Date startDate, Date endingDate);
	/**
	 * 
	 * @param userIdToNotify the user you want to notify
	 * @param eventTitle the title of the event
	 * @param eventType the type of the event
	 * @param startDate staring date
	 * @param endingDate ending date
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyEditedCalendarEvent(String userIdToNotify, String eventTitle, String eventType, Date startDate, Date endingDate);
	/**
	 * 
	 * @param userIdToNotify the user you want to notify
	 * @param eventTitle the title of the event
	 * @param eventType the type of the event
	 * @param startDate staring date
	 * @param endingDate ending date
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyDeletedCalendarEvent(String userIdToNotify, String eventTitle, String eventType, Date startDate, Date endingDate);
	/**
	 * use to notify a user that someone created this post
	 * 
	 * @param userIdToNotify the user you want to notify
	 * @param feedid the liked feedid 
	 * @param feedText the liked feed text or a portion of it
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyPost(String userIdToNotify, String feedid, String feedText, String ... hashtags);
	/**
	 * use to notify a user that someone commented on his post
	 * 
	 * @param userIdToNotify the user you want to notify
	 * @param feedid the liked feedid 
	 * @param feedText the liked feed text or a portion of it
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyOwnCommentReply(String userIdToNotify, String feedid, String feedText);
	/**
	 * use to notify a user that commented on a post (Not his) that someone commented too 
	 * 
	 * @param userIdToNotify the user you want to notify
	 * @param feedid the liked feedid 
	 * @param feedText the liked feed text or a portion of it
	 * @param feedOwnerFullName the full name of the user who created this post
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyCommentReply(String userIdToNotify, String feedid, String feedText, String feedOwnerFullName, String feedOwnerId);
	/**
	 * use to notify a user that someone commented on one of his favorite posts 
	 * 
	 * @param userIdToNotify the user you want to notify
	 * @param feedid the liked feedid 
	 * @param commentText the commentText
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyCommentOnFavorite(String userIdToNotify, String feedid, String commentText);
	/**
	 * use to notify a user that he was mentioned (tagged) on a post
	 * 
	 * @param userIdToNotify the user you want to notify
	 * @param feedid the liked feedid 
	 * @param feedText the liked feed text or a portion of it
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyUserTag(String userIdToNotify, String feedid, String commentText);
	/**
	 * use to notify a user he got one of his post liked
	 *  
	 * @param userIdToNotify the user you want to notify
	 * @param feedid the liked feedid 
	 * @param feedText the liked feed text or a portion of it
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyLikedFeed(String userIdToNotify, String feedid, String feedText);
	/**
	 * use to notify a user he got one of his job finished
	 *  
	 * @param userIdToNotify the user you want to notify
	 * @param executingApp the {@link ApplicationProfile} of the application from which the job was executed/lauched 
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyJobStatus(String userIdToNotify, ApplicationProfile executingJobApId, RunningJob job);
	/**
	 * use to notify a document workflow owner that someone 
	 * has viewed a document involved in a worflow he created
	 * @param userIdToNotify the user you want to notify
	 * @param documentName the document title
	 * @param documentid the id of the document workflow
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyDocumentWorkflowView(String userIdToNotify, String documentWorkflowId, String documentName);
	/**
	 * use to notify a document workflow owner that someone 
	 * has updated a document involved in a worflow he created
	 * @param userIdToNotify the user you want to notify
	 * @param documentName the document title
	 * @param documentid the id of the document workflow
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyDocumentWorkflowUpdate(String userIdToNotify, String documentWorkflowId, String documentName);
	/**
	 * use to notify a document workflow user (user that in the same document workflow) 
	 * that he is requested to do a task
	 * @param userIdToNotify the user you want to notify
	 * @param documentName the document title
	 * @param documentid the id of the document workflow
	 * @param isStartStep set to true is the document worflow was just created
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyDocumentWorkflowTaskRequest(String userIdToNotify, String documentWorkflowId, String documentName, String assignedRole);
	/**
	 * use to notify a document workflow user that a new Document Workflow was created and he is involved in the first step
	 * and he is requested to do a task
	 * @param userIdToNotify the user you want to notify
	 * @param documentName the document title
	 * @param documentid the id of the document workflow
	 * @param isStartStep set to true is the document worflow was just created
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyDocumentWorkflowFirstStepRequest(String userIdToNotify, String documentWorkflowId, String documentName, String assignedRole);
	/**
	 * use to notify a document workflow owner that a user performed a forward action to another step a document worflow he created 
	 * @param userIdToNotify the owner of this document workflow
	 * @param documentWorkflowId
	 * @param documentName
	 * @param fromStepName the name of the step from which the user performed the forward
	 * @param toStepName the name of the step to which the user performed the forward
	 * @return  true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyDocumentWorkflowUserForward(String userIdToNotify, String documentWorkflowId, String documentName, String fromStepName, String toStepName);
	/**
	 * use to notify a document workflow owner that someone  forwarded and the workflow moved to another step a document worflow he created
	 * @param userIdToNotify the owner of this document workflow
	 * @param documentWorkflowId
	 * @param documentName
	 * @param fromStepName the name of the step from which the user performed the forward
	 * @param toStepName the name of the step to which the user performed the forward
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyDocumentWorkflowStepForwardComplete(String userIdToNotify, String documentWorkflowId, String documentName, String fromStepName, String toStepName);
	/**
	 * use to notify a user he got a Tabular Data Resource shared
	 *
	 * @param userIdToNotify the user you want to notify
	 * @param tabularResourceName the shared tabular resource name
	 * @param encodedTabularResourceParams the parameters to be placed in the HTTP GET Request (must be encoded)
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyTDMTabularResourceSharing(String userIdToNotify, String tabularResourceName, String encodedTabularResourceParams) throws Exception;
	/**
	 * use to notify a user he got a Tabular Data Resource shared
	 *
	 * @param userIdToNotify the user you want to notify
	 * @param type type of the shared tdm object (TDM Rule or TDM Template at the moment)
	 * @param tdmObjectName the name
	 * @param encodedTabularResourceParams the parameters to be placed in the HTTP GET Request (must be encoded)
	 * @return true if the notification is correctly delivered, false otherwise
	 */
	boolean notifyTDMObjectSharing(String userIdToNotify, NotificationType type, String tdmObjectName, String encodedTabularResourceParams) throws Exception;
	
}
