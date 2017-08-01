package org.gcube.application.aquamaps.aquamapsspeciesview.client.species;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.constants.AquaMapsSpeciesViewConstants;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.DetailsParameter;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.fields.SpeciesFields;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Image;

public class SpeciesDetailsPanel extends ContentPanel{

	private Image image=new Image();

	private EditorGrid<DetailsParameter> grid;

	private GroupingStore<DetailsParameter> store = new GroupingStore<DetailsParameter>();  



	public SpeciesDetailsPanel() {
		setFrame(true);
		setFrame(true);  
		setSize(700, 450);  
		setLayout(new FitLayout());  
		
		image.setTitle("Fishbase picture");
		image.setAltText("");
		image.setSize("240", "240");

		
		//************************** GRID
		ContentPanel gridPanel=new ContentPanel();
		gridPanel.setLayout(new FitLayout());
		gridPanel.setHeading("Meta Information");
		gridPanel.setCollapsible(true);
		
		
		store.groupBy(DetailsParameter.PARAMETER_GROUP);
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();  

		ColumnConfig name = new ColumnConfig(DetailsParameter.PARAMETER_NAME, "Parameter", 100);
		columns.add(name);
		ColumnConfig value = new ColumnConfig(DetailsParameter.PARAMETER_VALUE, "Value", 100);
		columns.add(value);
		value.setEditor(new CellEditor(new TextField<Object>()){
			
		});
		ColumnModel cm = new ColumnModel(columns); 

		GroupingView view = new GroupingView();  
		view.setShowGroupedColumn(false);  
		view.setForceFit(true);
		view.setAutoFill(true);
		view.setSortingEnabled(false);
		view.setStartCollapsed(true);
		
		view.setGroupRenderer(new GridGroupRenderer() {  
			public String render(GroupColumnData data) { 

				String l = data.models.size() == 1 ? "Item" : "Items";  
				return data.group + " (" + data.models.size() + " " + l + ")";  
			}  
		});


		grid = new EditorGrid<DetailsParameter>(store, cm); 
		grid.addListener(Events.AfterEdit, new Listener<GridEvent<DetailsParameter>>() {
            public void handleEvent(GridEvent<DetailsParameter> be) {
            		be.getRecord().reject(false);
            		be.getRecord().cancelEdit();
             }
         });
		grid.addListener(Events.BeforeEdit, new Listener<GridEvent<DetailsParameter>>() {
            public void handleEvent(GridEvent<DetailsParameter> be) {
            	
            		be.getRecord().beginEdit();
            	
             }
         });
		grid.setHideHeaders(true);
		grid.setView(view);  
		grid.setBorders(true);
		grid.setHeight(300);
		grid.setWidth(340);
		grid.setStripeRows(true);

		gridPanel.add(grid);

		add(image);
		add(gridPanel);
		setHeading("Species Details");	   
		setScrollMode(Scroll.AUTOY);
		//	    setIcon(Resources.ICONS.table());  
		
	}

	public void setSpeciesData(ModelData data){
		mask("Loading details..");
		String scientificName=data.get(SpeciesFields.genus+"").toString()+" "+data.get(SpeciesFields.species+"").toString();
		setHeading(scientificName+" details.");
		image.setUrl(data.get(SpeciesThumbsView.PATH).toString());
		image.setAltText("Fish Base image for "+scientificName);
		image.setTitle(scientificName+" from FishBase");
		image.setPixelSize(240, 240);
		List<DetailsParameter> toAdd=new ArrayList<DetailsParameter>();
		for(SpeciesFields f:AquaMapsSpeciesViewConstants.nameSpeciesFields){
			try{
				toAdd.add(new DetailsParameter(AquaMapsSpeciesViewConstants.speciesFieldsNames.get(f), data.get(f+"").toString(), "Species Names"));
			}catch(Throwable t){
				toAdd.add(new DetailsParameter(AquaMapsSpeciesViewConstants.speciesFieldsNames.get(f), "N/A", "Species Names"));
			}
		}
		for(SpeciesFields f:AquaMapsSpeciesViewConstants.taxonomySpeciesFields){
			try{
				toAdd.add(new DetailsParameter(AquaMapsSpeciesViewConstants.speciesFieldsNames.get(f), data.get(f+"").toString(), "Species Taxonomy"));
			}catch(Throwable t){
				toAdd.add(new DetailsParameter(AquaMapsSpeciesViewConstants.speciesFieldsNames.get(f), "N/A", "Species Taxonomy"));
			}
		}
		for(SpeciesFields f:AquaMapsSpeciesViewConstants.characteristicsSpeciesFields){
			try{
				toAdd.add(new DetailsParameter(AquaMapsSpeciesViewConstants.speciesFieldsNames.get(f), 
						(Integer.parseInt((String)data.get(f+""))==1)?"true":"false", "Species Characteristics"));
			}catch(Throwable t){
				toAdd.add(new DetailsParameter(AquaMapsSpeciesViewConstants.speciesFieldsNames.get(f),"N/A","Species Characteristics"));
			}
		}
		for(SpeciesFields f:AquaMapsSpeciesViewConstants.codesSpeciesFields){
			try{
				toAdd.add(new DetailsParameter(AquaMapsSpeciesViewConstants.speciesFieldsNames.get(f), data.get(f+"").toString(), "Species Codes"));
			}catch(Throwable t){
				toAdd.add(new DetailsParameter(AquaMapsSpeciesViewConstants.speciesFieldsNames.get(f), "N/A", "Species Codes"));
			}
		}
		store.removeAll();
		store.add(toAdd);		
		unmask();
		Log.debug("store contains : "+store.getModels().size());
	}


}
