package org.gcube.common.storagehub.client.dsl;

import java.io.InputStream;
import java.util.List;

import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;

public class FolderContainer extends ItemContainer{
	
	private FolderItem item = null;
	
//	private String folderPath = null;
	
	protected FolderContainer(ItemManagerClient itemclient) {
		super(itemclient);		
	}
	
	protected void setItem(FolderItem item) {
		this.item = item;
		this.folderId = item.getId();
	}
	
	protected void setId(String folderId) {
		this.folderId = folderId;
//		this.folderPath = null;
	}
/*	
	protected void setPath(String folderPath) {
		this.folderPath = folderPath;
		this.folderId = null;
	}*/
	
	public FolderItem get() throws Exception {
		if (item==null) return (FolderItem)itemclient.get(folderId);
		else return item;
	}
	
	public List<? extends Item>  list() {
		return itemclient.getChildren(folderId);
	}
	
	public <T extends AbstractFileItem> T uploadFile(InputStream stream, String filename, String description) {
		
		return itemclient.uploadFile(stream, this.folderId , filename, description);
	}
	
	public StreamDescriptor download(InputStream stream) {
		return itemclient.download(this.folderId);
	}
}
