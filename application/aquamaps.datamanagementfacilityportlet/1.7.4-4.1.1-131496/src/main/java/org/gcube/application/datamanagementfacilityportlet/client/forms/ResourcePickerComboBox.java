package org.gcube.application.datamanagementfacilityportlet.client.forms;


import org.gcube.application.datamanagementfacilityportlet.client.DataManagementFacilityConstants;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.Tags;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.ClientResource;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientResourceType;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.HttpProxy;
import com.extjs.gxt.ui.client.data.JsonPagingLoadResultReader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.http.client.RequestBuilder;


public class ResourcePickerComboBox extends ComboBox<ModelData> {

	
	
	private ResourcePickerComboBox instance=this;
	
	private static final String COMPOSITE_TITLE="COMPOSITE_TITLE";
	private static final String templateString="<tpl for=\".\"><div class=\"search-item\" id=\"{"+ClientResource.SEARCH_ID+"}\">"+ 
    	"<h3>{"+ClientResource.TITLE+"} (ID : {"+ClientResource.SEARCH_ID+"}) </h3>" +
		" by {"+ClientResource.AUTHOR+"}</br>" +
		" Description : {"+ClientResource.DESCRIPTION+"}</h3>"+ 
		"</div></tpl>";
	
	
	public ResourcePickerComboBox(final ClientResourceType resourceType) {
		
		//****************** READER AND STORE Settings
		String url=DataManagementFacilityConstants.servletUrl.get(Tags.resourceServlet);
		
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		requestBuilder.setHeader(Tags.RESOURCE_TYPE, resourceType.toString());
		HttpProxy proxy = new HttpProxy(requestBuilder);
//		Log.debug("Using script with url "+requestBuilder.getUrl());
//		
//		ScriptTagProxy proxy = new ScriptTagProxy(requestBuilder.getUrl());

		

		ModelType type = new ModelType();  
		type.setRoot(Tags.DATA);  
		type.setTotalName(Tags.TOTAL_COUNT);  
		type.addField(ClientResource.SEARCH_ID);  
		type.addField(ClientResource.TITLE);
		type.addField(ClientResource.DESCRIPTION);
		type.addField(ClientResource.AUTHOR);
		type.addField(ClientResource.GENERATION_TIME);
		type.addField(ClientResource.STATUS);
		type.addField(ClientResource.TYPE);
		type.addField(ClientResource.SOURCE_HCAF);
		JsonPagingLoadResultReader<PagingLoadResult<ModelData>> reader = new JsonPagingLoadResultReader<PagingLoadResult<ModelData>>(type);  

		final PagingLoader<PagingLoadResult<ModelData>> loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy,reader);  

		loader.addListener(Loader.BeforeLoad, new Listener<LoadEvent>() {  
			public void handleEvent(LoadEvent be) {  
				be.<ModelData> getConfig().set("start", be.<ModelData> getConfig().get("offset"));  
			}  
		});  
		loader.setSortDir(SortDir.DESC);  
		loader.setSortField(ClientResource.TITLE);  
		loader.setRemoteSort(true);  
				
		
		ListStore<ModelData> store = new ListStore<ModelData>(loader);  
		setView(new ListView<ModelData>(store,XTemplate.create(templateString)){
			@Override
			protected ModelData prepareData(ModelData model) {
				model.set(COMPOSITE_TITLE, ((String)model.get(ClientResource.TITLE))+" (ID : "+((String)model.get(ClientResource.SEARCH_ID))+")");
				return model;
			}
		});
		

		this.setWidth(580);  
		this.setDisplayField(COMPOSITE_TITLE);  
		this.setItemSelector("div.search-item");  
		this.setTemplate(templateString);  
		this.setStore(store);  
		this.setPageSize(10);
				
		this.setLoadingText("fetching information...");
		this.setFieldLabel(type+"");
		this.setData("text", "Select "+DataManagementFacilityConstants.resourceNames.get(type));
		this.addPlugin(Common.plugin);
		
	}

	
}
