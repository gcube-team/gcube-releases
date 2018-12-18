package org.gcube.common.storagehub.client.dsl;

import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;

public abstract class ItemContainer<I extends Item> {
	
	protected ItemManagerClient itemclient;
	
	protected String itemId = null;
	protected I item;
	
	boolean invalidated = false;
	
	protected ItemContainer(ItemManagerClient itemclient, I item) {
		this.itemclient = itemclient;
		this.itemId = item.getId();
	}
	
	protected ItemContainer(ItemManagerClient itemclient, String itemId) {
		this.itemclient = itemclient;
		this.itemId = itemId;
	}
	
	protected void setItem(I item) {
		this.item = item;
		this.itemId = item.getId();
	}
	
	public String getId() {
		return this.itemId;	
	}
	
	public abstract ContainerType getType();
	
	
	@SuppressWarnings("unchecked")
	public I get() {
		if (item==null || invalidated) {
			I toRet = (I)itemclient.get(itemId);
			invalidated = false;
			return toRet;
		}
		else return item;
	}
	
	protected void invalidateItem() {
		invalidated = true;
	}
		
	
	public StreamDescriptor download(String ... nodeIdsToExclude) {
		return itemclient.download(this.itemId, nodeIdsToExclude);
	}
	
	public ListResolver getAnchestors() {
		return new ListResolver((onlyType, excludes) -> itemclient.getAnchestors(this.itemId,excludes) , itemclient);
	}
	
	public void delete() {
		itemclient.delete(this.itemId);
		invalidateItem();
	}
	
	public void rename(String newName) {
		itemclient.rename(this.getId(), newName);
		invalidateItem();
	}
	
	public void move(FolderContainer folder) {
		itemclient.move(this.getId(), folder.getId());
		invalidateItem();
	}
	
	public void setMetadata(Metadata metadata) {
		itemclient.setMetadata(this.getId(), metadata);
		invalidateItem();
	}
	
	public FolderContainer getRootSharedFolder() {
		return new FolderContainer(itemclient, (FolderItem)itemclient.getRootSharedFolder(this.itemId));
	}
}
