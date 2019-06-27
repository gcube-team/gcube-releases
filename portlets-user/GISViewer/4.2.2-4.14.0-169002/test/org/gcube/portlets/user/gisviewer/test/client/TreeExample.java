package org.gcube.portlets.user.gisviewer.test.client;

/* 
 * Ext GWT 2.2.5 - Ext for GWT 
 * Copyright(c) 2007-2010, Ext JS, LLC. 
 * licensing@extjs.com 
 *  
 * http://extjs.com/license 
 */

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.CheckCascade;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.CheckNodes;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;

public class TreeExample extends LayoutContainer {

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		//setLayout(new FitLayout());
		setSize("100%", "100%");
		setBorders(true);

		// creazione dello store
		TreeStore<ModelData> store = new TreeStore<ModelData>();
		
		// creazione tree panel
		TreePanel<ModelData> tree = new TreePanel<ModelData>(store);
		
		tree.setTrackMouseOver(false);
		tree.setCheckable(true);
		tree.setCheckNodes(CheckNodes.LEAF);
		tree.setCheckStyle(CheckCascade.NONE);
		tree.setDisplayProperty("nome");
		
		
		ModelData m = new BaseModelData();
		m.set("nome", "ciao");
		
		ModelData mnew = new BaseModelData();
		mnew.set("nome", "new!");

		ModelData mnew2 = new BaseModelData();
		mnew2.set("nome", "new!");

		ModelData m2 = new BaseModelData();
		m2.set("nome", "ciccio");

		ModelData m3 = new BaseModelData();
		m3.set("nome", "ceras");

		store.add(m, false);		
		store.add(mnew, false);		
		store.add(m, m2, false);
		store.add(m, m3, false);
		store.add(mnew, mnew2, false);
		
		tree.expandAll();
		
		tree.setChecked(m2, true);
		
		List<ModelData> a = tree.getCheckedSelection();

	    // change in node check state
	    tree.addListener(Events.CheckChange, new Listener<TreePanelEvent<ModelData>>() {
	      public void handleEvent(TreePanelEvent<ModelData> be) {	    	  
	    	  Window.alert("oh "+(be.isChecked()?"yes":"no")+"!");
	      }
	    });

		add(tree);
		//tree.getSelectionModel().select(store.getRootItems(), true);
	}

}
