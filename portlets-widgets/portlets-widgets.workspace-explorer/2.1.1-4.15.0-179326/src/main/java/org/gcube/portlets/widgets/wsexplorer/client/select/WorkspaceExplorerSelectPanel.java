/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.client.select;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.wsexplorer.client.Util;
import org.gcube.portlets.widgets.wsexplorer.client.WorkspaceExplorerConstants;
import org.gcube.portlets.widgets.wsexplorer.client.WorkspaceExplorerController;
import org.gcube.portlets.widgets.wsexplorer.client.event.BreadcrumbClickEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.BreadcrumbClickEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.event.ClickItemEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.ClickItemEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadFolderEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadMySpecialFolderEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadMySpecialFolderEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadRootEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.RootLoadedEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.RootLoadedEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.HasWorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.shared.FilterCriteria;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;



/**
 * The Class WorkspaceExplorerSelectPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 28, 2015
 */
public class WorkspaceExplorerSelectPanel extends ScrollPanel implements HasWorskpaceExplorerSelectNotificationListener{

	private WorkspaceExplorerSelectPanel INSTANCE = this;
	private boolean isSelect = false;
	private String captionTxt;
	private Alert alertConfirm;
	private WorkspaceExplorerController controller;
	private List<WorskpaceExplorerSelectNotificationListener> listeners = new ArrayList<WorskpaceExplorerSelectNotificationListener>();
	private VerticalPanel mainVP = new VerticalPanel();
	private HorizontalPanel footerHP = new HorizontalPanel();

	/**
	 * Instantiates a new workspace explorer select panel.
	 *
	 * @param captionTxt the tool-tip of the panel
	 */
	public WorkspaceExplorerSelectPanel(String captionTxt) {
		controller = new WorkspaceExplorerController(WorkspaceExplorerConstants.AUTO);
		loadExplorer(captionTxt, null);
	}

	/**
	 * Instantiates a new workspace explorer select panel.
	 *
	 * @param captionTxt the tool-tip of the panel
	 * @param filterCriteria the filter criteria
	 */
	public WorkspaceExplorerSelectPanel(String captionTxt, FilterCriteria filterCriteria) {
		controller = new WorkspaceExplorerController(filterCriteria, WorkspaceExplorerConstants.AUTO);
		loadExplorer(captionTxt, null);
	}


	/**
	 * Instantiates a new workspace explorer select panel.
	 *
	 * @param captionTxt the tool-tip of the panel
	 * @param filterCriteria the filter criteria
	 * @param selectableTypes the selectable types
	 */
	public WorkspaceExplorerSelectPanel(String captionTxt, FilterCriteria filterCriteria, List<ItemType> selectableTypes) {
		controller = new WorkspaceExplorerController(filterCriteria, WorkspaceExplorerConstants.AUTO);
		if(selectableTypes!=null){
			ItemType[] itemsType = new ItemType[selectableTypes.size()];
			itemsType = selectableTypes.toArray(itemsType);
			setSelectableTypes(selectableTypes.toArray(itemsType));
		}
		loadExplorer(captionTxt, null);
	}


	/**
	 * Instantiates a new workspace explorer select panel.
	 *
	 * @param folderId the folder id
	 * @param captionTxt the tool-tip of the panel
	 * @param filterCriteria the filter criteria
	 * @param selectableTypes the selectable types
	 */
	public WorkspaceExplorerSelectPanel(String folderId, String captionTxt, FilterCriteria filterCriteria, List<ItemType> selectableTypes, List<ItemType> showableTypes) {
		controller = new WorkspaceExplorerController(filterCriteria, WorkspaceExplorerConstants.AUTO);
		if(selectableTypes!=null){
			ItemType[] itemsType = new ItemType[selectableTypes.size()];
			itemsType = selectableTypes.toArray(itemsType);
			setSelectableTypes(selectableTypes.toArray(itemsType));
		}

		if(showableTypes!=null){
			ItemType[] itemsType = new ItemType[showableTypes.size()];
			itemsType = showableTypes.toArray(itemsType);
			setShowableTypes(showableTypes.toArray(itemsType));
		}

		loadExplorer(captionTxt, folderId);
	}

	/**
	 * Instantiates a new workspace explorer select panel.
	 *
	 * @param captionTxt the tool-tip of the panel
	 * @param showOnlyFolders the show only folders
	 */
	public WorkspaceExplorerSelectPanel(String captionTxt, boolean showOnlyFolders) {
		controller = new WorkspaceExplorerController(WorkspaceExplorerConstants.AUTO);
		if(showOnlyFolders){
			ItemType[] itemsType = new ItemType[4];
			itemsType[0] = ItemType.FOLDER;
			itemsType[1] = ItemType.PRIVATE_FOLDER;
			itemsType[2] = ItemType.SHARED_FOLDER;
			itemsType[3] = ItemType.VRE_FOLDER;

			setSelectableTypes(itemsType);
			setShowableTypes(itemsType);
		}

		loadExplorer(captionTxt, null);
		customizeEmptyMessage(showOnlyFolders);
	}

	/**
	 * Instantiates a new workspace explorer select panel.
	 *
	 * @param captionTxt the tool-tip of the panel
	 * @param showOnlyFolders the show only folders
	 * @param initFromFolderId the init from folder id
	 */
	public WorkspaceExplorerSelectPanel(String captionTxt, boolean showOnlyFolders, String baseFolderId) {
		controller = new WorkspaceExplorerController(WorkspaceExplorerConstants.AUTO);
		if(showOnlyFolders){
			ItemType[] itemsType = new ItemType[4];
			itemsType[0] = ItemType.FOLDER;
			itemsType[1] = ItemType.PRIVATE_FOLDER;
			itemsType[2] = ItemType.SHARED_FOLDER;
			itemsType[3] = ItemType.VRE_FOLDER;

			setSelectableTypes(itemsType);
			setShowableTypes(itemsType);
		}

		loadExplorer(captionTxt, baseFolderId);
		customizeEmptyMessage(showOnlyFolders);
	}

	private void customizeEmptyMessage(boolean showOnlyFolders){
		//AFTER THE INIT I'M CHANGING THE MESSAGE IN CASE OF ONLY FOLDER ARE SHWOWN
		if(showOnlyFolders)
			controller.getWsExplorer().getItTables().setEmptyTableMessage(WorkspaceExplorerConstants.NO_FOLDER);
	}

	/**
	 * Instantiates a new workspace explorer select panel.
	 *
	 * @param captionTxt the tool-tip of the panel
	 * @param selectableTypes the selectable types
	 * @param showableTypes the showable types
	 */
	public WorkspaceExplorerSelectPanel(String captionTxt, List<ItemType> selectableTypes, List<ItemType> showableTypes) {
		controller = new WorkspaceExplorerController(WorkspaceExplorerConstants.AUTO);
		if(selectableTypes!=null){
			ItemType[] itemsType = new ItemType[selectableTypes.size()];
			itemsType = selectableTypes.toArray(itemsType);
			setSelectableTypes(selectableTypes.toArray(itemsType));
		}

		if(showableTypes!=null){
			ItemType[] itemsType = new ItemType[showableTypes.size()];
			itemsType = showableTypes.toArray(itemsType);
			setShowableTypes(showableTypes.toArray(itemsType));
		}

		loadExplorer(captionTxt, null);
	}


	/**
	 * Bind events.
	 */
	private void bindEvents(){
		controller.getEventBus().addHandler(ClickItemEvent.TYPE, new ClickItemEventHandler() {

			@Override
			public void onClick(final ClickItemEvent clickItemEvent) {
				isSelect = false;
				Item item = controller.getWsExplorer().getItemSelected();

				//Return if item is not selectable
				if(!controller.itemIsSelectable(item)){
					notifyNotValidSelection();
					return;
				}

				notifySelectedItem(controller.getWsExplorer().getItemSelected());
				isSelect = true;
			}
		});

		controller.getEventBus().addHandler(BreadcrumbClickEvent.TYPE, new BreadcrumbClickEventHandler() {

			@Override
			public void onBreadcrumbClick(BreadcrumbClickEvent breadcrumbClickEvent) {
				isSelect = false;
				Item item = breadcrumbClickEvent.getTargetItem();
				GWT.log("BreadcrumbClickEvent: "+item);

				//Return if item is not selectable
				if(!controller.itemIsSelectable(item)){
					notifyNotValidSelection();
					return;
				}

				notifySelectedItem(item);
				isSelect = true;
			}
		});

		controller.getEventBus().addHandler(RootLoadedEvent.TYPE, new RootLoadedEventHandler() {

			@Override
			public void onRootLoaded(RootLoadedEvent rootLoadedEvent) {
				isSelect = false;
				if(rootLoadedEvent.getRoot()!=null){
					GWT.log("RootLoadedEvent: "+rootLoadedEvent.getRoot());

					if(!controller.itemIsSelectable(rootLoadedEvent.getRoot())){
						notifyNotValidSelection();
						return;
					}

					notifySelectedItem(rootLoadedEvent.getRoot());
					isSelect = true;
				}
			}
		});

		controller.getEventBus().addHandler(LoadMySpecialFolderEvent.TYPE, new LoadMySpecialFolderEventHandler() {

			@Override
			public void onLoadMySpecialFolder(LoadMySpecialFolderEvent loadMySpecialFolderEvent) {
				isSelect = false;
				GWT.log("LoadMySpecialFolder, notifies null");
				notifyNotValidSelection();
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.ScrollPanel#onResize()
	 */
	@Override
	public void onResize() {
		super.onResize();
		GWT.log("on Resize...");
		adjustSize();
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.Widget#onLoad()
	 */
	@Override
	protected void onLoad() {
		super.onLoad();
		GWT.log("on Load...");
//		adjustSize();
		adjustSize();
	}

	/**
	 * Adjust size.
	 */
	private void adjustSize(){
		if(this.getParent()!=null && this.getParent().getElement()!=null)
			Util.adjustSize(this.getParent().getElement(), controller.getWsExplorer().getPanel(), 85);
	}



	/**
	 * Load explorer.
	 * If folder id is null or empty the explorer loads the root element
	 * Otherwise, it loads the folder id
	 *
	 * @param captionTxt the caption txt
	 * @param folderId the folder id
	 */
	private void loadExplorer(String captionTxt, String folderId){

		if(folderId==null || folderId.isEmpty())
			controller.getEventBus().fireEvent(new LoadRootEvent());
		else{
			Item item = new Item(folderId, "", true);
			controller.getEventBus().fireEvent(new LoadFolderEvent<Item>(item));
		}
		bindEvents();
		initPanel(captionTxt);
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

		setWidth(WorkspaceExplorerConstants.WIDHT_DIALOG+"px");
		add(controller.getWorkspaceExplorerPanel());
		setAlertConfirm("", false, null);
		mainVP.add(controller.getWorkspaceExplorerPanel());
		mainVP.add(footerHP);
		add(mainVP);
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
	 * Set which items are selectable.
	 * @param selectableTypes the selectableTypes to set
	 */
	private void setSelectableTypes(ItemType ... selectableTypes) {
		ArrayList<ItemType> list = new ArrayList<>();
		for (int i = 0; i < selectableTypes.length; i++) {
			list.add(selectableTypes[i]);
		}
		GWT.log("setSelectableTypes for type " + list.toString());
		controller.setSelectableTypes(getPatchedTypes(selectableTypes));
	}

	private ItemType[] getPatchedTypes(ItemType[] selectableTypes) {
		boolean ANY_FOLDER = false;
		boolean ANY_FILE = false;

		ArrayList<ItemType> list = new ArrayList<>();
		for (int i = 0; i < selectableTypes.length; i++) {
			list.add(selectableTypes[i]);
		}

		for (int i = 0; i < selectableTypes.length; i++) {
			if (selectableTypes[i] == ItemType.FOLDER)
				ANY_FOLDER = true;
			if (selectableTypes[i] == ItemType.EXTERNAL_FILE) {
				ANY_FILE = true;
			}
		}

		for (int i = 0; i < selectableTypes.length; i++) {
			list.add(selectableTypes[i]);
		}
		if (ANY_FOLDER) {
			list.add(ItemType.PRIVATE_FOLDER);
			list.add(ItemType.VRE_FOLDER);
			list.add(ItemType.SHARED_FOLDER);
		}
		if (ANY_FILE) {
			ArrayList<ItemType> temp = new ArrayList<>();
			ItemType[] allValues = ItemType.values();
			for (int i = 0; i < allValues.length; i++) {
				temp.add(allValues[i]);
			}
			temp.remove(ItemType.PRIVATE_FOLDER);
			temp.remove(ItemType.VRE_FOLDER);
			temp.remove(ItemType.SHARED_FOLDER);
			temp.remove(ItemType.FOLDER);
			for (ItemType itemType : temp) {
				list.add(itemType);
			}
		}
		GWT.log("getPatchedTypes returns " + list.toString());
		return list.toArray(new ItemType[list.size()]);
	}


	/**
	 * Gets the selectable types.
	 *
	 * @return the selectable types
	 */
	public List<ItemType> getSelectableTypes() {
		return controller.getSelectableTypes();
	}

	/**
	 * Return the showable items.
	 * @return the showableTypes
	 */
	public List<ItemType> getShowableTypes() {
		return controller.getShowableTypes();
	}

	/**
	 * Set the showable items. The folders items are show as default.
	 * @param showableTypes the showableTypes to set
	 */
	private void setShowableTypes(ItemType ... showableTypes) {
		ArrayList<ItemType> list = new ArrayList<>();
		for (int i = 0; i < showableTypes.length; i++) {
			list.add(showableTypes[i]);
		}
		GWT.log("setShowableTypes for type " + list.toString());
		controller.setShowableTypes(getPatchedTypes(showableTypes));
	}
}
