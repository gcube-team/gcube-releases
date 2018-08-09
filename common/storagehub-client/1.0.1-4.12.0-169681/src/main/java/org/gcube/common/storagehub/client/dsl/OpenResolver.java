package org.gcube.common.storagehub.client.dsl;

import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;

public class OpenResolver {

	private Item item;
	private ItemManagerClient itemclient;
	
	protected OpenResolver(Item item, ItemManagerClient itemclient) {
		this.item = item;
		this.itemclient = itemclient;
	};
	
	public FolderContainer asFolder() {
		if (item instanceof FolderItem)
			return new FolderContainer(itemclient, (FolderItem)item);
		else throw new RuntimeException("this item is not a folder");
	}
	
	public ItemContainer<Item> asItem() {
		return new GenericItemContainer(itemclient, item);
	}
	
	public FileContainer asFile() {
		if (item instanceof AbstractFileItem)
			return new FileContainer(itemclient, (AbstractFileItem)item);
		else throw new RuntimeException("this item is not a File");
	}
}
