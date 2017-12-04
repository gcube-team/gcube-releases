/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.client.save;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.wsexplorer.client.Util;
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
import com.github.gwtbootstrap.client.ui.base.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * The Class WorkspaceExplorerSavePanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 7, 2015
 */
public class WorkspaceExplorerSavePanel extends ScrollPanel implements HasWorskpaceExplorerSaveNotificationListener {

	private Button saveButton;
	@SuppressWarnings("unused")
	private boolean isSave = false;
	private WorkspaceExplorerController controller;
	private List<WorskpaceExplorerSaveNotificationListener> listeners = new ArrayList<WorskpaceExplorerSaveNotificationListener>();
	private TextBox fileNamePanelTextBox = new TextBox();
	private VerticalPanel mainVP = new VerticalPanel();
	private HorizontalPanel footerPanelHP = new HorizontalPanel();


	/**
	 * Instantiates a new workspace explorer save panel.
	 *
	 * @param fileName the file name
	 */
	public WorkspaceExplorerSavePanel(String fileName) {
		controller = new WorkspaceExplorerController(WorkspaceExplorerConstants.AUTO);
		loadExplorer(fileName);
	}

	/**
	 * Instantiates a new workspace explorer save panel.
	 *
	 * @param fileName the file name
	 * @param filterCriteria the filter criteria
	 */
	public WorkspaceExplorerSavePanel(String fileName, FilterCriteria filterCriteria) {
		controller = new WorkspaceExplorerController(filterCriteria, WorkspaceExplorerConstants.AUTO);
		loadExplorer(fileName);
	}

	/**
	 * Instantiates a new workspace explorer save panel.
	 *
	 * @param fileName the file name
	 * @param showOnlyFolders the show only folders
	 */
	public WorkspaceExplorerSavePanel(String fileName, boolean showOnlyFolders) {
		controller = new WorkspaceExplorerController(WorkspaceExplorerConstants.AUTO);
		if (showOnlyFolders) {
			ItemType[] itemsType = new ItemType[1];
			itemsType[0] = ItemType.FOLDER;
			setSelectableTypes(itemsType);
			setShowableTypes(itemsType);
		}

		loadExplorer(fileName);
	}

	/**
	 * Instantiates a new workspace explorer save panel.
	 *
	 * @param fileName the file name
	 * @param showableTypes the showable types
	 */
	public WorkspaceExplorerSavePanel(String fileName, List<ItemType> showableTypes) {
		controller = new WorkspaceExplorerController(WorkspaceExplorerConstants.AUTO);
		if (showableTypes != null) {
			ItemType[] itemsType = new ItemType[showableTypes.size()];
			itemsType = showableTypes.toArray(itemsType);
			setShowableTypes(showableTypes.toArray(itemsType));
		}

		loadExplorer(fileName);
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

	private void adjustSize(){
		if(this.getParent()!=null && this.getParent().getElement()!=null)
			Util.adjustSize(this.getParent().getElement(), controller.getWsExplorer().getPanel(), 120);
	}

	/**
	 * Load explorer.
	 *
	 * @param fileName the file name
	 */
	private void loadExplorer(String fileName){
		controller.getEventBus().fireEvent(new LoadRootEvent());
		initPanel(fileName);
	}

	/**
	 * Inits the dialog.
	 *
	 * @param fileName the file name
	 */
	private void initPanel(String fileName) {
		saveButton = new Button(WorkspaceExplorerConstants.SAVE);
		saveButton.setType(ButtonType.PRIMARY);
		setWidth(WorkspaceExplorerConstants.WIDHT_DIALOG+"px");
		addStyleName("savePanel");

		saveButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				isSave = false;

				Item item = controller.getWsExplorer().getItemSelected();

				if (item != null && item.isFolder()) {

					//VALIDATING FILE NAME
					String fileName = getFileName();
					if(fileName==null || fileName.isEmpty()){
						Window.alert("You must insert a valid file name!! It cannot be empty!!");
						fileNamePanelTextBox.setFocus(true);
						return;
					}

					notifySaving(item, getFileName());

						//GO INTO FOLDER
//					controller.getEventBus().fireEvent(new org.gcube.portlets.widgets.wsexplorer.client.event.LoadFolderEvent(item));
				} else {
					//VALIDATING FILE NAME
					String fileName = getFileName();
					if(fileName==null || fileName.isEmpty()){
						Window.alert("You must insert a valid file name!! It cannot be empty!!");
						fileNamePanelTextBox.setFocus(true);
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
						isSave = true;
					}else
						Window.alert("Parent item is null!!");
				}
			}
		});

		fileNamePanelTextBox.addStyleName("fileNamePanelTextBox");
		fileNamePanelTextBox.setAlignment(TextAlignment.LEFT);
		setFileName(fileName);

		HTML name = new HTML("Name:");
		name.addStyleName("htmlSaveName");
		name.getElement().getStyle().setPaddingTop(7.0, Unit.PX);
		footerPanelHP.add(name);
		footerPanelHP.add(fileNamePanelTextBox);
		footerPanelHP.add(saveButton);
		footerPanelHP.addStyleName("footerPanelHP");

		mainVP.add(controller.getWorkspaceExplorerPanel());
		mainVP.add(footerPanelHP);

		add(mainVP);
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

		 Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand () {
		        public void execute () {
		        	fileNamePanelTextBox.selectAll();
		        	fileNamePanelTextBox.setFocus(true);
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

		fileNamePanelTextBox.setValue(txt);
	}


	/**
	 * Gets the file name.
	 *
	 * @return the file name
	 */
	private String getFileName(){
		return fileNamePanelTextBox.getValue();
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

	/**
	 * Gets the save button.
	 *
	 * @return the saveButton
	 */
	public Button getSaveButton() {
		return saveButton;
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
}