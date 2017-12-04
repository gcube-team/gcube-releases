/**
 *
 */
package org.gcube.portlets.user.workspace.client.workspace;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * The Class GWTWorkspaceItem.
 *
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public abstract class GWTWorkspaceItem implements IsSerializable {

	protected String id;
	protected GWTProperties properties;
	protected String name;
	protected String owner;
	protected Date creationTime;
	protected String description;
	protected Date lastModificationTime;
	protected GWTWorkspaceItemAction lastAction;
	protected GWTWorkspaceFolder parent;

	protected boolean isLoading = false;

	/**
	 * Instantiates a new GWT workspace item.
	 */
	protected GWTWorkspaceItem()
	{}

	/**
	 * Instantiates a new GWT workspace item.
	 *
	 * @param parent the parent
	 * @param id the id
	 * @param name the name
	 * @param description the description
	 * @param owner the owner
	 * @param creationTime the creation time
	 * @param properties the properties
	 * @param lastModificationTime the last modification time
	 * @param lastAction the last action
	 */
	public GWTWorkspaceItem(GWTWorkspaceFolder parent, String id, String name,
			String description, String owner, Date creationTime,
			GWTProperties properties, Date lastModificationTime, GWTWorkspaceItemAction lastAction) {

		this.parent = parent;
		this.creationTime = creationTime;
		this.id = id;
		this.properties = properties;
		this.name = name;
		this.owner = owner;
		this.description = description;
		this.lastModificationTime = lastModificationTime;
		this.lastAction = lastAction;
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
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId(){
		return id;
	}

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	public GWTWorkspaceFolder getParent()
	{
		return parent;
	}

	/**
	 * Gets the creation time.
	 *
	 * @return the creation time
	 */
	public Date getCreationTime(){
		return creationTime;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	public GWTProperties getProperties(){
		return properties;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName(){
		return name;
	}

	/**
	 * Gets the owner.
	 *
	 * @return the owner
	 */
	public String getOwner(){
		return owner;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the last modification time.
	 *
	 * @return the last modification time
	 */
	public Date getLastModificationTime() {
		return lastModificationTime;
	}

	/**
	 * Gets the last action.
	 *
	 * @return the last action
	 */
	public GWTWorkspaceItemAction getLastAction() {
		return lastAction;
	}

	/**
	 * Gets the item description.
	 *
	 * @return the item description
	 */
	public abstract GWTItemDescription getItemDescription();

	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel()
	{
		return getItemDescription().getLabel();
	}

	/**
	 * Gets the icon class.
	 *
	 * @return the icon class
	 */
	public String getIconClass(){
		return getItemDescription().getIconClass();
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public abstract GWTWorkspaceItemType getType();

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public abstract List<? extends GWTWorkspaceItem> getChildren();

	/**
	 * Sets the parent.
	 *
	 * @param parent the new parent
	 */
	protected void setParent(GWTWorkspaceFolder parent)
	{
		this.parent = parent;
	}

	/**
	 * Count sub tree children.
	 *
	 * @return the int
	 */
	public int countSubTreeChildren()
	{
		int total = 0;

		for (GWTWorkspaceItem child : getChildren()){
			total += 1 + child.countSubTreeChildren();
		}

		return total;

	}

	/**
	 * Count sub tree children.
	 *
	 * @param type the type
	 * @return the int
	 */
	public int countSubTreeChildren(GWTWorkspaceItemType type)
	{
		int total = 0;

		for (GWTWorkspaceItem child : getChildren()){
			total += child.countSubTreeChildren(type);

			if (child.getType() == type) total++;
		}

		return total;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	protected void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	protected void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Checks if is root.
	 *
	 * @return true, if is root
	 */
	public boolean isRoot()
	{
		return getParent() == null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Item: "+this.name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GWTWorkspaceItem)) return false;
		return this.id.equals(((GWTWorkspaceItem)obj).getId());
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath()
	{
		if (isRoot()) return "/"+name;
		return getParent().getPath()+"/"+name;
	}

	/**
	 * Checks if is ancestor.
	 *
	 * @param item the item
	 * @return true, if is ancestor
	 */
	public boolean isAncestor(GWTWorkspaceItem item)
	{
		if (id.equals(item.getId())) return true;

		if (isRoot()) return false;

		return getParent().isAncestor(item);
	}

	/**
	 * Checks if is loading.
	 *
	 * @return the isLoading
	 */
	public boolean isLoading() {
		return isLoading;
	}



}
