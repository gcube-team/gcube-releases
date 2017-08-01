package org.gcube.application.aquamaps.aquamapsportlet.client.selections;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.AreaType;
import org.gcube.portlets.widgets.gcubelivegrid.client.data.BufferedStore;
import org.gcube.portlets.widgets.gcubelivegrid.client.livegrid.BufferedGridPanel;
import org.gcube.portlets.widgets.gcubelivegrid.client.livegrid.BufferedGridToolbar;
import org.gcube.portlets.widgets.gcubelivegrid.client.livegrid.BufferedGridView;
import org.gcube.portlets.widgets.gcubelivegrid.client.livegrid.BufferedRowSelectionModel;

import com.allen_sauer.gwt.log.client.Log;
import com.gwtext.client.core.SortDir;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.event.StoreListenerAdapter;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListener;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.RowSelectionListener;
import com.gwtext.client.widgets.grid.event.RowSelectionListenerAdapter;

public class ExtendedLiveGrid extends BufferedGridPanel {
	protected AreaType type;
	public ToolbarButton addButton=new ToolbarButton("Add");
	public ToolbarButton removeButton=new ToolbarButton("Remove");
	public ToolbarButton useAllButton=new ToolbarButton("Use All");
	
	
	protected BufferedRowSelectionModel brsm ;
	
	protected RowSelectionListener selectionListener=new RowSelectionListenerAdapter(){
		
		public void onSelectionChange(RowSelectionModel sm) {		
			super.onSelectionChange(sm);
			if(sm.hasSelection()) {
				removeButton.enable();
				addButton.enable();				
			}
			else {
				removeButton.disable();
				addButton.disable();				
			}
		}
	};
	
	
	
	public ExtendedLiveGrid(String title,final BufferedStore store,ColumnModel colmodel,boolean singleSelection) {
		this.setTitle(title);
		this.setFrame(true);		
		
		this.setPaddings(0);		
		brsm = new BufferedRowSelectionModel(singleSelection);
		if(singleSelection)useAllButton.hide();
		useAllButton.setEnableToggle(true);
		
        BufferedGridView view = new BufferedGridView();
        view.setLoadMask("Wait ...");
        view.setEmptyText("Please, click the refresh button below to load remote data");
        view.setNearLimit(30);       
        BufferedGridToolbar toolbar = new BufferedGridToolbar(view);
        toolbar.setDisplayInfo(true);
        
        toolbar.addButton(useAllButton);
        toolbar.addButton(addButton);
		toolbar.addButton(removeButton);	
        
        
        

        store.addStoreListener(new StoreListenerAdapter(){
        	
        	public void onLoad(Store store, Record[] records) {
        		if(records.length>0){
        			brsm.selectFirstRow();
        		}        	
        	}
        });
        store.setDefaultSort(colmodel.getDataIndex(0), SortDir.ASC);
        this.setStore(store);
        this.setColumnModel(colmodel);
        this.setEnableDragDrop(false);
        this.setSelectionModel(brsm);
        this.setView(view);
        this.setBottomToolbar(toolbar);
        this.setWidth(600);
        this.setHeight(400);
        this.getView().setAutoFill(true);      
        this.setStripeRows(true);
        this.setAutoScroll(true);		
        this.getSelectionModel().addListener(selectionListener);
        this.setFrame(true);
			
		addButton.disable();
		removeButton.disable();	
		addButton.hide();
		removeButton.hide();
		this.addListener(new PanelListenerAdapter(){
			public void onShow(Component component) {
				store.reload();
				Log.debug("ExtendedLiveGrid should be refreshed - onShow");
			}
			@Override
			public void onActivate(Panel panel) {
				store.reload();
				Log.debug("ExtendedLiveGrid should be refreshed - onActivate");
			}
		});
		
	}
	
	public void setAdder(ButtonListener toAddListener){
		addButton.addListener(toAddListener);
		addButton.show();
	}
	
	public void setRemover(ButtonListener toAddListener){
		removeButton.addListener(toAddListener);
		removeButton.show();
	}
	
	
}
