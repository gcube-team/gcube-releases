package org.gcube.common.storagehub.client.dsl;

import java.io.InputStream;

import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;

public class ItemContainer {

	protected ItemManagerClient itemclient;
	
	protected String folderId = null;
//	private String folderPath = null;
	
	protected ItemContainer(ItemManagerClient itemclient) {
		this.itemclient = itemclient;
	}
	
	public StreamDescriptor download(InputStream stream) {
		return itemclient.download(this.folderId);
	}
		
		
}
