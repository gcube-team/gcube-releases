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
 * Filename: Callbacks.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.admin.resourcemanagement.client.views.profile.ResourceProfilePanel;
import org.gcube.portlets.admin.resourcemanagement.client.views.resourcedetails.ResourceDetailsPanel;
import org.gcube.portlets.admin.resourcemanagement.client.views.resourcedetails.ResourceGridFactory;
import org.gcube.portlets.admin.resourcemanagement.client.views.resourcetree.ResourcesTreePanel;
import org.gcube.portlets.admin.resourcemanagement.client.views.resourcetree.WSResourcesTreePanel;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.console.ConsoleMessageBroker;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.panels.MainPanel;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.registry.UIIdentifiers;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.registry.WidgetsRegistry;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.taskbar.TaskbarItem;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.taskbar.TaskbarRegister;
import org.gcube.resourcemanagement.support.client.utils.LocalStatus;
import org.gcube.resourcemanagement.support.client.utils.StatusHandler;
import org.gcube.resourcemanagement.support.client.views.ResourceTypeDecorator;
import org.gcube.resourcemanagement.support.shared.types.Tuple;
import org.gcube.resourcemanagement.support.shared.types.datamodel.CompleteResourceProfile;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * In order to simplify the management of asynchronous commands
 * executed by the application that involve access to the servlet
 * the invocations have been represented in {@link Commands} and
 * the related callbacks here.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class Callbacks {


	public static final AsyncCallback<List<String>> handleGetAvailableScopes = new AsyncCallback<List<String>>() {
		public void onSuccess(final List<String> result) {

			if (!WidgetsRegistry.containsElem(UIIdentifiers.BUTTON_AVAILABLE_SCOPES_ID)) {
				//MessageBox.info("Failure", "cannot retrieve the scopes button", null);
				Commands.unmask(UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID);
				return;
			}
			Button btnScopes = (Button) WidgetsRegistry.getWidget(UIIdentifiers.BUTTON_AVAILABLE_SCOPES_ID);

			Menu scrollMenu = new Menu();
			scrollMenu.setMaxHeight(200);

			LocalStatus.getInstance().getAvailableScopes().clear();

			for (String scope : result) {
				final String currScope = scope;
				LocalStatus.getInstance().getAvailableScopes().add(currScope);
				scrollMenu.add(new MenuItem(currScope) {
					@Override
					protected void onClick(final ComponentEvent be) {
						super.onClick(be);
						Commands.doLoadResourceTree(this, currScope);
					}
				});
			}

			btnScopes.setMenu(scrollMenu);
			Commands.unmask(UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID);
		}
		public void onFailure(final Throwable caught) {
			Commands.unmask(UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID);
			MessageBox.info("Failure", "cannot retrieve the scopes", null);
			GWT.log("cannot retrieve the scopes", caught);
		}
	};



	public static final void builtResourceTree(final HashMap<String, ArrayList<String>> result, final boolean clearGrid) {
		MainPanel panel = WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_NAVIGATION_PANEL);

		List<String> wsResourcesTypes = null;
		if (result.containsKey(ResourceTypeDecorator.WSResource.name())) {
			wsResourcesTypes = result.get(ResourceTypeDecorator.WSResource.name());
			result.remove(ResourceTypeDecorator.WSResource.name());
		}

		// Creates the tree of resources
		ResourcesTreePanel resourceTree = new ResourcesTreePanel(result);
		resourceTree.getWidget().setWidth("100%");
		resourceTree.getWidget().setHeight("100%");
		ContentPanel cp = new ContentPanel();
		cp.setAnimCollapse(false);
		cp.setHeading("Resources");
		cp.setLayout(new FitLayout());
		cp.add(resourceTree.getWidget());
		panel.add(cp, true);

		if (wsResourcesTypes != null && wsResourcesTypes.size() > 0) {
			// Creates the tree of WSResources
			WSResourcesTreePanel wsresourceTree = new WSResourcesTreePanel(wsResourcesTypes);
			wsresourceTree.getWidget().setWidth("100%");
			wsresourceTree.getWidget().setHeight("100%");
			cp = new ContentPanel();
			cp.setAnimCollapse(false);
			cp.setHeading("WSResources");
			cp.setLayout(new FitLayout());
			cp.add(wsresourceTree.getWidget());
			panel.add(cp, false);
		}

		if (clearGrid) {
			Commands.clearResourceGridPanel();
		}

		Commands.unmask(WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_NAVIGATION_PANEL).getContainer());
		Commands.unmask(
				UIIdentifiers.GLOBAL_STATUS_BAR_ID,
				UIIdentifiers.GLOBAL_MENUBAR_ID);
	}

	/**
	 * Once received the list of Types/Subtypes they are shown in a tree panel.
	 * Here implemented the logics for that.
	 */
	public static final AsyncCallback<HashMap<String, ArrayList<String>>> handleLoadResourceTree =
		new AsyncCallback<HashMap<String, ArrayList<String>>>() {
		public void onSuccess(final HashMap<String, ArrayList<String>> result) {
			builtResourceTree(result, true);
		}
		public void onFailure(final Throwable caught) {
			MessageBox.info("Failure", caught.getMessage(), null);

			Commands.clearResourceTreePanel();
			ConsoleMessageBroker.error("loading resource", caught.getMessage());
			Commands.unmask(WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_NAVIGATION_PANEL).getContainer());
			Commands.unmask(
					UIIdentifiers.GLOBAL_STATUS_BAR_ID,
					UIIdentifiers.GLOBAL_MENUBAR_ID);
		}
	};

	/**
	 * Once received the list of Types/Subtypes they are shown in a tree panel.
	 * Here implemented the logics for that.
	 */
	public static final AsyncCallback<HashMap<String, ArrayList<String>>> handleReloadResourceTree =
		new AsyncCallback<HashMap<String, ArrayList<String>>>() {
		public void onSuccess(final HashMap<String, ArrayList<String>> result) {
			builtResourceTree(result, false);
		}
		public void onFailure(final Throwable caught) {
			MessageBox.info("Failure", caught.getMessage(), null);

			Commands.clearResourceTreePanel();
			ConsoleMessageBroker.error("loading resource", caught.getMessage());
			Commands.unmask(WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_NAVIGATION_PANEL).getContainer());
			Commands.unmask(
					UIIdentifiers.GLOBAL_STATUS_BAR_ID,
					UIIdentifiers.GLOBAL_MENUBAR_ID);
		}
	};

	/**
	 * Once received the list of resource profiles for resources of a given type,
	 * they will be shown inside a grid that is created here.
	 */
	public static final AsyncCallback<List<String>> handleLoadResourceDetailsGrid =
		new AsyncCallback<List<String>>() {
		public void onSuccess(final List<String> result) {

			Commands.mask(
					"Rendering grid",
					WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL).getContainer()
			);
			try {
				ResourceDetailsPanel resGrid = Commands.getResourceDetailPanel();
				Grid<ModelData> grid = ResourceGridFactory.createGrid(StatusHandler.getStatus().getCurrentResourceType(),
						result,
						"SubType");
				resGrid.setGrid(grid, true);
			} catch (NullPointerException e1) {
				MessageBox.info(
						"Failure",
						"the grid container cannot be retrieved",
						null);
			}
			Commands.unmask(WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_NAVIGATION_PANEL).getContainer());
			Commands.unmask(WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL).getContainer());

			Commands.setLoadedResources(result != null ? result.size() : 0);
			Commands.showPopup("Grid Creation", "Retrieved " + (result != null ? result.size() : 0) + " elements.");
		}

		public void onFailure(final Throwable caught) {
			MessageBox.info("Failure", "the grid has not been built", null);
			Commands.unmask(WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_NAVIGATION_PANEL).getContainer());
			Commands.unmask(WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL).getContainer());
		}
	};



	public static final AsyncCallback<List<String>> handleFilterResourceDetailsGrid =
		new AsyncCallback<List<String>>() {
		public void onSuccess(final List<String> result) {
			try {
				ResourceDetailsPanel resGrid = Commands.getResourceDetailPanel();
				Grid<ModelData> grid = ResourceGridFactory.createGrid(StatusHandler.getStatus().getCurrentResourceType(),
						result,
						null);
				resGrid.setGrid(grid, false);
			} catch (NullPointerException e1) {
				MessageBox.info(
						"Failure",
						"the grid container cannot be retrieved",
						null);
			} finally {
				Commands.unmask(WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_NAVIGATION_PANEL).getContainer());
				Commands.unmask(WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL).getContainer());
			}

			Commands.setLoadedResources(result != null ? result.size() : 0);
			Commands.showPopup("Grid Creation", "Retrieved " + (result != null ? result.size() : 0) + " elements.");
		}

		public void onFailure(final Throwable caught) {
			MessageBox.info("Failure", "the grid has not been built", null);

			Commands.unmask(WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_NAVIGATION_PANEL).getContainer());
			Commands.unmask(WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL).getContainer());
		}
	};


	public static final AsyncCallback<CompleteResourceProfile> handleGetResourceProfile =
		new AsyncCallback<CompleteResourceProfile>() {

		public void onSuccess(final CompleteResourceProfile result) {
			Dialog dlg = new Dialog();
			dlg.setLayout(new FitLayout());
			dlg.setHeading("XML Resource Profile");
			dlg.setModal(true);
			dlg.setWidth(800);
			dlg.setHeight(400);
			dlg.setHideOnButtonClick(true);
			// HtmlContainer htmlContainer = new HtmlContainer();
			try {
				// This resource has already been opened
				if (TaskbarRegister.contains(result.getID())) {
					TaskbarRegister.getTaskbarItem(result.getID()).destroy();
					Commands.showPopup("Retrieve Resource Profile", "Refreshing profile for id: " +
							result.getID(), 3000);
				}
				MainPanel taskbarContainer = WidgetsRegistry.getPanel(UIIdentifiers.TASKBAR_PANEL);
				TaskbarItem tkItem = new TaskbarItem(
						StatusHandler.getStatus().getCurrentScope(),
						result.getType(),
						taskbarContainer,
						result.getID(),
						//result.getType() + ": " + result.getTitle(),
						result.getTitle(),
						"resource-profile-taskbar-item",
				"profile-big-icon");
				tkItem.getRelatedWindow().setMainWidget(
						new ResourceProfilePanel(
								result.getType(),
								result.getXmlRepresentation(),
								result.getHtmlRepresentation()).getWidget());
				TaskbarRegister.registerTaskbarWidget(result.getID(), tkItem);
			} catch (Exception e) {
				GWT.log("During HTML conversion", e);
			} finally {
				Commands.unmask(WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL).getContainer());
			}
		}

		public void onFailure(final Throwable caught) {
			MessageBox.info("Failure", "the profile has not been retrieved", null);
			GWT.log("on handleGetResourceProfile", caught);
			Commands.unmask(WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL).getContainer());
		}
	};


	public static final AsyncCallback<Tuple<String>> handleGetDeploymentReport =
		new AsyncCallback<Tuple<String>>() {

		public void onSuccess(final Tuple<String> result) {

			if (result.size() < 3) {
				MessageBox.alert("Invalid Report Received", "Don't know why :(", null);
				Commands.unmask(UIIdentifiers.GLOBAL_STATUS_BAR_ID);
				Commands.unmask(UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID, UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL);
				return;
			}

			Dialog dlg = new Dialog();
			dlg.setLayout(new FitLayout());
			dlg.setHeading("XML Resource Profile");
			dlg.setModal(true);
			dlg.setWidth(800);
			dlg.setHeight(400);
			dlg.setHideOnButtonClick(true);
			// HtmlContainer htmlContainer = new HtmlContainer();
			try {

				String reportID = result.get(0);
				String resourceTitle = ResourceTypeDecorator.DeployReport.getLabel() + ": " + reportID;

				// This resource has already been opened
				if (TaskbarRegister.contains(reportID)) {
					TaskbarRegister.getTaskbarItem(reportID).destroy();
					Commands.showPopup("Retrieve Report", "Refreshing Report id: " +
							reportID, 3000);
				}
				MainPanel taskbarContainer = WidgetsRegistry.getPanel(UIIdentifiers.TASKBAR_PANEL);
				TaskbarItem tkItem = new TaskbarItem(
						StatusHandler.getStatus().getCurrentScope(),
						ResourceTypeDecorator.DeployReport,
						taskbarContainer,
						reportID,
						resourceTitle,
						"resource-profile-taskbar-item",
						ResourceTypeDecorator.DeployReport.getIcon());
				tkItem.getRelatedWindow().setMainWidget(
						new ResourceProfilePanel(
								ResourceTypeDecorator.DeployReport,
								result.get(1),
								result.get(2)).getWidget());
				TaskbarRegister.registerTaskbarWidget(reportID, tkItem);
			} catch (Exception e) {
				GWT.log("During HTML conversion", e);
			} finally {
				Commands.unmask(UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID, UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL);
				Commands.unmask(UIIdentifiers.GLOBAL_STATUS_BAR_ID);
			}
		}

		public void onFailure(final Throwable caught) {
			MessageBox.info("Failure", "the report has not been retrieved", null);
			GWT.log("on handleGetDeploymentReport", caught);
			Commands.unmask(UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID, UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL);
			Commands.unmask(UIIdentifiers.GLOBAL_STATUS_BAR_ID);
		}
	};
}
