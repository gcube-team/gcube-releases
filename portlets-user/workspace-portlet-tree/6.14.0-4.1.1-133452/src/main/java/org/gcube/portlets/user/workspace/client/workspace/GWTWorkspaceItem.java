/**
 * 
 */
package org.gcube.portlets.user.workspace.client.workspace;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
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
	
	protected GWTWorkspaceItem()
	{}

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

	public String getId(){
		return id;
	}
	
	public GWTWorkspaceFolder getParent()
	{
		return parent;
	}

	public Date getCreationTime(){
		return creationTime;
	}
	
	public GWTProperties getProperties(){
		return properties;
	}
	
	public String getName(){
		return name;
	}

	public String getOwner(){
		return owner;
	}
	
	public String getDescription() {
		return description;
	}

	public Date getLastModificationTime() {
		return lastModificationTime;
	}

	public GWTWorkspaceItemAction getLastAction() {
		return lastAction;
	}
	
	public abstract GWTItemDescription getItemDescription();

	public String getLabel()
	{
		return getItemDescription().getLabel();
	}
	
	public String getIconClass(){	
		return getItemDescription().getIconClass();
	}
	
	public abstract GWTWorkspaceItemType getType();
	
	public abstract List<? extends GWTWorkspaceItem> getChildren();
	
	protected void setParent(GWTWorkspaceFolder parent)
	{
		this.parent = parent;
	}
	
	public int countSubTreeChildren()
	{
		int total = 0;
		
		for (GWTWorkspaceItem child : getChildren()){
			total += 1 + child.countSubTreeChildren();
		}
		
		return total;
		
	}
	public int countSubTreeChildren(GWTWorkspaceItemType type)
	{
		int total = 0;
		
		for (GWTWorkspaceItem child : getChildren()){
			total += child.countSubTreeChildren(type);
			
			if (child.getType() == type) total++;
		}
		
		return total;
	}
	
	protected void setName(String name)
	{
		this.name = name;
	}

	protected void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isRoot()
	{
		return getParent() == null;
	}

	@Override
	public String toString() {
		return "Item: "+this.name;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GWTWorkspaceItem)) return false;
		return this.id.equals(((GWTWorkspaceItem)obj).getId());
	}
	
	public String getPath()
	{
		if (isRoot()) return "/"+name;
		return getParent().getPath()+"/"+name;
	}
	
	public boolean isAncestor(GWTWorkspaceItem item)
	{
		if (id.equals(item.getId())) return true;
		
		if (isRoot()) return false;
		
		return getParent().isAncestor(item);
	}

	/**
	 * @return the isLoading
	 */
	public boolean isLoading() {
		return isLoading;
	}

}
