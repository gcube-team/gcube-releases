package org.gcube.common.storagehub.client.dsl;

import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.model.items.Item;

public class GenericItemContainer extends ItemContainer<Item>  {

	protected GenericItemContainer(ItemManagerClient itemclient, Item item) {
		super(itemclient, item);
	}
	
	protected GenericItemContainer(ItemManagerClient itemclient, String itemId) {
		super(itemclient, itemId);
	}

	public ContainerType getType() {
		return ContainerType.GENERIC_ITEM;
	}
	
}
