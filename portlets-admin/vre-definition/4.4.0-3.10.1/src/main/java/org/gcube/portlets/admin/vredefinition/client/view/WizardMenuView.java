package org.gcube.portlets.admin.vredefinition.client.view;

import java.util.ArrayList;

import org.gcube.portlets.admin.vredefinition.client.model.WizardStepModel;
import org.gcube.portlets.admin.vredefinition.client.presenter.WizardMenuViewPresenter;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class WizardMenuView extends Composite implements WizardMenuViewPresenter.Display{

	private TreePanel<ModelData> tree;
	private VREDefinitionView parentView;
	

	public WizardMenuView(WizardStepModel treeModel) {
	
		TreeStore<ModelData> store = new TreeStore<ModelData>();
		// The root node will not be shown
		store.add(treeModel.getChildren(), true);
		tree =  new TreePanel<ModelData>(store);

		tree.setWidth(300);
		
		tree.setDisplayProperty("name");
		// The icon decorator
		tree.setIconProvider(
				new ModelIconProvider<ModelData>() {
					public AbstractImagePrototype getIcon(final ModelData model) {
						if (model.get("icon") != null) {
							return IconHelper.createStyle((String) model.get("icon"));
						}
						return IconHelper.createStyle("defaultleaf-icon");
					}
				});
		tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		initComponent(tree);

	}


	public TreePanel<ModelData> getTreeMenu() {
		// TODO Auto-generated method stub
		return tree;
	}
	
}
