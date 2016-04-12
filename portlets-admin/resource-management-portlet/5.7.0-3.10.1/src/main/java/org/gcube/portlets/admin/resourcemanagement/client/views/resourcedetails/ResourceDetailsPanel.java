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
 * Filename: ResourceDetailsPanel.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.views.resourcedetails;

import java.util.List;
import java.util.Vector;

import org.gcube.portlets.admin.resourcemanagement.client.forms.DeployServicesForm;
import org.gcube.portlets.admin.resourcemanagement.client.utils.Commands;
import org.gcube.portlets.admin.resourcemanagement.client.utils.OpCommands;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.console.ConsoleMessageBroker;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.registry.UIIdentifiers;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.registry.WidgetsRegistry;
import org.gcube.portlets.admin.software_upload_wizard.client.AppController;
//import org.gcube.portlets.admin.software_upload_wizard.client.AppController;
import org.gcube.resourcemanagement.support.client.utils.StatusHandler;
import org.gcube.resourcemanagement.support.client.views.ResourceTypeDecorator;
import org.gcube.resourcemanagement.support.shared.operations.SupportedOperations;
import org.gcube.resourcemanagement.support.shared.types.datamodel.ResourceDetailModel;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridViewConfig;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;

/**
 * @author Massimiliano Assante (ISTI-CNR)
 * @author Daniele Strollo
 * @version 2.1 APR 2012
 */
public class ResourceDetailsPanel {
	private ContentPanel rootPanel = null;
	private Component widget = null;
	private boolean groupingEnabled = true;
	private String collapsibleColumn = "SubType";
	private ToolBar toolBar = new ToolBar();
	private final static String btnGroupID = "btn-grid-group";
	private final static String btnRefreshID = "btn-grid-refresh";
	private boolean highlightInvalidFields = false;

	public ResourceDetailsPanel() {
		this.widget = new Text();
		this.init();
	}

	private ListStore<ModelData> getStore() {
		return this.getGrid().getStore();
	}

	public final List<ModelData> getElemsForInstall() {
		List<ModelData> retval = new Vector<ModelData>();
		for (ModelData r : this.getStore().getModels()) {
			if (r.getProperties().containsKey(ResourceDetailModel.SERVICE_INSTALL_KEY) &&
					Boolean.parseBoolean(((Object) r.get(ResourceDetailModel.SERVICE_INSTALL_KEY)).toString())) {
				retval.add(r);
			}
		}
		return retval;
	}

	public final void refreshModel() {
		for (ModelData s : this.getSelection()) {
			this.getStore().update(s);
		}
	}

	public final List<ModelData> getSelection() {
		if (this.getGrid() == null || this.getGrid().getSelectionModel().getSelection() == null) {
			return null;
		}
		return this.getGrid().getSelectionModel().getSelection();
	}

	/**
	 * Method called at initialization phase (Constructor).
	 */
	public final void init() {
		this.rootPanel = new ContentPanel(new FitLayout());
	//	this.rootPanel.setHeading("Resource Details");
		this.rootPanel.setHeaderVisible(false);
		this.widget.setId("res-details-widget-fake");
		this.widget.setStyleName("x-panel-cube-background");

		// The first creation of button
		Button groupButton = new Button() {
			@Override
			protected void onClick(final ComponentEvent ce) {
				super.onClick(ce);
				if (groupingEnabled) {
					disableGrouping();
				} else {
					enableGrouping();
				}
			}
		};
		groupButton.setId(btnGroupID);
		groupButton.setIconStyle("grid-icon");
		groupButton.setToolTip("Categorize");
		this.toolBar.add(new SeparatorToolItem());
		this.toolBar.add(groupButton);

		// The first creation of button
		Button refreshButton = new Button() {
			@Override
			protected void onClick(final ComponentEvent ce) {
				Commands.refreshResourceGrid();
			}
		};
		refreshButton.setId(btnRefreshID);
		refreshButton.setIconStyle("refresh-icon");
		refreshButton.setToolTip("Refresh");
		this.toolBar.add(refreshButton);

		this.rootPanel.setTopComponent(this.toolBar);
	}

	public final void toggleHighlightInvalidFields() {
		this.highlightInvalidFields = !this.highlightInvalidFields;
		if (this.getGridView() != null) {
			this.getGridView().refresh(true);
		}
	}

	public final ToolBar getToolBar() {
		return this.toolBar;
	}

	public final Component getWidget() {
		return this.widget;
	}

	private void disableGrouping() {
		GroupingStore<ModelData> groupingStore = null;
		if (this.getStore() instanceof GroupingStore) {
			groupingStore = (GroupingStore<ModelData>) this.getStore();
			if (groupingStore != null) {
				groupingStore.clearGrouping();
			}
			this.groupingEnabled = false;
		}
	}

	private void enableGrouping() {
		GroupingStore<ModelData> groupingStore = null;
		if (this.getStore() instanceof GroupingStore) {
			groupingStore = (GroupingStore<ModelData>) this.getStore();
			if (groupingStore != null) {
				groupingStore.groupBy(collapsibleColumn);
			}
			this.groupingEnabled = true;
		}
	}

	@SuppressWarnings("unchecked")
	public final Grid<ModelData> getGrid() {
		if (this.getWidget() != null && this.getWidget() instanceof Grid) {
			return (Grid<ModelData>) this.getWidget();
		}
		return null;
	}

	private GroupingView getGridView() {
		if (this.getGrid() != null && this.getGrid().getView() instanceof GroupingView) {
			return (GroupingView) this.getGrid().getView();
		}
		return null;
	}

	public final void setWidget(final Component widget, final boolean enableGrouping) {
		this.rootPanel.removeAll();
		this.widget = widget;
		rootPanel.add(this.widget);

		Button groupButton = null;
		try {
			groupButton = (Button) this.toolBar.getItemByItemId(ResourceDetailsPanel.btnGroupID);
			groupButton.setEnabled(enableGrouping);
		} catch (Exception e) {
			ConsoleMessageBroker.error(this, "During set widget. " + e.getMessage());
		}

		// FIXME check if needed --- Commands.refreshResourceDetails();
	}

	public final ContentPanel getContainer() {
		return this.rootPanel;
	}

	private void resetToolBar() {
		List<Component> buttons = this.getToolBar().getItems();
		List<Component> toRemove = new Vector<Component>();
		if (buttons.size() > 3) {
			for (int i = 3; i < buttons.size(); i++) {
				toRemove.add(buttons.get(i));
			}
		}
		if (toRemove.size() > 0) {
			for (Component c : toRemove) {
				this.getToolBar().remove(c);
			}
		}
	}

	public final void initToolbar() {
		String resType = StatusHandler.getStatus().getCurrentResourceType();

		// Removes from the toolbar specific buttons
		resetToolBar();

		/************************************************
		 * GENERIC RESOURCE - customized toolbar
		 ***********************************************/

		// Adds toolbar buttons for Generic Resources
		if (resType.equals(ResourceTypeDecorator.GenericResource.name()) &&
				SupportedOperations.GENERIC_RESOURCE_CREATE.isAllowed(StatusHandler.getStatus().getCredentials())) {
			this.getToolBar().add(new SeparatorToolItem());
			ToolButton createNew = new ToolButton("new-icon") {
				@Override
				protected void onClick(final ComponentEvent ce) {
					super.onClick(ce);
					OpCommands.doOpenGenericResourceForm();
				}
			};
			createNew.setToolTip("Create new Generic Resource");
			this.getToolBar().add(createNew);
		}

		/************************************************
		 * SERVICES - customized toolbar
		 ***********************************************/

		// Adds toolbar buttons for Services
		//TODO: To be implemented in the future
		//		if (resType.equals(ResourceTypeDecorator.Service.name()) &&
		//				SupportedOperations.SERVICE_CREATE.isAllowed(StatusHandler.getStatus().getCredentials())) {
		//			this.getToolBar().add(new SeparatorToolItem());
		//			ToolButton createNew = new ToolButton("new-icon") {
		//				@Override
		//				protected void onClick(final ComponentEvent ce) {
		//					super.onClick(ce);
		//					CommonOperations.doOpenServiceForm();
		//				}
		//			};
		//			createNew.setToolTip("Create new Software");
		//			this.getToolBar().add(createNew);
		//		}

		// Adds toolbar buttons for Services
		if (resType.equals(ResourceTypeDecorator.Service.name()) &&
				SupportedOperations.SERVICE_DEPLOY.isAllowed(StatusHandler.getStatus().getCredentials())) {
			this.getToolBar().add(new SeparatorToolItem());
			ToolButton doDeploy = new ToolButton("deploy-icon") {
				protected void onClick(final ComponentEvent be) {
					super.onClick(be);
					GWT.runAsync(DeployServicesForm.class, new RunAsyncCallback() {
						@Override
						public void onFailure(Throwable reason) {
							Window.alert("Could not load the deploy form.");   			
						}
						@Override
						public void onSuccess() {
							new DeployServicesForm().show();							
						}						 
					});

				};
			};
			doDeploy.setToolTip("Prepare a deployment plan");
			this.getToolBar().add(doDeploy);
		}


		// Adds toolbar buttons for Services
		if (resType.equals(ResourceTypeDecorator.Service.name()) &&
				SupportedOperations.SERVICE_GET_REPORT.isAllowed(StatusHandler.getStatus().getCredentials())) {
			this.getToolBar().add(new SeparatorToolItem());
			ToolButton doDeploy = new ToolButton("getreport-icon") {
				protected void onClick(final ComponentEvent be) {
					super.onClick(be);
					OpCommands.doGetDeployReport();
				};
			};
			doDeploy.setToolTip("Get report for submitted deployment");
			this.getToolBar().add(doDeploy);
		}

		// Adds toolbar button for software upload wizard (SUW)
		if (resType.equals(ResourceTypeDecorator.Service.name()) &&
				SupportedOperations.SERVICE_DEPLOY.isAllowed(StatusHandler.getStatus().getCredentials())) {
			this.getToolBar().add(new SeparatorToolItem());
			ToolButton doAddSoftware = new ToolButton("add-software-icon") {
				protected void onClick(final ComponentEvent be) {
					super.onClick(be);
					GWT.runAsync(AppController.class, new RunAsyncCallback() {
						@Override
						public void onSuccess() {
							String currentScope = StatusHandler.getStatus().getCurrentScope();
							HandlerManager eventBus = new HandlerManager(null);
							AppController appViewer = new AppController(eventBus, currentScope);
							//AppController appViewer = new AppController(eventBus);
							appViewer.go();
						}

						public void onFailure(Throwable reason) {
							Window.alert("There are networks problem, please check your connection.");              
						}

					});

				};
			};
			doAddSoftware.setToolTip("Upload software");
			this.getToolBar().add(doAddSoftware);
			this.getToolBar().add(new SeparatorToolItem());
		}

	}

	public final void setGrid(final Grid<ModelData> grid, final boolean groupingEnabled) {
		// Sets the grid container title
		//this.rootPanel.setHeading("Resource Details (" + StatusHandler.getStatus().getCurrentResourceType() + ")");

		this.initToolbar();

		// adds highlighting support
		// Annotates invalid cells
		grid.getView().setViewConfig(new GridViewConfig() {
			@Override
			public String getRowStyle(
					final ModelData model,
					final int rowIndex,
					final ListStore<ModelData> ds) {
				try {
					if (!highlightInvalidFields) {
						return super.getRowStyle(model, rowIndex, ds);
					}

					// NOTE this strange cast to object is due to:
					// compilation error in jdk bug: java.lang.Object cannot be dereferenced

					// if no requirements expressed for this type, return the default style
					if (ResourceDetailModel.getRequiredFields(((Object) model.get("Type")).toString()) == null) {
						return super.getRowStyle(model, rowIndex, ds);
					}
					String[] reqs = ResourceDetailModel.getRequiredFields(((Object) model.get("Type")).toString());

					for (String req : reqs) {
						if (model.get(req) == null || ((Object) model.get(req)).toString().trim().length() == 0) {
							// Adds to the model with problems the error code
							return "x-grid-invalid-row";
						}
					}
					return super.getRowStyle(model, rowIndex, ds);
				} catch (Exception e) {
					GWT.log("getting required fields", e);
					return super.getRowStyle(model, rowIndex, ds);
				}
			}
		});
		/*
		 * add the row click listener by default open the profile
		 */
		grid.addListener(Events.RowClick, new Listener<BaseEvent>(){
			public void handleEvent(BaseEvent be) {
				//GridEvent ge = (GridEvent)be;
				openProfileInPinnedResources();
			}
		});

		grid.setContextMenu(ContextMenuFactory.getInstance().buildContextMenu(
				StatusHandler.getStatus().getCurrentResourceType(), this));

		this.setWidget(grid, groupingEnabled);

		// Add the new created widget inside the container
		WidgetsRegistry.getPanel(UIIdentifiers.RESOURCE_DETAIL_GRID_PANEL).add(this.getContainer(), true);
	}

	private void openProfileInPinnedResources() {
		List<ModelData> selectedElems = this.getSelection();
		for (ModelData selectedElem : selectedElems) {
			String type = ((Object) selectedElem.get("Type")).toString();
			String resID = ((Object) selectedElem.get("ID")).toString();
			String scope = StatusHandler.getStatus().getCurrentScope();
			Commands.doGetResourceProfile(this, scope, type, resID);
		}
	}
}
