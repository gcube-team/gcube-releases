package org.gcube.gcat.persistence.ckan;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class CKANGroup extends CKAN {
	
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.group_list
	public static final String GROUP_LIST = CKAN.CKAN_API_PATH + "group_list";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.create.group_create
	public static final String GROUP_CREATE = CKAN.CKAN_API_PATH + "group_create";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.group_show
	public static final String GROUP_SHOW = CKAN.CKAN_API_PATH + "group_show";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.update.group_update
	public static final String GROUP_UPDATE = CKAN.CKAN_API_PATH + "group_update";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.patch.group_patch
	public static final String GROUP_PATCH = CKAN.CKAN_API_PATH + "group_patch";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.delete.group_delete
	public static final String GROUP_DELETE = CKAN.CKAN_API_PATH + "group_delete";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.delete.group_purge
	public static final String GROUP_PURGE = CKAN.CKAN_API_PATH + "group_purge";
	
	public CKANGroup() {
		super();
		LIST = GROUP_LIST;
		CREATE = GROUP_CREATE;
		READ = GROUP_SHOW;
		UPDATE = GROUP_UPDATE;
		PATCH = GROUP_PATCH;
		DELETE = GROUP_DELETE;
		PURGE = GROUP_PURGE;
	}
	
}
