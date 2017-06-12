package org.gcube.portlets.widgets.lighttree.client;

import org.gcube.portlets.widgets.lighttree.client.resources.WorkspaceLightTreeResources;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;


/**
 * The tree panel.
 * Manages the item selection.
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public class WorkspaceLightTree extends Composite implements HasSelectionHandlers<Item>, SelectionHandler<TreeItem>{

	protected Item currrentSelection;
	protected Tree tree;
	protected static TreeItem fakeItem = new TreeItem();
	protected boolean expandRootChildren = true;
	protected boolean expandAllItems = false;

	/**
	 * Create a new WorkspaceLightTree.
	 */
	public WorkspaceLightTree()
	{
		tree = new Tree();
		tree.setAnimationEnabled(true);
		tree.addSelectionHandler(this);
		initWidget(tree);
	}

	/**
	 * Returns the ExpandRootChildren flag.
	 * @return the expandRootChildren <code>true</code> if all root children are expanded when data is loaded, <code>false</code> otherwise
	 */
	public boolean isExpandRootChildren() {
		return expandRootChildren;
	}

	/**
	 * Sets if expand the root children when data is loaded.
	 * @param expandRootChildren <code>true</code> to expand the root children when data is loaded, <code>false</code> otherwise. 
	 */
	public void setExpandRootChildren(boolean expandRootChildren) {
		this.expandRootChildren = expandRootChildren;
	}

	/**
	 * Returns the ExpandAllItems flag.
	 * @return the expandAllItems <code>true</code> if all tree items are expanded when the data is loaded, <code>false</code> otherwise
	 */
	public boolean isExpandAllItems() {
		return expandAllItems;
	}

	/**
	 * Sets if expand the entire tree when data is loaded.
	 * @param expandAllItems <code>true</code> to expand all tree items when data is loaded, <code>false</code> otherwise
	 */
	public void setExpandAllItems(boolean expandAllItems) {
		this.expandAllItems = expandAllItems;
	}

	/**
	 * Set the current root element.
	 * @param root the root element.
	 */
	public void setRootItem(Item root)
	{
		tree.clear();
		TreeItem treeRoot = generateTree(root);
		
		treeRoot.setStyleName("treeContainer");

		if (expandRootChildren) treeRoot.setState(true, false);

		if (expandAllItems) openChildren(treeRoot);

		tree.addItem(treeRoot);
	}

	/**
	 * Opens all item children.
	 * @param item the parent item.
	 */
	protected void openChildren(TreeItem item)
	{
		for (int i = 0; i<item.getChildCount(); i++)
		{
			TreeItem child = item.getChild(i);
			openChildren(child);
		}
		item.setState(true, false);
	}

	/**
	 * Return the current selected item.
	 * @return the selected item.
	 */
	public Item getSelectedItem()
	{
		return currrentSelection;
	}

	/**
	 * {@inheritDoc}
	 */
	public HandlerRegistration addSelectionHandler(SelectionHandler<Item> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public HandlerRegistration addOpenHandler(OpenHandler<Item> handler) {
		return addHandler(handler, OpenEvent.getType());
	}

	/**
	 * {@inheritDoc}
	 */
	public void onSelection(SelectionEvent<TreeItem> event) {
		TreeItem selectedItem = event.getSelectedItem();
		if (selectedItem == null) currrentSelection = null;
		else currrentSelection = (Item) selectedItem.getUserObject();
		SelectionEvent.fire(this, currrentSelection);
	}
	
	/**
	 * Select the specified item
	 * @param item the item to select.
	 */
	public void selectItem(Item item)
	{
		selectItem(item.getId());
	}

	/**
	 * Select the specified item.
	 * @param itemId the item id to select.
	 */
	public void selectItem(String itemId)
	{
		for (int i = 0; i<tree.getItemCount(); i++)
		{
			TreeItem child = tree.getItem(i);
			boolean selected = selectItem(child, itemId);
			if (selected) return;
		}
	}

	/**
	 * Selects an item in the treeItem subtree.
	 * @param treeItem the subtree root.
	 * @param itemId the item id.
	 * @return <code>true</code> if an item has been selected, <code>false</code> otherwise.
	 */
	protected boolean selectItem(TreeItem treeItem, String itemId)
	{
		if (treeItem.getUserObject()!=null && ((Item)treeItem.getUserObject()).getId().equals(itemId)) {
			tree.setSelectedItem(treeItem, true);
			return true;
		}

		for (int i = 0; i<treeItem.getChildCount(); i++)
		{
			TreeItem child = treeItem.getChild(i);
			boolean selected = selectItem(child, itemId);
			if (selected) return true;
		}

		return false;
	}


	/**
	 * Returns the tree root.
	 * @return the tree root.
	 */
	public Item getRoot()
	{
		TreeItem treeItem = tree.getItem(0);
		return (Item) treeItem.getUserObject();
	}

	/**
	 * Generate the tree starting from the root node. 
	 * @param item the root item.
	 * @return the root treeItem.
	 */
	protected TreeItem generateTree(Item item)
	{
		ImageResource imageResource = getImage(item);
		HorizontalPanel node = createNodeWidget(imageResource, item.getName());

		TreeItem treeItem = new TreeItem(node);
		treeItem.setUserObject(item);


		for (Item child:item.getChildren()) generateSubTree(treeItem, child);


		return treeItem;
	}

	/**
	 * Generate the item subtree.
	 * @param parent the parent item.
	 * @param item the item to load.
	 */
	protected void generateSubTree(TreeItem parent, Item item)
	{
		//GWT.log("generating "+item.getName()+" in "+parent.getText());
		ImageResource imageResource = getImage(item);

		TreeItem treeItem = addImageItem(parent, item.getName(), imageResource);
		treeItem.setUserObject(item);

		if (item.getChildren().size()>0) {
			for (Item child:item.getChildren()) generateSubTree(treeItem, child);
		} else {
			if (Util.isFolder(item.getType())) treeItem.addItem(fakeItem);
		}
	}

	/**
	 * Returns the image based on the item type.
	 * @param type the item type.
	 * @return the image.
	 */
	protected ImageResource getImage(Item item)
	{
		switch (item.getType()) {
			case ROOT: return WorkspaceLightTreeResources.INSTANCE.root();
			case FOLDER: {
				if (item.isShared()) return WorkspaceLightTreeResources.INSTANCE.sharedFolder();
				else return WorkspaceLightTreeResources.INSTANCE.folder();
			}
			case EXTERNAL_IMAGE: return WorkspaceLightTreeResources.INSTANCE.external_image();
			case EXTERNAL_FILE: return WorkspaceLightTreeResources.INSTANCE.external_file();
			case EXTERNAL_PDF_FILE: return WorkspaceLightTreeResources.INSTANCE.external_pdf();
			case EXTERNAL_RESOURCE_LINK: return WorkspaceLightTreeResources.INSTANCE.external_resource_link();
			case EXTERNAL_URL: return WorkspaceLightTreeResources.INSTANCE.external_url();
			case REPORT_TEMPLATE: return WorkspaceLightTreeResources.INSTANCE.report_template();
			case REPORT: return WorkspaceLightTreeResources.INSTANCE.report();
			case QUERY: return WorkspaceLightTreeResources.INSTANCE.query();
			case DOCUMENT: return WorkspaceLightTreeResources.INSTANCE.document();
			case METADATA: return WorkspaceLightTreeResources.INSTANCE.metadata();
			case PDF_DOCUMENT: return WorkspaceLightTreeResources.INSTANCE.pdf_document();
			case IMAGE_DOCUMENT: return WorkspaceLightTreeResources.INSTANCE.image_document();
			case URL_DOCUMENT: return WorkspaceLightTreeResources.INSTANCE.url_document();
			case TIME_SERIES: return WorkspaceLightTreeResources.INSTANCE.timeseries();
			case AQUAMAPS_ITEM: return WorkspaceLightTreeResources.INSTANCE.aquamaps();
			case WORKFLOW_REPORT: return WorkspaceLightTreeResources.INSTANCE.workflow_report();
			case WORKFLOW_TEMPLATE: return WorkspaceLightTreeResources.INSTANCE.workflow_template();
			case GCUBE_ITEM: return WorkspaceLightTreeResources.INSTANCE.gCube();

			case UNKNOWN_TYPE: return WorkspaceLightTreeResources.INSTANCE.unknownType();
			default: {
				System.err.println("Unknown item type "+item.getType());
				return WorkspaceLightTreeResources.INSTANCE.unknownType();
			}
		}
	}

	/**
	 * A helper method to simplify adding tree items that have attached images.
	 * {@link #addImageItem(TreeItem, String, AbstractImagePrototype) code}
	 * 
	 * @param root the tree item to which the new item will be added.
	 * @param title the text associated with this item.
	 * @param imageResource the image associated with the item.
	 * @return
	 */
	protected TreeItem addImageItem(TreeItem root, String title, ImageResource imageResource) {

		HorizontalPanel node = createNodeWidget(imageResource, title);
		TreeItem item = new TreeItem(node);
		root.addItem(item);
		return item;
	}

	/**
	 * Generates HTML for a tree item with an attached icon.
	 * @param imageResource the image resource to use
	 * @param title the title of the item
	 * @return the resultant HTML
	 */
	protected HorizontalPanel createNodeWidget(ImageResource imageResource, String title) {
		HorizontalPanel node = new HorizontalPanel();
		node.setSpacing(2);
		node.add(new Image(imageResource));
		node.add(new HTML(title));
		return node;
	}

	public Tree getTree() {
		return tree;
	}
}
