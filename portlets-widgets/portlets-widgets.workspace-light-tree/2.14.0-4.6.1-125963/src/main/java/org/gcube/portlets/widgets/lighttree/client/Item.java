/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 * Modified by Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 */
public class Item implements IsSerializable{
	
	protected Item parent;
	protected String id;
	protected String name;
	protected ItemType type;
	
	protected String path;
	
	protected boolean shared;
	
	protected ArrayList<Item> children;
	
	public Item(){}
	
	/**
	 * Create a new item.
	 * @param parent the item parent.
	 * @param id the item id.
	 * @param name the item name.
	 * @param type the item type.
	 * @param path the item path.
	 */
	public Item(Item parent, String id, String name, ItemType type, String path) {
		this.parent = parent;
		this.id = id;
		this.name = name;
		this.type = type;
		this.path = path;
		this.children = new ArrayList<Item>();
	}


	/**
	 * @return the shared
	 */
	public boolean isShared() {
		return shared;
	}

	/**
	 * @param shared the shared to set
	 */
	public void setShared(boolean shared) {
		this.shared = shared;
	}

	/**
	 * The item parent. Null if is the root.
	 * @return the item parent.
	 */
	public Item getParent() {
		return parent;
	}

	/**
	 * The item id.
	 * @return the id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * The item name.
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * The item type.
	 * @return the type.
	 */
	public ItemType getType() {
		return type;
	}


	/**
	 * The item children.
	 * @return the children.
	 */
	public ArrayList<Item> getChildren() {
		return children;
	}
	

	/**
	 * The item child.
	 * @return the child at the specific position.
	 */
	public Item getChild(int index) {
		return children.get(index);
	}
	
	/**
	 * Add a child to this item.
	 * @param child the child to add.
	 */
	public void addChild(Item child)
	{
		children.add(child);
	}
	
	/**
	 * Remove a child from this item.
	 * @param child
	 */
	public void removeChild(Item child)
	{
		children.remove(child);
	}
	
	
	
	/**
	 * Return this item path.
	 * @return the item path.
	 */
//	Modified in date 26/06/2012 by Francesco Mangiacrapa
	public String getPath()
	{
		System.out.println("getting path for "+name);
		if (parent == null) return "/";

		return parent.getPath() + name + "/";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Item [name=");
		builder.append(name);
		builder.append(", type=");
		builder.append(type);
		builder.append(", id=");
		builder.append(id);
		builder.append("]");
		return builder.toString();
	}

	public void setParent(Item parent) {
		this.parent = parent;
	}
}
