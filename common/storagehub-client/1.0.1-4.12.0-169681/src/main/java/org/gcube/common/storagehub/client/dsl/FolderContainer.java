package org.gcube.common.storagehub.client.dsl;

import java.io.InputStream;
import java.util.List;

import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.model.acls.ACL;
import org.gcube.common.storagehub.model.items.FolderItem;

public class FolderContainer extends ItemContainer<FolderItem>{
			
//	private String folderPath = null;
	
	protected FolderContainer(ItemManagerClient itemclient, FolderItem item) {
		super(itemclient, item);		
	}
	
	protected FolderContainer(ItemManagerClient itemclient, String folderId) {
		super(itemclient, folderId);		
	}
	
	public ContainerType getType() {
		return ContainerType.FOLDER;
	}
	
	public ListResolver list() {
		return new ListResolver(excludes -> itemclient.getChildren(itemId, excludes), itemclient);
	}
	
	public FileContainer uploadFile(InputStream stream, String filename, String description) {
		return new FileContainer(itemclient, itemclient.uploadFile(stream, this.itemId , filename, description));
	}
	
	public FolderContainer newFolder(String name, String description) throws Exception {
		String newFolderId = itemclient.createFolder(this.itemId, name, description);
		return new FolderContainer(itemclient, newFolderId);
	}
	
	public List<ACL> getAcls() throws Exception {
		return itemclient.getACL(this.itemId);
	}
	
	public ListResolver findByName(String namePattern) {
		return new ListResolver(excludes -> itemclient.findChildrenByNamePattern(itemId, namePattern , excludes), itemclient);
	}
	
	
}
