package org.gcube.common.storagehub.client.dsl;

import java.io.InputStream;
import java.util.List;

import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.plugins.AbstractPlugin;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.client.proxies.WorkspaceManagerClient;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;

public class WorkspaceContainer {

	private WorkspaceManagerClient wsClient = AbstractPlugin.workspace().build();
	private ItemManagerClient itemclient = AbstractPlugin.item().build();
	
	protected WorkspaceContainer() {}
	
	FolderItem rootFolder = null;
	
	private void init() {
		if (rootFolder == null)
			rootFolder = (FolderItem) wsClient.getWorkspace();
	}
	
	public FolderItem get(String ... excludes) {
		init();
		return rootFolder;
	}

	public List<? extends Item>  list() {
		init();
		return itemclient.getChildren(rootFolder.getId());
	}
	
	public FolderContainer open(String id) {
		FolderContainer fc = new FolderContainer(itemclient);
		fc.setId(id);
		return fc;
	}
	
	public <T extends AbstractFileItem> T uploadFile(InputStream stream, String filename, String description) {
		init();
		return itemclient.uploadFile(stream, this.rootFolder.getId(), filename, description);
	}
	
	public StreamDescriptor download() {
		init();
		return itemclient.download(this.rootFolder.getId());
	}
		
	
	/*public FolderContainer open(Path path) {
		FolderContainer fc = new FolderContainer(itemclient);
		fc.setId(get("hl:accounting","jcr:content").getId());
		return fc;
	}*/
}
