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
 * Filename: GenericTreePanel.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.widgets.panels;

import org.gcube.portlets.admin.resourcemanagement.shared.utils.XMLUtil;
import org.gcube.resourcemanagement.support.shared.types.datamodel.AtomicTreeNode;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class GenericTreePanel {
	private TreePanel<ModelData> tree = null;

	public GenericTreePanel(final String xml, final String rootNode) {
		AtomicTreeNode model = new AtomicTreeNode(rootNode, null);

		try {
			model.add(XMLUtil.XMLToTree(xml, rootNode));
		} catch (Exception e) {
			GWT.log("During profile conversion", e);
		}

		TreeStore<ModelData> store = new TreeStore<ModelData>();
		// The root node will not be shown
		store.add(model.getChildren(), true);

		store.sort("name", SortDir.ASC);

		this.tree = new TreePanel<ModelData>(store) {
			private boolean loaded = false;
			@Override
			protected void onLoad() {
				super.onLoad();
				if (loaded) {
					return;
				}
				// Expands the first level only
				this.setExpanded(this.getStore().getRootItems().get(0), true, false);
				this.loaded = true;
			}
		};
		this.tree.setWidth(300);
		this.tree.setDisplayProperty("name");

		this.tree.setHeight("100%");
		this.tree.setWidth("100%");

		Menu subMenu = new Menu();
		subMenu.add(new MenuItem("Expand All") {
			@Override
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				tree.expandAll();
			}
		});
		subMenu.add(new MenuItem("Collapse All") {
			@Override
			protected void onClick(final ComponentEvent be) {
				super.onClick(be);
				tree.collapseAll();
			}
		});
		this.tree.setContextMenu(subMenu);

		// The icon decorator
		this.tree.setIconProvider(
				new ModelIconProvider<ModelData>() {
					public AbstractImagePrototype getIcon(final ModelData model) {
						if (model instanceof AtomicTreeNode) {
							AtomicTreeNode m = (AtomicTreeNode) model;
							if (m.isLeaf()) {
								return IconHelper.createStyle("console-log-icon");
							} else {
								return IconHelper.createStyle("profile-folder-icon");
							}
						}
						return IconHelper.createStyle("defaultleaf-icon");
					}
				});
	}

	public final TreePanel<ModelData> getWidget() {
		return this.tree;
	}

}
