package org.gcube.application.aquamaps.aquamapsportlet.client.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.AquaMapsPortletCostants;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.Tags;

import com.allen_sauer.gwt.log.client.Log;
import com.gwtext.client.core.RegExp;
import com.gwtext.client.data.JsonReader;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StoreTraversalCallback;
import com.gwtext.client.data.event.StoreListenerAdapter;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.event.ComboBoxListenerAdapter;

public class PhylogenyComboBox extends ComboBox {
	
	private List<PhylogenyComboBox> toForce=new ArrayList<PhylogenyComboBox>();
	private Map<String,String> filters=new HashMap<String,String>();	
	
	Store store;
	
	
	
	public PhylogenyComboBox(final String level,final String label,RecordDef recordDefinition){
		super (label);
		String url=AquaMapsPortletCostants.servletUrl.get("phylogeny")+"?phylogenyLevel="+level;
		JsonReader internalReader = new JsonReader(recordDefinition);		 
		 internalReader.setRoot(Tags.DATA);  
		 internalReader.setTotalProperty(Tags.TOTAL_COUNT);  
		 store=new Store(internalReader);
		 store.setUrl(url);
		
		
		store.addStoreListener(new StoreListenerAdapter(){			
			public void onLoad(Store store, Record[] records) {
				super.onLoad(store, records);
				store.filterBy(new StoreTraversalCallback(){
					public boolean execute(Record record) {
						for(String filterLevel : filters.keySet()){
							if(!record.getAsString(filterLevel).equals(filters.get(filterLevel))) return false;
						}
						return true;
					}					
				});
			}
		});
		this.setWidth(AquaMapsPortletCostants.COMBOBOX_WIDTH);
		this.setDisplayField(level);
		this.setListWidth(AquaMapsPortletCostants.COMBOBOX_WIDTH);
		this.setMode(ComboBox.LOCAL);
		this.setTriggerAction(ComboBox.QUERY);
		this.setMinChars(1);
		//this.setForceSelection(true);
		this.setTypeAhead(true);		
		this.setSelectOnFocus(false);
		this.setEmptyText("Select "+label);
		this.setLoadingText("Loading...");
		this.setStore(store);
		//TODO aggiungi il listener per la lista di combobox!!!!
		
		this.addListener(new ComboBoxListenerAdapter(){
				
				public void onChange(com.gwtext.client.widgets.form.Field field, Object newVal, Object oldVal) {				
					super.onChange(field, newVal, oldVal);
					for(String level: filters.keySet()){
						store.filter(level, new RegExp((filters.get(level)+"(.)*")));
					}
					store.filter(level,new RegExp(newVal.toString()));	
					Log.debug(level+" filtered for chainging");
				}
			public void onSelect(ComboBox comboBox, Record record, int index) {				
				super.onSelect(comboBox, record, index);				
				for(PhylogenyComboBox linked: toForce){
					linked.setValue("");
					//linked.getStore().load();
					//linked.getStore().filter(level,comboBox.getValue());
					linked.setFilter(level,comboBox.getValue());
					Log.debug(level+ " action on linked");
				}
			}
	/*		
			public void onExpand(ComboBox comboBox) {
				super.onExpand(comboBox);
				store.queryBy(new StoreQueryFunction(){

					public boolean test(Record record, String id) {						
						for(String filterLevel : filters.keySet()){
							if(!record.getAsString(filterLevel).equals(filters.get(filterLevel))) return false;
						}
						return true;
					}
					
				});
			}
			*/
			
			public void onFocus(com.gwtext.client.widgets.form.Field field) {			
				super.onFocus(field);
				store.load();
				Log.debug(level+ " load called");
			}
			
		});
	
		
	}
	
	public void setFilter(String level, String value){
		filters.put(level, value);
	}
	
	
	public void linkToComboBox(PhylogenyComboBox toLink){
		toForce.add(toLink);
	}
	
	public void loadRemoteData(){
		store.load();
	}
	
}