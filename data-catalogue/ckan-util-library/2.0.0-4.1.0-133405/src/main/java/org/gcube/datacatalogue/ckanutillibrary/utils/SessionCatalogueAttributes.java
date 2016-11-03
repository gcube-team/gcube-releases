package org.gcube.datacatalogue.ckanutillibrary.utils;

/**
 * A list of attributes that are saved into http session.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class SessionCatalogueAttributes {

	// CKAN KEYS (PLEASE NOTE THAT MOST OF THESE INFO ARE SAVED INTO SESSION PER SCOPE)
	public static final String CKAN_ORGS_USER_KEY = "ckanOrgs"; // organizations to whom he belongs
	public static final String CKAN_HIGHEST_ROLE = "ckanHighestRole"; // editor, member, admin 
	public static final String CKAN_ORGANIZATIONS_PUBLISH_KEY = "ckanOrganizationsPublish"; // here he can publish (admin/editor role)
	public final static String SCOPE_CLIENT_PORTLET_URL = "currentClientUrlPortletScope"; // scope in which we need to discover
	public static final String CKAN_TOKEN_KEY = "ckanToken"; // token key
	public static final String CKAN_LICENSES_KEY = "ckanLicenses"; // licenses
	public static final String CKAN_PROFILES_KEY = "ckanProfiles"; // product profiles
	public static final String CKAN_PUBLISH_WORKSPACE = "ckanCatalogueInWorkspace";

}
