package org.gcube.portlets.user.workspace.client.view.grids;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.portlets.user.workspace.client.AppController;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.event.DoubleClickElementSelectedEvent;
import org.gcube.portlets.user.workspace.client.event.FileDownloadEvent;
import org.gcube.portlets.user.workspace.client.event.FileDownloadEvent.DownloadType;
import org.gcube.portlets.user.workspace.client.event.GridElementSelectedEvent;
import org.gcube.portlets.user.workspace.client.event.GridElementUnSelectedEvent;
import org.gcube.portlets.user.workspace.client.event.ImagePreviewEvent;
import org.gcube.portlets.user.workspace.client.event.OpenContextMenuTreeEvent;
import org.gcube.portlets.user.workspace.client.event.OpenReportsEvent;
import org.gcube.portlets.user.workspace.client.event.ShowUrlEvent;
import org.gcube.portlets.user.workspace.client.event.StoreGridChangedEvent;
import org.gcube.portlets.user.workspace.client.model.FileGridModel;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.GroupingStoreModel;
import org.gcube.portlets.user.workspace.client.view.windows.InfoDisplay;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.grid.filters.DateFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.NumericFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Random;


/**
 * The Class GxtGridFilterGroupPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 17, 2015
 */
public class GxtGridFilterGroupPanel extends LayoutContainer {

	private ContentPanel cp = new ContentPanel();
//	private ListStore<FileModel> store =  ListStoreModel.getInstance().getStore();
	private GroupingStore<FileGridModel> store = GroupingStoreModel.getInstance().getStore();
	private FileGridModel currentItemSelected = null;
	private FileModel currentFolderView = null;
	private GroupingView view = new GroupingView();
	private boolean groupingEnabled = false;
    private NumberFormat number = ConstantsExplorer.numberFormatterKB;
    private final Grid<FileGridModel> grid;

	/**
	 * Instantiates a new gxt grid filter group panel.
	 *
	 * @param group the group
	 */
	public GxtGridFilterGroupPanel(boolean group) {
//		setLayout(new FitLayout());
		setId("GxtGridFilterGroupPanel "+Random.nextInt());
		ColumnConfig icon = new ColumnConfig(FileModel.ICON, "", 25);
		icon.setSortable(false);
		ColumnConfig name = new ColumnConfig(FileGridModel.NAME, FileGridModel.NAME, 280);
		ColumnConfig type = new ColumnConfig(FileGridModel.TYPE, FileGridModel.TYPE, 60);
		ColumnConfig lastUpdate = new ColumnConfig(FileGridModel.LASTMODIFIED, "Last Update", 90);
		lastUpdate.setDateTimeFormat(DateTimeFormat.getFormat("dd MMM hh:mm aaa yyyy"));
		ColumnConfig category = new ColumnConfig(FileModel.HUMAN_REDABLE_CATEGORY, FileModel.HUMAN_REDABLE_CATEGORY, 100);
		ColumnConfig size = new ColumnConfig(FileGridModel.SIZE, FileGridModel.SIZE, 45);
		ColumnConfig ownerFullName = new ColumnConfig(FileGridModel.OWNERFULLNAME, FileGridModel.OWNER, 90);


		size.setEditor(new CellEditor(new NumberField()));

		ColumnModel cm  = null;

		if(group)
			cm = new ColumnModel(Arrays.asList(icon, name, ownerFullName, type, lastUpdate, size, category));
		else
			cm = new ColumnModel(Arrays.asList(icon, name, ownerFullName, type, lastUpdate, size));

		final ColumnModel columnModel = cm;

		cp.setBodyBorder(true);
		cp.setHeaderVisible(false);
		cp.setLayout(new FitLayout());

		grid = new Grid<FileGridModel>(store, columnModel);
		grid.getView().setAutoFill(true);

	    view.setShowGroupedColumn(false);
	    view.setForceFit(true);
	 	grid.setView(view);
	    view.setEmptyText(FileGridModel.EMPTY);

	    if(group){
	    	store.groupBy(FileGridModel.HUMAN_REDABLE_CATEGORY);
	    	groupingEnabled = true;
	    }

	    GridCellRenderer<FileGridModel> folderRender = new GridCellRenderer<FileGridModel>() {
			@Override
			public String render(FileGridModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<FileGridModel> store, Grid<FileGridModel> grid) {
		          String val = model.get(property);
				          if(val==null || val.isEmpty())
		        	  return "";

		          return "<span qtitle='" + columnModel.getColumnById(property).getHeader() + "' qtip='" + val  + "' >" + val + "</span>";
			}
	      };

	    GridCellRenderer<FileGridModel> kbRender = new GridCellRenderer<FileGridModel>() {
	      @Override
	      public String render(FileGridModel model, String property, ColumnData config,
		      int rowIndex, int colIndex, ListStore<FileGridModel> store, Grid<FileGridModel> grid) {

	    	  long value = (Long) model.get(property);

	    	  if(value!=-1){
			      double kb = value/1024;
			      if(kb<1)
			    	  kb=1;
			      return "<span>" + number.format(kb) + "</span>";

	    	  }

	    	  return "";
	      	}
	      };

	    size.setRenderer(kbRender);
	    type.setRenderer(folderRender);

	    //setAlphanumericStoreSorter(grid);

		GridFilters filters = new GridFilters();
		filters.setLocal(true);

		StringFilter nameFilter = new StringFilter(FileGridModel.NAME);
		StringFilter authorFilter = new StringFilter(FileGridModel.TYPE);
		DateFilter dateFilter = new DateFilter(FileGridModel.LASTMODIFIED);
		NumericFilter sizeFilter = new NumericFilter(FileGridModel.SIZE);

		filters.addFilter(nameFilter);
		filters.addFilter(authorFilter);
		filters.addFilter(dateFilter);
		filters.addFilter(sizeFilter);

		// grid.setStyleAttribute("borderTop", "none");
		grid.setAutoExpandColumn(FileGridModel.NAME);
		grid.setBorders(false);
		grid.setStripeRows(true);
		grid.setColumnLines(true);

		grid.getView().setShowDirtyCells(false);
		grid.addPlugin(filters);
		grid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);

		grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<FileGridModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<FileGridModel> se) {
				System.out.println("selection grid change");

				ModelData target = se.getSelectedItem();

				if(target!=null){
					currentItemSelected = (FileGridModel) target;

					boolean isMultiselection = false;

					if(se.getSelection()!=null && se.getSelection().size()>1)
						isMultiselection = true;

					AppController.getEventBus().fireEvent(new GridElementSelectedEvent(target, isMultiselection));
				}
				else{
					currentItemSelected = null;
					AppController.getEventBus().fireEvent(new GridElementUnSelectedEvent());
				}

			}
		});


		grid.addListener(Events.RowDoubleClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				FileGridModel fileModel = grid.getSelectionModel().getSelectedItem();

				if(fileModel!=null)
					fireEventByFileModelType(fileModel);
	//				if(fileModel.isDirectory())
	//					AppController.getEventBus().fireEvent(new DoubleClickElementSelectedEvent(fileModel));
			}

	    });


		grid.setContextMenu(null); //add context menu null - ignore browse event right click

		grid.addListener(Events.OnContextMenu, new Listener<GridEvent<FileGridModel>>(){
			@Override
			public void handleEvent(GridEvent<FileGridModel> be)
			{

		   		if(be.isRightClick())
		   		{
		   			if(grid.getSelectionModel().getSelectedItem()!=null){

		   				if(grid.getSelectionModel().getSelection().size()>1){
							new InfoDisplay("Info", "The context menu is not available if multi-select is active");
							return;
		   				}

		   				FileGridModel fileGridModel = grid.getSelectionModel().getSelectedItem();
		   				AppController.getEventBus().fireEvent(new OpenContextMenuTreeEvent(fileGridModel, be.getClientX(), be.getClientY()));
		   			}
		   		}
			}
		});

		new GridDragSource(grid);

		cp.add(grid);
		add(cp);

		addDataChangedStoreListener();

	}

	/**
	 * Disable grouping.
	 */
	public void disableGrouping() {
		GroupingStore<FileGridModel> groupingStore = null;
		if (this.getStore() instanceof GroupingStore) {
			groupingStore = this.getStore();
			if (groupingStore != null) {
				groupingStore.clearGrouping();
			}
			this.groupingEnabled = false;
		}
	}

	/**
	 * Enable grouping.
	 */
	public void enableGrouping() {
		GroupingStore<FileGridModel> groupingStore = null;
		if (this.getStore() instanceof GroupingStore) {
			groupingStore = this.getStore();
			if (groupingStore != null) {
				groupingStore.groupBy(FileGridModel.HUMAN_REDABLE_CATEGORY);
			}
			this.groupingEnabled  = true;
		}
	}

	/**
	 * Fire event by file model type.
	 *
	 * @param target the target
	 */
	private void fireEventByFileModelType(FileModel target){

		if(target.isDirectory()){
			AppController.getEventBus().fireEvent(new DoubleClickElementSelectedEvent(target));
			return;
		}


		switch(target.getGXTFolderItemType()){

		case EXTERNAL_IMAGE:
		case IMAGE_DOCUMENT:
			AppController.getEventBus().fireEvent(new ImagePreviewEvent(target,0, 0));
			break;
		case EXTERNAL_FILE:
		case EXTERNAL_PDF_FILE:
		case PDF_DOCUMENT:
		case DOCUMENT:
		case URL_DOCUMENT:
		case EXTERNAL_RESOURCE_LINK:

			AppController.getEventBus().fireEvent(new FileDownloadEvent(target.getIdentifier(), target.getName(), DownloadType.SHOW, target.isDirectory() || target.isVreFolder(), null));
			break;
		case EXTERNAL_URL:
			AppController.getEventBus().fireEvent(new ShowUrlEvent(target));
			break;
		case REPORT_TEMPLATE:
		case REPORT:
			AppController.getEventBus().fireEvent(new OpenReportsEvent(target));
			break;
		case QUERY:
		case TIME_SERIES:
		case METADATA:
		case WORKFLOW_REPORT:
		case WORKFLOW_TEMPLATE:
			break;
		default:

		}

	}


	/**
	 * Reset store.
	 */
	private void resetStore(){
		store.removeAll();
	}


	/**
	 * Update store.
	 *
	 * @param result the result
	 * @return true, if successful
	 */
	public boolean updateStore(List<FileGridModel> result){

		resetStore();
		if(result!= null && result.size()>0){
			for(FileGridModel file: result){
				//GWT.log("File: "+file.getName() + " has sync: "+file.getSynchedThreddsStatus());
				file.setIcon();
			}
			store.add(result);
			return true;
		}
		return false;
	}


	/**
	 * Adds the to store.
	 *
	 * @param result the result
	 * @return true, if successful
	 */
	public boolean addToStore(FileGridModel result){

		if(result!= null){
			result.setIcon();
			store.add(result);
			return true;
		}
		return false;
	}

	/**
	 * Gets the selected item.
	 *
	 * @return the selected item
	 */
	public FileGridModel getSelectedItem(){

		return currentItemSelected;
	}

	/**
	 * Gets the selected items.
	 *
	 * @return the selected items
	 */
	public List<FileGridModel> getSelectedItems(){

		return grid.getSelectionModel().getSelection();
	}

	/**
	 * Gets the ids selected items.
	 *
	 * @return the ids selected items
	 */
	public List<String> getIdsSelectedItems(){

		if(grid.getSelectionModel().getSelection()!=null){

			List<String> ids = new ArrayList<String>();
			for (FileModel file : grid.getSelectionModel().getSelection()) {
				ids.add(file.getIdentifier());
			}

			return ids;
		}

		return null;
	}

	/**
	 * Delete item.
	 *
	 * @param identifier (MANDATORY)
	 * @return true, if successful
	 */
	public boolean deleteItem(String identifier) {

		FileGridModel fileTarget =  getFileGridModelByIdentifier(identifier);

		if(fileTarget!=null){
			Record record = store.getRecord(fileTarget);
			store.remove((FileGridModel) record.getModel());
			return true;
		}
		else
			System.out.println("Delete Error: file target with " + identifier + " identifier not exist in store" );

		return false;
	}




	/**
	 * Gets the current folder view.
	 *
	 * @return the current folder view
	 */
	public FileModel getCurrentFolderView() {
		return currentFolderView;
	}


	/**
	 * Sets the current folder view.
	 *
	 * @param currentFolderView the new current folder view
	 */
	public void setCurrentFolderView(FileModel currentFolderView) {
		this.currentFolderView = currentFolderView;
	}


	/**
	 * Rename item.
	 *
	 * @param itemIdentifier the item identifier
	 * @param newName the new name
	 * @param extension the extension
	 * @return true, if successful
	 */
	public boolean renameItem(String itemIdentifier, String newName, String extension) {

		if(itemIdentifier!=null){
			FileGridModel fileTarget =  getFileGridModelByIdentifier(itemIdentifier);
			if(fileTarget!=null){
				Record record = store.getRecord(fileTarget);
				if(record!=null){
					if(extension!= null)
						record.set(FileGridModel.NAME, newName+extension);
					else
						record.set(FileGridModel.NAME, newName);

					return true;
				}
			}
			else
				System.out.println("Record Error: file target not exist in store" );
		}
		else
			System.out.println("Rename Error: file target is null" );

		return false;

	}

	/**
	 * Gets the file grid model by identifier.
	 *
	 * @param id the id
	 * @return the file grid model by identifier
	 */
	public FileGridModel getFileGridModelByIdentifier(String id){
		return store.findModel(FileGridModel.IDENTIFIER, id);
	}

	/**
	 * Select item by file model id.
	 *
	 * @param id the id
	 * @return true, if successful
	 */
	public boolean selectItemByFileModelId(String id){

		if(id!=null && !id.isEmpty()){

			FileGridModel fileModel = getFileGridModelByIdentifier(id);

			if(fileModel!=null){
				grid.getSelectionModel().select(fileModel, true);
				return true;
			}
		}

		return false;
	}


	/**
	 * Gets the store.
	 *
	 * @return the store
	 */
	public GroupingStore<FileGridModel> getStore(){
		return store;
	}

	/**
	 * Sets the border as on search.
	 *
	 * @param bool the new border as on search
	 */
	public void setBorderAsOnSearch(boolean bool){

		if(this.cp.getElement("body")!=null){

			if(bool){
				this.cp.getElement("body").getStyle().setBorderColor("#32CD32");
			}
			else
				this.cp.getElement("body").getStyle().setBorderColor("#99BBE8");

		}
	}

	/**
	 * Adds the data changed store listener.
	 */
	private void addDataChangedStoreListener(){

		store.addListener(Store.Add,  new Listener<StoreEvent<ModelData>>(){

			@Override
			public void handleEvent(StoreEvent<ModelData> be) {
				AppController.getEventBus().fireEvent(new StoreGridChangedEvent(storeSize()));

			}
		});

		store.addListener(Store.Remove,  new Listener<StoreEvent<ModelData>>(){

			@Override
			public void handleEvent(StoreEvent<ModelData> be) {
				AppController.getEventBus().fireEvent(new StoreGridChangedEvent(storeSize()));

			}
		});

		store.addListener(Store.Clear,  new Listener<StoreEvent<ModelData>>(){

			@Override
			public void handleEvent(StoreEvent<ModelData> be) {
				AppController.getEventBus().fireEvent(new StoreGridChangedEvent(storeSize()));

			}
		});

	}

	/**
	 * Store size.
	 *
	 * @return -1 if store is null. The size otherwise
	 */
	private int storeSize(){

		if(store!=null && store.getModels()!=null){
			return store.getModels().size();
		}

		return -1;
	}

	/**
	 * Refresh size.
	 *
	 * @param width the width
	 * @param height the height
	 */
	public void refreshSize(String width, String height) {

//		if(this.isRendered()){
//			GWT.log("refresh size grid "+height);
//			this.setHeight(height);
//		}
		GWT.log("refresh size grid "+width +"; heigth: "+height);
		this.setSize(width, height);
		cp.setSize(width, height);
//		grid.setSize(width, height);
	}

}