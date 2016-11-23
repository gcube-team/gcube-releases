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
 * Filename: Commands.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.utils;

import java.util.List;

import org.gcube.portlets.admin.resourcemanagement.client.remote.ProxyRegistry;
import org.gcube.portlets.admin.resourcemanagement.client.views.profile.ResourceProfilePanel;
import org.gcube.portlets.admin.resourcemanagement.client.views.resourcedetails.ResourceDetailsPanel;
import org.gcube.portlets.admin.resourcemanagement.client.views.resourcedetails.ResourceGridFactory;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.console.ConsoleMessageBroker;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.console.ConsolePanel;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.panels.MainPanel;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.registry.UIIdentifiers;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.registry.WidgetsRegistry;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.taskbar.TaskbarItem;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.taskbar.TaskbarRegister;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.viewport.MainContainer;
import org.gcube.portlets.admin.resourcemanagement.shared.exceptions.WidgetNotRegistered;
import org.gcube.resourcemanagement.support.client.utils.StatusHandler;
import org.gcube.resourcemanagement.support.client.views.ResourceTypeDecorator;
import org.gcube.resourcemanagement.support.shared.types.Tuple;
import org.gcube.resourcemanagement.support.shared.types.UserGroup;
import org.gcube.resourcemanagement.support.shared.util.Configuration;
import org.gcube.resourcemanagement.support.shared.util.DelayedOperation;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.InfoConfig;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Here is a set of commands executed inside callbacks that involve
 * interactions with client-side widgets.
 * Additionally other commands are provided for general purpose functionalities
 * (e.g. refresh components, main components lookup, ...).m
 * @author Daniele Strollo (ISTI-CNR)
 */
public class Commands {

	/**************************************************************************
	 * COMMANDS FOR RPC REQUESTS TO THE SERVLET
	 *************************************************************************/

	public static final void refreshProgressBar() {
		final ProgressBar progress = (ProgressBar) WidgetsRegistry.getElem(UIIdentifiers.STATUS_PROGRESS_BAR_ID);

		new DelayedOperation() {
			@Override
			public void doJob() {
				
			}
		}.start(1000);
	}

	/**
	 * Creates a new Navigation Tree for the resources in a given scope.
	 * @param caller for logging reasons the caller must be passed as parameter
	 * @param scope the scope for resources to retrieve
	 */
	public static final void doLoadResourceTree(final Object caller, final String scope) {
		StatusHandler.getStatus().setCurrentScope(scope);
		Commands.mask(
				"Loading Resource Tree",
				WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_NAVIGATION_PANEL).getContainer()
		);
		Commands.mask(null,
				UIIdentifiers.GLOBAL_STATUS_BAR_ID,
				UIIdentifiers.GLOBAL_MENUBAR_ID);
		ConsoleMessageBroker.info(caller, "Loading scope " + scope);
		ProxyRegistry.getProxyInstance().getResourceTypeTree(
				scope,
				Callbacks.handleLoadResourceTree);
	}

	public static final void doLoadResourceDetailsGrid(final Object caller, final String scope, final String resourceType) {
		StatusHandler.getStatus().setCurrentScope(scope);
		StatusHandler.getStatus().setCurrentResourceType(resourceType);
		StatusHandler.getStatus().setCurrentResourceSubType(null);
		WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL).removeAll();
		Commands.mask(
				"Waiting for Resource Details",
				WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_NAVIGATION_PANEL).getContainer()
		);
		Commands.mask(
				"Loading Resource Details for type: " + resourceType + " in scope: " + scope,
				WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL).getContainer()
		);
		ProxyRegistry.getProxyInstance().getResourcesByType(
				scope,
				resourceType,
				Callbacks.handleLoadResourceDetailsGrid);
	}

	public static final void doGetRelatedResources(final String type, final String id, final String scope) {
		ProxyRegistry.getProxyInstance().getRelatedResources(type, id, scope, new AsyncCallback<List<String>>() {

			public void onSuccess(final List<String> result) {
				final Grid<ModelData> grid = ResourceGridFactory.createGrid(type + "Related", result, null);
				if (grid == null) {
					Commands.showPopup("Grid Creation",
							"The grid for related resource of " + type + " cannot be built",
							6000);
				}
				final Dialog dlg = new Dialog() {
					protected void onRender(final com.google.gwt.user.client.Element parent, final int pos) {
						super.onRender(parent, pos);
						this.setLayout(new FitLayout());
						this.add(grid);
					};
				};
				dlg.setSize(720, 250);
				dlg.setHideOnButtonClick(true);
				dlg.setHeading("Resource related to " + type + " " + id);
				dlg.show();
			}

			public void onFailure(final Throwable caught) {
			}
		});

	}

	public static final void doLoadWSResourceDetailsGrid(
			final Object caller,
			final String scope) {
		StatusHandler.getStatus().setCurrentScope(scope);
		StatusHandler.getStatus().setCurrentResourceType(ResourceTypeDecorator.WSResource.name());
		StatusHandler.getStatus().setCurrentResourceSubType(null);
		WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL).removeAll();
		Commands.mask(
				"Waiting for Resource Details",
				WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_NAVIGATION_PANEL).getContainer()
		);
		Commands.mask(
				"Loading Resource Details for type: " + ResourceTypeDecorator.WSResource.name() + " in scope: " + scope,
				WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL).getContainer()
		);
		ProxyRegistry.getProxyInstance().getWSResources(
				scope,
				Callbacks.handleLoadResourceDetailsGrid);
	}

	/**
	 * Filters the result of resource detail grid by using its subtye.
	 * @param caller
	 * @param resourceSubType
	 */
	public static final void doFilterResourceDetailsGrid(
			final Object caller, final String scope, final String resourceType, final String resourceSubType) {
		StatusHandler.getStatus().setCurrentScope(scope);
		StatusHandler.getStatus().setCurrentResourceType(resourceType);
		StatusHandler.getStatus().setCurrentResourceSubType(resourceSubType);
		WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL).removeAll();
		Commands.mask(
				"Waiting for Resource Details",
				WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_NAVIGATION_PANEL).getContainer()
		);
		Commands.mask(
				"Filtering Resource Details for " + resourceType + "::" + resourceSubType,
				WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL).getContainer()
		);

		ProxyRegistry.getProxyInstance().getResourcesBySubType(
				scope,
				resourceType,
				resourceSubType,
				Callbacks.handleFilterResourceDetailsGrid);
	}

	public static final void doGetResourceProfileByID(final Object caller, final String scope, final String resourceID) {
		ProxyRegistry.getProxyInstance().getResourceByID(
				scope,
				null, // no type specified
				resourceID,
				Callbacks.handleGetResourceProfile);
	}

	public static final void doGetResourceProfile(final Object caller, final String scope, String resourceType, final String resourceID) {
		/**
		 * Patch (i know do not tell me about it)
		 */
		if (resourceType.compareTo("GenericResourceGenericResource") == 0) {
			resourceType = "GenericResource";
			GWT.log("resType changed to =" + resourceType);
		}
		StatusHandler.getStatus().setCurrentScope(scope);
		StatusHandler.getStatus().setCurrentResourceType(resourceType);
		Commands.mask(
				"Getting Profile details for " + FWSTranslate.getFWSNameFromLabel(resourceType) + ": " + resourceID,
				WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL).getContainer()
		);
		ProxyRegistry.getProxyInstance().getResourceByID(
				scope,
				resourceType,
				resourceID,
				Callbacks.handleGetResourceProfile);
	}


	public static final void doGetAvailableScopes(final Object caller) {
		Commands.mask("Loading scopes", UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID);
		ProxyRegistry.getProxyInstance().getAvailableScopes(Callbacks.handleGetAvailableScopes);
	}

	public static final void buildAddToScopeReport(final Tuple<String> result) {
		if (result.size() < 4) {
			MessageBox.alert("Invalid Report Received", "Don't know why :(", null);
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
			// String resType = result.get(1);
			String xmlReport = result.get(2);
			String htmlReport = result.get(3);

			//String resourceTitle = ResourceTypeDecorator.AddScopeReport.getLabel() + ": " + reportID;
			int repSoFar = TaskbarRegister.getCurrAddScopeReportsNumber() + 1;
			String resourceTitle = ResourceTypeDecorator.AddScopeReport.getLabel() + "("+repSoFar+")";

			// This resource has already been opened
			if (TaskbarRegister.contains(reportID)) {
				TaskbarRegister.getTaskbarItem(reportID).destroy();
				Commands.showPopup("Retrieve Report", "Refreshing Report id: " +
						reportID, 3000);
			}
			MainPanel taskbarContainer = WidgetsRegistry.getPanel(UIIdentifiers.TASKBAR_PANEL);
			TaskbarItem tkItem = new TaskbarItem(
					StatusHandler.getStatus().getCurrentScope(),
					ResourceTypeDecorator.AddScopeReport,
					taskbarContainer,
					reportID,
					resourceTitle,
					"resource-profile-taskbar-item",
					ResourceTypeDecorator.DeployReport.getIcon());
			tkItem.getRelatedWindow().setMainWidget(
					new ResourceProfilePanel(
							ResourceTypeDecorator.AddScopeReport,
							xmlReport, htmlReport).getWidget());
			TaskbarRegister.registerTaskbarWidget(reportID, tkItem);
		} catch (Exception e) {
			GWT.log("During HTML conversion", e);
		} finally {
			Commands.unmask(UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID, UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL);
		}
	}
	
	public static final void buildRemoveFromScopeReport(final Tuple<String> result) {
		if (result.size() < 4) {
			MessageBox.alert("Invalid Report Received", "Don't know why :(", null);
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
			// String resType = result.get(1);
			String xmlReport = result.get(2);
			String htmlReport = result.get(3);

			//String resourceTitle = ResourceTypeDecorator.AddScopeReport.getLabel() + ": " + reportID;
			int repSoFar = TaskbarRegister.getCurrAddScopeReportsNumber() + 1;
			String resourceTitle = ResourceTypeDecorator.RemoveScopeReport.getLabel() + "("+repSoFar+")";

			// This resource has already been opened
			if (TaskbarRegister.contains(reportID)) {
				TaskbarRegister.getTaskbarItem(reportID).destroy();
				Commands.showPopup("Retrieve Report", "Refreshing Report id: " +
						reportID, 3000);
			}
			MainPanel taskbarContainer = WidgetsRegistry.getPanel(UIIdentifiers.TASKBAR_PANEL);
			TaskbarItem tkItem = new TaskbarItem(
					StatusHandler.getStatus().getCurrentScope(),
					ResourceTypeDecorator.RemoveScopeReport,
					taskbarContainer,
					reportID,
					resourceTitle,
					"resource-profile-taskbar-item",
					ResourceTypeDecorator.DeployReport.getIcon());
			tkItem.getRelatedWindow().setMainWidget(
					new ResourceProfilePanel(
							ResourceTypeDecorator.RemoveScopeReport,
							xmlReport, htmlReport).getWidget());
			TaskbarRegister.registerTaskbarWidget(reportID, tkItem);
		} catch (Exception e) {
			GWT.log("During HTML conversion", e);
		} finally {
			Commands.unmask(UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID, UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL);
		}
	}

	public static final void doSetSuperUser(final boolean state) {
		if (state) {
			StatusHandler.getStatus().setCredentials(UserGroup.ADMIN);
		} else {
			StatusHandler.getStatus().setCredentials(UserGroup.DEBUG);
		}
		ProxyRegistry.getProxyInstance().setSuperUser(state, new AsyncCallback<Void>() {
			public void onSuccess(final Void result) {
				ConsoleMessageBroker.info(this, "Currently super user");
				Commands.refreshResourceGrid();
				//Commands.refreshResourceTree();
				Commands.doGetAvailableScopes(Commands.class);
			}
			public void onFailure(final Throwable caught) {
				ConsoleMessageBroker.error(this, "Super user mode failed");
			}
		});
	}


	/**************************************************************************
	 * GENERAL PURPOSE UTILITY COMMANDS
	 *************************************************************************/

	public static final void refreshResourceTree() {
		String scope = StatusHandler.getStatus().getCurrentScope();
		Commands.mask(
				"Loading Resource Tree",
				WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_NAVIGATION_PANEL).getContainer()
		);
		Commands.mask(null,
				UIIdentifiers.GLOBAL_STATUS_BAR_ID,
				UIIdentifiers.GLOBAL_MENUBAR_ID);
		ConsoleMessageBroker.info(Commands.class, "Loading scope " + scope);
		ProxyRegistry.getProxyInstance().getResourceTypeTree(
				scope,
				Callbacks.handleReloadResourceTree);
	}

	public static final void refreshResourceGrid() {
		String scope = StatusHandler.getStatus().getCurrentScope();
		String resType = StatusHandler.getStatus().getCurrentResourceType();
		String resSubType = StatusHandler.getStatus().getCurrentResourceSubType();

		if (scope != null && resType != null) {
			if (resSubType == null) {
				if (resType.equalsIgnoreCase(ResourceTypeDecorator.WSResource.name())) {
					doLoadWSResourceDetailsGrid(Commands.class.getName(),
							scope);
				} else {
					doLoadResourceDetailsGrid(Commands.class.getName(),
							scope,
							resType);
				}
			} else {
				// Has a subtype
				doFilterResourceDetailsGrid(Commands.class.getName(),
						scope,
						resType,
						resSubType);
			}
		}
	}

	public static void clearResourceTreePanel() {
		MainPanel treePanel = WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_NAVIGATION_PANEL);
		Text widget = new Text();
		widget.setId("res-details-widget-fake");
		widget.setStyleName("x-panel-tree-background");
		ContentPanel cp = new ContentPanel();
		cp.setAnimCollapse(false);
		cp.setHeading("Resources");
		cp.setLayout(new FitLayout());
		cp.add(widget);
		treePanel.add(cp, true);
	}

	public static void clearResourceGridPanel() {
		Component widget = new Text();
		widget.setId("res-details-widget-fake");
		widget.setStyleName("x-panel-cube-background");

		WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL).add(widget, true);
	}

	public static void showHideConsole() {
		Object elem = WidgetsRegistry.getElem(UIIdentifiers.CONSOLE_COMPONENT_ID);
		if (elem != null && elem instanceof Component) {
			Component console = (Component) elem;
			if (!console.isVisible()) {
				console.show();
			} else {
				console.hide();
			}
		}
	}

	public static MainContainer getViewport() {
		return (MainContainer) WidgetsRegistry.getWidget(UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID);
	}

	public static void refreshViewport() {
		MainContainer viewport = (MainContainer) WidgetsRegistry.getWidget(UIIdentifiers.MAIN_CONTAINER_VIEWPORT_ID);
		viewport.layout(true);
	}

	public static void refreshConsole() {
		ConsolePanel console = WidgetsRegistry.getConsole();
		if (console != null) {
			console.refresh();
		}
	}

	public static void refreshResourceDetails() {
		WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL).getContainer().layout(true);
	}

	@SuppressWarnings("unchecked")
	public static Grid<ModelData> getResourceDetailGrid() {
		Object retval = getResourceDetailPanel().getWidget();
		if (retval == null) {
			return null;
		}
		if (retval != null && retval instanceof Grid) {
			return (Grid<ModelData>) retval;
		}
		return null;
	}

	public static ResourceDetailsPanel getResourceDetailPanel() {
		try {
			Object o = WidgetsRegistry.getElem(UIIdentifiers.RESOURCE_DETAIL_GRID_CONTAINER_ID, ResourceDetailsPanel.class);
			if (o != null && o instanceof ResourceDetailsPanel) {
				return (ResourceDetailsPanel) o;
			}
			return null;
		} catch (WidgetNotRegistered e) {
			return null;
		}
	}

	/**
	 * In the status bar sets the currently selected scope.
	 * @param scope
	 */
	public static void setStatusScope(final String scope) {
		try {
			Status scopeInfo = (Status) WidgetsRegistry.getWidget(UIIdentifiers.STATUS_SCOPE_INFO_ID);
			scopeInfo.setText(scope);

			ProxyRegistry.getProxyInstance().setCurrentScope(scope, new AsyncCallback<Void>() {
				public void onSuccess(final Void result) {
					ConsoleMessageBroker.info(this, "The scope has been sent to server");
				}
				public void onFailure(final Throwable caught) {
					ConsoleMessageBroker.error(this, "The scope has not been sent to server");
				}
			});

		} catch (Exception e) {
			ConsoleMessageBroker.error(Commands.class, e.getMessage());
		}
	}


	public static void setLoadedResources(final int number) {
		try {
			Status loadedResources = (Status) WidgetsRegistry.getWidget(UIIdentifiers.STATUS_LOADED_RESOURCES_ID);
			loadedResources.setText(String.valueOf(number));
		} catch (Exception e) {
			ConsoleMessageBroker.error(Commands.class, e.getMessage());
		}
	}


	/**
	 * When masking the component show the rotating gear.
	 * @param component the element to mask
	 * @param message the message to show during loading
	 */
	public static void mask(final String message, final Component component) {
		if (component != null) {
			if (message != null) {
				component.mask(message, "loading-indicator");
			} else {
				component.mask();
			}
		}
	}

	public static void unmask(final Component component) {
		if (component != null) {
			component.unmask();
		}
	}

	public static void mask(final String message, final String... componentIDs) {
		for (String componentID : componentIDs) {
			if (WidgetsRegistry.containsElem(componentID)) {
				try {
					if (message == null) {
						((Component) WidgetsRegistry.getWidget(componentID)).mask();
					} else {
						((Component) WidgetsRegistry.getWidget(componentID)).mask(message, "loading-indicator");
					}
				} catch (Exception e) {}
			}
		}
	}

	public static void unmask(final String... componentIDs) {
		for (String componentID : componentIDs) {
			if (WidgetsRegistry.containsElem(componentID)) {
				try {
					((Component) WidgetsRegistry.getWidget(componentID)).unmask();

				} catch (Exception e) {}
			}
		}
	}

	public static void showPopup(final String title, final String text) {
		showPopup(title, text, Configuration.popupDelay);
	}

	public static void showPopup(final String title, final String text, final int delayMills) {
		InfoConfig cfg = new InfoConfig(title, text);
		cfg.display = delayMills;
		Info.display(cfg);
	}

	/**
	 * Checks that a component is enabled for the current credentials.
	 * If not it will be disabled and hidden.
	 * @param w
	 * @param args a list of enabled groups.
	 */
	public static final void evaluateCredentials(final Component w, final UserGroup... args) {
		if (StatusHandler.getStatus().getCredentials() == null) {
			w.setEnabled(false);
			w.hide();
			return;
		}
		for (UserGroup enabledGroup : args) {
			if (StatusHandler.getStatus().getCredentials() == enabledGroup) {
				w.setEnabled(true);
				w.show();
				return;
			}
		}
		w.setEnabled(false);
		w.hide();
	}
}
