package org.gcube.application.aquamaps.aquamapsportlet.client.filters;

import org.gcube.application.aquamaps.aquamapsportlet.client.AquaMapsPortlet;
import org.gcube.application.aquamaps.aquamapsportlet.client.RecordDefinitions;
import org.gcube.application.aquamaps.aquamapsportlet.client.Stores;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.AquaMapsPortletCostants;
import org.gcube.application.aquamaps.aquamapsportlet.client.constants.Tags;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.Msg;
import org.gcube.portlets.widgets.gcubelivegrid.client.data.BufferedStore;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.Template;
import com.gwtext.client.data.HttpProxy;
import com.gwtext.client.data.JsonReader;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.Store;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.event.ComboBoxListenerAdapter;

public class JobComboBox extends ComboBox{

	
	
	
	public JobComboBox(){
//		String url=AquaMapsPortletCostants.servletUrl.get("phylogeny")+"?phylogenyLevel="+level;
//		JsonReader internalReader = new JsonReader(recordDefinition);		 
//		 internalReader.setRoot("data");  
//		 internalReader.setTotalProperty("totalcount");  
//		 store=new Store(internalReader);
//		 store.setUrl(url);
//		
//		
//		store.addStoreListener(new StoreListenerAdapter(){			
//			public void onLoad(Store store, Record[] records) {
//				super.onLoad(store, records);
//				store.filterBy(new StoreTraversalCallback(){
//					public boolean execute(Record record) {
//						for(String filterLevel : filters.keySet()){
//							if(!record.getAsString(filterLevel).equals(filters.get(filterLevel))) return false;
//						}
//						return true;
//					}					
//				});
//			}
//		});
		
		final Template template = new Template("<div class=\"x-combo-list-item\">" +  
	            "<img src=\""+GWT.getModuleBaseURL() + "/img/cog_{status}.png\"> " +  
	            "{title}-{date}:{author}<div class=\"x-clear\"></div></div>");  
		
		this.setWidth(80);
		this.setDisplayField("title");
		this.setListWidth(AquaMapsPortletCostants.COMBOBOX_WIDTH);
		this.setMode(ComboBox.REMOTE);
		this.setTriggerAction(ComboBox.ALL);
		this.setMinChars(1);
		//this.setForceSelection(true);
		this.setTypeAhead(true);		
		this.setSelectOnFocus(false);
		this.setEmptyText("Filter by job ..");
		this.setLoadingText("Loading...");
		//BufferedStore store =Stores.jobStore();
		this.setTpl(template);
		this.setStore(Stores.jobStore());	
		this.setAllowBlank(true);
		this.setBlankText("all Jobs");
		this.setGrow(true);
		this.setGrowMax(20);
		this.setPageSize(10);
		
//		this.addListener(new ComboBoxListenerAdapter(){
//			@Override
//			public void onSelect(ComboBox comboBox, Record record, int index) {
//				Log.debug("entro in on select");
//				String jobId=record.getAsString("searchId");
//				AquaMapsPortlet.get().showLoading("Setting job filter..", AquaMapsPortlet.get().discoveringPanel.getId());
//				AquaMapsPortlet.commonGUIService.filterSubmitted(Tags.submittedJobId,jobId, jobFilterUpdate);
//				
//			}
////			@Override
////			public void onChange(Field field, Object newVal, Object oldVal) {
////				Log.debug("Entro in on Change old: "+oldVal+" new: "+newVal);
////				if(((String)newVal).equalsIgnoreCase("")){
////					AquaMapsPortlet.get().showLoading("Clearing job filter..", AquaMapsPortlet.get().discoveringPanel.getId());
////					AquaMapsPortlet.commonGUIService.filterSubmitted("", jobFilterUpdate);
////				}
////			}
//		});
	}
	
}
