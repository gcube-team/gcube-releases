package org.gcube.common.storagehub.client.dsl;

import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;

public class Util {
 
	protected static ItemContainer<? extends Item> getContainerFromItem(Item item,ItemManagerClient itemClient){
		if (item instanceof AbstractFileItem) 
			return new FileContainer(itemClient, (AbstractFileItem)item);
		else if (item instanceof FolderItem)
			return new FolderContainer(itemClient, (FolderItem)item);
		else return new GenericItemContainer(itemClient, item);
	}
}
