package org.gcube.common.storagehub.client.proxies;

import java.io.InputStream;
import java.util.List;

import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.model.acls.ACL;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;



public interface ItemManagerClient {

	//List<? extends Item> listByPath(String path, String ... excludeNodes);
	
	List<? extends Item> getChildren(String id, String ... excludeNodes);
	
	List<? extends Item> getChildren(String id, int start, int limit,  String ... excludeNodes);
	
	List<? extends Item> getAnchestors(String id, String ... excludeNodes);
	
	Integer childrenCount(String id);
	
	Item get(String id, String ... excludeNodes);

	StreamDescriptor download(String id);
	
	<T extends AbstractFileItem> T uploadFile(InputStream stream, String parentId, String fileName, String description);

	FolderItem createFolder(String parentId, String name, String description);

	List<ACL> getACL(String id);
	
}
