package org.gcube.portlets.user.workspaceexplorerapp.client.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.gcube.portlets.user.workspaceexplorerapp.client.Util;
import org.gcube.portlets.user.workspaceexplorerapp.client.WorkspaceExplorerApp;
import org.gcube.portlets.user.workspaceexplorerapp.client.WorkspaceExplorerAppConstants;
import org.gcube.portlets.user.workspaceexplorerapp.client.event.RootLoadedEvent;
import org.gcube.portlets.user.workspaceexplorerapp.client.grid.DisplayField;
import org.gcube.portlets.user.workspaceexplorerapp.client.grid.ItemsTable;
import org.gcube.portlets.user.workspaceexplorerapp.client.resources.newres.WorkspaceExplorerAppResources;
import org.gcube.portlets.user.workspaceexplorerapp.shared.FilterCriteria;
import org.gcube.portlets.user.workspaceexplorerapp.shared.Item;
import org.gcube.portlets.user.workspaceexplorerapp.shared.ItemType;

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


/**
 * The Class WorkspaceExplorer.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Aug 4, 2015
 */
public class WorkspaceExplorer implements ShowableTypes, SelectableTypes{


	protected static final HorizontalPanel LOADING_PANEL = new HorizontalPanel();
	protected static final Image LOADING_IMAGE = WorkspaceExplorerAppResources.getIconLoading().createImage();

	static {
		LOADING_PANEL.setWidth("95%");
		LOADING_PANEL.getElement().getStyle().setPadding(5.0, Unit.PX);
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
	private ItemsTable itTables;
	private DisplayField[] displayFields;
	private HandlerManager eventBus;


	/**
	 * Instantiates a new workspace explorer.
	 *
	 * @param eventBus the event bus
	 * @param showableTypes the showable types
	 * @param selectableTypes the selectable types
	 * @param fields the fields
	 */
	public WorkspaceExplorer(HandlerManager eventBus, ItemType[] showableTypes, ItemType[] selectableTypes, DisplayField...fields) {
		this.eventBus = eventBus;
		setShowableTypes(showableTypes);
		setSelectableTypes(selectableTypes);
		itTables = new ItemsTable(eventBus, true, fields);
		explorerPanel = new ScrollPanel();
		explorerPanel.getElement().setId("explorer_panel_we");
		explorerPanel.getElement().setPropertyString("id", "explorer_panel_we");
	}


	/**
	 * Instantiates a new workspace explorer.
	 *
	 * @param eventBus the event bus
	 * @param filterCriteria the filter criteria
	 * @param showableTypes the showable types
	 * @param selectableTypes the selectable types
	 * @param fields the fields
	 */
	public WorkspaceExplorer(HandlerManager eventBus, FilterCriteria filterCriteria, ItemType[] showableTypes, ItemType[] selectableTypes, DisplayField...fields) {
		this(eventBus, showableTypes, selectableTypes, fields);
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

		WorkspaceExplorerAppConstants.workspaceNavigatorService.getRoot(showableTypesParam, purgeEmpyFolders, filterCriteria, new AsyncCallback<Item>() {

			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				setAlert(caught.getMessage(), AlertType.ERROR);
				GWT.log("Error loading workspace tree from server",caught);
			}

			public void onSuccess(Item item) {
				eventBus.fireEvent(new RootLoadedEvent(item));
				updateExplorer(item.getChildren());
			}

		});
	}

	/**
	 * Load folder.
	 *
	 * @param item the item
	 * @throws Exception the exception
	 */
	public void loadFolder(final Item item) throws Exception {
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
		WorkspaceExplorerAppConstants.workspaceNavigatorService.getFolder(item, showableTypesParam, purgeEmpyFolders, filterCriteria, new AsyncCallback<Item>() {

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
			}
		});
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
		WorkspaceExplorerAppConstants.workspaceNavigatorService.getMySpecialFolder(showableTypesParam, purgeEmpyFolders, filterCriteria, new AsyncCallback<Item>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
					setAlert(caught.getMessage(), AlertType.ERROR);
					GWT.log("Error loading workspace folder from server",caught);
				}

				@Override
				public void onSuccess(Item items) {
					updateExplorer(items.getChildren());
				}
		});
	}

	/**
	 * Update explorer.
	 *
	 * @param items the items
	 */
	public void updateExplorer(ArrayList<Item> items){
		GWT.log("workspace explorer updating..");
		explorerPanel.clear();
//		itTables = new ItemsTable(true, displayFields);
		itTables.updateItems(items, true);
		explorerPanel.add(itTables.getCellTable());
		GWT.log("workspace explorer updated");
		WorkspaceExplorerApp.updateSize();
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
	public DisplayField[] getDisplayFields() {
		return displayFields;
	}


	/**
	 * Sets the display fields.
	 *
	 * @param displayFields the displayFields to set
	 */
	public void setDisplayFields(DisplayField[] displayFields) {
		this.displayFields = displayFields;
		itTables.setDisplayFields(displayFields);
	}


	/**
	 * Gets the it tables.
	 *
	 * @return the itTables
	 */
	public ItemsTable getItTables() {
		return itTables;
	}

	/**
	 * Gets the items selected.
	 *
	 * @return the items selected
	 */
	public Set<Item> getItemsSelected(){
		return itTables.getSelectedItems();
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
}
