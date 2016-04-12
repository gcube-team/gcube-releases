package org.gcube.portal.custom.communitymanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class PortletsIdManager {
	/**
	 * 
	 */
	public static final String GCUBE_LOGIN = "gCubeLogin";
	/**
	 * 
	 */
	public static final String GCUBE_LOGGEDIN = "gCubeLoggedin";
	/**
	 * 
	 */
	public static final String GCUBE_USERS_MANAGE = "usersManagement";
	/**
	 * 
	 */
	public static final String GCUBE_ADD_USERS_MANAGE = "addusers";
	/**
	 * 
	 */
	public static final String ACCOUNTING_PORTAL = "portalaccounting";
	/**
	 * 
	 */
	public static final String ACCOUNTING_NODES = "nodeaccounting";
	/**
	 * 
	 */
	public static final String ACCOUNTING_SERVICES= "servicesaccounting";
	/**
	 * 
	 */
	public static final String MONITORING_ECOSYSTEM = "ecomonitoring";
	/**
	 * 
	 */
	public static final String GCUBE_ROLES_MANAGE = "rolesManagement";
	/**
	 * 
	 */
	public static final String TIME_SERIES_MANAGER = "TimeSeriesPortlet";
	/**
	 * 
	 */
	public static final String WORKSPACE = "WorkspacePortlet";
	/**
	 * 
	 */
	public static final String INFORMATION_SPACE_EDITOR = "VREInformationSpaceEditorPortlet";
	/**
	 * 
	 */
	public static final String RESULTS_BROWSING = "newresultset";
	/**
	 * 
	 */
	public static final String RESOURCES_MANAGEMENT = "gCubeRM";
	/**
	 * 
	 */
	public static final String COLLECTIONS_NAVIGATOR = "CollectionsNavigatorPortlet";
	/**
	 * 
	 */
	public static final String SEARCH_UI = "Search";
	/**
	 * 
	 */
	public static final String ANNOTATION = "AnnotationFrontEnd_V2";	
	/**
	 * 
	 */
	public static final String REPORT_GENERATOR = "ReportGeneratorPortlet";
	/**
	 * 
	 */
	public static final String REPORT_TEMPLATE_CREATOR = "TemplateGenerator";
	/**
	 * 
	 */
	public static final String LR_NAVIGATION = "LR_navigation";
	/**
	 * 
	 */
	public static final String LR_LOGIN = "LR_login";
	/**
	 * 
	 */
	public static final String LR_VELOCITY = "LR_Hello_Velocity";
	/**
	 * 
	 */
	public static final String LR_WEBCONTENT_DISPLAY = "LR_WebContentDisplay";
	/**
	 * 
	 */
	public static final String LR_CALENDAR = "LR_Calendar";
	/**
	 * 
	 */
	public static final String LR_ACTIVITIES = "LR_Activities";
	/**
	 * 
	 */
	public static final String VRE_DEFINITION = "vredefinition";
	/**
	 * 
	 */
	public static final String VRE_DEPLOYER = "vredeployer";
	/**
	 * 
	 */
	public static final String VRE_DEPLOYMENT = "vredeployment";
	/**
	 * return the portlet name for Liferay 
	 * @return the liferay portlet id for a LR layout
	 */
	public static String getLRPortletId(String gCubePorletName) {

		//get the portles to look for from the property file
		Properties props = new Properties();
		String toReturn = "";
		try {
			String propertyfile = OrganizationsUtil.getTomcatFolder()+"conf/gcube-portlets.properties";
			File propsFile = new File(propertyfile);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis );
			toReturn = props.getProperty(gCubePorletName);			
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
}
