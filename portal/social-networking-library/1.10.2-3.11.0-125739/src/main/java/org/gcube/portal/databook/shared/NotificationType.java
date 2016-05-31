package org.gcube.portal.databook.shared;



/**
 * @author Massimiliano Assante ISTI-CNR
 * 
 * TODO: Buggy if NotificationType for WP_* are refactored see DBCassandraAstyanaxImpl#getUserNotificationPreferences(String userid)
 * introduced due to urgent matters
 */
public enum NotificationType {
	/**
	 * use to notify a user he got a Tabular Resource shared
	 */
	TDM_TAB_RESOURCE_SHARE,
	/**
	 * use to notify a user he got a TDM Rule shared
	 */
	TDM_RULE_SHARE,
	/**
	 * use to notify a user he got a TDM Templated shared
	 */
	TDM_TEMPLATE_SHARE,
	/**
	 * use to notify a user he got a workspace folder shared
	 */
	WP_FOLDER_SHARE,
	/**
	 * use to notify a user that a user in the share unshared
	 */
	WP_FOLDER_UNSHARE,
	/**
	 * use to notify a user that he got upgraded to administrator of a shared folder
	 */
	WP_ADMIN_UPGRADE,
	/**
	 * use to notify a user that he got downgraded from administrator of a shared folder
	 */
	WP_ADMIN_DOWNGRADE,
	/**
	 * use to notify a user that a new user was added in on of his workspace shared folder
	 */
	WP_FOLDER_ADDEDUSER,
	/**
	 * use to notify a user that an existing user was removed from one of his workspace shared folder
	 */
	WP_FOLDER_REMOVEDUSER,
	/**
	 * use to notify a user he got a workspace folder renamed
	 */
	WP_FOLDER_RENAMED,
	/**
	 * use to notify a user he got a workspace item deleted from one of his workspace shared folder
	 */
	WP_ITEM_DELETE, 
	/**
	 * use to notify a user he got a workspace item updated from one of his workspace shared folder
	 */
	WP_ITEM_UPDATED, 
	/**
	 * use to notify a user he got a workspace item renamed from one of his workspace shared folder
	 */
	WP_ITEM_RENAMED, 
	/**
	 * use to notify a user he got a workspace item new in some of his workspace shared folder
	 */
	WP_ITEM_NEW, 
	/**
	 * use to notify a user he got one of his feed commented 
	 */
	OWN_COMMENT,
	/**
	 * use to notify a user that commented on a feed (Not his) that someone commented too 
	 */
	COMMENT, 
	/**
	 * use to notify a user that he got mentioned in one post 
	 */
	MENTION, 
	/**
	 * use to notify a user he got one of his feed liked 
	 */
	LIKE, 
	/**
	 * use to notify a user he got a message
	 */
	MESSAGE,
	/**
	 * use to notify every user of a VRE/Group that the post was made
	 */
	POST_ALERT,
	/**
	 * use to notify a user that someone in his VRE created a new Event in the Calendar
	 */
	CALENDAR_ADDED_EVENT,
	/**
	 * use to notify a user that someone in his VRE updated an Event in the Calendar
	 */
	CALENDAR_UPDATED_EVENT,
	/**
	 * use to notify a user that someone in his VRE deleted an Event in the Calendar
	 */
	CALENDAR_DELETED_EVENT,
	/**
	 * use to notify a user he got a connections request
	 */
	REQUEST_CONNECTION,
	/**
	 * use to notify a user he got a job completed ok
	 */
	JOB_COMPLETED_OK,
	/**
	 * use to notify a user he got a job completed not ok
	 */
	JOB_COMPLETED_NOK,
	/**
	 * use to notify a document workflow owner that someone 
	 * has edited a document involved in a worflow he created
	 */
	DOCUMENT_WORKFLOW_EDIT,
	/**
	 * use to notify a document workflow owner that someone 
	 * has viewed a document involved in a worflow he created
	 */
	DOCUMENT_WORKFLOW_VIEW,
	/**
	 * use to notify a document workflow user (user that in the same document workflow) 
	 * that forwarded to a step where he is requested to do a task
	 */
	DOCUMENT_WORKFLOW_STEP_REQUEST_TASK,
	/**
	 * use to notify a document workflow user that he was involved into a new Document Workflow
	 * and he is requested to do a task
	 */
	DOCUMENT_WORKFLOW_FIRST_STEP_REQUEST_INVOLVMENT,
	/**
	 * use to notify a document workflow owner that a user performed a forward action to another step a document worflow he created
	 */
	DOCUMENT_WORKFLOW_USER_FORWARD_TO_OWNER,
	/**
	 * use to notify a document workflow owner that someone 
	 * forwarded and the workflow moved to another step a document worflow he created
	 */
	DOCUMENT_WORKFLOW_FORWARD_STEP_COMPLETED_OWNER,
	/**
	 * use to notify a document workflow peer (user that in the same step has your same role) 
	 * that someone performed a forward action to another step in a document worflow he is involved into
	 */
	DOCUMENT_WORKFLOW_STEP_FORWARD_PEER,
	/**
	 * generic notification
	 */
	GENERIC;
}
