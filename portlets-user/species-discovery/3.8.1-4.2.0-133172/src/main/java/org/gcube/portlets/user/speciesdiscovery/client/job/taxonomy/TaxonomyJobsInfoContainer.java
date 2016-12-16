package org.gcube.portlets.user.speciesdiscovery.client.job.taxonomy;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.util.Util;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class TaxonomyJobsInfoContainer extends LayoutContainer {

	private ColumnModel cm;
	private Grid<ModelData> grid;
	private ContentPanel cp;
	private GroupingStore<ModelData> store;

	public TaxonomyJobsInfoContainer(GroupingStore<ModelData> store) {

		this.store = store;
		this.store.groupBy(TaxonomyJobInfoFields.LOADING.getId());

		initContentPanel();

		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

		ColumnConfig name = Util.createColumnConfig(TaxonomyJobInfoFields.NAME, 230);
		columns.add(name);

		ColumnConfig status = Util.createColumnConfig(TaxonomyJobInfoFields.LOADING, 70);
		columns.add(status);

		cm = new ColumnModel(columns);
		
		final ColumnModel columnModel = cm;

		grid = new Grid<ModelData>(this.store, cm);
		
	    GroupingView view = new GroupingView();  
	    view.setShowGroupedColumn(true);  
	 	this.grid.setView(view);
	    
	    view.setGroupRenderer(new GridGroupRenderer() {  
	      public String render(GroupColumnData data) {  
	        String f = columnModel.getColumnById(data.field).getHeader();  
	        String l = data.models.size() == 1 ? "Item" : "Items";  
	        return f + ": " + data.group + " (" + data.models.size() + " " + l + ")";  
	      }  
		});  
	    
		grid.setBorders(true);
		grid.setStripeRows(true);
		grid.getView().setAutoFill(true);
		grid.setColumnLines(true);
		grid.setColumnReordering(true);
		grid.setStyleAttribute("borderTop", "none");
		
		cp.add(grid);
	}

	private void initContentPanel() {
		setLayout(new FitLayout());
		getAriaSupport().setPresentation(true);
		cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setBodyBorder(true);
		cp.setLayout(new FitLayout());
		cp.setButtonAlign(HorizontalAlignment.CENTER);
		cp.setLayout(new FitLayout());
//		cp.getHeader().setIconAltText("Grid Icon");
		cp.setSize(400, 250);

		add(cp);
	}
	
	public void updateStore(ListStore<ModelData> store){
	
		this.grid.getStore().removeAll();
//		for (ModelData modelData : store.getModels()){
//			this.grid.getStore().add(modelData);
//		}
		
		this.grid.getStore().add(store.getModels());
		
//		cp.layout();
	}

	public void setHeaderTitle(String title) {
		cp.setHeading(title);
//		cp.layout();
	}

}