/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: UIIdentifiers.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.widgets.registry;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class UIIdentifiers {
	// The panel containing the console
	public static final String CONSOLE_COMPONENT_ID = "console-panel";
	// The widget that will be used to send notification messages to the
	// console.
	public static final String CONSOLE_WIDGET_ID = "console-widget";
	// The main panel that will contain the console
	public static final String CONSOLE_PANEL_ID = "panel-south";
	// The ID of west panel (resource navigation)
	public static final String RESOURCE_NAVIGATION_PANEL = "panel-west";
	// The main panel containing the resource detail grid
	public static final String RESOURCE_DETAIL_GRID_PANEL = "resource-detail-grid-panel";
	// The wrapper of resource detail grid (getWidget will return the contained grid).
	public static final String RESOURCE_DETAIL_GRID_CONTAINER_ID = "resource-detail-grid";

	// The tool bar in the main menu panel
	public static final String GLOBAL_STATUS_BAR_ID = "global-status-bar";
	public static final String STATUS_SCOPE_INFO_ID = "scope-info-status-bar";
	public static final String STATUS_LOADED_RESOURCES_ID = "loaded-resources-status-bar";
	public static final String STATUS_PROGRESS_BAR_ID = "progress-status-bar";

	public static final String GLOBAL_MENUBAR_ID = "global-menu-bar";

	public static final String BUTTON_AVAILABLE_SCOPES_ID = "btn-available-scopes";

	public static final String MAIN_CONTAINER_VIEWPORT_ID = "main-container-viewport";

	public static final String GLOBAL_MENU_CONTAINER_PANEL = "panel-north";
	public static final String TASKBAR_PANEL = "panel-est";
}
