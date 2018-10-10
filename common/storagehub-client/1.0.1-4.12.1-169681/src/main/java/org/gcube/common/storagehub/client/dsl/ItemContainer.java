package org.gcube.common.storagehub.client.dsl;

import java.util.List;

import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.model.NodeConstants;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;

public abstract class ItemContainer<I extends Item> {
	
	protected ItemManagerClient itemclient;
	
	protected String itemId = null;
	protected I item;
	
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
	
	public abstract ContainerType getType();
		
	
	
	@SuppressWarnings("unchecked")
	public I get() throws Exception {
		if (item==null) return (I)itemclient.get(itemId);
		else return item;
	}
	
	public StreamDescriptor download() {
		return itemclient.download(this.itemId);
	}
	
	public List<? extends Item> getAnchestors() {
		return itemclient.getAnchestors(this.itemId, NodeConstants.ACCOUNTING_NAME);
	}
	
	public void delete() {
		itemclient.delete(this.itemId);
	}
	
	public FolderContainer getRootSharedFolder() {
		return new FolderContainer(itemclient, (FolderItem)itemclient.getRootSharedFolder(this.itemId));
	}
}
