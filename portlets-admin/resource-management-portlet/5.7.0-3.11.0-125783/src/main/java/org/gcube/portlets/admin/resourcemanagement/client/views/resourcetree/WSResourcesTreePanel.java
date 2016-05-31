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
 * Filename: WSResourcesTreePanel.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.views.resourcetree;

import java.util.List;
import org.gcube.portlets.admin.resourcemanagement.client.utils.Commands;
import org.gcube.portlets.admin.resourcemanagement.client.widgets.console.ConsoleMessageBroker;
import org.gcube.resourcemanagement.support.client.utils.StatusHandler;
import org.gcube.resourcemanagement.support.client.views.ResourceTypeDecorator;
import org.gcube.resourcemanagement.support.shared.types.datamodel.AtomicTreeNode;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class WSResourcesTreePanel {
	private TreePanel<ModelData> tree = null;


	public WSResourcesTreePanel(final List<String> types) {
		AtomicTreeNode model = new AtomicTreeNode(ResourceTypeDecorator.WSResource.name(), null);

		if (types != null && types.size() > 0) {
			String elem = ResourceTypeDecorator.WSResource.name();
			String icon = ResourceTypeDecorator.valueOf(elem).getIcon();
			String label = ResourceTypeDecorator.valueOf(elem).getLabel();

			AtomicTreeNode currType = new AtomicTreeNode(elem, label, icon);

			for (String subtype : types) {
				currType.add(new AtomicTreeNode(subtype, null, icon));
			}
			model.add(currType);
		}

		TreeStore<ModelData> store = new TreeStore<ModelData>();
		// The root node will not be shown
		store.add(model.getChildren(), true);
		store.sort("name", SortDir.ASC);

		this.tree = new TreePanel<ModelData>(store);
		this.tree.setWidth(300);
		this.tree.setDisplayProperty("name");

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
		tree.getSelectionModel().addListener(Events.SelectionChange, new Listener<SelectionChangedEvent<AtomicTreeNode>>() {
			public void handleEvent(final SelectionChangedEvent<AtomicTreeNode> be) {
				AtomicTreeNode selectedModel = be.getSelectedItem();
				// -- SELECTED A LEAF
				if (selectedModel != null && selectedModel.isLeaf()) {
					ConsoleMessageBroker.trace(this, "Selected leaf of WS-Resource: " + ((AtomicTreeNode) selectedModel.getParent()).getNode() + "::" + selectedModel.getName());
					// FIXME the AtomicTreeNode does not contain a subtype information
					Commands.doFilterResourceDetailsGrid(this,
							StatusHandler.getStatus().getCurrentScope(),
							((AtomicTreeNode) selectedModel.getParent()).getNode(),
							selectedModel.getSubType());
				}
				// -- SELECTED A NOT LEAF NODE
				if (selectedModel != null && !selectedModel.isLeaf()) {
					ConsoleMessageBroker.trace(this, "Selected node: " + selectedModel.getNode());
					Commands.doLoadWSResourceDetailsGrid(this, StatusHandler.getStatus().getCurrentScope());
				}

				be.setCancelled(true);
				// Removes the selection so that it can be easily refreshed.
				tree.getSelectionModel().deselectAll();
			}
		});
	}

	public final TreePanel<ModelData> getWidget() {
		return this.tree;
	}
}
