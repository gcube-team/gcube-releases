package org.gcube.application.aquamaps.aquamapsspeciesview.client.maps;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.ExtendedLiveGridView;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.PortletCommon;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.Reloadable;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.CompoundMapItem;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.AnchorData;
import com.extjs.gxt.ui.client.widget.layout.AnchorLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LiveToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

public class MapsGrid extends ContentPanel implements Reloadable{

	
	ExtendedLiveGridView liveGridView = new ExtendedLiveGridView(); 

	private Grid<ModelData> grid;
	
	public MapsGrid(ListStore<ModelData> store) {
		setLayout(new AnchorLayout());
		setHeaderVisible(false);
		setHeight(300);
		List<ColumnConfig> classicColumns = new ArrayList<ColumnConfig>();  

		ColumnConfig title = new ColumnConfig(CompoundMapItem.TITLE, "Title", 100);  
		classicColumns.add(title);
		
		ColumnConfig author = new ColumnConfig(CompoundMapItem.AUTHOR, "Author", 150);  
		classicColumns.add(author);  

		ColumnConfig typeCol = new ColumnConfig(CompoundMapItem.TYPE, "Map Type", 100);  
		classicColumns.add(typeCol);
		
		ColumnConfig algoCol = new ColumnConfig(CompoundMapItem.ALGORITHM, "Algorithm", 100);  
		classicColumns.add(algoCol);
		
		ColumnConfig customCol = new ColumnConfig(CompoundMapItem.CUSTOM, "Is Customized", 100);
		customCol.setRenderer(PortletCommon.booleanRenderer);
		classicColumns.add(customCol);

		ColumnConfig generationCol = new ColumnConfig(CompoundMapItem.CREATION_DATE, "Generation Time", 150);
		generationCol.setRenderer(PortletCommon.timeRenderer);
		classicColumns.add(generationCol);
		
		
		ColumnConfig datatime = new ColumnConfig(CompoundMapItem.DATA_GENERATION_TIME, "Data Generation Time", 150);
		datatime.setRenderer(PortletCommon.timeRenderer);
		classicColumns.add(datatime);
		
		ColumnConfig imageCountCol = new ColumnConfig(CompoundMapItem.IMAGE_COUNT, "Static Images", 100);  
		classicColumns.add(imageCountCol);
		
		ColumnConfig gisEnabled = new ColumnConfig(CompoundMapItem.GIS, "Gis Enabled", 100);  
		gisEnabled.setRenderer(PortletCommon.booleanRenderer);
		classicColumns.add(gisEnabled);
		
		ColumnConfig layerId = new ColumnConfig(CompoundMapItem.LAYER_ID, "Layer ID", 150);
		classicColumns.add(layerId);
		
		ColumnConfig layerUrl = new ColumnConfig(CompoundMapItem.LAYER_URL, "Layer Url", 100);
		classicColumns.add(layerUrl);
		
		
		
			
		final ColumnModel classicColumnModel = new ColumnModel(classicColumns);
		
		grid=new Grid<ModelData>(store,classicColumnModel);
		grid.setLoadMask(true);  
		grid.setBorders(true);  
		grid.setStripeRows(true);
		grid.setAutoExpandColumn(CompoundMapItem.TITLE);  
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);  
		
		
		grid.setView(liveGridView);
		
		
		
		add(grid,new AnchorData("100% 100%"));
		ToolBar gridBottomToolbar=new ToolBar();
		LiveToolItem item = new LiveToolItem();
		item.bindGrid(grid);
		gridBottomToolbar.add(item); 
		setBottomComponent(gridBottomToolbar);
		Log.debug("LiveToolbar id is "+gridBottomToolbar.getId());
	}
	@Override
	protected void onShow() {		
		super.onShow();
		reload();
	}
	
	public void reload(){
		grid.getStore().getLoader().load();
	}
}