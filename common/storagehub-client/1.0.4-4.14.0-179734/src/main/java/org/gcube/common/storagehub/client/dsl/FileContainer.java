package org.gcube.common.storagehub.client.dsl;

import java.net.URL;
import java.util.List;

import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.model.exceptions.StorageHubException;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.service.Version;

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
	
	public URL getPublicLink() throws StorageHubException{
		return itemclient.getPublickLink(this.itemId);
	}
	
	public URL getPublicLink(String version) throws StorageHubException{
		return itemclient.getPublickLink(this.itemId, version);
	}
	
	public List<Version> getVersions() throws StorageHubException{
		return itemclient.getFileVersions(this.itemId);
	}
	
	public StreamDescriptor downloadSpecificVersion(String versionName) throws StorageHubException{
		return itemclient.downloadSpecificVersion(this.itemId, versionName);
	}
	
	public FileContainer copy(FolderContainer folder, String newFileName) throws StorageHubException {
		return new FileContainer(itemclient, itemclient.copy(this.itemId, folder.get().getId(), newFileName));
	}
}
