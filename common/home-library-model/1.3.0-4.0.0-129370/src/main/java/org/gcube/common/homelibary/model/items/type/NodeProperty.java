package org.gcube.common.homelibary.model.items.type;

public enum NodeProperty {

	//nthl:workspaceItem
	TITLE {
		@Override
		public String toString() {
			return "jcr:title";
		}
	},
	CREATED {
		@Override
		public String toString() {
			return "jcr:created";
		}
	},
	LAST_MODIFIED {
		@Override
		public String toString() {
			return "jcr:lastModified";
		}
	},
	LAST_ACTION {
		@Override
		public String toString() {
			return "hl:lastAction";
		}
	},

	OWNER {
		@Override
		public String toString() {
			return "hl:owner";
		}
	},
	PORTAL_LOGIN {
		@Override
		public String toString() {
			return "hl:portalLogin";
		}
	},

	LAST_MODIFIED_BY {
		@Override
		public String toString() {
			return "jcr:lastModifiedBy";
		}
	},
	DESCRIPTION {
		@Override
		public String toString() {
			return "jcr:description";
		}
	},
	USER_ID {
		@Override
		public String toString() {
			return "hl:uuid";
		}
	},


	//	//nthl:workspaceSharedItem

	IS_VRE_FOLDER {
		@Override
		public String toString() {
			return "hl:isVreFolder";
		}
	},
	DISPLAY_NAME {
		@Override
		public String toString() {
			return "hl:displayName";
		}
	},
	USERS {
		@Override
		public String toString() {
			return "hl:users";
		}
	},
	MEMBERS {
		@Override
		public String toString() {
			return "hl:members";
		}
	},

	//	file

	CONTENT {
		@Override
		public String toString() {
			return "jcr:content";
		}
	},
	FOLDER_ITEM_TYPE {
		@Override
		public String toString() {
			return "hl:workspaceItemType";
		}
	},
	MIME_TYPE {
		@Override
		public String toString() {
			return "jcr:mimeType";
		}
	},
	DATA {
		@Override
		public String toString() {
			return "jcr:data";
		}
	},
	SIZE {
		@Override
		public String toString() {
			return "hl:size";
		}
	},
	REMOTE_STORAGE_PATH {
		@Override
		public String toString() {
			return "hl:remotePath";
		}
	},
	STORAGE_ID {
		@Override
		public String toString() {
			return "hl:storageId";
		}
	},	
	STORAGE_PATH {
		@Override
		public String toString() {
			return "hl:storagePath";
		}
	},

	//	image

	IMAGE_WIDTH {
		@Override
		public String toString() {
			return "hl:width";
		}
	},
	IMAGE_HEIGHT {
		@Override
		public String toString() {
			return "hl:height";
		}
	},
	THUMBNAIL_DATA {
		@Override
		public String toString() {
			return "hl:thumbnailData";
		}
	},
	THUMBNAIL_WIDTH {
		@Override
		public String toString() {
			return "hl:thumbnailWidth";
		}
	},
	THUMBNAIL_HEIGHT {
		@Override
		public String toString() {
			return "hl:thumbnailHeight";
		}
	},


	//pdf	

	AUTHOR {
		@Override
		public String toString() {
			return "hl:author";
		}
	},
	NUMBER_OF_PAGES {
		@Override
		public String toString() {
			return "hl:numberOfPages";
		}
	},
	VERSION {
		@Override
		public String toString() {
			return "hl:version";
		}
	},
	PDF_TITLE {
		@Override
		public String toString() {
			return "hl:title";
		}
	},
	PRODUCER {
		@Override
		public String toString() {
			return "hl:producer";
		}
	},

	//	gcubeItem	
	SCOPES {
		@Override
		public String toString() {
			return "hl:scopes";
		}
	},
	CREATOR {
		@Override
		public String toString() {
			return "hl:creator";
		}
	},
	ITEM_TYPE {
		@Override
		public String toString() {
			return "hl:itemType";
		}
	},
	PROPERTIES {
		@Override
		public String toString() {
			return "hl:properties";
		}
	},
	PROPERTY {
		@Override
		public String toString() {
			return "hl:property";
		}
	},
	IS_SHARED {
		@Override
		public String toString() {
			return "hl:isShared";
		}
	},
	SHARED_ROOT_ID {
		@Override
		public String toString() {
			return "hl:sharedRootId";
		}
	},


	//	trashItem
	DELETE_DATE {
		@Override
		public String toString() {
			return "hl:deletedTime";
		}
	},
	DELETE_BY {
		@Override
		public String toString() {
			return "hl:deletedBy";
		}
	},
	ORIGINAL_PARENT_ID {
		@Override
		public String toString() {
			return "hl:originalParentId";
		}
	},
	DELETED_FROM {
		@Override
		public String toString() {
			return "hl:deletedFrom";
		}
	},
	TRASH_ITEM_NAME {
		@Override
		public String toString() {
			return "hl:name";
		}
	},
	TRASH_ITEM_MIME_TYPE {
		@Override
		public String toString() {
			return "hl:mimeType";
		}
	},
	LENGTH {
		@Override
		public String toString() {
			return "hl:length";
		}
	},	
	IS_FOLDER {
		@Override
		public String toString() {
			return "hl:isFolder";
		}
	},

	//	messages
	SUBJECT {
		@Override
		public String toString() {
			return "hl:subject";
		}
	},
	BODY {
		@Override
		public String toString() {
			return "hl:body";
		}
	},
	ATTACHMENTS {
		@Override
		public String toString() {
			return "hl:attachments";
		}
	},
	ATTACHMENTS_ID {
		@Override
		public String toString() {
			return "hl:attachId";
		}
	},
	ADDRESSES {
		@Override
		public String toString() {
			return "hl:addresses";
		}
	},
	SCOPE {
		@Override
		public String toString() {
			return "hl:scope";
		}
	},
	READ {
		@Override
		public String toString() {
			return "hl:read";
		}
	},
	OPEN {
		@Override
		public String toString() {
			return "hl:open";
		}
	},
	NT_USER {
		@Override
		public String toString() {
			return "nthl:user";
		}
	},

	//	timeseries

	TIMESERIES_ID {
		@Override
		public String toString() {
			return "hl:id";
		}
	},
	TIMESERIES_TITLE {
		@Override
		public String toString() {
			return "hl:title";
		}
	},
	TIMESERIES_DESCRIPTION {
		@Override
		public String toString() {
			return "hl:description";
		}
	},
	TIMESERIES_CREATOR {
		@Override
		public String toString() {
			return "hl:creator";
		}
	},
	TIMESERIES_CREATED {
		@Override
		public String toString() {
			return "hl:created";
		}
	},
	TIMESERIES_PUBLISHER {
		@Override
		public String toString() {
			return "hl:publisher";
		}
	},
	TIMESERIES_SOURCE_ID {
		@Override
		public String toString() {
			return "hl:sourceId";
		}
	},
	TIMESERIES_SOURCE_NAME {
		@Override
		public String toString() {
			return "hl:sourceName";
		}
	},
	TIMESERIES_RIGHTS {
		@Override
		public String toString() {
			return "hl:rights";
		}
	},
	TIMESERIES_DIMENSION {
		@Override
		public String toString() {
			return "hl:dimension";
		}
	},
	HEADER_LABELS {
		@Override
		public String toString() {
			return "hl:headerLabels";
		}
	},

	//	query

	QUERY {
		@Override
		public String toString() {
			return "hl:query";
		}
	},
	FOLDER_ID {
		@Override
		public String toString() {
			return "hl:folderId";
		}
	},
	QUERY_TYPE {
		@Override
		public String toString() {
			return "hl:queryType";
		}
	},

	//report template

	RT_CREATED {
		@Override
		public String toString() {
			return "hl:created";
		}
	},
	LAST_EDIT {
		@Override
		public String toString() {
			return "hl:lastEdit";
		}
	},
	LAST_EDIT_BY {
		@Override
		public String toString() {
			return "hl:lastEditBy";
		}
	},
	NUMBER_OF_SECTION {
		@Override
		public String toString() {
			return "hl:numberOfSection";
		}
	},
	STATUS {
		@Override
		public String toString() {
			return "hl:status";
		}
	},
	FAILURES {
		@Override
		public String toString() {
			return "hl:failures";
		}
	},
	TEMPLATE_NAME {
		@Override
		public String toString() {
			return "hl:templateName";
		}
	}, 

	//metadata

	SCHEMA{
		@Override
		public String toString() {
			return "hl:schema";
		}
	}, 
	LANGUAGE{
		@Override
		public String toString() {
			return "hl:language";
		}
	}, 
	COLLECTION_NAME{
		@Override
		public String toString() {
			return "hl:collectionName";
		}
	}, 
	OID{
		@Override
		public String toString() {
			return "hl:oid";
		}
	}, 

	NT_CONTENT{
		@Override
		public String toString() {
			return "nthl:metadataItemContent";

		}
	},

	//document

	METADATA{
		@Override
		public String toString() {
			return "hl:metadata";
		}
	},
	ANNOTATIONS{
		@Override
		public String toString() {
			return "hl:annotations";
		}
	},


	ALTERNATIVES {
		@Override
		public String toString() {
			return "hl:alternatives";
		}
	},	

	PARENT_URI{
		@Override
		public String toString() {
			return  "hl:parentUri";
		}
	},
	URI{
		@Override
		public String toString() {
			return  "hl:uri";
		}
	},
	NAME{
		@Override
		public String toString() {
			return  "hl:name";
		}
	},
	HL_MIME_TYPE{
		@Override
		public String toString() {
			return  "hl:mimeType";
		}
	},

	NT_ALTERNATIVE{
		@Override
		public String toString() {
			return "nthl:documentAlternativeLink";
		}
	},
	NT_PART	{
		@Override
		public String toString() {
			return "nthl:documentPartLink";
		}
	},

	PARTS{
		@Override
		public String toString() {
			return  "hl:parts";
		}
	},

	URL{
		@Override
		public String toString() {
			return  "hl:url";
		}
	},


	//accounting
	ACCOUNTING{
		@Override
		public String toString() {
			return "hl:accounting";
		}
	},
	NT_ACCOUNTING{
		@Override
		public String toString() {
			return "nthl:accountingSet";
		}
	},

	//workflow
	WORKFLOW_ID{
		@Override
		public String toString() {
			return "hl:workflowId";
		}
	},
	WORKFLOW_DATA{
		@Override
		public String toString() {
			return "hl:workflowData";
		}
	},
	WORKFLOW_STATUS{
		@Override
		public String toString() {
			return "hl:workflowStatus";
		}
	}, 
	REFERENCE{
		@Override
		public String toString() {
			return "hl:reference";
		}
	}, 
	HIDDEN{
		@Override
		public String toString() {
			return "hl:hidden";
		}
	}, 
	GROUP_ID{
		@Override
		public String toString() {
			return "hl:groupId";
		}
	}, 
	IS_SYSTEM_FOLDER{
		@Override
		public String toString() {
			return "hl:IsSystemFolder";
		}
	}
}