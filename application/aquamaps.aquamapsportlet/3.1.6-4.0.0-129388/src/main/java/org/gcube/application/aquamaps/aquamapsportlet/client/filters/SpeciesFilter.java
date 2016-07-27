package org.gcube.application.aquamaps.aquamapsportlet.client.filters;



import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsportlet.client.AquaMapsPortlet;
import org.gcube.application.aquamaps.aquamapsportlet.client.RecordDefinitions;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.AquaMapsPortletCostants;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.fields.SpeciesFields;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientFieldType;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientFilterType;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.Callbacks;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientField;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientFilter;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Record;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.event.EditorGridListenerAdapter;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.VerticalLayout;

public class SpeciesFilter extends FormPanel {
	PhylogenyComboBox 	kingdomSelector=new PhylogenyComboBox(SpeciesFields.kingdom+"","Kingdom",RecordDefinitions.kingdomRecordDef);
	PhylogenyComboBox 	phylumSelector=new PhylogenyComboBox(SpeciesFields.phylum+"","Phylum",RecordDefinitions.phylumRecordDef);
	PhylogenyComboBox 	classSelector=new PhylogenyComboBox(SpeciesFields.classcolumn+"","Class",RecordDefinitions.classRecordDef);
	PhylogenyComboBox 	orderSelector=new PhylogenyComboBox(SpeciesFields.ordercolumn+"","Order",RecordDefinitions.orderRecordDef);
	PhylogenyComboBox 	familySelector=new PhylogenyComboBox(SpeciesFields.familycolumn+"","Family",RecordDefinitions.familyRecordDef);
	
	
	final MinMaxGrid minMax=new MinMaxGrid("Min/Max values");
	final TrueFalseGrid trueFalse=new TrueFalseGrid("Additional Criteria");
	
	SearchByKeywordsGrid nameFilters;
	SearchByKeywordsGrid codeFilters;
	final FieldSet comboBoxes=new FieldSet();
	
	final FieldSet names=new FieldSet();
	final FieldSet codes=new FieldSet();

	ToolbarButton apply = new ToolbarButton("Apply Filter");
	ToolbarButton clear = new ToolbarButton("Clear Filters");
	ToolbarButton toggleDetails= new ToolbarButton("Show Active Filters Summary");
	
	public FilterSummary filterDetails=new FilterSummary();
	
	public SpeciesFilter() {		
		this.setTitle("Search/Filter By");
		//this.setFrame(false);
		this.setLayout(new VerticalLayout(1));
		this.setAutoScroll(true);
		this.setBorder(true);
		
		kingdomSelector.linkToComboBox(phylumSelector);
		kingdomSelector.linkToComboBox(classSelector);
		kingdomSelector.linkToComboBox(orderSelector);
		kingdomSelector.linkToComboBox(familySelector);
		
		phylumSelector.linkToComboBox(classSelector);
		phylumSelector.linkToComboBox(orderSelector);
		phylumSelector.linkToComboBox(familySelector);
		
		classSelector.linkToComboBox(orderSelector);
		classSelector.linkToComboBox(familySelector);
		
		orderSelector.linkToComboBox(familySelector);
		
		
		
		
		comboBoxes.setTitle("Phylogeny");
		comboBoxes.add(kingdomSelector);
		comboBoxes.add(phylumSelector);		
		comboBoxes.add(classSelector);		
		comboBoxes.add(orderSelector);
		comboBoxes.add(familySelector);
		comboBoxes.setCollapsible(true);
		comboBoxes.collapse();
		comboBoxes.setAutoHeight(true);
		comboBoxes.add(new HTMLPanel("<p> Taxonomic classification follows  <a href=\"http://www.catalogueoflife.org/\" target=\"_blank\">Catalogue of Life</a> classification.</p>"));
		comboBoxes.setWidth(AquaMapsPortletCostants.Filter_Container_Width);
		
		
		String[] nameFilter=new String[]{
				//SpeciesFields.FBNAME,
				SpeciesFields.scientific_name+"",				
				//SpeciesFields.EnglishName,
				//SpeciesFields.FrenchName,
				//SpeciesFields.SpanishName,
				SpeciesFields.genus+"",
				SpeciesFields.species+"",
				SpeciesFields.fbname+"",
				
		};		
				
		
		nameFilters=new SearchByKeywordsGrid("Species Name",nameFilter,false);
		
		names.setTitle("Name Filter");		
		names.setCollapsible(true);
		names.collapse();
		names.setLayout(new FitLayout());
		names.setAutoHeight(true);
		names.setAutoWidth(false);
		names.setWidth(AquaMapsPortletCostants.Filter_Container_Width);
		names.add(nameFilters);		
		
		
		String[] codeFilter=new String[]{
				SpeciesFields.speciesid+""
		};	
		codeFilters=new SearchByKeywordsGrid("Code",codeFilter,true);		
		codes.setTitle("Code Filter");		
		codes.setCollapsible(true);
		codes.collapse();
		codes.setLayout(new FitLayout());
		codes.setAutoHeight(true);
		codes.setAutoWidth(false);
		codes.setWidth(AquaMapsPortletCostants.Filter_Container_Width);
		codes.add(codeFilters);		
		
		codeFilters.addEditorGridListener(new EditorGridListenerAdapter(){			
			public void onAfterEdit(GridPanel grid, Record record,
					String field, Object newValue, Object oldValue,
					int rowIndex, int colIndex) {
				if(record.getAsBoolean("enabled")){
					filterDetails.addFilter(FilterSummary.CodeType, record.getAsString("attribute"), record.getAsString("type"), record.getAsString("value"));					
				}
				else filterDetails.removeFilter(FilterSummary.CodeType, record.getAsString("attribute"));
			}
		});
		
		nameFilters.addEditorGridListener(new EditorGridListenerAdapter(){			
			public void onAfterEdit(GridPanel grid, Record record,
					String field, Object newValue, Object oldValue,
					int rowIndex, int colIndex) {
				if(record.getAsBoolean("enabled")){
					filterDetails.addFilter(FilterSummary.NameType, record.getAsString("attribute"), record.getAsString("type"), record.getAsString("value"));					
				}
				else filterDetails.removeFilter(FilterSummary.NameType, record.getAsString("attribute"));
			}
		});
		this.add(names);
		this.add(comboBoxes);
		this.add(trueFalse);
		this.add(minMax);
		this.add(codes);
		
		filterDetails.hide();
		
		this.add(filterDetails);		
		
		this.setBottomToolbar(new Button[] {apply,clear,toggleDetails}); 
		
		apply.addListener(new ButtonListenerAdapter(){			
			public void onClick(Button button, EventObject e) {
				applyFiltering();
			}
		});
		
		clear.addListener(new ButtonListenerAdapter(){			
			public void onClick(Button button, EventObject e) {
				nameFilters.reset();
				familySelector.clearValue();
				orderSelector.clearValue();
				classSelector.clearValue();
				phylumSelector.clearValue();
				kingdomSelector.clearValue();
				
				trueFalse.reset();
				minMax.reset();
				codeFilters.reset();
				applyFiltering();
			}
		});	
		
		
		toggleDetails.setEnableToggle(true);
		toggleDetails.addListener(new ButtonListenerAdapter(){			
			public void onToggle(Button button, boolean pressed) {
				if(pressed){
					button.setText(button.getText().replaceFirst("Show", "Hide"));
					filterDetails.show();
				}
				else {
					button.setText(button.getText().replaceFirst("Hide", "Show"));
					filterDetails.hide();
				}
			}
		});
		
		
	}
	public ClientField returnSelectedPhylogeny(){
		ClientField toReturn;
		
		//if(comboBoxes.isCollapsed()) return toReturn=null;
		if(familySelector.getValue()!=null){
			toReturn=new ClientField();
			toReturn.setName(SpeciesFields.familycolumn+"");
			toReturn.setValue(familySelector.getValue());
			toReturn.setType(ClientFieldType.STRING);
		}else if(orderSelector.getValue()!=null){
			toReturn=new ClientField();
			toReturn.setName(SpeciesFields.ordercolumn+"");
			toReturn.setValue(orderSelector.getValue());
			toReturn.setType(ClientFieldType.STRING);			
		}else  if(classSelector.getValue()!=null){
			toReturn=new ClientField();
			toReturn.setName(SpeciesFields.classcolumn+"");
			toReturn.setValue(classSelector.getValue());
			toReturn.setType(ClientFieldType.STRING);			
		}else if(phylumSelector.getValue()!=null){
			toReturn=new ClientField();
			toReturn.setName(SpeciesFields.phylum+"");
			toReturn.setValue(phylumSelector.getValue());
			toReturn.setType(ClientFieldType.STRING);
		}else if(kingdomSelector.getValue()!=null){
			toReturn=new ClientField();
			toReturn.setName(SpeciesFields.kingdom+"");
			toReturn.setValue(kingdomSelector.getValue());
			toReturn.setType(ClientFieldType.STRING);			
		}else toReturn=null;
		
		
		
		return toReturn;
	}
	
	
	public void applyFiltering(){
		
		AquaMapsPortlet.get().showLoading("Sending Filter Information", AquaMapsPortlet.get().species.toAddSpecies.getId());
		//Characteristics
		List<ClientFilter> filter=new ArrayList<ClientFilter>();
		
		ClientField taxField=returnSelectedPhylogeny();
		if(taxField!=null)	{
			ClientFilter taxoFilter=new ClientFilter();
			taxoFilter.setType(ClientFilterType.is);
			taxoFilter.setField(taxField);
			filter.add(taxoFilter);	
		}
		try{
		filter.addAll(minMax.getFilter());
		filter.addAll(trueFalse.getFilter());
		}catch(Exception e){
			Log.error("Exception while parsing characteristic Filter",e);
		}
		//AquaMapsPortlet.commonGUIService.setCharacteristicFilter(params, Callbacks.setSpeciesFilterCallback);
		
		// Names
		filter.addAll(nameFilters.getFilter());
				
		//Codes
		
		filter.addAll(codeFilters.getFilter());
		
		
		AquaMapsPortlet.localService.setSpeciesFilter(filter,filterDetails.getJSON(),Callbacks.setSpeciesFilterCallback);
		
	}
	
	
}
