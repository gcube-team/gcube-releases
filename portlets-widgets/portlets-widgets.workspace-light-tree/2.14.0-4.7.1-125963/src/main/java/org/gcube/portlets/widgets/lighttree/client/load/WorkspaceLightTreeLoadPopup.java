/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.client.load;

import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.widgets.lighttree.client.Item;
import org.gcube.portlets.widgets.lighttree.client.ItemType;
import org.gcube.portlets.widgets.lighttree.client.WorkspaceLightTreePanel;
import org.gcube.portlets.widgets.lighttree.client.event.DataLoadEvent;
import org.gcube.portlets.widgets.lighttree.client.event.DataLoadHandler;
import org.gcube.portlets.widgets.lighttree.client.event.HasDataLoadHandlers;
import org.gcube.portlets.widgets.lighttree.client.event.HasPopupHandlers;
import org.gcube.portlets.widgets.lighttree.client.event.ItemSelectionEvent;
import org.gcube.portlets.widgets.lighttree.client.event.ItemSelectionHandler;
import org.gcube.portlets.widgets.lighttree.client.event.PopupEvent;
import org.gcube.portlets.widgets.lighttree.client.event.PopupHandler;

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
public class WorkspaceLightTreeLoadPopup extends GCubeDialog implements DataLoadHandler, ItemSelectionHandler, HasDataLoadHandlers, HasPopupHandlers {

	protected Button openButton;
	protected boolean closeOnSelect;
	protected boolean showButtons;

	protected String title;

	protected Item selectedItem;

	protected WorkspaceLightTreePanel loadPanel;

	/**
	 * Create a new load popup.
	 * @param title the popup title.
	 * @param closeOnSelect close the popup when a selectable item is selected.
	 * @param showButtons show the open and cancel button.
	 */
	public WorkspaceLightTreeLoadPopup(final String title, boolean closeOnSelect, boolean showButtons)
	{
		super();

		this.closeOnSelect = closeOnSelect;
		this.showButtons = showButtons;
		this.title = title;

		setText("loading...");

		VerticalPanel panelContents = new VerticalPanel();
		panelContents.setSpacing(4);

		loadPanel = new WorkspaceLightTreePanel();
		loadPanel.addDataLoadHandler(this);
		loadPanel.addSelectionHandler(this);
		panelContents.add(loadPanel);

		if (showButtons){

			HorizontalPanel hp = new HorizontalPanel();

			openButton = new Button("Open", new ClickHandler() {

				public void onClick(ClickEvent event) {
					hide();
					PopupEvent.fireItemSelected(WorkspaceLightTreeLoadPopup.this, selectedItem);
				}
			});

			openButton.setEnabled(false);

			hp.add(openButton);

			Button cancelButton = new Button("Cancel", new ClickHandler() {

				public void onClick(ClickEvent event) {
					hide();
					PopupEvent.fireCanceled(WorkspaceLightTreeLoadPopup.this);
				}
			});
			hp.add(cancelButton);

			panelContents.add(hp);
			if (LocaleInfo.getCurrentLocale().isRTL()) { 
				panelContents.setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_LEFT);
			} else {
				panelContents.setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_RIGHT);
			}

		}

		setWidget(panelContents);
	}
	/**
	 * resizes the scrollpanel too
	 */
	public void setWidth(String width) {
		loadPanel.setWidth(width);
	}

	/**
	 * @return the expandRootChildren
	 */
	public boolean isExpandRootChildren() {
		return loadPanel.isExpandRootChildren();
	}

	/**
	 * Set to expand the root children when data is loaded.
	 * @param expandRootChildren the expandRootChildren to set
	 */
	public void setExpandRootChildren(boolean expandRootChildren) {
		loadPanel.setExpandRootChildren(expandRootChildren);
	}

	/**
	 * @return the expandAllItems
	 */
	public boolean isExpandAllItems() {
		return loadPanel.isExpandAllItems();
	}

	/**
	 * Set to expand the entire tree when data is loaded.
	 * @param expandAllItems the expandAllItems to set
	 */
	public void setExpandAllItems(boolean expandAllItems) {
		loadPanel.setExpandAllItems(expandAllItems);
	}

	/**
	 * Return which items are selectable.
	 * @return the selectableTypes
	 */
	public List<ItemType> getSelectableTypes() {
		return loadPanel.getSelectableTypes();
	}

	/**
	 * Set which items are selectable.
	 * @param selectableTypes the selectableTypes to set
	 */
	public void setSelectableTypes(ItemType ... selectableTypes) {
		loadPanel.setSelectableTypes(selectableTypes);
	}
	
	/**
	 * Set the root folder and the folders non selectable.
	 */
	public void setFoldersNonSelectable() {
		loadPanel.getSelectableTypes().remove(ItemType.ROOT);
		loadPanel.getSelectableTypes().remove(ItemType.FOLDER);
	}


	/**
	 * Return the showable items.
	 * @return the showableTypes
	 */
	public List<ItemType> getShowableTypes() {
		return loadPanel.getShowableTypes();
	}

	/**
	 * Set the showable items. The folders items are show as default.
	 * @param showableTypes the showableTypes to set
	 */
	public void setShowableTypes(ItemType ... showableTypes) {
		loadPanel.setShowableTypes(showableTypes);
	}

	/**
	 * @return the showEmptyFolders
	 */
	public boolean isShowEmptyFolders() {
		return loadPanel.isShowEmptyFolders();
	}

	/**
	 * Set if show empty folders. Default is true.
	 * @param showEmptyFolders the showEmptyFolders to set
	 */
	public void setShowEmptyFolders(boolean showEmptyFolders) {
		loadPanel.setShowEmptyFolders(showEmptyFolders);
	}

	/**
	 * @return
	 * @see org.gcube.portlets.widgets.lighttree.client.WorkspaceLightTreePanel#getAllowedMimeTypes()
	 */
	public List<String> getAllowedMimeTypes() {
		return loadPanel.getAllowedMimeTypes();
	}

	/**
	 * @param allowedMimeTypes
	 * @see org.gcube.portlets.widgets.lighttree.client.WorkspaceLightTreePanel#setAllowedMimeTypes(java.util.List)
	 */
	public void setAllowedMimeTypes(List<String> allowedMimeTypes) {
		loadPanel.setAllowedMimeTypes(allowedMimeTypes);
	}

	/**
	 * @param allowedMimeTypes
	 * @see org.gcube.portlets.widgets.lighttree.client.WorkspaceLightTreePanel#setAllowedMimeTypes(java.lang.String[])
	 */
	public void setAllowedMimeTypes(String... allowedMimeTypes) {
		loadPanel.setAllowedMimeTypes(allowedMimeTypes);
	}

	/**
	 * @param name
	 * @param value
	 * @see org.gcube.portlets.widgets.lighttree.client.WorkspaceLightTreePanel#addRequiredProperty(java.lang.String, java.lang.String)
	 */
	public void addRequiredProperty(String name, String value) {
		loadPanel.addRequiredProperty(name, value);
	}
	/**
	 * @return
	 * @see org.gcube.portlets.widgets.lighttree.client.WorkspaceLightTreePanel#getRequiredProperties()
	 */
	public Map<String, String> getRequiredProperties() {
		return loadPanel.getRequiredProperties();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void show() {
		super.show();
		loadPanel.loadTree();
	}

	/**
	 * {@inheritDoc}
	 */
	public void onDataLoad(DataLoadEvent event) {
		if (event.isFailed()) {
			setText("Error "+event.getCaught().getMessage());
		} else {
			setText(title);
			if (!closeOnSelect) loadPanel.selectItem(loadPanel.getRoot());
		}
		
		//we propagate the event
		fireEvent(event);
	}

	/**
	 * {@inheritDoc}
	 */
	public void onSelection(ItemSelectionEvent event) {
		if (event.isSelectable()){
			selectedItem = event.getSelectedItem();

			if (showButtons) openButton.setEnabled(true);

			if (closeOnSelect) {
				hide();
				PopupEvent.fireItemSelected(this, selectedItem);
			}
		} else {
			if (showButtons) openButton.setEnabled(false);
		}
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
