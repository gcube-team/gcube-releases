/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.client.save;

import java.util.List;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.widgets.lighttree.client.Item;
import org.gcube.portlets.widgets.lighttree.client.ItemType;
import org.gcube.portlets.widgets.lighttree.client.Util;
import org.gcube.portlets.widgets.lighttree.client.event.DataLoadEvent;
import org.gcube.portlets.widgets.lighttree.client.event.DataLoadHandler;
import org.gcube.portlets.widgets.lighttree.client.event.HasDataLoadHandlers;
import org.gcube.portlets.widgets.lighttree.client.event.HasPopupHandlers;
import org.gcube.portlets.widgets.lighttree.client.event.ItemSelectionEvent;
import org.gcube.portlets.widgets.lighttree.client.event.ItemSelectionHandler;
import org.gcube.portlets.widgets.lighttree.client.event.NameChangeEvent;
import org.gcube.portlets.widgets.lighttree.client.event.NameChangeHandler;
import org.gcube.portlets.widgets.lighttree.client.event.PopupEvent;
import org.gcube.portlets.widgets.lighttree.client.event.PopupHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class WorkspaceLightTreeSavePopup extends GCubeDialog implements DataLoadHandler, ItemSelectionHandler, NameChangeHandler, HasDataLoadHandlers, HasPopupHandlers {

	protected Button saveButton;
	protected boolean showNameField;

	protected String title;

	protected Item selectedItem;
	protected String name;

	protected WorkspaceLightTreeSavePanel savePanel;

	public WorkspaceLightTreeSavePopup(final String title, boolean showNameField)
	{
		this(title, showNameField,"");
	}

	/**
	 * Create a new load popup.
	 * @param title the popup title.
	 * @param closeOnSelect close the popup when a selectable item is selected.
	 * @param showButtons show the open and cancel button.
	 */
	public WorkspaceLightTreeSavePopup(final String title, boolean showNameField, String nameValue)
	{
		super();

		this.showNameField = showNameField;
		this.title = title;

		setText("loading...");

		VerticalPanel panelContents = new VerticalPanel();
		panelContents.setSpacing(4);

		savePanel = new WorkspaceLightTreeSavePanel(showNameField, nameValue);
		savePanel.addDataLoadHandler(this);
		savePanel.addNameChangeHandler(this);
		savePanel.addSelectionHandler(this);
		panelContents.add(savePanel);

		HorizontalPanel hp = new HorizontalPanel();

		saveButton = new Button("Save", new ClickHandler() {

			public void onClick(ClickEvent event) {
				hide();
				PopupEvent.fireItemSelected(WorkspaceLightTreeSavePopup.this, selectedItem, name);
			}
		});

		saveButton.setEnabled(false);

		hp.add(saveButton);

		Button cancelButton = new Button("Cancel", new ClickHandler() {

			public void onClick(ClickEvent event) {
				hide();
				PopupEvent.fireCanceled(WorkspaceLightTreeSavePopup.this);
			}
		});
		hp.add(cancelButton);

		panelContents.add(hp);
		if (LocaleInfo.getCurrentLocale().isRTL()) { 
			panelContents.setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_LEFT);
		} else {
			panelContents.setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_RIGHT);
		}

		setWidget(panelContents);
	}

	/**
	 * @return the expandRootChildren
	 */
	public boolean isExpandRootChildren() {
		return savePanel.isExpandRootChildren();
	}

	/**
	 * Set to expand the root children when data is loaded.
	 * @param expandRootChildren the expandRootChildren to set
	 */
	public void setExpandRootChildren(boolean expandRootChildren) {
		savePanel.setExpandRootChildren(expandRootChildren);
	}

	/**
	 * @return the expandAllItems
	 */
	public boolean isExpandAllItems() {
		return savePanel.isExpandAllItems();
	}

	/**
	 * Set to expand the entire tree when data is loaded.
	 * @param expandAllItems the expandAllItems to set
	 */
	public void setExpandAllItems(boolean expandAllItems) {
		savePanel.setExpandAllItems(expandAllItems);
	}

	/**
	 * Return which items are selectable.
	 * @return the selectableTypes
	 */
	public List<ItemType> getSelectableTypes() {
		return savePanel.getSelectableTypes();
	}

	/**
	 * Set which items are selectable.
	 * @param selectableTypes the selectableTypes to set
	 */
	public void setSelectableTypes(ItemType ... selectableTypes) {
		savePanel.setSelectableTypes(selectableTypes);
	}

	/**
	 * Return the showable items.
	 * @return the showableTypes
	 */
	public List<ItemType> getShowableTypes() {
		return savePanel.getShowableTypes();
	}

	/**
	 * Set the showable items. The folders items are show as default.
	 * @param showableTypes the showableTypes to set
	 */
	public void setShowableTypes(ItemType ... showableTypes) {
		savePanel.setShowableTypes(showableTypes);
	}

	/**
	 * @return the showEmptyFolders
	 */
	public boolean isShowEmptyFolders() {
		return savePanel.isShowEmptyFolders();
	}

	/**
	 * Set if show empty folders. Default is true.
	 * @param showEmptyFolders the showEmptyFolders to set
	 */
	public void setShowEmptyFolders(boolean showEmptyFolders) {
		savePanel.setShowEmptyFolders(showEmptyFolders);
	}

	/**
	 * @return
	 * @see org.gcube.portlets.widgets.lighttree.client.save.WorkspaceLightTreeSavePanel#isAcceptSiblingName()
	 */
	public boolean isAcceptSiblingName() {
		return savePanel.isAcceptSiblingName();
	}

	/**
	 * @param acceptSiblingName
	 * @see org.gcube.portlets.widgets.lighttree.client.save.WorkspaceLightTreeSavePanel#setAcceptSiblingName(boolean)
	 */
	public void setAcceptSiblingName(boolean acceptSiblingName) {
		savePanel.setAcceptSiblingName(acceptSiblingName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void show() {
		super.show();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				savePanel.loadData();
			}
		});
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void onDataLoad(DataLoadEvent event) {
		GWT.log("SavePopup onDataLoad "+event);
		
		String newTile = event.isFailed()?("Error "+event.getCaught().getMessage()):title;
		setText(Util.ellipsis(newTile, 45, false));
	
		//we propagate the event
		fireEvent(event);
	}

	/**
	 * {@inheritDoc}
	 */
	public void onSelection(ItemSelectionEvent event) {
		//GWT.log("itemSelected event: "+event);
		selectedItem = event.isSelectable()?event.getSelectedItem():null;
		checkSaveButton();
	}

	/**
	 * {@inheritDoc}
	 */
	public void onNameChange(NameChangeEvent event) {
		this.name = event.isCorrectName()?event.getName():null;
		checkSaveButton();
	}
	
	protected void checkSaveButton()
	{
		//we enable the save button only if a selectable item and a valid name have been selected
		boolean enable = (selectedItem!=null) && (!showNameField || (name!=null));//(selectedItem!=null) && (name!=null);
		
		GWT.log("checkSaveButton enable: "+enable+" selectedItem: "+selectedItem+" name: "+name);
		saveButton.setEnabled(enable);
	}

	/**
	 * {@inheritDoc}
	 */
	public HandlerRegistration addDataLoadHandler(DataLoadHandler handler) {
		return addHandler(handler, DataLoadEvent.getType());
	}

	/**
	 * {@inheritDoc}
	 */
	public HandlerRegistration addPopupHandler(PopupHandler handler) {
		return addHandler(handler, PopupEvent.getType());
	}

}
