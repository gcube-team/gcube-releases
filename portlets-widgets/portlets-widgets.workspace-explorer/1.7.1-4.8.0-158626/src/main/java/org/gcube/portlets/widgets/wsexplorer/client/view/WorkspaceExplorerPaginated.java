/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.widgets.wsexplorer.client.Util;
import org.gcube.portlets.widgets.wsexplorer.client.WorkspaceExplorerConstants;
import org.gcube.portlets.widgets.wsexplorer.client.view.grid.ItemsTable.DISPLAY_FIELD;
import org.gcube.portlets.widgets.wsexplorer.client.view.grid.SortedCellTable;
import org.gcube.portlets.widgets.wsexplorer.shared.FilterCriteria;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;
import org.gcube.portlets.widgets.wsexplorer.shared.SearchedFolder;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;


/**
 * The Class WorkspaceExplorerPaginated.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 5, 2017
 */
public class WorkspaceExplorerPaginated extends WorkspaceExplorer{

	private VerticalPanel vPanel = new VerticalPanel();

	private FlowPanel pagerPanel = new FlowPanel();

	private Boolean newLoading = false;

	private MyCustomDataProvider<Item> dataProvider = new MyCustomDataProvider<Item>();

	protected boolean loadGcubeProperties = false;

	protected Widget orginalLoadingIndicator = null;

	private int serverStartIndex = 0;

	private HashMap<String, SearchedFolder> cachedPage = new HashMap<String, SearchedFolder>();

	/**
	 * Instantiates a new workspace explorer paginated.
	 *
	 * @param eventBus the event bus
	 * @param filterCriteria the filter criteria
	 * @param showableTypes the showable types
	 * @param selectableTypes the selectable types
	 * @param displayProperties the display properties
	 * @param showGcubeInfo the show gcube info
	 * @param sortByColumn the sort by column
	 * @param fields the fields
	 */
	public WorkspaceExplorerPaginated(
		HandlerManager eventBus, FilterCriteria filterCriteria,
		ItemType[] showableTypes, ItemType[] selectableTypes,
		List<String> displayProperties, boolean showGcubeInfo,
		DISPLAY_FIELD sortByColumn, DISPLAY_FIELD[] fields) {
		super(eventBus, filterCriteria, showableTypes, selectableTypes, displayProperties, showGcubeInfo, sortByColumn);
		initTable(dataProvider);
		newLoading = true;
		orginalLoadingIndicator = getCellTable().getLoadingIndicator();
	}


	/**
	 * Gets the asycn data provider.
	 *
	 * @return the asycn data provider
	 */
	public AsyncDataProvider<Item> getAsycnDataProvider(){
		return (AsyncDataProvider<Item>) getCellTable().getDataProvider();
	}

	/**
	 * Gets the cell tale.
	 *
	 * @return the cell tale
	 */
	public SortedCellTable<Item> getCellTable(){
		return  getItTables().getCellTable();
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.view.WorkspaceExplorer#loadFolder(org.gcube.portlets.widgets.wsexplorer.shared.Item, boolean, int, int, boolean)
	 */
	public void loadFolder(final Item item, final boolean loadGcubeProperties, final int startIdx, final int limit, final boolean resetStore) throws Exception {
		newLoading = resetStore;
		GWT.log("loading folder data for Item: "+item.getId()+" [startIdx: "+startIdx +", limit: "+limit+", resetStore:"+resetStore+"]");
		//super.loadFolder(item, loadGcubeProperties);
		this.loadGcubeProperties = loadGcubeProperties;
		setLoading();
		if(!item.isFolder())
			throw new Exception("Item is not a folder");

		if(item.getId()==null || item.getId().isEmpty())
			throw new Exception("Item id is null or empty");

		// we make a copy of showable types
		final List<ItemType> showableTypesParam = new ArrayList<ItemType>(showableTypes);

		// we get sure that folders are displayed
		for (ItemType folder : Util.FOLDERS) {
			if (!showableTypesParam.contains(folder))
				showableTypesParam.add(folder);
		}

		final boolean purgeEmpyFolders = !showEmptyFolders;

//		//IF IS INIT OR A DOUBLE CLICK
//		if(getDisplayingFolderItem()==null || getDisplayingFolderItem().getId()!=item.getId()){
//			GWT.log("Performing Get Folder Children Count ");
//			WorkspaceExplorerConstants.workspaceNavigatorService.getFolderChildrenCount(item, new AsyncCallback<Integer>() {
//
//				@Override
//				public void onFailure(Throwable caught) {
//				}
//
//				@Override
//				public void onSuccess(final Integer result) {
//					GWT.log("Folder Children count: "+result);
//
//					int newStartIndex = startIdx;
//					getAsycnDataProvider().updateRowCount(result, true);
//
//					if(newLoading){
//						GWT.log("Cleaning all data...");
//						newStartIndex = 0;
//						GWT.log("Store reset performed start index is: "+newStartIndex);
//					}
//
//					//final Range range = display.getVisibleRange();
//					perfomGetFolderChildren(item, loadGcubeProperties, newStartIndex, limit, purgeEmpyFolders, showableTypesParam);
//				}
//			});
//		}else
//			perfomGetFolderChildren(item, loadGcubeProperties, startIdx, limit, purgeEmpyFolders, showableTypesParam);

		int newStartIndex = startIdx;

		if(newLoading){
			GWT.log("Cleaning all data...");
			newStartIndex = 0;
			serverStartIndex = 0;
			GWT.log("Store reset performed start index is: "+newStartIndex);
			getAsycnDataProvider().updateRowCount(WorkspaceExplorerConstants.ITEMS_PER_PAGE, false);
		}

		perfomGetFolderChildren(item, loadGcubeProperties, newStartIndex, limit, serverStartIndex, purgeEmpyFolders, showableTypesParam);

	}


	/**
	 * Perfom get folder children.
	 *
	 * @param item the item
	 * @param loadGcubeProperties the load gcube properties
	 * @param startIdx the start idx
	 * @param limit the limit
	 * @param serSI the server start index to use
	 * @param purgeEmpyFolders the purge empy folders
	 * @param showableTypesParam the showable types param
	 */
	private void perfomGetFolderChildren(final Item item, boolean loadGcubeProperties, final int startIdx, final int limit, final int serSI, boolean purgeEmpyFolders, List<ItemType> showableTypesParam){
		GWT.log("loading workspace folder by item id from server: "+item.getId());

		SearchedFolder page = getCachePage(item, startIdx, limit);
		if(page!=null){
			setNewPageResult(page);
			serverStartIndex = page.getServerEndIndex();
			GWT.log("Using cached page, serverStartIndex: "+serverStartIndex);
			return;
		}

		WorkspaceExplorerConstants.workspaceNavigatorService.getFolder(item, showableTypesParam, purgeEmpyFolders, filterCriteria, loadGcubeProperties, startIdx, limit, serSI, new AsyncCallback<SearchedFolder>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				setAlert(caught.getMessage(), AlertType.ERROR);
				GWT.log("Error loading workspace folder from server",caught);
				newLoading = false;
			}

			@Override
			public void onSuccess(SearchedFolder result) {
				setNewPageResult(result);
				setCachePage(item, result);
			}
		});
	}


	/**
	 * Sets the new page result.
	 *
	 * @param result the new new page result
	 */
	private void setNewPageResult(SearchedFolder result){

		serverStartIndex = result.getServerEndIndex();

		if(result.getFolder().getName()==null || result.getFolder().getName().isEmpty())
			result.getFolder().setName(result.getFolder().getName());

		if(newLoading){
			getCellTable().setVisibleRangeAndClearData(new Range(result.getClientStartIndex(), WorkspaceExplorerConstants.ITEMS_PER_PAGE), false);
		}

		SelectionModel<? super Item> sm = getCellTable().getSelectionModel();

		if(sm instanceof SingleSelectionModel){
			SingleSelectionModel<Item> ssm = (SingleSelectionModel<Item>) sm;
			ssm.clear();
		}

		getAsycnDataProvider().updateRowData(result.getClientStartIndex(), result.getFolder().getChildren());

		if(result.getFolder().getChildren().size()==0){
			getCellTable().setLoadingIndicator(new Label("No data"));
		}else{
			getCellTable().setLoadingIndicator(orginalLoadingIndicator);
		}

		//getCellTable().setVisibleRangeAndClearData(new Range(startIdx, result.getChildren()).), false);
		//getAsycnDataProvider().getDataDisplays().
		GWT.log("Updating row data startIndex: "+result.getClientStartIndex() + " children size: "+result.getFolder().getChildren().size());
		GWT.log("getAsycnDataProvider().getDataDisplays().size(): "+getCellTable().getRowCount());

		if(result.isServerSearchFinished()){
			GWT.log("Search finished!!!");
			getAsycnDataProvider().updateRowCount(getCellTable().getRowCount(), true);
		}
		//getCellTable().setPageSize(result.getChildren().size()+1);
		//getCellTable().setVisibleRange(startIdx, result.getChildren().size());
		//getCellTable().redraw();
		//GWT.log("cellTable size: "+getCellTable().getRowCount());
		setDisplayingFolderItem(result.getFolder());
		newLoading = false;

	}


	/**
	 * Sets the cache page.
	 *
	 * @param item the item
	 * @param result the result
	 */
	private void setCachePage(Item item, SearchedFolder result){

		String key = getCacheKey(item, result.getClientStartIndex(), result.getLimit());

		if(key!=null){
			GWT.log("Caching result with key: "+key);
			cachedPage.put(key, result);
		}
	}


	/**
	 * Gets the cache page.
	 *
	 * @param item the item
	 * @param startIdx the start idx
	 * @param limit the limit
	 * @return the cache page
	 */
	private SearchedFolder getCachePage(Item item, int startIdx, int limit){

		String key = getCacheKey(item, startIdx, limit);

		return cachedPage.get(key);
	}


	/**
	 * Gets the cache key.
	 *
	 * @param item the item
	 * @param startIdx the start idx
	 * @param limit the limit
	 * @return the cache key
	 */
	private String getCacheKey(Item item, int startIdx, int limit){

		if(item==null || startIdx<0 || limit< 0)
			return null;

		return item.getId() + "["+startIdx + "-" +limit+"]";
	}

	/**
	 * Inits the pagination.
	 *
	 * @param itemsPerPage the items per page
	 */
	public void initPagination(int itemsPerPage){

		//dataProvider.updateRowCount(100, true);
		// Add the cellList to the dataProvider.
		//asyncDataProvider.addDataDisplay(cellTable);
		// Create paging controls.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.setDisplay(getCellTable());
		pager.setPageSize(itemsPerPage);
		pager.getElement().getStyle().setProperty("margin", "auto");
		vPanel.add(getCellTable());
		vPanel.getElement().addClassName("vPanel");
		//vPanel.add(pager);

		pagerPanel.add(pager);
	}

	/**
	 * Gets the pager panel.
	 *
	 * @return the pager panel
	 */
	public VerticalPanel getCellPanel(){
		return vPanel;
	}


	/**
	 * Gets the pager panel.
	 *
	 * @return the pager panel
	 */
	public FlowPanel getPagerPanel(){
		return pagerPanel;
	}

	/**
	 * A custom {@link AsyncDataProvider}.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Jul 5, 2017
	 * @param <T> the generic type
	 */
	public class MyCustomDataProvider<T> extends AsyncDataProvider<T> {

		/**
		 * {@link #onRangeChanged(HasData)} is called when the table requests a
		 * new range of data. You can push data back to the displays using
		 * {@link #updateRowData(int, List)}.
		 *
		 * @param display the display
		 */
		@Override
		public void onRangeChanged(HasData<T> display) {

			// Get the new range.
			final Range range = display.getVisibleRange();

			int start = range.getStart();
			int length = range.getLength();

			if(newLoading){
				GWT.log("OnLoading is true.. returning");
				return;
			}
			try {
				GWT.log("Range changed: "+start +" "+length + " visible count: "+display.getVisibleItemCount());
				GWT.log("Server start index: "+serverStartIndex);
//				int newStart = start < serverStartIndex? serverStartIndex : start;
//				GWT.log("newStart index: "+newStart);

				if(getDisplayingFolderItem()!=null)
					loadFolder(getDisplayingFolderItem(), loadGcubeProperties, start, length, false);
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Purge cache.
	 */
	public void purgeCache() {
		cachedPage.clear();
	}

}
