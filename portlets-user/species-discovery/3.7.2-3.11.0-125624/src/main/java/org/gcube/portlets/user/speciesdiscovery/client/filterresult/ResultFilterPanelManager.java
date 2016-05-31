package org.gcube.portlets.user.speciesdiscovery.client.filterresult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.SpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.event.ActiveButtonCheckAllRowEvent;
import org.gcube.portlets.user.speciesdiscovery.client.model.ClassificationModel;
import org.gcube.portlets.user.speciesdiscovery.client.util.SpeciesGridFields;
import org.gcube.portlets.user.speciesdiscovery.client.util.stream.StreamPagingLoader;
import org.gcube.portlets.user.speciesdiscovery.client.util.stream.StreamPagingLoaderListener;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceModel;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ResultFilterPanelManager{
	private static ResultFilterPanelManager instance;
	private ClassificationFilter classificationFilter;
	private DataProviderFilter dataProviderFilter;
	private TypeFilter typeFilter;
	private DataSourceFilter dataSourceFilter;
	private RankFilter rankFilter;
	private List<ContentPanel> listResultFilterPanel = new ArrayList<ContentPanel>();
	private StreamPagingLoader loader;
	private int pageSize;
	private EventBus eventBus;
	private int oldStreamSize= -1;
	private boolean isDataLoaded = false;
	
	private String currentGroupByRank = "";
	
	private ResultFilterPanelManager(){
		
		classificationFilter = new ClassificationFilter();
		dataProviderFilter = new DataProviderFilter();
		typeFilter = new TypeFilter();
		dataSourceFilter = new DataSourceFilter();
		rankFilter = new RankFilter();
		
		listResultFilterPanel.add(classificationFilter.getPanel());
		listResultFilterPanel.add(dataProviderFilter.getPanel());
		listResultFilterPanel.add(dataSourceFilter.getPanel());
		listResultFilterPanel.add(rankFilter.getPanel());
//		listResultFilterPanel.add(typeFilter.getPanel());
		
	}

	public List<ContentPanel> getListResultFilterPanel() {
		return listResultFilterPanel;
	}
	
	
	public static synchronized ResultFilterPanelManager getInstance() {
		if (instance == null)
			instance = new ResultFilterPanelManager();
		return instance;
	}

	public void loadDataSource(List<DataSourceModel> result) {
		
//		dataSourceFilter.loadDataSource(result);
		
	}
	
	public void setEventBus(EventBus eventBus){
		this.eventBus = eventBus;
		classificationFilter.setEventBus(eventBus);
		dataProviderFilter.setEventBus(eventBus);
		dataSourceFilter.setEventBus(eventBus);
		rankFilter.setEventBus(eventBus);
	}
	
	public void setIsDataLoaded(boolean bool){
		isDataLoaded = bool;
	}
	
	
	public void bind(StreamPagingLoader loader)
	{
		this.loader = loader;
		this.pageSize = loader.getPageSize();
		loader.addListener(new StreamPagingLoaderListener() {
			
			@Override
			public void onStreamUpdate(int streamSize, int currentStartItem, int currentEndItem) {
				Log.trace("in on stream UPDATE - Stream size: " +streamSize);
				
				if(oldStreamSize != streamSize)
					updateDataSourceFilter();
				
				oldStreamSize = streamSize;
				
				setIsDataLoaded(true);
			}
			
			@Override
			public void onStreamLoadingComplete() {
				Log.trace("####### onStreamLoadingComplete COMPLETED");
				updateDataSourceFilter();
				eventBus.fireEvent(new ActiveButtonCheckAllRowEvent(true));
				
				
			}

			@Override
			public void onStreamStartLoading() {
				resetFilters();
				eventBus.fireEvent(new ActiveButtonCheckAllRowEvent(false));
			
			}
		});
		
		loader.getStore().addStoreListener(new StoreListener<ModelData>(){

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void storeBeforeDataChanged(StoreEvent<ModelData> se) {
			}
		
		});
	}
	
	public void updateDataSourceFilterById(SpeciesGridFields filterId){
		
		if(filterId==null)
			return;
		
		switch (filterId) {
		
			case DATASOURCE:
				
				SpeciesDiscovery.taxonomySearchService.getFilterCounterById(SpeciesGridFields.DATASOURCE, new AsyncCallback<HashMap<String,Integer>>() {
					
					@Override
					public void onSuccess(HashMap<String, Integer> result) {
						if(result!=null)
							dataSourceFilter.loadDataSource(result);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						resetFilters();
//						errorAlert("Error in SpeciesDiscovery.taxonomySearchService.getFilterCounterById " + caught.getMessage());
						Log.error("Error in SpeciesDiscovery.taxonomySearchService.getFilterCounterById " + caught);
						
					}
				});
				
				break;
				
			case DATAPROVIDER:
							
				SpeciesDiscovery.taxonomySearchService.getFilterCounterById(SpeciesGridFields.DATAPROVIDER, new AsyncCallback<HashMap<String,Integer>>() {
					
					@Override
					public void onSuccess(HashMap<String, Integer> result) {
						if(result!=null)
							dataProviderFilter.loadDataSource(result);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						resetFilters();
//						errorAlert("Error in SpeciesDiscovery.taxonomySearchService.getFilterCounterById " + caught.getMessage());
						Log.error("Error in SpeciesDiscovery.taxonomySearchService.getFilterCounterById " + caught);
						
					}
				});
				
				break;
							
							
			case MATCHING_RANK:
				
				SpeciesDiscovery.taxonomySearchService.getFilterCounterById(SpeciesGridFields.MATCHING_RANK, new AsyncCallback<HashMap<String,Integer>>() {
					
					@Override
					public void onSuccess(HashMap<String, Integer> result) {
						if(result!=null)
							rankFilter.loadDataSource(result);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						resetFilters();
//						errorAlert("Error in SpeciesDiscovery.taxonomySearchService.getFilterCounterById" + caught.getMessage());
						Log.error("Error in SpeciesDiscovery.taxonomySearchService.getFilterCounterById " + caught);
						
					}
				});
				
				break;
		}
	}
	
	public void updateDataSourceFilter(){
		
		SpeciesDiscovery.taxonomySearchService.getFilterCounterById(SpeciesGridFields.DATASOURCE, new AsyncCallback<HashMap<String,Integer>>() {
			
			@Override
			public void onSuccess(HashMap<String, Integer> result) {
				if(result!=null)
					dataSourceFilter.loadDataSource(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				resetFilters();
//				errorAlert("Error in SpeciesDiscovery.taxonomySearchService.getFilterCounterById " + caught.getMessage());
				Log.error("Error in SpeciesDiscovery.taxonomySearchService.getFilterCounterById " + caught);
				
			}
		});
		
		SpeciesDiscovery.taxonomySearchService.getFilterCounterById(SpeciesGridFields.DATAPROVIDER, new AsyncCallback<HashMap<String,Integer>>() {
			
			@Override
			public void onSuccess(HashMap<String, Integer> result) {
				if(result!=null)
					dataProviderFilter.loadDataSource(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				resetFilters();
//				errorAlert("Error in SpeciesDiscovery.taxonomySearchService.getFilterCounterById " + caught.getMessage());
				Log.error("Error in SpeciesDiscovery.taxonomySearchService.getFilterCounterById " + caught);
				
			}
		});
		
		SpeciesDiscovery.taxonomySearchService.getFilterCounterById(SpeciesGridFields.MATCHING_RANK, new AsyncCallback<HashMap<String,Integer>>() {
			
			@Override
			public void onSuccess(HashMap<String, Integer> result) {
				if(result!=null)
					rankFilter.loadDataSource(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				resetFilters();
//				errorAlert("Error in SpeciesDiscovery.taxonomySearchService.getFilterCounterById" + caught.getMessage());
				Log.error("Error in SpeciesDiscovery.taxonomySearchService.getFilterCounterById " + caught);
				
			}
		});
		
		SpeciesDiscovery.taxonomySearchService.getFilterCounterForClassification(getUpdatedGroupByRank() , callbackFilterCounter);
	}
	

	public void resetFilters() {
		dataSourceFilter.reset();
		dataProviderFilter.reset();
		classificationFilter.reset();
		rankFilter.reset();
		oldStreamSize = -1;
		setIsDataLoaded(false);
		
	}
	
	public void updateFilterCounterForClassification(){
		
		if(isDataLoaded)
			SpeciesDiscovery.taxonomySearchService.getFilterCounterForClassification(getUpdatedGroupByRank(), callbackFilterCounter);

	}

	public String getUpdatedGroupByRank() {

		if(currentGroupByRank.compareTo(classificationFilter.getGroupRank())!=0)
			classificationFilter.reset();
		
		setGroupByRank(classificationFilter.getGroupRank());
		
		return currentGroupByRank;
	}

	public void setGroupByRank(String groupByRank) {
		currentGroupByRank = groupByRank;
		
	}

	public String getGroupByRank() {
		return classificationFilter.getGroupRank();
	}
	
	
	private AsyncCallback<HashMap<String,ClassificationModel>> callbackFilterCounter = new AsyncCallback<HashMap<String,ClassificationModel>>() {

			@Override
			public void onFailure(Throwable caught) {
				resetFilters();
//				errorAlert("Error in SpeciesDiscovery.taxonomySearchService.getFilterCounterForClassification" + caught.getMessage());
				Log.error("Error in SpeciesDiscovery.taxonomySearchService.getFilterCounterForClassification " + caught);
				
			}

			@Override
			public void onSuccess(HashMap<String, ClassificationModel> result) {
				if(result!=null)
					classificationFilter.loadDataSourceClassification(result, currentGroupByRank);
			}
	};
	

	
}
