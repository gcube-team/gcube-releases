package org.gcube.application.aquamaps.aquamapsportlet.client.filters;

import org.gcube.application.aquamaps.aquamapsportlet.client.RecordDefinitions;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.AquaMapsPortletCostants;

import com.allen_sauer.gwt.log.client.Log;
import com.gwtext.client.core.SortDir;
import com.gwtext.client.data.GroupingStore;
import com.gwtext.client.data.JsonReader;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.SortState;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GroupingView;



public class FilterSummary extends GridPanel {

	public static String CodeType="Code";
	public static String NameType="Name";
	public static String additional="Additional Criteria";
	public static String Phylogeny="Phylogeny";
	public static String MinMax="MinMax";
	
	
	//ArrayReader reader = new ArrayReader();
	JsonReader reader;

	GroupingStore store;
	GroupingView gridView = new GroupingView();
	
	
	
	public FilterSummary() {		    
		this.setWidth(AquaMapsPortletCostants.Filter_Container_Width);
		this.setHeight(300);
		//this.setAutoHeight(true);
		this.setAutoScroll(true);  
		this.setTitle("Active Filters Summary");		 
		this.setFrame(true);
		reader = new JsonReader(RecordDefinitions.perturbationRecordDef);
		reader.setRoot("data");
		reader.setTotalProperty("totalcount");
		store  = new GroupingStore(reader);
		store.setGroupField("type");		
		this.setStore(store);
		this.setColumnModel(new ColumnModel(new ColumnConfig[]{  
				new ColumnConfig("Type", "type", 150, true),  
				new ColumnConfig("Attribute", "attribute", 90, true),
				new ColumnConfig("Operator", "operator", 60, true),
				new ColumnConfig("Value", "value", 150, true),
		}));		  
		this.setStripeRows(true); 
		
		store.setSortInfo(new SortState(RecordDefinitions.perturbationRecordDef.getFields()[0].getName(), SortDir.ASC));
		
		gridView.setEmptyText("No active Filters");
		gridView.setEmptyGroupText("No active filters of this kind");
		gridView.setEnableGrouping(true);
		gridView.setEnableGroupingMenu(false);
		gridView.setHideGroupedColumn(true);
		gridView.setStartCollapsed(true);		
		//gridView.setForceFit(true);  
		gridView.setGroupTextTpl("{text} ({[values.rs.length]} {[values.rs.length > 1 ? \"Items\" : \"Item\"]})");
		this.setView(gridView);  
	}

	public void addFilter(String type,String attribute,String operator,String value){
		Record record=store.getById(type+attribute);
		if(record==null) {
			record=RecordDefinitions.filterRecordDef.createRecord(type+attribute, new Object[]{		
					type,
					attribute,				
					operator,
					value
			}); 
			store.add(record);
			Log.debug("record added");
		}else {
			record.set("operator",operator);
			record.set("value", value);
			Log.debug("record updated");
			store.commitChanges();
		}
		gridView.refresh();
	}

	public void removeFilter(String type,String attribute){
		Record record = store.getById(type+attribute);
		if(record!=null) {
			store.remove(record);
			Log.debug("record removed");
			gridView.refresh();
		}
	}
	
	public void loadJSON(String data){
		store.setGroupField("type");
		store.loadJsonData(data	, false);
	}
	
	public String getJSON(){
		StringBuilder summary=new StringBuilder("{\"data\":[");		
		
		for(Record r : store.getRecords()){
			summary.append("{");
			String[] fieldNames =r.getFields();
			for(int i = 0; i<fieldNames.length;i++){
				summary.append("\""+fieldNames[i]+"\":\""+r.getAsString(fieldNames[i])+"");
				if(i<fieldNames.length-1)summary.append(",");
			}			
			summary.append("}");
			if(store.indexOf(r)<store.getRecords().length-1)summary.append(",");
		}
		summary.append("],\"totalcount\":"+store.getCount()+"}");
		return summary.toString();
	}
}
