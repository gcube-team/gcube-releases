package org.gcube.datacatalogue.catalogue.utils;

/**
 * Constants used within this service.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class Constants {
	
	// methods
	public final static String SHOW_METHOD = "show";
	public final static String CREATE_METHOD = "create";
	public final static String UPDATE_METHOD = "update";
	public final static String PURGE_METHOD = "purge";
	public final static String DELETE_METHOD = "delete";
	public final static String PATCH_METHOD = "patch";
	public static final String LIST_METHOD = "list";
	
	// this service's resources
	public final static String USERS = "api/users";
	public final static String ORGANIZATIONS = "api/organizations";
	public final static String GROUPS = "api/groups";
	public final static String ITEMS = "api/items";
	public static final String PROFILES = "api/profiles";
	public final static String RESOURCES = "api/resources";
	public final static String LICENSES = "api/licenses";
	
	// api rest path CKAN
	public final static String CKAN_API_PATH = "/api/3/action/";
	
	// ckan header authorization 
	public final static String AUTH_CKAN_HEADER = "Authorization";
	
	// ckan Groups subpaths methods (to be appended to the catalogue url)
	public final static String GROUP_SHOW = CKAN_API_PATH + "group_show?";
	public final static String GROUP_CREATE = CKAN_API_PATH + "group_create";
	public final static String GROUP_DELETE = CKAN_API_PATH + "group_delete";
	public final static String GROUP_PURGE = CKAN_API_PATH + "group_purge";
	public final static String GROUP_UPDATE = CKAN_API_PATH + "group_patch";
	public final static String GROUP_PATCH = CKAN_API_PATH + "group_patch";
	public final static String GROUP_LIST = CKAN_API_PATH + "group_list";
	
	// ckan Organizations subpaths methods (to be appended to the catalogue url)
	public final static String ORGANIZATION_SHOW = CKAN_API_PATH + "organization_show?";
	public final static String ORGANIZATION_CREATE = CKAN_API_PATH + "organization_create";
	public final static String ORGANIZATION_DELETE = CKAN_API_PATH + "organization_delete";
	public final static String ORGANIZATION_PURGE = CKAN_API_PATH + "organization_purge";
	public final static String ORGANIZATION_PATCH = CKAN_API_PATH + "organization_patch";
	public final static String ORGANIZATION_UPDATE = CKAN_API_PATH + "organization_update";
	public final static String ORGANIZATION_LIST = CKAN_API_PATH + "organization_list";
	
	// ckan User subpaths methods (to be appended to the catalogue url)
	public final static String USER_SHOW = CKAN_API_PATH + "user_show?";
	public final static String USER_CREATE = CKAN_API_PATH + "user_create";
	public final static String USER_DELETE = CKAN_API_PATH + "user_delete";
	public final static String USER_UPDATE = CKAN_API_PATH + "user_update";
	
	// ckan Dataset/package subpath methods (to be appended to the catalogue url)
	public final static String ITEM_SHOW = CKAN_API_PATH + "package_show?";
	public final static String ITEM_CREATE = CKAN_API_PATH + "package_create";
	public final static String ITEM_DELETE = CKAN_API_PATH + "package_delete";
	public final static String ITEM_PURGE = CKAN_API_PATH + "dataset_purge";
	public final static String ITEM_PATCH = CKAN_API_PATH + "package_patch";
	public final static String ITEM_UPDATE = CKAN_API_PATH + "package_update";
	
	// ckan Resource subpath methods (to be appended to the catalogue url)
	public final static String RESOURCE_SHOW = CKAN_API_PATH + "resource_show?";
	public final static String RESOURCE_CREATE = CKAN_API_PATH + "resource_create";
	public final static String RESOURCE_DELETE = CKAN_API_PATH + "resource_delete";
	public final static String RESOURCE_UPDATE = CKAN_API_PATH + "resource_update";
	public final static String RESOURCE_PATCH = CKAN_API_PATH + "resource_patch";
	
	// licenses
	public final static String LICENSES_SHOW = CKAN_API_PATH + "license_list?";
	
	// other capabilities of this services related to gCube Profiles
	public static final String PROFILES_NAMES_SHOW = "profile_names/";
	public static final String PROFILE_SHOW = "profile/";
	public static final String NAMESPACES_SHOW = "namespaces/";

}
