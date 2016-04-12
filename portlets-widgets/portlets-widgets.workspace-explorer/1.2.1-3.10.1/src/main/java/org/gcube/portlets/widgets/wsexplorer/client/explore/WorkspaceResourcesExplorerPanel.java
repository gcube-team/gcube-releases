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
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
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
	private Alert alertConfirm;
//	private WorkspaceExplorerController controller;
	private List<WorskpaceExplorerSelectNotificationListener> listeners = new ArrayList<WorskpaceExplorerSelectNotificationListener>();
	private VerticalPanel mainVP = new VerticalPanel();
	private HorizontalPanel footerHP = new HorizontalPanel();
	private WorkspaceExplorer wsExplorer;
	private ScrollPanel centerScrollable = new ScrollPanel();
	private ScrollPanel southPanel = new ScrollPanel();
	private String folderId;
	private String folderName;

	/**
	 * Instantiates a new workspace folder explorer select panel.
	 *
	 * @param folderId the folder id like root folder to explore
	 * @param folderName the folder name
	 * @throws Exception the exception
	 */
	public WorkspaceResourcesExplorerPanel(String folderId, String folderName) throws Exception {
		initExplorer(folderId, folderName, ItemType.values(), ItemType.values());
	}

	/**
	 * Inits the explorer.
	 *
	 * @param folderId the folder id
	 * @param folderName the folder name
	 * @param selectableTypes the selectable types
	 * @param showableTypes the showable types
	 * @throws Exception the exception
	 */
	private void initExplorer(String folderId, String folderName, ItemType[] selectableTypes, ItemType[] showableTypes) throws Exception{
		this.folderId = folderId;
		this.folderName = folderName;
		bindEvents();
		wsExplorer = new WorkspaceExplorer(eventBus, showableTypes, selectableTypes, new DISPLAY_FIELD[]{DISPLAY_FIELD.ICON, DISPLAY_FIELD.NAME, DISPLAY_FIELD.CREATION_DATE});
		Item item = new Item(folderId, folderName, true);
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
	public WorkspaceResourcesExplorerPanel(String folderId, boolean showOnlyFolders) throws Exception {

		if(showOnlyFolders){
			ItemType[] itemsType = new ItemType[1];
			itemsType[0] = ItemType.FOLDER;
			initExplorer(folderId, "", itemsType, itemsType);
		}else{
			initExplorer(folderId, "", ItemType.values(), ItemType.values());
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
			public void onLoadFolder(LoadFolderEvent loadFolderEvent) {

				if(loadFolderEvent.getTargetItem()!=null && loadFolderEvent.getTargetItem().isFolder()){
					try {
						wsExplorer.loadFolder(loadFolderEvent.getTargetItem());
						loadParentBreadcrumbByItemId(loadFolderEvent.getTargetItem().getId(), true);
						clearMoreInfo();
					} catch (Exception e) {
						GWT.log(e.getMessage());
					}
				}
			}
		});

		eventBus.addHandler(BreadcrumbClickEvent.TYPE, new BreadcrumbClickEventHandler() {

			@Override
			public void onBreadcrumbClick(BreadcrumbClickEvent breadcrumbClickEvent) {
				eventBus.fireEvent(new LoadFolderEvent(breadcrumbClickEvent.getTargetItem()));
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
		eventBus.fireEvent(new LoadFolderEvent(item));
	}

	/**
	 * Clear more info.
	 */
	private void clearMoreInfo(){
		southPanel.clear();
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
//	    centerScrollable.setSize(width+"px", height);
		setAlertConfirm("", false, null);

		mainVP.add(wsExplorer.getPanel());
		mainVP.add(footerHP);
	    centerScrollable.add(mainVP);
	    add(centerScrollable, DockPanel.CENTER);

	    add(footerHP, DockPanel.SOUTH);
	}


	/**
	 * Sets the alert confirm.
	 *
	 * @param html the html
	 * @param show the show
	 * @param item the item
	 */
	private void setAlertConfirm(String html, boolean show, final Item item){
		try{
			footerHP.remove(alertConfirm);
		}catch(Exception e){
			//silent
		}

		alertConfirm = new Alert();
		alertConfirm.setText(html);
		alertConfirm.setVisible(show);
		alertConfirm.setClose(true);
		alertConfirm.setType(AlertType.INFO);

		Button yes = new Button("Yes");
		yes.setType(ButtonType.LINK);
		yes.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if(item!=null){
					isSelect = true;
					notifySelectedItem(item);
				}
			}
		});

		Button no = new Button("No");
		no.setType(ButtonType.LINK);
		no.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				alertConfirm.close();
			}
		});
		alertConfirm.add(yes);
		alertConfirm.add(no);
		footerHP.insert(alertConfirm,0);
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
}
