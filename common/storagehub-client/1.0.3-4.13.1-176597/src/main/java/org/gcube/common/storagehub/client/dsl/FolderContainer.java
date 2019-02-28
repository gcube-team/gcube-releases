package org.gcube.common.storagehub.client.dsl;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.model.acls.ACL;
import org.gcube.common.storagehub.model.acls.AccessType;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.GCubeItem;

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
	
	public ListResolverTyped list() {
		return new ListResolverTyped((onlyType, includeHidden, excludes) -> itemclient.getChildren(itemId, onlyType, includeHidden, excludes), itemclient) ;
	}
	
	public FileContainer uploadFile(InputStream stream, String filename, String description) {
		return new FileContainer(itemclient, itemclient.uploadFile(stream, this.itemId , filename, description));
	}
	
	public FolderContainer uploadArchive(InputStream stream, String extractionFolderName) {
		return new FolderContainer(itemclient, itemclient.uploadArchive(stream, this.itemId , extractionFolderName));
	}
	
	public FolderContainer newFolder(String name, String description) throws Exception {
		String newFolderId = itemclient.createFolder(this.itemId, name, description, false);
		return new FolderContainer(itemclient, newFolderId);
	}
	
	public FolderContainer newHiddenFolder(String name, String description) throws Exception {
		String newFolderId = itemclient.createFolder(this.itemId, name, description, true);
		return new FolderContainer(itemclient, newFolderId);
	}
	
	public GenericItemContainer newGcubeItem(GCubeItem item) throws Exception {
		String itemId = itemclient.createGcubeItem(this.itemId, item);
		return new GenericItemContainer(itemclient, itemId);
	}
	
	public List<ACL> getAcls() throws Exception {
		return itemclient.getACL(this.itemId);
	}
	
	public ListResolver findByName(String namePattern) {
		return new ListResolver((onlyType, includeHidden, excludes) -> itemclient.findChildrenByNamePattern(itemId, namePattern , excludes), itemclient);
	}
	
	public FolderContainer share(Set<String> users, AccessType accessType) throws Exception {
		itemclient.shareFolder(this.itemId, users, accessType);
		return this;
	}
	
	public FolderContainer unshare(Set<String> users) throws Exception {
		itemclient.unshareFolder(this.itemId, users);
		return this;
	}
	
}
