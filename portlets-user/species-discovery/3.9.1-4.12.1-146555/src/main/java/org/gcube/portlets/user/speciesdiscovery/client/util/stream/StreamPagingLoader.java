/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.util.stream;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.util.ExtendedTimer;
import org.gcube.portlets.user.speciesdiscovery.shared.filter.ResultFilter;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Record.RecordUpdate;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it" - "Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it"
 *
 */
public class StreamPagingLoader {

	protected static final int STREAM_STATUS_POLL_DELAY = 2000;
	protected static final int STREAM_FILL_POLL_DELAY = 1000;

	protected ListStore<ModelData> store;
	protected EditListener editListener;
	protected DataSource dataSource;
	protected List<StreamPagingLoaderListener> listeners;
	protected int pageSize;

	protected int currentPage;
	protected int currentStartItem;
	protected int currentEndItem;
	protected int streamSize;
	protected boolean streamComplete = false;
	
	protected boolean isFilteredStream = false;
	private int limitOnDataFilter = 0;

	protected Timer streamSizePoller;
	protected ExtendedTimer storeFiller;
	
	protected boolean pageComplete = false;
	
	private ResultFilter activeFilterObject = null;
	
	private int itemsLoaded = 0;
	private boolean reloadWithoutFilter;
	private int start = 0;
	private int oldStartItem = 0;
	private int oldLimit = 0;
	private int limit = 0;
	private boolean isNewPage = true;
	
	private boolean isOccurrences = false;

	/**
	 * @param pageSize
	 */
	public StreamPagingLoader(int pageSize) {
		this.pageSize = pageSize;
		listeners = new ArrayList<StreamPagingLoaderListener>(1);
		initialize();	
	}


	/**
	 * @param editListener the editListener to set
	 */
	public void setEditListener(EditListener editListener) {
		this.editListener = editListener;
	}

	public void addListener(StreamPagingLoaderListener listener)
	{
		listeners.add(listener);
	}

	protected void initialize()
	{
		store = new ListStore<ModelData>();
		store.addStoreListener(new StoreListener<ModelData>(){

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void storeUpdate(StoreEvent<ModelData> se) {
				Log.trace("storeUpdate: "+se.getOperation());
				if (editListener!=null && se.getOperation()==RecordUpdate.EDIT) {
					final Record record = se.getRecord();
					editListener.onEdit(record);
				}
			}
		});

		streamSizePoller = new Timer() {

			@Override
			public void run() {
				pollStreamState();		
			}
		};

		storeFiller = new ExtendedTimer() {

			@Override
			public void run() {
				fillPage();				
			}
		};

	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @return the currentPage
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	public void reset()
	{
		currentPage = 0;
		currentStartItem = 0;
		
		start = 0;
		oldStartItem = 0;
		oldLimit = 0;
		limit = 0;
		isNewPage = false;
		isOccurrences = false;
		
		currentEndItem = 0;
		streamComplete = false;
		streamSizePoller.cancel();
		storeFiller.cancel();
		pageComplete = false;
		itemsLoaded = 0;

//		streamSizePoller.cancel();
	}
	
	/**
	 * 
	 * @param isOccurrences
	 */
	public void startLoading(boolean isOccurrences)
	{
		
		isFilteredStream = false;
		limitOnDataFilter = 0;
		
		this.isOccurrences = isOccurrences;
		
		Log.trace("start loading set is occurrences " + isOccurrences);
		
		fireStreamStartLoading();
		
		//we anticipate the first polling
		pollStreamState();
//		streamSizePoller.scheduleRepeating(STREAM_STATUS_POLL_DELAY);
		streamSizePollerStart();
		nextPage();
	}
	
	
	public void streamSizePollerStart(){
		streamSizePoller.scheduleRepeating(STREAM_STATUS_POLL_DELAY);
	}

	public ListStore<ModelData> getStore()
	{
		return store;
	}

	public void pollStreamState()
	{

		dataSource.getStreamState(new AsyncCallback<StreamState>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.error("Failed getting stream state", caught.getMessage());
				//TODO retry policy
				reset();
				resetFilters();
			}

			@Override
			public void onSuccess(StreamState result) {
//				Log.error("+++++++++++++++++++++++++stream state completed " + result.isComplete());
				itemsLoaded = result.getSize();
				setStreamState(result);
				if (streamComplete) 
					streamComplete();
			}
		});
	}
	
	protected void setPageComplete(boolean bool){
		pageComplete = bool;
	}

	
	protected boolean getPageCompleted(){
		return this.pageComplete;
	}
	
	protected void setStreamState(StreamState state)
	{
		Log.trace("setStreamState state: "+state);
		
		//if isBufferFull = true, the MAXIMUM ELEMENTS are reached
//		if(state.isBufferFull())
//			Log.error("+++++++++++++++++++++++++MAX_BUFFERING_ELEMENTS is reached");	
//		Log.error("+++++++setStreamState ++++++++++++state.getSize(): "+ state.getSize());

		streamSize = state.getSize();
		streamComplete = state.isComplete();
		fireStreamUpdate();
	}
	
	protected void streamComplete()
	{
		Timer t = new Timer() {

			@Override
			public void run() {
				streamSizePoller.cancel();
				fireStreamLoadingComplete();		
			}
		};
		
		t.schedule(500);
	}
	
	public void setPage(int page)
	{
		int maxPage = (streamSize/pageSize)+1;
		currentPage = Math.min(page, maxPage); 
		currentPage = Math.max(currentPage, 1);
		setIsNewPage(true);
		startFilling();
	}

	public void nextPage()
	{
		currentPage++;
		setPageComplete(false);
		setIsNewPage(true);
		startFilling();
	}

	public void prevPage()
	{
		currentPage--;
		setPageComplete(false);
		setIsNewPage(true);
		startFilling();
	}
	
	public void reloadPageForFiltering(int limitFilter, boolean isFirstStart){
		
//		int start;
//		int limit;
		
		isFilteredStream = true;

		if(limitFilter!=-1)
			this.limitOnDataFilter = limitFilter;
		
		
		if(isFirstStart){
			reset();
			currentPage = 1;
			setIsNewPage(true);
		}
		
		currentStartItem = (currentPage-1)*pageSize;
		if(currentStartItem<0) currentStartItem = 0;
		
		
		if(limit>0){
			oldStartItem = start;
			oldLimit = limit;
		}
		
		start = currentStartItem;
		limit = Math.min(limitOnDataFilter-currentStartItem, pageSize);
		
//		limit = Math.min(pageSize, limitOnDataFilter-store.getCount());
		
		Log.trace(" OLD START: "+oldStartItem + 
					"  OLD LIMIT:  "+oldLimit + 
					"  START: " +start + 
					"  LIMIT: " + limit + 
					" LIMIT ON DATA: " + limitOnDataFilter);
		
		//EXIT IF START ITEM NOT CHANGE
		if(oldStartItem==start && !isNewPage() && limit>=oldLimit && !isOccurrences) {
			Log.trace("NOT FILL PAGE - CONDITION oldStartItem==start is true, check page complete");
			if (pageComplete) pageComplete();
		}
		
		else{
			
			if(limit>0)
				setIsNewPage(false);

			Log.trace("reloadPageForFiltering startFilling " +
					" currentPage: " +currentPage+
					" currentStartItem: "+currentStartItem +
					" pageSize: " + pageSize + 
	//				" activeFilterObject: " + activeFilterObject + 
					" limitOnDataFilter: " + limitOnDataFilter);
			
				
			store.removeAll();
			
			Log.trace("Firing BeforeDataChanged");
			store.fireEvent(Store.BeforeDataChanged, new StoreEvent<ModelData>(store));
			Log.trace("Fired BeforeDataChanged");
			
			Log.trace("reloadPageForFiltering fillPage start: "+start+" limit: "+limit);
	
			if (limit >= 0) {
				final long startTime = System.currentTimeMillis();
				
				dataSource.getData(start, limit, activeFilterObject, new AsyncCallback<List<ModelData>>() {
		
					@Override
					public void onFailure(Throwable caught) {
						Log.error("Failed loading results", caught.getMessage());
//						Log.error("Failed loading results");
//						System.out.println("Failed loading results" + caught.getMessage());
						reset();
						resetFilters();
					}
		
					@Override
					public void onSuccess(List<ModelData> result) {
						Log.trace("dataLoaded "+result.size()+" in "+(System.currentTimeMillis()-startTime));
	
						if(!exceedPage(result.size())){
							loadData(result);
							pollStreamState();
						}
						else{
							setPageComplete(true);
							pageComplete();
						}
	//					checkPageComplete();
					}
				});
			} else {
				Log.trace("There is no data to download, skipping");
			}	
		
		}
		
		
	}
	
	public void resetFilters(){
		setActiveFilterObject(null);
		isFilteredStream = false;
	}
	
	
	public void reloadPageWithoutFiltering(){
		
		reset();
		resetFilters();
		
		reloadWithoutFilter = true;

		currentStartItem = (currentPage-1)*pageSize;
		if(currentStartItem<0) currentStartItem = 0;

		Log.trace("reloadPageWithoutFiltering" +
				" currentPage: " +currentPage+
				" currentStartItem: "+currentStartItem +
				" pageSize: " + pageSize + 
				" streamSize: " + streamSize +
				" store.getCount(): " + store.getCount());
		
//		store.removeAll();
		Log.trace("Firing BeforeDataChanged");
		store.fireEvent(Store.BeforeDataChanged, new StoreEvent<ModelData>(store));
		Log.trace("Fired BeforeDataChanged");
		
		pollingState();

		setPage(0);

	}
	
	public void pollingState(){
		
		if(!streamComplete)
			streamSizePoller.scheduleRepeating(STREAM_STATUS_POLL_DELAY);
		else
			pollStreamState();
		
	}

	protected void startFilling()
	{
		
		if(!isFilteredStream){
			currentStartItem = (currentPage-1)*pageSize;
			Log.trace("startFilling currentPage: "+currentPage+ " currentStartItem: "+currentStartItem);
			
			store.removeAll();
			Log.trace("Firing BeforeDataChanged");
			store.fireEvent(Store.BeforeDataChanged, new StoreEvent<ModelData>(store));
			Log.trace("Fired BeforeDataChanged");
			
			//we anticipate the filling
			fillPage();
			storeFiller.scheduleRepeating(STREAM_FILL_POLL_DELAY);
		}
		else
			reloadPageForFiltering(-1, false);
	}
	

	protected void fillPage()
	{

	/*	oldLimit = limit;
		
		limit = Math.min(pageSize-store.getCount(), streamSize-start);
		
		if(limit>0)
			oldStartItem = start;
		
		start = currentStartItem + store.getCount();
	*/
		
		if(limit>0){
			oldStartItem = start;
			oldLimit = limit;
		}
			
		start = currentStartItem + store.getCount();
		limit = Math.min(pageSize-store.getCount(), streamSize-start);	
		
		Log.trace(" OLD START: "+oldStartItem + 
					"  OLD LIMIT:  "+oldLimit + 
							"  START: " +start + 
							"  LIMIT: " + limit +
							" IS NEW PAGE: " +isNewPage());
		
		
//		//EXIT IF START ITEM NOT CHANGE
//		if(oldStartItem==start && start!=(currentPage-1)*pageSize && limit>=oldLimit && limit<0) {
//			Log.trace("NOT FILL PAGE - CONDITION oldStartItem==start is true, check page complete");
//			if (pageComplete) pageComplete();
//		}
		
		//EXIT IF START ITEM NOT CHANGE
		if(oldStartItem==start && !isNewPage() && limit>=oldLimit && !isOccurrences) {
			Log.trace("NOT FILL PAGE - CONDITION oldStartItem==start is true, check page complete");
			if (pageComplete) pageComplete();
//			checkPageComplete();
		}
		else{
			
			Log.trace("fillPage" +
					" currentPage: " +currentPage+
					" currentStartItem: "+currentStartItem +
					" pageSize: " + pageSize + 
					" streamSize: " + streamSize +
					" store.getCount(): " + store.getCount());
			
			Log.trace("fillPage start: "+start+" limit: "+limit);
			
			if(limit>0)
				setIsNewPage(false);
				
			if (limit >= 0) {

				final long startTime = System.currentTimeMillis();
				
				dataSource.getData(start, limit, activeFilterObject, new AsyncCallback<List<ModelData>>() {
		
					@Override
					public void onFailure(Throwable caught) {
						Log.error("Failed loading results", caught);
						//TODO retry policy
						reset();
						resetFilters();
					}
		
					@Override
					public void onSuccess(List<ModelData> result) {
						Log.trace("dataLoaded "+result.size()+" in "+(System.currentTimeMillis()-startTime));

						if(!exceedPage(result.size()))
							loadData(result);
						else{
							setPageComplete(true);
							pageComplete();
						}
	//					checkPageComplete();
					}
				});
			} else {
				
				Log.trace("Limit is < 0 - there is no data to download, skipping");
				checkPageComplete();
			}
		}
	}
	
	
	public boolean exceedPage(int storeSize){
		
		boolean cond;
		int currentEnd = store.getCount() + storeSize;
		
		if (streamComplete) {
			int expectedPageSize = Math.min(pageSize, streamSize-currentStartItem);
			int calc = ((currentPage-1)*pageSize)+(streamSize-currentStartItem);
			int expectedEndElements = Math.min(currentPage*pageSize, calc);

			Log.trace("Exceed Page - stream completed: true ");
			Log.trace("expectedPageSize: "+expectedPageSize);
			Log.trace("expectedEndElements: "+expectedEndElements);
			Log.trace("currentEnd: "+currentEnd);
			Log.trace(" store.getCount(): "+ store.getCount());
			Log.trace(" streamSize: "+ streamSize);
//			cond = (store.getCount() >= expectedPageSize) || (currentEndItem > expectedEndElements);
			cond = currentEnd > expectedEndElements;
		}
		else{
			cond = currentEnd > (currentPage*pageSize);
		}
		
		Log.trace("Exceed Page cond: "+cond +" pageSize: " + pageSize + " currentEnd: " + currentEnd);
		
		return cond;
		
	}

	protected void loadData(List<ModelData> data)
	{
		int prevStoreCount = store.getCount();

		for (ModelData row:data) store.add(row);

		if (prevStoreCount == 0 && data.size()>0) store.fireEvent(Store.DataChanged, new StoreEvent<ModelData>(store));
		
		checkPageComplete();
	}
	
	protected void checkPageComplete()
	{
		updateCurrentValues();
		
//		pageComplete = isPageComplete();
//		if (pageComplete) pageComplete();
		
//		setPageComplete(isPageComplete());
//		if (pageComplete) pageComplete();
		
		if(!pageComplete){
			setPageComplete(isPageComplete());
		}
	
		if (pageComplete) pageComplete();
			
		
	}
	
	protected void pageComplete()
	{
		Log.trace("page complete");
		store.fireEvent(Store.DataChanged, new StoreEvent<ModelData>(store));
		storeFiller.cancel();
	}

	protected void updateCurrentValues()
	{
		currentEndItem = currentStartItem + store.getCount();
		Log.trace("updateCurrentValues currentPage: "+currentPage+" currentStartItem: "+currentStartItem+" currentEndItem: "+currentEndItem);
		fireStreamUpdate();
	}
	
	protected boolean isPageComplete()
	{
		Log.trace("checking if the page is complete loaded (store count: "+store.getCount()+")");
		Log.trace("streamComplete: "+streamComplete);
		
		boolean cond;
		
		//no more data is expected from the stream
		if (streamComplete) {

			int expectedPageSize = Math.min(pageSize, streamSize-currentStartItem);

			return store.getCount() == expectedPageSize;
		}

		cond = store.getCount() == pageSize;
//		Log.error("++++++isPageComplete: "+cond +" ++++++++++++ pageSize " + pageSize + " store.getCount() "+store.getCount());
		
		return cond;
	}

	protected void fireStreamUpdate()
	{
		for (StreamPagingLoaderListener listener:listeners) listener.onStreamUpdate(streamSize, currentStartItem, currentEndItem);
	}

	protected void fireStreamLoadingComplete()
	{
		for (StreamPagingLoaderListener listener:listeners) listener.onStreamLoadingComplete();
	}
	
	protected void fireStreamStartLoading()
	{
		for (StreamPagingLoaderListener listener:listeners) listener.onStreamStartLoading();
	}

	public ResultFilter getActiveFilterObject() {
		return activeFilterObject;
	}

	public void setActiveFilterObject(ResultFilter activeFilterObject) {
		this.activeFilterObject = activeFilterObject;
	}

	public boolean isStreamComplete() {
		return streamComplete;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	public int getStart() {
		return start;
	}


	public void setStart(int start) {
		this.start = start;
	}


	public boolean isNewPage() {
		return isNewPage;
	}


	public void setIsNewPage(boolean isNewPage) {
		this.isNewPage = isNewPage;
	}
	
}
