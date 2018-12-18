package org.gcube.common.storagehub.client.proxies;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.acls.ACL;
import org.gcube.common.storagehub.model.acls.AccessType;
import org.gcube.common.storagehub.model.items.GCubeItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.service.Version;



public interface ItemManagerClient {
	
	List<? extends Item> getChildren(String id, String ... excludeNodes);
	
	List<? extends Item> getChildren(String id, Class<? extends Item> onlyOfType, String ... excludeNodes);
	
	List<? extends Item> getChildren(String id, int start, int limit, Class<? extends Item> onlyOfType,  String ... excludeNodes);
	
	List<? extends Item> getChildren(String id, int start, int limit,  String ... excludeNodes);
	
	List<? extends Item> getAnchestors(String id, String ... excludeNodes);
	
	Integer childrenCount(String id);
	
	Integer childrenCount(String id, Class<? extends Item> onlyOfType);
	
	Item get(String id, String ... excludeNodes);

	StreamDescriptor download(String id, String... excludeNodes);
	
	String uploadFile(InputStream stream, String parentId, String fileName, String description);

	String createFolder(String parentId, String name, String description);

	List<ACL> getACL(String id);

	void delete(String id);

	URL getPublickLink(String id);

	List<? extends Item> findChildrenByNamePattern(String id, String name, String ... excludeNodes);

	Item getRootSharedFolder(String id);

	String shareFolder(String id, Set<String> users, AccessType accessType);
	
	String copy(String id, String destinationFolderId, String newFilename);

	String uploadArchive(InputStream stream, String parentId, String extractionFolderName);

	String unshareFolder(String id, Set<String> users);

	String move(String id, String destinationFolderId);

	String rename(String id, String newName);

	List<Version> getFileVersions(String id);

	StreamDescriptor downloadSpecificVersion(String id, String version);
	
	String setMetadata(String id,Metadata metadata);

	String createGcubeItem(String parentId, GCubeItem item);
	
}
