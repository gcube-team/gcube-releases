package org.gcube.common.storagehub.client.dsl;

import org.gcube.common.storagehub.client.plugins.AbstractPlugin;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.client.proxies.WorkspaceManagerClient;
import org.gcube.common.storagehub.model.Path;
import org.gcube.common.storagehub.model.items.Item;

public class StorageHubClient {

	private WorkspaceManagerClient wsClient = AbstractPlugin.workspace().build();
	private ItemManagerClient itemclient = AbstractPlugin.item().build();
		
	public FolderContainer getWSRoot(){
		return new FolderContainer(itemclient, wsClient.getWorkspace());
	}
	
	public OpenResolver open(Path relativePath) {
		Item item = wsClient.retieveItemByPath(relativePath.toPath());
		return new OpenResolver(item, itemclient);		
	}
	
	public OpenResolver open(String id) {
		Item item = itemclient.get(id);
		return new OpenResolver(item, itemclient);	
	}

	
	
}
