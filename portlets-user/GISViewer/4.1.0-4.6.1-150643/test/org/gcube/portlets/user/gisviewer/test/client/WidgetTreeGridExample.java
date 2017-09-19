package org.gcube.portlets.user.gisviewer.test.client;

/* 
 * Ext GWT 2.2.5 - Ext for GWT 
 * Copyright(c) 2007-2010, Ext JS, LLC. 
 * licensing@extjs.com 
 *  
 * http://extjs.com/license 
 */  

import java.util.Arrays;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.WidgetTreeGridCellRenderer;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class WidgetTreeGridExample extends LayoutContainer {  

	@Override  
	protected void onRender(Element parent, int index) {  
		super.onRender(parent, index);  
		setLayout(new FlowLayout(10));  

		LayerFolder model = getTreeModel();

		TreeStore<ModelData> store = new TreeStore<ModelData>();  
		store.add(model.getChildren(), true);  

		ColumnConfig name = new ColumnConfig("name", "Name", 100);
		name.setRenderer(new WidgetTreeGridCellRenderer<ModelData>(){
			@Override
			public Widget getWidget(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				
				if (model.get("type").equals("music")) {
					VerticalPanel lc = new VerticalPanel();
					lc.setSize(200, 100);
					lc.setBorders(true);
					lc.add(new Button("ciao"));
					lc.add(new Button("ciao2"));
					lc.add(new Button("ciao3"));
					
					return lc;
				} else {
					Button b = new Button((String)model.get(property));
					b.setToolTip("Click for more information");
					return b;
				}
			}
		});

		ColumnConfig date = new ColumnConfig("author", "Author", 100);  
		ColumnConfig size = new ColumnConfig("genre", "Genre", 100);
		ColumnModel cm = new ColumnModel(Arrays.asList(name, date, size));  

		ContentPanel cp = new ContentPanel();  
		cp.setBodyBorder(false);
		cp.setHeading("Widget Renderer TreeGrid");  
		cp.setButtonAlign(HorizontalAlignment.CENTER);  
		cp.setLayout(new FitLayout());
		cp.setFrame(true);
		cp.setSize(600, 300);  

		final TreeGrid<ModelData> tree = new TreeGrid<ModelData>(store, cm);
		tree.setBorders(true);
		tree.setSize(400, 400);
		tree.setAutoExpandColumn("name");  
		tree.getTreeView().setRowHeight(26);  
		tree.getStyle().setLeafIcon(IconHelper.createStyle("icon-music"));		
		cp.add(tree);
		

		add(cp);
		add(new Button("dfg", new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				tree.getTreeView().getRow(1).getStyle().setHeight(200, Unit.PX);
			}
		}));
	}


	private LayerFolder getTreeModel() {
		LayerFolder[] folders = new LayerFolder[] {
			new LayerFolder("Beethoven", new Music[] {
				new Music("Six String Quartets", "Beethoven", "Quartets"),
			}),
			new LayerFolder("Brahms", new Music[] {
				new Music("Violin Concerto", "Brahms", "Concertos"),
			}),
			new LayerFolder("Mozart", new Music[] {
				new Music("Piano Concerto No. 12", "Mozart", "Concertos"),
			})
		};

		LayerFolder root = new LayerFolder("root");
		for (int i = 0; i < folders.length; i++) {
			root.add((LayerFolder) folders[i]);
		}

		return root;
	}  


	private class LayerFolder extends BaseTreeModel {

		private static final long serialVersionUID = 1L;

		public LayerFolder(String name) {
			set("name", name);
			set("type", "layer");
		}

		public LayerFolder(String name, BaseTreeModel[] children) {
			this(name);
			for (BaseTreeModel baseTreeModel : children)
				add(baseTreeModel);
		}

		public String getName() {
			return get("name");
		}

		public String toString() {
			return getName();
		}		
	}

	private class Music extends BaseTreeModel {
		private static final long serialVersionUID = 1L;

		public Music(String name, String author, String genre) {
			set("name", name);
			set("author", author);
			set("genre", genre);
			set("type", "music");
		}

		public String getName() {
			return (String) get("name");
		}

		public String toString() {
			return getName();
		}
	}  
} 



