package org.gcube.application.datamanagementfacilityportlet.client.forms;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.ClientResource;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.ClientTinyResource;

import com.extjs.gxt.ui.client.data.ModelComparer;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;

public class TinyResourceGrid extends Grid<ClientTinyResource>{

		
	public TinyResourceGrid() {
		this(getDefaultStore(),getDefaultColumnModel());
	}
	public TinyResourceGrid(ListStore<ClientTinyResource> store, ColumnModel cm){
		super(store,cm);
		setAutoExpandColumn(ClientResource.TITLE);  
		setBorders(true);  
		setStripeRows(true);  
		getView().setEmptyText("No resources selected");
		getView().setAutoFill(true);
		getView().setSortingEnabled(false);
		setHeight(250);
		store.setModelComparer(new ModelComparer<ClientTinyResource>() {
			
			public boolean equals(ClientTinyResource m1, ClientTinyResource m2) {
				return m1.getId().equals(m2.getId());
			}
		});
	}
	
	private static ListStore<ClientTinyResource> getDefaultStore(){
		 return new ListStore<ClientTinyResource>();
	}
	private static ColumnModel getDefaultColumnModel(){
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		ColumnConfig idCol = new ColumnConfig(ClientResource.SEARCH_ID+"", "ID", 30);
		columns.add(idCol);
		idCol.setMenuDisabled(true);
//		idCol.setRenderer(new GridCellRenderer<ClientTinyResource>() {
//			public Object render(ClientTinyResource model, String property,
//					ColumnData config, int rowIndex, int colIndex,
//					ListStore<ClientTinyResource> store,
//					Grid<ClientTinyResource> grid) {
//				Log.debug("Value is "+model.get(property));
//				return model.get(property);
//			}
//		});
		ColumnConfig titleCol = new ColumnConfig(ClientResource.TITLE+"", "Title", 100);
		columns.add(titleCol);
		titleCol.setMenuDisabled(true);
		ColumnConfig typeCol = new ColumnConfig(ClientResource.TYPE+"", "Type", 80);
		columns.add(typeCol);
		typeCol.setMenuDisabled(true);
		return new ColumnModel(columns);
	}
}
