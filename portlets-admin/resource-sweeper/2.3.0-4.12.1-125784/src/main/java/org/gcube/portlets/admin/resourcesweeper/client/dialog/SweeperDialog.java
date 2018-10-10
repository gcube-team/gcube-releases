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
 * Filename: SweeperDialog.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcesweeper.client.dialog;

import java.util.List;

import org.gcube.portlets.admin.resourcesweeper.client.Commands;
import org.gcube.portlets.admin.resourcesweeper.client.async.SweeperService;
import org.gcube.portlets.admin.resourcesweeper.client.async.SweeperServiceAsync;
import org.gcube.portlets.admin.resourcesweeper.client.clientlogs.ConsoleMessageBroker;
import org.gcube.portlets.admin.resourcesweeper.client.grids.ResourceGridFactory;
import org.gcube.resourcemanagement.support.client.views.ResourceTypeDecorator;
import org.gcube.resourcemanagement.support.shared.types.datamodel.AtomicTreeNode;
import org.gcube.resourcemanagement.support.shared.util.SweeperActions;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.tips.QuickTip;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Daniele Strollo
 * @author Massimiliano Asssante (ISTI-CNR)
 *
 */
public class SweeperDialog {
	
	private final SweeperServiceAsync sweeperService = GWT.create(SweeperService.class);

	private ContentPanel treeContainer = null;
	private ContentPanel gridContainer = null;
	private Dialog dialog = null;

	public SweeperDialog(final String currentScope) {

		this.initDialog(currentScope);
		TreePanel<ModelData> tree = null;
		TreeStore<ModelData> store = new TreeStore<ModelData>();

		AtomicTreeNode model = new AtomicTreeNode("Resource", null);

		/// BUILDS the tree of operations for sweeper
		/// 1) GHN
		AtomicTreeNode ghn = new AtomicTreeNode(
				ResourceTypeDecorator.GHN.name(),
				ResourceTypeDecorator.GHN.getFWSName(),
				ResourceTypeDecorator.GHN.getIcon());

		AtomicTreeNode ghn_expired = new AtomicTreeNode(SweeperActions.GET_GHN_MOVE_TO_UNREACHABLE.name(),
				SweeperActions.GET_GHN_MOVE_TO_UNREACHABLE.getLabel(), ResourceTypeDecorator.Sweeper_GHN_Expired.getIcon());
		// setting tooltip
		ghn_expired.set("tooltip", SweeperActions.GET_GHN_MOVE_TO_UNREACHABLE.getTooltip());
		ghn_expired.set("operation", SweeperActions.GET_GHN_MOVE_TO_UNREACHABLE.getOperationDescription());

		AtomicTreeNode ghn_dead = new AtomicTreeNode(SweeperActions.GET_GHN_DELETE.name(),
				SweeperActions.GET_GHN_DELETE.getLabel(), ResourceTypeDecorator.Sweeper_GHN_Dead.getIcon());
		// setting tooltip
		ghn_dead.set("tooltip", SweeperActions.GET_GHN_DELETE.getTooltip());
		ghn_dead.set("operation", SweeperActions.GET_GHN_DELETE.getOperationDescription());

		ghn.add(ghn_expired);
		ghn.add(ghn_dead);
		model.add(ghn);

		/// 2) RUNNING INSTANCE
		AtomicTreeNode ri = new AtomicTreeNode(
				ResourceTypeDecorator.RunningInstance.name(),
				ResourceTypeDecorator.RunningInstance.getFWSName(),
				ResourceTypeDecorator.RunningInstance.getIcon());
		AtomicTreeNode ri_orphan = new AtomicTreeNode(SweeperActions.GET_RI_DELETE.name(),
				SweeperActions.GET_RI_DELETE.getLabel(), ResourceTypeDecorator.Sweeper_RI_Orphan.getIcon());
		// setting tooltip
		ri_orphan.set("tooltip", SweeperActions.GET_RI_DELETE.getTooltip());
		ri_orphan.set("operation", SweeperActions.GET_RI_DELETE.getOperationDescription());

		ri.add(ri_orphan);
		model.add(ri);

		// The root node will not be shown
		store.add(model.getChildren(), true);
		store.sort("name", SortDir.ASC);

		tree = new TreePanel<ModelData>(store) {
			protected void onClick(@SuppressWarnings("rawtypes") final TreePanelEvent be) {
				super.onClick(be);
				// do you stuff, right click detected for a click event
				final AtomicTreeNode selectedModel = (AtomicTreeNode) be.getItem();

				// -- SELECTED A LEAF
				if (selectedModel != null && selectedModel.isLeaf()) {
					ConsoleMessageBroker.trace(this,
							"Selected leaf: " + ((AtomicTreeNode) selectedModel.getParent()).getNode() + "::" + selectedModel.getNode());


					dialog.mask("Loading resource to cleanup, this may take a while", "loading-indicator");

					sweeperService.getSweepElems(currentScope, SweeperActions.valueOf(selectedModel.getNode()),
							/*
							 * the code to execute once the resources to cleanup are retrieved.
							 */
							new AsyncCallback<List<String>>() {
						public void onSuccess(final List<String> result) {
							dialog.unmask();
							Grid<ModelData> tmpgrid = null;
							// Cleanup the grid
							gridContainer.removeAll();
							if (selectedModel.getNode().equals(SweeperActions.GET_RI_DELETE.name())) {
								// The grid is created for orphanRI model
								tmpgrid = ResourceGridFactory.createGrid(ResourceTypeDecorator.Sweeper_RI.name(), result, null);
							} else {
								// The grid is created for GHN model
								tmpgrid = ResourceGridFactory.createGrid(ResourceTypeDecorator.Sweeper_GHN.name(), result, null);
							}
							if (tmpgrid == null) {
								Commands.showPopup("Sweeper grid creation", "No elements found for the chosen category");
								return;
							}
							final Grid<ModelData> grid = tmpgrid;

							gridContainer.add(grid);
							gridContainer.layout(true);

							Menu gridMnu = new Menu();
							gridMnu.add(new MenuItem("Apply Cleanup") {
								@Override
								protected void onClick(final ComponentEvent be) {
									super.onClick(be);
									final List<ModelData> selectedElems = grid.getSelectionModel().getSelection();

									dialog.mask("Applying the required operation, please wait", "loading-indicator");
									for (ModelData selectedModel : selectedElems) {
										ConsoleMessageBroker.info(this, ((Object) selectedModel.get("ID")).toString() + " :: " +((Object) selectedModel.get("Actions")).toString());
									}
									sweeperService.applySweep(currentScope, selectedElems, new AsyncCallback<Boolean>() {
										public void onSuccess(Boolean result) {
											dialog.unmask();
											if (result) {
												Commands.showPopup("Sweeper Result", "Success. Refresh operation suggested.");
												for (ModelData elem : selectedElems) 
													grid.getStore().remove(elem);														
											}
											else
												Commands.showPopup("Sweeper Result", "Error. Error on server, please check server logs.");
										}

										public void onFailure(final Throwable caught) {
											dialog.unmask();
											Commands.showPopup("Sweeper Result", "Failure");
										}
									});
								}
							});
							grid.setContextMenu(gridMnu);
						}

						public void onFailure(final Throwable caught) {
							Commands.showPopup("Sweeper", "Failure");
							dialog.unmask();
						}
					});
				}
			};
		};
		tree.setWidth("100%");
		tree.setHeight("100%");
		tree.setDisplayProperty("name");

		// Tooltip support on the tree
		tree.setLabelProvider(new ModelStringProvider<ModelData>() {
			public String getStringValue(final ModelData model, final String property) {
				String name = ((Object) model.get("name")).toString();
				try {
					String tooltip = ((Object) model.get("tooltip")).toString();
					String operation = ((Object) model.get("operation")).toString();
					return "<span qtip='<b>Sweep:</b> " + operation + "' qtitle='" + tooltip + "'>" + name + "</span>";
				} catch (Exception e) {
					// if not tooltip defined
					return "<span>" + name + "</span>";
				}
			}
		});
		new QuickTip(tree);

		// The icon decorator
		tree.setIconProvider(
				new ModelIconProvider<ModelData>() {
					public AbstractImagePrototype getIcon(final ModelData model) {
						if (model.get("node") != null && model.get("icon") != null) {
							return IconHelper.createStyle((String) model.get("icon"));
						}
						return IconHelper.createStyle("defaultleaf-icon");
					}
				});


		this.treeContainer.add(tree);

		// Handles the selection
		tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		this.dialog.show();
	}

	public final void initDialog(String scope) {
		this.dialog = new Dialog();
		this.dialog.setBodyBorder(false);
		this.dialog.setIconStyle("sweeper-dialog-icon");
		this.dialog.setHeading("Resource Sweeper " + "(" + scope + ")");
		this.dialog.setWidth(900);
		this.dialog.setHeight(450);
		this.dialog.setHideOnButtonClick(true);

		BorderLayout layout = new BorderLayout();
		this.dialog.setLayout(layout);

		// west
		this.treeContainer = new ContentPanel();
		this.treeContainer.setHeading("Resources");
		BorderLayoutData data = new BorderLayoutData(LayoutRegion.WEST, 150, 100, 250);
		data.setMargins(new Margins(0, 5, 0, 0));
		data.setSplit(true);
		data.setCollapsible(true);
		this.dialog.add(this.treeContainer, data);

		// center
		this.gridContainer = new ContentPanel();
		this.gridContainer.setLayout(new FitLayout());
		data = new BorderLayoutData(LayoutRegion.CENTER);
		this.dialog.add(this.gridContainer, data);
	}
}
