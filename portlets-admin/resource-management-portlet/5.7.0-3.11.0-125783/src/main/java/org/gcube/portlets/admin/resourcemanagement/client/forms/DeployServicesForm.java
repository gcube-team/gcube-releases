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
 * Filename: DeployServicesForm.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.forms;

import java.util.List;
import java.util.Vector;
import org.gcube.portlets.admin.resourcemanagement.client.remote.ProxyRegistry;
import org.gcube.portlets.admin.resourcemanagement.client.utils.Callbacks;
import org.gcube.portlets.admin.resourcemanagement.client.utils.Commands;
import org.gcube.portlets.admin.resourcemanagement.client.views.resourcedetails.ResourceGridFactory;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.console.ConsoleMessageBroker;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.registry.UIIdentifiers;
import org.gcube.resourcemanagement.support.client.utils.StatusHandler;
import org.gcube.resourcemanagement.support.client.views.ResourceTypeDecorator;
import org.gcube.resourcemanagement.support.shared.operations.SupportedOperations;
import org.gcube.resourcemanagement.support.shared.types.datamodel.ResourceDescriptor;
import org.gcube.resourcemanagement.support.shared.types.datamodel.ResourceDetailModel;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DualListField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * @author Daniele Strollo
 * @author Massimiliano Assante  (ISTI-CNR)
 * @version 2.1 APR 2012
 */
public class DeployServicesForm {
	private ContentPanel rootPanel = null;
	private ContentPanel ghnContainer = null;
	private ContentPanel serviceContainer = null;
	private final ListStore<ModelData> GHNstore = new ListStore<ModelData>();
	private final ListStore<ModelData> selectedGHNs = new ListStore<ModelData>();
	private final Window window = new Window();
	private int resourceLoaded = 0;


	public DeployServicesForm() {
		this.ghnContainer = new ContentPanel(new FitLayout());
		this.serviceContainer = new ContentPanel(new FitLayout());

		this.ghnContainer.getHeader().setStyleName("x-hide-panel-header");
		this.ghnContainer.setHeaderVisible(false);
		this.serviceContainer.getHeader().setStyleName("x-hide-panel-header");
		this.serviceContainer.setHeaderVisible(false);

		this.ghnContainer.add(new Text());
		this.serviceContainer.add(new Text());
		this.init();
	}

	private void markResourceLoaded() {
		this.resourceLoaded++;
		if (this.resourceLoaded >= 2) {
			Commands.unmask(Commands.getViewport());
			Commands.unmask(this.rootPanel);
		}
	}

	private void loadGHNs(final String scope) {
		ProxyRegistry.getProxyInstance().getResourcesModel(scope,
				ResourceTypeDecorator.GHN.name(),
				null,
				null,
				new AsyncCallback<List<ResourceDescriptor>>() {

			public void onSuccess(final List<ResourceDescriptor> result) {
				ghnContainer.removeAll();
				ghnContainer.add(createGHNForm());
				GHNstore.add(result);
				ghnContainer.layout(true);
				Commands.showPopup("GHN load", "Loaded " + result.size() + " GHNs");
				markResourceLoaded();
			}
			public void onFailure(final Throwable caught) {
				markResourceLoaded();
			}
		});
	}

	private void closeDialog() {
		this.window.hide();
	}

	private Component createGHNForm() {
		FormPanel panel = new FormPanel();
		panel.setFrame(true);
		panel.getHeader().setStyleName("x-hide-panel-header");

		DualListField<ModelData> lists = new DualListField<ModelData>();
		lists.setFieldLabel("GHNs");

		ListField<ModelData> from = lists.getFromList();
		from.setDisplayField("name");
		GHNstore.setStoreSorter(new StoreSorter<ModelData>());
		GHNstore.setSortField("name");
		from.setStore(GHNstore);
		ListField<ModelData> to = lists.getToList();
		to.setDisplayField("name");
		to.setStore(this.selectedGHNs);

		panel.addButton(new Button("Cancel") {
			@Override
			protected void onClick(final ComponentEvent ce) {
				super.onClick(ce);
				closeDialog();
			}
		});
		panel.addButton(new Button("Apply Deploy") {
			@Override
			protected void onClick(final ComponentEvent ce) {
				super.onClick(ce);
				MessageBox.confirm("Software Deployment",
						"Are you sure you want to apply the deployment plan?",
						new Listener<MessageBoxEvent>() {
					public void handleEvent(final MessageBoxEvent be) {
						if (be.getButtonClicked().getItemId().equalsIgnoreCase("yes")) {
							// - THE OPERATION IS CONFIRMED
							List<ModelData> ghns = getSelectedGHNs();
							List<ModelData> sw = getSelectedSoftwares();

							if (ghns == null || ghns.size() == 0) {
								MessageBox.alert("Service Deployment", "No GHNs have been selected", null);
								return;
							}
							if (sw == null || sw.size() == 0) {
								MessageBox.alert("Service Deployment", "No softwares have been selected", null);
								return;
							}

							List<String> ghnNames = new Vector<String>();
							for (ModelData m : ghns) {
								String name = m.get("name").toString();
								ghnNames.add(name);											
							}
							List<String> swIDs = new Vector<String>();
							for (ModelData m : sw) {
								swIDs.add(((Object) m.get("ID")).toString());
							}
							ConsoleMessageBroker.info(this, "Applying deployment of " + swIDs.size() + " softwares on " + ghnNames.size() + " gHNs");
							Commands.mask("Waiting the deployment report", UIIdentifiers.GLOBAL_STATUS_BAR_ID);
							ProxyRegistry.getProxyInstance().deploy(
									ghnNames,
									swIDs,
									new AsyncCallback<String>() {
										public void onSuccess(final String result) {
											if (result != null) {
												MessageBox.info("Service Deployment",
														"The required deployment has been applied.<br/>" +
														"The generated report ID is:<br/>" +
														"<b>" + result + "</b>",
														null);
												ProxyRegistry.getProxyInstance().checkDeployStatus(
														StatusHandler.getStatus().getCurrentScope(),
														result.trim(),
														Callbacks.handleGetDeploymentReport);
											} else {
												MessageBox.alert("Service Deployment",
														"The required deployment has been applied.<br/>" +
														"But the resulting report ID is null or invalid.",
														null);
												Commands.unmask(UIIdentifiers.GLOBAL_STATUS_BAR_ID);
											}
										}
										public void onFailure(final Throwable caught) {
											Commands.unmask(UIIdentifiers.GLOBAL_STATUS_BAR_ID);
											MessageBox.alert("Service Deployment error",
													"The required deployment has not been applied. " +
													"Received exception:<br/>" + caught.getMessage(),
													null);
										}
									});

							closeDialog();
						}
					}
				});

			}
		});

		panel.add(lists, new FormData("100%"));
		return panel;
	}

	private List<ModelData> getSelectedGHNs() {
		return this.selectedGHNs.getModels();
	}

	private List<ModelData> getSelectedSoftwares() {
		List<ModelData> retval = new Vector<ModelData>();
		if (serviceContainer.getWidget(0) == null ||
				!(serviceContainer.getWidget(0) instanceof Grid)) {
			return null;
		}
		@SuppressWarnings("unchecked")
		Grid<ModelData> grid = (Grid<ModelData>) serviceContainer.getWidget(0);
		if (grid.getSelectionModel().getSelectedItems() == null) {
			return null;
		}
		Object toDeploy = null;
		for (ModelData m : grid.getStore().getModels()) {
			toDeploy = m.get(ResourceDetailModel.SERVICE_INSTALL_KEY);
			if (toDeploy != null && Boolean.parseBoolean(toDeploy.toString())) {
				retval.add(m);
			}
		}
		return retval;
	}

	private void loadSoftwares(final String scope) {

		ProxyRegistry.getProxyInstance().getResourcesByType(
				scope,
				ResourceTypeDecorator.Service.name(),
				new AsyncCallback<List<String>>() {

					public void onSuccess(final List<String> result) {
						Grid<ModelData> grid =
							ResourceGridFactory.createGrid(ResourceTypeDecorator.InstallableSoftware.name(),
									result,
									null,
									false);

						/******************************************
						 * ADD TO INSTALL FEATURE
						 *****************************************/
						// 0 - INSTALL button
						MenuItem toInstall = new MenuItem("Mark for install") {
							@Override
							protected void onClick(final ComponentEvent be) {
								super.onClick(be);

								if (serviceContainer.getWidget(0) == null ||
										!(serviceContainer.getWidget(0) instanceof Grid)) {
									return;
								}
								@SuppressWarnings("unchecked")
								Grid<ModelData> grid = (Grid<ModelData>) serviceContainer.getWidget(0);
								if (grid.getSelectionModel().getSelectedItems() == null) {
									return;
								}

								for (ModelData e : grid.getSelectionModel().getSelectedItems()) {
									ConsoleMessageBroker.info(this, "Required install for: " + e.get("ServiceName") + " " + e.get("ID"));
									if (e.getProperties().containsKey(ResourceDetailModel.SERVICE_INSTALL_KEY) &&
											Boolean.parseBoolean(((Object) e.get(ResourceDetailModel.SERVICE_INSTALL_KEY)).toString()))
									{
										e.set(ResourceDetailModel.SERVICE_INSTALL_KEY, false);
									} else {
										e.set(ResourceDetailModel.SERVICE_INSTALL_KEY, true);
									}
									grid.getStore().update(e);
								}
							}
						};
						Commands.evaluateCredentials(
								toInstall,
								SupportedOperations.SERVICE_DEPLOY.getPermissions());

						Menu menu = new Menu();
						menu.add(toInstall);
						grid.setContextMenu(menu);
						/******************************************
						 * ENDOF ADD TO INSTALL FEATURE
						 *****************************************/

						serviceContainer.removeAll();
						serviceContainer.add(grid);
						serviceContainer.layout(true);
						Commands.showPopup("Service load", "Loaded " + result.size() + " services");
						markResourceLoaded();
					}

					public void onFailure(final Throwable caught) {
						markResourceLoaded();
					}
				});
	}

	private void init() {
		this.rootPanel = new ContentPanel();
		this.rootPanel.getHeader().setStyleName("x-hide-panel-header");
		this.rootPanel.setHeaderVisible(false);
		this.rootPanel.setLayout(new RowLayout(Orientation.VERTICAL));

		this.rootPanel.add(this.ghnContainer, new RowData(1, .4, new Margins(4)));
		this.rootPanel.add(this.serviceContainer, new RowData(1, .6, new Margins(0, 4, 0, 4)));

		String scope = StatusHandler.getStatus().getCurrentScope();

		Commands.mask("Loading Deployment Resources in scope: " + scope, Commands.getViewport());
		Commands.mask("Loading Deployment Resources in scope: " + scope, this.rootPanel);

		this.loadGHNs(scope);
		this.loadSoftwares(scope);
	}

	public final void show() {
		window.setPlain(true);
		window.setSize(800, 600);
		window.setHeading("Software Deployment");
		window.setLayout(new FitLayout());
		window.add(this.rootPanel);
		window.show();
		window.layout(true);
	}
}
