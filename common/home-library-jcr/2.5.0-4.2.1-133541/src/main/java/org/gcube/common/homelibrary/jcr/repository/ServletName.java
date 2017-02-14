package org.gcube.common.homelibrary.jcr.repository;

public class ServletName {

	public static final String GCUBE_TOKEN 				= "gcube-token";
	public static final String GCUBE_SCOPE 				= "gcube-scope";
	

	
	//privilege manager
	public static final String CREATE_PRIVILEGE 		= "CreateCostumePrivilegeServlet";

	//user manager
	public static final String PORTAL_USER_MANAGER 		= "PortalUserManager";
	
	public static final String CREATE_USER 				= "CreateUserServlet";
	public static final String LIST_USERS 				= "ListUsersServlet";
	public static final String LIST_GROUPS 				= "ListGroupsServlet";
	public static final String CREATE_GROUP 			= "CreateGroupServlet";
	public static final String UPDATE_GROUP 			= "UpdateGroupServlet";
	public static final String IS_GROUP 				= "IsGroupServlet";
	public static final String GROUP_MEMBERSHIP 		= "GroupMembershipServlet";
	public static final String DELETE_USER 				= "DeleteAuthorizableServlet";

	public static final String GET_DISPLAY_NAME 		= "GetDisplayNameServlet";
	public static final String SET_DISPLAY_NAME 		= "SetDisplayNameServlet";

	public static final String GET_VERSION 				= "GetVersionServlet";
	public static final String SET_VERSION 				= "SetVersionServlet";

	//workspace servlets get
	public static final String GET_CHILDREN_BY_ID 		= "GetChildrenById";
	public static final String GET_PARENT_BY_ID 		= "GetParentById";
	public static final String GET_PARENTS_BY_ID 		= "GetParentsById";
	public static final String GET_ITEM_BY_ID 			= "GetItemById";
	public static final String GET_ITEM_BY_PATH 		= "GetItemByPath";
	public static final String GET_HIDDEN_ITEMS_BY_ID 	= "GetHiddenItemsById";

	//workspace servlets post
	public static final String UPLOAD 					= "Upload";
	public static final String UPLOAD_FILE 				= "UploadFile";
	public static final String GET_REFERENCES 			= "GetReferences";
	public static final String REMOVE_ITEM 				= "RemoveItem";
	public static final String ADD_NODE 				= "AddNode";
	public static final String REMOVE_DATA				= "RemoveData";
	public static final String DELETE 					= "Delete";
	public static final String LIST_FOLDER 				= "ListFolder";
	public static final String COPY 					= "Copy";
	public static final String MOVE_TO_TRASH_IDS 		= "MoveToTrashIds";
	public static final String CREATE_REFERENCE			= "CreateReference";
	public static final String SEARCH_ITEMS 			= "SearchItems";
	public static final String EXECUTE_QUERY 			= "ExecuteQuery";
	public static final String SAVE_ITEM 				= "SaveItem";
	public static final String MOVE 					= "Move";
	public static final String CLONE 					= "Clone";
	public static final String COPY_CONTENT 			= "CopyContent";
	public static final String CHANGE_PRIMARY_TYPE 		= "ChangePrimaryType";
	public static final String CREATE_FOLDER 			= "CreateFolder";

	//ACL
	public static final String GET_EACL 				= "GetEACL";
	public static final String GET_ACL 					= "GetACL";
	public static final String GET_ACL_BY_USER 			= "GetACLByUser";
	public static final String GET_DENIED_MAP 			= "GetDeniedMap";
	public static final String DELETE_ACL 				= "DeleteAcesServlet";
	public static final String MODIFY_ACL 				= "ModifyAceServlet";

	//check ACL
	public static final String CAN_READ 				= "CanReadNode";
	public static final String CAN_ADD_CHILDREN 		= "CanAddChildren";
	public static final String CAN_MODIFY 				= "CanModifyProperties";
	public static final String CAN_DELETE 				= "CanDelete";
	public static final String CAN_DELETE_CHILDREN 		= "CanDeleteChildren";

	//session
	public static final String CREATE_SESSION 			= "CreateSession";
	public static final String RELEASE_SESSION 			= "ReleaseSession";
	public static final String ACTIVE_SESSIONS 			= "ActiveSessions";
	
	//lock
	public static final String LOCK_SESSION 			= "LockSession";
	public static final String UNLOCK_SESSION 			= "UnlockSession";
	public static final String IS_LOCKED 				= "IsLocked";
	
	//accounting
	public static final String SAVE_ACCOUNTING 			= "SaveAccountingItem";
	public static final String GET_ACCOUNTING_BY_ID 	= "GetAccountingById";
	
}
