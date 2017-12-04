package org.gcube.portlets.admin.vredeployer.client.view.panels;

import org.gcube.portlets.admin.vredeployer.client.control.Controller;
import org.gcube.portlets.admin.vredeployer.client.model.AtomicTreeNode;
import org.gcube.portlets.admin.vredeployer.client.model.NodeType;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;


public class WestPanel extends ContentPanel { 

	private Controller controller;
	private TreePanel<ModelData> tree;

	final AtomicTreeNode info = new AtomicTreeNode("Info", "Info", "info-icon", NodeType.INFO);
	final AtomicTreeNode func = new AtomicTreeNode("Functionality", "Functionality", "functionality-icon",NodeType.FUNCTIONALITY);
	final AtomicTreeNode arch = new AtomicTreeNode("Architecture", "Architecture", "architecture-icon",NodeType.ARCHITECTURE);
	final AtomicTreeNode finalize = new AtomicTreeNode("Finalize", "Finalize", "play-icon",NodeType.REPORT);
	
	public WestPanel(Controller c) {

		controller = c;

		AtomicTreeNode root = new AtomicTreeNode("root");		

		root.add(info);
		//root.add(metadata);
		root.add(func);
		root.add(arch);
		root.add(finalize);
		


		TreeStore<ModelData> store = new TreeStore<ModelData>();
		// The root node will not be shown
		store.add(root.getChildren(), true);
		tree =  new TreePanel<ModelData>(store);
		tree.setWidth(300);
		tree.setDisplayProperty("name");

		add(tree);

		
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
		
		// Handles the selection
		tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		
		tree.addListener(Events.OnClick, new Listener<TreePanelEvent<ModelData>>() {

			public void handleEvent(TreePanelEvent<ModelData> be) {
				if (be.getType() == Events.OnClick) {
					AtomicTreeNode selectedModel = (AtomicTreeNode) be.getItem();	
					controller.treeItemClicked(selectedModel);
				}
			}
		});
	
		
	}
	
	/**
	 * 
	 */
	public void setDefaultSelected() {
		tree.getSelectionModel().select(true, info);
		controller.setDefaultTreeItemSelected();
	}
	
	public void disable() {
		removeAll();		
	}
}  
