package org.gcube.datacatalogue.ckanutillibrary.server.utils;

/**
 * A list of attributes that are saved into http session.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class SessionCatalogueAttributes {

	// CKAN KEYS (PLEASE NOTE THAT MOST OF THESE INFO ARE SAVED INTO SESSION PER SCOPE)
	public static final String CKAN_ORGS_USER_KEY = "ckanOrgs"; // organizations to whom he belongs (shown into the portlet)
	public static final String CKAN_HIGHEST_ROLE = "ckanHighestRole"; // editor, member, admin 
	public static final String CKAN_ORGANIZATIONS_PUBLISH_KEY = "ckanOrganizationsPublish"; // here he can publish (admin/editor role)
	public static final String CKAN_LICENSES_KEY = "ckanLicenses"; // licenses
	public static final String CKAN_PROFILES_KEY = "ckanProfiles"; // product profiles
	public static final String CKAN_GROUPS_MEMBER = "ckanGroupsMember";
	public static final String CKAN_GROUPS_USER_KEY = "ckanGroups"; // to show the list of groups in the portlet
}
