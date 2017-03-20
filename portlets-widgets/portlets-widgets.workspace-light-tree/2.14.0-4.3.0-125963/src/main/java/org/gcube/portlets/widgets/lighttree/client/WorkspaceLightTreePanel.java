/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.widgets.lighttree.client.event.DataLoadEvent;
import org.gcube.portlets.widgets.lighttree.client.event.DataLoadHandler;
import org.gcube.portlets.widgets.lighttree.client.event.HasDataLoadHandlers;
import org.gcube.portlets.widgets.lighttree.client.event.HasItemSelectionHandlers;
import org.gcube.portlets.widgets.lighttree.client.event.ItemSelectionEvent;
import org.gcube.portlets.widgets.lighttree.client.event.ItemSelectionHandler;
import org.gcube.portlets.widgets.lighttree.client.event.NameChangeEvent;
import org.gcube.portlets.widgets.lighttree.client.event.NameChangeHandler;
import org.gcube.portlets.widgets.lighttree.client.resources.WorkspaceLightTreeResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 * Modified by Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class WorkspaceLightTreePanel extends Composite implements SelectionHandler<Item>, HasItemSelectionHandlers, HasDataLoadHandlers, OpenHandler<TreeItem> {
	
	protected static final Image LOADING_IMAGE = new Image(WorkspaceLightTreeResources.INSTANCE.loading());
	protected static final HorizontalPanel LOADING_PANEL = new HorizontalPanel();
	static {
		LOADING_PANEL.setSpacing(3);
		LOADING_PANEL.add(LOADING_IMAGE);
		LOADING_PANEL.add(new HTML("Loading..."));
	}
	
	protected List<ItemType> selectableTypes = new ArrayList<ItemType>();
	protected List<ItemType> showableTypes = new ArrayList<ItemType>();
	protected List<String> allowedMimeTypes = new ArrayList<String>();
	protected Map<String, String> requiredProperties = new HashMap<String, String>();
	
	protected boolean showEmptyFolders = true;
	
	protected Item selectedItem;
	
	protected WorkspaceLightTree tree;

	protected ScrollPanel sp;
	
	protected WorkspaceServiceAsync workspaceAreaService;
	
	protected ErrorPanel errorPanel;
	
	/**
	 * Create a WorkspaceLightTreePanel instance.
	 * As default all item types are showable and selectable, the empty folder are showed.
	 */
	public WorkspaceLightTreePanel()
	{
		//as default all items are showable and selectable
		for (ItemType type:ItemType.values()) {
			showableTypes.add(type);
			selectableTypes.add(type);
		}
		
		workspaceAreaService = GWT.create(WorkspaceService.class);
		errorPanel = new ErrorPanel();
		errorPanel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				refreshTree();
			}
		});


		tree = new WorkspaceLightTree();
		tree.addSelectionHandler(this);
		//Added by Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
		tree.getTree().addOpenHandler(this);

		sp = new ScrollPanel();

		sp.setWidth("350px");
		sp.setHeight("350px");

		initWidget(sp);
	}
	/**
	 * resizes the scrollpanel too
	 */
	public void setWidth(String width) {
		sp.setWidth(width);
	}
	
	/**
	 * @return the selectableTypes
	 */
	public List<ItemType> getSelectableTypes() {
		return selectableTypes;
	}

	/**
	 * @param selectableTypes the selectableTypes to set
	 */
	public void setSelectableTypes(ItemType ... selectableTypes) {
		this.selectableTypes.clear();
		if (selectableTypes!=null) for (ItemType type:selectableTypes) this.selectableTypes.add(type);
	}


	/**
	 * @param selectableTypes the selectableTypes to set
	 */
	public void setSelectableTypes(List<ItemType> selectableTypes) {
		this.selectableTypes = selectableTypes;
	}

	/**
	 * @return the showableTypes
	 */
	public List<ItemType> getShowableTypes() {
		return showableTypes;
	}

	/**
	 * @param showableTypes the showableTypes to set
	 */
	public void setShowableTypes(ItemType ... showableTypes) {
		this.showableTypes.clear();
		if (showableTypes!=null) for (ItemType type:showableTypes) this.showableTypes.add(type);
	}
	
	/**
	 * @param showableTypes the showableTypes to set
	 */
	public void setShowableTypes(List<ItemType> showableTypes) {
		this.showableTypes = showableTypes;
	}

	/**
	 * @return the mimeTypeFilters
	 */
	public List<String> getAllowedMimeTypes() {
		return allowedMimeTypes;
	}

	/**
	 * @param mimeTypeFilters the mimeTypeFilters to set
	 */
	public void setAllowedMimeTypes(List<String> allowedMimeTypes) {
		this.allowedMimeTypes = allowedMimeTypes;
	}
	
	public void setAllowedMimeTypes(String ... allowedMimeTypes) {
		this.allowedMimeTypes.clear();
		if (allowedMimeTypes!=null) for (String allowedMimeType:allowedMimeTypes)	this.allowedMimeTypes.add(allowedMimeType);
	}
	
	public void addRequiredProperty(String name, String value)
	{
		requiredProperties.put(name, value);
	}

	/**
	 * @return the requiredProperties
	 */
	public Map<String, String> getRequiredProperties() {
		return requiredProperties;
	}
	/**
	 * @return the showEmptyFolders
	 */
	public boolean isShowEmptyFolders() {
		return showEmptyFolders;
	}

	/**
	 * @param showEmptyFolders the showEmptyFolders to set
	 */
	public void setShowEmptyFolders(boolean showEmptyFolders) {
		this.showEmptyFolders = showEmptyFolders;
	}

	/**
	 * @return the selectedItem
	 */
	public Item getSelectedItem() {
		return selectedItem;
	}

	/**
	 * Refresh the Workspace Tree.
	 */
	public void refreshTree(){
		loadTree();
	}
	
	/**
	 * Set the panel in loading mode.
	 */
	protected void setLoading()
	{
		sp.clear();
		sp.add(LOADING_PANEL);
	}
	
	/**
	 * Load the Workspace Tree.
	 */
	public void loadTree() {
		GWT.log("loading tree data");
		setLoading();
		
		//we make a copy of showable types
		List<ItemType> showableTypesParam = new ArrayList<ItemType>(showableTypes);

		//we get sure that folders are displayed
		for (ItemType folder:Util.FOLDERS){
			if (!showableTypesParam.contains(folder)) showableTypesParam.add(folder);
		}

		boolean purgeEmpyFolders = !showEmptyFolders;
		
		FilterCriteria filterCriteria = new FilterCriteria(allowedMimeTypes, requiredProperties);
		
		GWT.log("loading workspace tree from server");
		workspaceAreaService.getRoot(showableTypesParam, purgeEmpyFolders, filterCriteria, new AsyncCallback<Item>(){

			/**
			 * {@inheritDoc}
			 */
			public void onFailure(Throwable caught) {
				GWT.log("Error loading workspace tree from server",caught);
				showErrorPanel(caught);
				DataLoadEvent.fireLoadDataFailed(WorkspaceLightTreePanel.this, caught);
			}

			public void onSuccess(Item root) {
				GWT.log("workspace tree retrieved.");
				tree.setRootItem(root);
				
				//we select the root
				//tree.selectItem(root.getId());

				//then we remove the loading icon
				sp.clear();
				sp.add(tree);

				GWT.log("workspace tree loaded");
				DataLoadEvent.fireLoadDataSuccess(WorkspaceLightTreePanel.this);
			}

		});
	}
	
	/**
	 * Select the specified item.
	 * @param item
	 */
	public void selectItem(Item item)
	{
		tree.selectItem(item);
	}
	
	/**
	 * Returns the tree root.
	 * @return the root.
	 */
	public Item getRoot()
	{
		return tree.getRoot();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void onSelection(SelectionEvent<Item> event) {
		Item item = event.getSelectedItem();
		//GWT.log("onSelection item: "+item);
		if (item!=null){
			boolean selectable = selectableTypes.contains(item.getType());
			selectedItem = (selectable)?item:null;
			//GWT.log("selectable: "+selectable+" selectedItem: "+selectedItem);
			ItemSelectionEvent.fire(this, item, selectable);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onOpen(OpenEvent<TreeItem> event) {
		final TreeItem openedItem = event.getTarget();

		System.out.println("openItem child count " + openedItem.getChildCount());

		final Item curExpand;
		if (openedItem != null) {
			curExpand = (Item) openedItem.getUserObject();

			//Execute RPC only if the number of item child is 0 or 1
			if (openedItem.getChildCount() == 1 || openedItem.getChildCount() == 0) {
				// Close the item immediately
				// openedItem.setState(false, false);
				openedItem.getChild(0).remove();
				tree.addImageItem(openedItem, "loading", WorkspaceLightTreeResources.INSTANCE.loading());
				
				//we make a copy of showable types
				List<ItemType> showableTypesParam = new ArrayList<ItemType>(showableTypes);

				//we get sure that folders are displayed
				for (ItemType folder:Util.FOLDERS){
					if (!showableTypesParam.contains(folder)) showableTypesParam.add(folder);
				}
				
				FilterCriteria filterCriteria = new FilterCriteria(allowedMimeTypes, requiredProperties);

				workspaceAreaService.getFolder(curExpand.getId(), showableTypesParam,  !isShowEmptyFolders(), filterCriteria, new AsyncCallback<Item>() {

					@Override
					public void onSuccess(Item result) {
						// item.removeItems();
						
						if(result!=null){

							TreeItem treeRoot = tree.generateTree(result);

//							System.out.println("result child count "+ treeRoot.getChildCount());

							int numChildren = treeRoot.getChildCount();
							
							GWT.log("retrived on client "+numChildren);

							for (int i = 0; i < numChildren; i++) {
//								System.out.println("index "+ i +" child add "+ treeRoot.getChild(0));
								
								Item child = (Item) treeRoot.getChild(0).getUserObject();
								child.setParent(curExpand);
								openedItem.addItem(treeRoot.getChild(0)); //this operation will affect the child count
							}
							// Reopen the item
							// openedItem.setState(true, false);
							
							GWT.log(numChildren + " ws items loading successfully");
						}
						
						// Remove the temporary item when we finish loading
						openedItem.getChild(0).remove();

					}

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Error loading workspace folder from server",caught);

					}
				});

			}

		}
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
	 * @return
	 * @see org.gcube.portlets.widgets.lighttree.client.WorkspaceLightTree#isExpandAllItems()
	 */
	public boolean isExpandAllItems() {
		return tree.isExpandAllItems();
	}

	/**
	 * @return
	 * @see org.gcube.portlets.widgets.lighttree.client.WorkspaceLightTree#isExpandRootChildren()
	 */
	public boolean isExpandRootChildren() {
		return tree.isExpandRootChildren();
	}

	/**
	 * @param expandAllItems
	 * @see org.gcube.portlets.widgets.lighttree.client.WorkspaceLightTree#setExpandAllItems(boolean)
	 */
	public void setExpandAllItems(boolean expandAllItems) {
		tree.setExpandAllItems(expandAllItems);
	}

	/**
	 * @param expandRootChildren
	 * @see org.gcube.portlets.widgets.lighttree.client.WorkspaceLightTree#setExpandRootChildren(boolean)
	 */
	public void setExpandRootChildren(boolean expandRootChildren) {
		tree.setExpandRootChildren(expandRootChildren);
	}

	
	public void showErrorPanel(Throwable throwable)
	{
		errorPanel.setMessage(throwable);
		sp.clear();
		sp.add(errorPanel);
	}


}
