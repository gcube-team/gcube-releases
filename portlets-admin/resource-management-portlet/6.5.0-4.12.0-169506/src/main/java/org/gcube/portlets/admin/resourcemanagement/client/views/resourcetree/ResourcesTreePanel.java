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
 * Filename: ResourcesTreePanel.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.views.resourcetree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.gcube.portlets.admin.resourcemanagement.client.utils.Commands;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.console.ConsoleMessageBroker;
import org.gcube.resourcemanagement.support.client.utils.StatusHandler;
import org.gcube.resourcemanagement.support.client.views.ResourceTypeDecorator;
import org.gcube.resourcemanagement.support.shared.types.datamodel.AtomicTreeNode;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;


class TreeSelectionListener implements Listener<TreePanelEvent<ModelData>> {
	private boolean doExpand = false;

	public void handleEvent(final TreePanelEvent<ModelData> be) {
		if (be.getType() == Events.Expand) {
			be.cancelBubble();
			doExpand = true;
			return;
		}

		if (be.getType() == Events.Collapse) {
			be.cancelBubble();
			doExpand = true;
			return;
		}

		if (be.getType() == Events.OnClick &&
				!be.isRightClick() && be.getItem() != null &&
				be.getItem() instanceof AtomicTreeNode) {
			if (!doExpand) {
				// do you stuff, right click detected for a click event
				AtomicTreeNode selectedModel = (AtomicTreeNode) be.getItem();
				// -- SELECTED A LEAF
				if (selectedModel != null && selectedModel.isLeaf()) {
					ConsoleMessageBroker.trace(this, "Selected leaf: " + ((AtomicTreeNode) selectedModel.getParent()).getNode() + "::" + selectedModel.getName());
					Commands.doFilterResourceDetailsGrid(this,
							StatusHandler.getStatus().getCurrentScope(),
							((AtomicTreeNode) selectedModel.getParent()).getNode(),
							selectedModel.getSubType());
				}
				// -- SELECTED A NOT LEAF NODE
				if (selectedModel != null && !selectedModel.isLeaf()) {
					ConsoleMessageBroker.trace(this, "Selected node: " + selectedModel.getNode());
					Commands.doLoadResourceDetailsGrid(this, StatusHandler.getStatus().getCurrentScope(), selectedModel.getNode());
				}
			}
			doExpand = false;
		}
	}
}




/**
 * @author Daniele Strollo
 * @author Massimiliano Assante (ISTI-CNR)
 */
public class ResourcesTreePanel {
	private TreePanel<ModelData> tree = null;
	
	private boolean isOn = false;
	private Timer t;
	private int counter;
	private	ModelData gHN = null;

	public ResourcesTreePanel(final HashMap<String, ArrayList<String>> rawmodel) {
		AtomicTreeNode model = new AtomicTreeNode("Resource", null);
		String icon = null;
		String label = null;
		counter = 0;
		if (rawmodel == null) {
			model.add(new AtomicTreeNode(ResourceTypeDecorator.Empty.getLabel(), null, ResourceTypeDecorator.Empty.getIcon()));
		} else {
			for (Entry<String, ArrayList<String>> elems : rawmodel.entrySet()) {
				String type = elems.getKey();
				List<String> subtypes = elems.getValue();

				try {
					icon = ResourceTypeDecorator.valueOf(type).getIcon();
					label = ResourceTypeDecorator.valueOf(type).getFWSName();
					//label = ResourceTypeDecorator.valueOf(type).getLabel();
				} catch (java.lang.IllegalArgumentException e) {
					label = type;
					icon = null;
				}

				AtomicTreeNode currType = new AtomicTreeNode(type, label, icon);

				for (String subtype : subtypes) {
					currType.add(new AtomicTreeNode(subtype, null, icon));
				}
				model.add(currType);
			}
		}

		final TreeStore<ModelData> store = new TreeStore<ModelData>();
		// The root node will not be shown
		store.add(model.getChildren(), true);
		// FIXME store.sort("sortIdx", SortDir.ASC);
		store.sort("name", SortDir.ASC);

		this.tree = new TreePanel<ModelData>(store);
		this.tree.setWidth(300);
		this.tree.setDisplayProperty("name");

		Menu contextMenu = new Menu();
		MenuItem refresh = new MenuItem("Refresh Tree") {
			@Override
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				Commands.refreshResourceTree();
			}
		};
		refresh.setIconStyle("refresh-icon");
		contextMenu.add(refresh);
		this.tree.setContextMenu(contextMenu);


		// The icon decorator
		this.tree.setIconProvider(
				new ModelIconProvider<ModelData>() {
					public AbstractImagePrototype getIcon(final ModelData model) {
						if (model.get("node") != null && model.get("icon") != null) {
							return IconHelper.createStyle((String) model.get("icon"));
						}
						return IconHelper.createStyle("defaultleaf-icon");
					}
				});

		// Handles the selection
		tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		TreeSelectionListener handler = new TreeSelectionListener();

		tree.addListener(Events.Expand, handler);
		tree.addListener(Events.Collapse, handler);
		tree.addListener(Events.OnClick, handler);

		/**
		 * show gHN list if the configuration says so.
		 */
		if (StatusHandler.getStatus().isLoadGHNatStartup()) {
			GWT.log(""+StatusHandler.getStatus().isLoadGHNatStartup());
		
			Commands.doLoadResourceDetailsGrid(this,
					StatusHandler.getStatus().getCurrentScope(),
					"GHN");
		
			//select the GHN
			for (ModelData md: store.getAllItems()) {
				if (md.get("node").toString().compareTo("GHN") == 0)
					gHN = md;
			}

			if (gHN != null) {
				tree.getSelectionModel().select(false, gHN);
				t = new Timer() {

					@Override
					public void run() {
						if (isOn) 
							tree.getSelectionModel().deselectAll();
						else 
							tree.getSelectionModel().select(false, gHN);
						t.schedule(80);
						isOn = !isOn;
						counter++;
						if (counter > 10)
							t.cancel();
					}
				};
				t.schedule(100);
			}
		}

	}

	public final TreePanel<ModelData> getWidget() {
		return this.tree;
	}
}
