package org.gcube.portlets.widgets.wsexplorer.client.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.portlets.widgets.wsexplorer.client.Util;
import org.gcube.portlets.widgets.wsexplorer.client.WorkspaceExplorerConstants;
import org.gcube.portlets.widgets.wsexplorer.client.event.RootLoadedEvent;
import org.gcube.portlets.widgets.wsexplorer.client.resources.WorkspaceExplorerResources;
import org.gcube.portlets.widgets.wsexplorer.client.view.grid.ItemsTable;
import org.gcube.portlets.widgets.wsexplorer.client.view.grid.ItemsTable.DISPLAY_FIELD;
import org.gcube.portlets.widgets.wsexplorer.shared.FilterCriteria;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.AbstractDataProvider;


/**
 * The Class WorkspaceExplorer.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Aug 4, 2015
 */
public class WorkspaceExplorer implements ShowableTypes, SelectableTypes, FolderLoader{


	protected static final HorizontalPanel LOADING_PANEL = new HorizontalPanel();
	protected static final Image LOADING_IMAGE = WorkspaceExplorerResources.getIconLoading().createImage();

	static {
		LOADING_PANEL.getElement().getStyle().setMargin(5.0, Unit.PX);
//		LOADING_PANEL.setSpacing(3);
		LOADING_PANEL.add(LOADING_IMAGE);
		HTML loading = new HTML("Loading...");
		loading.getElement().getStyle().setPaddingLeft(5.0, Unit.PX);
		LOADING_PANEL.add(loading);
	}

	protected Alert alert = new Alert();
	protected List<ItemType> selectableTypes = new ArrayList<ItemType>();
	protected List<ItemType> showableTypes = new ArrayList<ItemType>();
	protected FilterCriteria filterCriteria = null;
	protected boolean showEmptyFolders = true;
	protected ScrollPanel explorerPanel;
	private ItemsTable<Item> itTables;
	private DISPLAY_FIELD[] displayFields;
	private DISPLAY_FIELD sortByColumn;
	private HandlerManager eventBus;

	private Item displayingFolderItem;
	private boolean showGcubeItemsInfo;
	private List<String> displayProperties;

	private AbstractDataProvider<Item> dataProvider;

	/**
	 * Instantiates a new workspace explorer - This is the base constructor.
	 *
	 * @param eventBus the event bus
	 * @param showableTypes the showable types
	 * @param selectableTypes the selectable types
	 * @param displayProperties the display properties
	 * @param showGcubeInfo the show gcube info
	 * @param sortByColumn the sort by column
	 * @param fields the columns to display
	 */
	private WorkspaceExplorer(HandlerManager eventBus, ItemType[] showableTypes, ItemType[] selectableTypes, List<String> displayProperties, boolean showGcubeInfo, DISPLAY_FIELD sortByColumn, DISPLAY_FIELD[] fields){
		this.eventBus = eventBus;
		this.showGcubeItemsInfo = showGcubeInfo;
		this.displayFields = fields;
		this.sortByColumn = sortByColumn;
		this.displayProperties = displayProperties;
		this.explorerPanel = new ScrollPanel();
		setShowableTypes(showableTypes);
		setSelectableTypes(selectableTypes);
	}


	/**
	 * Sets the data provider table.
	 *
	 * @param dataProvider the new data provider table
	 */
	public void initTable(AbstractDataProvider<Item> dataProvider){
		this.dataProvider = dataProvider;
		this.itTables = new ItemsTable<Item>(eventBus, true, displayFields, displayProperties, showGcubeItemsInfo, sortByColumn);
		this.itTables.initTable(null, null, dataProvider);
	}


	/**
	 * Instantiates a new workspace explorer.
	 *
	 * @param eventBus the event bus
	 * @param showableTypes the showable types
	 * @param selectableTypes the selectable types
	 * @param fields the fields
	 */
	public WorkspaceExplorer(HandlerManager eventBus, ItemType[] showableTypes, ItemType[] selectableTypes, DISPLAY_FIELD...fields) {
		this(eventBus, showableTypes, selectableTypes, null, false, null, fields);
	}


	/**
	 * Instantiates a new workspace explorer.
	 *
	 * @param eventBus the event bus
	 * @param showableTypes the showable types
	 * @param selectableTypes the selectable types
	 * @param displayProperties the display properties
	 * @param fields the fields
	 */
	public WorkspaceExplorer(HandlerManager eventBus, ItemType[] showableTypes, ItemType[] selectableTypes, List<String> displayProperties, DISPLAY_FIELD...fields) {
		this(eventBus, showableTypes, selectableTypes, displayProperties, false, null, fields);
	}



	/**
	 * Instantiates a new workspace explorer.
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
	public WorkspaceExplorer(HandlerManager eventBus, FilterCriteria filterCriteria, ItemType[] showableTypes, ItemType[] selectableTypes, List<String> displayProperties, boolean showGcubeInfo, DISPLAY_FIELD sortByColumn, DISPLAY_FIELD...fields) {
		this(eventBus, showableTypes, selectableTypes, displayProperties, showGcubeInfo, sortByColumn, fields);
		this.filterCriteria = filterCriteria;

	}

	/**
	 * Sets the alert.
	 *
	 * @param html the html
	 * @param type the type
	 */
	public void setAlert(String html, AlertType type){
		explorerPanel.clear();
		alert.setHTML(html);
		alert.setType(type);
		alert.setClose(false);
		explorerPanel.add(alert);
	}

	/**
	 * Set the panel in loading mode.
	 */
	protected void setLoading() {
		explorerPanel.clear();
		explorerPanel.add(LOADING_PANEL);
	}

	/**
	 * Load the Workspace Tree.
	 */
	public void loadRoot() {
		GWT.log("loading tree data");
		setLoading();

		// we make a copy of showable types
		List<ItemType> showableTypesParam = new ArrayList<ItemType>(showableTypes);

		// we get sure that folders are displayed
		for (ItemType folder : Util.FOLDERS) {
			if (!showableTypesParam.contains(folder))
				showableTypesParam.add(folder);
		}

		boolean purgeEmpyFolders = !showEmptyFolders;

		GWT.log("loading workspace tree from server");

		WorkspaceExplorerConstants.workspaceNavigatorService.getRoot(showableTypesParam, purgeEmpyFolders, filterCriteria, new AsyncCallback<Item>() {

			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				setAlert(caught.getMessage(), AlertType.ERROR);
				GWT.log("Error loading workspace tree from server",caught);
			}

			public void onSuccess(Item item) {
				eventBus.fireEvent(new RootLoadedEvent(item));
				updateExplorer(item.getChildren());

				setDisplayingFolderItem(item);
			}

		});
	}

	/**
	 * Sets the displaying folder item.
	 *
	 * @param item the new displaying folder item
	 */
	protected void setDisplayingFolderItem(Item item) {
		this.displayingFolderItem = item;
	}

	/**
	 * Load the Workspace Tree.
	 */
	public void loadMySpecialFolder() {
		GWT.log("loading folder data");
		setLoading();


		// we make a copy of showable types
		List<ItemType> showableTypesParam = new ArrayList<ItemType>(showableTypes);

		// we get sure that folders are displayed
		for (ItemType folder : Util.FOLDERS) {
			if (!showableTypesParam.contains(folder))
				showableTypesParam.add(folder);
		}

		boolean purgeEmpyFolders = !showEmptyFolders;
//		FilterCriteria filterCriteria = new FilterCriteria(allowedMimeTypes,requiredProperties, allowedFileExtensions);

		GWT.log("loading workspace tree from server");
		WorkspaceExplorerConstants.workspaceNavigatorService.getMySpecialFolder(showableTypesParam, purgeEmpyFolders, filterCriteria, new AsyncCallback<Item>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
					setAlert(caught.getMessage(), AlertType.ERROR);
					GWT.log("Error loading workspace folder from server",caught);
				}

				@Override
				public void onSuccess(Item item) {

					updateExplorer(item.getChildren());
					setDisplayingFolderItem(item);
				}
		});
	}


	/**
	 * Update explorer.
	 *
	 * @param items the items
	 */
	protected void updateExplorer(ArrayList<Item> items){
		GWT.log("workspace explorer updating..");
		explorerPanel.clear();
//		itTables = new ItemsTable(true, displayFields);
		itTables.updateItems(items, true);
		explorerPanel.add(itTables.getCellTable());
		GWT.log("workspace explorer updated");
	}


	/**
	 * Adds the item to explorer.
	 *
	 * @param item the item
	 */
	public void addItemToExplorer(Item item){
		GWT.log("workspace explorer add item.."+item);
//		itTables = new ItemsTable(true, displayFields);
//		explorerPanel.clear();
		itTables.addItems(Arrays.asList(item));
//		explorerPanel.add(itTables.getCellTable());
		GWT.log("added item: "+item.getName() +", to Explorer");
	}

	/**
	 * Gets the panel.
	 *
	 * @return the explorerPanel
	 */
	public ScrollPanel getPanel() {
		return explorerPanel;
	}


	/**
	 * Gets the display fields.
	 *
	 * @return the displayFields
	 */
	public DISPLAY_FIELD[] getDisplayFields() {
		return displayFields;
	}


	/**
	 * Sets the display fields.
	 *
	 * @param displayFields the displayFields to set
	 */
	public void setDisplayFields(DISPLAY_FIELD[] displayFields) {
		this.displayFields = displayFields;
		itTables.setDisplayFields(displayFields);
	}


	/**
	 * Gets the it tables.
	 *
	 * @return the itTables
	 */
	public ItemsTable<Item> getItTables() {
		return itTables;
	}


	/**
	 * Gets the item selected.
	 *
	 * @return the item selected
	 */
	public Item getItemSelected(){
		return itTables.getSelectedItem();
	}

	/**
	 * Gets the selectable types.
	 *
	 * @return the selectableTypes
	 */
	@Override
	public List<ItemType> getSelectableTypes() {
		return selectableTypes;
	}

	/**
	 * Sets the selectable types.
	 *
	 * @param selectableTypes the selectableTypes to set
	 */
	@Override
	public void setSelectableTypes(ItemType ... selectableTypes) {
		this.selectableTypes.clear();
		if (selectableTypes!=null) for (ItemType type:selectableTypes) this.selectableTypes.add(type);
	}

	/**
	 * Gets the showable types.
	 *
	 * @return the showableTypes
	 */
	@Override
	public List<ItemType> getShowableTypes() {
		return showableTypes;
	}

	/**
	 * Sets the showable types.
	 *
	 * @param showableTypes the showableTypes to set
	 */
	@Override
	public void setShowableTypes(ItemType ... showableTypes) {
		this.showableTypes.clear();
		if (showableTypes!=null) for (ItemType type:showableTypes) this.showableTypes.add(type);
	}

	/**
	 * Sets the new filter criteria.
	 *
	 * @param filterCriteria the new new filter criteria
	 */
	public void setNewFilterCriteria(FilterCriteria filterCriteria) {
		this.filterCriteria = filterCriteria;
	}


	/**
	 * Gets the displaying folder item.
	 *
	 * @return the displayingFolderItem
	 */
	public Item getDisplayingFolderItem() {

		return displayingFolderItem;
	}


	/**
	 * Gets the data provider.
	 *
	 * @return the dataProvider
	 */
	public AbstractDataProvider<Item> getDataProvider() {

		return dataProvider;
	}


	/**
	 * Sets the new show properties.
	 *
	 * @param displayProperties the new new show properties
	 */
	public void setNewShowProperties(List<String> displayProperties) {
		this.itTables.setDisplayProperties(displayProperties);
		itTables.reInitColumnsTable();
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.view.FolderLoader#loadFolder(org.gcube.portlets.widgets.wsexplorer.shared.Item, boolean, int, int, boolean)
	 * Pagination is not implemented here
	 */
	@Override
	public void loadFolder(
		final Item item, boolean loadGcubeProperties, int startIdx, int limit,
		boolean resetStore)
		throws Exception {

		GWT.log("loading folder data");
		setLoading();

		if(!item.isFolder())
			throw new Exception("Item is not a folder");

		if(item.getId()==null || item.getId().isEmpty())
			throw new Exception("Item id is null or empty");

		// we make a copy of showable types
		List<ItemType> showableTypesParam = new ArrayList<ItemType>(showableTypes);

		// we get sure that folders are displayed
		for (ItemType folder : Util.FOLDERS) {
			if (!showableTypesParam.contains(folder))
				showableTypesParam.add(folder);
		}

		boolean purgeEmpyFolders = !showEmptyFolders;
//			FilterCriteria filterCriteria = new FilterCriteria(allowedMimeTypes,requiredProperties, allowedFileExtensions);

		GWT.log("loading workspace folder by item id from server: "+item.getId());
		WorkspaceExplorerConstants.workspaceNavigatorService.getFolder(item, showableTypesParam, purgeEmpyFolders, filterCriteria, loadGcubeProperties, new AsyncCallback<Item>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				setAlert(caught.getMessage(), AlertType.ERROR);
				GWT.log("Error loading workspace folder from server",caught);
			}

			@Override
			public void onSuccess(Item result) {
				if(item.getName()==null || item.getName().isEmpty())
					item.setName(result.getName());

				updateExplorer(result.getChildren());

				setDisplayingFolderItem(result);
			}
		});

	}
}
