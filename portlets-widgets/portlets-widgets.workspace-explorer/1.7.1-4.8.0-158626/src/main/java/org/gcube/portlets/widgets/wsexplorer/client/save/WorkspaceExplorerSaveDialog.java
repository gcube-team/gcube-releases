package org.gcube.portlets.widgets.wsexplorer.client.save;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.wsexplorer.client.WorkspaceExplorerConstants;
import org.gcube.portlets.widgets.wsexplorer.client.WorkspaceExplorerController;
import org.gcube.portlets.widgets.wsexplorer.client.event.ClickItemEvent;
import org.gcube.portlets.widgets.wsexplorer.client.event.ClickItemEventHandler;
import org.gcube.portlets.widgets.wsexplorer.client.event.LoadRootEvent;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSaveNotification.HasWorskpaceExplorerSaveNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSaveNotification.WorskpaceExplorerSaveNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.shared.FilterCriteria;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.ModalFooter;
import com.github.gwtbootstrap.client.ui.base.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.event.HideEvent;
import com.github.gwtbootstrap.client.ui.event.HideHandler;
import com.github.gwtbootstrap.client.ui.event.ShownEvent;
import com.github.gwtbootstrap.client.ui.event.ShownHandler;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;


/**
 * The Class WorkspaceExplorerSaveDialog.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 7, 2015
 */
public class WorkspaceExplorerSaveDialog extends Modal implements HasWorskpaceExplorerSaveNotificationListener {

	private Button saveButton;
	private boolean isSave = false;
	private String captionTxt;
	private ModalFooter footer = new ModalFooter();
	private WorkspaceExplorerSaveDialog INSTANCE = this;
	private WorkspaceExplorerController controller;
	private List<WorskpaceExplorerSaveNotificationListener> listeners = new ArrayList<WorskpaceExplorerSaveNotificationListener>();
	private TextBox fileNameTextBox = new TextBox();
	private int zIndex = -1;


	/**
	 * Instantiates a new workspace explorer save dialog.
	 *
	 * @param captionTxt the caption txt
	 * @param fileName the file name
	 */
	public WorkspaceExplorerSaveDialog(String captionTxt, String fileName) {
		controller = new WorkspaceExplorerController(WorkspaceExplorerConstants.HEIGHT_EXPLORER_PANEL);
		loadExplorer(captionTxt, fileName);
	}

	/**
	 * Instantiates a new workspace explorer save dialog.
	 *
	 * @param captionTxt the caption txt
	 * @param fileName the file name
	 * @param filterCriteria the filter criteria to filter for Mime Types, File Extensions, Properties
	 */
	public WorkspaceExplorerSaveDialog(String captionTxt, String fileName, FilterCriteria filterCriteria) {
		controller = new WorkspaceExplorerController(filterCriteria, WorkspaceExplorerConstants.HEIGHT_EXPLORER_PANEL);
		loadExplorer(captionTxt, fileName);
	}

	/**
	 * Instantiates a new workspace explorer save dialog.
	 *
	 * @param captionTxt the caption txt
	 * @param fileName the file name
	 * @param showOnlyFolders the show only folders
	 */
	public WorkspaceExplorerSaveDialog(String captionTxt, String fileName, boolean showOnlyFolders) {
		controller = new WorkspaceExplorerController(WorkspaceExplorerConstants.HEIGHT_EXPLORER_PANEL);
		if (showOnlyFolders) {
			ItemType[] itemsType = new ItemType[1];
			itemsType[0] = ItemType.FOLDER;
			setSelectableTypes(itemsType);
			setShowableTypes(itemsType);
		}
		loadExplorer(captionTxt, fileName);
	}

	/**
	 * Instantiates a new workspace explorer save dialog.
	 *
	 * @param fileName the file name
	 * @param showOnlyFolders the show only folders
	 */
	public WorkspaceExplorerSaveDialog(String fileName, boolean showOnlyFolders) {
		controller = new WorkspaceExplorerController(WorkspaceExplorerConstants.HEIGHT_EXPLORER_PANEL);
		if (showOnlyFolders) {
			ItemType[] itemsType = new ItemType[1];
			itemsType[0] = ItemType.FOLDER;
			setSelectableTypes(itemsType);
			setShowableTypes(itemsType);
		}

		loadExplorer(WorkspaceExplorerConstants.WORKSPACE_EXPLORER_SAVE_AS_CAPTION, fileName);
	}


	/**
	 * Instantiates a new workspace explorer save dialog.
	 *
	 * @param captionTxt the caption txt
	 * @param fileName the file name
	 * @param showableTypes the showable types
	 */
	public WorkspaceExplorerSaveDialog(String captionTxt, String fileName, List<ItemType> showableTypes) {
		controller = new WorkspaceExplorerController(WorkspaceExplorerConstants.HEIGHT_EXPLORER_PANEL);
		if (showableTypes != null) {
			ItemType[] itemsType = new ItemType[showableTypes.size()];
			itemsType = showableTypes.toArray(itemsType);
			setShowableTypes(showableTypes.toArray(itemsType));
		}

		loadExplorer(captionTxt, fileName);
	}


	/**
	 * Instantiates a new workspace explorer save dialog.
	 *
	 * @param fileName the file name
	 * @param showableTypes the showable types
	 */
	public WorkspaceExplorerSaveDialog(String fileName, List<ItemType> showableTypes) {
		controller = new WorkspaceExplorerController(WorkspaceExplorerConstants.HEIGHT_EXPLORER_PANEL);
		if (showableTypes != null) {
			ItemType[] itemsType = new ItemType[showableTypes.size()];
			itemsType = showableTypes.toArray(itemsType);
			setShowableTypes(showableTypes.toArray(itemsType));
		}

		loadExplorer(WorkspaceExplorerConstants.WORKSPACE_EXPLORER_SAVE_AS_CAPTION, fileName);
	}


	/**
	 * Load explorer.
	 *
	 * @param captionTxt the caption txt
	 * @param fileName the file name
	 */
	private void loadExplorer(String captionTxt, String fileName){
		controller.getEventBus().fireEvent(new LoadRootEvent());
		initDialog(captionTxt, fileName);
	}


	/**
	 * Inits the dialog.
	 *
	 * @param captionTxt the caption txt
	 * @param fileName the file name
	 */
	private void initDialog(String captionTxt, String fileName) {
		this.captionTxt = captionTxt == null || captionTxt.isEmpty() ? WorkspaceExplorerConstants.WORKSPACE_EXPLORER_SAVE_AS_CAPTION: captionTxt;
		setAnimation(false);
		setCloseVisible(true);
		hide(false);
		setTitle(this.captionTxt);
		saveButton = new Button(WorkspaceExplorerConstants.SAVE);
		saveButton.setType(ButtonType.PRIMARY);
		setWidth(WorkspaceExplorerConstants.WIDHT_DIALOG);
		setMaxHeigth(WorkspaceExplorerConstants.MAX_HEIGHT_DIALOG);

		addHideHandler(new HideHandler() {

			@Override
			public void onHide(HideEvent hideEvent) {
				if (!isSave)
					notifyAborted();
			}
		});

		add(controller.getWorkspaceExplorerPanel());
		saveButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				isSave = false;

				Item item = controller.getWsExplorer().getItemSelected();

				//A FOLDER IS SELECTED
				if (item != null && item.isFolder()) {
					GWT.log("folder selected: "+item.getName());
						//GO INTO FOLDER
//					controller.getEventBus().fireEvent(new org.gcube.portlets.widgets.wsexplorer.client.event.LoadFolderEvent(item));
					//VALIDATING FILE NAME
					String fileName = getFileName();
					if(fileName==null || fileName.isEmpty()){
						Window.alert("You must insert a valid file name!! It cannot be empty!!");
						fileNameTextBox.setFocus(true);
						return;
					}

					notifySaving(item, getFileName());
					INSTANCE.hide();

				} else {
					//VALIDATING FILE NAME
					String fileName = getFileName();
					if(fileName==null || fileName.isEmpty()){
						Window.alert("You must insert a valid file name!! It cannot be empty!!");
						fileNameTextBox.setFocus(true);
						return;
					}

					Item itemB = controller.getBreadcrumbs().getLastParent();
					GWT.log("last parent: "+itemB);
					if (itemB != null) {
						if(itemB.isSpecialFolder()){
							Window.alert("Destination folder "+WorkspaceExplorerConstants.WORKSPACE_MY_SPECIAL_FOLDERS_PATH+" is not valid!");
							return;
						}
						notifySaving(itemB, getFileName());
						INSTANCE.hide();
						isSave = true;
					}else
						Window.alert("Parent item is null!!");
				}
			}
		});

		fileNameTextBox.addStyleName("fileNameTextBox");
		fileNameTextBox.setAlignment(TextAlignment.LEFT);
		setFileName(fileName);
		HTML name = new HTML("Name:");
		name.addStyleName("htmlSaveName");
		footer.add(name);
		footer.add(fileNameTextBox);
		footer.add(saveButton);
		add(footer);

		addHandlers();
	}

	/**
	 * Adds the handlers.
	 */
	private void addHandlers() {

		controller.getEventBus().addHandler(ClickItemEvent.TYPE, new ClickItemEventHandler() {

			@Override
			public <T> void onClick(final ClickItemEvent<T> clickItemEvent) {

				if(clickItemEvent.getItem()!=null){
					if (clickItemEvent.getItem() instanceof Item) {
						Item item = (Item) clickItemEvent.getItem();
						if(item!=null && !item.isFolder())
							setFileName("New_"+item.getName());
					}
				}
			}
		});


		addShownHandler(new ShownHandler() {

			@Override
			public void onShown(ShownEvent shownEvent) {
				fileNameTextBox.selectAll();
	        	fileNameTextBox.setFocus(true);
			}
		});

		 Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand () {
		        public void execute () {
		        	fileNameTextBox.selectAll();
		        	fileNameTextBox.setFocus(true);
		     }
		 });

	}

	/**
	 * Sets the file name.
	 *
	 * @param txt the txt
	 */
	private void setFileName(final String txt){
		if(txt==null)
			return;

		fileNameTextBox.setValue(txt);
	}


	/**
	 * Gets the file name.
	 *
	 * @return the file name
	 */
	private String getFileName(){
		return fileNameTextBox.getValue();
	}

	/**
	 * Notify parent selected.
	 *
	 * @param parent the parent
	 * @param fileName the file name
	 */
	private void notifySaving(Item parent, String fileName) {

		for (WorskpaceExplorerSaveNotificationListener worskpaceExplorerNotificationListener : listeners) {
			worskpaceExplorerNotificationListener.onSaving(parent, fileName);
		}
	}

	/**
	 * Notify aborted.
	 */
	private void notifyAborted() {

		for (WorskpaceExplorerSaveNotificationListener worskpaceExplorerNotificationListener : listeners) {
			worskpaceExplorerNotificationListener.onAborted();
		}
	}

	/**
	 * Notify failed.
	 *
	 * @param t
	 *            the t
	 */
	@SuppressWarnings("unused")
	private void notifyFailed(Throwable t) {

		for (WorskpaceExplorerSaveNotificationListener worskpaceExplorerNotificationListener : listeners) {
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
		return isSave;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.github.gwtbootstrap.client.ui.Modal#show()
	 */
	@Override
	public void show() {
		super.show();
		isSave = false;
	}

	/**
	 * Gets the save button.
	 *
	 * @return the saveButton
	 */
	public Button getSaveButton() {
		return saveButton;
	}

	/**
	 * Set which items are selectable.
	 *
	 * @param selectableTypes
	 *            the selectableTypes to set
	 */
	private void setSelectableTypes(ItemType... selectableTypes) {
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
	 *
	 * @return the showableTypes
	 */
	public List<ItemType> getShowableTypes() {
		return controller.getShowableTypes();
	}

	/**
	 * Set the showable items. The folders items are show as default.
	 *
	 * @param showableTypes
	 *            the showableTypes to set
	 */
	private void setShowableTypes(ItemType... showableTypes) {
		controller.setShowableTypes(showableTypes);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.notification.WorskpaceExplorerSaveNotification.HasWorskpaceExplorerSaveNotificationListener#addWorkspaceExplorerSaveNotificationListener(org.gcube.portlets.widgets.wsexplorer.client.notification.WorskpaceExplorerSaveNotification.WorskpaceExplorerSaveNotificationListener)
	 */
	@Override
	public void addWorkspaceExplorerSaveNotificationListener(WorskpaceExplorerSaveNotificationListener handler) {
		if(handler!=null)
			listeners.add(handler);

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsexplorer.client.notification.WorskpaceExplorerSaveNotification.HasWorskpaceExplorerSaveNotificationListener#removeWorkspaceExplorerSaveNotificationListener(org.gcube.portlets.widgets.wsexplorer.client.notification.WorskpaceExplorerSaveNotification.WorskpaceExplorerSaveNotificationListener)
	 */
	@Override
	public void removeWorkspaceExplorerSaveNotificationListener(WorskpaceExplorerSaveNotificationListener handler) {
		if(handler!=null){
			if(listeners.contains(handler))
				listeners.remove(handler);
		}

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

		zIndex = zIndex > WorkspaceExplorerConstants.STATIC_BOOTSTRAP_ZINDEX_MODAL_VALUE?zIndex:WorkspaceExplorerConstants.STATIC_BOOTSTRAP_ZINDEX_MODAL_VALUE+50;

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
