package org.gcube.portlets.admin.vredefinition.client.view;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.vredefinition.client.AppController;
import org.gcube.portlets.admin.vredefinition.client.event.ExternalResourceSelectionEvent;
import org.gcube.portlets.admin.vredefinition.client.model.VREFunctionalityModel;
import org.gcube.portlets.admin.vredefinition.shared.ExternalResourceModel;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 * @version 0.2 Sep 2012
 * 
 */
public class ResourcesGrid {

	private List<ExternalResourceModel> myResourcesList;
	private VREFunctionalityModel myFunc;
	public ResourcesGrid(VREFunctionalityModel func, List<ExternalResourceModel> list) {
		myFunc = func;
		myResourcesList = list;
	}
	public ContentPanel getGrid() {

		ArrayList<ColumnConfig> configs = new ArrayList<ColumnConfig>();  
		
		XTemplate tpl = XTemplate.create("<div class=\"resourceText\"><b>Resource name:</b> {name}</div><div class=\"resourceText\"><b>Description:</b> {description}<div>");  
		  
	    RowExpander expander = new RowExpander();  
	    expander.setTemplate(tpl);  
	  
	    configs.add(expander);  

		ColumnConfig column = new ColumnConfig();  		

		column.setId("id");  
		column.setHeader("ID");  
		column.setWidth(50);  
		column.setHidden(true);
		configs.add(column);  

		column = new ColumnConfig();  
		column.setId("name");  
		column.setHeader("Name");  
		column.setWidth(100);  
		configs.add(column);  

		column = new ColumnConfig();  
		column.setId("description");  
		column.setHeader("Description"); 
		column.setWidth(200);  
		configs.add(column);  

		column = new ColumnConfig();  
		column.setId("category");  
		column.setHeader("Type"); 
		column.setHidden(true);
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setWidth(30);  
		configs.add(column); 

		CheckColumnConfig checkColumn = new CheckColumnConfig("isSelected", "isSelected", 35) {
			@Override
			protected void onMouseDown(final GridEvent<ModelData> ge) {
				super.onMouseDown(ge);
				ExternalResourceModel bean = (ExternalResourceModel) ge.getModel();
				//TODO:
			}	
		};
		checkColumn.setHeader("Select");  
		checkColumn.setAlignment(HorizontalAlignment.CENTER);

		CheckBox checkbox = new CheckBox();

		CellEditor checkBoxEditor = new CellEditor(checkbox);

		checkColumn.setEditor(checkBoxEditor);
		configs.add(checkColumn);  

		final ColumnModel cm = new ColumnModel(configs); 

		/**
		 * load the grid data
		 */
		final GroupingStore<ExternalResourceModel> store = new GroupingStore<ExternalResourceModel>();
		store.groupBy("category");

		//store.sort("isSelectable", SortDir.DESC);
		store.add(myResourcesList);  


		Grid<ExternalResourceModel> grid = new Grid<ExternalResourceModel>(store, cm);

		GroupingView view = new GroupingView();  
		view.setShowGroupedColumn(false);  
		view.setForceFit(true);  
		grid.setView(view);  
		grid.addPlugin(expander);  
		grid.addPlugin(checkColumn);
		grid.setStyleAttribute("borderTop", "none"); 
		grid.setAutoExpandColumn("name"); 
		grid.setBorders(true); 
		grid.setStripeRows(true);
		grid.getView().setForceFit(true);

		ContentPanel gridPanel = new ContentPanel(new FitLayout());
		gridPanel.setHeaderVisible(false);
		gridPanel.add(grid); 	
		gridPanel.setHeight(400);

		gridPanel.addButton(new Button("Reset", new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {  
				store.rejectChanges();  
			}  
		}));  



		gridPanel.addButton(new Button("Commit changes", new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {  
				List<ExternalResourceModel> resources = store.getModels();
				store.commitChanges();
				AppController.getAppController().getBus().fireEvent(new ExternalResourceSelectionEvent(myFunc, resources));
				
			}  
		}));  

		return gridPanel;
	}


}

