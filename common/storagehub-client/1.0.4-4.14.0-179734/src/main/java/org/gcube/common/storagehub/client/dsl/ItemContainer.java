package org.gcube.common.storagehub.client.dsl;

import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.exceptions.StorageHubException;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;

public abstract class ItemContainer<I extends Item> {

	protected ItemManagerClient itemclient;

	protected String itemId = null;
	protected I item;

	boolean invalidated = false;

	protected ItemContainer(ItemManagerClient itemclient, I item) {
		this.itemclient = itemclient;
		this.item = item;
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
			try {
				I toRet = (I)itemclient.get(itemId);
				invalidated = false;
				item = toRet;
				return toRet;
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		else return item;
	}

	protected void invalidateItem() {
		invalidated = true;
	}


	public StreamDescriptor download(String ... nodeIdsToExclude) throws StorageHubException {
		return itemclient.download(this.itemId, nodeIdsToExclude);
	}

	public ListResolver getAnchestors() throws StorageHubException{
		return new ListResolver((onlyType, includeHidden, excludes) -> itemclient.getAnchestors(this.itemId, excludes) , itemclient);
	}

	public void delete() throws StorageHubException {
		itemclient.delete(this.itemId, false);
		invalidateItem();
	}

	public void forceDelete() throws StorageHubException {
		itemclient.delete(this.itemId, true);
		invalidateItem();
	}
	
	public void rename(String newName) throws StorageHubException {
		itemclient.rename(this.getId(), newName);
		invalidateItem();
	}

	public void move(FolderContainer folder) throws StorageHubException {
		itemclient.move(this.getId(), folder.getId());
		invalidateItem();
	}

	public void setMetadata(Metadata metadata) throws StorageHubException {
		itemclient.setMetadata(this.getId(), metadata);
		invalidateItem();
	}

	public FolderContainer getRootSharedFolder() throws StorageHubException {
		return new FolderContainer(itemclient, (FolderItem)itemclient.getRootSharedFolder(this.itemId));
	}
}
