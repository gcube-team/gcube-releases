/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.client.select;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.wsexplorer.client.WorkspaceExplorerConstants;
import org.gcube.portlets.widgets.wsexplorer.client.WorkspaceExplorerController;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadFolderEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadRootEvent;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.HasWorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.shared.FilterCriteria;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.ModalFooter;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.event.HideEvent;
import com.github.gwtbootstrap.client.ui.event.HideHandler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;



/**
 * The Class WorkspaceExplorerSelectDialog.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 21, 2016
 */
public class WorkspaceExplorerSelectDialog extends Modal implements HasWorskpaceExplorerSelectNotificationListener{

	private Button selectButton;
	private WorkspaceExplorerSelectDialog INSTANCE = this;
	private boolean isSelect = false;
	private String captionTxt;
	private ModalFooter footer = new ModalFooter();
	private Alert alertConfirm;
	private WorkspaceExplorerController controller;
	private List<WorskpaceExplorerSelectNotificationListener> listeners = new ArrayList<WorskpaceExplorerSelectNotificationListener>();
	private int zIndex = -1;


	/**
	 * Instantiates a new workspace explorer select dialog.
	 * You can implement {@link WorskpaceExplorerSelectNotificationListener} to receive events
	 * @param captionTxt the dialog caption
	 *
	 */
	public WorkspaceExplorerSelectDialog(String captionTxt) {
		controller = new WorkspaceExplorerController(WorkspaceExplorerConstants.HEIGHT_EXPLORER_PANEL);
		loadExplorer(captionTxt, null);
	}

	/**
	 * Instantiates a new workspace explorer select dialog.
	 * You can implement {@link WorskpaceExplorerSelectNotificationListener} to receive events
	 * @param captionTxt the dialog caption
	 * @param filterCriteria the filter criteria
	 */
	public WorkspaceExplorerSelectDialog(String captionTxt, FilterCriteria filterCriteria) {
		controller = new WorkspaceExplorerController(filterCriteria, WorkspaceExplorerConstants.HEIGHT_EXPLORER_PANEL);
		loadExplorer(captionTxt, null);
	}

	/**
	 * Instantiates a new workspace explorer select dialog.
	 * You can implement {@link WorskpaceExplorerSelectNotificationListener} to receive events
	 *
	 * @param captionTxt the caption txt
	 * @param filterCriteria the filter criteria
	 * @param selectableTypes the selectable types
	 */
	public WorkspaceExplorerSelectDialog(String captionTxt, FilterCriteria filterCriteria, List<ItemType> selectableTypes) {
		controller = new WorkspaceExplorerController(filterCriteria, WorkspaceExplorerConstants.HEIGHT_EXPLORER_PANEL);
		if(selectableTypes!=null){
			ItemType[] itemsType = new ItemType[selectableTypes.size()];
			itemsType = selectableTypes.toArray(itemsType);
			setSelectableTypes(selectableTypes.toArray(itemsType));
		}
		loadExplorer(captionTxt, null);
	}



	/**
	 * Instantiates a new workspace explorer select dialog.
	 * You can implement {@link WorskpaceExplorerSelectNotificationListener} to receive events
	 *
	 * @param folderId the folder id
	 * @param captionTxt the dialog caption
	 * @param filterCriteria the filter criteria
	 * @param selectableTypes the selectable types
	 * @param showableTypes the showable types
	 */
	public WorkspaceExplorerSelectDialog(String folderId, String captionTxt, FilterCriteria filterCriteria, List<ItemType> selectableTypes, List<ItemType> showableTypes) {
		controller = new WorkspaceExplorerController(filterCriteria, WorkspaceExplorerConstants.HEIGHT_EXPLORER_PANEL);
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
	 * Instantiates a new workspace explorer select dialog.
	 *
	 * @param captionTxt the caption txt
	 * @param showOnlyFolders the show only folders
	 */
	public WorkspaceExplorerSelectDialog(String captionTxt, boolean showOnlyFolders) {
		controller = new WorkspaceExplorerController(WorkspaceExplorerConstants.HEIGHT_EXPLORER_PANEL);
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
	}

	/**
	 * Instantiates a new workspace explorer select dialog.
	 *
	 * @param captionTxt captionTxt sets the text inside the caption, if null sets "Workspace Explorer"
	 * @param selectableTypes the selectable types
	 * @param showableTypes the showable types
	 *
	 * You can implement {@link WorskpaceExplorerSelectNotificationListener} to receive events
	 */
	public WorkspaceExplorerSelectDialog(String captionTxt, List<ItemType> selectableTypes, List<ItemType> showableTypes) {
		controller = new WorkspaceExplorerController(WorkspaceExplorerConstants.HEIGHT_EXPLORER_PANEL);
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
		initDialog(captionTxt);
	}

	/**
	 * Inits the dialog.
	 *
	 * @param captionTxt the caption txt
	 */
	private void initDialog(String captionTxt) {
		this.captionTxt = captionTxt==null || captionTxt.isEmpty()?WorkspaceExplorerConstants.WORKSPACE_EXPLORER_CAPTION:captionTxt;
		setAnimation(false);
		setCloseVisible(true);
		hide(false);
		setTitle(this.captionTxt);
		selectButton = new Button(WorkspaceExplorerConstants.SELECT);
		selectButton.setType(ButtonType.PRIMARY);
		setWidth(WorkspaceExplorerConstants.WIDHT_DIALOG);
		setMaxHeigth(WorkspaceExplorerConstants.MAX_HEIGHT_DIALOG);

		addHideHandler(new HideHandler() {

			@Override
			public void onHide(HideEvent hideEvent) {
				if(!isSelect)
					notifyAborted();
			}
		});

		add(controller.getWorkspaceExplorerPanel());
		selectButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				isSelect = false;
				Item item = controller.getWsExplorer().getItemSelected();
				boolean isSelectable = controller.itemIsSelectable(item);
				GWT.log("Item is selectable? "+isSelectable);
				if(item!=null && isSelectable){
					isSelect = true;
					INSTANCE.hide();
					notifySelectedItem(item);
				}else if(item==null){ //IGNORING ITEM SELECT, CAN USE BREADCRUMBS?
					Item itemB = controller.getBreadcrumbs().getLastParent();
					if(itemB!=null){
						isSelectable = controller.itemIsSelectable(itemB); //BREADCRUMB IS SELECTABLE?
						if(isSelectable)
							setAlertConfirm("Selecting \""+itemB.getName()+"\", confirm?", true, itemB);
					}
				}
			}
		});

		setAlertConfirm("", false, null);
		footer.add(selectButton);
		add(footer);
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
			footer.remove(alertConfirm);
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
					INSTANCE.hide();
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
		footer.insert(alertConfirm,0);
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
	 * @see com.github.gwtbootstrap.client.ui.Modal#show()
	 */
	@Override
	public void show() {
		super.show();
		isSelect = false;
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
		controller.setSelectableTypes(selectableTypes);
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
		controller.setShowableTypes(showableTypes);
	}

	/**
	 * Sets the z-index.
	 *
	 * @param zIndex the new z index
	 */
	public void setZIndex(int zIndex){
		this.zIndex = zIndex;
		configureZindex();
	}


	/**
	 * Configure zindex.
	 */
	private void configureZindex() {

		zIndex = zIndex > WorkspaceExplorerConstants.STATIC_BOOTSTRAP_ZINDEX_MODAL_VALUE?zIndex:WorkspaceExplorerConstants.STATIC_BOOTSTRAP_ZINDEX_MODAL_VALUE+20;

		Element el = null;
		try{
			el = getElement();
		}catch (Exception e) {
			//silent
			return;
		}
		el.getStyle().setZIndex(zIndex+20);

		//IS MODAL-BACKDROP
		if(el.getNextSiblingElement()!=null)
			el.getNextSiblingElement().getStyle().setZIndex(zIndex+10);
	}


	/* (non-Javadoc)
	 * @see com.github.gwtbootstrap.client.ui.Modal#onShown(com.google.gwt.user.client.Event)
	 */
	@Override
	protected void onShown(Event e) {
		super.onShown(e);
		GWT.log("Shown fired");
		configureZindex();
	}
}
