/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.shared;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * The Class Item.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 2, 2015
 */
public class Item implements IsSerializable, ItemInterface{

	protected Item parent;
	protected String id;

	protected String name;
	protected ItemType type;
	protected String path;
	protected ArrayList<Item> children;
	private String owner;
	private boolean isFolder;
	private boolean isSpecialFolder = false;
	private boolean isSharedFolder = false;
	private boolean isRoot = false;
	private Date creationDate;

	/**
	 * Instantiates a new item.
	 */
	public Item() {
	}
	/**
	 * Instantiates a new item.
	 *
	 * @param id the id
	 * @param name the name
	 * @param isFolder the is folder
	 */
	public Item(String id, String name, boolean isFolder) {
		this.id = id;
		this.isFolder = isFolder;
		this.name = name;
	}


	/**
	 * Instantiates a new item.
	 *
	 * @param parent the parent
	 * @param id the id
	 * @param name the name
	 * @param type the type
	 * @param path the path
	 * @param owner the owner
	 * @param creationDate the creation date
	 * @param isFolder the is folder
	 * @param isRoot the is root
	 */
	public Item(Item parent, String id, String name, ItemType type, String path, String owner, Date creationDate, boolean isFolder, boolean isRoot) {
		this(parent, id, name, type, path, owner, isFolder, isRoot);
		this.creationDate = creationDate;
	}


	/**
	 * Instantiates a new item.
	 *
	 * @param parent the parent
	 * @param id the id
	 * @param name the name
	 * @param type the type
	 * @param path the path
	 * @param owner the owner
	 * @param isFolder the is folder
	 * @param isRoot the is root
	 */
	public Item(Item parent, String id, String name, ItemType type, String path, String owner, boolean isFolder, boolean isRoot) {
		this(id, name, isFolder);
		this.parent = parent;
		this.type = type;
		this.path = path;
		this.children = new ArrayList<Item>();
		this.owner = owner;
		this.isRoot = isRoot;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Checks if is root.
	 *
	 * @return true, if is root
	 */
	public boolean isRoot() {
		return isRoot;
	}

	/**
	 * Sets the is root.
	 *
	 * @param isRoot the is root
	 * @return true, if successful
	 */
	public void setIsRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}

	/**
	 * Checks if is folder.
	 *
	 * @return the isFolder
	 */
	public boolean isFolder() {
		return isFolder;
	}

	/**
	 * Sets the folder.
	 *
	 * @param isFolder
	 *            the isFolder to set
	 */
	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}

	/**
	 * Checks if is shared folder.
	 *
	 * @return the isSharedFolder
	 */
	public boolean isSharedFolder() {
		return isSharedFolder;
	}

	/**
	 * Sets the shared folder.
	 *
	 * @param isSharedFolder
	 *            the isSharedFolder to set
	 */
	public void setSharedFolder(boolean isSharedFolder) {
		this.isSharedFolder = isSharedFolder;
	}

	/**
	 * The item parent. Null if is the root.
	 *
	 * @return the item parent.
	 */
	public Item getParent() {
		return parent;
	}

	/**
	 * The item id.
	 *
	 * @return the id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * The item name.
	 *
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * The item type.
	 *
	 * @return the type.
	 */
	public ItemType getType() {
		return type;
	}

	/**
	 * The item children.
	 *
	 * @return the children.
	 */
	public ArrayList<Item> getChildren() {
		return children;
	}

	/**
	 * Gets the owner.
	 *
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * Sets the name.
	 *
	 * @param name            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the owner.
	 *
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * The item child.
	 *
	 * @param index
	 *            the index
	 * @return the child at the specific position.
	 */
	public Item getChild(int index) {
		return children.get(index);
	}

	/**
	 * Add a child to this item.
	 *
	 * @param child
	 *            the child to add.
	 */
	public void addChild(Item child) {
		children.add(child);
	}

	/**
	 * Remove a child from this item.
	 *
	 * @param child
	 *            the child
	 */
	public void removeChild(Item child) {
		children.remove(child);
	}

	/**
	 * Checks if is special folder.
	 *
	 * @return the isSpecialFolder
	 */
	public boolean isSpecialFolder() {
		return isSpecialFolder;
	}

	/**
	 * Sets the special folder.
	 *
	 * @param isSpecialFolder            the isSpecialFolder to set
	 */
	public void setSpecialFolder(boolean isSpecialFolder) {
		this.isSpecialFolder = isSpecialFolder;
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (id == null ? 0 : id.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Item other = (Item) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}



	/**
	 * Gets the creation date.
	 *
	 * @return the creationDate
	 */
	public Date getCreationDate() {

		return creationDate;
	}

	/**
	 * Sets the parent.
	 *
	 * @param parent
	 *            the new parent
	 */
	public void setParent(Item parent) {
		this.parent = parent;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("Item [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", type=");
		builder.append(type);
		builder.append(", path=");
		builder.append(path);
		builder.append(", children=");
		builder.append(children);
		builder.append(", owner=");
		builder.append(owner);
		builder.append(", isFolder=");
		builder.append(isFolder);
		builder.append(", isSpecialFolder=");
		builder.append(isSpecialFolder);
		builder.append(", isSharedFolder=");
		builder.append(isSharedFolder);
		builder.append(", isRoot=");
		builder.append(isRoot);
		builder.append(", creationDate=");
		builder.append(creationDate);
		builder.append("]");
		return builder.toString();
	}

}
