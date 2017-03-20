/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.client.save;


import java.util.List;

import org.gcube.portlets.widgets.lighttree.client.Item;
import org.gcube.portlets.widgets.lighttree.client.ItemType;
import org.gcube.portlets.widgets.lighttree.client.Util;
import org.gcube.portlets.widgets.lighttree.client.WorkspaceLightTreePanel;
import org.gcube.portlets.widgets.lighttree.client.WorkspaceService;
import org.gcube.portlets.widgets.lighttree.client.WorkspaceServiceAsync;
import org.gcube.portlets.widgets.lighttree.client.event.DataLoadEvent;
import org.gcube.portlets.widgets.lighttree.client.event.DataLoadHandler;
import org.gcube.portlets.widgets.lighttree.client.event.HasDataLoadHandlers;
import org.gcube.portlets.widgets.lighttree.client.event.HasItemSelectionHandlers;
import org.gcube.portlets.widgets.lighttree.client.event.HasNameChangeHandlers;
import org.gcube.portlets.widgets.lighttree.client.event.ItemSelectionEvent;
import org.gcube.portlets.widgets.lighttree.client.event.ItemSelectionHandler;
import org.gcube.portlets.widgets.lighttree.client.event.NameChangeEvent;
import org.gcube.portlets.widgets.lighttree.client.event.NameChangeHandler;
import org.gcube.portlets.widgets.lighttree.client.resources.WorkspaceLightTreeResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class WorkspaceLightTreeSavePanel extends Composite implements ItemSelectionHandler, KeyUpHandler, DataLoadHandler, HasItemSelectionHandlers, HasDataLoadHandlers, HasNameChangeHandlers {

	protected TextBox name;

	protected HTML destinationFolderName;

	protected boolean showNameField;

	protected boolean acceptSiblingName;

	protected WorkspaceLightTreePanel lightTreePanel;
	
	protected WorkspaceServiceAsync workspaceAreaService;

	
	public WorkspaceLightTreeSavePanel(boolean showNameField)
	{
		this(showNameField, "");
	}

	/**
	 * Create a WorkspaceAreaTreeSavePanel instance.
	 * As default all item types are showable and selectable, the empty folder are showed.
	 * @param showNameField
	 * @param nameValue
	 */
	public WorkspaceLightTreeSavePanel(boolean showNameField, String nameValue)
	{
		this.showNameField = showNameField;
		this.acceptSiblingName = false;
		
		workspaceAreaService = GWT.create(WorkspaceService.class);

		//FIXME workaround for GWT issue 4254
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
						WorkspaceLightTreeResources.INSTANCE.css().ensureInjected();
					}
				});

		VerticalPanel panelContents = new VerticalPanel();
		panelContents.setSpacing(4);

		lightTreePanel = new WorkspaceLightTreePanel();
		lightTreePanel.addSelectionHandler(this);
		lightTreePanel.addDataLoadHandler(this);

		panelContents.add(lightTreePanel);

		HorizontalPanel destinationFolderPanel = new HorizontalPanel();
		destinationFolderPanel.setSpacing(2);
		destinationFolderPanel.add(new Label("Destination:"));

		destinationFolderName = new HTML();
		destinationFolderPanel.add(destinationFolderName);

		panelContents.add(destinationFolderPanel);

		if (showNameField){

			HorizontalPanel hp = new HorizontalPanel();
			hp.setSpacing(2);
			hp.add(new Label("Name:"));
			name = new TextBox();
			name.setText(nameValue);

			name.addKeyUpHandler(this);

			hp.add(name);
			panelContents.add(hp);
		}

		if (showNameField && nameValue!=null && !nameValue.equals("")) checkName(); 

		initWidget(panelContents);
	}
	
	/**
	 * @return the acceptSiblingName
	 */
	public boolean isAcceptSiblingName() {
		return acceptSiblingName;
	}

	/**
	 * <code>true</code> if the name can be the same of sibling elements.
	 * @param acceptSiblingName the acceptSiblingName to set
	 */
	public void setAcceptSiblingName(boolean acceptSiblingName) {
		this.acceptSiblingName = acceptSiblingName;
	}

	public void refreshData(){
		lightTreePanel.refreshTree();
	}
	
	public void loadData(){
		lightTreePanel.loadTree();
	}

	protected void checkName()
	{
		final String nameValue = name.getText();

		//GWT.log("checkName newNameValue: "+nameValue);
		
		//check name conflicts
		Item selectedItem = lightTreePanel.getSelectedItem();
		//GWT.log("acceptSiblingName: "+acceptSiblingName);
		//GWT.log("selectedItem: "+selectedItem);
		//if (selectedItem!=null) GWT.log("isFolder: "+Util.isFolder(selectedItem.getType()));
		
		if (!acceptSiblingName && selectedItem!=null && Util.isFolder(selectedItem.getType())){
			//GWT.log("checking sibling with name: "+nameValue);
			for (Item child:selectedItem.getChildren()){
				if (child.getName().equals(nameValue)) {
					invalidName("An item with same name already exists");
					//GWT.log("found a confliting name with itema: "+child);
					return;
				}
			}
			//GWT.log("no conflict name");
		}

		//check name validity
		//GWT.log("checking "+nameValue+" server side");
		workspaceAreaService.checkName(nameValue, new AsyncCallback<Boolean>() {

			public void onFailure(Throwable caught) {
				GWT.log("checkName server side failed",caught);
				invalidName("Error server side "+caught.getMessage());
			}

			public void onSuccess(Boolean result) {
				//GWT.log("checkName server side result: "+result);
				if (!result) invalidName("Invalid name");
				else validName(nameValue);
			}

		});
	}
	
	protected void validName(String nameValue)
	{
		name.removeStyleName(WorkspaceLightTreeResources.INSTANCE.css().nameError());
		NameChangeEvent.fire(WorkspaceLightTreeSavePanel.this, nameValue, true);
	}


	protected void invalidName(String errorMsg)
	{
		name.addStyleName(WorkspaceLightTreeResources.INSTANCE.css().nameError());
		name.setTitle(errorMsg);
		NameChangeEvent.fire(this, name.getValue(), false);
	}

	/**
	 * Set the destination folder.
	 * @param item the item where to get the destination folder.
	 */
	public void updateDestinationFolder(Item item)
	{
		if (item == null) return;
		String folderPath = Util.isFolder(item.getType())?item.getPath():item.getParent().getPath();
		destinationFolderName.setHTML(Util.ellipsis(folderPath, 40, true));
		destinationFolderName.setTitle(folderPath);
	}

	/**
	 * {@inheritDoc}
	 */
	public void onKeyUp(KeyUpEvent event) {
		checkName();
	}

	/**
	 * {@inheritDoc}
	 */
	public HandlerRegistration addSelectionHandler(ItemSelectionHandler handler) {
		return addHandler(handler, ItemSelectionEvent.getType());
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
	public HandlerRegistration addNameChangeHandler(NameChangeHandler handler) {
		return addHandler(handler, NameChangeEvent.getType());
	}

	/**
	 * {@inheritDoc}
	 */
	public void onSelection(ItemSelectionEvent event) {
		
		if (event.isSelectable()){
			updateDestinationFolder(event.getSelectedItem());
			//we need to check if name still valid
			if (showNameField) checkName();
		}
		//we propagate the event
		fireEvent(event);
	}

	/**
	 * {@inheritDoc}
	 */
	public void onDataLoad(DataLoadEvent event) {
		GWT.log("onDataLoad event:"+event);
		if (!event.isFailed() && name!=null && !name.getText().equals("")) checkName();
		if (!event.isFailed()) selectItem(getRoot());
		//we propagate the event
		fireEvent(event);
	}
	
	
	
	/**
	 * @return
	 * @see org.gcube.portlets.widgets.lighttree.client.WorkspaceLightTreePanel#getRoot()
	 */
	public Item getRoot() {
		return lightTreePanel.getRoot();
	}

	/**
	 * @param item
	 * @see org.gcube.portlets.widgets.lighttree.client.WorkspaceLightTreePanel#selectItem(org.gcube.portlets.widgets.lighttree.client.Item)
	 */
	public void selectItem(Item item) {
		lightTreePanel.selectItem(item);
	}

	/**
	 * @return the expandRootChildren
	 */
	public boolean isExpandRootChildren() {
		return lightTreePanel.isExpandRootChildren();
	}

	/**
	 * Set to expand the root children when data is loaded.
	 * @param expandRootChildren the expandRootChildren to set
	 */
	public void setExpandRootChildren(boolean expandRootChildren) {
		lightTreePanel.setExpandRootChildren(expandRootChildren);
	}

	/**
	 * @return the expandAllItems
	 */
	public boolean isExpandAllItems() {
		return lightTreePanel.isExpandAllItems();
	}

	/**
	 * Set to expand the entire tree when data is loaded.
	 * @param expandAllItems the expandAllItems to set
	 */
	public void setExpandAllItems(boolean expandAllItems) {
		lightTreePanel.setExpandAllItems(expandAllItems);
	}

	/**
	 * @return the selectableTypes
	 */
	public List<ItemType> getSelectableTypes() {
		return lightTreePanel.getSelectableTypes();
	}

	/**
	 * @param selectableTypes the selectableTypes to set
	 */
	public void setSelectableTypes(ItemType ... selectableTypes) {
		lightTreePanel.setSelectableTypes(selectableTypes);
	}

	/**
	 * @return the showableTypes
	 */
	public List<ItemType> getShowableTypes() {
		return lightTreePanel.getShowableTypes();
	}

	/**
	 * @param showableTypes the showableTypes to set
	 */
	public void setShowableTypes(ItemType ... showableTypes) {
		lightTreePanel.setShowableTypes(showableTypes);
	}

	/**
	 * @return the showEmptyFolders
	 */
	public boolean isShowEmptyFolders() {
		return lightTreePanel.isShowEmptyFolders();
	}

	/**
	 * @param showEmptyFolders the showEmptyFolders to set
	 */
	public void setShowEmptyFolders(boolean showEmptyFolders) {
		lightTreePanel.setShowEmptyFolders(showEmptyFolders);
	}

	public void setShowableTypes(List<ItemType> showableTypes) {
		lightTreePanel.setShowableTypes(showableTypes);		
	}

	public void setSelectableTypes(List<ItemType> selectableTypes) {
		lightTreePanel.setSelectableTypes(selectableTypes);		
	}
}
