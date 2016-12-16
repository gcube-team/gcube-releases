package org.gcube.portlets.admin.accountingmanager.shared;

import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class Constants {
	public static final boolean DEBUG_MODE = false;
	public static final boolean TEST_ENABLE = false;

	public static final String CURR_GROUP_ID = "CURR_GROUP_ID";

	public static final String APPLICATION_ID = "org.gcube.portlets.admin.accountingmanager.server.portlet.AccountingManagerPortlet";
	public static final String ACCOUNTING_MANAGER_ID = "AccountingManagerId";
	public static final String AM_LANG_COOKIE = "AMLangCookie";
	public static final String AM_LANG = "AMLang";
	public static final String DEFAULT_USER = "giancarlo.panichi";
	public static final String DEFAULT_SCOPE = "/gcube/devNext/NextNext";
	public static final String DEFAULT_TOKEN = "ae1208f0-210d-47c9-9b24-d3f2dfcce05f-98187548";

	// public static final String DEFAULT_SCOPE = "/gcube/devsec/devVRE";
	// public static final String DEFAULT_TOKEN =
	// "16e65d4f-11e0-4e4a-84b9-351688fccc12-98187548";
	public static final String DEFAULT_ROLE = "OrganizationMember";

	public static final String EXPORT_SERVLET = "ExportServlet";
	public static final String EXPORT_SERVLET_TYPE_PARAMETER = "ExportServletType";
	public static final String EXPORT_SERVLET_ACCOUNTING_TYPE_PARAMETER = "AccountingType";

	public static final String SESSION_ACCOUNTING_STATE = "ACCOUNTING_STATE";

	public static final AccountingType[] DEFAULT_TABS = new AccountingType[] { AccountingType.STORAGE };;

	// IS Resource
	public static final String ACCOUNTING_NAME = "AccountingManager";
	public static final String ACCOUNTING_CATEGORY = "AccountingProfile";

}
