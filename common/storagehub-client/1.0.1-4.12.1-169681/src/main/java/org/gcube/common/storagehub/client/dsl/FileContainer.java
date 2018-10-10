package org.gcube.common.storagehub.client.dsl;

import java.net.URL;

import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.model.items.AbstractFileItem;

public class FileContainer extends ItemContainer<AbstractFileItem> {

	protected FileContainer(ItemManagerClient itemclient, AbstractFileItem item) {
		super(itemclient, item);
	}

	protected FileContainer(ItemManagerClient itemclient, String fileId) {
		super(itemclient, fileId);		
	}
	
	public ContainerType getType() {
		return ContainerType.FILE;
	}
	
	public URL getPublicLink() {
		return itemclient.getPublickLink(this.itemId);
	}
	
}
