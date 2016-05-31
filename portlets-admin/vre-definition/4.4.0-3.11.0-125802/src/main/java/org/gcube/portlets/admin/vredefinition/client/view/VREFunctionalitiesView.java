package org.gcube.portlets.admin.vredefinition.client.view;

import java.util.List;

import org.gcube.portlets.admin.vredefinition.client.model.VREFunctionalityModel;
import org.gcube.portlets.admin.vredefinition.client.presenter.VREFunctionalitiesPresenter;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.CheckCascade;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelSelectionModel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class VREFunctionalitiesView extends Composite implements VREFunctionalitiesPresenter.Display{

	private TreePanel<ModelData> tree;
	private LayoutContainer container;

	public VREFunctionalitiesView() {
		
		
		container = new LayoutContainer();
		container.setScrollMode(Scroll.AUTO);
		container.setSize("100%","100%");
		
		initComponent(container);
	}


	public void setData(List<ModelData> categories) {
		TreeStore<ModelData> store = new TreeStore<ModelData>();
		store.add(categories, true);
		tree = new TreePanel<ModelData>(store);
	
		tree.setCheckable(true);
		tree.setWidth(300);
		tree.setDisplayProperty("name");
		
			// Handles the selection
		TreePanelSelectionModel<ModelData> sm = new TreePanelSelectionModel<ModelData>();
		tree.setSelectionModel(sm);
		
		tree.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
		tree.setCheckStyle(CheckCascade.CHILDREN);
		
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
		 
		tree.expandAll();
		tree.setAutoExpand(true);

		
		
		container.removeAll();
		container.add(tree);
		container.layout();
		
		// Set node checked after layout
		for(ModelData functionality : tree.getStore().getAllItems()) {
			tree.setChecked(functionality,((VREFunctionalityModel)functionality).isSelected());
			
		}
		
	}
	

	public TreePanel<ModelData> getTreeFunctionalities() {
		// TODO Auto-generated method stub
		return tree;
	}

	
}
