/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.client;

import gwt.material.design.client.ui.MaterialToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gcube.portlets.user.workspaceexplorerapp.client.WorkspaceExplorerSelectNotification.HasWorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.user.workspaceexplorerapp.client.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.user.workspaceexplorerapp.client.download.RequestBuilderWorkspaceValidateItem;
import org.gcube.portlets.user.workspaceexplorerapp.client.download.WindowOpenParameter;
import org.gcube.portlets.user.workspaceexplorerapp.client.event.BreadcrumbClickEvent;
import org.gcube.portlets.user.workspaceexplorerapp.client.event.BreadcrumbClickEventHandler;
import org.gcube.portlets.user.workspaceexplorerapp.client.event.ClickItemEvent;
import org.gcube.portlets.user.workspaceexplorerapp.client.event.ClickItemEventHandler;
import org.gcube.portlets.user.workspaceexplorerapp.client.event.DownloadItemEvent;
import org.gcube.portlets.user.workspaceexplorerapp.client.event.DownloadItemEventHandler;
import org.gcube.portlets.user.workspaceexplorerapp.client.event.LoadFolderEvent;
import org.gcube.portlets.user.workspaceexplorerapp.client.event.LoadFolderEventHandler;
import org.gcube.portlets.user.workspaceexplorerapp.client.event.OrderDataByEvent;
import org.gcube.portlets.user.workspaceexplorerapp.client.event.OrderDataByEventHandler;
import org.gcube.portlets.user.workspaceexplorerapp.client.event.RightClickItemEvent;
import org.gcube.portlets.user.workspaceexplorerapp.client.event.RightClickItemEventHandler;
import org.gcube.portlets.user.workspaceexplorerapp.client.grid.DisplayField;
import org.gcube.portlets.user.workspaceexplorerapp.client.view.Breadcrumbs;
import org.gcube.portlets.user.workspaceexplorerapp.client.view.PopupContextMenu;
import org.gcube.portlets.user.workspaceexplorerapp.client.view.WorkspaceExplorer;
import org.gcube.portlets.user.workspaceexplorerapp.shared.Item;
import org.gcube.portlets.user.workspaceexplorerapp.shared.ItemType;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * The Class WorkspaceResourcesExplorerPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 3, 2015
 */
public class WorkspaceResourcesExplorerPanel extends DockPanel implements HasWorskpaceExplorerSelectNotificationListener{

	public HandlerManager eventBus;
	private Breadcrumbs breadcrumbs;
	private boolean isSelect = false;
	private String captionTxt;
//	private WorkspaceExplorerController controller;
	private List<WorskpaceExplorerSelectNotificationListener> listeners = new ArrayList<WorskpaceExplorerSelectNotificationListener>();
	private VerticalPanel mainVP = new VerticalPanel();
//	private HorizontalPanel footerHP = new HorizontalPanel();
	private WorkspaceExplorer wsExplorer;
	private ScrollPanel centerScrollable = new ScrollPanel();
//	private ScrollPanel southPanel = new ScrollPanel();
	private String folderId;
	private String folderName;
	public static final DisplayField[] displayFields = new DisplayField[]{DisplayField.ICON, DisplayField.NAME, DisplayField.OWNER, DisplayField.CREATION_DATE};

	/**
	 * Instantiates a new workspace folder explorer select panel.
	 *
	 * @param eventBus the event bus
	 * @param folderId the folder id like root folder to explore
	 * @param folderName the folder name
	 * @throws Exception the exception
	 */
	public WorkspaceResourcesExplorerPanel(HandlerManager eventBus, String folderId, String folderName) throws Exception {
		initExplorer(eventBus, folderId, folderName, ItemType.values(), ItemType.values());
	}

	/**
	 * Inits the explorer.
	 *
	 * @param eventBus the event bus
	 * @param folderId the folder id
	 * @param folderName the folder name
	 * @param selectableTypes the selectable types
	 * @param showableTypes the showable types
	 * @throws Exception the exception
	 */
	private void initExplorer(HandlerManager eventBus, String folderId, String folderName, ItemType[] selectableTypes, ItemType[] showableTypes) throws Exception{
		this.folderId = folderId;
		this.folderName = folderName;
		this.eventBus = eventBus;
		this.breadcrumbs = new Breadcrumbs(eventBus);
		bindEvents();

		wsExplorer = new WorkspaceExplorer(eventBus, showableTypes, selectableTypes, displayFields);
		Item item = new Item(folderId, folderName, true);
		if(folderId!=null && !folderId.isEmpty())
			wsExplorer.loadFolder(item);
		initPanel("");
	}

	/**
	 * Instantiates a new workspace explorer select panel.
	 *
	 * @param folderId the folder id
	 * @param showOnlyFolders the show only folders
	 * @throws Exception the exception
	 */
	public WorkspaceResourcesExplorerPanel(HandlerManager eventBus, String folderId, boolean showOnlyFolders) throws Exception {

		if(showOnlyFolders){
			ItemType[] itemsType = new ItemType[1];
			itemsType[0] = ItemType.FOLDER;
			initExplorer(eventBus, folderId, "", itemsType, itemsType);
		}else{
			initExplorer(eventBus, folderId, "", ItemType.values(), ItemType.values());
		}
	}


	/**
	 * Bind events.
	 */
	private void bindEvents(){

		eventBus.addHandler(ClickItemEvent.TYPE, new ClickItemEventHandler() {

			@Override
			public void onClick(final ClickItemEvent clickItemEvent) {
				isSelect = false;
				Set<Item> items = wsExplorer.getItemsSelected();
				List<Item> lstItems = toList(items);

				//Return if item is not selectable
				if(!itemIsSelectable(lstItems.get(0))){
					notifyNotValidSelection();
					return;
				}

				notifySelectedItem(lstItems.get(0));
				isSelect = true;
			}
		});

		eventBus.addHandler(LoadFolderEvent.TYPE, new LoadFolderEventHandler() {

			@Override
			public void onLoadFolder(LoadFolderEvent loadFolderEvent) {

				if(loadFolderEvent.getTargetFolder()==null)
					return;

				Item item = loadFolderEvent.getTargetFolder();
				if(item.isFolder()){
					try {
						wsExplorer.loadFolder(item);
						loadParentBreadcrumbByItemId(item.getId(), true);
//						clearMoreInfo();
					} catch (Exception e) {
						GWT.log(e.getMessage());
					}
				}
			}
		});

		eventBus.addHandler(BreadcrumbClickEvent.TYPE, new BreadcrumbClickEventHandler() {

			@Override
			public void onBreadcrumbClick(BreadcrumbClickEvent breadcrumbClickEvent) {
				if(breadcrumbClickEvent.getTargetItem()!=null)
					eventBus.fireEvent(new LoadFolderEvent(breadcrumbClickEvent.getTargetItem()));
			}
		});

		eventBus.addHandler(OrderDataByEvent.TYPE, new OrderDataByEventHandler() {

			@Override
			public void onOrderDataBy(OrderDataByEvent orderDataByEvent) {

				wsExplorer.getItTables().sortDataBy(orderDataByEvent.getLabel());

				/*ArrayList<Item> items = ItemComparatorUtility.sortItems(DisplayField.NAME, true, wsExplorer.getItTables().getDataProvider().getList());
				wsExplorer.updateExplorer(items);*/
			}
		});


		eventBus.addHandler(RightClickItemEvent.TYPE, new RightClickItemEventHandler() {

			@Override
			public void onClick(RightClickItemEvent rightClickItemEvent) {

				if(rightClickItemEvent.getItem()!=null && rightClickItemEvent.getItem().getId()!=null){

					PopupContextMenu popupCM = new PopupContextMenu(true, eventBus, rightClickItemEvent.getItem());
					popupCM.showPopup(rightClickItemEvent.getXPos(), Window.getScrollTop()+rightClickItemEvent.getYPos());

		    		/*final PopupPanel contextMenu = new PopupPanel(true);
		    		contextMenu.getElement().getStyle().setBackgroundColor("#F5F5F5");
		    		Navigation nav = new Navigation(eventBus, rightClickItemEvent.getItem());
		    		nav.addCommandOnDownloadClick(new Command() {

						@Override
						public void execute() {
							contextMenu.hide();
						}
					});
		    		contextMenu.add(nav);
		    		contextMenu.setPopupPosition(rightClickItemEvent.getXPos(), Window.getScrollTop()+rightClickItemEvent.getYPos());
		    		contextMenu.show();*/
				}
			}
		});

		eventBus.addHandler(DownloadItemEvent.TYPE, new DownloadItemEventHandler() {

			@Override
			public void onDownloadItem(DownloadItemEvent downloadItemEvent) {
				GWT.log("Fired event DownloadItemEvent");
				String itemIds = "";
				if(downloadItemEvent.getItem()!=null)
					itemIds = downloadItemEvent.getItem().getId()+WorkspaceExplorerAppConstants.IDS_SEPARATOR;
				else{

					List<Item> lstItems = toList(wsExplorer.getItemsSelected());
					for (Item item : lstItems) {
						itemIds +=item.getId()+WorkspaceExplorerAppConstants.IDS_SEPARATOR;
					}
				}
				GWT.log("itemIds: "+itemIds);
				if(!itemIds.isEmpty()){
//					MaterialToast.fireToast("Download...");

					switch (downloadItemEvent.getType()) {
					case DOWNLOAD:
						MaterialToast.fireToast("Download...");
						try {
							new RequestBuilderWorkspaceValidateItem(RequestBuilder.GET,WorkspaceExplorerAppConstants.DOWNLOAD_WORKSPACE_SERVICE, WorkspaceExplorerAppConstants.IDS+"="+itemIds, "_self", downloadHandlerCallback);
						} catch (Exception e) {
							Window.alert("Sorry, an error occurred while contacting server, try again");
						}
						break;
					case PREVIEW:
						break;
					case OPEN:
						MaterialToast.fireToast("Showing...");
						try {
							new RequestBuilderWorkspaceValidateItem(RequestBuilder.GET, WorkspaceExplorerAppConstants.DOWNLOAD_WORKSPACE_SERVICE, WorkspaceExplorerAppConstants.IDS+"="+itemIds+"&viewContent=true", "_blank", downloadHandlerCallback);

						} catch (Exception e) {
							Window.alert("Sorry, an error occurred while contacting server, try again");
						}
						break;
					default:
						break;
					}


					/*final NewBrowserWindow newBW = NewBrowserWindow.open("", "_self", "");
					WorkspaceExplorerAppConstants.workspaceNavigatorService.getPublicLinkForItemId(itemId, new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
						}

						@Override
						public void onSuccess(String url) {

							if(url!=null){
								newBW.setUrl(url);
							}else
								Window.alert("Sorry, the item selected is not available for download");

						}
					});*/
				}
			}
		});
	}


	/**
	 * To list.
	 *
	 * @param items the items
	 * @return the list
	 */
	private List<Item> toList(Set<Item> items){
		if(items!=null && items.size()>0){
			List<Item> lstItems = new ArrayList<Item> (items.size());
			lstItems.addAll(items);
			return lstItems;
		}
		return null;
	}


	/**
	 * Load parent breadcrumb by item id.
	 *
	 * @param itemIdentifier the item identifier
	 * @param includeItemAsParent the include item as parent
	 */
	protected void loadParentBreadcrumbByItemId(final String itemIdentifier, boolean includeItemAsParent){

		GWT.log("Reload Parent Breadcrumb: [Item id: "+itemIdentifier+"]");

		WorkspaceExplorerAppConstants.workspaceNavigatorService.getBreadcrumbsByItemIdentifierToParentLimit(itemIdentifier, folderId, includeItemAsParent,  new AsyncCallback<List<Item>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log(caught.getMessage());
				wsExplorer.setAlert(caught.getMessage(), AlertType.ERROR);
			}

			@Override
			public void onSuccess(List<Item> result) {
				if(result!=null){
					breadcrumbs.setPath(result);
//					clearMoreInfo();
				}
			}
		});
	}


	/**
	 * Refresh root folder view.
	 */
	public void refreshRootFolderView(){
		Item item = new Item(folderId, folderName, true);
		eventBus.fireEvent(new LoadFolderEvent(item));
	}

//	/**
//	 * Clear more info.
//	 */
//	private void clearMoreInfo(){
//		southPanel.clear();
//	}

	/**
	 * Inits the panel.
	 *
	 * @param captionTxt the caption txt is the tool-tip
	 */
	private void initPanel(String captionTxt) {
		this.captionTxt = captionTxt;
		this.getElement().setId("WorkspaceExplorerContainer");
		this.getElement().setAttribute("id", "WorkspaceExplorerContainer");
		this.getElement().addClassName("workspace-explorer-container");

		if(this.captionTxt!=null && !this.captionTxt.isEmpty())
			setTitle(this.captionTxt);

	    setWidth("100%");
	    add(breadcrumbs, DockPanel.NORTH);
		mainVP.add(wsExplorer.getPanel());
	    centerScrollable.add(mainVP);
	    add(centerScrollable, DockPanel.CENTER);
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
	 * @return the wsExplorer
	 */
	public WorkspaceExplorer getWsExplorer() {
		return wsExplorer;
	}

	/**
	 * Gets the event bus.
	 *
	 * @return the eventBus
	 */
	public HandlerManager getEventBus() {

		return eventBus;
	}

	public AsyncCallback<WindowOpenParameter> downloadHandlerCallback = new AsyncCallback<WindowOpenParameter>() {

		@Override
		public void onFailure(Throwable caught) {

		}

		@Override
		public void onSuccess(WindowOpenParameter windowOpenParam) {
			String params = "?"+windowOpenParam.getParameters();
			if(params.length()>1)
				params+="&";
			params+=WorkspaceExplorerAppConstants.REDIRECTONERROR+"="+windowOpenParam.isRedirectOnError();
			windowOpenParam.getBrowserWindow().setUrl(WorkspaceExplorerAppConstants.DOWNLOAD_WORKSPACE_SERVICE+params);
		}
	};
}
