package org.gcube.portlets.user.performfish.util;

public class PFISHConstants {
	/**
	 * set to true if the private company repo should not appear in the workspace (portlet) of the company users
	 */
	public static final boolean HIDE_COMPANY_SHARED_FOLDER = true;
	/**
	 * 
	 */
	public static final String SET_COMPANY_ADMINISTRATOR_PORTLETID = "setcompanyadministratorssuperuser_WAR_PerformFISHAppsportlet";
	public static final String SET_FARM_ADMINISTRATOR_PORTLETID = "setfarmadministrators_WAR_PerformFISHAppsportlet";
	/**
	 * The URL (relative) of the Company Dashboard
	 */
	public static final String COMPANY_DASHBOARD_URL = "company-dashboard";
	/**
	 * The URL (relative) of the Company Farms Dashboard
	 */
	public static final String FARMS_DASHBOARD_URL = "farm-dashboard";
	/**
	 * The URL (relative) of the From submission pages
	 */
	public static final String HATCHERY_PAGE_LAYOUT_FRIENDLY_URL = "/hatchery";	
	public static final String PREGROW_PAGE_LAYOUT_FRIENDLY_URL = "/pre-grow";
	public static final String GROWOUT_PAGE_LAYOUT_FRIENDLY_URL = "/grow-out";
	/**
	 * This is the role a company Administrator should have to be interpreted as Administrator by this application
	 */
	public static final String COMPANY_ADMIN_SITE_ROLE = "Infrastructure-Manager"; 
	/**
	 * This is the role a company farm Administrator should have to be interpreted as Administrator by this application
	 */
	public static final String FARM_ADMIN_SITE_ROLE = "Data-Manager";
	/**
	 * These are the 2 coordinates for the Database containing the logic of the app 
	 */
	public static final String PF_DB_SERVICE_ENDPOINT_NAME = "PF_DB";
	public static final String PF_DB_SERVICE_ENDPOINT_CATEGORY = "Database";
	/**
	 * used to call the display of a user profile
	 */
	public static final String USER_PROFILE_OID = "userIdentificationParameter";
	/**
	 * this is the default role of the authorization service
	 */
	public final static String DEFAULT_ROLE = "OrganizationMember";	
	/**
	 * The hidden ws folder suffix automatically created by in the workspace for the compani repositories
	 */
	public static final String COMPANY_WS_FOLDER_SUFFIX = "_PerformFISH_Data";
	/**
	 * The name of the folder containing the logo of the companies 
	 */
	public static final String LOGO_FOLDER_NAME = "Logo";
	/**
	 * The attribute name for the portlet preference that indicates which phase is displaying
	 */
	public static final String PHASE_PREFERENCE_ATTR_NAME = "phase";
	/**
	 * And these 4 are the possible values for the attribute above (PHASE_PREFERENCE_ATTR_NAME)
	 */
	public static final String SHOW_ALL_PHASES = "All";
	public static final String SHOW_HATCHERY = "Hatchery";
	public static final String SHOW_PRE_ONGROWING = "Pre";
	public static final String SHOW_GROW_OUT = "Grow";
	/**
	 * waringin or error pages paths
	 */
	public static final String OPERATION_ERROR_PATH = "/html/error_pages/operation-error.jsp";
}
