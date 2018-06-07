package org.gcube.application.aquamaps.aquamapsspeciesview.client.species;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.ExtendedLiveGridView;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.PortletCommon;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.fields.SpeciesFields;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.AnchorData;
import com.extjs.gxt.ui.client.widget.layout.AnchorLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LiveToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

public class SpeciesGrid extends ContentPanel implements SpeciesView{

	
	ExtendedLiveGridView liveGridView = new ExtendedLiveGridView(); 

	private Grid<ModelData> grid;
	
	public SpeciesGrid(ListStore<ModelData> store) {
		setLayout(new AnchorLayout());
		setHeaderVisible(false);
		setHeight(300);
		List<ColumnConfig> classicColumns = new ArrayList<ColumnConfig>();  

		ColumnConfig scientificName = new ColumnConfig(SpeciesFields.scientific_name+"", "Scientific Name", 100);  
		classicColumns.add(scientificName);
		
		ColumnConfig genus = new ColumnConfig(SpeciesFields.genus+"", "Genus", 150);  
		classicColumns.add(genus);  

		ColumnConfig species = new ColumnConfig(SpeciesFields.species+"", "Species", 150);  
		classicColumns.add(species);

		ColumnConfig fbName = new ColumnConfig(SpeciesFields.fbname+"", "FB Name", 150);  
		classicColumns.add(fbName);
		
		ColumnConfig englishName = new ColumnConfig(SpeciesFields.english_name+"", "English_name", 150);  
		classicColumns.add(englishName);
		
		ColumnConfig frenchName = new ColumnConfig(SpeciesFields.french_name+"", "French Name", 150);  
		classicColumns.add(frenchName);
		
		ColumnConfig spanishName = new ColumnConfig(SpeciesFields.spanish_name+"", "Spanish Name", 150);  
		classicColumns.add(spanishName);
		
		
		ColumnConfig kingdom = new ColumnConfig(SpeciesFields.kingdom+"", "Kingdom", 100);  
		classicColumns.add(kingdom);
		kingdom.setHidden(true);
		
		ColumnConfig phylum = new ColumnConfig(SpeciesFields.phylum+"", "Phylum", 100);  
		classicColumns.add(phylum);
		phylum.setHidden(true);
		
		ColumnConfig classColumn = new ColumnConfig(SpeciesFields.classcolumn+"", "Class", 100);  
		classicColumns.add(classColumn);
		classColumn.setHidden(true);
		
		ColumnConfig orderColumn = new ColumnConfig(SpeciesFields.ordercolumn+"", "Order", 100);  
		classicColumns.add(orderColumn);
		orderColumn.setHidden(true);
		
		ColumnConfig family = new ColumnConfig(SpeciesFields.familycolumn+"", "Family", 100);  
		classicColumns.add(family);
		family.setHidden(true);
		
		
		
		ColumnConfig deepwater = new ColumnConfig(SpeciesFields.deepwater+"", "Deepwater", 75);
		deepwater.setRenderer(PortletCommon.booleanRenderer);
		classicColumns.add(deepwater);
		deepwater.setHidden(true);
		
		ColumnConfig mammal = new ColumnConfig(SpeciesFields.m_mammals+"", "Mammal", 75);
		mammal.setRenderer(PortletCommon.booleanRenderer);
		classicColumns.add(mammal);
		mammal.setHidden(true);
		
		ColumnConfig angling = new ColumnConfig(SpeciesFields.angling+"", "Angling", 75);
		angling.setRenderer(PortletCommon.booleanRenderer);
		classicColumns.add(angling);
		angling.setHidden(true);
		
		ColumnConfig diving = new ColumnConfig(SpeciesFields.diving+"", "Diving", 75);
		diving.setRenderer(PortletCommon.booleanRenderer);
		classicColumns.add(diving);
		diving.setHidden(true);		
		
		ColumnConfig dangerous = new ColumnConfig(SpeciesFields.dangerous+"", "Dangerous", 75);
		dangerous.setRenderer(PortletCommon.booleanRenderer);
		classicColumns.add(dangerous);
		dangerous.setHidden(true);
		
		ColumnConfig invertebrate = new ColumnConfig(SpeciesFields.m_invertebrates+"", "Invertebrate", 75);
		invertebrate.setRenderer(PortletCommon.booleanRenderer);
		classicColumns.add(invertebrate);
		invertebrate.setHidden(true);
		
		ColumnConfig algae = new ColumnConfig(SpeciesFields.algae+"", "Algae", 75);
		algae.setRenderer(PortletCommon.booleanRenderer);
		classicColumns.add(algae);
		algae.setHidden(true);
		
		ColumnConfig seabird = new ColumnConfig(SpeciesFields.seabirds+"", "Sea Bird", 75);
		seabird.setRenderer(PortletCommon.booleanRenderer);
		classicColumns.add(seabird);
		seabird.setHidden(true);
		
		ColumnConfig freshwater = new ColumnConfig(SpeciesFields.freshwater+"", "Fresh Water", 75);
		freshwater.setRenderer(PortletCommon.booleanRenderer);
		classicColumns.add(freshwater);
		freshwater.setHidden(true);
		
		ColumnConfig speciesId = new ColumnConfig(SpeciesFields.speciesid+"", "Species Id", 100);  
		classicColumns.add(speciesId);
		speciesId.setHidden(true);
		
		ColumnConfig specCode = new ColumnConfig(SpeciesFields.speccode+"", "Species Code", 100);  
		classicColumns.add(specCode);
		specCode.setHidden(true);
		
		final ColumnModel classicColumnModel = new ColumnModel(classicColumns);
		
		grid=new Grid<ModelData>(store,classicColumnModel);
		grid.setLoadMask(true);  
		grid.setBorders(true);  
		grid.setStripeRows(true);
		grid.setAutoExpandColumn(SpeciesFields.scientific_name+"");  
		grid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);  
		
		
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
	@Override
	public void bindToSelection(final Button toBind) {
		grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				if(se.getSelection().size()>0)toBind.enable();
				else toBind.disable();
			}
		});
	}
	@Override
	public List<ModelData> getSelection() {
		return grid.getSelectionModel().getSelectedItems();
	}
	
	
	
	
	
}
