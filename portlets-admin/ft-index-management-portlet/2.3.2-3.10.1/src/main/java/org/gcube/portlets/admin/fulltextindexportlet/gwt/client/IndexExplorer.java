package org.gcube.portlets.admin.fulltextindexportlet.gwt.client;

import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces.ItemAddedListener;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces.ItemContent;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.interfaces.ItemRemovedListener;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.client.ui.indexmanagement.index.IndexItemContent;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.CollectionBean;
import org.gcube.portlets.admin.fulltextindexportlet.gwt.shared.IndexBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeImages;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.ImageBundle.Resource;

/** An Explorer tree allowing the user to browse Indices and IndexLookups. */
public class IndexExplorer extends Composite implements ItemRemovedListener,
ItemAddedListener, TreeListener {

	/** An ImageBundle for the images to be used in the IndexExplorer trees */
	private static final Images images = (Images) GWT.create(Images.class);

	/**
	 * A map used to manage the items of the IndexEplorer tree, by linking items
	 * to a unique ID
	 */
	private HashMap<String, TreeItem> itemMap;

	/** A Tree Item to use at the root for all fake collections */
	private TreeItem fakeCollectionRoot = null;

	/** A Tree representing the the different parts of an Index */
	private Tree tree = new Tree();

	/** an ImageBundle for the images to be used in the IndexExplorer tree */
	public interface Images extends ImageBundle, TreeImages {
		/**
		 * The image icon used for "collection name" tree items
		 */ 
		@Resource("org/gcube/portlets/admin/fulltextindexportlet/gwt/public/collectionName.png")
		AbstractImagePrototype nameImage();

		/**
		 * The image icon used for "collection name" tree items which have index grand children
		 */
		@Resource("org/gcube/portlets/admin/fulltextindexportlet/gwt/public/indexedColName.png")
		AbstractImagePrototype indexedNameImage();

		/**
		 * The image icon used for "collection" tree items
		 */
		@Resource("org/gcube/portlets/admin/fulltextindexportlet/gwt/public/collection.png")
		AbstractImagePrototype collectionImage();

		/**
		 * The image icon used for "collection" tree items which have indices under them
		 */
		@Resource("org/gcube/portlets/admin/fulltextindexportlet/gwt/public/indexedCollection.png")
		AbstractImagePrototype indexedColImage();

		/**
		 * The image icon used for the "fake collections" tree item
		 */ 
		@Resource("org/gcube/portlets/admin/fulltextindexportlet/gwt/public/test.png")
		AbstractImagePrototype testImage();

		/**
		 * The image icon used for "index" tree items
		 */
		@Resource("org/gcube/portlets/admin/fulltextindexportlet/gwt/public/index.png")
		AbstractImagePrototype indexImage();

		/**
		 * The image icon used for "New Index" tree items
		 */
		@Resource("org/gcube/portlets/admin/fulltextindexportlet/gwt/public/new.gif")
		AbstractImagePrototype newIndexImage();
		
		/**
		 * The image icon used for "New Collection" tree items
		 */ 
		@Resource("org/gcube/portlets/admin/fulltextindexportlet/gwt/public/new_collection.gif")
		AbstractImagePrototype newCollectionImage();
	}

	/**
	 * An extention of the TreeItem class used to hold information needed in
	 * order to create new Index related items.
	 */
	public class AdditionTreeItem extends TreeItem {
		/** A constant used indicate that the Type an Item handle is "Collection" */
		public static final String COLLECTION = "COLLECTION";

		/** A constant used indicate that the Type an Item handle is "Index" */
		public static final String INDEX = "INDEX";

		/**
		 * The type of Index related Item of which this item represents the
		 * addition. Should always be one of the constants
		 */
		private String type;

		/**
		 * The constructor, creating an AdditionTreeItem with a specific caption
		 * for a specific type.
		 * 
		 * @param caption -
		 *            the HTML Caption of this TreeItem
		 * @param type -
		 *            The type of Index related Item of which this
		 *            AdditionTreeItem should represent the addition
		 */
		public AdditionTreeItem(String caption, String type) {
			super(caption);
			this.type = type;
		}

		/**
		 * Getter method for the type of Index related Item of which this item
		 * represents the addition.
		 * 
		 * @return - the type of Index related Item of which this item
		 *         represents the addition.
		 */
		public String getType() {
			return type;
		}
	}

	/**
	 * An extension of the TreeItem class used to hold information regarding a
	 * Collection (or a number of collections aggregated under a common name)
	 */
	public class CollectionTreeItem extends TreeItem {
		/** The number of indices under this collection item */
		private int indexCount = 0;
		private String title;
		private String collectionID;
		AbstractImagePrototype unindexedImage;
		AbstractImagePrototype indexedImage;

		public CollectionTreeItem(String title, String collectionID, AbstractImagePrototype indexedImage, AbstractImagePrototype unindexedImage){
			super(toHTML(unindexedImage, title));
			this.title = title;
			this.collectionID = collectionID;
			this.unindexedImage = unindexedImage;
			this.indexedImage = indexedImage;

		}

		public String getCollectionID() {
			return this.collectionID;
		}
		
		public void addItem(TreeItem item){
			super.addItem(item);
			if(item instanceof IndexTreeItem){
				indexAdded();
			}
		}

		public void removeItem(TreeItem item){
			super.removeItem(item);
			if(item instanceof IndexTreeItem){
				indexRemoved();
			}
		}

		public void indexAdded() {
			indexCount++;
			if(indexCount == 1){
				this.setHTML(toHTML(indexedImage, title));
			}
			if(this.getParentItem() != null &&
					this.getParentItem() instanceof CollectionTreeItem){
				((CollectionTreeItem)this.getParentItem()).indexAdded();
			}
		}

		public void indexRemoved() {
			indexCount--;
			if(indexCount == 0){
				this.setHTML(toHTML(unindexedImage, title));
			}
			if(this.getParentItem() != null &&
					this.getParentItem() instanceof CollectionTreeItem){
				((CollectionTreeItem)this.getParentItem()).indexRemoved();
			}
		}

	}

	/**
	 * An extention of the TreeItem class used to hold information regarding an
	 * Index
	 */
	public class IndexTreeItem extends TreeItem {
		/** The ID of the Index this TreeItem represents */
		private String indexID;
		
		private String relatedCollectionID;

		/**
		 * The constructor, creating an IndexTreeItem with a specific caption
		 * representing a specific Index
		 * 
		 * @param caption -
		 *            the HTML Caption of this TreeItem.
		 * @param indexID -
		 *            the ID of the index this IndexTreeItem should represent.
		 */
		public IndexTreeItem(String caption, String indexID, String relatedCollectionID) {
			super(caption);
			this.indexID = indexID;
			this.relatedCollectionID = relatedCollectionID;
		}

		/** Getter method for the ID of the index this IndexTreeItem represents */
		public String getIndexID() {
			return indexID;
		}
		
		public String getRelatedCollectionID () {
			return this.relatedCollectionID;
		}
	}

	/**
	 * A constructor which creates an IndexExplorer and ads a TreeListener to
	 * its TreeItem
	 * 
	 * @param listener -
	 *            the listener to add to this IndexExplorers TreeItem
	 */
	public IndexExplorer(TreeListener listener) {
		this.itemMap = new HashMap<String, TreeItem>();

		getIndices();
		tree.addTreeListener(listener);
		tree.addTreeListener(this);

		initWidget(tree);
	}

	/**
	 * A helper method used to create a TreeItem with an image caption and add
	 * it to the IndexExplorer tree, as well as to the itemMap for later
	 * retrieval.
	 * 
	 * @param root -
	 *            the Item to add the new Item under. Null if it is to be added
	 *            directly under the Tree's root.
	 * @param title -
	 *            the text part of caption of the new TreeItem
	 * @param id -
	 *            the ID used to identify the TreeItem in the itemMap
	 * @param image -
	 *            the image part of the caption of the new TreeItem
	 * @return The newly added TreeItem
	 */
	private TreeItem addItem(TreeItem root, String title, String id, AbstractImagePrototype image) {
		TreeItem item = new TreeItem(toHTML(image, title));
		itemMap.put(id, item);
		if (root == null) {
			tree.addItem(item);
		} else {
			root.addItem(item);
		}
		return item;
	}

	/**
	 * A helper method used to create a TreeItem with an image caption and add
	 * it to the IndexExplorer tree.
	 * 
	 * @param root-
	 *            the Item to add the new Item under. Null if it is to be added
	 *            directly under the Tree's root.
	 * @param title -
	 *            the text part of caption of the new TreeItem
	 * @param image -
	 *            the image part of the caption of the new TreeItem
	 * @return The newly added TreeItem
	 */
	private TreeItem addItem(TreeItem root, String title, AbstractImagePrototype image) {
		TreeItem item = new TreeItem(toHTML(image, title));
		if (root == null) {
			tree.addItem(item);
		} else {
			root.addItem(item);
		}
		return item;
	}

	/**
	 * A helper method used to create a CollectionTreeItem with an image caption and
	 * add it to the IndexExplorer tree, as well as to the itemMap for later
	 * retrieval.
	 * 
	 * @param root-
	 *            the Item to add the new CollectionTreeItem under. Null if it is to
	 *            be added directly under the Tree's root.
	 * @param title -
	 *            title used both in the caption and to identify the
	 *            IndexTreeItem in the itemMap
	 * @param indexedImage -
	 *            the image part of the caption of the new CollectionTreeItem to be
	 *            used when there are index items bellow it.
	 * @param unindexedImage -
	 *            the image part of the caption of the new CollectionTreeItem to be
	 *            used when there are no index items bellow it.
	 * @return The newly added CollectionTreeItem
	 */
	private CollectionTreeItem addCollectionTreeItem(TreeItem root, String id, String title,
			AbstractImagePrototype indexedImage, AbstractImagePrototype unindexedImage) {
		CollectionTreeItem item = new CollectionTreeItem(title, id, indexedImage, unindexedImage);
		itemMap.put(id, item);
		if (root == null) {
			tree.addItem(item);
		} else {
			root.addItem(item);
		}
		return item;
	}

	/**
	 * A helper method used to create a IndexTreeItem with an image caption and
	 * add it to the IndexExplorer tree, as well as to the itemMap for later
	 * retrieval.
	 * 
	 * @param root-
	 *            the Item to add the new IndexTreeItem under. Null if it is to
	 *            be added directly under the Tree's root.
	 * @param id -
	 *            the ID used both in the caption and to identify the
	 *            IndexTreeItem in the itemMap
	 * @return The newly added IndexTreeItem
	 */
	private IndexTreeItem addIndexTreeItem(TreeItem root, String id, String relatedCollectionID) {
		IndexTreeItem item = new IndexTreeItem(toHTML(images.indexImage(), id), id, relatedCollectionID);
		itemMap.put(id, item);
		if (root == null) {
			tree.addItem(item);
		} else {
			root.addItem(item);
		}
		return item;
	}


	/**
	 * A helper method used to create an AdditionTreeItem with an image caption
	 * and add it to the IndexExplorer tree.
	 * 
	 * @param root-
	 *            the Item to add the new AdditionTreeItem under. Null if it is
	 *            to be added directly under the Tree's root.
	 * @param type -
	 *            the type of the AdditionTreeItem to be created. The value of
	 *            this argument must be one of the String constants belonging to
	 *            the AdditionTreeItem.
	 * @return The newly added AdditionTreeItem
	 */
	private AdditionTreeItem addAdditionTreeItem(TreeItem root, String type) {
		AbstractImagePrototype image;
		String caption;
		if (AdditionTreeItem.COLLECTION.equalsIgnoreCase(type)) {
			image = images.newCollectionImage();
			caption = "New Fake Collection";
		} else {
			image = images.newIndexImage();
			caption = "New Index Node";
		}
		AdditionTreeItem item = new AdditionTreeItem(toHTML(image, caption),
				type);
		if (root == null) {
			tree.addItem(item);
		} else {
			root.addItem(item);
		}
		return item;
	}

	/**
	 * Creates a HMTL caption containing an Image and a String title
	 * 
	 * @param image -
	 *            the Image to add to the caption
	 * @param title -
	 *            the title to add to the caption
	 * @return - the requested HTML caption
	 */
	private String toHTML(AbstractImagePrototype image, String title) {
		return "<span>" + image.getHTML() + title + "</span>";
	}

	/**
	 * A method used to asynchronously retrieve collection and index information
	 * from the ManagementService, and then fill the tree with this information
	 */
	private void getIndices() {
		/**
		 * The callback which will handle the result of the ManagementService
		 * call in accordance with the GWT RPC framework
		 */
		AsyncCallback<List<CollectionBean>> callback = new AsyncCallback<List<CollectionBean>>() {
			/**
			 * {@inheritDoc}
			 */
			public void onSuccess(List<CollectionBean> result) {
				for (int i = 0; i < result.size(); i++) {
					CollectionBean col = (CollectionBean) result.get(i);
					TreeItem nameItem;
					if (i == result.size() - 1) {
						nameItem = addItem(null, col.getName(), images.testImage());
						fakeCollectionRoot = nameItem;
					} else {
						nameItem = addCollectionTreeItem(null,  col.getId(), col.getName() + " (" + col.getId() + ")",
								images.indexedNameImage(),images.nameImage());
					}
					List<IndexBean> indices = col.getIndices();
					for (int k = 0; k < indices.size(); k++) {
						IndexBean index = (IndexBean) indices.get(k);
						IndexTreeItem item = addIndexTreeItem(nameItem, index.getId(), col.getId());
						//TODO Giota removed it 
						//item.addItem("please wait...");
					}
					//TODO maybe this is not needed and should be removed. The only one new index should remain
					if (i == result.size() - 1) {
						addAdditionTreeItem(nameItem, AdditionTreeItem.INDEX);
					}
				}
				fakeCollectionRoot.setState(true, false);
			}

			/**
			 * {@inheritDoc}
			 */
			public void onFailure(Throwable caught) {
				Window.alert("Error retrieving available collections and indices." + caught);
			}
		};
		FTIndexManagementPortlet.mgmtService.getCollections(callback);
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemAdded(ItemContent content) {
		if (content instanceof IndexItemContent) {
			if (fakeCollectionRoot != null) {
				IndexItemContent newItemContent = (IndexItemContent) content;
				TreeItem collectionItem = (TreeItem) itemMap.get(newItemContent
						.getCollectionID());
				IndexTreeItem indexItem;
				// this is for fake thus for empty manager
				if (collectionItem == null) {
					TreeItem lastItem = fakeCollectionRoot
							.getChild(fakeCollectionRoot.getChildCount() - 1);
					fakeCollectionRoot.removeItem(lastItem);
					//   collectionItem = addCollectionTreeItem(fakeCollectionRoot,
					//   		newItemContent.getCollectionID(),
					//           newItemContent.getCollectionID(),
					//           images.indexedColImage(),images.collectionImage());
					//TODO check this null values
					indexItem = addIndexTreeItem(fakeCollectionRoot, newItemContent.getID(), null);
					fakeCollectionRoot.setState(true, false);
					addAdditionTreeItem(fakeCollectionRoot, AdditionTreeItem.INDEX);
					fakeCollectionRoot.addItem(lastItem);


				} else {
					TreeItem lastItem = collectionItem.getChild(collectionItem
							.getChildCount() - 1);
					collectionItem.removeItem(lastItem);
					indexItem = addIndexTreeItem(collectionItem, newItemContent.getID(), newItemContent.getCollectionID());
					collectionItem.addItem(lastItem);
					collectionItem.setState(true, false);
				}
				addAdditionTreeItem(indexItem, AdditionTreeItem.INDEX);
				tree.setSelectedItem(indexItem);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void itemRemoved(String itemID) {
		try {
			TreeItem item = (TreeItem) itemMap.get(itemID);
			if (item != null) {
				item.remove();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void onTreeItemSelected(TreeItem item) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void onTreeItemStateChanged(TreeItem item) {
		
	}
}
