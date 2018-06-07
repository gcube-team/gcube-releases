/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.client.explore;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.wsexplorer.client.Util;
import org.gcube.portlets.widgets.wsexplorer.client.WorkspaceExplorerConstants;
import org.gcube.portlets.widgets.wsexplorer.client.event.BreadcrumbClickEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.BreadcrumbClickEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.event.ClickItemEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.ClickItemEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadFolderEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadFolderEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectBNotification.HasWorskpaceExplorerSelectBNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectBNotification.WorskpaceExplorerSelectBNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.view.Breadcrumbs;
import org.gcube.portlets.widgets.wsexplorer.client.view.WorkspaceExplorer;
import org.gcube.portlets.widgets.wsexplorer.client.view.grid.ItemsTable.DISPLAY_FIELD;
import org.gcube.portlets.widgets.wsexplorer.shared.FilterCriteria;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

// TODO: Auto-generated Javadoc
/**
 * The Class WorkspaceResourcesExplorerPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 11, 2017
 */
public class WorkspaceResourcesBExplorerPanel extends DockPanel implements HasWorskpaceExplorerSelectBNotificationListener{

	/** The Constant ITEMS_PER_PAGE. */
	protected static final int ITEMS_PER_PAGE = WorkspaceExplorerConstants.ITEMS_PER_PAGE;
	
	/** The Constant ITEM_START_INDEX. */
	protected static final int ITEM_START_INDEX = WorkspaceExplorerConstants.ITEM_START_INDEX;

	/** The event bus. */
	public final HandlerManager eventBus = new HandlerManager(null);
	
	/** The breadcrumbs. */
	protected Breadcrumbs breadcrumbs = new Breadcrumbs(eventBus);
	
	/** The is select. */
	protected boolean isSelect = false;
	
	/** The caption txt. */
	protected String captionTxt;
	
	/** The listeners. */
	protected List<WorskpaceExplorerSelectBNotificationListener> listeners = new ArrayList<WorskpaceExplorerSelectBNotificationListener>();
	
	/** The ws explorer. */
	protected WorkspaceExplorer wsExplorer;
	
	/** The center scrollable. */
	protected VerticalPanel centerScrollable = new VerticalPanel();

/** The folder id. */
//	private ScrollPanel southPanel = new ScrollPanel();
	protected String folderId;
	
	/** The folder name. */
	protected String folderName;
	
	/** The show properties. */
	protected List<String> showProperties;
	
	/** The filter criteria. */
	protected FilterCriteria filterCriteria;
	
	/** The offset breadcrumb. */
	protected final int offsetBreadcrumb = 40;
	
	/** The parent height. */
	protected int parentHeight = -1;


	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.Widget#onLoad()
	 */
	@Override
	protected void onLoad() {

		// TODO Auto-generated method stub
		super.onLoad();
		int newHeight = getValidAncestorsHeight(this.getElement());

		if (newHeight>-1){
			int nh = getNewHeightForContainer(newHeight);
			Util.console("Height founded through ancestors is: "+nh);
			if(nh>0){
				parentHeight = nh;
				Util.console("WorkspaceResourcesExplorerPanel Set new height to center panel: "+nh);
				wsExplorer.getPanel().setHeight(nh+"px");
			}
		}else
			Util.console("No height found through anchestors");

	}

	/**
	 * Gets the valid ancestors height.
	 *
	 * @param ele the ele
	 * @return the valid ancestors height
	 */
	public int getValidAncestorsHeight(Element ele){

		try{

			if (ele == null)
				return -1;

			Element parent = ele.getParentElement();
			if(parent==null){
				return -1;
			}

			String height = parent.getStyle().getHeight();
			if(height!=null && !height.isEmpty() && height.contains("px")){
				String purgedHeight = height.replaceAll("px", "");
				return Integer.parseInt(purgedHeight);
			}

			return getValidAncestorsHeight(ele.getParentElement());
		}catch (Exception e){
			GWT.log(e.getMessage());
			return -1;

		}
	}


	/**
	 * Instantiates a new workspace resources explorer panel.
	 */
	public WorkspaceResourcesBExplorerPanel(){

	}

	/**
	 * Instantiates a new workspace folder explorer panel.
	 *
	 * @param folderId the folder id like root folder to explore
	 * @param folderName the folder name
	 * @throws Exception the exception
	 */
	public WorkspaceResourcesBExplorerPanel(String folderId, String folderName) throws Exception {
		initExplorer(folderId, folderName, ItemType.values(), ItemType.values(), null, null, false, null);
	}

	/**
	 * Instantiates a new workspace explorer panel.
	 *
	 * @param folderId the folder id
	 * @param showOnlyFolders the show only folders
	 * @throws Exception the exception
	 */
	public WorkspaceResourcesBExplorerPanel(String folderId, boolean showOnlyFolders) throws Exception {

		if(showOnlyFolders){
			ItemType[] itemsType = new ItemType[1];
			itemsType[0] = ItemType.FOLDER;
			initExplorer(folderId, "", itemsType, itemsType, null, null, false, null);
		}else{
			initExplorer(folderId, "", ItemType.values(), ItemType.values(), null, null, false, null);
		}
	}

	/**
	 * Instantiates a new workspace explorer panel.
	 *
	 * @param folderId the folder id
	 * @param showOnlyFolders the show only folders
	 * @param showProperties the show properties - show the input properties as column/s
	 * @param filter the filter - get only workspace item/s with input key=value like GcubeProperty
	 * @throws Exception the exception
	 */
	public WorkspaceResourcesBExplorerPanel(String folderId, boolean showOnlyFolders, List<String> showProperties, FilterCriteria filter) throws Exception {

		if(showOnlyFolders){
			ItemType[] itemsType = new ItemType[1];
			itemsType[0] = ItemType.FOLDER;
			initExplorer(folderId, "", itemsType, itemsType, showProperties, filter, false, null);
		}else{
			initExplorer(folderId, "", ItemType.values(), ItemType.values(), showProperties, filter, false, null);
		}
	}

	/**
	 * Instantiates a new workspace explorer panel.
	 *
	 * @param folderId the folder id
	 * @param showOnlyFolders the show only folders
	 * @param showProperties the show properties - show the input properties as column/s
	 * @param filter the filter - get only workspace item/s with input key=value like GcubeProperty
	 * @param showGcubeInfo the show gcube info - if true shows all the properties associated to a file (or a gcube item) by opening a popup window when clicking on the item
	 * @param sortByColumn the sort by column
	 * @throws Exception the exception
	 */
	public WorkspaceResourcesBExplorerPanel(String folderId, boolean showOnlyFolders, List<String> showProperties, FilterCriteria filter, boolean showGcubeInfo, DISPLAY_FIELD sortByColumn) throws Exception {

		if(showOnlyFolders){
			ItemType[] itemsType = new ItemType[1];
			itemsType[0] = ItemType.FOLDER;
			initExplorer(folderId, "", itemsType, itemsType, showProperties, filter, showGcubeInfo, sortByColumn);
		}else{
			initExplorer(folderId, "", ItemType.values(), ItemType.values(), showProperties, filter, showGcubeInfo, sortByColumn);
		}
	}


	/**
	 * Inits the explorer.
	 *
	 * @param folderId the folder id
	 * @param folderName the folder name
	 * @param selectableTypes the selectable types
	 * @param showableTypes the showable types
	 * @param showProperties the show properties
	 * @param filter the filter
	 * @param showGcubeInfo the show gcube info
	 * @param sortByColumn the sort by column
	 * @throws Exception the exception
	 */
	protected void initExplorer(String folderId, String folderName, ItemType[] selectableTypes, ItemType[] showableTypes, List<String> showProperties, FilterCriteria filter, boolean showGcubeInfo, DISPLAY_FIELD sortByColumn) throws Exception{
		GWT.log("Using base init");
		setParameters(folderId, folderName, showProperties, filter);
		bindEvents();
		wsExplorer = new WorkspaceExplorer(eventBus, filter, showableTypes, selectableTypes, showProperties, showGcubeInfo, sortByColumn, new DISPLAY_FIELD[]{DISPLAY_FIELD.ICON, DISPLAY_FIELD.NAME,DISPLAY_FIELD.CREATION_DATE});
		wsExplorer.initTable(new ListDataProvider<Item>());
		Item item = new Item(folderId, folderName, true);
		wsExplorer.loadFolder(item, true, -1, -1, false);
		initPanel("");
	}

	/**
	 * Sets the parameters.
	 *
	 * @param folderId the folder id
	 * @param folderName the folder name
	 * @param showProperties the show properties
	 * @param filterCriteria the filter criteria
	 */
	protected void setParameters(
		String folderId, String folderName, List<String> showProperties,
		FilterCriteria filterCriteria) {
		this.folderId = folderId;
		this.folderName = folderName;
		this.showProperties = showProperties;
		this.filterCriteria = filterCriteria;
	}

	/**
	 * Bind events.
	 */
	private void bindEvents(){

		eventBus.addHandler(ClickItemEvent.TYPE, new ClickItemEventHandler() {

			@Override
			public <T> void onClick(final ClickItemEvent<T> clickItemEvent) {
				isSelect = false;
				Item item = wsExplorer.getItemSelected();

				//Return if item is not selectable
				if(!itemIsSelectable(item)){
					notifyNotValidSelection();
					return;
				}

				notifySelectedItem(wsExplorer.getItemSelected());
				isSelect = true;
			}
		});

		eventBus.addHandler(LoadFolderEvent.TYPE, new LoadFolderEventHandler() {

			@Override
			public <T> void onLoadFolder(LoadFolderEvent<T> loadFolderEvent) {

				if(loadFolderEvent.getTargetItem()!=null){

					if(loadFolderEvent.getTargetItem() instanceof Item){
						Item item = (Item) loadFolderEvent.getTargetItem();
						if(item.isFolder()){
							try {
								wsExplorer.loadFolder(item, true, ITEM_START_INDEX, ITEMS_PER_PAGE, true);
								loadParentBreadcrumbByItemId(item.getId(), true);
								clearMoreInfo();
							} catch (Exception e) {
								GWT.log(e.getMessage());
							}
						}
					}
				}
			}
		});


		eventBus.addHandler(BreadcrumbClickEvent.TYPE, new BreadcrumbClickEventHandler() {

			@Override
			public void onBreadcrumbClick(BreadcrumbClickEvent breadcrumbClickEvent) {
				eventBus.fireEvent(new LoadFolderEvent<Item>(breadcrumbClickEvent.getTargetItem()));
			}
		});
	}


	/**
	 * Load parent breadcrumb by item id.
	 *
	 * @param itemIdentifier the item identifier
	 * @param includeItemAsParent the include item as parent
	 */
	protected void loadParentBreadcrumbByItemId(final String itemIdentifier, boolean includeItemAsParent){

		GWT.log("Reload Parent Breadcrumb: [Item id: "+itemIdentifier+"]");

		WorkspaceExplorerConstants.workspaceNavigatorService.getBreadcrumbsByItemIdentifierToParentLimit(itemIdentifier, folderId, includeItemAsParent,  new AsyncCallback<List<Item>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log(caught.getMessage());
				wsExplorer.setAlert(caught.getMessage(), AlertType.ERROR);
			}

			@Override
			public void onSuccess(List<Item> result) {
				if(result!=null){
					notifyOnBreadcrumbChanged(result.get(result.size()-1));
					breadcrumbs.setPath(result);
					clearMoreInfo();
				}
			}
		});
	}


	/**
	 * Refresh root folder view.
	 */
	public void refreshRootFolderView(){
		Item item = new Item(folderId, folderName, true);
		eventBus.fireEvent(new LoadFolderEvent<Item>(item));
	}

	/**
	 * Clear more info.
	 */
	protected void clearMoreInfo(){
//		southPanel.clear();
	}

	/**
	 * Inits the panel.
	 *
	 * @param captionTxt the caption txt is the tool-tip
	 */
	protected void initPanel(String captionTxt) {
		this.captionTxt = captionTxt;
		if(this.captionTxt!=null && !this.captionTxt.isEmpty())
			setTitle(this.captionTxt);
	    add(breadcrumbs, DockPanel.NORTH);
	    setCellHeight(breadcrumbs, offsetBreadcrumb+"px");
	    centerScrollable.add(wsExplorer.getPanel());
	    add(centerScrollable, DockPanel.CENTER);
	}

	/**
	 * Gets the north panel height.
	 *
	 * @return the north panel height
	 */
	public void getNorthPanelHeight(){

	}

	/**
	 * Notify selected item.
	 *
	 * @param selected the selected
	 */
	protected void notifySelectedItem(Item selected){

		for (WorskpaceExplorerSelectBNotificationListener worskpaceExplorerNotificationListener : listeners) {
			worskpaceExplorerNotificationListener.onSelectedItem(selected);
		}
	}
	

	/**
	 * Notify on breadcrumb changed.
	 *
	 * @param selected the selected
	 */
	protected void notifyOnBreadcrumbChanged(Item selected){

		for (WorskpaceExplorerSelectBNotificationListener worskpaceExplorerNotificationListener : listeners) {
			worskpaceExplorerNotificationListener.onBreadcrumbChanged(selected);
		}
	}


	/**
	 * Notify aborted.
	 */
	protected void notifyAborted(){

		for (WorskpaceExplorerSelectBNotificationListener worskpaceExplorerNotificationListener : listeners) {
			worskpaceExplorerNotificationListener.onAborted();
		}
	}


	/**
	 * Notify not valid selection.
	 */
	protected void notifyNotValidSelection(){

		for (WorskpaceExplorerSelectBNotificationListener worskpaceExplorerNotificationListener : listeners) {
			worskpaceExplorerNotificationListener.onNotValidSelection();
		}
	}


	/**
	 * Notify failed.
	 *
	 * @param t the t
	 */
	@SuppressWarnings("unused")
	protected void notifyFailed(Throwable t){

		for (WorskpaceExplorerSelectBNotificationListener worskpaceExplorerNotificationListener : listeners) {
			worskpaceExplorerNotificationListener.onFailed(t);
		}
	}

	/**
	 * Gets the caption txt.
	 *
	 * @return the captionTxt
	 */
	public String getCaptionTxt() {
		return captionTxt;
	}

	/**
	 * Checks if is valid hide.
	 *
	 * @return the isValidHide
	 */
	public boolean isValidHide() {
		return isSelect;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.notification.WorskpaceExplorerNotification.HasWorskpaceExplorerNotificationListener#addWorkspaceExplorerNotificationListener(org.gcube.portlets.widgets.wsexplorer.client.notification.WorskpaceExplorerNotification.WorskpaceExplorerNotificationListener)
	 */
	@Override
	public void addWorkspaceExplorerSelectNotificationListener(WorskpaceExplorerSelectBNotificationListener handler) {
		if(handler!=null)
			listeners.add(handler);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.notification.WorskpaceExplorerNotification.HasWorskpaceExplorerNotificationListener#removeWorkspaceExplorerNotificationListener(org.gcube.portlets.widgets.wsexplorer.client.notification.WorskpaceExplorerNotification.WorskpaceExplorerNotificationListener)
	 */
	@Override
	public void removeWorkspaceExplorerSelectNotificationListener(WorskpaceExplorerSelectBNotificationListener handler) {
		if(handler!=null){
			if(listeners.contains(handler))
				listeners.remove(handler);
		}
	}

	/**
	 * Item is selectable.
	 *
	 * @param item the item
	 * @return true, if successful
	 */
	public boolean itemIsSelectable(Item item){
		//GWT.log("Selectable type: "+wsExplorer.getSelectableTypes());
		//GWT.log("item: "+item);
		if (item!=null){
			boolean selectable = wsExplorer.getSelectableTypes().contains(item.getType());
			return selectable?true:false;
		}
		return false;
	}



	/**
	 * Gets the filter criteria.
	 *
	 * @return the filterCriteria
	 */
	public FilterCriteria getFilterCriteria() {

		return filterCriteria;
	}


	/**
	 * Updates filter criteria.
	 *
	 * @param filterCriteria the filter criteria
	 */
	public void updatesFilterCriteria(FilterCriteria filterCriteria) {

		this.filterCriteria = filterCriteria;
		wsExplorer.setNewFilterCriteria(filterCriteria);
		try {
			wsExplorer.loadFolder(wsExplorer.getDisplayingFolderItem(), true, ITEM_START_INDEX, ITEMS_PER_PAGE, true);
		}
		catch (Exception e) {
			wsExplorer.setAlert("Sorry, an error occurred during filter update", AlertType.ERROR);
		}
	}

	/**
	 * Updates filter criteria.
	 *
	 * @param showProperties the show properties
	 */
	public void updatesShowProperties(List<String> showProperties) {

		this.showProperties = showProperties;
		wsExplorer.setNewShowProperties(showProperties);
		try {
			wsExplorer.loadFolder(wsExplorer.getDisplayingFolderItem(), true, ITEM_START_INDEX, ITEMS_PER_PAGE, true);
		}
		catch (Exception e) {
			wsExplorer.setAlert("Sorry, an error occurred during show properties update", AlertType.ERROR);
		}
	}

	/**
	 * Updates filters and properties.
	 *
	 * @param filterCriteria the filter criteria
	 * @param showProperties the show properties
	 */
	public void updatesFiltersAndProperties(FilterCriteria filterCriteria, List<String> showProperties) {

		this.filterCriteria = filterCriteria;
		this.showProperties = showProperties;
		wsExplorer.setNewFilterCriteria(filterCriteria);
		wsExplorer.setNewShowProperties(showProperties);
		try {
			wsExplorer.loadFolder(wsExplorer.getDisplayingFolderItem(), true, ITEM_START_INDEX, ITEMS_PER_PAGE, true);
		}
		catch (Exception e) {
			wsExplorer.setAlert("Sorry, an error occurred during filters or properties update", AlertType.ERROR);
		}
	}


	/**
	 * Gets the show properties.
	 *
	 * @return the showProperties
	 */
	public List<String> getShowProperties() {

		return showProperties;
	}



	/**
	 * Gets the new height for container.
	 *
	 * @param parentHeight the parent height
	 * @return the new height for container
	 */
	protected int getNewHeightForContainer(int parentHeight){
		GWT.log("getNewHeightForContainer: "+parentHeight);
		if(parentHeight>0){
			int bh = breadcrumbs.getHeight();
			bh = bh>offsetBreadcrumb?bh:offsetBreadcrumb;
			if(parentHeight>bh)
				return parentHeight-bh;
		}
		return -1;
	}

	/**
	 * Sets the height to internal scroll.
	 *
	 * @param height the new height to internal scroll
	 */
	public void setHeightToInternalScroll(int height){
		GWT.log("setHeightToInternalScroll: "+height);
		int nh = getNewHeightForContainer(height);
		if(nh>0){
			Util.console("Set new height to center panel: "+nh);
			wsExplorer.getPanel().setHeight(nh+"px");
		}
	}
	
	public WorkspaceExplorer getWsExplorer() {
		return wsExplorer;
	}
}
