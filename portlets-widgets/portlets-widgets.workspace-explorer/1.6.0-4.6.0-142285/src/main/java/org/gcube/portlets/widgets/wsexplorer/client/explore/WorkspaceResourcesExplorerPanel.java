/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.client.explore;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.wsexplorer.client.WorkspaceExplorerConstants;
import org.gcube.portlets.widgets.wsexplorer.client.event.BreadcrumbClickEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.BreadcrumbClickEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.event.ClickItemEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.ClickItemEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadFolderEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadFolderEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.HasWorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.view.Breadcrumbs;
import org.gcube.portlets.widgets.wsexplorer.client.view.WorkspaceExplorer;
import org.gcube.portlets.widgets.wsexplorer.client.view.grid.ItemsTable.DISPLAY_FIELD;
import org.gcube.portlets.widgets.wsexplorer.shared.FilterCriteria;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * The Class WorkspaceResourcesExplorerPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 3, 2015
 */
public class WorkspaceResourcesExplorerPanel extends DockPanel implements HasWorskpaceExplorerSelectNotificationListener{

	public final HandlerManager eventBus = new HandlerManager(null);
	private Breadcrumbs breadcrumbs = new Breadcrumbs(eventBus);
	private boolean isSelect = false;
	private String captionTxt;
	private List<WorskpaceExplorerSelectNotificationListener> listeners = new ArrayList<WorskpaceExplorerSelectNotificationListener>();
	private WorkspaceExplorer wsExplorer;
	private VerticalPanel centerScrollable = new VerticalPanel();
//	private ScrollPanel southPanel = new ScrollPanel();
	private String folderId;
	private String folderName;
	private List<String> showProperties;
	private FilterCriteria filterCriteria;

	private final int offsetBreadcrumb = 40;


	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.Widget#onLoad()
	 */
	@Override
	protected void onLoad() {

		// TODO Auto-generated method stub
		super.onLoad();
		Element parent = getParent().getElement();
		if(parent==null){
			GWT.log("WorkspaceResourcesExplorerPanel parent is null");
		}else{
			GWT.log("WorkspaceResourcesExplorerPanel parent exists");
			Style style = parent.getStyle();
			if(style==null){
				GWT.log("WorkspaceResourcesExplorerPanel style is null");
			}else{
				GWT.log("WorkspaceResourcesExplorerPanel style exists");
				String height = style.getHeight();
				GWT.log("WorkspaceResourcesExplorerPanel read height from parent: "+height);
				if(height!=null && !height.isEmpty() && height.contains("px")){
					String purgedHeight = height.replaceAll("px", "");
					int heightToInt = Integer.parseInt(purgedHeight);
					int nh = getNewHeightForContainer(heightToInt);
					if(nh>0){
						GWT.log("Set new height to center panel: "+nh);
						wsExplorer.getPanel().setHeight(nh+"px");
					}
				}else
					GWT.log("WorkspaceResourcesExplorerPanel read invalid height from parent!");

			}
		}
	}

	/**
	 * Instantiates a new workspace folder explorer panel.
	 *
	 * @param folderId the folder id like root folder to explore
	 * @param folderName the folder name
	 * @throws Exception the exception
	 */
	public WorkspaceResourcesExplorerPanel(String folderId, String folderName) throws Exception {
		initExplorer(folderId, folderName, ItemType.values(), ItemType.values(), null, null, false, null);
	}

	/**
	 * Instantiates a new workspace explorer panel.
	 *
	 * @param folderId the folder id
	 * @param showOnlyFolders the show only folders
	 * @throws Exception the exception
	 */
	public WorkspaceResourcesExplorerPanel(String folderId, boolean showOnlyFolders) throws Exception {

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
	public WorkspaceResourcesExplorerPanel(String folderId, boolean showOnlyFolders, List<String> showProperties, FilterCriteria filter) throws Exception {

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
	public WorkspaceResourcesExplorerPanel(String folderId, boolean showOnlyFolders, List<String> showProperties, FilterCriteria filter, boolean showGcubeInfo, DISPLAY_FIELD sortByColumn) throws Exception {

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
	 * @param showGcubeInfo the show gcube info - shows all the properties associated to a file (or a gcube item) stored into related gcube item by opening a popup window when clicking on the item
	 * @param sortByColumn the sort by column
	 * @throws Exception the exception
	 */
	private void initExplorer(String folderId, String folderName, ItemType[] selectableTypes, ItemType[] showableTypes, List<String> showProperties, FilterCriteria filter, boolean showGcubeInfo, DISPLAY_FIELD sortByColumn) throws Exception{
		this.folderId = folderId;
		this.folderName = folderName;
		this.showProperties = showProperties;
		this.filterCriteria = filter;
		bindEvents();
		wsExplorer = new WorkspaceExplorer(eventBus, filter, showableTypes, selectableTypes, showProperties, showGcubeInfo, sortByColumn, new DISPLAY_FIELD[]{DISPLAY_FIELD.ICON, DISPLAY_FIELD.NAME, DISPLAY_FIELD.CREATION_DATE});
		Item item = new Item(folderId, folderName, true);
		wsExplorer.loadFolder(item, true);
		initPanel("");
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
								wsExplorer.loadFolder(item, true);
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
	private void loadParentBreadcrumbByItemId(final String itemIdentifier, boolean includeItemAsParent){

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
	private void clearMoreInfo(){
//		southPanel.clear();
	}

	/**
	 * Inits the panel.
	 *
	 * @param captionTxt the caption txt is the tool-tip
	 */
	private void initPanel(String captionTxt) {
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
	private void notifySelectedItem(Item selected){

		for (WorskpaceExplorerSelectNotificationListener worskpaceExplorerNotificationListener : listeners) {
			worskpaceExplorerNotificationListener.onSelectedItem(selected);
		}
	}

	/**
	 * Notify aborted.
	 */
	private void notifyAborted(){

		for (WorskpaceExplorerSelectNotificationListener worskpaceExplorerNotificationListener : listeners) {
			worskpaceExplorerNotificationListener.onAborted();
		}
	}


	/**
	 * Notify not valid selection.
	 */
	private void notifyNotValidSelection(){

		for (WorskpaceExplorerSelectNotificationListener worskpaceExplorerNotificationListener : listeners) {
			worskpaceExplorerNotificationListener.onNotValidSelection();
		}
	}

	/**
	 * Notify failed.
	 *
	 * @param t the t
	 */
	@SuppressWarnings("unused")
	private void notifyFailed(Throwable t){

		for (WorskpaceExplorerSelectNotificationListener worskpaceExplorerNotificationListener : listeners) {
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
	public void addWorkspaceExplorerSelectNotificationListener(WorskpaceExplorerSelectNotificationListener handler) {
		if(handler!=null)
			listeners.add(handler);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.notification.WorskpaceExplorerNotification.HasWorskpaceExplorerNotificationListener#removeWorkspaceExplorerNotificationListener(org.gcube.portlets.widgets.wsexplorer.client.notification.WorskpaceExplorerNotification.WorskpaceExplorerNotificationListener)
	 */
	@Override
	public void removeWorkspaceExplorerSelectNotificationListener(WorskpaceExplorerSelectNotificationListener handler) {
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
		GWT.log("Selectable type: "+wsExplorer.getSelectableTypes());
		GWT.log("item: "+item);
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
			wsExplorer.loadFolder(wsExplorer.getDisplayingFolderItem(), true);
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
			wsExplorer.loadFolder(wsExplorer.getDisplayingFolderItem(), true);
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
			wsExplorer.loadFolder(wsExplorer.getDisplayingFolderItem(), true);
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
	private int getNewHeightForContainer(int parentHeight){
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

		int nh = getNewHeightForContainer(height);
		if(nh>0){
			GWT.log("Set new height to center panel: "+nh);
			wsExplorer.getPanel().setHeight(nh+"px");
		}
	}
}
